package com.hbm.block.machine.rbmk;

import com.hbm.block.base.BlockMachineBase;
import com.hbm.block.interfaces.ILookOverlay;
import com.hbm.blockentity.machine.rbmk.RBMKPeripheralEntity;
import com.hbm.reactor.rbmk.RBMKDoddOverlay;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * Generic RBMK peripheral that reuses {@link RBMKPeripheralEntity} to expose
 * diagnostics for consoles, elements, reflectors, etc.
 */
public class BlockRBMKPeripheral extends BlockMachineBase implements ILookOverlay {

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

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    @Override
    public List<Component> getDesc(Level level, BlockPos pos) {
        return RBMKDoddOverlay.describe(level, pos);
    }
}
