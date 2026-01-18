package com.hbm.entity.weapon.missile;

import com.hbm.entity.ModEntityType;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityMissileTier0 extends EntityMissile{
    public EntityMissileTier0(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public EntityMissileTier0(EntityType<? extends ThrowableProjectile> pEntityType, Level level, float x, float y, float z, BlockPos target) {
        super(pEntityType, level, x, y, z, target);
    }

    @Override
    public List<ItemStack> getDebris() {
        List<ItemStack> list = new ArrayList<ItemStack>();
        list.add(new ItemStack(ModItems.WIRE_FINE_ALUMINIUM.get(), 4));
        list.add(new ItemStack(ModItems.PLATE_TITANIUM.get(), 4));
        list.add(new ItemStack(ModItems.SHELL.get(), 2));
        list.add(new ItemStack(ModItems.DUCT_TAPE.get(), 1));
        return list;
    }

    @Override
    protected float getContrailScale() {
        return 0.5f;
    }
    public static class EntityMissileTest extends EntityMissileTier0 {
        public EntityMissileTest(EntityType<? extends ThrowableProjectile> pEntityType, Level world) {
            super(pEntityType, world);
        }
        public EntityMissileTest(Level world, float x, float y, float z, BlockPos target) {
            super(ModEntityType.ENTITY_MISSILE_TEST.get(), world, x, y, z, target);
        }
        @Override public ItemStack getDebrisRareDrop() { return null; }
        @Override public ItemStack getMissileItemForInfo() {
            return ItemStack.EMPTY;
//            return new ItemStack(HBMWeapon.MISSILE_TEST.get());
        }

        @Override public void onMissileImpact(HitResult mop) {
            Vec3 loc = mop.getLocation();
            level().explode(null, loc.x, loc.y, loc.z, 5, Level.ExplosionInteraction.BLOCK);
//            Vec3 location = mop.getLocation();
//            int x = (int) Math.floor(location.x);
//            int y = (int) Math.floor(location.y);
//            int z = (int) Math.floor(location.z);
//            int range = 50;
//
//            for(int iX = -range; iX <= range; iX++) {
//                for(int iY = -range; iY <= range; iY++) {
//                    for(int iZ = -range; iZ <= range; iZ++) {
//                        double dist = Math.sqrt(iX * iX + iY * iY + iZ * iZ);
//                        if(dist > range) continue;
//                        BlockPos blockPos = new BlockPos(x + iX, y + iY, z + iZ);
//                        BlockState blockState = level().getBlockState(blockPos);
//                        int charMeta = (int) Mth.clamp(12 - (dist / range) * (dist / range) * 13, 0, 12);
//
//                        if (!blockState.isSolid() && !blockState.is(HBMBlockComponent.SELLAFIELD_SLAKED.get()) && blockState.getExplosionResistance(level(),blockPos,null) <= charMeta){
//                            level().setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
//                        }
//                    }
//                }
//            }
        }
    }
}
