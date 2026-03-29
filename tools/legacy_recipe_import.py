import json
import os
import re
from pathlib import Path
from collections import defaultdict

ROOT = Path(__file__).resolve().parents[1]
OLD_ROOT = ROOT / "Hbm-s-Nuclear-Tech-GIT"
OUT_DIR = ROOT / "src/generated/resources/data/hbm/recipes/legacy"
REPORT_PATH = ROOT / "reports/legacy_recipe_import_report.json"

# ------------------------
# Utility parsing helpers
# ------------------------

def split_top_level(s, delimiter=","):
    parts = []
    buf = []
    depth_paren = depth_brace = depth_bracket = 0
    in_str = False
    in_char = False
    in_line_comment = False
    in_block_comment = False
    escape = False
    i = 0
    while i < len(s):
        ch = s[i]
        nxt = s[i + 1] if i + 1 < len(s) else ""

        if in_line_comment:
            if ch == "\n":
                in_line_comment = False
            buf.append(ch)
            i += 1
            continue
        if in_block_comment:
            if ch == "*" and nxt == "/":
                in_block_comment = False
                buf.append(ch)
                buf.append(nxt)
                i += 2
                continue
            buf.append(ch)
            i += 1
            continue

        if in_str:
            buf.append(ch)
            if escape:
                escape = False
            elif ch == "\\":
                escape = True
            elif ch == '"':
                in_str = False
            i += 1
            continue

        if in_char:
            buf.append(ch)
            if escape:
                escape = False
            elif ch == "\\":
                escape = True
            elif ch == "'":
                in_char = False
            i += 1
            continue

        # start comment?
        if ch == "/" and nxt == "/":
            in_line_comment = True
            buf.append(ch)
            buf.append(nxt)
            i += 2
            continue
        if ch == "/" and nxt == "*":
            in_block_comment = True
            buf.append(ch)
            buf.append(nxt)
            i += 2
            continue

        if ch == '"':
            in_str = True
            buf.append(ch)
            i += 1
            continue
        if ch == "'":
            in_char = True
            buf.append(ch)
            i += 1
            continue

        if ch == "(":
            depth_paren += 1
        elif ch == ")":
            depth_paren -= 1
        elif ch == "{":
            depth_brace += 1
        elif ch == "}":
            depth_brace -= 1
        elif ch == "[":
            depth_bracket += 1
        elif ch == "]":
            depth_bracket -= 1

        if ch == delimiter and depth_paren == 0 and depth_brace == 0 and depth_bracket == 0:
            part = "".join(buf).strip()
            if part:
                parts.append(part)
            buf = []
        else:
            buf.append(ch)
        i += 1

    tail = "".join(buf).strip()
    if tail:
        parts.append(tail)
    return parts


def strip_outer_parens(s):
    s = s.strip()
    if s.startswith("(") and s.endswith(")"):
        # naive; only strip one layer if balanced
        return s[1:-1].strip()
    return s


def find_matching(code, start_idx, open_char="(", close_char=")"):
    depth = 0
    in_str = False
    in_char = False
    in_line_comment = False
    in_block_comment = False
    escape = False
    i = start_idx
    while i < len(code):
        ch = code[i]
        nxt = code[i + 1] if i + 1 < len(code) else ""

        if in_line_comment:
            if ch == "\n":
                in_line_comment = False
            i += 1
            continue
        if in_block_comment:
            if ch == "*" and nxt == "/":
                in_block_comment = False
                i += 2
                continue
            i += 1
            continue

        if in_str:
            if escape:
                escape = False
            elif ch == "\\":
                escape = True
            elif ch == '"':
                in_str = False
            i += 1
            continue
        if in_char:
            if escape:
                escape = False
            elif ch == "\\":
                escape = True
            elif ch == "'":
                in_char = False
            i += 1
            continue

        if ch == "/" and nxt == "/":
            in_line_comment = True
            i += 2
            continue
        if ch == "/" and nxt == "*":
            in_block_comment = True
            i += 2
            continue

        if ch == '"':
            in_str = True
            i += 1
            continue
        if ch == "'":
            in_char = True
            i += 1
            continue

        if ch == open_char:
            depth += 1
        elif ch == close_char:
            depth -= 1
            if depth == 0:
                return i
        i += 1
    return -1


def extract_calls(code, name):
    calls = []
    idx = 0
    while True:
        idx = code.find(name, idx)
        if idx == -1:
            break
        # Ensure it's a call (next non-space is '(')
        j = idx + len(name)
        while j < len(code) and code[j].isspace():
            j += 1
        if j >= len(code) or code[j] != "(":
            idx = j
            continue
        end = find_matching(code, j, "(", ")")
        if end == -1:
            idx = j + 1
            continue
        args_str = code[j + 1:end]
        # Skip method definitions
        if "ItemStack result" in args_str or "Object..." in args_str:
            idx = end + 1
            continue
        calls.append(args_str)
        idx = end + 1
    return calls


