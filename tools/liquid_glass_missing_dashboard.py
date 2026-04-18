#!/usr/bin/env python3
"""PySide6 liquid-glass dashboard for missing model/texture diagnostics.

Features:
- Parse Minecraft/Forge `latest.log` missing-resource errors.
- Optional baseline log comparison.
- Auto output JSON report to `reports/missing_asset_detection.json`.
- Console summary output (totals + top missing resources).
- Visual charts (Seaborn): pie, bar, line, heatmap.
- Chart-type toggle buttons (multi-select).
- Draggable/resizable split layout (up/down + left/right).
"""

from __future__ import annotations

import argparse
import json
import math
import re
import sys
from collections import Counter
from dataclasses import asdict, dataclass
from datetime import datetime
from pathlib import Path
from typing import Optional


ROOT = Path(__file__).resolve().parents[1]
DEFAULT_CURRENT_LOG = ROOT / "run" / "logs" / "latest.log"
DEFAULT_REPORT = ROOT / "reports" / "missing_asset_detection.json"


@dataclass
class MissingEvent:
    category: str
    resource: str
    line: str


@dataclass
class MissingAnalysis:
    log_path: str
    total_events: int
    category_counts: dict[str, int]
    resource_counts: dict[str, int]
    generated_at: str


PATTERNS: list[tuple[str, re.Pattern[str]]] = [
    (
        "missing_variant_model",
        re.compile(r"missing model for variant: '([^']+)'", re.IGNORECASE),
    ),
    (
        "unable_load_model",
        re.compile(r"Unable to load model: '([^']+)'", re.IGNORECASE),
    ),
    (
        "missing_texture",
        re.compile(r"Unable to resolve texture reference: ([^\s]+)", re.IGNORECASE),
    ),
    (
        "filenotfound_model",
        re.compile(r"FileNotFoundException: ([^\s]+models/[^\s]+\.json)", re.IGNORECASE),
    ),
]


def normalize_resource(resource: str) -> str:
    resource = resource.strip().strip("'").strip('"')
    if resource.startswith("#"):
        resource = resource[1:]
    return resource


def parse_log(log_path: Path) -> list[MissingEvent]:
    if not log_path.exists():
        raise FileNotFoundError(f"Log not found: {log_path}")

    events: list[MissingEvent] = []
    for line in log_path.read_text(encoding="utf-8", errors="ignore").splitlines():
        for category, pattern in PATTERNS:
            m = pattern.search(line)
            if not m:
                continue
            events.append(
                MissingEvent(
                    category=category,
                    resource=normalize_resource(m.group(1)),
                    line=line.strip(),
                )
            )
            break
    return events


def analyze_log(log_path: Path) -> tuple[MissingAnalysis, list[MissingEvent]]:
    events = parse_log(log_path)
    category_counter = Counter(e.category for e in events)
    resource_counter = Counter(e.resource for e in events)

    analysis = MissingAnalysis(
        log_path=str(log_path),
        total_events=len(events),
        category_counts=dict(sorted(category_counter.items())),
        resource_counts=dict(resource_counter.most_common(500)),
        generated_at=datetime.now().isoformat(timespec="seconds"),
    )
    return analysis, events


def comparison_dict(current: MissingAnalysis, baseline: Optional[MissingAnalysis]) -> dict:
    if baseline is None:
        return {"has_baseline": False}

    all_categories = sorted(set(current.category_counts) | set(baseline.category_counts))
    by_category: dict[str, dict[str, int]] = {}
    for cat in all_categories:
        c = int(current.category_counts.get(cat, 0))
        b = int(baseline.category_counts.get(cat, 0))
        by_category[cat] = {
            "current": c,
            "baseline": b,
            "delta": c - b,
        }

    return {
        "has_baseline": True,
        "current_log": current.log_path,
        "baseline_log": baseline.log_path,
        "total_current": current.total_events,
        "total_baseline": baseline.total_events,
        "total_delta": current.total_events - baseline.total_events,
        "by_category": by_category,
    }


