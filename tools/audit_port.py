#!/usr/bin/env python3
"""Audit HBM-Modernized registries vs current project."""
from __future__ import annotations

import json
import re
from collections import defaultdict
from pathlib import Path
from typing import Dict, List, Set, Tuple

ROOT = Path(__file__).resolve().parents[1]
OLD_JAVA = ROOT / 'tools' / 'HBM-Modernized' / 'src' / 'main' / 'java'
NEW_JAVA = ROOT / 'src' / 'main' / 'java'


def read(path: Path) -> str:
    return path.read_text(encoding='utf-8')


def gather_literals(java_root: Path, token: str) -> Set[str]:
    pattern = re.compile(rf'{re.escape(token)}\.register\("([^"]+)"')
    names: Set[str] = set()
    for file in java_root.rglob('*.java'):
        text = read(file)
        names.update(pattern.findall(text))
    return names


def parse_enum_names(path: Path) -> List[str]:
    text = read(path)
    pattern = re.compile(r'\b[A-Z0-9_]+\("([^"]+)"', re.MULTILINE)
    return pattern.findall(text)


def parse_set(text: str, var_name: str) -> List[str]:
    pattern = re.compile(rf'{re.escape(var_name)}\s*=\s*Set\.of\((.*?)\);', re.S)
    match = pattern.search(text)
    if not match:
        return []
    return re.findall(r'"([^"]+)"', match.group(1))


def old_items() -> Set[str]:
    names = gather_literals(OLD_JAVA, 'ITEMS')
    mod_items = OLD_JAVA / 'com' / 'hbm_m' / 'item' / 'ModItems.java'
    mod_ingots = OLD_JAVA / 'com' / 'hbm_m' / 'item' / 'tags_and_tiers' / 'ModIngots.java'
    mod_powders = OLD_JAVA / 'com' / 'hbm_m' / 'item' / 'tags_and_tiers' / 'ModPowders.java'
    text = read(mod_items)
    ingot_names = parse_enum_names(mod_ingots)
    powder_names = parse_enum_names(mod_powders)

    names.update(f"{name}_ingot" for name in ingot_names)

    enabled_modpowders = set(parse_set(text, 'ENABLED_MODPOWDERS'))
    enabled_ingot_powders = set(parse_set(text, 'ENABLED_INGOT_POWDERS'))
    powder_tiny = set(parse_set(text, 'POWDER_TINY_NAMES'))
    enabled_tiny = set(parse_set(text, 'ENABLED_TINY_POWDERS'))

    for name in powder_names:
        lname = name.lower()
        if lname in enabled_modpowders:
            names.add(f"{lname}_powder")
    for name in ingot_names:
        lname = name.lower()
        if lname in enabled_ingot_powders:
            names.add(f"{lname}_powder")
        if lname in powder_tiny and lname in enabled_tiny:
            names.add(f"{lname}_powder_tiny")
    return names


def old_blocks() -> Set[str]:
    names = gather_literals(OLD_JAVA, 'BLOCKS')
    mod_blocks = OLD_JAVA / 'com' / 'hbm_m' / 'block' / 'ModBlocks.java'
    text = read(mod_blocks)
    enabled_blocks = set(parse_set(text, 'ENABLED_INGOT_BLOCKS'))
    names.update(f"block_{name}" for name in enabled_blocks)
    helper_pattern = re.compile(r'(?:registerBlock|registerBlockWithoutItem)\("([^"]+)"')
    names.update(helper_pattern.findall(text))
    return names


def old_fluids() -> Set[str]:
    names = gather_literals(OLD_JAVA, 'FLUIDS')
    ft = gather_literals(OLD_JAVA, 'FLUID_TYPES')
    names.update(ft)
    return names


def hbmitems_builder_names() -> Set[str]:
    path = NEW_JAVA / 'com' / 'hbm' / 'item' / 'HBMItems.java'
    text = read(path)
    pattern = re.compile(r'=\s*(?:new\s+ItemBuilder|parts|machine|missile|gun|consumable|template|control|add)\(\s*"([^"]+)"', re.S)
    return set(pattern.findall(text))


def hbmcomponent_register_names(file: Path) -> Set[str]:
    text = read(file)
    pattern = re.compile(r'register\((?:matherialList|partList|itemList|standaloneModels),\s*"([^"]+)"')
    return set(pattern.findall(text))