def snake_case(name):
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', name)
    s2 = re.sub('([a-z0-9])([A-Z])', r'\1_\2', s1)
    return s2.replace("__", "_").lower()


# ------------------------
# Load mappings
# ------------------------

def load_oredict_defs():
    path = OLD_ROOT / "src/main/java/com/hbm/inventory/OreDictManager.java"
    if not path.exists():
        return {}, {}, {}
    text = path.read_text(encoding="utf-8", errors="ignore")
    dict_frames = {}
    dict_groups = {}
    key_strings = {}

    # DictFrame defs
    for m in re.finditer(r"public\s+static\s+final\s+DictFrame\s+(\w+)\s*=\s*new\s+DictFrame\(([^;]+)\);", text):
        ident = m.group(1)
        args = m.group(2)
        str_lits = re.findall(r'"([^"]+)"', args)
        if str_lits:
            dict_frames[ident] = str_lits[0]

    # DictGroup defs (use first referenced DictFrame name or first string literal)
    for m in re.finditer(r"public\s+static\s+final\s+DictGroup\s+(\w+)\s*=\s*new\s+DictGroup\(([^;]+)\);", text):
        ident = m.group(1)
        args = m.group(2)
        refs = re.findall(r"\b([A-Z0-9_]+)\b", args)
        for ref in refs:
            if ref in dict_frames:
                dict_groups[ident] = dict_frames[ref]
                break
        if ident not in dict_groups:
            str_lits = re.findall(r'\"([^\"]+)\"', args)
            if str_lits:
                dict_groups[ident] = str_lits[0]

    # KEY_ string defs
    for m in re.finditer(r"public\s+static\s+final\s+String\s+(KEY_\w+)\s*=\s*\"([^\"]+)\";", text):
        key_strings[m.group(1)] = m.group(2)

    return dict_frames, dict_groups, key_strings


def collect_known_ids():
    ids = set()
    # legacy items
    legacy_path = ROOT / "src/main/resources/data/hbm/legacy_items.json"
    if legacy_path.exists():
        data = json.loads(legacy_path.read_text(encoding="utf-8"))
        if isinstance(data, dict):
            ids.update(data.keys())

    # item models (main + generated)
    for base in [ROOT / "src/main/resources/assets/hbm/models/item", ROOT / "src/generated/resources/assets/hbm/models/item"]:
        if base.exists():
            for p in base.glob("*.json"):
                ids.add(p.stem)

    # textures (fallback)
    tex_base = ROOT / "src/main/resources/assets/hbm/textures/item"
    if tex_base.exists():
        for p in tex_base.glob("*.png"):
            ids.add(p.stem)

    # parse registration names from java
    java_root = ROOT / "src/main/java"
    patterns = [
        re.compile(r"\bITEMS\.register\(\s*\"([^\"]+)\""),
        re.compile(r"new\s+WrapperRegistry\.ItemBuilder\(\s*\"([^\"]+)\""),
        re.compile(r"new\s+ItemBuilder\(\s*\"([^\"]+)\""),
        re.compile(r"\bregister\(\s*[^,]+,\s*\"([^\"]+)\""),
    ]
    for path in java_root.rglob("*.java"):
        text = path.read_text(encoding="utf-8", errors="ignore")
        for pat in patterns:
            for m in pat.finditer(text):
                ids.add(m.group(1))

    return ids


DICT_FRAMES, DICT_GROUPS, KEY_STRINGS = load_oredict_defs()
KNOWN_IDS = collect_known_ids()


# ------------------------
# Expression resolution
# ------------------------

ITEM_RENAMES = {
    "reeds": "sugar_cane",
    "potato": "potato",
    "carrot": "carrot",
    "poisonous_potato": "poisonous_potato",
    "wheat": "wheat",
    "wheat_seeds": "wheat_seeds",
    "seeds": "wheat_seeds",
    "slime_ball": "slime_ball",
    "slime": "slime_ball",
    "redstone": "redstone",
    "glowstone_dust": "glowstone_dust",
    "iron_ingot": "iron_ingot",
    "gold_ingot": "gold_ingot",
    "diamond": "diamond",
    "emerald": "emerald",
    "quartz": "quartz",
    "string": "string",
    "paper": "paper",
    "book": "book",
    "apple": "apple",
    "stick": "stick",
    "gunpowder": "gunpowder",
    "water_bucket": "water_bucket",
    "lava_bucket": "lava_bucket",
    "bucket": "bucket",
    "snowball": "snowball",
    "clay_ball": "clay_ball",
    "leather": "leather",
    "bone": "bone",
    "rotten_flesh": "rotten_flesh",
    "fish": "cod",
    "cooked_fish": "cooked_cod",
    "porkchop": "porkchop",
    "cooked_porkchop": "cooked_porkchop",
}

