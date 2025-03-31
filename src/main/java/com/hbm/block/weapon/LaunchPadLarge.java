package com.hbm.block.weapon;

import com.hbm.blockentity.weapon.LaunchPadEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class LaunchPadLarge extends LaunchPad{
    public LaunchPadLarge(Properties pProperties) {
        super(pProperties);
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LaunchPadEntity(pPos,pState);
    }
}
