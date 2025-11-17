#!/usr/bin/env python3
import json
import re
from pathlib import Path

PROJECT_ROOT = Path(__file__).resolve().parents[1]
ASSETS_MAIN = PROJECT_ROOT / 'src' / 'main' / 'resources' / 'assets' / 'hbm'
BLOCKSTATE_DIR = ASSETS_MAIN / 'blockstates'
MODEL_BLOCK_DIR = ASSETS_MAIN / 'models' / 'block'
TEXTURES_DIR = ASSETS_MAIN / 'textures'
LOG_PATH = PROJECT_ROOT / 'run' / 'logs' / 'latest.log'
DEFAULT_TEXTURE = 'block/env/ore_random_1'

ORE_TEXTURES = {
    'ore_scorched_uranium': 'block/env/ore_uranium',
    'ore_titanium': 'block/env/ore_random_1',
    'ore_thorium': 'block/env/ore_random_2',
    'ore_niter': 'block/env/ore_random_3',
    'ore_tungsten': 'block/env/ore_random_4',
    'ore_aluminium': 'block/env/ore_random_5',
    'ore_fluorite': 'block/env/ore_random_6',
    'ore_lead': 'block/env/ore_random_7',
    'ore_beryllium': 'block/env/ore_random_8',
    'ore_oil': 'block/env/ore_random_9',
    'oil_ore_empty': 'block/env/ore_random_9',
    'oil_ore_sand': 'block/env/ore_random_10',
    'ore_cobalt': 'block/env/ore_random_4',
    'ore_coltan': 'block/env/ore_random_5',
    'ore_geniss_gas': 'block/env/ore_random_6',
    'smolder_ore_nether': 'block/env/ore_random_7',
    'plutonium_ore_nether': 'block/env/ore_random_8',
    'fire_ore_nether': 'block/env/ore_random_3',
    'tikite_ore_end': 'block/env/ore_random_2'
}

BOMB_MODELS = {
    'bomb_boy': 'hbm:block/bomb/boy',
    'bomb_custom': 'hbm:block/bomb/custom',
    'bomb_fat_man': 'hbm:block/bomb/fat_man'
}


def load_missing_from_log():
    missing = {}
    if LOG_PATH.exists():
        text = LOG_PATH.read_text(errors='ignore')
        pattern = re.compile(r"hbm:blockstates/([\w/]+\.json)' missing model for variant: 'hbm:([^#]+)#([^\n']*)")
        for blockstate, block, variant in pattern.findall(text):
            name = blockstate.rsplit('/', 1)[-1][:-5]
            missing.setdefault(name, set()).add(variant)
    return missing


def write_json(path: Path, data: dict):
    path.parent.mkdir(parents=True, exist_ok=True)
    text = json.dumps(data, indent=2) + '\n'
    path.write_text(text)


def ensure_cube_block(name: str, texture: str):
    texture_path = TEXTURES_DIR / (texture + '.png')
    resolved_texture = texture if texture_path.exists() else DEFAULT_TEXTURE
    if resolved_texture != texture:
        print(f'[warn] Texture {texture}.png missing, using {DEFAULT_TEXTURE} for {name}')
    model_path = MODEL_BLOCK_DIR / f'{name}.json'
    if not model_path.exists():
        write_json(model_path, {
            'parent': 'block/cube_all',
            'textures': {'all': f'hbm:{resolved_texture}'}
        })
    state_path = BLOCKSTATE_DIR / f'{name}.json'
    if not state_path.exists():
        write_json(state_path, {
            'variants': {
                '': {'model': f'hbm:block/{name}'}
            }
        })


def ensure_fluid_block(name: str):
    model_path = MODEL_BLOCK_DIR / f'{name}.json'
    if not model_path.exists():
        write_json(model_path, {
            'loader': 'forge:fluid',
            'custom': {'fluid': f'hbm:{name}'}
        })
    state_path = BLOCKSTATE_DIR / f'{name}.json'
    if not state_path.exists():
        variants = {f'level={i}': {'model': f'hbm:block/{name}'} for i in range(16)}
        write_json(state_path, {'variants': variants})


def ensure_bomb_block(name: str, parent: str):
    model_path = MODEL_BLOCK_DIR / f'{name}.json'
    if not model_path.exists():
        write_json(model_path, {'parent': parent})
    rotations = {'north': 0, 'east': 90, 'south': 180, 'west': 270}
    variants = {}
    for facing, rot in rotations.items():
        for is_core in (False, True):
            key = f'facing={facing},is_core={str(is_core).lower()}'
            entry = {'model': f'hbm:block/{name}'}
            if rot:
                entry['y'] = rot
            variants[key] = entry
    state_path = BLOCKSTATE_DIR / f'{name}.json'
    if not state_path.exists():
        write_json(state_path, {'variants': variants})


def ensure_dummible():
    model_path = MODEL_BLOCK_DIR / 'dummible.json'
    if not model_path.exists():
        write_json(model_path, {'elements': []})
    state_path = BLOCKSTATE_DIR / 'dummible.json'
    if not state_path.exists():
        write_json(state_path, {
            'variants': {
                '': {'model': 'hbm:block/dummible'}
            }
        })


def main():
    missing = load_missing_from_log()
    if not missing:
        print('No missing block entries detected in logs; nothing to do.')
        return
    for name, variants in missing.items():
        if name in BOMB_MODELS:
            ensure_bomb_block(name, BOMB_MODELS[name])
        elif name == 'dummible':
            ensure_dummible()
        elif len(variants) == 16:
            ensure_fluid_block(name)
        else:
            texture = ORE_TEXTURES.get(name)
            if texture is None:
                # default to matching texture name under block root
                texture = f'block/{name}'
            ensure_cube_block(name, texture)
    print(f'Processed {len(missing)} block definitions from run log.')

if __name__ == '__main__':
    main()
