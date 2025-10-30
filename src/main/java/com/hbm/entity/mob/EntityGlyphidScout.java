package com.hbm.entity.mob;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class EntityGlyphidScout extends EntityGlyphid{
    public EntityGlyphidScout(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public EntityGlyphidScout(Level level) {
        super(level);
    }
}
