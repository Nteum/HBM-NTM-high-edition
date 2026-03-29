#!/usr/bin/env python3
from __future__ import annotations

import argparse
import csv
import json
import math
import os
import subprocess
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, Iterable, List, Tuple

from PIL import Image, ImageDraw, ImageFont


@dataclass(frozen=True)
class LanguageStats:
    language: str
    files: int
    blank: int
    comment: int
    code: int


def _run(cmd: List[str], cwd: Path) -> str:
    proc = subprocess.run(
        cmd,
        cwd=str(cwd),
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        check=False,
    )
    if proc.returncode != 0:
        msg = proc.stderr.strip() or proc.stdout.strip() or "unknown error"
        raise RuntimeError(f"Command failed ({proc.returncode}): {' '.join(cmd)}\n{msg}")
    return proc.stdout


def _repo_root(cwd: Path) -> Path:
    out = _run(["git", "rev-parse", "--show-toplevel"], cwd=cwd).strip()
    return Path(out)


def _tracked_existing_files(repo_root: Path) -> List[Path]:
    raw = _run(["git", "ls-files", "-z"], cwd=repo_root)
    rel_paths = [p for p in raw.split("\0") if p]
    files: List[Path] = []
    for rel in rel_paths:
        p = repo_root / rel
        if p.is_file():
            files.append(p)
    return files


def _run_cloc_json(repo_root: Path, file_list: List[Path]) -> Dict:
    # cloc expects one path per line; allow spaces.
    tmp_dir = repo_root / "build" / "reports" / "language"
    tmp_dir.mkdir(parents=True, exist_ok=True)
    list_path = tmp_dir / "cloc_file_list.txt"
    with list_path.open("w", encoding="utf-8", newline="\n") as f:
        for p in file_list:
            f.write(str(p) + "\n")

    out = _run(
        [
            "cloc",
            "--json",
            "--timeout",
            "0",
            "--list-file",
            str(list_path),
        ],
        cwd=repo_root,
    )
    return json.loads(out)


def _extract_language_stats(cloc_json: Dict) -> Tuple[List[LanguageStats], LanguageStats]:
    stats: List[LanguageStats] = []
    total = cloc_json.get("SUM")
    if not isinstance(total, dict):
        raise RuntimeError("Unexpected cloc JSON: missing SUM")

    for k, v in cloc_json.items():
        if k in {"header", "SUM"}:
            continue
        if not isinstance(v, dict):
            continue
        # Skip binary buckets if present.
        if k.lower() == "binary":
            continue
        stats.append(
            LanguageStats(
                language=str(k),
                files=int(v.get("nFiles", 0)),
                blank=int(v.get("blank", 0)),
                comment=int(v.get("comment", 0)),
                code=int(v.get("code", 0)),
            )
        )

    stats.sort(key=lambda s: s.code, reverse=True)
    total_stats = LanguageStats(
        language="SUM",
        files=int(total.get("nFiles", 0)),
        blank=int(total.get("blank", 0)),
        comment=int(total.get("comment", 0)),
        code=int(total.get("code", 0)),
    )
    return stats, total_stats


def _group_for_pie(
    stats: List[LanguageStats],
    *,
    max_slices: int,
    min_percent: float,
) -> List[LanguageStats]:
    nonzero = [s for s in stats if s.code > 0]
    total_code = sum(s.code for s in nonzero)
    if total_code <= 0:
        return []

    kept: List[LanguageStats] = []
    other_code = other_files = other_blank = other_comment = 0

    for s in nonzero:
        pct = (s.code / total_code) * 100.0
        if pct >= min_percent:
            kept.append(s)
        else:
            other_code += s.code
            other_files += s.files
            other_blank += s.blank
            other_comment += s.comment

    kept.sort(key=lambda s: s.code, reverse=True)
    if len(kept) > max_slices:
        tail = kept[max_slices - 1 :]
        kept = kept[: max_slices - 1]
        for s in tail:
            other_code += s.code
            other_files += s.files
            other_blank += s.blank
            other_comment += s.comment

    if other_code > 0:
        kept.append(
            LanguageStats(
                language="Other",
                files=other_files,
                blank=other_blank,
                comment=other_comment,
                code=other_code,
            )
        )
    return kept


def _format_int(n: int) -> str:
    return f"{n:,}"


def _palette() -> List[Tuple[int, int, int]]:
    return [
        (74, 144, 226),
        (80, 227, 194),
        (245, 166, 35),
        (184, 233, 134),
        (189, 16, 224),
        (144, 19, 254),
        (208, 2, 27),
        (139, 87, 42),
        (126, 211, 33),
        (248, 231, 28),
        (155, 155, 155),
        (0, 0, 0),
    ]


