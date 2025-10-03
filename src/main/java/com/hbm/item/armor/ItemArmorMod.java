package com.hbm.item.armor;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorItem.*;
import net.minecraft.world.item.Item;

public class ItemArmorMod extends Item {
    public final Type type;
    public final boolean helmet;
    public final boolean chestplate;
    public final boolean leggings;
    public final boolean boots;
    public ItemArmorMod(Type type, boolean helmet, boolean chestplate, boolean leggings, boolean boots) {
        super(new Properties());
        this.type = type;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }
}