def write_report(
    output_path: Path,
    current: MissingAnalysis,
    current_events: list[MissingEvent],
    baseline: Optional[MissingAnalysis],
    baseline_events: list[MissingEvent],
) -> None:
    output_path.parent.mkdir(parents=True, exist_ok=True)

    report = {
        "current": asdict(current),
        "baseline": asdict(baseline) if baseline else None,
        "comparison": comparison_dict(current, baseline),
        "top_current_missing_resources": [
            {"resource": k, "count": v}
            for k, v in list(current.resource_counts.items())[:30]
        ],
        "sample_current_events": [asdict(e) for e in current_events[:120]],
        "sample_baseline_events": [asdict(e) for e in baseline_events[:120]],
    }
    output_path.write_text(json.dumps(report, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def print_console_summary(current: MissingAnalysis, baseline: Optional[MissingAnalysis]) -> None:
    print(f"current_log: {current.log_path}")
    print(f"current_total_missing_events: {current.total_events}")
    print("current_by_category:")
    for k, v in current.category_counts.items():
        print(f"  - {k}: {v}")

    print("current_top_missing_resources:")
    for resource, count in list(current.resource_counts.items())[:10]:
        print(f"  - {resource}: {count}")

    if baseline:
        print(f"baseline_log: {baseline.log_path}")
        print(f"baseline_total_missing_events: {baseline.total_events}")
        print(f"delta_total: {current.total_events - baseline.total_events:+d}")


class _GuiApp:
    CHART_KEYS = ("pie", "bar", "line", "heatmap")
    CHART_LABELS = {
        "pie": "Pie",
        "bar": "Bar",
        "line": "Line",
        "heatmap": "Heatmap",
    }

    def __init__(self) -> None:
        from PySide6.QtCore import Qt
        from PySide6.QtGui import QFont
        from PySide6.QtWidgets import (
            QCheckBox,
            QFileDialog,
            QFrame,
            QGridLayout,
            QHBoxLayout,
            QHeaderView,
            QLabel,
            QLineEdit,
            QMainWindow,
            QMessageBox,
            QPushButton,
            QSplitter,
            QTableWidget,
            QTableWidgetItem,
            QToolButton,
            QVBoxLayout,
            QWidget,
        )
        from matplotlib.backends.backend_qtagg import FigureCanvasQTAgg, NavigationToolbar2QT
        from matplotlib.figure import Figure

        import seaborn as sns

        self.Qt = Qt
        self.QFileDialog = QFileDialog
        self.QMessageBox = QMessageBox
        self.QTableWidgetItem = QTableWidgetItem
        self.sns = sns
        self.sns.set_theme(style="darkgrid", palette="deep")

        class MainWindow(QMainWindow):
            pass

        self.window = MainWindow()
        self.window.setWindowTitle("HBMH© Missing Assets/Models Dashboard")
        self.window.resize(1580, 980)

        self.current_analysis: Optional[MissingAnalysis] = None
        self.baseline_analysis: Optional[MissingAnalysis] = None

        root = QWidget()
        self.window.setCentralWidget(root)
        root_layout = QVBoxLayout(root)
        root_layout.setContentsMargins(14, 14, 14, 14)
        root_layout.setSpacing(10)

        title = QLabel("HBM Missing Model / Texture Inspector")
        title.setObjectName("title")
        title.setFont(QFont("PingFang SC", 17, QFont.Bold))
        root_layout.addWidget(title)

        # Splitters: up/down + left/right drag-resize
        vertical_splitter = QSplitter(Qt.Orientation.Vertical)
        bottom_splitter = QSplitter(Qt.Orientation.Horizontal)
        vertical_splitter.setChildrenCollapsible(False)
        bottom_splitter.setChildrenCollapsible(False)
        root_layout.addWidget(vertical_splitter, 1)

        # ---- Top panel (controls + stats + chart toggles)
        top_panel = QFrame()
        top_panel.setObjectName("glassCard")
        top_layout = QVBoxLayout(top_panel)
        top_layout.setContentsMargins(12, 12, 12, 12)
        top_layout.setSpacing(10)

        controls_grid = QGridLayout()
        controls_grid.setHorizontalSpacing(8)
        controls_grid.setVerticalSpacing(8)

        self.current_input = QLineEdit(str(DEFAULT_CURRENT_LOG))
        self.baseline_input = QLineEdit("")
        self.output_input = QLineEdit(str(DEFAULT_REPORT))

        browse_current = QPushButton("Browse")
        browse_baseline = QPushButton("Browse")
        browse_output = QPushButton("Browse")

        controls_grid.addWidget(QLabel("Current Log"), 0, 0)
        controls_grid.addWidget(self.current_input, 0, 1)
        controls_grid.addWidget(browse_current, 0, 2)

        controls_grid.addWidget(QLabel("Baseline Log (Optional)"), 1, 0)
        controls_grid.addWidget(self.baseline_input, 1, 1)
        controls_grid.addWidget(browse_baseline, 1, 2)

        controls_grid.addWidget(QLabel("Output JSON"), 2, 0)
        controls_grid.addWidget(self.output_input, 2, 1)
        controls_grid.addWidget(browse_output, 2, 2)

        self.auto_save_check = QCheckBox("Auto save JSON report")
        self.auto_save_check.setChecked(True)
        self.analyze_btn = QPushButton("Analyze")

        controls_grid.addWidget(self.auto_save_check, 3, 1)
        controls_grid.addWidget(self.analyze_btn, 3, 2)

        top_layout.addLayout(controls_grid)

        stats_row = QFrame()
        stats_row.setObjectName("statsCard")
        stats_layout = QHBoxLayout(stats_row)
        stats_layout.setContentsMargins(8, 8, 8, 8)
        stats_layout.setSpacing(12)
        self.current_total_label = QLabel("Current: 0")
        self.baseline_total_label = QLabel("Baseline: -")
        self.delta_label = QLabel("Delta: -")
        stats_layout.addWidget(self.current_total_label)
        stats_layout.addWidget(self.baseline_total_label)
        stats_layout.addWidget(self.delta_label)
        stats_layout.addStretch(1)
        top_layout.addWidget(stats_row)

        toggle_row = QFrame()
        toggle_row.setObjectName("statsCard")
        toggle_layout = QHBoxLayout(toggle_row)
        toggle_layout.setContentsMargins(8, 8, 8, 8)
        toggle_layout.setSpacing(8)
        toggle_layout.addWidget(QLabel("Charts:"))

        self.chart_buttons: dict[str, QToolButton] = {}
        for key in self.CHART_KEYS:
            btn = QToolButton()
            btn.setText(self.CHART_LABELS[key])
            btn.setCheckable(True)
            btn.setChecked(True)
            btn.clicked.connect(self.update_charts)
            self.chart_buttons[key] = btn
            toggle_layout.addWidget(btn)

        toggle_layout.addStretch(1)
        top_layout.addWidget(toggle_row)

        vertical_splitter.addWidget(top_panel)

        # ---- Charts panel
        chart_panel = QFrame()
        chart_panel.setObjectName("glassCard")
        chart_layout = QVBoxLayout(chart_panel)
        chart_layout.setContentsMargins(10, 10, 10, 10)
        chart_layout.setSpacing(8)

        self.figure = Figure(figsize=(12, 7), dpi=110)
        self.figure.patch.set_facecolor("#111827")
        self.canvas = FigureCanvasQTAgg(self.figure)
        self.toolbar = NavigationToolbar2QT(self.canvas, chart_panel)
        chart_layout.addWidget(self.toolbar)
        chart_layout.addWidget(self.canvas, 1)

        # ---- Table panel
        table_panel = QFrame()
        table_panel.setObjectName("glassCard")
        table_layout = QVBoxLayout(table_panel)
        table_layout.setContentsMargins(10, 10, 10, 10)
        table_layout.setSpacing(8)

        self.top_table = QTableWidget(0, 3)
        self.top_table.setHorizontalHeaderLabels(["Rank", "Resource", "Count"])
        header = self.top_table.horizontalHeader()
        header.setSectionResizeMode(0, QHeaderView.ResizeMode.ResizeToContents)
        header.setSectionResizeMode(1, QHeaderView.ResizeMode.Stretch)
        header.setSectionResizeMode(2, QHeaderView.ResizeMode.ResizeToContents)
        self.top_table.setSortingEnabled(True)
        table_layout.addWidget(self.top_table, 1)

        bottom_splitter.addWidget(chart_panel)
        bottom_splitter.addWidget(table_panel)
        vertical_splitter.addWidget(bottom_splitter)

        vertical_splitter.setStretchFactor(0, 0)
        vertical_splitter.setStretchFactor(1, 1)
        bottom_splitter.setStretchFactor(0, 3)
        bottom_splitter.setStretchFactor(1, 2)
        vertical_splitter.setSizes([260, 700])
        bottom_splitter.setSizes([980, 540])

        self.window.setStyleSheet(
            """
            QMainWindow {
                background: qlineargradient(x1:0, y1:0, x2:1, y2:1,
                    stop:0 rgba(8, 13, 21, 255),
                    stop:1 rgba(18, 28, 43, 255));
            }
            QLabel#title {
                color: rgba(236, 245, 255, 245);
                padding: 2px 2px;
            }
            QFrame#glassCard {
                background: rgba(148, 163, 184, 28);
                border: 1px solid rgba(148, 163, 184, 70);
                border-radius: 14px;
            }
            QFrame#statsCard {
                background: rgba(30, 41, 59, 160);
                border: 1px solid rgba(148, 163, 184, 60);
                border-radius: 10px;
            }
            QLabel, QCheckBox {
                color: rgba(241, 245, 249, 238);
            }
            QLineEdit, QTableWidget {
                background: rgba(2, 6, 23, 210);
                color: rgba(248, 250, 252, 242);
                border: 1px solid rgba(148, 163, 184, 80);
                border-radius: 8px;
                padding: 6px;
            }
            QPushButton {
                background: rgba(59, 130, 246, 150);
                color: rgba(255, 255, 255, 240);
                border-radius: 8px;
                padding: 7px 12px;
                border: 1px solid rgba(147, 197, 253, 100);
            }
            QPushButton:hover {
                background: rgba(96, 165, 250, 180);
            }
            QToolButton {
                background: rgba(30, 64, 175, 110);
                color: rgba(226, 232, 240, 235);
                border: 1px solid rgba(147, 197, 253, 80);
                border-radius: 8px;
                padding: 6px 10px;
            }
            QToolButton:checked {
                background: rgba(34, 197, 94, 180);
                color: rgba(7, 12, 20, 255);
                border: 1px solid rgba(134, 239, 172, 150);
                font-weight: 700;
            }
            QHeaderView::section {
                background: rgba(30, 41, 59, 220);
                color: rgba(226, 232, 240, 235);
                border: none;
                padding: 6px;
            }
            QSplitter::handle {
                background: rgba(148, 163, 184, 95);
            }
            QSplitter::handle:hover {
                background: rgba(125, 211, 252, 160);
            }
            """
        )

        browse_current.clicked.connect(lambda: self.pick_file(self.current_input, save=False))
        browse_baseline.clicked.connect(lambda: self.pick_file(self.baseline_input, save=False))
        browse_output.clicked.connect(lambda: self.pick_file(self.output_input, save=True))
        self.analyze_btn.clicked.connect(self.run_analysis)

        self.run_analysis(initial=True)

    def pick_file(self, target, save: bool) -> None:
        if save:
            path, _ = self.QFileDialog.getSaveFileName(
                self.window,
                "Select output JSON",
                target.text(),
                "JSON (*.json)",
            )
        else:
            path, _ = self.QFileDialog.getOpenFileName(
                self.window,
                "Select log file",
                target.text(),
                "Log (*.log *.txt);;All files (*)",
            )
        if path:
            target.setText(path)

    def run_analysis(self, initial: bool = False) -> None:
        current_path = Path(self.current_input.text().strip() or DEFAULT_CURRENT_LOG)
        baseline_raw = self.baseline_input.text().strip()
        baseline_path = Path(baseline_raw) if baseline_raw else None

        try:
            current, current_events = analyze_log(current_path)
            baseline = None
            baseline_events: list[MissingEvent] = []
            if baseline_path:
                baseline, baseline_events = analyze_log(baseline_path)

            self.current_analysis = current
            self.baseline_analysis = baseline

            self.update_stats(current, baseline)
            self.update_table(current)
            self.update_charts()
            print_console_summary(current, baseline)

            if self.auto_save_check.isChecked():
                out = Path(self.output_input.text().strip() or DEFAULT_REPORT)
                write_report(out, current, current_events, baseline, baseline_events)
                print(f"json_report: {out}")

        except Exception as exc:
            if not initial:
                self.QMessageBox.critical(self.window, "Analyze failed", str(exc))

    def update_stats(self, current: MissingAnalysis, baseline: Optional[MissingAnalysis]) -> None:
        self.current_total_label.setText(f"Current: {current.total_events}")
        if baseline:
            delta = current.total_events - baseline.total_events
            self.baseline_total_label.setText(f"Baseline: {baseline.total_events}")
            self.delta_label.setText(f"Delta: {delta:+d}")
        else:
            self.baseline_total_label.setText("Baseline: -")
            self.delta_label.setText("Delta: -")

    def update_table(self, current: MissingAnalysis) -> None:
        rows = list(current.resource_counts.items())[:50]
        self.top_table.setRowCount(len(rows))
        for idx, (resource, count) in enumerate(rows, start=1):
            self.top_table.setItem(idx - 1, 0, self.QTableWidgetItem(str(idx)))
            self.top_table.setItem(idx - 1, 1, self.QTableWidgetItem(resource))
            self.top_table.setItem(idx - 1, 2, self.QTableWidgetItem(str(count)))

    def enabled_charts(self) -> list[str]:
        return [k for k in self.CHART_KEYS if self.chart_buttons[k].isChecked()]

    def update_charts(self) -> None:
        if self.current_analysis is None:
            return

        import numpy as np

        current = self.current_analysis
        baseline = self.baseline_analysis

        selected = self.enabled_charts()
        self.figure.clear()
        self.figure.patch.set_facecolor("#111827")

        if not selected:
            ax = self.figure.add_subplot(111)
            ax.set_facecolor("#111827")
            ax.text(0.5, 0.5, "No chart selected", ha="center", va="center", color="#cbd5e1", fontsize=13)
            ax.set_axis_off()
            self.canvas.draw_idle()
            return

        cols = 1 if len(selected) == 1 else 2
        rows = math.ceil(len(selected) / cols)
        axes = [self.figure.add_subplot(rows, cols, i + 1) for i in range(len(selected))]

        for ax in axes:
            ax.set_facecolor("#0f172a")

        for idx, chart_key in enumerate(selected):
            ax = axes[idx]
            if chart_key == "pie":
                self._draw_pie(ax, current)
            elif chart_key == "bar":
                self._draw_bar(ax, current)
            elif chart_key == "line":
                self._draw_line(ax, current, baseline)
            elif chart_key == "heatmap":
                self._draw_heatmap(ax, current, baseline, np)

        self.figure.tight_layout(pad=2.2)
        self.canvas.draw_idle()

    def _draw_pie(self, ax, current: MissingAnalysis) -> None:
        categories = list(current.category_counts.keys())
        counts = list(current.category_counts.values())
        if not counts:
            ax.text(0.5, 0.5, "No missing events", ha="center", va="center", color="#cbd5e1")
            ax.set_axis_off()
            return
        wedges, texts, autotexts = ax.pie(
            counts,
            labels=categories,
            autopct="%1.1f%%",
            startangle=95,
            textprops={"color": "#e2e8f0", "fontsize": 9},
        )
        for a in autotexts:
            a.set_color("#f8fafc")
        ax.set_title("Current Missing Breakdown (Pie)", color="#e2e8f0", fontsize=11)

    def _draw_bar(self, ax, current: MissingAnalysis) -> None:
        top = list(current.resource_counts.items())[:12]
        if not top:
            ax.text(0.5, 0.5, "No resources", ha="center", va="center", color="#cbd5e1")
            ax.set_axis_off()
            return
        labels = [k if len(k) <= 38 else (k[:35] + "...") for k, _ in top]
        values = [v for _, v in top]
        self.sns.barplot(x=labels, y=values, ax=ax, color="#60a5fa")
        ax.set_title("Top Missing Resources (Bar)", color="#e2e8f0", fontsize=11)
        ax.set_xlabel("")
        ax.set_ylabel("Count", color="#e2e8f0")
        ax.tick_params(axis="x", rotation=30, labelsize=8, colors="#cbd5e1")
        ax.tick_params(axis="y", colors="#cbd5e1")

    def _draw_line(self, ax, current: MissingAnalysis, baseline: Optional[MissingAnalysis]) -> None:
        categories = sorted(set(current.category_counts.keys()) | set(baseline.category_counts.keys() if baseline else []))
        if not categories:
            ax.text(0.5, 0.5, "No category data", ha="center", va="center", color="#cbd5e1")
            ax.set_axis_off()
            return

        x = list(range(len(categories)))
        cur_vals = [current.category_counts.get(cat, 0) for cat in categories]
        self.sns.lineplot(x=x, y=cur_vals, ax=ax, marker="o", linewidth=2, label="Current", color="#22d3ee")
        if baseline:
            base_vals = [baseline.category_counts.get(cat, 0) for cat in categories]
            self.sns.lineplot(x=x, y=base_vals, ax=ax, marker="o", linewidth=2, label="Baseline", color="#f97316")

        ax.set_xticks(x)
        ax.set_xticklabels(categories, rotation=25, ha="right", color="#cbd5e1")
        ax.tick_params(axis="y", colors="#cbd5e1")
        ax.set_ylabel("Count", color="#e2e8f0")
        ax.set_title("Category Comparison (Line)", color="#e2e8f0", fontsize=11)
        legend = ax.legend()
        if legend:
            legend.get_frame().set_facecolor("#0f172a")
            legend.get_frame().set_alpha(0.8)
            for t in legend.get_texts():
                t.set_color("#e2e8f0")

    def _draw_heatmap(self, ax, current: MissingAnalysis, baseline: Optional[MissingAnalysis], np) -> None:
        categories = sorted(set(current.category_counts.keys()) | set(baseline.category_counts.keys() if baseline else []))
        if not categories:
            ax.text(0.5, 0.5, "No category data", ha="center", va="center", color="#cbd5e1")
            ax.set_axis_off()
            return

        rows = ["Current"]
        matrix = [[current.category_counts.get(cat, 0) for cat in categories]]
        if baseline:
            rows.append("Baseline")
            matrix.append([baseline.category_counts.get(cat, 0) for cat in categories])

        data = np.array(matrix, dtype=float)
        self.sns.heatmap(
            data,
            annot=True,
            fmt=".0f",
            cmap="mako",
            linewidths=0.35,
            linecolor="#1e293b",
            cbar=True,
            xticklabels=categories,
            yticklabels=rows,
            ax=ax,
        )
        ax.set_title("Category Heatmap (Seaborn)", color="#e2e8f0", fontsize=11)
        ax.tick_params(axis="x", labelrotation=25, labelsize=8, colors="#cbd5e1")
        ax.tick_params(axis="y", labelrotation=0, colors="#cbd5e1")


def run_gui() -> int:
    try:
        from PySide6.QtWidgets import QApplication
        import matplotlib  # noqa: F401
        import seaborn  # noqa: F401
    except Exception as exc:
        print(
            "Missing GUI deps. Install first: pip install PySide6 matplotlib seaborn\n"
            f"detail: {exc}"
        )
        return 1

    app = QApplication(sys.argv)
    ui = _GuiApp()
    ui.window.show()
    return app.exec()


def parse_cli() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Missing model/texture dashboard")
    parser.add_argument("--current-log", type=Path, default=DEFAULT_CURRENT_LOG)
    parser.add_argument("--baseline-log", type=Path, default=None)
    parser.add_argument("--output", type=Path, default=DEFAULT_REPORT)
    parser.add_argument("--nogui", action="store_true", help="Run analyze-only mode and exit")
    return parser.parse_args()


def main() -> int:
    args = parse_cli()

    if not args.nogui:
        return run_gui()

    current, current_events = analyze_log(args.current_log)
    baseline = None
    baseline_events: list[MissingEvent] = []
    if args.baseline_log:
        baseline, baseline_events = analyze_log(args.baseline_log)

    print_console_summary(current, baseline)
    write_report(args.output, current, current_events, baseline, baseline_events)
    print(f"json_report: {args.output}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