BLOCK_RENAMES = {
    "stonebrick": "stone_bricks",
    "melon_block": "melon",
    "planks": "oak_planks",
    "log": "oak_log",
    "log2": "oak_log",
    "leaves": "oak_leaves",
    "leaves2": "oak_leaves",
    "sapling": "oak_sapling",
    "wool": "white_wool",
    "trapdoor": "oak_trapdoor",
    "wooden_slab": "oak_slab",
    "fence": "oak_fence",
    "fence_gate": "oak_fence_gate",
}

BLOCK_TAGS = {
    "planks": "minecraft:planks",
    "log": "minecraft:logs",
    "log2": "minecraft:logs",
    "leaves": "minecraft:leaves",
    "leaves2": "minecraft:leaves",
    "sapling": "minecraft:saplings",
    "wool": "minecraft:wool",
    "wooden_slab": "minecraft:wooden_slabs",
}

ORE_KEY_OVERRIDES = {
    "stickWood": {"item": "minecraft:stick"},
    "plankWood": {"tag": "minecraft:planks"},
    "logWood": {"tag": "minecraft:logs"},
    "slabWood": {"tag": "minecraft:wooden_slabs"},
    "treeSapling": {"tag": "minecraft:saplings"},
    "treeLeaves": {"tag": "minecraft:leaves"},
    "blockGlass": {"item": "minecraft:glass"},
    "blockGlassColorless": {"item": "minecraft:glass"},
    "paneGlass": {"item": "minecraft:glass_pane"},
    "paneGlassColorless": {"item": "minecraft:glass_pane"},
    "ingotBrick": {"item": "minecraft:brick"},
    "ingotNetherBrick": {"item": "minecraft:nether_brick"},
    "ingotBrickNether": {"item": "minecraft:nether_brick"},
    "slimeball": {"item": "minecraft:slime_ball"},
    "sand": {"item": "minecraft:sand"},
    "cobblestone": {"item": "minecraft:cobblestone"},
    "stone": {"item": "minecraft:stone"},
    "dustGlowstone": {"item": "minecraft:glowstone_dust"},
    "dustRedstone": {"item": "minecraft:redstone"},
    "record": {"tag": "minecraft:music_discs"},
    "dye": {"tag": "minecraft:dyes"},
    "cropCarrot": {"item": "minecraft:carrot"},
    "ntmscrewdriver": {"item": "hbm:screwdriver"},
    "ntmchemistryset": {"item": "hbm:chemistry_set"},
    "ntmtorch": {"item": "minecraft:torch"},
}

DYE_META = {
    0: "black",
    1: "red",
    2: "green",
    3: "brown",
    4: "blue",
    5: "purple",
    6: "cyan",
    7: "light_gray",
    8: "gray",
    9: "pink",
    10: "lime",
    11: "yellow",
    12: "light_blue",
    13: "magenta",
    14: "orange",
    15: "white",
}

WOOL_META = {
    0: "white",
    1: "orange",
    2: "magenta",
    3: "light_blue",
    4: "yellow",
    5: "lime",
    6: "pink",
    7: "gray",
    8: "light_gray",
    9: "cyan",
    10: "purple",
    11: "blue",
    12: "brown",
    13: "green",
    14: "red",
    15: "black",
}

SAND_META = {0: "sand", 1: "red_sand"}

SHAPE_PREFIX_ORDER = [
    "plateSextuple",
    "plateTriple",
    "wireDense",
    "wireFine",
    "barrelLight",
    "barrelHeavy",
    "receiverLight",
    "receiverHeavy",
    "gunMechanism",
    "ntmpipe",
    "dustTiny",
    "ingot",
    "plate",
    "dust",
    "nugget",
    "gem",
    "block",
    "ore",
    "billet",
    "crystal",
    "rod",
    "bolt",
    "shell",
    "stock",
    "grip",
]

SHAPE_TO_ITEM = {
    "wireFine": "wire_fine",
    "wireDense": "wire_dense",
    "bolt": "bolt",
    "shell": "shell",
    "plateTriple": "plate_cast",
    "plateSextuple": "plate_welded",
    "ntmpipe": "pipe",
    "barrelLight": "part_barrel_light",
    "barrelHeavy": "part_barrel_heavy",
    "receiverLight": "part_receiver_light",
    "receiverHeavy": "part_receiver_heavy",
    "gunMechanism": "part_mechanism",
    "stock": "part_stock",
    "grip": "part_grip",
}

MATERIAL_REMAP = {
    "aluminum": "aluminium",
}


def normalize_material(name):
    base = snake_case(name)
    return MATERIAL_REMAP.get(base, base)


def known_hbm_id(name):
    return name in KNOWN_IDS


def resolve_prefixed_item(prefix, material):
    item_name = f"{prefix}{material}"
    if known_hbm_id(item_name):
        return f"hbm:{item_name}"
    return None


