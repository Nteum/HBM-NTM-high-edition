package com.hbm.block.machine;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.machine.TileOreSloppper;
import com.hbm.registries.HBMCaps;
import com.hbm.core.contents.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class MachineOreSlopper extends BlockDummyable {
    public static String name = "machine_ore_slopper";
    public static MultiblockData MULTIBLOCK_DATA;
    public MachineOreSlopper(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileOreSloppper(pPos, pState);
    }

    @Override
    public MultiblockData getMultiblockData() {
        if (MULTIBLOCK_DATA == null) MULTIBLOCK_DATA = new MultiblockData(3, 0, 3, 3, 1, 1).addCaps(
                HBMCaps.LONG_ENERGY, ForgeCapabilities.FLUID_HANDLER,
                0, 0, 4, Direction.SOUTH, 0, 0, -3, Direction.NORTH,
                2, 0, 2, Direction.EAST, -1, 0, 1, Direction.WEST,
                2, 0, 0, Direction.EAST, -1, 0, 0, Direction.WEST,
                2, 0, -2, Direction.EAST, -1, 0, -1, Direction.WEST
        );
        return MULTIBLOCK_DATA;
    }
}
