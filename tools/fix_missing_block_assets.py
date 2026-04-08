#!/usr/bin/env python3
"""Auto-repair missing legacy-port assets from `reports/migration_missing.json`.

What it fixes:
- Missing block models / blockstates / block item-model wrappers.
- Missing item models.
- Missing fluid block models / blockstates.

Repair strategy (in order):
1) Reuse existing files if already present.
2) Copy matching legacy JSON/texture from old repo (default: ~/HBM-s-Nuclear-Tech-GIT).
3) Generate fallback assets (stone/paper-based) so resources are loadable.

This script is intentionally pragmatic: it prioritizes making resources usable and
load-error free for large migration batches.
"""

from __future__ import annotations

import argparse
import json
import shutil
import subprocess
import sys
from dataclasses import dataclass, field
from pathlib import Path
from typing import Dict, List, Optional, Sequence

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_LEGACY = Path.home() / "HBM-s-Nuclear-Tech-GIT"
FALLBACK_LEGACY = Path.home() / "Hbm-s-Nuclear-Tech-GIT"

MAIN_ASSETS = ROOT / "src" / "main" / "resources" / "assets" / "hbm"
MAIN_MODELS_BLOCK = MAIN_ASSETS / "models" / "block"
MAIN_MODELS_ITEM = MAIN_ASSETS / "models" / "item"
MAIN_BLOCKSTATES = MAIN_ASSETS / "blockstates"
MAIN_TEXTURES = MAIN_ASSETS / "textures"

REPORT_MISSING = ROOT / "reports" / "migration_missing.json"
REPORT_FIX_SUMMARY = ROOT / "reports" / "asset_autofix_summary.json"
REPORT_UNRESOLVED = ROOT / "reports" / "asset_autofix_unresolved.json"


@dataclass
class FixCounters:
    total: int = 0
    fixed: int = 0
    copied_legacy_json: int = 0
    copied_legacy_texture: int = 0
    generated_model: int = 0
    generated_blockstate: int = 0
    unresolved: List[str] = field(default_factory=list)


@dataclass
class FixReport:
    item: FixCounters = field(default_factory=FixCounters)
    block: FixCounters = field(default_factory=FixCounters)
    fluid: FixCounters = field(default_factory=FixCounters)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Auto-repair missing migrated HBM assets")
    parser.add_argument("--project-root", type=Path, default=ROOT)
    parser.add_argument("--legacy-root", type=Path, default=DEFAULT_LEGACY)
    parser.add_argument("--missing-report", type=Path, default=REPORT_MISSING)
    parser.add_argument("--dry-run", action="store_true", help="Show actions without writing files")
    parser.add_argument("--skip-audit", action="store_true", help="Do not auto-run tools/audit_port.py when missing report is absent")
    return parser.parse_args()


def resolve_legacy_root(path: Path) -> Path:
    resolved = path.expanduser().resolve()
    if resolved.exists():
        return resolved
    if path == DEFAULT_LEGACY and FALLBACK_LEGACY.exists():
        return FALLBACK_LEGACY.resolve()
    return resolved


def read_json(path: Path) -> dict:
    return json.loads(path.read_text(encoding="utf-8"))


def write_json(path: Path, data: dict, dry_run: bool) -> None:
    if dry_run:
        return
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def copy_file(src: Path, dst: Path, dry_run: bool) -> None:
    if dry_run:
        return
    dst.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(src, dst)


def ensure_missing_report(path: Path, legacy_root: Path, project_root: Path, skip_audit: bool) -> Path:
    report = path.expanduser().resolve()
    if report.exists():
        return report
    if skip_audit:
        raise SystemExit(f"Missing report not found: {report}")
    audit_script = project_root / "tools" / "audit_port.py"
    if not audit_script.exists():
        raise SystemExit(f"Audit script not found: {audit_script}")
    cmd = [sys.executable, str(audit_script), "--legacy-root", str(legacy_root), "--project-root", str(project_root)]
    subprocess.run(cmd, check=True)
    if not report.exists():
        raise SystemExit(f"Audit ran but report still missing: {report}")
    return report


def normalize_hbm_path_segment(segment: str) -> str:
    segment = segment.replace("\\", "/").lower()
    if segment.startswith("textures/"):
        segment = segment[len("textures/") :]
    if segment.startswith("items/"):
        segment = "item/" + segment[len("items/") :]
    if segment.startswith("blocks/"):
        segment = "block/" + segment[len("blocks/") :]
    return segment


