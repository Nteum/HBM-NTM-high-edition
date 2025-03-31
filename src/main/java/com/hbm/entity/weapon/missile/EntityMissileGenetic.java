package com.hbm.entity.weapon.missile;

import com.hbm.entity.ModEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntityMissileGenetic extends EntityMissile{
    public EntityMissileGenetic(EntityType<EntityMissileGenetic> entityType, Level pLevel) {
        super(entityType, pLevel);
    }
    public EntityMissileGenetic(Level level, double x, double y,double z, BlockPos target){
        this(ModEntityType.ENTITY_MISSILE_GENETIC.get(),level);
    }

    @Override
    public String getUnlocalizedName() {
        return null;
    }

    @Override
    public int getBlipLevel() {
        return 0;
    }

    @Override
    public ItemStack getMissileItemForInfo() {
        return null;
    }
}
