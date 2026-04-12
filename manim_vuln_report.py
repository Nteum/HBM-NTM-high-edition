"""
Manim template: Responsible vulnerability report video (config-driven).

Quick render (low quality preview):
    VULN_REPORT_YAML=vuln_report_example.yaml ./.venv-manim/bin/manim -pql manim_vuln_report.py VulnReportVideo

Higher quality:
    VULN_REPORT_YAML=vuln_report_example.yaml ./.venv-manim/bin/manim -pqh manim_vuln_report.py VulnReportVideo

Env:
  - VULN_REPORT_YAML: path to YAML report (default: vuln_report.yaml, fallback: vuln_report_example.yaml)
  - VULN_REPORT_HOLD: multiplies all waits (default: 1.0)
  - VULN_REPORT_FAST: if set, overrides hold factor to 0.15 (useful for verifying layout quickly)

Note:
  This template intentionally supports "redacted" PoC content to avoid enabling abuse.
"""

from __future__ import annotations

import os
import textwrap
from pathlib import Path
from typing import Any, Iterable

import yaml
from manim import (
    DOWN,
    LEFT,
    RIGHT,
    UP,
    Arrow,
    Create,
    Dot,
    FadeIn,
    FadeOut,
    LaggedStart,
    Line,
    MarkupText,
    RoundedRectangle,
    Scene,
    Text,
    VGroup,
    Write,
    config,
)


BACKGROUND = "#0b0d12"
CARD_BG = "#121722"
CODE_BG = "#0f1218"
TEXT_COLOR = "#f5f7fb"
PRIMARY = "#5dc3e6"
SECONDARY = "#9be7c4"
ACCENT = "#ff8c66"
DANGER = "#ff5555"
WARN = "#f1fa8c"

TITLE_FONT = "Helvetica"
BODY_FONT = "Helvetica"
CODE_FONT = "Menlo"

CARD_WIDTH = 10.8
CARD_HEIGHT = 5.8

config.background_color = BACKGROUND


def _hold_factor() -> float:
    if os.getenv("VULN_REPORT_FAST"):
        return 0.15
    try:
        return float(os.getenv("VULN_REPORT_HOLD", "1.0"))
    except ValueError:
        return 1.0


def _wrap(text: str, width: int) -> str:
    text = str(text or "")
    if width <= 0:
        return text
    return "\n".join(
        textwrap.wrap(
            text,
            width=width,
            break_long_words=True,
            break_on_hyphens=False,
        )
    )


def _resolve_report_path() -> Path:
    repo_root = Path(__file__).resolve().parent
    env = os.getenv("VULN_REPORT_YAML")
    if env:
        return Path(env).expanduser().resolve()
    default = repo_root / "vuln_report.yaml"
    if default.exists():
        return default
    return repo_root / "vuln_report_example.yaml"


def _load_report() -> dict[str, Any]:
    path = _resolve_report_path()
    try:
        data = yaml.safe_load(path.read_text(encoding="utf-8"))
    except FileNotFoundError:
        data = {}
    except Exception as exc:  # pragma: no cover (render-time diagnostics)
        print(f"[vuln_report] Failed to load {path}: {exc}")
        data = {}

    if not isinstance(data, dict):
        data = {}

    data.setdefault("meta", {})
    data.setdefault("severity", {})
    data.setdefault("summary", [])
    data.setdefault("affected", {})
    data.setdefault("impact", [])
    data.setdefault("attack", {})
    data.setdefault("repro", {})
    data.setdefault("root_cause", {})
    data.setdefault("fix", {})
    data.setdefault("timeline", [])
    data.setdefault("credits", [])
    data.setdefault("recommendations", [])

    return data


def _as_list(value: Any) -> list[str]:
    if value is None:
        return []
    if isinstance(value, list):
        return [str(v) for v in value]
    return [str(value)]