def resolve_ore_dict_key(key):
    # direct override mapping
    if key in ORE_KEY_OVERRIDES:
        return ORE_KEY_OVERRIDES[key]

    if key == "ntmhanddrill":
        if known_hbm_id("hand_drill"):
            return {"item": "hbm:hand_drill"}
        if known_hbm_id("hand_drill_desh"):
            return {"item": "hbm:hand_drill_desh"}
        return None

    # dyes
    if key.startswith("dye"):
        color = key[3:]
        if color:
            return {"item": f"minecraft:{snake_case(color)}_dye"}

    # prefix-based mapping
    for prefix in SHAPE_PREFIX_ORDER:
        if key.startswith(prefix):
            material = key[len(prefix):]
            material = normalize_material(material)
            # special shapes mapped to base items
            if prefix in SHAPE_TO_ITEM:
                item = SHAPE_TO_ITEM[prefix]
                return {"item": f"hbm:{item}"}
            if prefix == "dustTiny":
                # try tiny dust/powder
                cand = f"dust_tiny_{material}"
                if known_hbm_id(cand):
                    return {"item": f"hbm:{cand}"}
                cand2 = f"powder_{material}_tiny"
                if known_hbm_id(cand2):
                    return {"item": f"hbm:{cand2}"}
                cand3 = f"powder_{material}"
                if known_hbm_id(cand3):
                    return {"item": f"hbm:{cand3}"}
                return None
            # standard prefixed items
            pref = f"{prefix}_"
            # map dust -> powder fallback if needed
            if prefix == "dust":
                cand = f"dust_{material}"
                if known_hbm_id(cand):
                    return {"item": f"hbm:{cand}"}
                cand2 = f"powder_{material}"
                if known_hbm_id(cand2):
                    return {"item": f"hbm:{cand2}"}
                return {"item": f"hbm:{cand}"}
            item = f"{prefix}_{material}"
            return {"item": f"hbm:{item}"}

    # fallback: if key matches a known id
    if known_hbm_id(key):
        return {"item": f"hbm:{key}"}

    return None


