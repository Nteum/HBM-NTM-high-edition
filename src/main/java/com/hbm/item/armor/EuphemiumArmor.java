package com.hbm.item.armor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EuphemiumArmor extends ArmorItem {
    public EuphemiumArmor(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if(entity instanceof PlayerEntity && ArmorUtils.checkSuit((PlayerEntity) entity,material))
        {
            ((PlayerEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,5,127,true,true));
            ((PlayerEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE,5,127,true,true));
            ((PlayerEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,5,127,true,true));
            ((PlayerEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION,5,127,true,true));
            ((PlayerEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING,5,127,true,true));
        }
    }

//    @Override
//    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
//        super.usageTick(world, user, stack, remainingUseTicks);
//
//    }
}
