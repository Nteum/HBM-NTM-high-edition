#!/usr/bin/env python3
"""Audit legacy HBM (1.7.x) content vs current port progress.

Default legacy path is `~/HBM-s-Nuclear-Tech-GIT`.

Outputs are written to `/reports`:
- `migration_report.json`
- `migration_summary.json`
- `migration_missing.json`
- `migration_new_only.json`
"""

from __future__ import annotations

import argparse
import json
import re
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, List, Sequence, Set, Tuple

ROOT = Path(__file__).resolve().parents[1]
DEFAULT_OLD_ROOT = Path.home() / "HBM-s-Nuclear-Tech-GIT"
FALLBACK_OLD_ROOT = Path.home() / "Hbm-s-Nuclear-Tech-GIT"

OLD_JAVA_REL = Path("src/main/java")
NEW_JAVA_REL = Path("src/main/java")


@dataclass(frozen=True)
class AuditSets:
    items: Set[str]
    blocks: Set[str]
    fluids: Set[str]


def _read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def _exists(path: Path) -> bool:
    return path.exists() and path.is_file()


def _parse_public_statics(path: Path, type_name: str) -> Set[str]:
    """Extract names from declarations like: `public static Item foo;`"""
    if not _exists(path):
        return set()
    text = _read(path)
    pattern = re.compile(
        rf"\bpublic\s+static\s+{re.escape(type_name)}\s+([A-Za-z_][A-Za-z0-9_]*)\s*;"
    )
    return set(pattern.findall(text))


def _parse_register_calls(java_root: Path, token: str) -> Set[str]:
    """Extract from calls like `TOKEN.register("name", ...)` in all java files."""
    names: Set[str] = set()
    pattern = re.compile(rf"\b{re.escape(token)}\s*\.\s*register\s*\(\s*\"([^\"]+)\"")
    for file in java_root.rglob("*.java"):
        try:
            text = _read(file)
        except Exception:
            continue
        names.update(pattern.findall(text))
    return names


def _parse_call_first_string(path: Path, call_names: Sequence[str]) -> Set[str]:
    """Extract first string arg for listed helper calls in a file."""
    if not _exists(path):
        return set()
    text = _read(path)
    joined = "|".join(re.escape(c) for c in call_names)
    pattern = re.compile(rf"\b(?:{joined})\s*\(\s*\"([^\"]+)\"")
    return set(pattern.findall(text))


def _parse_extended_fluid_names(path: Path) -> Set[str]:
    if not _exists(path):
        return set()
    text = _read(path)
    return set(re.findall(r"\bnew\s+ExtendedFluidType\s*\(\s*\"([^\"]+)\"", text))


def collect_old_sets(old_root: Path) -> AuditSets:
    old_java = old_root / OLD_JAVA_REL

    old_items = _parse_public_statics(old_java / "com/hbm/items/ModItems.java", "Item")

    old_blocks = _parse_public_statics(old_java / "com/hbm/blocks/ModBlocks.java", "Block")

    # Legacy fluids are static FluidType fields declared in Fluids.java
    old_fluids_raw = _parse_public_statics(old_java / "com/hbm/inventory/fluid/Fluids.java", "FluidType")
    old_fluids = {name.lower() for name in old_fluids_raw}

    return AuditSets(items=old_items, blocks=old_blocks, fluids=old_fluids)


def collect_new_sets(new_root: Path) -> AuditSets:
    new_java = new_root / NEW_JAVA_REL

    new_items: Set[str] = set()
    new_blocks: Set[str] = set()
    new_fluids: Set[str] = set()

    # General register patterns
    new_items.update(_parse_register_calls(new_java, "ITEMS"))
    new_blocks.update(_parse_register_calls(new_java, "BLOCKS"))
    new_fluids.update(_parse_register_calls(new_java, "FLUIDS"))
    new_fluids.update(_parse_register_calls(new_java, "FLUID_TYPES"))

    # New registry helpers
    new_items_path = new_java / "com/hbm/registries/ModItems.java"
    new_items.update(
        _parse_call_first_string(
            new_items_path,
            (
                "parts",
                "machine",
                "missile",
                "gun",
                "consumable",
                "template",
                "control",
                "add",
                "nuke",
                "weapon",
            ),
        )
    )

    new_blocks_path = new_java / "com/hbm/registries/ModBlocks.java"
    new_blocks.update(
        _parse_call_first_string(
            new_blocks_path,
            (
                "registerBlockWithItem",
                "registerBattery",
                "add",
                "block",
                "machine",
            ),
        )
    )
    if _exists(new_blocks_path):
        text = _read(new_blocks_path)
        new_blocks.update(re.findall(r"\bnew\s+BlockBuilder\s*\(\s*\"([^\"]+)\"", text))

    # Fluids are mostly defined in ModFluids via ExtendedFluidType("name", ...)
    mod_fluids = new_java / "com/hbm/Inventory/fluid/ModFluids.java"
    new_fluids.update(_parse_extended_fluid_names(mod_fluids))

    return AuditSets(items=new_items, blocks=new_blocks, fluids=new_fluids)


