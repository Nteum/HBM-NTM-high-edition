package com.hbm.block.machine.rbmk;

import com.hbm.api.fluid.FluidUtils;
import com.hbm.block.interfaces.ILookOverlay;
import com.hbm.blockentity.machine.rbmk.RBMKSteamPortEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockRBMKSteamPort extends Block implements EntityBlock, ILookOverlay {

    private final boolean inlet;

    public BlockRBMKSteamPort(final Properties properties, final boolean inlet) {
        super(properties);
        this.inlet = inlet;
    }

    public boolean isInlet() {
        return inlet;
    }

    @Override
    public RenderShape getRenderShape(final BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(final BlockState state, final Level level, final BlockPos pos, final Player player,
                                 final InteractionHand hand, final BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof RBMKSteamPortEntity port)) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            player.displayClientMessage(Component.literal(port.debugSummary()), true);
            return InteractionResult.CONSUME;
        }

        final ItemStack held = player.getItemInHand(hand);
        final ItemStack result = inlet
                ? FluidUtils.absorbFromItem(port, 0, held)
                : FluidUtils.pourToItem(port, 0, held);
        if (!ItemStack.matches(held, result)) {
            player.setItemInHand(hand, result);
            port.setChanged();
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new RBMKSteamPortEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final Level level, final BlockState state,
                                                                  final BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof RBMKSteamPortEntity port) {
                port.serverTick();
            }
        };
    }

    @Override
    public List<Component> getDesc(final Level level, final BlockPos pos) {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof RBMKSteamPortEntity port) {
            return java.util.List.of(Component.literal(port.debugSummary()));
        }
        return java.util.List.of(Component.literal(inlet ? "RBMK water inlet" : "RBMK steam outlet"));

    }
}
