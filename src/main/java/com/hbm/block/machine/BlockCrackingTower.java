package com.hbm.block.machine;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.machine.CrackingTowerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BlockCrackingTower extends BlockDummyable {

    public BlockCrackingTower(Properties properties) {
        super(properties);
        this.shape = Block.box(-48.0, 0.0D, -48.0D, 64.0D, 256.0D, 48.0D);
        this.shapeRotates = true;
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pos, BlockState state) {
        return new CrackingTowerEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(IS_CORE) ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    /**
     * 为了正确的选择框，需要提供与机器底座一致的Shape
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(IS_CORE) ? super.getShape(state, level, pos, context) : Shapes.empty();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.SUCCESS;
    }
}
