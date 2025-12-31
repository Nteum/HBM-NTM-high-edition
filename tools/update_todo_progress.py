#!/usr/bin/env python3
from __future__ import annotations

from dataclasses import dataclass
import json
from pathlib import Path
import re


CHECKBOX_RE = re.compile(r"^\s*-\s*\[(?P<mark>[ xX])\]\s+")
H2_RE = re.compile(r"^##\s+(?P<title>.+?)\s*$")
PROGRESS_RE = re.compile(r"^\s*-\s*进度：")

OVERVIEW_START = "<!-- overview:start -->"
OVERVIEW_END = "<!-- overview:end -->"

BAR_LEN = 20
BAR_FILL = "█"
BAR_EMPTY = "░"

# Stable milestone IDs; update only if the TODO milestone list changes.
MILESTONE_IDS = ("M0", "M0.5", "M1", "M2", "M3", "M4", "M5")


@dataclass(frozen=True)
class Section:
    title: str
    start: int
    end: int
    done: int
    total: int


@dataclass(frozen=True)
class ModuleProgress:
    name: str
    done: int
    total: int
    percent: int


@dataclass(frozen=True)
class MilestoneProgress:
    id: str
    title: str
    detail: str
    done: bool


@dataclass(frozen=True)
class ProgressStats:
    total_done: int
    total_all: int
    total_percent: int
    modules: list[ModuleProgress]
    milestones: list[MilestoneProgress]


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


def percent_for(done: int, total: int) -> int:
    if total <= 0:
        return 0
    return int(round(done * 100.0 / total))


def render_progress(done: int, total: int) -> str:
    if total <= 0:
        bar = BAR_EMPTY * BAR_LEN
        return f"`0/0 (0%)` `{bar}`"
    pct = percent_for(done, total)
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


def parse_milestone_text(text: str) -> tuple[str, str]:
    body = text.strip()
    for sep in ("：", ":"):
        if sep in body:
            _, body = body.split(sep, 1)
            break
    body = body.strip()

    for left, right in (("（", "）"), ("(", ")")):
        left_idx = body.find(left)
        right_idx = body.rfind(right)
        if left_idx != -1 and right_idx != -1 and right_idx > left_idx:
            title = body[:left_idx].strip()
            detail = body[left_idx + 1 : right_idx].strip()
            return title, detail

    return body.strip(), ""


def extract_milestones(lines: list[str], sections: list[Section]) -> list[MilestoneProgress]:
    milestone_section = next((section for section in sections if section.title == "里程碑"), None)
    if milestone_section is None:
        return []

    milestones: list[MilestoneProgress] = []
    for line in lines[milestone_section.start + 1 : milestone_section.end]:
        match = CHECKBOX_RE.match(line)
        if not match:
            continue
        index = len(milestones)
        if index >= len(MILESTONE_IDS):
            raise ValueError(f"Missing milestone id mapping for entry: {line.strip()}")
        title, detail = parse_milestone_text(line[match.end() :])
        milestones.append(
            MilestoneProgress(
                id=MILESTONE_IDS[index],
                title=title,
                detail=detail,
                done=match.group("mark").lower() == "x",
            )
        )
    return milestones


def build_progress_stats(
    lines: list[str],
    sections: list[Section],
    overall_done: int,
    overall_total: int,
) -> ProgressStats:
    modules: list[ModuleProgress] = []
    for section in sections:
        if section.title in {"总进度", "模块概览", "里程碑"}:
            continue
        modules.append(
            ModuleProgress(
                name=section.title,
                done=section.done,
                total=section.total,
                percent=percent_for(section.done, section.total),
            )
        )

    return ProgressStats(
        total_done=overall_done,
        total_all=overall_total,
        total_percent=percent_for(overall_done, overall_total),
        modules=modules,
        milestones=extract_milestones(lines, sections),
    )


def export_progress_json(stats: ProgressStats, out_path: Path) -> None:
    data = {
        "total": {
            "done": stats.total_done,
            "all": stats.total_all,
            "percent": stats.total_percent,
        },
        "modules": [
            {
                "name": m.name,
                "done": m.done,
                "total": m.total,
                "percent": m.percent,
            }
            for m in stats.modules
        ],
        "milestones": [
            {
                "id": ms.id,
                "title": ms.title,
                "detail": ms.detail,
                "done": ms.done,
            }
            for ms in stats.milestones
        ],
    }

    out_path.parent.mkdir(parents=True, exist_ok=True)
    with out_path.open("w", encoding="utf-8") as handle:
        json.dump(data, handle, ensure_ascii=False, indent=2)


def main() -> int:
    todo_path = Path(__file__).resolve().parents[1] / "TODO.md"
    if not todo_path.exists():
        raise SystemExit(f"TODO file not found: {todo_path}")

    lines = todo_path.read_text(encoding="utf-8").splitlines(keepends=True)
    sections = find_sections(lines)

    overall_done, overall_total = count_checkboxes(lines)
    stats = build_progress_stats(lines, sections, overall_done, overall_total)

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
    export_progress_json(stats, Path(__file__).resolve().parent.parent / "progress.json")
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
