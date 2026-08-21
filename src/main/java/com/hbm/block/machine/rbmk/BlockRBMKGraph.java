package com.hbm.block.machine.rbmk;

import com.hbm.block.machine.BaseSingleBlockMachine;
import com.hbm.blockentity.HBMTiles;
import com.hbm.core.blockentity.BEUpdateable;
import com.hbm.blockentity.machine.rbmk.RBMKGraphEntityBE;
import net.minecraft.core.BlockPos;
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
import org.jetbrains.annotations.Nullable;

/**
 * Legacy RBMK graph panel. Sneak-right-click rotates the panel, regular
 * right-click cycles the upper or lower graph metric.
 */
public class BlockRBMKGraph extends BaseSingleBlockMachine {

    public BlockRBMKGraph(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RBMKGraphEntityBE(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == HBMTiles.RBMK_GRAPH_ENTITY.get()
                ? (level.isClientSide ? BEUpdateable::clientTicker : BEUpdateable::serverTicker)
                : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return RBMKMiniPanelGeometry.shape(state.getValue(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return RBMKMiniPanelGeometry.shape(state.getValue(FACING));
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return getInteractionShape(state, level, pos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof RBMKGraphEntityBE graph)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            if (player.isCrouching()) {
                graph.rotatePanel();
            } else {
                int slot = hit.getLocation().y - pos.getY() < 0.5D ? 1 : 0;
                graph.cycleMetric(slot);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
