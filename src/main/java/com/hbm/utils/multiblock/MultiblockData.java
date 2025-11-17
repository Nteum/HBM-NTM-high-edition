package com.hbm.utils.multiblock;

import com.hbm.block.HBMMachine;
import com.hbm.blockentity.base2.DummyableBlockEntity;
import com.hbm.blockentity.base2.TileProxyBase;
import com.hbm.capabilities.HBMCaps;
import com.hbm.registries.ModBlocks;
import com.hbm.utils.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.core.Direction.*;

public class MultiblockData {
    public static final Map<Block, MultiblockData> mapping = new HashMap<>();
    static {
        mapping.put(ModBlocks.ASSEMBLER.get(), new MultiblockData(1, 0, 2 ,1 ,2 ,1)
                .addCaps(HBMCaps.LONG_ENERGY, -1,0,1, SOUTH, 0,0,1, SOUTH, -1,0,-2,Direction.NORTH, 0,0,-2,Direction.NORTH)
                .addCaps(ForgeCapabilities.ITEM_HANDLER, 1,0,-1, Direction.EAST, -2,0,0,Direction.WEST));
        mapping.put(HBMMachine.CHEMPLANT.get(), new MultiblockData(2, 0, 2 ,1 ,2 ,1)
                .addCaps(HBMCaps.LONG_ENERGY,ForgeCapabilities.FLUID_HANDLER, -1,0,1, SOUTH, 0,0,1, SOUTH, -1,0,-2,Direction.NORTH, 0,0,-2,Direction.NORTH)
                .addCaps(ForgeCapabilities.ITEM_HANDLER, 1,0,-1, Direction.EAST, -2,0,0,Direction.WEST));
        mapping.put(ModBlocks.BOMB_BOY.get(), new MultiblockData(0,0,0,0,1,1));
        mapping.put(ModBlocks.BOMB_CUSTOM.get(), mapping.get(ModBlocks.BOMB_BOY.get()));
        mapping.put(ModBlocks.BOMB_FAT_MAN.get(), new MultiblockData(1,0,0,1,1,1));
        mapping.put(HBMMachine.LAUNCH_PAD.get(), new MultiblockData(0, 0, 1, 1, 1, 1));
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
//    public Map<Capability<?>,List<Tuple<Vec3i, Direction>>> beforeTrans = new IdentityHashMap<>();
    public Map<Vec3i, Map<Capability<?>, Set<Direction>>> capsMap = new HashMap<>();

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

}
