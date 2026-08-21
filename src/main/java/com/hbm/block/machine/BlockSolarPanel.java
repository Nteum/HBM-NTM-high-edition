package com.hbm.block.machine;

import com.hbm.blockentity.machine.SolarPanelEntity;
import com.hbm.core.block.BlockDummyable;
import com.hbm.core.contents.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 太阳能板（多方块）。
 * 移植自旧版 MachineSolar（BlockDummyable，5x5 平台）。
 */
public class BlockSolarPanel extends BlockDummyable {
    public BlockSolarPanel(Properties pProperties){
        super(pProperties);
        this.shape = Block.box(-16.0D, 0.0D, -16.0D, 32.0D, 16.0D, 16.0D);
    }

    @Nullable
    @Override
    protected BlockEntity mainBlockEntity(BlockPos pos, BlockState state) {
        return new SolarPanelEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(IS_CORE) ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.INVISIBLE;
    }
}
