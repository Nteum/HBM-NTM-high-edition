package com.hbm.block.machine;

import com.hbm.blockentity.machine.HydrotreaterEntity;
import com.hbm.core.block.BlockDummyable;
import com.hbm.core.contents.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

/**
 * 加氢装置（多方块）。移植自旧版 MachineHydrotreater（6 格高）。
 */
public class BlockHydrotreater extends BlockDummyable {
    public static MultiblockData MULTIBLOCK_DATA;
    public BlockHydrotreater(Properties pProperties){
        super(pProperties);
        this.shape = Block.box(-16.0D, 0.0D, -16.0D, 32.0D, 96.0D, 16.0D);
    }

    @Nullable
    @Override
    protected BlockEntity mainBlockEntity(BlockPos pos, BlockState state) {
        return new HydrotreaterEntity(pos, state);
    }

    @Override
    public MultiblockData getMultiblockData() {
        if (MULTIBLOCK_DATA == null){
            MULTIBLOCK_DATA = new MultiblockData(6, 0, 1, 1, 1, 1)
                    .addCap(new Vec3i(0, 0, -1), ForgeCapabilities.FLUID_HANDLER, net.minecraft.core.Direction.SOUTH)
                    .addCap(new Vec3i(0, 0, 1), ForgeCapabilities.FLUID_HANDLER, net.minecraft.core.Direction.NORTH)
                    .addCap(new Vec3i(-1, 0, 0), ForgeCapabilities.FLUID_HANDLER, net.minecraft.core.Direction.EAST)
                    .addCap(new Vec3i(1, 0, 0), ForgeCapabilities.FLUID_HANDLER, net.minecraft.core.Direction.WEST);
        }
        return MULTIBLOCK_DATA;
    }
}
