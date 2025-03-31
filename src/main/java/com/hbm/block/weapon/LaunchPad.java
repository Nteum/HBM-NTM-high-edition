package com.hbm.block.weapon;

import com.hbm.block.base.BedLikeBlock;
import com.hbm.blockentity.weapon.LaunchPadEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** 导弹发射台 */
public abstract class LaunchPad extends BedLikeBlock {
//    Type type;
    public LaunchPad(Properties pProperties) {
        super(pProperties);
//        this.type = type;
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        if (!level.isClientSide()){
            if (level.getBlockEntity(pos) instanceof LaunchPadEntity entity){
                if (level.hasNeighborSignal(pos))
                    entity.updateRedstonePower(pos);
            }
        }
        super.onNeighborChange(state, level, pos, neighbor);
    }

    public static enum Type{
        BASIC,COMPAT,LARGE,SOYUZ;
    }
}