def normalize_resource_ref(value: str) -> str:
    if not isinstance(value, str):
        return value
    if value.startswith("#"):
        return value

    if value.startswith("hbm:models/block/"):
        return "hbm:block/" + value[len("hbm:models/block/") :].lower()
    if value.startswith("hbm:models/item/"):
        return "hbm:item/" + value[len("hbm:models/item/") :].lower()

    if ":" in value:
        namespace, path = value.split(":", 1)
        if namespace == "hbm":
            return f"hbm:{normalize_hbm_path_segment(path)}"
        return value

    if value.startswith("minecraft:item/"):
        return value.replace("minecraft:item/", "item/", 1)
    if value.startswith("minecraft:block/"):
        return value.replace("minecraft:block/", "block/", 1)
    return value


def normalize_json_refs(node: object) -> object:
    if isinstance(node, dict):
        out: dict = {}
        for key, value in node.items():
            if key in {"parent", "model"} and isinstance(value, str):
                out[key] = normalize_resource_ref(value)
            elif key == "textures" and isinstance(value, dict):
                textures: dict = {}
                for t_key, t_val in value.items():
                    textures[t_key] = normalize_resource_ref(t_val) if isinstance(t_val, str) else t_val
                out[key] = textures
            else:
                out[key] = normalize_json_refs(value)
        return out
    if isinstance(node, list):
        return [normalize_json_refs(v) for v in node]
    return node


def build_texture_index(texture_root: Path) -> Dict[str, List[Path]]:
    index: Dict[str, List[Path]] = {}
    if not texture_root.exists():
        return index
    for png in texture_root.rglob("*.png"):
        key = png.stem.lower()
        index.setdefault(key, []).append(png)
    return index


def prefer_texture(paths: Sequence[Path], prefer_kind: str, textures_root: Path) -> Optional[Path]:
    if not paths:
        return None

    def score(path: Path) -> tuple[int, int, str]:
        rel = path.relative_to(textures_root).as_posix().lower()
        kind = rel.split("/", 1)[0]
        s0 = 0
        if prefer_kind == "item":
            if kind == "item":
                s0 = 0
            elif kind == "items":
                s0 = 1
            elif kind == "block":
                s0 = 3
            else:
                s0 = 4
        else:
            if kind == "block":
                s0 = 0
            elif kind == "env":
                s0 = 1
            elif kind == "blocks":
                s0 = 2
            elif kind == "item":
                s0 = 4
            else:
                s0 = 5
        depth = rel.count("/")
        return (s0, depth, rel)

    return sorted(paths, key=score)[0]


def texture_rel_to_id(rel: Path) -> str:
    rel_no_ext = rel.with_suffix("").as_posix().lower()
    rel_no_ext = normalize_hbm_path_segment(rel_no_ext)
    return f"hbm:{rel_no_ext}"


def maybe_copy_legacy_texture(
    name: str,
    prefer_kind: str,
    legacy_textures: Path,
    main_textures: Path,
    legacy_index: Dict[str, List[Path]],
    dry_run: bool,
) -> Optional[str]:
    candidates = legacy_index.get(name.lower(), [])
    candidate = prefer_texture(candidates, prefer_kind=prefer_kind, textures_root=legacy_textures)
    if candidate is None:
        return None

    rel = candidate.relative_to(legacy_textures)
    rel_str = rel.as_posix().lower()
    if rel_str.startswith("items/"):
        rel = Path("item") / rel_str[len("items/") :]
    elif rel_str.startswith("blocks/"):
        rel = Path("block") / rel_str[len("blocks/") :]
    else:
        rel = Path(*[p.lower() for p in rel.parts])

    dst = main_textures / rel
    if not dst.exists():
        copy_file(candidate, dst, dry_run)
    return texture_rel_to_id(rel)


