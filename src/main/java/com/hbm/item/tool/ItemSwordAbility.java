package com.hbm.item.tool;

import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Rarity;

public class ItemSwordAbility extends SwordItem {
    private Rarity rarity = Rarity.COMMON;
    // was there a reason for this to be private?
    protected float damage;
    protected double movement;
    public ItemSwordAbility(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.damage = attackDamage;
        this.movement = attackSpeed;
    }
}