def _rating_from_cvss(cvss: float) -> str:
    if cvss >= 9.0:
        return "Critical"
    if cvss >= 7.0:
        return "High"
    if cvss >= 4.0:
        return "Medium"
    if cvss > 0.0:
        return "Low"
    return "N/A"


def _color_for_rating(rating: str) -> str:
    r = (rating or "").strip().lower()
    if r in ("critical", "严重", "致命"):
        return DANGER
    if r in ("high", "高"):
        return ACCENT
    if r in ("medium", "中"):
        return WARN
    if r in ("low", "低"):
        return SECONDARY
    return PRIMARY


def _sanitize_code(text: str) -> str:
    # MarkupText parses Pango markup: escape meta chars and preserve spaces.
    return (
        str(text)
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace(" ", "\u00A0")
    )


def _fit_group(group: VGroup, max_width: float, max_height: float) -> None:
    if max_width <= 0 or max_height <= 0:
        return
    scale = min(max_width / max(group.width, 1e-6), max_height / max(group.height, 1e-6), 1.0)
    group.scale(scale)


class VulnReportVideo(Scene):
    def construct(self):
        report = _load_report()
        hold = _hold_factor()

        title = self._show_title(report.get("meta", {}))
        self.wait(2.2 * hold)
        self.play(FadeOut(title, shift=UP * 0.2))

        summary = self._show_summary(report)
        self.wait(6.0 * hold)
        self.play(FadeOut(summary, shift=DOWN * 0.25))

        attack = self._show_attack_flow(report)
        self.wait(6.0 * hold)
        self.play(FadeOut(attack, shift=DOWN * 0.25))

        repro = self._show_repro(report)
        self.wait(6.5 * hold)
        self.play(FadeOut(repro, shift=DOWN * 0.25))

        root = self._show_root_cause(report)
        self.wait(7.5 * hold)
        self.play(FadeOut(root, shift=DOWN * 0.25))

        fix = self._show_fix(report)
        self.wait(7.5 * hold)
        self.play(FadeOut(fix, shift=DOWN * 0.25))

        timeline = self._show_timeline(report)
        self.wait(6.0 * hold)
        self.play(FadeOut(timeline, shift=DOWN * 0.25))

        outro = self._show_outro(report)
        self.wait(6.0 * hold)
        self.play(FadeOut(outro, shift=DOWN * 0.25))

        self.wait(0.6 * hold)

    # ------------------------------------------------------------------ #
    def _section_frame(self, title: str, color: str) -> tuple[RoundedRectangle, Text, VGroup]:
        frame = RoundedRectangle(
            width=CARD_WIDTH,
            height=CARD_HEIGHT,
            corner_radius=0.28,
            stroke_color=color,
            stroke_width=2.0,
            fill_color=CARD_BG,
            fill_opacity=0.95,
        )
        title_text = Text(title, color=color, font=TITLE_FONT).scale(0.72)
        title_text.next_to(frame.get_top(), DOWN, buff=0.34).align_to(frame, LEFT).shift(RIGHT * 0.55)
        group = VGroup(frame, title_text)
        return frame, title_text, group

    def _bullets(self, lines: Iterable[str], width: int = 34, scale: float = 0.5) -> VGroup:
        bullet_mobs = []
        for line in lines:
            wrapped = _wrap(line, width=width)
            bullet_mobs.append(Text(f"• {wrapped}", color=TEXT_COLOR, font=BODY_FONT).scale(scale))
        return VGroup(*bullet_mobs).arrange(DOWN, aligned_edge=LEFT, buff=0.18)

    def _code_block(
        self,
        lines: Iterable[str],
        color: str,
        width: float = 5.0,
        height: float = 3.1,
        scale: float = 0.34,
        diff_mode: bool = False,
    ) -> VGroup:
        box = RoundedRectangle(
            width=width,
            height=height,
            corner_radius=0.18,
            stroke_color=color,
            stroke_width=2.0,
            fill_color=CODE_BG,
            fill_opacity=0.92,
        )

        line_mobs = []
        for line in lines:
            line = str(line)
            line_color = "#e0e0e0"
            if diff_mode:
                if line.startswith("+"):
                    line_color = SECONDARY
                elif line.startswith("-"):
                    line_color = ACCENT
            line_mobs.append(MarkupText(_sanitize_code(line), font=CODE_FONT, color=line_color).scale(scale))

        stack = VGroup(*line_mobs).arrange(DOWN, aligned_edge=LEFT, buff=0.05)
        _fit_group(stack, max_width=box.width - 0.6, max_height=box.height - 0.45)
        stack.move_to(box.get_center()).align_to(box, LEFT).shift(RIGHT * 0.3)
        return VGroup(box, stack)

    def _chip(self, text: str, color: str, width: float = 3.4) -> VGroup:
        pill = RoundedRectangle(
            width=width,
            height=0.84,
            corner_radius=0.28,
            stroke_color=color,
            stroke_width=2.0,
            fill_color=CARD_BG,
            fill_opacity=0.95,
        )
        label = Text(text, color=TEXT_COLOR, font=BODY_FONT).scale(0.48)
        label.move_to(pill.get_center())
        return VGroup(pill, label)

    # ------------------------------------------------------------------ #
    def _show_title(self, meta: dict[str, Any]) -> VGroup:
        title_text = Text(meta.get("title", "漏洞汇报 (模板)"), color=PRIMARY, font=TITLE_FONT).scale(0.92)

        subtitle_pieces = []
        if meta.get("id"):
            subtitle_pieces.append(str(meta["id"]))
        if meta.get("date"):
            subtitle_pieces.append(str(meta["date"]))
        if meta.get("target"):
            subtitle_pieces.append(str(meta["target"]))
        subtitle = Text(" · ".join(subtitle_pieces) or "Responsible Disclosure", color=TEXT_COLOR, font=BODY_FONT).scale(
            0.48
        )
        subtitle.next_to(title_text, DOWN, buff=0.26)

        reporter = Text(f"Reporter: {meta.get('reporter', 'N/A')}", color=TEXT_COLOR, font=BODY_FONT).scale(0.42)
        reporter.next_to(subtitle, DOWN, buff=0.22)

        badge = self._chip("PoC 已脱敏 · 请勿滥用", color=ACCENT, width=4.0).scale(0.95)
        badge.next_to(reporter, DOWN, buff=0.32)

        frame = RoundedRectangle(
            width=10.8,
            height=3.9,
            corner_radius=0.32,
            stroke_color=PRIMARY,
            stroke_width=2.2,
            fill_color=CARD_BG,
            fill_opacity=0.95,
        )

        content = VGroup(title_text, subtitle, reporter, badge)
        content.move_to(frame.get_center())
        group = VGroup(frame, content)

        self.play(Create(frame), Write(title_text))
        self.play(FadeIn(subtitle, shift=UP * 0.12), FadeIn(reporter, shift=UP * 0.12))
        self.play(FadeIn(badge, shift=UP * 0.12))
        return group

    def _show_summary(self, report: dict[str, Any]) -> VGroup:
        frame, title, base = self._section_frame("概要 / Executive Summary", PRIMARY)
        self.play(FadeIn(frame, shift=UP * 0.2), Write(title))

        severity = report.get("severity", {}) or {}
        cvss_raw = severity.get("cvss", 0.0)
        try:
            cvss = float(cvss_raw)
        except (TypeError, ValueError):
            cvss = 0.0

        rating = str(severity.get("rating") or _rating_from_cvss(cvss))
        sev_color = _color_for_rating(rating)
        sev = self._chip(f"CVSS {cvss:.1f}  {rating}", sev_color, width=3.6)
        sev.next_to(title, DOWN, buff=0.35).align_to(frame, RIGHT).shift(LEFT * 0.55)

        summary_lines = _as_list(report.get("summary", [])) or ["(在 YAML 里填写 summary 列表)"]
        bullets = self._bullets(summary_lines, width=36, scale=0.52)
        bullets.next_to(title, DOWN, buff=0.65).align_to(frame, LEFT).shift(RIGHT * 0.55)

        affected = report.get("affected", {}) or {}
        versions = ", ".join(_as_list(affected.get("versions", []))[:3]) or "N/A"
        components = _as_list(affected.get("components", []))
        comp_preview = components[:4]
        comp_line = " / ".join(comp_preview) + (" ..." if len(components) > len(comp_preview) else "")

        kv = VGroup(
            Text(f"Affected: {versions}", color=TEXT_COLOR, font=BODY_FONT).scale(0.44),
            Text(f"Components: {comp_line or 'N/A'}", color=TEXT_COLOR, font=BODY_FONT).scale(0.44),
        ).arrange(DOWN, aligned_edge=LEFT, buff=0.18)
        kv.next_to(sev, DOWN, buff=0.35).align_to(sev, LEFT)
        _fit_group(kv, max_width=frame.width * 0.42, max_height=frame.height * 0.36)

        group = VGroup(base, sev, bullets, kv)
        self.play(LaggedStart(FadeIn(sev, shift=UP * 0.15), FadeIn(bullets, shift=UP * 0.15), lag_ratio=0.2))
        self.play(FadeIn(kv, shift=UP * 0.15))
        return group

    def _show_attack_flow(self, report: dict[str, Any]) -> VGroup:
        frame, title, base = self._section_frame("攻击路径 / Attack Flow", ACCENT)
        self.play(FadeIn(frame, shift=UP * 0.2), Write(title))

        attack = report.get("attack", {}) or {}
        vector = _as_list(attack.get("vector", [])) or ["N/A"]
        prereq = _as_list(attack.get("prerequisites", []))

        impacted = _as_list(report.get("impact", [])) or ["N/A"]
        impacts = self._bullets([_wrap(s, 22) for s in impacted], width=28, scale=0.5)

        step_titles = [
            ("攻击者", PRIMARY),
            ("入口/载荷", ACCENT),
            ("服务端处理", WARN),
            ("影响", DANGER),
        ]
        boxes = []
        for label, color in step_titles:
            box = RoundedRectangle(
                width=3.2,
                height=1.15,
                corner_radius=0.22,
                stroke_color=color,
                fill_color=CARD_BG,
                fill_opacity=0.95,
            )
            text = Text(label, color=color, font=BODY_FONT).scale(0.5)
            text.move_to(box.get_center())
            boxes.append(VGroup(box, text))

        chain = VGroup(*boxes).arrange(DOWN, buff=0.32)
        chain.next_to(title, DOWN, buff=0.6).align_to(frame, LEFT).shift(RIGHT * 0.75)

        arrows = VGroup()
        for upper, lower in zip(boxes, boxes[1:]):
            arrows.add(Arrow(upper.get_bottom(), lower.get_top(), buff=0.12, color=TEXT_COLOR))

        vector_text = self._bullets(vector[:3], width=28, scale=0.44)
        vector_text.next_to(chain[1], RIGHT, buff=0.55).shift(UP * 0.05)

        prereq_text = self._bullets(prereq[:3], width=28, scale=0.44)
        prereq_text.next_to(chain[0], RIGHT, buff=0.55).shift(UP * 0.05)
        if not prereq:
            prereq_text = Text("Prereq: N/A", color=TEXT_COLOR, font=BODY_FONT).scale(0.44)
            prereq_text.next_to(chain[0], RIGHT, buff=0.55).shift(UP * 0.05)

        impacts.next_to(chain[3], RIGHT, buff=0.55).shift(UP * 0.15)
        _fit_group(vector_text, max_width=frame.width * 0.45, max_height=frame.height * 0.22)
        _fit_group(prereq_text, max_width=frame.width * 0.45, max_height=frame.height * 0.22)
        _fit_group(impacts, max_width=frame.width * 0.45, max_height=frame.height * 0.30)

        group = VGroup(base, chain, arrows, vector_text, prereq_text, impacts)
        self.play(FadeIn(chain, shift=UP * 0.15))
        self.play(Create(arrows))
        self.play(FadeIn(prereq_text, shift=UP * 0.1), FadeIn(vector_text, shift=UP * 0.1), FadeIn(impacts, shift=UP * 0.1))
        return group

    def _show_repro(self, report: dict[str, Any]) -> VGroup:
        frame, title, base = self._section_frame("复现步骤 / Reproduction (Redacted)", SECONDARY)
        self.play(FadeIn(frame, shift=UP * 0.2), Write(title))

        repro = report.get("repro", {}) or {}
        notes = str(repro.get("notes") or "为避免滥用，本段仅展示高层步骤，细节请私下交换。")
        steps = _as_list(repro.get("steps", [])) or ["(在 YAML 里填写 repro.steps 列表)"]

        note_text = Text(_wrap(notes, 44), color=TEXT_COLOR, font=BODY_FONT).scale(0.44)
        note_text.next_to(title, DOWN, buff=0.35).align_to(frame, LEFT).shift(RIGHT * 0.55)

        bullets = self._bullets(steps, width=44, scale=0.5)
        bullets.next_to(note_text, DOWN, buff=0.3).align_to(note_text, LEFT)
        _fit_group(bullets, max_width=frame.width - 1.1, max_height=frame.height - 2.0)

        redacted = self._chip("REDACTED", color=ACCENT, width=2.4).scale(0.9)
        redacted.move_to(frame.get_top() + DOWN * 0.55 + RIGHT * 3.7)

        group = VGroup(base, note_text, bullets, redacted)
        self.play(FadeIn(note_text, shift=UP * 0.12))
        self.play(FadeIn(bullets, shift=UP * 0.12))
        self.play(FadeIn(redacted, shift=UP * 0.12))
        return group

    def _show_root_cause(self, report: dict[str, Any]) -> VGroup:
        frame, title, base = self._section_frame("根因分析 / Root Cause", WARN)
        self.play(FadeIn(frame, shift=UP * 0.2), Write(title))

        root = report.get("root_cause", {}) or {}
        bullets = self._bullets(_as_list(root.get("bullets", [])) or ["(在 YAML 里填写 root_cause.bullets)"], width=34, scale=0.5)
        bullets.next_to(title, DOWN, buff=0.55).align_to(frame, LEFT).shift(RIGHT * 0.55)

        code_lines = _as_list(root.get("code", [])) or [
            "if (!authorized(sender, pos)) return;",
            "tile.applyWhitelistedSync(updateTag);",
        ]
        code = self._code_block(code_lines, color=WARN, width=5.2, height=3.3, scale=0.34, diff_mode=False)
        code.next_to(title, DOWN, buff=0.55).align_to(frame, RIGHT).shift(LEFT * 0.55)

        group = VGroup(base, bullets, code)
        self.play(FadeIn(bullets, shift=UP * 0.12), FadeIn(code, shift=UP * 0.12))
        return group

    def _show_fix(self, report: dict[str, Any]) -> VGroup:
        frame, title, base = self._section_frame("修复方案 / Fix", PRIMARY)
        self.play(FadeIn(frame, shift=UP * 0.2), Write(title))

        fix = report.get("fix", {}) or {}
        bullets = self._bullets(_as_list(fix.get("bullets", [])) or ["(在 YAML 里填写 fix.bullets)"], width=34, scale=0.5)
        bullets.next_to(title, DOWN, buff=0.55).align_to(frame, LEFT).shift(RIGHT * 0.55)

        diff_lines = _as_list(fix.get("diff", [])) or [
            "- tile.load(updateTag);",
            "+ if (!authorized(sender, tilePos)) return;",
            "+ tile.applyWhitelistedSync(updateTag);",
        ]
        diff = self._code_block(diff_lines, color=PRIMARY, width=5.2, height=3.3, scale=0.34, diff_mode=True)
        diff.next_to(title, DOWN, buff=0.55).align_to(frame, RIGHT).shift(LEFT * 0.55)

        group = VGroup(base, bullets, diff)
        self.play(FadeIn(bullets, shift=UP * 0.12), FadeIn(diff, shift=UP * 0.12))
        return group

    def _show_timeline(self, report: dict[str, Any]) -> VGroup:
        frame, title, base = self._section_frame("时间线 / Timeline", SECONDARY)
        self.play(FadeIn(frame, shift=UP * 0.2), Write(title))

        events = report.get("timeline", [])
        if not isinstance(events, list):
            events = []

        if not events:
            events = [
                {"date": "2026-02-10", "event": "发现问题并复现"},
                {"date": "2026-02-11", "event": "私下通知维护者"},
                {"date": "2026-02-14", "event": "修复合并"},
                {"date": "2026-02-15", "event": "发布安全公告"},
            ]

        line = Line(LEFT * 4.6, RIGHT * 4.6, color=TEXT_COLOR, stroke_width=2.0)
        line.next_to(title, DOWN, buff=1.2)

        n = max(1, min(len(events), 6))
        dot_positions = [line.point_from_proportion(i / (n - 1 if n > 1 else 1)) for i in range(n)]

        dots = []
        labels = []
        for pos, ev in zip(dot_positions, events[:n]):
            dot = Dot(pos, color=SECONDARY, radius=0.08)
            date = Text(str(ev.get("date", "")), color=TEXT_COLOR, font=BODY_FONT).scale(0.36)
            desc = Text(_wrap(str(ev.get("event", "")), 10), color=TEXT_COLOR, font=BODY_FONT).scale(0.42)
            date.next_to(dot, UP, buff=0.18)
            desc.next_to(dot, DOWN, buff=0.22)
            dots.append(dot)
            labels.extend([date, desc])

        group = VGroup(base, line, *dots, *labels)
        self.play(Create(line))
        self.play(LaggedStart(*[FadeIn(d, shift=UP * 0.1) for d in dots], lag_ratio=0.12))
        self.play(LaggedStart(*[FadeIn(l, shift=UP * 0.1) for l in labels], lag_ratio=0.05))
        return group

    def _show_outro(self, report: dict[str, Any]) -> VGroup:
        frame, title, base = self._section_frame("结论 / Next Steps", ACCENT)
        self.play(FadeIn(frame, shift=UP * 0.2), Write(title))

        recs = _as_list(report.get("recommendations", []))
        if not recs:
            recs = [
                "优先升级到已修复版本或回滚到安全配置",
                "服务端开启日志与速率限制，监控可疑同步报文",
                "对外公告时提供最小必要信息，PoC 通过私下渠道提供",
            ]
        bullets = self._bullets(recs, width=44, scale=0.5)
        bullets.next_to(title, DOWN, buff=0.6).align_to(frame, LEFT).shift(RIGHT * 0.55)

        credits = _as_list(report.get("credits", []))
        credit_lines = credits or ["(可选) credits: 署名 / 致谢 / 联系方式"]
        credit_box = self._code_block(credit_lines, color=ACCENT, width=5.2, height=2.4, scale=0.34, diff_mode=False)
        credit_box.next_to(title, DOWN, buff=0.6).align_to(frame, RIGHT).shift(LEFT * 0.55)

        footer = Text("Template only · Follow responsible disclosure", color=TEXT_COLOR, font=BODY_FONT).scale(0.36)
        footer.next_to(frame.get_bottom(), UP, buff=0.28)

        group = VGroup(base, bullets, credit_box, footer)
        self.play(FadeIn(bullets, shift=UP * 0.12), FadeIn(credit_box, shift=UP * 0.12))
        self.play(FadeIn(footer, shift=UP * 0.12))
        return group
