#!/usr/bin/env python3
"""
Utility script that normalizes HBM asset paths so every item/block model and
blockstate points at lowercase textures using the correct namespace layout.

Steps performed:
1. Rename any uppercase texture files under assets/hbm/textures to lowercase.
2. Re-write all model and blockstate JSON files under src/**/resources/assets/hbm
   so that parent/model references drop legacy namespaces and texture paths use
   hbm:item/* or hbm:block/* with lowercase segments.
3. Ensure every bucket_* item registered via ModFluids has a generated model.
"""

from __future__ import annotations

import json
import re
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, List, Tuple

PROJECT_ROOT = Path(__file__).resolve().parents[1]
RESOURCE_ROOTS = [
    PROJECT_ROOT / "src" / "main" / "resources",
    PROJECT_ROOT / "src" / "generated" / "resources",
]
ASSET_SUBPATH = Path("assets") / "hbm"
TEXTURE_ROOT = PROJECT_ROOT / "src" / "main" / "resources" / ASSET_SUBPATH / "textures"
GENERATED_ITEM_MODEL_ROOT = PROJECT_ROOT / "src" / "generated" / "resources" / ASSET_SUBPATH / "models" / "item"
MOD_FLUIDS_SRC = PROJECT_ROOT / "src" / "main" / "java" / "com" / "hbm" / "Inventory" / "fluid" / "ModFluids.java"


@dataclass
class NormalizeStats:
    files_seen: int = 0
    files_changed: int = 0

    def bump(self, changed: bool) -> None:
        self.files_seen += 1
        if changed:
            self.files_changed += 1


def rename_texture_files() -> List[Tuple[Path, Path]]:
    if not TEXTURE_ROOT.exists():
        return []
    renames: List[Tuple[Path, Path]] = []
    for file in sorted(TEXTURE_ROOT.rglob("*")):
        if not file.is_file():
            continue
        name_lower = file.name.lower()
        if not (file.suffix.lower() == ".png" or name_lower.endswith(".png.mcmeta")):
            continue
        rel = file.relative_to(TEXTURE_ROOT)
        lowered_parts = [part.lower() for part in rel.parts]
        target = TEXTURE_ROOT.joinpath(*lowered_parts)
        if target == file:
            continue
        target.parent.mkdir(parents=True, exist_ok=True)
        if target.exists():
            if target.samefile(file):
                tmp = target.with_name(target.name + ".__tmp__")
                file.rename(tmp)
                tmp.rename(target)
            else:
                raise RuntimeError(f"Refusing to rename {file} to existing {target}")
        else:
            file.rename(target)
        renames.append((file, target))
    return renames


def load_json(path: Path) -> Dict:
    return json.loads(path.read_text())


def dump_json(path: Path, data: Dict) -> None:
    text = json.dumps(data, indent=2, ensure_ascii=False)
    path.write_text(text + "\n")


def normalize_resource_string(value: str) -> Tuple[str, bool]:
    original = value
    if not isinstance(value, str):
        return value, False
    if value.startswith("#"):
        return value, False
    namespace = None
    path = value
    if ":" in value:
        namespace, path = value.split(":", 1)
    if namespace is None:
        return value, False
    if namespace != "hbm":
        return value, False
    path = path.replace("\\", "/")
    if path.startswith("textures/"):
        path = path[len("textures/") :]
    path = path.replace("items/", "item/")
    path = path.replace("blocks/", "block/")
    path = re.sub(r"/+", "/", path)
    path = path.lower()
    normalized = f"{namespace}:{path}"
    return normalized, normalized != original


def normalize_parent(data: Dict) -> bool:
    parent = data.get("parent")
    if not isinstance(parent, str):
        return False
    new_parent = parent
    if parent.startswith("minecraft:item/"):
        new_parent = parent.replace("minecraft:item/", "item/", 1)
    elif parent.startswith("minecraft:block/"):
        new_parent = parent.replace("minecraft:block/", "block/", 1)
    elif parent.startswith("minecraft:"):
        new_parent = parent.replace("minecraft:", "", 1)
    elif parent.startswith("hbm:"):
        new_parent, ch = normalize_resource_string(parent)
        if ch:
            data["parent"] = new_parent
            return True
        return False
    if new_parent != parent:
        data["parent"] = new_parent
        return True
    return False