def find_texture_id(
    name: str,
    prefer_kind: str,
    main_textures: Path,
    main_index: Dict[str, List[Path]],
    legacy_textures: Path,
    legacy_index: Dict[str, List[Path]],
    dry_run: bool,
) -> tuple[str, bool]:
    candidates = main_index.get(name.lower(), [])
    candidate = prefer_texture(candidates, prefer_kind=prefer_kind, textures_root=main_textures)
    if candidate is not None:
        return texture_rel_to_id(candidate.relative_to(main_textures)), False

    copied = maybe_copy_legacy_texture(
        name=name,
        prefer_kind=prefer_kind,
        legacy_textures=legacy_textures,
        main_textures=main_textures,
        legacy_index=legacy_index,
        dry_run=dry_run,
    )
    if copied is not None:
        return copied, True

    fallback = "minecraft:item/paper" if prefer_kind == "item" else "minecraft:block/stone"
    return fallback, False


def maybe_copy_legacy_json(src: Path, dst: Path, dry_run: bool) -> bool:
    if not src.exists() or dst.exists():
        return False
    data = read_json(src)
    data = normalize_json_refs(data)
    write_json(dst, data, dry_run)
    return True


def ensure_block(
    name: str,
    legacy_assets: Path,
    main_assets: Path,
    main_index: Dict[str, List[Path]],
    legacy_index: Dict[str, List[Path]],
    dry_run: bool,
    counters: FixCounters,
) -> None:
    counters.total += 1

    model_path = main_assets / "models" / "block" / f"{name}.json"
    state_path = main_assets / "blockstates" / f"{name}.json"
    item_model_path = main_assets / "models" / "item" / f"{name}.json"

    legacy_model = legacy_assets / "models" / "block" / f"{name}.json"
    legacy_state = legacy_assets / "blockstates" / f"{name}.json"
    legacy_item_model = legacy_assets / "models" / "item" / f"{name}.json"

    model_ready = model_path.exists()
    state_ready = state_path.exists()

    if not model_ready and maybe_copy_legacy_json(legacy_model, model_path, dry_run):
        counters.copied_legacy_json += 1
        model_ready = True

    if not model_ready:
        texture_id, copied_tex = find_texture_id(
            name=name,
            prefer_kind="block",
            main_textures=main_assets / "textures",
            main_index=main_index,
            legacy_textures=legacy_assets / "textures",
            legacy_index=legacy_index,
            dry_run=dry_run,
        )
        if copied_tex:
            counters.copied_legacy_texture += 1
        model_data = {
            "parent": "block/cube_all",
            "textures": {"all": texture_id},
        }
        write_json(model_path, model_data, dry_run)
        counters.generated_model += 1
        model_ready = True

    if not state_ready and maybe_copy_legacy_json(legacy_state, state_path, dry_run):
        counters.copied_legacy_json += 1
        state_ready = True

    if not state_ready:
        state_data = {
            "multipart": [
                {
                    "apply": {"model": f"hbm:block/{name}"}
                }
            ]
        }
        write_json(state_path, state_data, dry_run)
        counters.generated_blockstate += 1
        state_ready = True

    if not item_model_path.exists():
        if maybe_copy_legacy_json(legacy_item_model, item_model_path, dry_run):
            counters.copied_legacy_json += 1
        else:
            item_model = {"parent": f"hbm:block/{name}"}
            write_json(item_model_path, item_model, dry_run)
            counters.generated_model += 1

    if model_ready and state_ready:
        counters.fixed += 1
    else:
        counters.unresolved.append(name)


def ensure_item(
    name: str,
    legacy_assets: Path,
    main_assets: Path,
    main_index: Dict[str, List[Path]],
    legacy_index: Dict[str, List[Path]],
    dry_run: bool,
    counters: FixCounters,
) -> None:
    counters.total += 1

    item_model_path = main_assets / "models" / "item" / f"{name}.json"
    legacy_item_model = legacy_assets / "models" / "item" / f"{name}.json"

    if item_model_path.exists():
        counters.fixed += 1
        return

    if maybe_copy_legacy_json(legacy_item_model, item_model_path, dry_run):
        counters.copied_legacy_json += 1
        counters.fixed += 1
        return

    texture_id, copied_tex = find_texture_id(
        name=name,
        prefer_kind="item",
        main_textures=main_assets / "textures",
        main_index=main_index,
        legacy_textures=legacy_assets / "textures",
        legacy_index=legacy_index,
        dry_run=dry_run,
    )
    if copied_tex:
        counters.copied_legacy_texture += 1

    item_model = {
        "parent": "item/generated",
        "textures": {
            "layer0": texture_id,
        },
    }
    write_json(item_model_path, item_model, dry_run)
    counters.generated_model += 1
    counters.fixed += 1


