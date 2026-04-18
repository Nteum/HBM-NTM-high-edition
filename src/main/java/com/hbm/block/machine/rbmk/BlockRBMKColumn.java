package com.hbm.block.machine.rbmk;

import com.hbm.block.interfaces.ILookOverlay;
import com.hbm.blockentity.machine.rbmk.RBMKTickableEntity;
import com.hbm.reactor.rbmk.RBMKDoddOverlay;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;

public class BlockRBMKColumn extends Block implements EntityBlock, ILookOverlay {

    private static final VoxelShape FULL_BLOCK_SHAPE = Shapes.block();

    @Nullable
    private final BiFunction<BlockPos, BlockState, BlockEntity> blockEntityFactory;

    public BlockRBMKColumn(final Properties properties) {
        this(properties, null);
    }

    public BlockRBMKColumn(final Properties properties,
                           @Nullable final BiFunction<BlockPos, BlockState, BlockEntity> blockEntityFactory) {
        super(properties);
        this.blockEntityFactory = blockEntityFactory;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeFor(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeFor(state);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return shapeFor(state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityFactory != null ? blockEntityFactory.apply(pos, state) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || blockEntityFactory == null) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof RBMKTickableEntity tickable) {
                tickable.serverTick();
            }
        };
    }

    @Override
    public List<Component> getDesc(Level level, BlockPos pos) {
        return RBMKDoddOverlay.describe(level, pos);
    }

    private VoxelShape shapeFor(BlockState state) {
        return FULL_BLOCK_SHAPE;
    }
}
