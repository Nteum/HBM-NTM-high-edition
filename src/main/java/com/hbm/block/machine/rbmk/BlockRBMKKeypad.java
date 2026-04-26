package com.hbm.block.machine.rbmk;

import com.hbm.block.machine.BaseSingleBlockMachine;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.UpdateableBlockEntity;
import com.hbm.blockentity.machine.rbmk.RBMKKeypadEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

/**
 * Modern RBMK keypad slice: linked control rods can be driven to four preset
 * insertion levels by clicking the panel quadrants.
 */
public class BlockRBMKKeypad extends BaseSingleBlockMachine {

    public BlockRBMKKeypad(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RBMKKeypadEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == ModBlockEntityType.RBMK_KEYPAD_ENTITY.get()
                ? (level.isClientSide ? UpdateableBlockEntity::clientTicker : UpdateableBlockEntity::serverTicker)
                : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return RBMKMiniPanelGeometry.keypadShape();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return RBMKMiniPanelGeometry.keypadShape();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getInteractionShape(state, level, pos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof RBMKKeypadEntity keypad)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player.isCrouching() && player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, keypad, buf -> buf.writeBlockPos(pos));
            return InteractionResult.CONSUME;
        }
        if (!level.isClientSide) {
            keypad.trigger(quadrantFromHit(state.getValue(FACING), pos, hit));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static int quadrantFromHit(Direction facing, BlockPos pos, BlockHitResult hit) {
        double localX = hit.getLocation().x - pos.getX();
        double localY = hit.getLocation().y - pos.getY();
        double localZ = hit.getLocation().z - pos.getZ();
        double u = switch (facing) {
            case NORTH -> 1.0D - localX;
            case SOUTH -> localX;
            case WEST -> localZ;
            case EAST -> 1.0D - localZ;
            default -> localX;
        };
        int row = localY < 0.5D ? 1 : 0;
        int col = u >= 0.5D ? 1 : 0;
        return row * 2 + col;
    }
}
