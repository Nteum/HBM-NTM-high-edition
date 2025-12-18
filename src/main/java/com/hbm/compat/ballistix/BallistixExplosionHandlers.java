package com.hbm.compat.ballistix;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

final class BallistixExplosionHandlers {

    private BallistixExplosionHandlers() {
    }

    static void detonate(BallistixExplosiveType type, Level level, Vec3 pos, Entity owner) {
        switch (type) {
            case OBSIDIAN -> blast(level, pos, type.baseRadius(), false);
            case CONDENSIVE -> blast(level, pos, type.baseRadius(), false);
            case INCENDIARY -> incendiary(level, pos, type.baseRadius());
            case ATTRACTIVE -> applyForce(level, pos, 6.0D, 1.2D, false);
            case REPULSIVE -> applyForce(level, pos, 6.0D, 1.4D, true);
            case SHRAPNEL -> shrapnel(level, pos, 28, owner);
            case CHEMICAL -> chemicalCloud(level, pos, 6.0F);
            case ANVIL -> dropAnvils(level, pos, 10);
            case INFESTIVE -> spawnInfestation(level, pos, 8);
            case DEBILITATION -> debilitate(level, pos, 7.0F);
            case FRAGMENTATION -> shrapnel(level, pos, 50, owner);
            case CONTAGIOUS -> contagious(level, pos, type.baseRadius());
            case BREACHING -> breaching(level, pos, type.baseRadius());
            case THERMOBARIC -> thermobaric(level, pos, type.baseRadius());
            case SONIC -> sonic(level, pos, type.baseRadius());
            case ANTIGRAVITY -> antigravity(level, pos, type.baseRadius());
            case EMP -> emp(level, pos, type.baseRadius());
            case NUCLEAR -> nuclear(level, pos, type.baseRadius());
            case ENDOTHERMIC -> endothermic(level, pos, type.baseRadius());
            case EXOTHERMIC -> exothermic(level, pos, type.baseRadius());
            case ENDER -> ender(level, pos, type.baseRadius());
            case HYPERSONIC -> hypersonic(level, pos, type.baseRadius());
            case REJUVINATION -> rejuvenate(level, pos, type.baseRadius());
            case ANTIMATTER -> antimatter(level, pos, type.baseRadius());
            case LARGE_ANTIMATTER -> antimatter(level, pos, type.baseRadius());
            case DARKMATTER -> darkmatter(level, pos, type.baseRadius());
            case LANDMINE -> blast(level, pos, type.baseRadius(), false);
            default -> blast(level, pos, 4.0F, false);
        }
    }

    private static void blast(Level level, Vec3 pos, float radius, boolean causesFire) {
        level.explode(null, pos.x, pos.y, pos.z, radius, causesFire, ExplosionInteraction.TNT);
    }

