package com.hbm.entity.grenade;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public abstract class ThrownGrenade extends Entity {

    public ThrownGrenade(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
}
