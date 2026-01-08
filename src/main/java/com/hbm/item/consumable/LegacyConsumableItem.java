package com.hbm.item.consumable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Shared helper for legacy consumable items coming from 1.7.10 content.
 * Allows configuring tooltips, potion effects, custom actions (vomit, etc.)
 * and optional container return logic (e.g. bowls, bottles).
 */
public class LegacyConsumableItem extends Item {

    private final Supplier<Item> containerSupplier;
    private final List<String> tooltipKeys;
    private final List<LegacyEffect> effects;
    private final BiConsumer<Level, LivingEntity> customAction;
    private final UseAnim useAnimation;
    private final int useDuration;

    private LegacyConsumableItem(Properties properties,
                                 @Nullable Supplier<Item> containerSupplier,
                                 List<String> tooltipKeys,
                                 List<LegacyEffect> effects,
                                 @Nullable BiConsumer<Level, LivingEntity> customAction,
                                 UseAnim useAnimation,
                                 int useDuration) {
        super(properties);
        this.containerSupplier = containerSupplier;
        this.tooltipKeys = tooltipKeys;
        this.effects = effects;
        this.customAction = customAction;
        this.useAnimation = useAnimation;
        this.useDuration = useDuration;
    }

    public static Builder builder(int nutrition, float saturation) {
        return new Builder(nutrition, saturation);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        for (String key : tooltipKeys) {
            if (key == null || key.isEmpty()) {
                continue;
            }
            tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return useAnimation;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return useDuration;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide) {
            applyEffects(level, entity);
            if (customAction != null) {
                customAction.accept(level, entity);
            }
        }
        return handleContainerReturn(result, entity);
    }

    private void applyEffects(Level level, LivingEntity entity) {
        for (LegacyEffect effect : effects) {
            if (effect.chance <= 0.0F) {
                continue;
            }
            if (level.random.nextFloat() <= effect.chance) {
                entity.addEffect(new MobEffectInstance(effect.effect().get(), effect.duration(), effect.amplifier()));
            }
        }
    }

    private ItemStack handleContainerReturn(ItemStack stack, LivingEntity entity) {
        if (containerSupplier == null) {
            return stack;
        }
        Item container = containerSupplier.get();
        if (stack.isEmpty()) {
            return new ItemStack(container);
        }
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            ItemStack remainder = new ItemStack(container);
            if (!player.getInventory().add(remainder)) {
                player.drop(remainder, false);
            }
        }
        return stack;
    }

    public record LegacyEffect(Supplier<MobEffect> effect, int duration, int amplifier, float chance) {}

    public static class Builder {

        private final FoodProperties.Builder foodBuilder;
        private final Item.Properties itemProperties;
        private Supplier<Item> containerSupplier;
        private final List<String> tooltipKeys = new ArrayList<>();
        private final List<LegacyEffect> effects = new ArrayList<>();
        private BiConsumer<Level, LivingEntity> customAction;
        private UseAnim useAnimation = UseAnim.EAT;
        private int useDuration = 32;

        private Builder(int nutrition, float saturation) {
            this.foodBuilder = new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation);
            this.itemProperties = new Item.Properties();
        }

        public Builder meat() {
            foodBuilder.meat();
            return this;
        }

        public Builder alwaysEat() {
            foodBuilder.alwaysEat();
            return this;
        }

        public Builder container(Supplier<Item> supplier) {
            this.containerSupplier = supplier;
            return this;
        }

        public Builder tooltip(String... keys) {
            Collections.addAll(this.tooltipKeys, keys);
            return this;
        }

        public Builder effect(Supplier<MobEffect> effect, int duration, int amplifier, float chance) {
            this.effects.add(new LegacyEffect(effect, duration, amplifier, chance));
            return this;
        }

        public Builder customAction(BiConsumer<Level, LivingEntity> handler) {
            this.customAction = handler;
            return this;
        }

        public Builder stacksTo(int size) {
            this.itemProperties.stacksTo(size);
            return this;
        }

        public Builder useAnimation(UseAnim animation) {
            this.useAnimation = animation;
            return this;
        }

        public Builder useDuration(int duration) {
            this.useDuration = duration;
            return this;
        }

        public LegacyConsumableItem build() {
            Item.Properties props = this.itemProperties.food(foodBuilder.build());
            return new LegacyConsumableItem(props, containerSupplier, List.copyOf(tooltipKeys), List.copyOf(effects), customAction, useAnimation, useDuration);
        }
    }
}
