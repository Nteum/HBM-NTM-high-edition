package com.hbm.compat.ballistix;

import com.hbm.config.ConfigBomb;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.particle.ModParticleTypes;
import com.hbm.registries.ModSounds;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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

public final class BallistixExplosionHandlers {

    private BallistixExplosionHandlers() {
    }

    public static void detonate(BallistixExplosiveType type, Level level, Vec3 pos, Entity owner) {
        playSignatureSound(type, level, pos);
        spawnSignatureParticles(type, level, pos);
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
        if (!(level instanceof ServerLevel server)) {
            return;
        }
        for (int i = 0; i < 6; i++) {
            server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.x, pos.y + 0.4D, pos.z, 30, radius, 0.45D, radius, 0.01D);
            server.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, pos.x, pos.y + 0.8D, pos.z, 14, radius * 0.7D, 0.5D, radius * 0.7D, 0.0D);
        }
        List<LivingEntity> victims = server.getEntitiesOfClass(LivingEntity.class,
                new AABB(pos, pos).inflate(radius));
        for (LivingEntity living : victims) {
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 360, 1));
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 360, 1));
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 360, 0));
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
            living.hurt(level.damageSources().magic(), 2.0F);
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
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.DRAGON_BREATH, pos.x, pos.y + 0.5D, pos.z, 80, radius * 0.8D, 0.6D, radius * 0.8D, 0.01D);
            server.sendParticles(ParticleTypes.MYCELIUM, pos.x, pos.y + 0.5D, pos.z, 60, radius, 0.75D, radius, 0.01D);
        }
        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(pos, pos).inflate(radius),
                LivingEntity::isAlive);
        for (LivingEntity living : victims) {
            living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 0));
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, 1));
            living.addEffect(new MobEffectInstance(MobEffects.HUNGER, 300, 1));
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 140, 0));
            living.hurt(level.damageSources().magic(), 3.0F);
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
        if (!level.isClientSide) {
            int nukeRadius = Math.max(25, Math.round(radius));
            if (ConfigBomb.allowNukes) {
                level.addFreshEntity(EntityNukeExplosionMK5.statFac(level, nukeRadius, pos));
            } else {
                blast(level, pos, Math.min(8.0F, nukeRadius / 12.0F), true);
            }
            if (level instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.FLASH, pos.x, pos.y + 1.0D, pos.z, 4, 0.2D, 0.2D, 0.2D, 0.0D);
                server.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.x, pos.y + 1.5D, pos.z, 6, 0.8D, 0.5D, 0.8D, 0.0D);
                server.sendParticles(ModParticleTypes.HBM_SMOKE.get(), pos.x, pos.y + 0.5D, pos.z, 220, radius * 0.15D, radius * 0.07D, radius * 0.15D, 0.03D);
            }
            level.addFreshEntity(new EntityNukeTorex(level, pos.add(0.0D, 4.5D, 0.0D), Math.max(18.0F, nukeRadius)));
            List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class,
                    new AABB(pos, pos).inflate(radius + 10),
                    LivingEntity::isAlive);
            for (LivingEntity living : victims) {
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 600, 2));
                living.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1));
                living.hurt(level.damageSources().explosion(null), 16.0F);
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

    private static void spawnSignatureParticles(BallistixExplosiveType type, Level level, Vec3 pos) {
        if (!(level instanceof ServerLevel server)) {
            return;
        }
        switch (type) {
            case CONDENSIVE, LANDMINE, OBSIDIAN -> {
                burst(server, ParticleTypes.EXPLOSION, pos, 22, 0.9D, 0.0D);
                burst(server, ParticleTypes.SMOKE, pos, 45, 1.3D, 0.01D);
            }
            case INCENDIARY, EXOTHERMIC -> {
                burst(server, ParticleTypes.FLAME, pos, 80, 1.8D, 0.04D);
                burst(server, ParticleTypes.LAVA, pos, 20, 1.2D, 0.04D);
                burst(server, ParticleTypes.LARGE_SMOKE, pos, 50, 1.6D, 0.01D);
            }
            case CHEMICAL -> {
                burst(server, ParticleTypes.SPORE_BLOSSOM_AIR, pos, 75, 2.0D, 0.0D);
                burst(server, ParticleTypes.CAMPFIRE_COSY_SMOKE, pos, 70, 2.2D, 0.01D);
            }
            case CONTAGIOUS -> {
                burst(server, ParticleTypes.DRAGON_BREATH, pos, 60, 2.0D, 0.01D);
                burst(server, ParticleTypes.MYCELIUM, pos, 60, 2.2D, 0.01D);
            }
            case BREACHING -> {
                burst(server, ParticleTypes.CLOUD, pos, 45, 1.2D, 0.02D);
                burst(server, ParticleTypes.CRIT, pos, 40, 1.0D, 0.2D);
            }
            case THERMOBARIC -> {
                burst(server, ParticleTypes.EXPLOSION_EMITTER, pos, 3, 1.4D, 0.0D);
                burst(server, ParticleTypes.FLAME, pos, 120, 2.7D, 0.06D);
                burst(server, ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, pos, 65, 2.4D, 0.03D);
            }
            case SONIC, HYPERSONIC -> {
                burst(server, ParticleTypes.SONIC_BOOM, pos, 1, 0.1D, 0.0D);
                burst(server, ParticleTypes.CLOUD, pos, 70, 2.5D, 0.04D);
            }
            case ANTIGRAVITY -> {
                burst(server, ParticleTypes.REVERSE_PORTAL, pos, 90, 2.3D, 0.4D);
                burst(server, ParticleTypes.END_ROD, pos, 40, 1.8D, 0.03D);
            }
            case EMP -> {
                burst(server, ParticleTypes.ELECTRIC_SPARK, pos, 120, 2.8D, 0.08D);
                burst(server, ParticleTypes.END_ROD, pos, 40, 1.5D, 0.03D);
            }
            case NUCLEAR -> {
                burst(server, ParticleTypes.FLASH, pos, 5, 0.2D, 0.0D);
                burst(server, ParticleTypes.EXPLOSION_EMITTER, pos, 8, 1.0D, 0.0D);
                burst(server, ModParticleTypes.HBM_SMOKE.get(), pos, 260, 3.0D, 0.04D);
            }
            case ENDOTHERMIC -> {
                burst(server, ParticleTypes.SNOWFLAKE, pos, 90, 2.0D, 0.01D);
                burst(server, ParticleTypes.CLOUD, pos, 60, 1.8D, 0.02D);
            }
            case ENDER -> {
                burst(server, ParticleTypes.PORTAL, pos, 110, 2.2D, 0.15D);
                burst(server, ParticleTypes.REVERSE_PORTAL, pos, 40, 1.7D, 0.1D);
            }
            case ANTIMATTER, LARGE_ANTIMATTER -> {
                burst(server, ParticleTypes.REVERSE_PORTAL, pos, 200, 3.4D, 0.2D);
                burst(server, ParticleTypes.FLASH, pos, 8, 0.3D, 0.0D);
                burst(server, ParticleTypes.EXPLOSION_EMITTER, pos, 12, 1.6D, 0.0D);
            }
            case DARKMATTER -> {
                burst(server, ParticleTypes.PORTAL, pos, 130, 2.6D, 0.2D);
                burst(server, ParticleTypes.ASH, pos, 80, 2.2D, 0.01D);
            }
            case SHRAPNEL, FRAGMENTATION -> {
                burst(server, ParticleTypes.CRIT, pos, 120, 2.1D, 0.7D);
                burst(server, ParticleTypes.SMOKE, pos, 50, 1.4D, 0.01D);
            }
            case ATTRACTIVE, REPULSIVE -> {
                burst(server, ParticleTypes.CLOUD, pos, 70, 2.0D, 0.02D);
                burst(server, ParticleTypes.END_ROD, pos, 35, 1.6D, 0.03D);
            }
            case ANVIL, INFESTIVE, DEBILITATION, REJUVINATION -> {
                burst(server, ParticleTypes.EXPLOSION, pos, 15, 0.8D, 0.0D);
                burst(server, ParticleTypes.CLOUD, pos, 36, 1.2D, 0.01D);
            }
            default -> burst(server, ParticleTypes.EXPLOSION, pos, 12, 0.8D, 0.0D);
        }
    }

    private static void burst(ServerLevel server, ParticleOptions particle, Vec3 pos, int count, double spread, double speed) {
        server.sendParticles(particle, pos.x, pos.y, pos.z, count, spread, spread * 0.45D, spread, speed);
    }

    private static void playSignatureSound(BallistixExplosiveType type, Level level, Vec3 pos) {
        if (level.isClientSide) {
            return;
        }
        SoundEvent sound = switch (type) {
            case NUCLEAR -> ModSounds.WEAPON_NUCLEAR_EXPLOSION.get();
            case ANTIMATTER, LARGE_ANTIMATTER, DARKMATTER -> ModSounds.WEAPON_MUKE_EXPLOSION.get();
            case THERMOBARIC, EXOTHERMIC -> ModSounds.WEAPON_EXPLOSION_LARGE_NEAR.get();
            case ENDOTHERMIC -> ModSounds.WEAPON_EXPLOSION_SMALL_FAR.get();
            case EMP -> ModSounds.WEAPON_TESLA_SHOOT.get();
            case CHEMICAL, CONTAGIOUS -> ModSounds.ITEM_SPRAY.get();
            case SONIC, HYPERSONIC -> ModSounds.BLOCK_SONAR_PING.get();
            case ANTIGRAVITY, ATTRACTIVE, REPULSIVE, ENDER -> ModSounds.WEAPON_SING_FLYBY.get();
            case SHRAPNEL, FRAGMENTATION -> ModSounds.WEAPON_EXPLOSION_MEDIUM.get();
            case BREACHING, CONDENSIVE, LANDMINE, OBSIDIAN, ANVIL, INFESTIVE, DEBILITATION -> ModSounds.WEAPON_EXPLOSION_SMALL_NEAR.get();
            case INCENDIARY -> ModSounds.WEAPON_FLAMETHROWER_IGNITE.get();
            case REJUVINATION -> ModSounds.ITEM_RADAWAY.get();
        };

        float volume = switch (type) {
            case NUCLEAR, LARGE_ANTIMATTER, DARKMATTER -> 6.0F;
            case THERMOBARIC, ANTIMATTER -> 4.5F;
            case SONIC, HYPERSONIC -> 3.0F;
            default -> 2.2F;
        };
        float pitch = switch (type) {
            case CHEMICAL, CONTAGIOUS, REJUVINATION -> 1.15F;
            case NUCLEAR, LARGE_ANTIMATTER, DARKMATTER -> 0.8F;
            case EMP -> 1.25F;
            default -> 1.0F;
        };

        level.playSound(null, pos.x, pos.y, pos.z, sound, SoundSource.HOSTILE, volume, pitch);
    }
}
