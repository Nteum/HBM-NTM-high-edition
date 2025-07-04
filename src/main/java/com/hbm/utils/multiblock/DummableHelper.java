package com.hbm.utils.multiblock;

import com.hbm.blockentity.base2.DummyableBlockEntity;
import com.hbm.utils.MultipartUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class DummableHelper {
    private DummableHelper(){}

    /** 检查方块是否可以放得下 */
    public static boolean checkRequirement(Level level, BlockPos blockPos, Direction dir, List<Vec3i> offsets){
        List<Vec3i> offsets2 = MultiblockData.transOffsets(offsets, dir);
        for (Vec3i offset : offsets2) {
            if (!level.getBlockState(blockPos.offset(offset)).canBeReplaced())return false;
        }
        return true;
    }
    /** 填充实体的方块 */
    public static void fillSpace(Level level, BlockPos blockPos, BlockState blockState, Direction dir, List<Vec3i> offsets){
        List<Vec3i> offsets2 = MultiblockData.transOffsets(offsets, dir);
        for (Vec3i offset : offsets2) {
            if (offset.getX()==0&&offset.getY()==0&&offset.getZ()==0)continue;
            level.setBlock(blockPos.offset(offset),blockState,3);
            BlockEntity blockEntity = level.getBlockEntity(blockPos.offset(offset));
            if (blockEntity instanceof DummyableBlockEntity dummyableBlockEntity){
                //填充方块实体记录中心点位
                dummyableBlockEntity.isCore = false;
                dummyableBlockEntity.corePos = new BlockPos(blockPos);
            }
        }
        //中心方块实体设为core
        if (level.getBlockEntity(blockPos) instanceof DummyableBlockEntity entity){
            entity.isCore = true;
            entity.isFormed = true;
            entity.corePos = new BlockPos(blockPos);
        }
    }
    public static void clearSpace(Level level, BlockPos blockPos, BlockState blockState, Direction direction){
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof DummyableBlockEntity dummyableBlockEntity){
            BlockPos corePos = dummyableBlockEntity.corePos;
            List<Vec3i> offsets2 = MultipartUtils.transOffsets(MultiblockData.mapping.get(blockState.getBlock()).offsets, direction);
            for (Vec3i offset : offsets2) {
                BlockPos pos = corePos.offset(offset);
                if (level.getBlockState(pos).is(blockState.getBlock())){
                    level.removeBlock(pos,false);
                }
            }
        }
    }
}
