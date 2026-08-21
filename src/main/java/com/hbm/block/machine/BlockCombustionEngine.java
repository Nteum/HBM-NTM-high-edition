package com.hbm.block.machine;

import com.hbm.blockentity.machine.CombustionEngineEntity;
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
 * 内燃机（多方块）。移植自旧版 MachineCombustionEngine（2 格高）。
 */
public class BlockCombustionEngine extends BlockDummyable {
    public static MultiblockData MULTIBLOCK_DATA;
    public BlockCombustionEngine(Properties pProperties){
        super(pProperties);
        this.shape = Block.box(-3.0D, 0.0D, -3.0D, 4.0D, 32.0D, 4.0D);
    }

    @Nullable
    @Override
    protected BlockEntity mainBlockEntity(BlockPos pos, BlockState state) {
        return new CombustionEngineEntity(pos, state);
    }

    @Override
    public MultiblockData getMultiblockData() {
        if (MULTIBLOCK_DATA == null){
            MULTIBLOCK_DATA = new MultiblockData(1, 0, 1, 0, 3, 2)
                    .addCap(new Vec3i(0, 0, -1), ForgeCapabilities.FLUID_HANDLER, Direction.SOUTH)
                    .addCap(new Vec3i(0, 0, 1), ForgeCapabilities.FLUID_HANDLER, Direction.NORTH)
                    .addCap(new Vec3i(-1, 0, 0), ForgeCapabilities.FLUID_HANDLER, Direction.EAST)
                    .addCap(new Vec3i(1, 0, 0), ForgeCapabilities.FLUID_HANDLER, Direction.WEST)
                    .addCap(new Vec3i(0, 0, -1), com.hbm.registries.HBMCaps.LONG_ENERGY, Direction.SOUTH)
                    .addCap(new Vec3i(0, 0, 1), com.hbm.registries.HBMCaps.LONG_ENERGY, Direction.NORTH)
                    .addCap(new Vec3i(-1, 0, 0), com.hbm.registries.HBMCaps.LONG_ENERGY, Direction.EAST)
                    .addCap(new Vec3i(1, 0, 0), com.hbm.registries.HBMCaps.LONG_ENERGY, Direction.WEST);
        }
        return MULTIBLOCK_DATA;
    }
}
