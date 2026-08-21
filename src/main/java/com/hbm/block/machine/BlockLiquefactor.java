package com.hbm.block.machine;

import com.hbm.blockentity.machine.LiquefactorEntity;
import com.hbm.core.block.BlockDummyable;
import com.hbm.core.contents.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 工业液化机（多方块）。移植自旧版 MachineLiquefactor。
 * 结构：3 格高核心柱 + 顶部 +1 与四周 +1 五个额外代理。
 */
public class BlockLiquefactor extends BlockDummyable {
    public static MultiblockData MULTIBLOCK_DATA;
    public BlockLiquefactor(Properties pProperties){
        super(pProperties);
        this.shape = Block.box(-16.0D, 0.0D, -16.0D, 32.0D, 64.0D, 16.0D);
    }

    @Nullable
    @Override
    protected BlockEntity mainBlockEntity(BlockPos pos, BlockState state) {
        return new LiquefactorEntity(pos, state);
    }

    @Override
    public MultiblockData getMultiblockData() {
        if (MULTIBLOCK_DATA == null){
            MULTIBLOCK_DATA = new MultiblockData(3, 0, 1, 1, 1, 1)
                    .addCap(new Vec3i(0, 0, -1), net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER, net.minecraft.core.Direction.SOUTH)
                    .addCap(new Vec3i(0, 0, 1), net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER, net.minecraft.core.Direction.NORTH)
                    .addCap(new Vec3i(-1, 0, 0), net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER, net.minecraft.core.Direction.EAST)
                    .addCap(new Vec3i(1, 0, 0), net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER, net.minecraft.core.Direction.WEST)
                    .addCap(new Vec3i(0, 3, 0), net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER, net.minecraft.core.Direction.UP);
        }
        return MULTIBLOCK_DATA;
    }
}
