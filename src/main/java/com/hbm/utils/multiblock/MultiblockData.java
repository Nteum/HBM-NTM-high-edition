package com.hbm.utils.multiblock;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.blockentity.base.TileProxyBase;
import com.hbm.registries.HBMCaps;
import com.hbm.registries.ModBlocks;
import com.hbm.utils.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.core.Direction.*;

public class MultiblockData {
    public static final Map<Block, MultiblockData> mapping = new HashMap<>();
    static {
        mapping.put(ModBlocks.machine_assembler.get(), new MultiblockData(1, 0, 2 ,1 ,2 ,1)
                .addCaps(HBMCaps.LONG_ENERGY, -1,0,1, SOUTH, 0,0,1, SOUTH, -1,0,-2,Direction.NORTH, 0,0,-2,Direction.NORTH)
                .addCaps(ForgeCapabilities.ITEM_HANDLER, 1,0,-1, Direction.EAST, -2,0,0,Direction.WEST));
        mapping.put(ModBlocks.CHEMPLANT.get(), new MultiblockData(2, 0, 2 ,1 ,2 ,1)
                .addCaps(HBMCaps.LONG_ENERGY,ForgeCapabilities.FLUID_HANDLER, -1,0,1, SOUTH, 0,0,1, SOUTH, -1,0,-2,Direction.NORTH, 0,0,-2,Direction.NORTH)
                .addCaps(ForgeCapabilities.ITEM_HANDLER, 1,0,-1, Direction.EAST, -2,0,0,Direction.WEST));
        mapping.put(ModBlocks.bomb_boy.get(), new MultiblockData(0,0,0,0,1,1));
        mapping.put(ModBlocks.bomb_custom.get(), mapping.get(ModBlocks.bomb_boy.get()));
        mapping.put(ModBlocks.bomb_fat_man.get(), new MultiblockData(1,0,0,1,1,1));
        mapping.put(ModBlocks.LAUNCH_PAD.get(), new MultiblockData(0, 0, 1, 1, 1, 1));
        mapping.put(ModBlocks.machine_rbmk_base.get(), new MultiblockData(0,0,0,0,0,0));
        List<Vec3i> rbmkConsoleOffsets = new ArrayList<>(square(new int[]{3, 0, 0, 0, 2, 2}));
        for (Vec3i offset : translated(square(new int[]{0, 0, 0, 1, 2, 2}), 0, 0, 1)) {
            if (!rbmkConsoleOffsets.contains(offset)) {
                rbmkConsoleOffsets.add(offset);
            }
        }
        mapping.put(ModBlocks.machine_rbmk_console.get(), new MultiblockData(rbmkConsoleOffsets, new int[]{3, 0, 0, 2, 2, 2}));
        mapping.put(ModBlocks.machine_cracking_tower.get(), new MultiblockData(0, 0, 3, 3, 2, 3)
                .addCap(new Vec3i(0, 0, -2), ForgeCapabilities.FLUID_HANDLER, SOUTH)
                .addCap(new Vec3i(2, 0, 0), ForgeCapabilities.FLUID_HANDLER, EAST)
                .addCap(new Vec3i(0, 0, 2), ForgeCapabilities.FLUID_HANDLER, NORTH)
                .addCap(new Vec3i(-2, 0, 0), ForgeCapabilities.FLUID_HANDLER, WEST)
                .addCap(new Vec3i(1, 0, 2), ForgeCapabilities.FLUID_HANDLER, NORTH));
        mapping.put(ModBlocks.machine_cooling_tower.get(), new MultiblockData(12, 0, 4, 4, 4, 4)
                .addCap(new Vec3i(0, 0, 4), ForgeCapabilities.FLUID_HANDLER, SOUTH)
                .addCap(new Vec3i(4, 0, 0), ForgeCapabilities.FLUID_HANDLER, EAST)
                .addCap(new Vec3i(0, 0, -4), ForgeCapabilities.FLUID_HANDLER, NORTH)
                .addCap(new Vec3i(-4, 0, 0), ForgeCapabilities.FLUID_HANDLER, WEST)
                .addCap(new Vec3i(3, 0, 3), ForgeCapabilities.FLUID_HANDLER, SOUTH)
                .addCap(new Vec3i(-3, 0, 3), ForgeCapabilities.FLUID_HANDLER, SOUTH)
                .addCap(new Vec3i(3, 0, -3), ForgeCapabilities.FLUID_HANDLER, NORTH)
                .addCap(new Vec3i(-3, 0, -3), ForgeCapabilities.FLUID_HANDLER, NORTH)
                .addCap(new Vec3i(0, 1, 4), ForgeCapabilities.FLUID_HANDLER, SOUTH)
                .addCap(new Vec3i(4, 1, 0), ForgeCapabilities.FLUID_HANDLER, EAST)
                .addCap(new Vec3i(0, 1, -4), ForgeCapabilities.FLUID_HANDLER, NORTH)
                .addCap(new Vec3i(-4, 1, 0), ForgeCapabilities.FLUID_HANDLER, WEST)
                .addCap(new Vec3i(3, 1, 3), ForgeCapabilities.FLUID_HANDLER, SOUTH)
                .addCap(new Vec3i(-3, 1, 3), ForgeCapabilities.FLUID_HANDLER, SOUTH)
                .addCap(new Vec3i(3, 1, -3), ForgeCapabilities.FLUID_HANDLER, NORTH)
                .addCap(new Vec3i(-3, 1, -3), ForgeCapabilities.FLUID_HANDLER, NORTH));
        int[] turbineDims = new int[]{2, 0, 1, 1, 4, 5};
        List<Vec3i> turbineOffsets = new ArrayList<>(square(turbineDims));
        Vec3i extraWest = new Vec3i(-5, 1, 0);
        if (!turbineOffsets.contains(extraWest)) {
            turbineOffsets.add(extraWest);
        }
        mapping.put(ModBlocks.machine_turbine_gas.get(), new MultiblockData(turbineOffsets, turbineDims)
                .addCap(new Vec3i(-1, 0, -1), ForgeCapabilities.FLUID_HANDLER, Direction.values())
                .addCap(new Vec3i(-1, 0, 1), ForgeCapabilities.FLUID_HANDLER, Direction.values())
                .addCap(new Vec3i(-2, 0, -1), ForgeCapabilities.FLUID_HANDLER, Direction.values())
                .addCap(new Vec3i(-2, 0, 1), ForgeCapabilities.FLUID_HANDLER, Direction.values())
                .addCap(new Vec3i(2, 0, -1), ForgeCapabilities.FLUID_HANDLER, Direction.values())
                .addCap(new Vec3i(2, 0, 1), ForgeCapabilities.FLUID_HANDLER, Direction.values())
                .addCap(new Vec3i(3, 1, 0), ForgeCapabilities.FLUID_HANDLER, Direction.values())
                .addCap(new Vec3i(0, 1, -1), ForgeCapabilities.ENERGY, Direction.values())
                .addCap(new Vec3i(0, 1, -1), HBMCaps.LONG_ENERGY, Direction.values()));

        List<Vec3i> zirnoxOffsets = new ArrayList<>(square(new int[]{1, 0, 2, 2, 2, 2}));
        for (int y = 2; y <= 4; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    zirnoxOffsets.add(new Vec3i(x, y, z));
                }
            }
            zirnoxOffsets.add(new Vec3i(-2, y, 0));
            zirnoxOffsets.add(new Vec3i(2, y, 0));
        }
        mapping.put(ModBlocks.machine_zirnox.get(), new MultiblockData(zirnoxOffsets, new int[]{1, 0, 2, 2, 2, 2})
                .addCap(new Vec3i(-2, 1, 0), ForgeCapabilities.FLUID_HANDLER, WEST)
                .addCap(new Vec3i(2, 1, 0), ForgeCapabilities.FLUID_HANDLER, EAST)
                .addCap(new Vec3i(-2, 3, 0), ForgeCapabilities.FLUID_HANDLER, WEST)
                .addCap(new Vec3i(2, 3, 0), ForgeCapabilities.FLUID_HANDLER, EAST));
        mapping.put(ModBlocks.machine_icf.get(), new MultiblockData(new ArrayList<>(), new int[]{0, 0, 0, 0, 0, 0}));
        mapping.put(ModBlocks.machine_research_reactor.get(), new MultiblockData(2, 0, 0, 0, 0, 0));
        mapping.put(ModBlocks.machine_reactor_breeding.get(), new MultiblockData(2, 0, 0, 0, 0, 0));
        mapping.put(ModBlocks.SPACE_STATION_BASE.get(), new MultiblockData(1, 0, 2, 2, 2, 2));
        mapping.put(ModBlocks.HEATER_FIREBOX.get(), new MultiblockData(0, 0, 1, 1, 1, 1));
        mapping.put(ModBlocks.machine_crucible.get(), new MultiblockData(1, 0, 1, 1, 1, 1).setInvGeneral(true));
        List<Vec3i> rbmkCraneConsoleOffsets = new ArrayList<>(square(new int[]{1, 0, 0, 0, 1, 1}));
        for (Vec3i offset : translated(square(new int[]{0, 0, 0, 1, 1, 1}), 0, 0, 1)) {
            if (!rbmkCraneConsoleOffsets.contains(offset)) {
                rbmkCraneConsoleOffsets.add(offset);
            }
        }
        mapping.put(ModBlocks.machine_rbmk_crane_console.get(), new MultiblockData(rbmkCraneConsoleOffsets, new int[]{1, 0, 0, 2, 1, 1}));
        mapping.put(ModBlocks.machine_rbmk_autoloader.get(), new MultiblockData(8, 0, 0, 0, 0, 0));
    }

    MultiblockData(List<Vec3i> offsets, int[] dirOffsets){
        this.offsets = offsets;
        this.dirOffsets = dirOffsets;
    }
    MultiblockData(int ... dirOffsets){
        this.dirOffsets = dirOffsets;
        this.offsets = square(dirOffsets);
    }
    /**
     * 注意：offset不包括核心方块，它记录的是所有填充方块的位置。
     * */
    public List<Vec3i> offsets;
    public int[] dirOffsets;
    public Map<Vec3i, Map<Capability<?>, Set<Direction>>> capsMap = new HashMap<>();
    private boolean inventoryGeneral = false;       // 是否让机器所有方块都有收发物品的能力，原版hbm中太多机器直接让所有方块都具有物品能力，一个个记录方块太麻烦。

    public MultiblockData addCap(Vec3i offset, Capability<?> cap, @Nullable Direction ... directions){
        capsMap.computeIfAbsent(offset, pos -> new HashMap<>()).computeIfAbsent(cap, capability -> new HashSet<>());
        capsMap.get(offset).get(cap).addAll(List.of(directions));
        if (capsMap.get(offset).get(cap).contains(null) && directions.length > 1)
            capsMap.get(offset).get(cap).remove(null);
        return this;
    }
    public MultiblockData addCap(Vec3i offset, Capability<?> cap){
        return addCap(offset, cap, new Direction[]{null});
    }
    public MultiblockData addCaps(Vec3i offset, Capability<?> ... caps){
        for (Capability<?> cap : caps) {
            addCap(offset, cap);
        }
        return this;
    }
    public MultiblockData addCaps(Object ...objs){
        int[] relativePos = new int[4];
        Set<Capability<?>> caps = new HashSet<>();
        for (Object obj : objs) {
            if (obj instanceof Capability<?> cap){
                caps.add(cap);
            }else if (obj instanceof Integer integer){
                relativePos[relativePos[3]] = integer;
                relativePos[3] = (relativePos[3] + 1) % 3;
            }
            else if (obj instanceof Direction direction){
                for (Capability<?> cap : caps) {
                    addCap(new Vec3i(relativePos[0],relativePos[1],relativePos[2]), cap, direction);
                }
            }
        }
        return this;
    }
    private MultiblockData setInvGeneral(boolean inventoryGeneral){
        this.inventoryGeneral = inventoryGeneral;
        return this;
    }
    public List<Tuple<BlockPos, Direction>> getCapLocation(Capability<?> cap, BlockPos corePos, Direction facing){
        List<Tuple<BlockPos, Direction>> list = new ArrayList<>();
        for (Map.Entry<Vec3i, Map<Capability<?>, Set<Direction>>> entry : capsMap.entrySet()) {
            if (!entry.getValue().containsKey(cap)) continue;
            BlockPos pos = corePos.offset(entry.getKey());
            for (Direction direction : entry.getValue().get(cap)) {
                list.add(new Tuple<>(pos, direction));
            }
        }
        return list;
    }

    public void assignCapabilities(DummyableBlockEntity be, Direction facing){
        if (!be.hasLevel() || be.getLevel().isClientSide) return;
        Level level = be.getLevel();
        capsMap.forEach((offset, capMap) -> {
            BlockPos dummyablePos = be.getBlockPos().offset(DirectionUtils.offsetRot(offset, SOUTH, facing));
            BlockEntity dummyableTile = level.getBlockEntity(dummyablePos);
            if (dummyableTile != null && dummyableTile instanceof TileProxyBase tileProxy){
                capMap.forEach((cap, dirSet) -> be.getCapability(cap).ifPresent(handler -> {
                    tileProxy.capabilitiesContent.addCapability(cap, handler, new HashSet<>(DirectionUtils.horizRot(SOUTH, facing, dirSet)));
                }));
                be.getLevel().updateNeighborsAt(dummyableTile.getBlockPos(), dummyableTile.getBlockState().getBlock());
            }
        });
        if (inventoryGeneral){
            be.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
                for (Vec3i offset : DirectionUtils.offsetRot(offsets, SOUTH, facing)) {
                    level.getBlockEntity(be.getBlockPos().offset(offset), ModBlockEntityType.PROXY_ENTITY.get()).ifPresent(tileProxyCombo -> {
                        tileProxyCombo.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, iItemHandler);
                    });
                }
            });
        }
    }

    /** 工具函数，用于计算立方体型空间的偏移量
     * 输入数组的方向：U  D  N  S  W  E
     * 别问我为什么，我也想知道bob为什么这么干
     * */
    public static List<Vec3i> square(int[] dim){
        List<Vec3i> offsets = new ArrayList<>();
        for (int i = -dim[4]; i <= dim[5]; i++) {
            for (int j = -dim[1]; j <= dim[0]; j++) {
                for (int k = -dim[2]; k <= dim[3]; k++) {
                    if (!(i==0&&j==0&&k==0))
                        offsets.add(new Vec3i(i,j,k));
                }
            }
        }
        return offsets;
    }

    private static List<Vec3i> translated(List<Vec3i> offsets, int dx, int dy, int dz) {
        List<Vec3i> translated = new ArrayList<>(offsets.size());
        for (Vec3i offset : offsets) {
            translated.add(new Vec3i(offset.getX() + dx, offset.getY() + dy, offset.getZ() + dz));
        }
        return translated;
    }
    //工具函数，返回一个立方体的偏移
    public static Vec3i square(int n){
        return new Vec3i(n,n,n);
    }
    /**
     * 为需要的方块实体添加能力，能力是从核心实体复制过去的，从而保证对核心实体的交互。
     * */
    public void distributeCaps(BlockEntity be){
        if (!(be instanceof DummyableBlockEntity) || !be.hasLevel()) return;
        BlockPos bePos = be.getBlockPos();
        Level level = be.getLevel();
        Direction facing = be.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        for (Map.Entry<Vec3i, Map<Capability<?>, Set<Direction>>> entry : capsMap.entrySet()) {
            Vec3i offset = entry.getKey();
            BlockPos proxyPos = bePos.offset(DirectionUtils.offsetRot(offset, SOUTH, facing));
            for (Map.Entry<Capability<?>, Set<Direction>> setEntry : entry.getValue().entrySet()) {
                Capability<?> proxyCap = setEntry.getKey();
                Set<Direction> proxyDir = setEntry.getValue().stream().map(direction -> DirectionUtils.horizRot(SOUTH, facing, direction)).collect(Collectors.toSet());
                if (level.getBlockEntity(proxyPos) instanceof TileProxyBase proxy && proxy.getBlockEntity()!= null && proxy.getBlockEntity().equals(be)){
                    ((DummyableBlockEntity)be).giveProxyCapabilities(offset, proxy, proxyCap, proxyDir);
//                    be.getCapability(proxyCap).ifPresent(handler -> proxy.capabilitiesContent.addCapability(proxyCap, handler, proxyDir));
                }
            }
        }
    }

    /** 将offset根据方向进行旋转。
     * 默认方向是南方，其他方向按照南方进行旋转（因为南方两个坐标都是正的）
     * （本以为会有现成方法的，但好像确实没有）
     * */
    public static List<Vec3i> transOffsets(List<Vec3i> offsets, Direction dir){
        List<Vec3i> result = new ArrayList<>(offsets);
        int[] trans;
        switch (dir){
            case NORTH -> trans = new int[]{-1,0,0,-1};
            case EAST -> trans = new int[]{0,-1,1,0};
            case SOUTH -> trans = new int[]{1,0,0,1};
            case WEST -> trans = new int[]{0,1,-1,0};
            default -> trans = new int[]{1,0,0,1};
        }
        for (int i = 0; i < result.size(); i++) {
            Vec3i v1 = result.get(i);
            Vec3i v2 = new Vec3i(v1.getX() * trans[0] + v1.getZ() * trans[2], v1.getY(), v1.getX() * trans[1] + v1.getZ() * trans[3]);
            result.set(i, v2);
        }
        return result;
    }

    public VoxelShape toShape(){
        return Shapes.box(dirOffsets[4] * -16, dirOffsets[1] * -16, dirOffsets[2] * -16,
                dirOffsets[5] * 16, dirOffsets[0] * 16, dirOffsets[3] * 16);
    }
}
