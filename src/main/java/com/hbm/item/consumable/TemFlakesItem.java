package com.hbm.item.consumable;

import com.hbm.item.CreativeTabVariantItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import java.util.EnumSet;

/**
 * Tem Flakes from the legacy mod—three price tiers that all heal 2HP.
 * Metadata-based variants are emulated via stack NBT in modern MC versions.
 */
public class TemFlakesItem extends Item implements CreativeTabVariantItem {

    private static final String TAG_VARIANT = "Variant";

    public TemFlakesItem() {
        super(new Item.Properties().food(new FoodProperties.Builder().nutrition(0).saturationMod(0.0F).alwaysEat().build()));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide) {
            entity.heal(2.0F);
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, java.util.List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        Variant variant = getVariant(stack);
        tooltip.add(Component.translatable(variant.descriptionKey()).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        setVariant(stack, Variant.DISCOUNT);
        return stack;
    }

    @Override
    public void fillCreativeTab(BuildCreativeModeTabContentsEvent event) {
        for (Variant variant : EnumSet.allOf(Variant.class)) {
            ItemStack stack = new ItemStack(this);
            setVariant(stack, variant);
            event.getEntries().put(stack, net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    private static void setVariant(ItemStack stack, Variant variant) {
        stack.getOrCreateTag().putInt(TAG_VARIANT, variant.id);
    }

    private static Variant getVariant(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_VARIANT)) {
            return Variant.byId(tag.getInt(TAG_VARIANT));
        }
        return Variant.DISCOUNT;
    }

    public enum Variant {
        DISCOUNT(0, "item.hbm.tem_flakes.desc.discount"),
        NORMAL(1, "item.hbm.tem_flakes.desc.normal"),
        EXPENSIVE(2, "item.hbm.tem_flakes.desc.expensive");

        private final int id;
        private final String descriptionKey;

        Variant(int id, String descriptionKey) {
            this.id = id;
            this.descriptionKey = descriptionKey;
        }

        public static Variant byId(int id) {
            for (Variant variant : values()) {
                if (variant.id == id) {
                    return variant;
                }
            }
            return DISCOUNT;
        }

        public String descriptionKey() {
            return descriptionKey;
        }
    }
}
