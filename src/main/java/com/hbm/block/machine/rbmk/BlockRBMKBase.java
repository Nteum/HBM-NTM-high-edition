package com.hbm.block.machine.rbmk;

import com.hbm.block.base.BlockDummyable;
import com.hbm.block.interfaces.ILookOverlay;
import com.hbm.blockentity.machine.rbmk.RBMKFuelChannelEntity;
import com.hbm.blockentity.machine.rbmk.RBMKBaseEntity;
import com.hbm.registries.ModItems;
import com.hbm.reactor.rbmk.RBMKColumnState;
import com.hbm.reactor.rbmk.RBMKDoddOverlay;
import com.hbm.reactor.rbmk.RBMKLevelContext;
import com.hbm.reactor.rbmk.RBMKManager;
import com.hbm.reactor.rbmk.RBMKLidType;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import java.util.Locale;
import java.util.List;
import java.util.Optional;

/**
 * 最基础的 RBMK 反应堆柱体。当前仅用于测试服务端注册流程，后续会继续扩展盖板、控制棒等逻辑。
 */
public class BlockRBMKBase extends BlockDummyable implements ILookOverlay {

    public static final EnumProperty<RBMKLidType> LID = EnumProperty.create("lid", RBMKLidType.class);
    public BlockRBMKBase(Properties properties) {
        super(properties);
        this.shape = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        this.registerDefaultState(this.defaultBlockState().setValue(LID, RBMKLidType.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LID);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new RBMKBaseEntity(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(IS_CORE) ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getCoreShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getCoreShape(state, level, pos, context);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        final BlockPos corePos = getCore(state, level, pos);
        final BlockEntity blockEntity = level.getBlockEntity(corePos);
        if (!(blockEntity instanceof RBMKBaseEntity baseEntity)) {
            player.displayClientMessage(Component.literal("RBMK 核心丢失: " + corePos.toShortString()), true);
            return InteractionResult.CONSUME;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.CONSUME;
        }

        if (!player.isShiftKeyDown()) {
            BlockPos columnTop = corePos.above();
            BlockState stateAbove = level.getBlockState(columnTop);
            Block aboveBlock = stateAbove.getBlock();
            if (aboveBlock instanceof BlockRBMKFuelChannel || aboveBlock instanceof BlockRBMKControlRod) {
                BlockHitResult redirectedHit = new BlockHitResult(hit.getLocation(), hit.getDirection(), columnTop, hit.isInside());
                return aboveBlock.use(stateAbove, level, columnTop, player, hand, redirectedHit);
            }

            if (player instanceof ServerPlayer serverPlayer && blockEntity instanceof MenuProvider provider) {
                NetworkHooks.openScreen(serverPlayer, provider, corePos);
            }
            return InteractionResult.CONSUME;
        }

        final RBMKLevelContext context = RBMKManager.context(serverLevel);
        final Optional<RBMKColumnState> column = context.column(corePos);
        if (column.isEmpty()) {
            player.displayClientMessage(Component.literal("RBMK 未注册: " + corePos.toShortString()), true);
            return InteractionResult.CONSUME;
        }

        final RBMKColumnState columnState = column.get();
        player.displayClientMessage(Component.literal(String.format(Locale.ROOT,
                "RBMK %s | heat=%.2f | ctrl=%.2f | gctrl=%.2f | lid=%s | height=%d | cooling=%.2f | flow=%.2f",
                corePos.toShortString(),
                columnState.heat(),
                columnState.controlRodInsertion(),
                context.controlRodAverage(),
                columnState.lidType().getSerializedName(),
                columnState.columnHeight(),
                columnState.settings().passiveCooling(),
                columnState.settings().columnHeatFlow())), true);

        final BlockEntity aboveEntity = level.getBlockEntity(corePos.above());
        if (aboveEntity instanceof RBMKFuelChannelEntity fuelChannel) {
            final ItemStack fuelStack = fuelChannel.fuelStack();
            final ItemStack spentStack = fuelChannel.spentFuelStack();
            final int remaining = fuelChannel.burnTimeRemaining();
            final int total = fuelChannel.burnTimeTotal();
            final double percent = total > 0 ? (remaining * 100.0D / total) : 0.0D;
            player.displayClientMessage(Component.literal(String.format(Locale.ROOT,
                    "Fuel %s -> %s | burn=%d/%d (%.0f%%) | burning=%s | rs=%d | cmp=%d",
                    fuelStack.isEmpty() ? "<empty>" : fuelStack.getHoverName().getString(),
                    spentStack.isEmpty() ? "<empty>" : spentStack.getHoverName().getString(),
                    remaining,
                    total,
                    percent,
                    fuelChannel.isBurning(),
                    fuelChannel.redstoneSignal(),
                    fuelChannel.comparatorSignal())), true);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            RBMKLidType lid = state.getValue(LID);
            if (lid.isPresent()) {
                dropLidItem(level, pos, lid);
            }
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    public static void dropLidItem(Level level, BlockPos pos, RBMKLidType lid) {
        ItemStack stack = ItemStack.EMPTY;
        if (lid == RBMKLidType.GLASS) {
            stack = ModItems.rbmk_lid_glass.get().getDefaultInstance();
        } else if (lid == RBMKLidType.SOLID) {
            stack = ModItems.rbmk_lid.get().getDefaultInstance();
        }
        if (!stack.isEmpty()) {
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, stack);
        }
    }

    @Override
    public List<Component> getDesc(Level level, BlockPos pos) {
        return RBMKDoddOverlay.describe(level, pos);
    }
}
