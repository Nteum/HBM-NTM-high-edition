package com.hbm.block.weapon;

import com.hbm.blockentity.weapon.LaunchPadEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LaunchPadBasic extends LaunchPad{
    public static final VoxelShape SHAPE = Block.box(-32.0,0.0D,-32.0D,32.0D,16.0D,32.0D);
    public LaunchPadBasic(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LaunchPadEntity(pPos,pState);
    }

    @Override
    protected List<Vec3i> getOffsets() {
        return square(new int[]{0,0,1,1,1,1});
    }
}
