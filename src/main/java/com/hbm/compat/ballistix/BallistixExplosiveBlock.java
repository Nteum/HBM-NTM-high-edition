package com.hbm.compat.ballistix;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BallistixExplosiveBlock extends Block {

    public static final BooleanProperty UNSTABLE = BlockStateProperties.UNSTABLE;

    private final BallistixExplosiveType type;

    public BallistixExplosiveBlock(final BallistixExplosiveType type) {
        super(Properties.copy(Blocks.TNT).instabreak().sound(SoundType.GRASS));
        this.type = type;
        registerDefaultState(this.stateDefinition.any().setValue(UNSTABLE, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UNSTABLE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(UNSTABLE, ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return type.shape();
    }

    @Override
    public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
        ignite(level, pos, igniter);
        level.removeBlock(pos, false);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock()) && level.hasNeighborSignal(pos)) {
            onCaughtFire(state, level, pos, null, null);
            level.removeBlock(pos, false);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.hasNeighborSignal(pos)) {
            onCaughtFire(state, level, pos, null, null);
            level.removeBlock(pos, false);
        }
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        if (level.isClientSide) {
            return;
        }
        LivingEntity living = explosion.getIndirectSourceEntity();
        BallistixPrimedExplosive primed = BallistixPrimedExplosive.spawn(level, pos, type, living);
        primed.setFuse(level.random.nextInt(primed.getFuse() / 4 + 1) + primed.getFuse() / 8);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (type.isPressureTriggered() && entity instanceof LivingEntity) {
            explodeInstant(level, pos, (LivingEntity) entity);
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        final ItemStack held = player.getItemInHand(hand);
        final Item item = held.getItem();
        if (item != Items.FLINT_AND_STEEL && item != Items.FIRE_CHARGE) {
            return super.use(state, level, pos, player, hand, hit);
        }
        onCaughtFire(state, level, pos, hit.getDirection(), player);
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        if (!player.isCreative()) {
            if (item == Items.FLINT_AND_STEEL) {
                held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            } else {
                held.shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!level.isClientSide && projectile.isOnFire()) {
            LivingEntity living = projectile.getOwner() instanceof LivingEntity owner ? owner : null;
            explodeFromLevel(level, hit.getBlockPos(), living);
            level.removeBlock(hit.getBlockPos(), false);
        }
    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return false;
    }

    private void ignite(Level level, BlockPos pos, @Nullable LivingEntity igniter) {
        if (!level.isClientSide) {
            BallistixPrimedExplosive.spawn(level, pos, type, igniter);
        }
        level.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private void explodeFromLevel(Level level, BlockPos pos, @Nullable LivingEntity igniter) {
        ignite(level, pos, igniter);
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }

    private void explodeInstant(Level level, BlockPos pos, LivingEntity trigger) {
        if (!level.isClientSide) {
            BallistixExplosionHandlers.detonate(type, level, pos.getCenter(), trigger);
        }
        level.removeBlock(pos, false);
    }
}