def _draw_pie_chart(
    out_path: Path,
    pie_stats: List[LanguageStats],
    *,
    title: str,
    subtitle: str,
) -> None:
    width, height = 1400, 900
    background = (255, 255, 255)
    img = Image.new("RGB", (width, height), background)
    draw = ImageDraw.Draw(img)
    font = ImageFont.load_default()

    margin = 40
    legend_w = 520
    pie_area_w = width - legend_w - 2 * margin

    # Title
    draw.text((margin, margin), title, fill=(0, 0, 0), font=font)
    draw.text((margin, margin + 20), subtitle, fill=(60, 60, 60), font=font)

    pie_center_x = margin + pie_area_w // 2
    pie_center_y = height // 2 + 20
    radius = min(pie_area_w, height - 2 * margin) // 2 - 40
    bbox = [
        pie_center_x - radius,
        pie_center_y - radius,
        pie_center_x + radius,
        pie_center_y + radius,
    ]

    total_code = sum(s.code for s in pie_stats)
    if total_code <= 0:
        raise RuntimeError("No code lines to chart")

    colors = _palette()
    start_angle = -90.0
    legend_x = margin + pie_area_w + 30
    legend_y = margin + 80
    legend_line_h = 22

    for i, s in enumerate(pie_stats):
        frac = s.code / total_code
        sweep = frac * 360.0
        end_angle = start_angle + sweep
        color = colors[i % len(colors)]
        draw.pieslice(bbox, start=start_angle, end=end_angle, fill=color, outline=(255, 255, 255))
        start_angle = end_angle

        pct = frac * 100.0
        label = f"{s.language:>12}  {pct:5.1f}%  ({_format_int(s.code)} code lines)"
        box = [legend_x, legend_y + i * legend_line_h + 4, legend_x + 14, legend_y + i * legend_line_h + 18]
        draw.rectangle(box, fill=color, outline=(200, 200, 200))
        draw.text((legend_x + 22, legend_y + i * legend_line_h + 4), label, fill=(0, 0, 0), font=font)

    # Simple border around pie area for clarity
    draw.ellipse(bbox, outline=(220, 220, 220), width=2)

    out_path.parent.mkdir(parents=True, exist_ok=True)
    img.save(out_path)


def _write_csv(out_path: Path, stats: List[LanguageStats], total: LanguageStats) -> None:
    out_path.parent.mkdir(parents=True, exist_ok=True)
    with out_path.open("w", encoding="utf-8", newline="") as f:
        w = csv.writer(f)
        w.writerow(["language", "files", "blank", "comment", "code"])
        for s in stats:
            w.writerow([s.language, s.files, s.blank, s.comment, s.code])
        w.writerow([total.language, total.files, total.blank, total.comment, total.code])


def main(argv: List[str] | None = None) -> int:
    p = argparse.ArgumentParser(description="Generate language breakdown (cloc) and pie chart PNG.")
    p.add_argument("--out-dir", default="reports/language", help="Output directory (relative to repo root).")
    p.add_argument("--max-slices", type=int, default=12, help="Max pie slices (including Other).")
    p.add_argument("--min-percent", type=float, default=1.0, help="Group languages below this percent into Other.")
    args = p.parse_args(argv)

    repo_root = _repo_root(Path.cwd())
    out_dir = repo_root / args.out_dir
    out_dir.mkdir(parents=True, exist_ok=True)

    files = _tracked_existing_files(repo_root)
    cloc_json = _run_cloc_json(repo_root, files)

    (stats, total) = _extract_language_stats(cloc_json)
    _write_csv(out_dir / "language_breakdown.csv", stats, total)
    with (out_dir / "cloc.json").open("w", encoding="utf-8") as f:
        json.dump(cloc_json, f, ensure_ascii=False, indent=2)

    pie_stats = _group_for_pie(stats, max_slices=args.max_slices, min_percent=args.min_percent)
    subtitle = f"Tracked files: {_format_int(len(files))}  |  Total code lines: {_format_int(total.code)}"
    _draw_pie_chart(
        out_dir / "language_pie.png",
        pie_stats,
        title="Language Breakdown (by code lines)",
        subtitle=subtitle,
    )

    print(f"Wrote: {out_dir / 'language_breakdown.csv'}")
    print(f"Wrote: {out_dir / 'cloc.json'}")
    print(f"Wrote: {out_dir / 'language_pie.png'}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
