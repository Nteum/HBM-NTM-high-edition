package com.hbm.block.machine.icf;

import com.hbm.block.base.BaseMachineBlock;
import com.hbm.blockentity.machine.icf.ICFControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockICFController extends BaseMachineBlock implements EntityBlock {

    public BlockICFController(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ICFControllerBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ICFControllerBlockEntity controller) {
                if (player.isShiftKeyDown()) {
                    controller.toggleEnabled();
                    player.displayClientMessage(controller.isEnabled()
                            ? Component.translatable("gui.hbm.icf_controller.enabled")
                            : Component.translatable("gui.hbm.icf_controller.disabled"), true);
                } else {
                    controller.forceRescan(true);
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.hbm.icf_controller"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
