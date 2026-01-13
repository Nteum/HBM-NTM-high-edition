package com.hbm.item.tool;

import com.hbm.api.badthing.ContaminationUtil;
import com.hbm.api.badthing.HbmLivingProps;
import com.hbm.registries.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Simplified dosimeter: plays Geiger ticks based on buffered dose and prints a detailed readout on use.
 */
public class ItemDosimeter extends Item {

    public ItemDosimeter(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof Player player) || level.isClientSide) {
            return;
        }
        if (player.tickCount % 5 != 0) {
            return;
        }
        float envDose = HbmLivingProps.getRadBuf(player);
        playGeiger(level, player, envDose);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.playSound(ModSounds.ITEM_TECH_BOOP.get(), 1.0F, 1.0F);
            ContaminationUtil.printDosimeterData(player);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.translatable("item.hbm.dosimeter.desc1").withStyle(ChatFormatting.GRAY));
    }

    private static void playGeiger(Level level, Player player, float envDose) {
        if (envDose <= 0.0F) {
            if (level.random.nextInt(100) == 0) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.ITEM_GEIGER1.get(), SoundSource.PLAYERS, 0.6F, 1.0F);
            }
            return;
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                selectGeigerSound(envDose), SoundSource.PLAYERS, 0.8F, 1.0F);
    }

    private static net.minecraft.sounds.SoundEvent selectGeigerSound(float envDose) {
        if (envDose < 0.5F) {
            return ModSounds.ITEM_GEIGER2.get();
        }
        if (envDose < 1.0F) {
            return ModSounds.ITEM_GEIGER3.get();
        }
        if (envDose < 2.0F) {
            return ModSounds.ITEM_GEIGER4.get();
        }
        if (envDose < 5.0F) {
            return ModSounds.ITEM_GEIGER5.get();
        }
        return ModSounds.ITEM_GEIGER6.get();
    }
}
