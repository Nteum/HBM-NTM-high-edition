package com.hbm.item.special;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.registries.HBMDamage;
import com.hbm.registries.ModSounds;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemUnstable extends Item {
    int radius;
    int timer;
    public ItemUnstable(int radius, int timer, Properties pProperties) {
        super(pProperties.durability(timer));
        this.radius = radius;
        this.timer = timer;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        if (pStack.getDamageValue() > 0){
            pTooltipComponents.add(HBMLang.ITEM_UNSTABLE.translate(pStack.getDamageValue() * 1.0f / pStack.getMaxDamage() + "%"));
        }
    }

//    @Override
//    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
//        ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);
//        if (!pLevel.isClientSide && itemInHand.is(this)){
//            CompoundTag tag = itemInHand.getOrCreateTag();
//            if (!tag.contains("stage", Tag.TAG_INT)){
//                tag.putInt("stage", 0);
//            }
//            tag.putInt("stage", (tag.getInt("stage") + 1) % 2);
//            return InteractionResultHolder.success(itemInHand);
//        }
//        return super.use(pLevel, pPlayer, pUsedHand);
//    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
        if (level.random.nextInt(20) == 0 && stack.getItem() instanceof ItemUnstable){
            stack.hurtAndBreak(1, player, player1 -> {
                level.addFreshEntity(EntityNukeExplosionMK5.statFac(level, radius, player.position()));
                level.playSound(null, player.getOnPos(), ModSounds.ENTITY_OLD_EXPLOSION.get(), SoundSource.RECORDS, 1.0f, 1.0f);
                player.hurt(HBMDamage.get(HBMDamage.NUKE, level.registryAccess(), player, null), 10000f);
            });
        }
    }
}
