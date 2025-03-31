package com.hbm.item.weapon;

import com.hbm.entity.weapon.missile.EntityMissile;
import com.hbm.entity.weapon.missile.EntityMissileGenetic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ItemMissle extends Item{

    public ItemMissle(Properties pProperties) {
        super(pProperties);
    }

    public EntityMissile createEntity(Level level, BlockPos pos, BlockPos target){
        Vec3 center = pos.getCenter();
        return new EntityMissileGenetic(level,center.x,center.y,center.z,target);
    }
}