FLUID_ALIASES: Dict[str, str] = {
    "hotsteam": "hot_steam",
    "superhotsteam": "superhot_steam",
    "spentsteam": "spent_steam",
    "carbondioxide": "carbon_dioxide",
    "crackoil": "crack_oil",
    "woodoil": "wood_oil",
    "heatingoil": "heating_oil",
    "reformgas": "reform_gas",
}


def canonical_key(name: str) -> str:
    lowered = name.lower().replace(".", "_").replace("-", "_")
    lowered = FLUID_ALIASES.get(lowered, lowered)
    tokens = [tok for tok in lowered.split("_") if tok]
    return " ".join(sorted(tokens))


def match_sets(old_set: Set[str], new_set: Set[str]) -> Tuple[Dict[str, Dict[str, object]], Set[str], Set[str]]:
    """Return per-old mapping + missing(old-only) + extras(new-only)."""
    canonical_to_new: Dict[str, Set[str]] = {}
    for n in new_set:
        canonical_to_new.setdefault(canonical_key(n), set()).add(n)

    report: Dict[str, Dict[str, object]] = {}
    matched_new: Set[str] = set()
    missing_old: Set[str] = set()

    for old_name in sorted(old_set):
        mapped = None
        if old_name in new_set:
            mapped = old_name
        else:
            candidates = canonical_to_new.get(canonical_key(old_name), set())
            if len(candidates) == 1:
                mapped = next(iter(candidates))

        if mapped is None:
            missing_old.add(old_name)
        else:
            matched_new.add(mapped)

        report[old_name] = {
            "old": True,
            "new": mapped is not None,
            "mapped_to": mapped,
        }

    extra_new = set(new_set) - matched_new
    return report, missing_old, extra_new


def write_reports(root: Path, old_sets: AuditSets, new_sets: AuditSets) -> tuple[Path, dict, dict, dict]:
    out_dir = root / "reports"
    out_dir.mkdir(parents=True, exist_ok=True)

    datasets = {
        "item": (old_sets.items, new_sets.items),
        "block": (old_sets.blocks, new_sets.blocks),
        "fluid": (old_sets.fluids, new_sets.fluids),
    }

    migration_report: Dict[str, Dict[str, Dict[str, object]]] = {}
    missing: Dict[str, List[str]] = {}
    new_only: Dict[str, List[str]] = {}
    summary: Dict[str, Dict[str, int]] = {}

    for kind, (old_set, new_set) in datasets.items():
        report, missing_old, extra_new = match_sets(old_set, new_set)
        migration_report[kind] = report
        missing[kind] = sorted(missing_old)
        new_only[kind] = sorted(extra_new)
        ported_count = sum(1 for entry in report.values() if bool(entry["new"]))
        summary[kind] = {
            "total": len(old_set),
            "ported": ported_count,
            "missing": len(old_set) - ported_count,
            "new_only": len(extra_new),
        }

    (out_dir / "migration_report.json").write_text(
        json.dumps(migration_report, indent=2, sort_keys=True, ensure_ascii=False),
        encoding="utf-8",
    )
    (out_dir / "migration_summary.json").write_text(
        json.dumps(summary, indent=2, sort_keys=True, ensure_ascii=False),
        encoding="utf-8",
    )
    (out_dir / "migration_missing.json").write_text(
        json.dumps(missing, indent=2, sort_keys=True, ensure_ascii=False),
        encoding="utf-8",
    )
    (out_dir / "migration_new_only.json").write_text(
        json.dumps(new_only, indent=2, sort_keys=True, ensure_ascii=False),
        encoding="utf-8",
    )
    return out_dir, summary, missing, new_only


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Audit migration progress from legacy HBM to current port")
    parser.add_argument(
        "--legacy-root",
        type=Path,
        default=DEFAULT_OLD_ROOT,
        help=f"Legacy repo root (default: {DEFAULT_OLD_ROOT})",
    )
    parser.add_argument(
        "--project-root",
        type=Path,
        default=ROOT,
        help=f"Current project root (default: {ROOT})",
    )
    return parser.parse_args()


def resolve_legacy_root(user_path: Path) -> Path:
    candidate = user_path.expanduser().resolve()
    if candidate.exists():
        return candidate
    if user_path == DEFAULT_OLD_ROOT and FALLBACK_OLD_ROOT.exists():
        return FALLBACK_OLD_ROOT.resolve()
    return candidate


def main() -> None:
    args = parse_args()
    legacy_root = resolve_legacy_root(args.legacy_root)
    project_root = args.project_root.expanduser().resolve()

    if not legacy_root.exists():
        raise SystemExit(f"Legacy root does not exist: {legacy_root}")

    old_sets = collect_old_sets(legacy_root)
    new_sets = collect_new_sets(project_root)
    out_dir, summary, missing, new_only = write_reports(project_root, old_sets, new_sets)

    print(f"Audit finished. Reports: {out_dir}")
    for kind in ("item", "block", "fluid"):
        data = summary[kind]
        print(
            f"{kind}: total={data['total']}, ported={data['ported']}, "
            f"missing={data['missing']}, new_only={data['new_only']}"
        )
        if missing.get(kind):
            preview = ", ".join(missing[kind][:8])
            print(f"  missing sample: {preview}")
        if new_only.get(kind):
            preview = ", ".join(new_only[kind][:5])
            print(f"  new-only sample: {preview}")


if __name__ == "__main__":
    main()
