package com.hbm.block.weapon;

import com.hbm.blockentity.weapon.NukeBombBoyEntity;
import com.hbm.blockentity.weapon.NukeBombFatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NukeBoy extends NukeBomb{
    public NukeBoy(Properties pProperties, int range) {
        super(pProperties, range);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new NukeBombBoyEntity(pPos,pState);
    }

    @Override
    protected List<Vec3i> getOffsets() {
        return square(new int[]{0, 0, 1 ,0 ,0 ,2});
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }
}
