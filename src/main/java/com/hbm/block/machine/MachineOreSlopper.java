package com.hbm.block.machine;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.machine.TileOreSloppper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

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
}
