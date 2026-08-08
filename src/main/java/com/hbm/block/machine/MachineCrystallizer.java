package com.hbm.block.machine;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.machine.TileCrystallizer;
import com.hbm.registries.HBMCaps;
import com.hbm.core.contents.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

// 矿物酸化机
public class MachineCrystallizer extends BlockDummyable {
    public static final String id = "machine_crystallizer";
    private static MultiblockData MULTIBLOCK_DATA;
    public MachineCrystallizer(Properties pProperties) {
        super(pProperties);
        shape = Shapes.create(-1, 0, -1, 2, 5, 2);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileCrystallizer(pPos, pState);
    }

    @Override
    public MultiblockData getMultiblockData() {
        if (MULTIBLOCK_DATA == null){
            MULTIBLOCK_DATA = new MultiblockData(5, 0, 1, 1, 1, 1).addCaps(
                    HBMCaps.LONG_ENERGY, ForgeCapabilities.FLUID_HANDLER,
                    2, 0, 1, Direction.EAST, 2, 0, -1, Direction.EAST,
                    -2, 0, 1, Direction.WEST,-2, 0, -1, Direction.WEST,
                    1, 0, 2, Direction.SOUTH, -1, 0, 2, Direction.SOUTH,
                    1, 0, -2, Direction.NORTH, -1, 0, -2, Direction.NORTH
                    );
        }
        return MULTIBLOCK_DATA;
    }
}