def normalize_textures(node: Dict) -> bool:
    changed = False
    if not isinstance(node, dict):
        return False
    textures = node.get("textures")
    if isinstance(textures, dict):
        for key, value in list(textures.items()):
            if not isinstance(value, str):
                continue
            normalized, did_change = normalize_resource_string(value)
            if did_change:
                textures[key] = normalized
                changed = True
    for value in node.values():
        if isinstance(value, dict):
            if normalize_textures(value):
                changed = True
        elif isinstance(value, list):
            for entry in value:
                if isinstance(entry, dict) and normalize_textures(entry):
                    changed = True
    return changed


def normalize_models(path: Path) -> NormalizeStats:
    stats = NormalizeStats()
    if not path.exists():
        return stats
    for json_file in sorted(path.rglob("*.json")):
        data = load_json(json_file)
        changed = normalize_parent(data)
        changed |= normalize_textures(data)
        if changed:
            dump_json(json_file, data)
        stats.bump(changed)
    return stats


def normalize_blockstates(path: Path) -> NormalizeStats:
    stats = NormalizeStats()
    if not path.exists():
        return stats
    for json_file in sorted(path.rglob("*.json")):
        data = load_json(json_file)
        changed = normalize_blockstate_models(data)
        if changed:
            dump_json(json_file, data)
        stats.bump(changed)
    return stats


def normalize_blockstate_models(node) -> bool:
    changed = False
    if isinstance(node, dict):
        for key, value in node.items():
            if key == "model" and isinstance(value, str):
                normalized, did_change = normalize_resource_string(value)
                if did_change:
                    node[key] = normalized
                    changed = True
            else:
                if normalize_blockstate_models(value):
                    changed = True
    elif isinstance(node, list):
        for entry in node:
            if normalize_blockstate_models(entry):
                changed = True
    return changed


def ensure_bucket_models() -> List[Path]:
    created: List[Path] = []
    if not MOD_FLUIDS_SRC.exists():
        return created
    pattern = re.compile(r"register\\((\\w+)\\)")
    content = MOD_FLUIDS_SRC.read_text()
    fluid_ids = sorted({match for match in pattern.findall(content) if match.islower()})
    if not fluid_ids:
        return created
    GENERATED_ITEM_MODEL_ROOT.mkdir(parents=True, exist_ok=True)
    for fluid in fluid_ids:
        bucket_model = GENERATED_ITEM_MODEL_ROOT / f"bucket_{fluid}.json"
        if bucket_model.exists():
            continue
        dump_json(bucket_model, {"parent": "hbm:item/fluid_bucket"})
        created.append(bucket_model)
    return created


def run_normalization() -> None:
    texture_changes = rename_texture_files()
    item_stats = NormalizeStats()
    block_stats = NormalizeStats()
    blockstate_stats = NormalizeStats()
    for resources_root in RESOURCE_ROOTS:
        assets_root = resources_root / ASSET_SUBPATH
        if not assets_root.exists():
            continue
        item_dir = assets_root / "models" / "item"
        block_dir = assets_root / "models" / "block"
        blockstate_dir = assets_root / "blockstates"
        stats = normalize_models(item_dir)
        item_stats.files_seen += stats.files_seen
        item_stats.files_changed += stats.files_changed
        stats = normalize_models(block_dir)
        block_stats.files_seen += stats.files_seen
        block_stats.files_changed += stats.files_changed
        stats = normalize_blockstates(blockstate_dir)
        blockstate_stats.files_seen += stats.files_seen
        blockstate_stats.files_changed += stats.files_changed
    bucket_files = ensure_bucket_models()
    print(f"Texture files renamed: {len(texture_changes)}")
    print(f"Item models processed: {item_stats.files_changed}/{item_stats.files_seen}")
    print(f"Block models processed: {block_stats.files_changed}/{block_stats.files_seen}")
    print(f"Blockstates processed: {blockstate_stats.files_changed}/{blockstate_stats.files_seen}")
    print(f"New bucket models created: {len(bucket_files)}")
    if bucket_files:
        for created in bucket_files:
            print(f"  + {created.relative_to(PROJECT_ROOT)}")


if __name__ == "__main__":
    run_normalization()
