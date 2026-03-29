package com.hbm.block.machine.rbmk;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.machine.rbmk.RBMKPeripheralEntity;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

/**
 * Multi-block RBMK peripheral for large OBJ models (console, crane console, autoloader).
 */
public class BlockRBMKPeripheralLarge extends BlockDummyable {

    private final RBMKPeripheralType type;

    public BlockRBMKPeripheralLarge(Properties properties, RBMKPeripheralType type, VoxelShape shape, boolean rotateShape) {
        super(properties);
        this.type = type;
        this.shape = shape;
        this.shapeRotates = rotateShape;
    }

    public RBMKPeripheralType getPeripheralType() {
        return type;
    }

    @Nullable
    @Override
    protected BlockEntity mainBlockEntity(BlockPos pos, BlockState state) {
        return new RBMKPeripheralEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(IS_CORE) ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockPos core = getCore(state, level, pos);
        BlockEntity blockEntity = level.getBlockEntity(core);
        if (blockEntity instanceof MenuProvider provider) {
            NetworkHooks.openScreen((net.minecraft.server.level.ServerPlayer) player, provider, buf -> buf.writeBlockPos(core));
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
