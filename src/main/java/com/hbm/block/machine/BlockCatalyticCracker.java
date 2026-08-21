package com.hbm.block.machine;

import com.hbm.blockentity.machine.CatalyticCrackerEntity;
import com.hbm.core.block.BlockDummyable;
import com.hbm.core.contents.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

/**
 * 催化裂化塔（多方块）。移植自旧版 MachineCatalyticCracker（4 格高）。
 * 重油/柴油 + 蒸汽 → 轻质馏分。
 */
public class BlockCatalyticCracker extends BlockDummyable {
    public static MultiblockData MULTIBLOCK_DATA;
    public BlockCatalyticCracker(Properties pProperties){
        super(pProperties);
        this.shape = Block.box(-3.0D, 0.0D, -3.0D, 3.0D, 64.0D, 4.0D);
    }

    @Override
    protected int placementOffset() {
        return 3;
    }

    @Nullable
    @Override
    protected BlockEntity mainBlockEntity(BlockPos pos, BlockState state) {
        return new CatalyticCrackerEntity(pos, state);
    }

    @Override
    public MultiblockData getMultiblockData() {
        if (MULTIBLOCK_DATA == null){
            MULTIBLOCK_DATA = new MultiblockData(0, 0, 3, 3, 2, 3)
                    .addCap(new Vec3i(0, 0, -3), ForgeCapabilities.FLUID_HANDLER, Direction.SOUTH)
                    .addCap(new Vec3i(0, 0, 3), ForgeCapabilities.FLUID_HANDLER, Direction.NORTH)
                    .addCap(new Vec3i(-2, 0, 0), ForgeCapabilities.FLUID_HANDLER, Direction.EAST)
                    .addCap(new Vec3i(2, 0, 0), ForgeCapabilities.FLUID_HANDLER, Direction.WEST);
        }
        return MULTIBLOCK_DATA;
    }
}
