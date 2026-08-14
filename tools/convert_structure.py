#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
将 1.7.10 HBM 结构文件 (.nbt) 批量转换为 1.20.1 StructureTemplate 格式。

用法:
    python3 tools/convert_structure.py <file1.nbt> [file2.nbt ...]
    python3 tools/convert_structure.py --all          # 转换旧项目 structures 下所有文件

方块名映射规则:
  - hbm:tile.XXX  ->  hbm:XXX   (去掉 tile. 前缀，新工程注册名一致)
  - minecraft:XXX -> 按 VANILLA_RENAME 表映射到 1.20.1
  - hbm:tile.wand_jigsaw -> minecraft:jigsaw (orientation 由旧 direction 换算)
  - 未知/未注册 -> minecraft:air (结构仍可放置)

输出到新工程 src/main/resources/data/hbm/structures/<同名>.nbt
"""
import struct, gzip, os, glob, sys, zlib

SRC_ROOT = "/mnt/e/LEARNING/code/Minecraft/reference/Hbm-s-Nuclear-Tech-GIT-space/src/main/resources/assets/hbm/structures"
DST_ROOT = "/mnt/e/game/MineCraftModDevelop/HBM-forge/src/main/resources/data/hbm/structures"

DATA_VERSION = 3465

# ---------------- NBT 读取 ----------------
def read_payload(t, buf, pos):
    if t == 0: return None, pos
    if t == 1: return struct.unpack_from('b', buf, pos)[0], pos + 1
    if t == 2: return struct.unpack_from('>h', buf, pos)[0], pos + 2
    if t == 3: return struct.unpack_from('>i', buf, pos)[0], pos + 4
    if t == 4: return struct.unpack_from('>q', buf, pos)[0], pos + 8
    if t == 5: return struct.unpack_from('>f', buf, pos)[0], pos + 4
    if t == 6: return struct.unpack_from('>d', buf, pos)[0], pos + 8
    if t == 7:
        n = struct.unpack_from('>i', buf, pos)[0]; pos += 4
        return list(buf[pos:pos + n]), pos + n
    if t == 8:
        n = struct.unpack_from('>h', buf, pos)[0]; pos += 2
        s = buf[pos:pos + n].decode('utf-8'); pos += n
        return s, pos
    if t == 9:
        et = buf[pos]; pos += 1
        n = struct.unpack_from('>i', buf, pos)[0]; pos += 4
        items = []
        for _ in range(n):
            v, pos = read_payload(et, buf, pos)
            items.append(v)
        return (et, items), pos
    if t == 10:
        d = {}
        while True:
            t2 = buf[pos]; pos += 1
            if t2 == 0: break
            nl = struct.unpack_from('>h', buf, pos)[0]; pos += 2
            name = buf[pos:pos + nl].decode('utf-8'); pos += nl
            v, pos = read_payload(t2, buf, pos)
            d[name] = (t2, v)
        return d, pos
    if t == 11:
        n = struct.unpack_from('>i', buf, pos)[0]; pos += 4
        return list(struct.unpack_from('>' + 'i' * n, buf, pos)), pos + 4 * n
    if t == 12:
        n = struct.unpack_from('>i', buf, pos)[0]; pos += 4
        return list(struct.unpack_from('>' + 'q' * n, buf, pos)), pos + 8 * n
    raise ValueError('unknown tag %d' % t)

def parse(path):
    raw = open(path, 'rb').read()
    # 用 zlib 手动解 gzip (Python gzip 模块对某些文件报 BadGzipFile, zlib raw inflate 更鲁棒)
    if raw[:2] == b'\x1f\x8b':
        data = zlib.decompressobj(-zlib.MAX_WBITS).decompress(raw[10:])
    else:
        data = raw
    t = data[0]; pos = 1
    nl = struct.unpack_from('>h', data, pos)[0]; pos += 2
    pos += nl
    val, _ = read_payload(t, data, pos)
    return val

# ---------------- NBT 写入 ----------------
def write_payload(t, v, out):
    if t == 0: pass
    elif t == 1: out.append(struct.pack('b', v))
    elif t == 2: out.append(struct.pack('>h', v))
    elif t == 3: out.append(struct.pack('>i', v))
    elif t == 4: out.append(struct.pack('>q', v))
    elif t == 5: out.append(struct.pack('>f', v))
    elif t == 6: out.append(struct.pack('>d', v))
    elif t == 7:
        out.append(struct.pack('>i', len(v)))
        out.append(bytes(v))
    elif t == 8:
        b = v.encode('utf-8')
        out.append(struct.pack('>h', len(b)))
        out.append(b)
    elif t == 9:
        et, items = v
        out.append(struct.pack('B', et))
        out.append(struct.pack('>i', len(items)))
        for item in items:
            write_payload(et, item, out)
    elif t == 10:
        for k, (kt, kv) in v.items():
            out.append(struct.pack('B', kt))
            kb = k.encode('utf-8')
            out.append(struct.pack('>h', len(kb)))
            out.append(kb)
            write_payload(kt, kv, out)
        out.append(struct.pack('B', 0))
    elif t == 11:
        out.append(struct.pack('>i', len(v)))
        out.append(struct.pack('>' + 'i' * len(v), *v))
    elif t == 12:
        out.append(struct.pack('>i', len(v)))
        out.append(struct.pack('>' + 'q' * len(v), *v))
    else:
        raise ValueError('unknown tag %d' % t)

def write_root(d):
    chunks = [struct.pack('B', 10), struct.pack('>h', 0)]
    out = []
    write_payload(10, d, out)
    chunks.extend(out)
    return b''.join(chunks)

# ---------------- 转换规则 ----------------
# 1.7.10 方块名 -> 1.20.1 方块名 (minecraft 侧的重命名)
# 特殊处理: 彩色混凝土暂无专属 Block 类，临时映射为普通混凝土 (待实现 BlockConcreteColored)
SPECIAL_MAP = {
    'hbm:tile.concrete_colored': 'hbm:concrete',
    'hbm:tile.concrete_colored_ext': 'hbm:concrete',
}
VANILLA_RENAME = {
    'minecraft:brick_block': 'minecraft:bricks',
    'minecraft:stonebrick': 'minecraft:stone_bricks',
    'minecraft:grass': 'minecraft:grass_block',
    'minecraft:tallgrass': 'minecraft:short_grass',
    'minecraft:double_plant': 'minecraft:tall_grass',      # 用 meta 细分
    'minecraft:red_flower': 'minecraft:poppy',             # 用 meta 细分
    'minecraft:yellow_flower': 'minecraft:dandelion',
    'minecraft:wooden_door': 'minecraft:oak_door',
    'minecraft:wooden_slab': 'minecraft:oak_slab',
    'minecraft:double_wooden_slab': 'minecraft:oak_slab',
    'minecraft:stone_slab': 'minecraft:stone_slab',
    'minecraft:double_stone_slab': 'minecraft:stone_slab',
    'minecraft:log': 'minecraft:oak_log',
    'minecraft:planks': 'minecraft:oak_planks',
    'minecraft:leaves': 'minecraft:oak_leaves',
    'minecraft:stained_glass': 'minecraft:white_stained_glass',
    'minecraft:stained_glass_pane': 'minecraft:white_stained_glass_pane',
    'minecraft:stained_hardened_clay': 'minecraft:white_terracotta',
    'minecraft:wool': 'minecraft:white_wool',
    'minecraft:unlit_redstone_torch': 'minecraft:redstone_torch',
    'minecraft:redstone_torch': 'minecraft:redstone_torch',
    'minecraft:unpowered_repeater': 'minecraft:repeater',
    'minecraft:unpowered_comparator': 'minecraft:comparator',
    'minecraft:bed': 'minecraft:red_bed',
    'minecraft:flower_pot': 'minecraft:flower_pot',
    'minecraft:skull': 'minecraft:skeleton_skull',
    'minecraft:trapdoor': 'minecraft:oak_trapdoor',
    'minecraft:wooden_pressure_plate': 'minecraft:oak_pressure_plate',
    'minecraft:waterlily': 'minecraft:lily_pad',
    'minecraft:web': 'minecraft:cobweb',
    'minecraft:fence': 'minecraft:oak_fence',
    'minecraft:glass_pane': 'minecraft:glass_pane',
    'minecraft:redstone_wire': 'minecraft:redstone_wire',
    'minecraft:iron_bars': 'minecraft:iron_bars',
    'minecraft:redstone_lamp': 'minecraft:redstone_lamp',
    'minecraft:coal_block': 'minecraft:coal_block',
    'minecraft:glowstone': 'minecraft:glowstone',
    'minecraft:clay': 'minecraft:clay',
    'minecraft:gravel': 'minecraft:gravel',
    'minecraft:sand': 'minecraft:sand',
    'minecraft:sandstone': 'minecraft:sandstone',
    'minecraft:stone': 'minecraft:stone',
    'minecraft:dirt': 'minecraft:dirt',
    'minecraft:cobblestone': 'minecraft:cobblestone',
    'minecraft:torch': 'minecraft:torch',
    'minecraft:water': 'minecraft:water',
    'minecraft:lava': 'minecraft:lava',
    'minecraft:air': 'minecraft:air',
    'minecraft:crafting_table': 'minecraft:crafting_table',
    'minecraft:bookshelf': 'minecraft:bookshelf',
    'minecraft:sponge': 'minecraft:sponge',
    'minecraft:tripwire_hook': 'minecraft:tripwire_hook',
    'minecraft:trapped_chest': 'minecraft:trapped_chest',
    'minecraft:hopper': 'minecraft:hopper',
    'minecraft:vine': 'minecraft:vine',
    'minecraft:lever': 'minecraft:lever',
    'minecraft:ladder': 'minecraft:ladder',
    'minecraft:stone_button': 'minecraft:stone_button',
    'minecraft:cobblestone_wall': 'minecraft:cobblestone_wall',
}

# 旧 ForgeDirection: 0=DOWN 1=UP 2=NORTH 3=SOUTH 4=WEST 5=EAST
ORIENTATION_BY_DIR = {
    0: 'down_north', 1: 'up_north', 2: 'north_up',
    3: 'south_up', 4: 'west_up', 5: 'east_up',
}

# 1.7.10 染料序 -> 1.20.1 颜色名 (旧 meta 对应 color 顺序)
# 旧: 0black 1red 2green 3brown 4blue 5purple 6cyan 7silver 8gray 9pink 10lime 11yellow 12lightBlue 13magenta 14orange 15white
DYE_OLD_TO_NEW = {
    0: 'black', 1: 'red', 2: 'green', 3: 'brown', 4: 'blue', 5: 'purple',
    6: 'cyan', 7: 'light_gray', 8: 'gray', 9: 'pink', 10: 'lime', 11: 'yellow',
    12: 'light_blue', 13: 'magenta', 14: 'orange', 15: 'white',
}

def block_id_of(name):
    """返回 (新方块名, kind)，kind 为 None/JIGSAW/UNKNOWN。"""
    if name in SPECIAL_MAP:
        return SPECIAL_MAP[name], None
    if name == 'hbm:tile.wand_jigsaw':
        return None, 'JIGSAW'
    if name.startswith('hbm:tile.'):
        return 'hbm:' + name[len('hbm:tile.'):], None
    if name in VANILLA_RENAME:
        return VANILLA_RENAME[name], None
    if name.startswith('minecraft:'):
        return name, None
    return name, 'UNKNOWN'

# 1.7.10 楼梯 meta: 低2位=facing(0=east,1=west,2=north,3=south), bit2=half(0=bottom,1=top)
STAIRS_FACING = {0: 'east', 1: 'west', 2: 'north', 3: 'south'}
# 1.7.10 半砖 meta: bit3=half(0=bottom,1=top)
# 1.7.10 石头半砖材质: 0=stone 1=sandstone 2=wooden 3=cobblestone 4=brick 5=stone_brick 6=nether_brick 7=quartz
STONE_SLAB_MAT = {0: 'minecraft:stone_slab', 1: 'minecraft:sandstone_slab', 2: 'minecraft:oak_slab',
                  3: 'minecraft:cobblestone_slab', 4: 'minecraft:brick_slab', 5: 'minecraft:stone_brick_slab',
                  6: 'minecraft:nether_brick_slab', 7: 'minecraft:quartz_slab'}
# 1.7.10 木质半砖材质: 0=oak 1=spruce 2=birch 3=jungle 4=acacia 5=dark_oak
WOOD_SLAB_MAT = {0: 'minecraft:oak_slab', 1: 'minecraft:spruce_slab', 2: 'minecraft:birch_slab',
                 3: 'minecraft:jungle_slab', 4: 'minecraft:acacia_slab', 5: 'minecraft:dark_oak_slab'}
# 1.7.10 双石头半砖材质 (double_stone_slab)
DOUBLE_STONE_SLAB_MAT = {0: 'minecraft:stone_slab', 1: 'minecraft:sandstone_slab', 2: 'minecraft:oak_slab',
                         3: 'minecraft:cobblestone_slab', 4: 'minecraft:brick_slab', 5: 'minecraft:stone_brick_slab',
                         6: 'minecraft:nether_brick_slab', 7: 'minecraft:quartz_slab'}

def palette_entry(name, meta):
    """返回新调色板条目 (dict, kind)。kind 用于后续 nbt 处理。"""
    # 1.7.10 可旋转柱 (继承 BlockRotatedPillar): meta 0=x,4=y,8=z
    if name.startswith('hbm:tile.') and name.endswith('_pillar'):
        axis = {0: 'x', 4: 'y', 8: 'z'}.get(meta & 12, 'y')
        return {
            'Name': (8, 'hbm:' + name[len('hbm:tile.'):]),
            'Properties': (10, {'axis': (8, axis)}),
        }, None
    # 1.7.10 楼梯 (minecraft)
    if name.startswith('minecraft:') and name.endswith('_stairs'):
        facing = STAIRS_FACING.get(meta & 3, 'north')
        half = 'top' if (meta & 4) else 'bottom'
        return {
            'Name': (8, name),
            'Properties': (10, {
                'facing': (8, facing),
                'half': (8, half),
                'shape': (8, 'straight'),
                'waterlogged': (8, 'false'),
            }),
        }, 'VANILLA'
    # 1.7.10 石半砖
    if name == 'minecraft:stone_slab':
        mat = STONE_SLAB_MAT.get(meta & 7, 'minecraft:stone_slab')
        typ = 'top' if (meta & 8) else 'bottom'
        return {
            'Name': (8, mat),
            'Properties': (10, {'type': (8, typ), 'waterlogged': (8, 'false')}),
        }, 'VANILLA'
    if name == 'minecraft:double_stone_slab':
        mat = DOUBLE_STONE_SLAB_MAT.get(meta & 7, 'minecraft:stone_slab')
        return {
            'Name': (8, mat),
            'Properties': (10, {'type': (8, 'double'), 'waterlogged': (8, 'false')}),
        }, 'VANILLA'
    if name == 'minecraft:wooden_slab':
        mat = WOOD_SLAB_MAT.get(meta & 7, 'minecraft:oak_slab')
        typ = 'top' if (meta & 8) else 'bottom'
        return {
            'Name': (8, mat),
            'Properties': (10, {'type': (8, typ), 'waterlogged': (8, 'false')}),
        }, 'VANILLA'
    if name == 'minecraft:double_wooden_slab':
        mat = WOOD_SLAB_MAT.get(meta & 7, 'minecraft:oak_slab')
        return {
            'Name': (8, mat),
            'Properties': (10, {'type': (8, 'double'), 'waterlogged': (8, 'false')}),
        }, 'VANILLA'
    # 默认: 普通方块 (丢弃 meta)
    bid, kind = block_id_of(name)
    if kind == 'JIGSAW':
        return None, 'JIGSAW'
    if kind == 'UNKNOWN' or bid is None:
        bid = 'minecraft:air'
    return {'Name': (8, bid)}, None

def convert_one(src, dst):
    d = parse(src)
    if 'palette' not in d or 'blocks' not in d:
        print('  SKIP (no palette/blocks): %s' % os.path.basename(src))
        return False

    palette = d['palette'][1][1]
    blocks = d['blocks'][1][1]

    # 读旧调色板方块名 + meta
    old_names = []
    for p in palette:
        name = p['Name'][1]
        meta = 0
        if 'Properties' in p:
            try:
                meta = int(p['Properties'][1].get('meta', (8, '0'))[1])
            except (ValueError, TypeError):
                meta = 0
        old_names.append((name, meta))

    # 记录 jigsaw 的 direction (由 block nbt 提供)
    jigsaw_orient = {}
    for b in blocks:
        state = b['state'][1]
        if state < len(old_names) and old_names[state][0] == 'hbm:tile.wand_jigsaw' and 'nbt' in b:
            direction = b['nbt'][1].get('direction', (3, 2))[1]
            jigsaw_orient[state] = ORIENTATION_BY_DIR.get(direction, 'north_up')

    # 生成新调色板
    new_palette = []
    new_palette_kinds = []
    for i, (name, meta) in enumerate(old_names):
        entry, kind = palette_entry(name, meta)
        if kind == 'JIGSAW':
            orient = jigsaw_orient.get(i, 'north_up')
            new_palette.append({
                'Name': (8, 'minecraft:jigsaw'),
                'Properties': (10, {'orientation': (8, orient)}),
            })
        else:
            new_palette.append(entry)
        new_palette_kinds.append(kind)

    # 生成新方块列表 (改写 jigsaw nbt)
    new_blocks = []
    for b in blocks:
        state = b['state'][1]
        nb = dict(b)
        if 'nbt' in b and state < len(old_names) and old_names[state][0] == 'hbm:tile.wand_jigsaw':
            old_nbt = b['nbt'][1]
            old_block = old_nbt.get('block', (8, 'minecraft:air'))[1]
            target, kind2 = block_id_of(old_block)
            if kind2 == 'UNKNOWN' or target is None:
                target = 'minecraft:air'
            nb['nbt'] = (10, {
                'id': (8, 'minecraft:jigsaw'),
                'name': (8, 'minecraft:empty'),
                'target': (8, 'minecraft:empty'),
                'pool': (8, 'minecraft:empty'),
                'final_state': (8, target),
                'joint': (8, 'rollable'),
            })
        new_blocks.append(nb)

    new_root = {
        'size': d['size'],
        'entities': (9, (10, [])),
        'blocks': (9, (10, new_blocks)),
        'palette': (9, (10, new_palette)),
        'DataVersion': (3, DATA_VERSION),
    }

    data = write_root(new_root)
    os.makedirs(os.path.dirname(dst), exist_ok=True)
    with open(dst, 'wb') as f:
        f.write(gzip.compress(data, 9))
    # 校验
    back = parse(dst)
    assert back['size'] == d['size']
    return True

def main():
    args = sys.argv[1:]
    if not args or args[0] == '--all':
        files = glob.glob(os.path.join(SRC_ROOT, '**', '*.nbt'), recursive=True)
    else:
        files = []
        for a in args:
            if os.path.isabs(a):
                files.append(a)
            else:
                files.append(os.path.join(SRC_ROOT, a))
    for f in files:
        rel = os.path.relpath(f, SRC_ROOT)
        dst = os.path.join(DST_ROOT, rel)
        try:
            ok = convert_one(f, dst)
            print('%s %s' % ('OK ' if ok else 'ERR', rel))
        except Exception as e:
            print('FAIL %s: %s' % (rel, e))

if __name__ == '__main__':
    main()