def resolve_item_expr(expr):
    expr = expr.strip()
    # string literal
    if expr.startswith('"') and expr.endswith('"'):
        key = expr.strip('"')
        return resolve_ore_dict_key(key)

    # char literal -> used as key in shaped
    if re.fullmatch(r"'.'", expr):
        return {"char": expr.strip("'")}

    # Item.getItemFromBlock(...)
    if expr.startswith("Item.getItemFromBlock"):
        inner = expr[expr.find("(")+1:expr.rfind(")")]
        return resolve_item_expr(inner)

    # new ItemStack(...)
    if expr.startswith("new ItemStack"):
        inner = expr[expr.find("(")+1:expr.rfind(")")]
        args = split_top_level(inner)
        if not args:
            return None
        base_expr = args[0]
        meta = None
        if len(args) >= 3:
            meta = args[2].strip()
        base = resolve_item_expr(base_expr)
        if base is None:
            # special: Items.dye with meta
            if base_expr.strip() == "Items.dye" and meta:
                try:
                    m = int(re.sub(r"\D", "", meta))
                    color = DYE_META.get(m)
                    if color:
                        return {"item": f"minecraft:{color}_dye"}
                except Exception:
                    pass
            if base_expr.strip() == "Blocks.wool" and meta:
                try:
                    m = int(re.sub(r"\D", "", meta))
                    color = WOOL_META.get(m)
                    if color:
                        return {"item": f"minecraft:{color}_wool"}
                except Exception:
                    pass
            if base_expr.strip() == "Blocks.sand" and meta:
                try:
                    m = int(re.sub(r"\D", "", meta))
                    sand = SAND_META.get(m)
                    if sand:
                        return {"item": f"minecraft:{sand}"}
                except Exception:
                    pass
            return None
        return base

    # DictFrame.fromOne(...)
    if "DictFrame.fromOne" in expr:
        inner = expr[expr.find("(")+1:expr.rfind(")")]
        args = split_top_level(inner)
        if args:
            return resolve_item_expr(args[0])

    # stackFromEnum or stackFromEnumMulti
    if ".stackFromEnum" in expr:
        base = expr.split(".stackFromEnum", 1)[0]
        return resolve_item_expr(base)

    # Mats.MAT_*.make(ModItems.foo)
    if ".make(" in expr and "Mats.MAT_" in expr:
        inner = expr[expr.find("(")+1:expr.rfind(")")]
        return resolve_item_expr(inner)

    # ItemBattery.getFullBattery(...), getEmptyBattery(...)
    if expr.startswith("ItemBattery.getFullBattery") or expr.startswith("ItemBattery.getEmptyBattery"):
        inner = expr[expr.find("(")+1:expr.rfind(")")]
        return resolve_item_expr(inner)

    # ItemBlowtorch.getEmptyTool / ItemToolAbilityFueled.getEmptyTool
    if expr.startswith("ItemBlowtorch.getEmptyTool") or expr.startswith("ItemToolAbilityFueled.getEmptyTool"):
        inner = expr[expr.find("(")+1:expr.rfind(")")]
        return resolve_item_expr(inner)

    # ItemModMinecart.createCartItem(...)
    if expr.startswith("ItemModMinecart.createCartItem"):
        if known_hbm_id("mod_minecart"):
            return {"item": "hbm:mod_minecart"}
        return None

    # Fluids.* (limited support)
    if expr.startswith("Fluids."):
        m = re.match(r"Fluids\.(\w+)", expr)
        if m:
            name = m.group(1)
            if name == "WATER":
                return {"item": "minecraft:water_bucket"}
            if name == "LAVA":
                return {"item": "minecraft:lava_bucket"}
        return None

    # ModItems.<name>
    m = re.fullmatch(r"ModItems\.(\w+)", expr)
    if m:
        return {"item": f"hbm:{m.group(1)}"}

    # ModBlocks.<name>
    m = re.fullmatch(r"ModBlocks\.(\w+)", expr)
    if m:
        return {"item": f"hbm:{m.group(1)}"}

    # Items.<name>
    m = re.fullmatch(r"Items\.(\w+)", expr)
    if m:
        name = m.group(1)
        name = ITEM_RENAMES.get(name, name)
        return {"item": f"minecraft:{name}"}

    # Blocks.<name>
    m = re.fullmatch(r"Blocks\.(\w+)", expr)
    if m:
        name = m.group(1)
        if name in BLOCK_TAGS:
            return {"tag": BLOCK_TAGS[name]}
        name = BLOCK_RENAMES.get(name, name)
        return {"item": f"minecraft:{name}"}

    # OreDictManager.getReflector() (may be statically imported)
    if expr in ("OreDictManager.getReflector()", "getReflector()"):
        return {"item": "hbm:neutron_reflector"} if known_hbm_id("neutron_reflector") else None

    # Ore dict KEY_* constant
    m = re.fullmatch(r"(KEY_\w+)", expr)
    if m:
        key = KEY_STRINGS.get(m.group(1))
        if key:
            return resolve_ore_dict_key(key)

    # DictFrame constants like STEEL.plate()
    m = re.fullmatch(r"(\w+)\.(\w+)\(\)", expr)
    if m:
        ident = m.group(1)
        method = m.group(2)
        material_name = DICT_FRAMES.get(ident) or DICT_GROUPS.get(ident)
        if material_name:
            # map method to ore dict key
            prefix_map = {
                "ingot": "ingot",
                "nugget": "nugget",
                "dust": "dust",
                "dustTiny": "dustTiny",
                "gem": "gem",
                "block": "block",
                "ore": "ore",
                "plate": "plate",
                "plateCast": "plateTriple",
                "plate528": "plateTriple",
                "plateWelded": "plateSextuple",
                "wireFine": "wireFine",
                "wireDense": "wireDense",
                "bolt": "bolt",
                "billet": "billet",
                "crystal": "crystal",
                "shell": "shell",
                "pipe": "ntmpipe",
                "lightBarrel": "barrelLight",
                "heavyBarrel": "barrelHeavy",
                "lightReceiver": "receiverLight",
                "heavyReceiver": "receiverHeavy",
                "mechanism": "gunMechanism",
                "stock": "stock",
                "grip": "grip",
                "any": "any",
            }
            prefix = prefix_map.get(method)
            if prefix == "any":
                # try direct material name
                mat = normalize_material(material_name)
                if mat == "ash" and known_hbm_id("powder_ash"):
                    return {"item": "hbm:powder_ash"}
                if known_hbm_id(mat):
                    return {"item": f"hbm:{mat}"}
                # tar fallback
                if "tar" in mat and known_hbm_id("oil_tar"):
                    return {"item": "hbm:oil_tar"}
                return None
            if prefix:
                key = f"{prefix}{material_name}"
                return resolve_ore_dict_key(key)

    # OreDictionary.WILDCARD_VALUE in ItemStack is ignored

    return None


