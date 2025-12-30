package com.hbm.block.machine.rbmk;

import com.hbm.block.base.BlockMachineBase;
import com.hbm.blockentity.machine.rbmk.RBMKPeripheralEntity;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Generic RBMK peripheral that reuses {@link RBMKPeripheralEntity} to expose
 * diagnostics for consoles, elements, reflectors, etc.
 */
public class BlockRBMKPeripheral extends BlockMachineBase {

    private final RBMKPeripheralType type;

    public BlockRBMKPeripheral(Properties properties, RBMKPeripheralType type) {
        super(properties);
        this.type = type;
    }

    public RBMKPeripheralType getPeripheralType() {
        return type;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RBMKPeripheralEntity(pos, state);
    }
}
