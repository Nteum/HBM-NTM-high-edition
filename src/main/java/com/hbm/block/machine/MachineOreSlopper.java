package com.hbm.block.machine;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.machine.TileOreSloppper;
import com.hbm.registries.HBMCaps;
import com.hbm.utils.multiblock.DummableHelper;
import com.hbm.utils.multiblock.MultiblockData;
import com.hbm.utils.multiblock.MultiblockModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.List;

public class MachineOreSlopper extends BlockDummyable {
    public static String name = "machine_ore_slopper";
    public MachineOreSlopper(Properties pProperties) {
        super(pProperties);
        shape = Shapes.or(
                Shapes.box(-1, 0, -3, 2, 3, 4),
                Shapes.box(-1, 3, 2, 2, 6.5, 4)
        );
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileOreSloppper(pPos, pState);
    }

    @Override
    public MultiblockData getMultiblockData() {
        MultiblockModule multiblockModule = new MultiblockModule(3, 0, 3, 3, 1, 1);
        multiblockModule.addCaps(
                HBMCaps.LONG_ENERGY, ForgeCapabilities.FLUID_HANDLER,
                    0, 0, 4, Direction.SOUTH, 0, 0, -3, Direction.NORTH,
                    2, 0, 2, Direction.EAST, -1, 0, 1, Direction.WEST,
                    2, 0, 0, Direction.EAST, -1, 0, 0, Direction.WEST,
                    2, 0, -2, Direction.EAST, -1, 0, -1, Direction.WEST
                );
        return super.getMultiblockData();
    }
}