def resolve_output(expr):
    expr = expr.strip()
    # ItemStack with count
    if expr.startswith("new ItemStack"):
        inner = expr[expr.find("(")+1:expr.rfind(")")]
        args = split_top_level(inner)
        if not args:
            return None
        base = resolve_item_expr(args[0])
        if base is None:
            return None
        count = 1
        if len(args) >= 2:
            try:
                count = int(re.sub(r"\D", "", args[1]))
                if count <= 0:
                    count = 1
            except Exception:
                count = 1
        return base.get("item"), count

    # DictFrame.fromOne
    if "DictFrame.fromOne" in expr:
        inner = expr[expr.find("(")+1:expr.rfind(")")]
        args = split_top_level(inner)
        if args:
            base = resolve_item_expr(args[0])
            if base is None:
                return None
            count = 1
            if len(args) >= 3:
                try:
                    count = int(re.sub(r"\D", "", args[2]))
                    if count <= 0:
                        count = 1
                except Exception:
                    count = 1
            return base.get("item"), count

    # stackFromEnum
    if ".stackFromEnum" in expr:
        base = expr.split(".stackFromEnum", 1)[0]
        base_res = resolve_item_expr(base)
        if base_res:
            return base_res.get("item"), 1

    # Mats.MAT_*.make
    if ".make(" in expr and "Mats.MAT_" in expr:
        inner = expr[expr.find("(")+1:expr.rfind(")")]
        base_res = resolve_item_expr(inner)
        if base_res:
            return base_res.get("item"), 1

    # ItemBattery.getFullBattery/Empty
    if expr.startswith("ItemBattery.getFullBattery") or expr.startswith("ItemBattery.getEmptyBattery"):
        inner = expr[expr.find("(")+1:expr.rfind(")")]
        base_res = resolve_item_expr(inner)
        if base_res:
            return base_res.get("item"), 1

    # ItemBlowtorch.getEmptyTool / ItemToolAbilityFueled.getEmptyTool
    if expr.startswith("ItemBlowtorch.getEmptyTool") or expr.startswith("ItemToolAbilityFueled.getEmptyTool"):
        inner = expr[expr.find("(")+1:expr.rfind(")")]
        base_res = resolve_item_expr(inner)
        if base_res:
            return base_res.get("item"), 1

    # ItemModMinecart.createCartItem(...)
    if expr.startswith("ItemModMinecart.createCartItem"):
        # fallback to base minecart item if available
        if known_hbm_id("mod_minecart"):
            return "hbm:mod_minecart", 1
        return None

    # direct items
    base_res = resolve_item_expr(expr)
    if base_res and "item" in base_res:
        return base_res.get("item"), 1

    return None


# ------------------------
# Conversion
# ------------------------

def parse_call_args(args_str):
    args = split_top_level(args_str)
    if not args:
        return None, []
    result_expr = args[0]
    rest = args[1:]
    # unwrap new Object[] { ... }
    if len(rest) == 1 and "new Object[]" in rest[0]:
        arr = rest[0]
        brace_start = arr.find("{")
        brace_end = arr.rfind("}")
        if brace_start != -1 and brace_end != -1:
            inside = arr[brace_start + 1:brace_end]
            rest = split_top_level(inside)
    return result_expr, rest


def build_shaped(patterns, mapping, result_item, result_count, result_nbt=None):
    key = {}
    for k, v in mapping.items():
        key[k] = v
    result = {"item": result_item, "count": result_count}
    if result_nbt:
        result["nbt"] = result_nbt
    return {
        "type": "minecraft:crafting_shaped",
        "pattern": patterns,
        "key": key,
        "result": result,
    }


def build_shapeless(ingredients, result_item, result_count, result_nbt=None):
    result = {"item": result_item, "count": result_count}
    if result_nbt:
        result["nbt"] = result_nbt
    return {
        "type": "minecraft:crafting_shapeless",
        "ingredients": ingredients,
        "result": result,
    }


def convert_calls(calls, recipe_type):
    recipes = []
    skipped = []
    for args_str in calls:
        result_expr, ins = parse_call_args(args_str)
        out = resolve_output(result_expr)
        if not out:
            skipped.append({"reason": "unresolved_output", "call": args_str})
            continue
        result_item, result_count = out
        if not result_item:
            skipped.append({"reason": "unresolved_output_item", "call": args_str})
            continue

        if recipe_type == "shaped":
            # Extract pattern strings first
            patterns = []
            idx = 0
            while idx < len(ins):
                tok = ins[idx].strip()
                if tok.startswith('"') and tok.endswith('"'):
                    patterns.append(tok.strip('"'))
                    idx += 1
                else:
                    break
            if not patterns:
                skipped.append({"reason": "no_pattern", "call": args_str})
                continue
            mapping = {}
            # remaining should be pairs: char, ingredient
            rest = ins[idx:]
            if len(rest) % 2 != 0:
                skipped.append({"reason": "odd_mapping", "call": args_str})
                continue
            ok = True
            for i in range(0, len(rest), 2):
                key_expr = rest[i].strip()
                val_expr = rest[i+1].strip()
                key_res = resolve_item_expr(key_expr)
                if not key_res or "char" not in key_res:
                    ok = False
                    break
                ing = resolve_item_expr(val_expr)
                if not ing or ("item" not in ing and "tag" not in ing):
                    ok = False
                    break
                mapping[key_res["char"]] = {k: v for k, v in ing.items() if k in ("item", "tag")}
            if not ok:
                skipped.append({"reason": "unresolved_ingredient", "call": args_str})
                continue
            recipes.append(build_shaped(patterns, mapping, result_item, result_count))
        else:
            ingredients = []
            ok = True
            for tok in ins:
                ing = resolve_item_expr(tok)
                if not ing or ("item" not in ing and "tag" not in ing):
                    ok = False
                    break
                ingredients.append({k: v for k, v in ing.items() if k in ("item", "tag")})
            if not ok:
                skipped.append({"reason": "unresolved_ingredient", "call": args_str})
                continue
            recipes.append(build_shapeless(ingredients, result_item, result_count))
    return recipes, skipped


