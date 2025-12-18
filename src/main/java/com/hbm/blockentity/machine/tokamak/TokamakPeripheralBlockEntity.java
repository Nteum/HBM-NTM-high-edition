package com.hbm.blockentity.machine.tokamak;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Small helper base for Tokamak IO blocks (ports/injectors) that simply proxy capabilities to the
 * adjacent controller without forcing every block to duplicate lookup logic.
 */
abstract class TokamakPeripheralBlockEntity extends BlockEntity {

    protected TokamakPeripheralBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Nullable
    protected ControllerLink findController() {
        if (level == null) {
            return null;
        }
        for (Direction dir : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(dir));
            if (neighbor instanceof TokamakControllerBlockEntity controller) {
                return new ControllerLink(controller, dir.getOpposite());
            }
        }
        return null;
    }

    protected record ControllerLink(TokamakControllerBlockEntity controller, Direction directionFromController) {}
}
