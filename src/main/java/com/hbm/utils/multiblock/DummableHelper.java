package com.hbm.utils.multiblock;

import com.hbm.block.HBMBlockProperties;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.blockentity.base.TileProxyBase;
import com.hbm.utils.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DummableHelper {
    private DummableHelper(){}

    private static final ThreadLocal<Set<ClearingKey>> CLEARING_GUARD = ThreadLocal.withInitial(HashSet::new);

    private record ClearingKey(Level level, BlockPos corePos) {
    }

    /** 检查方块是否可以放得下 */
    public static boolean checkRequirement(Level level, BlockPos blockPos, Direction dir, List<Vec3i> offsets){
        return checkRequirement(level, blockPos, dir, offsets, null);
    }

    public static boolean checkRequirement(Level level, BlockPos blockPos, Direction dir, List<Vec3i> offsets, BlockPos ignorePos){
//        List<Vec3i> offsets2 = MultiblockData.transOffsets(offsets, dir);
        List<Vec3i> offsets2 = DirectionUtils.offsetRot(offsets, Direction.SOUTH, dir);
        for (Vec3i offset : offsets2) {
            BlockPos targetPos = blockPos.offset(offset);
            if (ignorePos != null && targetPos.equals(ignorePos)) {
                continue;
            }
            if (!level.getBlockState(targetPos).canBeReplaced()) return false;
        }
        return true;
    }
    /** 填充实体的方块 */
    public static void fillSpace(Level level, BlockPos blockPos, BlockState blockState, Direction dir, List<Vec3i> offsets){
//        List<Vec3i> offsets2 = MultiblockData.transOffsets(offsets, dir);
        List<Vec3i> offsets2 = DirectionUtils.offsetRot(offsets, Direction.SOUTH, dir);
        BlockState newSate = blockState.setValue(HBMBlockProperties.IS_CORE, Boolean.FALSE);
        for (Vec3i offset : offsets2) {
            if (offset.getX()==0&&offset.getY()==0&&offset.getZ()==0)continue;
            level.setBlock(blockPos.offset(offset),newSate,3);
            BlockEntity blockEntity = level.getBlockEntity(blockPos.offset(offset));
            if (blockEntity instanceof TileProxyBase tileProxyBase){
                tileProxyBase.cachedPos = new BlockPos(blockPos);
            }
        }
        //中心方块实体设为core
        if (level.getBlockEntity(blockPos) instanceof DummyableBlockEntity entity){
            entity.isFormed = true;
        }
    }
    public static void clearSpace(Level level, BlockPos blockPos, BlockState blockState, Direction direction){
        final BlockPos corePos = resolveCorePos(level, blockPos);
        if (corePos == null) {
            return;
        }
        final ClearingKey key = new ClearingKey(level, corePos.immutable());
        final Set<ClearingKey> active = CLEARING_GUARD.get();
        if (!active.add(key)) {
            return;
        }
        try {
            BlockEntity coreEntity = level.getBlockEntity(corePos);
            if (!(coreEntity instanceof DummyableBlockEntity)){
                return;
            }
            final MultiblockData data = MultiblockData.mapping.get(blockState.getBlock());
            if (data == null) {
                level.removeBlock(corePos, false);
                return;
            }
            // 移除填充方块
//            List<Vec3i> offsets2 = MultipartUtils.transOffsets(MultiblockData.mapping.get(blockState.getBlock()).offsets, direction);
            List<Vec3i> offsets2 = DirectionUtils.offsetRot(data.offsets, Direction.SOUTH, direction);
            for (Vec3i offset : offsets2) {
                BlockPos pos = corePos.offset(offset);
                if (level.getBlockState(pos).is(blockState.getBlock())){
                    level.removeBlock(pos,false);
                }
            }
            // 移除核心方块
            level.removeBlock(corePos, false);
        } finally {
            active.remove(key);
            if (active.isEmpty()) {
                CLEARING_GUARD.remove();
            }
        }
    }

    public static boolean isClearing(Level level, BlockPos blockPos, BlockState blockState) {
        BlockPos corePos = resolveCorePos(level, blockPos);
        if (corePos == null) {
            return false;
        }
        return CLEARING_GUARD.get().contains(new ClearingKey(level, corePos));
    }

    private static BlockPos resolveCorePos(Level level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof TileProxyBase tileProxyBase) {
            return tileProxyBase.cachedPos;
        }
        return blockPos;
    }
}
