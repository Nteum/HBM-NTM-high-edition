#!/usr/bin/env python3
from __future__ import annotations

import re
from dataclasses import dataclass
from pathlib import Path


CHECKBOX_RE = re.compile(r"^\s*-\s*\[(?P<mark>[ xX])\]\s+")
H2_RE = re.compile(r"^##\s+(?P<title>.+?)\s*$")
PROGRESS_RE = re.compile(r"^\s*-\s*进度：")

OVERVIEW_START = "<!-- overview:start -->"
OVERVIEW_END = "<!-- overview:end -->"

BAR_LEN = 20
BAR_FILL = "█"
BAR_EMPTY = "░"


@dataclass(frozen=True)
class Section:
    title: str
    start: int
    end: int
    done: int
    total: int


def count_checkboxes(lines: list[str]) -> tuple[int, int]:
    done = 0
    total = 0
    for line in lines:
        match = CHECKBOX_RE.match(line)
        if not match:
            continue
        total += 1
        if match.group("mark").lower() == "x":
            done += 1
    return done, total


def render_progress(done: int, total: int) -> str:
    if total <= 0:
        bar = BAR_EMPTY * BAR_LEN
        return f"`0/0 (0%)` `{bar}`"
    pct = int(round(done * 100.0 / total))
    filled = int(done * BAR_LEN / total)
    filled = max(0, min(BAR_LEN, filled))
    bar = (BAR_FILL * filled) + (BAR_EMPTY * (BAR_LEN - filled))
    return f"`{done}/{total} ({pct}%)` `{bar}`"


def find_sections(lines: list[str]) -> list[Section]:
    headings: list[tuple[str, int]] = []
    for idx, line in enumerate(lines):
        match = H2_RE.match(line)
        if match:
            headings.append((match.group("title"), idx))

    sections: list[Section] = []
    for i, (title, start) in enumerate(headings):
        end = headings[i + 1][1] if i + 1 < len(headings) else len(lines)
        body = lines[start + 1 : end]
        done, total = count_checkboxes(body)
        sections.append(Section(title=title, start=start, end=end, done=done, total=total))
    return sections


def replace_progress_line(block: list[str], done: int, total: int) -> list[str]:
    for i, line in enumerate(block):
        if PROGRESS_RE.match(line):
            block[i] = f"- 进度：{render_progress(done, total)}\n"
            break
    return block


def update_overview(lines: list[str], sections: list[Section]) -> list[str]:
    start_idx = None
    end_idx = None
    for idx, line in enumerate(lines):
        stripped = line.strip()
        if stripped == OVERVIEW_START:
            start_idx = idx
        elif stripped == OVERVIEW_END:
            end_idx = idx
            break

    if start_idx is None or end_idx is None or end_idx <= start_idx:
        return lines

    overview_lines: list[str] = []
    for section in sections:
        if section.title in {"总进度", "模块概览"}:
            continue
        overview_lines.append(f"- {section.title}：{render_progress(section.done, section.total)}\n")

    return lines[: start_idx + 1] + overview_lines + lines[end_idx:]


def main() -> int:
    todo_path = Path(__file__).resolve().parents[1] / "TODO.md"
    if not todo_path.exists():
        raise SystemExit(f"TODO file not found: {todo_path}")

    lines = todo_path.read_text(encoding="utf-8").splitlines(keepends=True)
    sections = find_sections(lines)

    overall_done, overall_total = count_checkboxes(lines)

    # Update per-section progress lines.
    updated = list(lines)
    for section in sections:
        block = updated[section.start : section.end]
        if section.title == "总进度":
            replace_progress_line(block, overall_done, overall_total)
        else:
            replace_progress_line(block, section.done, section.total)
        updated[section.start : section.end] = block

    # Update module overview.
    updated = update_overview(updated, sections)

    todo_path.write_text("".join(updated), encoding="utf-8")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
