package com.hbm.item.tool;

import com.hbm.HBMLang;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModSounds;
import com.hbm.world.feature.Meteorite;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemMeteorRemote extends Item {
    public ItemMeteorRemote(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(HBMLang.ITEM_METEOR_REMOTE_DESC.translate());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide && pPlayer.getItemInHand(pUsedHand).is(ModItems.METEOR_REMOTE.get())){
            ItemStack stack = pPlayer.getItemInHand(pUsedHand);
            stack.hurt(1, pLevel.random, (ServerPlayer) pPlayer);
            Meteorite.spawnMeteorByPlayer(pPlayer, true);
            pPlayer.sendSystemMessage(Component.literal("Watch your head!"));
            pLevel.playSound(null, pPlayer.getOnPos(), ModSounds.ITEM_TECH_BLEEP.get(), SoundSource.RECORDS, 1, 1);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
