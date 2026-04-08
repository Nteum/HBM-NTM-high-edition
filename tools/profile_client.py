#!/usr/bin/env python3
"""Profile the local Forge/Minecraft client with async-profiler and open the HTML report.

Examples:
  python3 tools/profile_client.py
  python3 tools/profile_client.py --duration 20 --event cpu
  python3 tools/profile_client.py --match forgeclientuserdev
  python3 tools/profile_client.py --pid 12345
  python3 tools/profile_client.py --launch-client
"""

from __future__ import annotations

import argparse
import os
import shutil
import subprocess
import sys
import time
from datetime import datetime
from pathlib import Path
from typing import Iterable


ROOT = Path(__file__).resolve().parents[1]
DEFAULT_OUTPUT_DIR = ROOT / "run" / "profiles"
DEFAULT_MATCH = ("forgeclientuserdev", "net.minecraft.client.main.Main", "Minecraft")
ASYNC_PROFILER_CANDIDATES = (
    os.environ.get("ASYNC_PROFILER"),
    shutil.which("asprof"),
    "/opt/homebrew/bin/asprof",
    "/usr/local/bin/asprof",
)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Attach async-profiler to a running Minecraft client and open the HTML report."
    )
    parser.add_argument("--pid", type=int, help="Profile an explicit JVM PID instead of auto-detecting.")
    parser.add_argument(
        "--match",
        default="|".join(DEFAULT_MATCH),
        help="Pipe-separated substrings used to find the client process from `jcmd -l`.",
    )
    parser.add_argument("--duration", type=int, default=15, help="Profile duration in seconds.")
    parser.add_argument(
        "--event",
        default="cpu",
        choices=("cpu", "wall", "alloc", "lock", "itimer"),
        help="async-profiler event to record.",
    )
    parser.add_argument(
        "--interval",
        type=str,
        default=None,
        help="Optional async-profiler sampling interval, e.g. `1000000`.",
    )
    parser.add_argument(
        "--output",
        type=Path,
        default=None,
        help="Output HTML path. Defaults to run/profiles/<timestamp>-<event>.html",
    )
    parser.add_argument(
        "--title",
        default="HBM Client Profile",
        help="Flamegraph title written into the HTML report.",
    )
    parser.add_argument(
        "--launch-client",
        action="store_true",
        help="Launch `./gradlew --no-daemon runClient --offline` before attaching.",
    )
    parser.add_argument(
        "--launch-timeout",
        type=int,
        default=120,
        help="Seconds to wait for the launched client PID to appear.",
    )
    parser.add_argument(
        "--no-open",
        action="store_true",
        help="Do not auto-open the resulting HTML report.",
    )
    return parser.parse_args()


def resolve_asprof() -> Path:
    for candidate in ASYNC_PROFILER_CANDIDATES:
        if not candidate:
            continue
        path = Path(candidate)
        if path.exists():
            return path
    raise SystemExit(
        "async-profiler not found. Install it first, e.g. `brew install async-profiler`, "
        "or set ASYNC_PROFILER=/absolute/path/to/asprof."
    )


def run(cmd: list[str], *, cwd: Path | None = None, check: bool = True) -> subprocess.CompletedProcess[str]:
    return subprocess.run(
        cmd,
        cwd=str(cwd) if cwd else None,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        check=check,
    )


def iter_jcmd_processes() -> Iterable[tuple[int, str]]:
    result = run(["jcmd", "-l"])
    for line in result.stdout.splitlines():
        line = line.strip()
        if not line:
            continue
        parts = line.split(maxsplit=1)
        if not parts:
            continue
        try:
            pid = int(parts[0])
        except ValueError:
            continue
        command = parts[1] if len(parts) > 1 else ""
        yield pid, command


def find_pid(match_expression: str) -> int:
    needles = [part.strip() for part in match_expression.split("|") if part.strip()]
    matches: list[tuple[int, str]] = []
    for pid, command in iter_jcmd_processes():
        if any(needle in command for needle in needles):
            matches.append((pid, command))
    if not matches:
        raise SystemExit(
            f"No JVM process matched `{match_expression}`. Start the client first or pass `--pid`."
        )
    if len(matches) > 1:
        formatted = "\n".join(f"  {pid}: {command}" for pid, command in matches)
        raise SystemExit(
            "Multiple JVM processes matched. Use `--pid` or narrow `--match`.\n" + formatted
        )
    return matches[0][0]


def launch_client_and_wait(match_expression: str, timeout_seconds: int) -> subprocess.Popen[str]:
    process = subprocess.Popen(
        ["./gradlew", "--no-daemon", "runClient", "--offline"],
        cwd=ROOT,
        text=True,
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL,
    )
    deadline = time.time() + timeout_seconds
    while time.time() < deadline:
        try:
            pid = find_pid(match_expression)
            print(f"Detected client PID {pid}")
            return process
        except SystemExit:
            time.sleep(2)
    process.terminate()
    raise SystemExit(f"Timed out waiting {timeout_seconds}s for a client process to appear.")


def default_output_path(event: str) -> Path:
    timestamp = datetime.now().strftime("%Y%m%d-%H%M%S")
    return DEFAULT_OUTPUT_DIR / f"profile-{timestamp}-{event}.html"


def open_report(path: Path) -> None:
    if sys.platform == "darwin":
        subprocess.Popen(["open", str(path)])
        return
    opener = shutil.which("xdg-open")
    if opener:
        subprocess.Popen([opener, str(path)])
        return
    print(f"Report saved to {path}")


def main() -> int:
    args = parse_args()
    asprof = resolve_asprof()

    if args.launch_client:
        print("Launching client...")
        launch_client_and_wait(args.match, args.launch_timeout)

    pid = args.pid if args.pid else find_pid(args.match)
    output = args.output if args.output else default_output_path(args.event)
    output = output.expanduser().resolve()
    output.parent.mkdir(parents=True, exist_ok=True)

    command = [
        str(asprof),
        "-d",
        str(args.duration),
        "-e",
        args.event,
        "-f",
        str(output),
        "--title",
        args.title,
        str(pid),
    ]
    if args.interval:
        command[1:1] = ["-i", args.interval]

    print(f"Profiling PID {pid} for {args.duration}s with event={args.event}")
    print("Command:", " ".join(command))

    result = run(command, check=False)
    if result.returncode != 0:
        sys.stderr.write(result.stdout)
        sys.stderr.write(result.stderr)
        raise SystemExit(result.returncode)

    if not output.exists():
        raise SystemExit(f"Profiler exited successfully, but report was not created: {output}")

    print(f"HTML report written to {output}")
    if not args.no_open:
        open_report(output)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