def ensure_fluid(name: str, main_assets: Path, dry_run: bool, counters: FixCounters) -> None:
    counters.total += 1
    state_path = main_assets / "blockstates" / f"{name}.json"

    if not state_path.exists():
        variants = {f"level={i}": {"model": "minecraft:block/water"} for i in range(16)}
        write_json(state_path, {"variants": variants}, dry_run)
        counters.generated_blockstate += 1

    if state_path.exists() or dry_run:
        counters.fixed += 1
    else:
        counters.unresolved.append(name)


def save_reports(report: FixReport, dry_run: bool) -> None:
    summary = {
        "item": report.item.__dict__,
        "block": report.block.__dict__,
        "fluid": report.fluid.__dict__,
    }
    unresolved = {
        "item": report.item.unresolved,
        "block": report.block.unresolved,
        "fluid": report.fluid.unresolved,
    }

    if not dry_run:
        REPORT_FIX_SUMMARY.parent.mkdir(parents=True, exist_ok=True)
        REPORT_FIX_SUMMARY.write_text(json.dumps(summary, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
        REPORT_UNRESOLVED.write_text(json.dumps(unresolved, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def print_report(report: FixReport, dry_run: bool) -> None:
    def fmt(name: str, c: FixCounters) -> str:
        coverage = (c.fixed / c.total * 100.0) if c.total else 100.0
        return (
            f"{name}: fixed {c.fixed}/{c.total} ({coverage:.1f}%), "
            f"legacy_json={c.copied_legacy_json}, legacy_tex={c.copied_legacy_texture}, "
            f"gen_model={c.generated_model}, gen_state={c.generated_blockstate}, "
            f"unresolved={len(c.unresolved)}"
        )

    print("[dry-run]" if dry_run else "[apply]")
    print(fmt("item", report.item))
    print(fmt("block", report.block))
    print(fmt("fluid", report.fluid))

    total = report.item.total + report.block.total + report.fluid.total
    fixed = report.item.fixed + report.block.fixed + report.fluid.fixed
    coverage = (fixed / total * 100.0) if total else 100.0
    print(f"overall: fixed {fixed}/{total} ({coverage:.1f}%)")
    if not dry_run:
        print(f"summary json: {REPORT_FIX_SUMMARY}")
        print(f"unresolved json: {REPORT_UNRESOLVED}")
    for label, counters in (("item", report.item), ("block", report.block), ("fluid", report.fluid)):
        if counters.unresolved:
            sample = ", ".join(counters.unresolved[:8])
            print(f"{label} unresolved sample: {sample}")


def main() -> None:
    args = parse_args()

    project_root = args.project_root.expanduser().resolve()
    legacy_root = resolve_legacy_root(args.legacy_root)
    if not legacy_root.exists():
        raise SystemExit(f"Legacy repo not found: {legacy_root}")

    report_path = ensure_missing_report(
        path=args.missing_report,
        legacy_root=legacy_root,
        project_root=project_root,
        skip_audit=args.skip_audit,
    )
    missing = read_json(report_path)

    main_assets = project_root / "src" / "main" / "resources" / "assets" / "hbm"
    legacy_assets = legacy_root / "src" / "main" / "resources" / "assets" / "hbm"
    main_textures = main_assets / "textures"
    legacy_textures = legacy_assets / "textures"

    main_index = build_texture_index(main_textures)
    legacy_index = build_texture_index(legacy_textures)

    fix_report = FixReport()

    for name in sorted(set(missing.get("item", []))):
        ensure_item(
            name=name,
            legacy_assets=legacy_assets,
            main_assets=main_assets,
            main_index=main_index,
            legacy_index=legacy_index,
            dry_run=args.dry_run,
            counters=fix_report.item,
        )

    for name in sorted(set(missing.get("block", []))):
        ensure_block(
            name=name,
            legacy_assets=legacy_assets,
            main_assets=main_assets,
            main_index=main_index,
            legacy_index=legacy_index,
            dry_run=args.dry_run,
            counters=fix_report.block,
        )

    for name in sorted(set(missing.get("fluid", []))):
        ensure_fluid(
            name=name,
            main_assets=main_assets,
            dry_run=args.dry_run,
            counters=fix_report.fluid,
        )

    save_reports(fix_report, dry_run=args.dry_run)
    print_report(fix_report, dry_run=args.dry_run)


if __name__ == "__main__":
    main()