    private static void applyForce(Level level, Vec3 pos, double radius, double strength, boolean pushOut) {
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(pos, pos).inflate(radius),
                living -> living.isAlive() && !living.isSpectator());
        for (LivingEntity living : targets) {
            Vec3 delta = living.position().subtract(pos);
            double distance = delta.length();
            if (distance < 0.001D || distance > radius) {
                continue;
            }
            Vec3 dir = delta.normalize().scale(strength * (1.0D - distance / radius));
            if (!pushOut) {
                dir = dir.scale(-1);
            }
            living.push(dir.x, dir.y + 0.05D, dir.z);
            living.hurtMarked = true;
        }
    }

    private static void incendiary(Level level, Vec3 pos, float radius) {
        blast(level, pos, radius, true);
        if (!level.isClientSide) {
            BlockPos center = BlockPos.containing(pos);
            for (BlockPos target : BlockPos.betweenClosed(center.offset(-(int) radius, -1, -(int) radius),
                    center.offset((int) radius, 1, (int) radius))) {
                if (center.distSqr(target) > radius * radius) {
                    continue;
                }
                if (level.random.nextFloat() > 0.3F) {
                    continue;
                }
                if (level.getBlockState(target).isAir() && level.getBlockState(target.below()).isSolid()) {
                    level.setBlock(target, Blocks.FIRE.defaultBlockState(), 11);
                }
            }
        }
    }

    private static void shrapnel(Level level, Vec3 pos, int count, Entity owner) {
        if (level.isClientSide) {
            return;
        }
        for (int i = 0; i < count; i++) {
            Arrow arrow = new Arrow(level, pos.x, pos.y + 0.2D, pos.z);
            arrow.setOwner(owner);
            Vec3 direction = Vec3.directionFromRotation(level.random.nextFloat() * 360.0F,
                    level.random.nextFloat() * 60.0F - 30.0F);
            arrow.setDeltaMovement(direction.normalize().scale(1.2D));
            arrow.setBaseDamage(4.0D);
            arrow.pickup = Arrow.Pickup.DISALLOWED;
            level.addFreshEntity(arrow);
        }
    }

    private static void chemicalCloud(Level level, Vec3 pos, float radius) {
        if (level.isClientSide) {
            for (int i = 0; i < 120; i++) {
                double rx = pos.x + (level.random.nextDouble() - 0.5D) * radius * 2;
                double rz = pos.z + (level.random.nextDouble() - 0.5D) * radius * 2;
                level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, rx, pos.y + 0.5D, rz, 0.0D, 0.01D, 0.0D);
            }
            return;
        }
        ServerLevel server = (ServerLevel) level;
        List<LivingEntity> victims = server.getEntitiesOfClass(LivingEntity.class,
                new AABB(pos, pos).inflate(radius));
        for (LivingEntity living : victims) {
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 360, 1));
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 360, 1));
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 360, 0));
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
        }
    }

    private static void dropAnvils(Level level, Vec3 pos, int count) {
        if (level.isClientSide) {
            return;
        }
        for (int i = 0; i < count; i++) {
            double dx = pos.x + level.random.nextDouble() * 6 - 3;
            double dz = pos.z + level.random.nextDouble() * 6 - 3;
            BlockPos spawnPos = BlockPos.containing(dx, pos.y + 12 + level.random.nextInt(6), dz);
            FallingBlockEntity.fall(level, spawnPos, Blocks.ANVIL.defaultBlockState());
        }
    }

    private static void spawnInfestation(Level level, Vec3 pos, int count) {
        if (level.isClientSide) {
            return;
        }
        ServerLevel server = (ServerLevel) level;
        for (int i = 0; i < count; i++) {
            Silverfish fish = new Silverfish(net.minecraft.world.entity.EntityType.SILVERFISH, server);
            fish.setPos(pos.x + server.random.nextGaussian() * 2, pos.y, pos.z + server.random.nextGaussian() * 2);
            fish.finalizeSpawn(server, server.getCurrentDifficultyAt(fish.blockPosition()), null, null, null);
            server.addFreshEntity(fish);
        }
    }

    private static void debilitate(Level level, Vec3 pos, float radius) {
        if (level.isClientSide) {
            return;
        }
        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(pos, pos).inflate(radius),
                LivingEntity::isAlive);
        for (LivingEntity living : victims) {
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
            living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 400, 1));
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2));
        }
    }

    private static void contagious(Level level, Vec3 pos, float radius) {
        if (level.isClientSide) return;
        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(pos, pos).inflate(radius),
                LivingEntity::isAlive);
        for (LivingEntity living : victims) {
            living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 0));
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, 1));
            living.addEffect(new MobEffectInstance(MobEffects.HUNGER, 300, 1));
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 140, 0));
        }
    }

    private static void breaching(Level level, Vec3 pos, float radius) {
        level.explode(null, pos.x, pos.y, pos.z, radius, false, ExplosionInteraction.BLOCK);
    }

    private static void thermobaric(Level level, Vec3 pos, float radius) {
        blast(level, pos, radius, true);
        blast(level, pos, radius / 2.0F, true);
    }

    private static void sonic(Level level, Vec3 pos, float radius) {
        applyForce(level, pos, radius, 2.5D, true);
        if (!level.isClientSide) {
            AABB area = new AABB(pos, pos).inflate(radius);
            for (BlockPos bp : BlockPos.betweenClosed(BlockPos.containing(area.minX, area.minY, area.minZ),
                    BlockPos.containing(area.maxX, area.maxY, area.maxZ))) {
                if (pos.distanceTo(bp.getCenter()) > radius) continue;
                if (level.getBlockState(bp).isAir()) continue;
                if (level.random.nextFloat() < 0.05F) {
                    level.destroyBlock(bp, false);
                }
            }
        }
    }

    private static void antigravity(Level level, Vec3 pos, float radius) {
        if (level.isClientSide) return;
        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(pos, pos).inflate(radius),
                LivingEntity::isAlive);
        for (LivingEntity living : victims) {
            living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 1));
        }
    }

    private static void emp(Level level, Vec3 pos, float radius) {
        if (level.isClientSide) return;
        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(pos, pos).inflate(radius),
                LivingEntity::isAlive);
        for (LivingEntity living : victims) {
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
        }
    }

    private static void nuclear(Level level, Vec3 pos, float radius) {
        blast(level, pos, radius, true);
        if (!level.isClientSide) {
            List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class,
                    new AABB(pos, pos).inflate(radius + 10),
                    LivingEntity::isAlive);
            for (LivingEntity living : victims) {
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 600, 2));
                living.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1));
            }
        }
    }

    private static void endothermic(Level level, Vec3 pos, float radius) {
        blast(level, pos, radius / 2.0F, false);
        if (!level.isClientSide) {
            BlockPos center = BlockPos.containing(pos);
            for (BlockPos bp : BlockPos.betweenClosed(center.offset(-(int) radius, -1, -(int) radius),
                    center.offset((int) radius, 1, (int) radius))) {
                if (center.distSqr(bp) > radius * radius) continue;
                if (level.random.nextFloat() > 0.2F) continue;
                if (level.getBlockState(bp).isAir()) {
                    level.setBlock(bp, Blocks.SNOW.defaultBlockState(), 11);
                }
            }
        }
    }

    private static void exothermic(Level level, Vec3 pos, float radius) {
        blast(level, pos, radius / 2.0F, true);
        if (!level.isClientSide) {
            BlockPos center = BlockPos.containing(pos);
            for (BlockPos bp : BlockPos.betweenClosed(center.offset(-(int) radius, -1, -(int) radius),
                    center.offset((int) radius, 1, (int) radius))) {
                if (center.distSqr(bp) > radius * radius) continue;
                if (level.random.nextFloat() > 0.15F) continue;
                if (level.getBlockState(bp).isAir() && level.getBlockState(bp.below()).isSolid()) {
                    level.setBlock(bp, Blocks.FIRE.defaultBlockState(), 11);
                } else if (level.random.nextFloat() < 0.05F) {
                    level.setBlock(bp, Blocks.LAVA.defaultBlockState(), 11);
                }
            }
        }
    }

    private static void ender(Level level, Vec3 pos, float radius) {
        if (level.isClientSide) return;
        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(pos, pos).inflate(radius),
                LivingEntity::isAlive);
        for (LivingEntity living : victims) {
            double dx = pos.x + level.random.nextDouble() * radius * 2 - radius;
            double dz = pos.z + level.random.nextDouble() * radius * 2 - radius;
            double dy = pos.y + level.random.nextInt(6) - 3;
            living.teleportTo(dx, dy, dz);
        }
    }

    private static void hypersonic(Level level, Vec3 pos, float radius) {
        applyForce(level, pos, radius, 3.5D, true);
        if (!level.isClientSide) {
            List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class,
                    new AABB(pos, pos).inflate(radius),
                    LivingEntity::isAlive);
            for (LivingEntity living : victims) {
                living.hurt(level.damageSources().explosion(null), 8.0F);
            }
        }
    }

    private static void rejuvenate(Level level, Vec3 pos, float radius) {
        if (level.isClientSide) return;
        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(pos, pos).inflate(radius),
                LivingEntity::isAlive);
        for (LivingEntity living : victims) {
            living.heal(8.0F);
            living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
        }
    }

    private static void antimatter(Level level, Vec3 pos, float radius) {
        blast(level, pos, radius, true);
    }

    private static void darkmatter(Level level, Vec3 pos, float radius) {
        applyForce(level, pos, radius, 2.0D, false);
        if (!level.isClientSide) {
            List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class,
                    new AABB(pos, pos).inflate(radius),
                    LivingEntity::isAlive);
            for (LivingEntity living : victims) {
                living.hurt(level.damageSources().generic(), 6.0F);
            }
        }
    }
}
