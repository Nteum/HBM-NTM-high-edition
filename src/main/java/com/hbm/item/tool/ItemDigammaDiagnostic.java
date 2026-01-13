package com.hbm.item.tool;

import com.hbm.api.badthing.ContaminationUtil;
import com.hbm.registries.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Handheld scanner that reads the player's digamma exposure.
 */
public class ItemDigammaDiagnostic extends Item {

    public ItemDigammaDiagnostic(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.playSound(ModSounds.ITEM_TECH_BOOP.get(), 1.0F, 1.0F);
            ContaminationUtil.printDiagnosticData(player);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.translatable("item.hbm.digamma_diagnostic.desc1").withStyle(ChatFormatting.GRAY));
    }
}