def new_items() -> Set[str]:
    names = gather_literals(NEW_JAVA, 'ITEMS')
    names.update(hbmitems_builder_names())
    component = NEW_JAVA / 'com' / 'hbm' / 'item' / 'HBMComponent.java'
    combat = NEW_JAVA / 'com' / 'hbm' / 'item' / 'HBMCombat.java'
    names.update(hbmcomponent_register_names(component))
    names.update(hbmcomponent_register_names(combat))
    return names


def new_blocks() -> Set[str]:
    names = gather_literals(NEW_JAVA, 'BLOCKS')
    # capture helper usage in ModBlocks/HBMMachine
    helpers = {
        NEW_JAVA / 'com' / 'hbm' / 'registries' / 'ModBlocks.java',
        NEW_JAVA / 'com' / 'hbm' / 'block' / 'HBMMachine.java',
        NEW_JAVA / 'com' / 'hbm' / 'block' / 'HBMBlockComponent.java',
    }
    primary_pattern = re.compile(r'(?:registerBlockWithItem|registerBattery)\([^,]+,\s*"([^"]+)"')
    secondary_pattern = re.compile(r'(?:add|block|machine)\(\s*"([^"]+)"')
    for file in helpers:
        text = read(file)
        names.update(primary_pattern.findall(text))
        names.update(secondary_pattern.findall(text))
    return names


def new_fluids() -> Set[str]:
    names = gather_literals(NEW_JAVA, 'FLUIDS')
    names.update(gather_literals(NEW_JAVA, 'FLUID_TYPES'))
    mod_fluids = NEW_JAVA / 'com' / 'hbm' / 'Inventory' / 'fluid' / 'ModFluids.java'
    if mod_fluids.exists():
        text = read(mod_fluids)
        for fname in re.findall(r'ExtendedFluidType\(\s*"([^"]+)"', text):
            names.add(fname)
            names.add(f'{fname}_flow')
    return names


def canonical_key(name: str) -> str | None:
    ignore = {'source'}
    replacements = {'flowing': 'flow'}
    tokens: List[str] = []
    for token in name.lower().split('_'):
        if not token or token in ignore:
            continue
        token = replacements.get(token, token)
        tokens.append(token)
    if not tokens:
        return None
    return ' '.join(sorted(tokens))


def match_old_to_new(old_set: Set[str], new_set: Set[str]) -> tuple[Dict[str, Dict[str, str | bool]], Set[str]]:
    canonical_map: Dict[str, Set[str]] = defaultdict(set)
    for new_name in new_set:
        key = canonical_key(new_name)
        if key:
            canonical_map[key].add(new_name)
    unmatched_new = set(new_set)
    report: Dict[str, Dict[str, str | bool]] = {}
    for old_name in sorted(old_set):
        match: str | None = None
        if old_name in new_set:
            match = old_name
        else:
            key = canonical_key(old_name)
            if key:
                candidates = canonical_map.get(key, set())
                if len(candidates) == 1:
                    match = next(iter(candidates))
        if match:
            unmatched_new.discard(match)
        report[old_name] = {
            'old': True,
            'new': bool(match),
            'mapped_to': match,
        }
    return report, unmatched_new


def build_report() -> Dict[str, Dict[str, Dict[str, str | bool]]]:
    report: Dict[str, Dict[str, Dict[str, str | bool]]] = {}
    extras: Dict[str, List[str]] = {}
    datasets = [
        ('item', old_items(), new_items()),
        ('block', old_blocks(), new_blocks()),
        ('fluid', old_fluids(), new_fluids()),
    ]
    for kind, old_set, new_set in datasets:
        entries, unmatched_new = match_old_to_new(old_set, new_set)
        report[kind] = entries
        extras[kind] = sorted(unmatched_new)
    out_dir = ROOT / 'reports'
    out_dir.mkdir(parents=True, exist_ok=True)
    (out_dir / 'migration_new_only.json').write_text(json.dumps(extras, indent=2, sort_keys=True), encoding='utf-8')
    return report


def main() -> None:
    report = build_report()
    out_dir = ROOT / 'reports'
    summary = {
        kind: {
            'total': len(entries),
            'ported': sum(1 for data in entries.values() if data['new']),
        }
        for kind, entries in report.items()
    }
    (out_dir / 'migration_report.json').write_text(json.dumps(report, indent=2, sort_keys=True), encoding='utf-8')
    (out_dir / 'migration_summary.json').write_text(json.dumps(summary, indent=2, sort_keys=True), encoding='utf-8')


if __name__ == '__main__':
    main()