BREEDER_TYPE_ORDER = [
    "LITHIUM",
    "TRITIUM",
    "CO",
    "CO60",
    "TH232",
    "THF",
    "U235",
    "NP237",
    "U238",
    "PU238",
    "PU239",
    "RGP",
    "WASTE",
    "LEAD",
    "URANIUM",
    "RA226",
    "AC227",
]

ZIRNOX_TYPE_ORDER = [
    "NATURAL_URANIUM_FUEL",
    "URANIUM_FUEL",
    "TH232",
    "THORIUM_FUEL",
    "MOX_FUEL",
    "PLUTONIUM_FUEL",
    "U233_FUEL",
    "U235_FUEL",
    "LES_FUEL",
    "LITHIUM",
    "ZFB_MOX",
]


def ingredient_with_nbt(item_id, nbt_snbt):
    return {"type": "forge:nbt", "item": item_id, "nbt": nbt_snbt}


def make_breeder_nbt(type_name):
    if type_name not in BREEDER_TYPE_ORDER:
        return None
    idx = BREEDER_TYPE_ORDER.index(type_name)
    return f"{{hbmRodType:{idx}b}}"


def make_zirnox_nbt(type_name):
    if type_name not in ZIRNOX_TYPE_ORDER:
        return None
    idx = ZIRNOX_TYPE_ORDER.index(type_name)
    return f"{{zirnoxType:{idx}}}"


def resolve_billet_expr(expr):
    expr = expr.strip()
    if expr in DICT_FRAMES or expr in DICT_GROUPS:
        return resolve_item_expr(f"{expr}.billet()")
    return resolve_item_expr(expr)


def resolve_ingot_expr(expr):
    expr = expr.strip()
    if expr in DICT_FRAMES or expr in DICT_GROUPS:
        return resolve_item_expr(f"{expr}.ingot()")
    return resolve_item_expr(expr)


def add_manual_recipes():
    extra = []

    # Stamp recipes (brick + nether brick)
    stamp_targets = [
        ("stamp_stone_flat", ["III", "SSS"], {"S": {"item": "minecraft:stone"}}),
        ("stamp_iron_flat", ["III", "SSS"], {"S": resolve_item_expr("IRON.ingot()")}),
        ("stamp_steel_flat", ["III", "SSS"], {"S": resolve_item_expr("STEEL.ingot()")}),
        ("stamp_titanium_flat", ["III", "SSS"], {"S": resolve_item_expr("TI.ingot()")}),
        ("stamp_obsidian_flat", ["III", "SSS"], {"S": {"item": "minecraft:obsidian"}}),
        ("stamp_desh_flat", ["BDB", "DSD", "BDB"], {"D": resolve_item_expr("DESH.ingot()"), "S": resolve_item_expr("FERRO.ingot()")}),
    ]
    bricks = ["minecraft:brick", "minecraft:nether_brick"]
    for brick in bricks:
        for out_item, pattern, base_key in stamp_targets:
            if not known_hbm_id(out_item):
                continue
            key = dict(base_key)
            if out_item == "stamp_desh_flat":
                key["B"] = {"item": brick}
            else:
                key["I"] = {"item": brick}
            extra.append(build_shaped(pattern, key, f"hbm:{out_item}", 1))

    # Rod recipes from RodRecipes.java
    rod_path = OLD_ROOT / "src/main/java/com/hbm/crafting/RodRecipes.java"
    if rod_path.exists():
        text = rod_path.read_text(encoding="utf-8", errors="ignore")

        # addPellet(...)
        for args_str in extract_calls(text, "addPellet"):
            args = split_top_level(args_str)
            if len(args) != 2:
                continue
            mat_expr = args[0].strip()
            ing = resolve_ingot_expr(mat_expr)
            graphite = resolve_item_expr("GRAPHITE.ingot()")
            if not ing or not graphite:
                continue
            if not known_hbm_id("watz_pellet"):
                continue
            extra.append(build_shaped(
                [" I ", "IGI", " I "],
                {"I": {k: v for k, v in ing.items() if k in ("item", "tag")},
                 "G": {k: v for k, v in graphite.items() if k in ("item", "tag")}},
                "hbm:watz_pellet",
                1,
            ))

        # addZIRNOXRod(...)
        for args_str in extract_calls(text, "addZIRNOXRod"):
            args = split_top_level(args_str)
            if len(args) != 2:
                continue
            billet_expr = args[0].strip()
            type_expr = args[1].strip()
            m = re.search(r"EnumZirnoxType\.(\w+)", type_expr)
            if not m:
                continue
            nbt = make_zirnox_nbt(m.group(1))
            if not nbt or not known_hbm_id("rod_zirnox"):
                continue
            billet = resolve_billet_expr(billet_expr)
            if not billet:
                continue
            extra.append(build_shapeless(
                [
                    {"item": "hbm:rod_zirnox_empty"},
                    {k: v for k, v in billet.items() if k in ("item", "tag")},
                    {k: v for k, v in billet.items() if k in ("item", "tag")},
                ],
                "hbm:rod_zirnox",
                1,
                nbt,
            ))

        # addRBMKRod(...)
        for args_str in extract_calls(text, "addRBMKRod"):
            args = split_top_level(args_str)
            if len(args) != 2:
                continue
            billet_expr = args[0].strip()
            out_expr = args[1].strip()
            out = resolve_item_expr(out_expr)
            if not out or "item" not in out:
                continue
            billet = resolve_billet_expr(billet_expr)
            if not billet:
                continue
            ingredients = [{"item": "hbm:rbmk_fuel_empty"}]
            for _ in range(8):
                ingredients.append({k: v for k, v in billet.items() if k in ("item", "tag")})
            extra.append(build_shapeless(ingredients, out["item"], 1))

        # addBreedingRod(...)
        for args_str in extract_calls(text, "addBreedingRod"):
            args = split_top_level(args_str)
            if len(args) not in (2, 3):
                continue
            if len(args) == 2:
                billet_expr = args[0].strip()
                type_expr = args[1].strip()
                mat_expr = None
            else:
                mat_expr = args[0].strip()
                billet_expr = args[1].strip()
                type_expr = args[2].strip()

            m = re.search(r"BreedingRodType\.(\w+)", type_expr)
            if not m:
                continue
            nbt = make_breeder_nbt(m.group(1))
            if not nbt:
                continue

            billet_item = resolve_item_expr(billet_expr)
            if not billet_item or "item" not in billet_item:
                continue

            if mat_expr:
                billet_ing = resolve_billet_expr(mat_expr)
            else:
                billet_ing = billet_item
            if not billet_ing:
                continue

            def rod_ing(item_id):
                return ingredient_with_nbt(item_id, nbt)

            def add_load(out_item, empty_item, count):
                if not known_hbm_id(out_item) or not known_hbm_id(empty_item):
                    return
                ingredients = [{"item": f"hbm:{empty_item}"}]
                for _ in range(count):
                    ingredients.append({k: v for k, v in billet_ing.items() if k in ("item", "tag")})
                extra.append(build_shapeless(ingredients, f"hbm:{out_item}", 1, nbt))

            def add_unload(out_count, rod_item):
                if not known_hbm_id(rod_item):
                    return
                extra.append(build_shapeless([rod_ing(f"hbm:{rod_item}")], billet_item["item"], out_count))

            add_load("rod_breeder_single", "rod_empty", 1)
            add_load("rod_breeder_dual", "rod_dual_empty", 2)
            add_load("rod_breeder_quad", "rod_quad_empty", 4)

            add_unload(1, "rod_breeder_single")
            add_unload(2, "rod_breeder_dual")
            add_unload(4, "rod_breeder_quad")

    return extra

def main():
    java_files = list((OLD_ROOT / "src/main/java").rglob("*.java"))
    shaped_calls = []
    shapeless_calls = []
    for path in java_files:
        text = path.read_text(encoding="utf-8", errors="ignore")
        shaped_calls.extend(extract_calls(text, "addRecipeAuto"))
        shapeless_calls.extend(extract_calls(text, "addShapelessAuto"))

    shaped_recipes, shaped_skipped = convert_calls(shaped_calls, "shaped")
    shapeless_recipes, shapeless_skipped = convert_calls(shapeless_calls, "shapeless")

    # Deduplicate recipes by JSON canonical form
    seen = set()
    deduped = []
    for r in shaped_recipes + shapeless_recipes:
        key = json.dumps(r, sort_keys=True)
        if key in seen:
            continue
        seen.add(key)
        deduped.append(r)

    # Add manual recipes for complex helpers/loops
    for r in add_manual_recipes():
        key = json.dumps(r, sort_keys=True)
        if key in seen:
            continue
        seen.add(key)
        deduped.append(r)

    # Write recipes
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    # clear old legacy recipes
    for p in OUT_DIR.glob("*.json"):
        p.unlink()

    name_counts = defaultdict(int)
    for r in deduped:
        result_item = r["result"]["item"].split(":", 1)[1]
        base = result_item.replace("/", "_")
        name_counts[base] += 1
        idx = name_counts[base]
        filename = f"legacy_{base}_{idx}.json"
        (OUT_DIR / filename).write_text(json.dumps(r, indent=2), encoding="utf-8")

    report = {
        "shaped_total": len(shaped_calls),
        "shapeless_total": len(shapeless_calls),
        "recipes_written": len(deduped),
        "shaped_skipped": len(shaped_skipped),
        "shapeless_skipped": len(shapeless_skipped),
        "skipped": (shaped_skipped + shapeless_skipped)[:200],
        "skipped_note": "Only first 200 skipped entries shown",
    }

    REPORT_PATH.parent.mkdir(parents=True, exist_ok=True)
    REPORT_PATH.write_text(json.dumps(report, indent=2), encoding="utf-8")

    print(json.dumps(report, indent=2))


if __name__ == "__main__":
    main()
