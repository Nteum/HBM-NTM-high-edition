package com.hbm.entity.weapon.missile;

import com.hbm.api.badthing.ContaminationUtil;

import com.hbm.compat.ballistix.BallistixExplosiveType;
import com.hbm.compat.ballistix.BallistixExplosionHandlers;
import com.hbm.config.ConfigBomb;
import com.hbm.entity.effect.EntityBlackHole;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.entity.ModEntityType;
import com.hbm.explosion.ExplosionChaos;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.AABB;
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
        private Payload payload = Payload.TEST;

        public EntityMissileTest(EntityType<? extends ThrowableProjectile> pEntityType, Level world) {
            super(pEntityType, world);
        }
        public EntityMissileTest(Level world, float x, float y, float z, BlockPos target) {
            super(ModEntityType.ENTITY_MISSILE_TEST.get(), world, x, y, z, target);
        }
        public EntityMissileTest(Level world, float x, float y, float z, BlockPos target, Payload payload) {
            super(ModEntityType.ENTITY_MISSILE_TEST.get(), world, x, y, z, target);
            this.payload = payload == null ? Payload.TEST : payload;
        }

        public static EntityMissileTest create(Level world, float x, float y, float z, BlockPos target, Payload payload) {
            return new EntityMissileTest(world, x, y, z, target, payload);
        }

        public EntityMissileTest setPayload(Payload payload) {
            this.payload = payload == null ? Payload.TEST : payload;
            return this;
        }

        public Payload getPayload() {
            return this.payload;
        }

        @Override public ItemStack getDebrisRareDrop() { return null; }
        @Override public ItemStack getMissileItemForInfo() {
            return ItemStack.EMPTY;
//            return new ItemStack(HBMWeapon.MISSILE_TEST.get());
        }

        @Override public void onMissileImpact(HitResult mop) {
            if (level().isClientSide) {
                return;
            }
            Vec3 loc = mop.getLocation();
            switch (payload) {
                case TEST -> legacyTestContamination(loc);
                case GENERIC -> detonate(BallistixExplosiveType.OBSIDIAN, loc);
                case DECOY -> {
                    level().explode(this, loc.x, loc.y, loc.z, 4.0F, false, Level.ExplosionInteraction.BLOCK);
                    detonate(BallistixExplosiveType.CONDENSIVE, loc);
                }
                case MICRO -> spawnNuclearPayload(loc, Math.max(25, ConfigBomb.nukaRadius), false, 0.75F);
                case STRONG -> {
                    detonate(BallistixExplosiveType.THERMOBARIC, loc);
                    detonate(BallistixExplosiveType.FRAGMENTATION, loc);
                }
                case BUSTER -> drillingImpact(loc, 15, 4.8F, false);
                case BUSTER_STRONG -> drillingImpact(loc, 20, 6.5F, false);
                case DRILL -> drillingImpact(loc, 30, 7.0F, false);
                case BURST -> detonate(BallistixExplosiveType.FRAGMENTATION, loc);
                case CLUSTER -> clusterImpact(loc, 10, 12.0D, false);
                case CLUSTER_STRONG -> clusterImpact(loc, 18, 18.0D, false);
                case RAIN -> clusterImpact(loc, 34, 26.0D, true);
                case EMP -> detonate(BallistixExplosiveType.EMP, loc);
                case EMP_STRONG -> {
                    detonate(BallistixExplosiveType.EMP, loc);
                    detonate(BallistixExplosiveType.SONIC, loc);
                }
                case INCENDIARY -> detonate(BallistixExplosiveType.INCENDIARY, loc);
                case INCENDIARY_STRONG -> {
                    detonate(BallistixExplosiveType.INCENDIARY, loc);
                    detonate(BallistixExplosiveType.EXOTHERMIC, loc);
                    ExplosionChaos.flameDeath(level(), (int) Math.floor(loc.x), (int) Math.floor(loc.y), (int) Math.floor(loc.z), 20);
                }
                case INFERNO -> {
                    detonate(BallistixExplosiveType.EXOTHERMIC, loc);
                    detonate(BallistixExplosiveType.THERMOBARIC, loc);
                    ExplosionChaos.burn(level(), (int) Math.floor(loc.x), (int) Math.floor(loc.y), (int) Math.floor(loc.z), 12);
                    ExplosionChaos.flameDeath(level(), (int) Math.floor(loc.x), (int) Math.floor(loc.y), (int) Math.floor(loc.z), 26);
                }
                case STEALTH -> detonate(BallistixExplosiveType.HYPERSONIC, loc);
                case SCHRABIDIUM -> {
                    detonate(BallistixExplosiveType.LARGE_ANTIMATTER, loc);
                    detonate(BallistixExplosiveType.DARKMATTER, loc);
                    if (level() instanceof ServerLevel server) {
                        server.sendParticles(ParticleTypes.FLASH, loc.x, loc.y + 0.5D, loc.z, 6, 0.35D, 0.2D, 0.35D, 0.0D);
                    }
                }
                case BHOLE -> {
                    detonate(BallistixExplosiveType.DARKMATTER, loc);
                    EntityBlackHole blackHole = new EntityBlackHole(level(), 2.2F);
                    blackHole.setPos(loc.x, loc.y + 0.5D, loc.z);
                    level().addFreshEntity(blackHole);
                }
                case TAINT -> {
                    detonate(BallistixExplosiveType.CONTAGIOUS, loc);
                    spreadTaint(loc, 120, 6);
                }
                case VOLCANO -> volcanoImpact(loc);
                case NUCLEAR -> spawnNuclearPayload(loc, ConfigBomb.missileRadius, false, 1.0F);
                case NUCLEAR_CLUSTER -> spawnNuclearPayload(loc,
                        Math.max(ConfigBomb.mikeRadius, Math.max(ConfigBomb.missileRadius * 2, ConfigBomb.mirvRadius * 2)),
                        false,
                        1.0F);
                case DOOMSDAY -> {
                    spawnNuclearPayload(loc, Math.max(ConfigBomb.tsarRadius, ConfigBomb.missileRadius * 2), true, 1.0F);
                    applyFallout(loc, 96, 1800.0F, 14.0F);
                }
                case DOOMSDAY_RUSTED -> {
                    spawnNuclearPayload(loc, Math.max(ConfigBomb.missileRadius, ConfigBomb.manRadius), true, 1.0F);
                    applyFallout(loc, 60, 900.0F, 7.0F);
                }
                case REJUVENATION -> detonate(BallistixExplosiveType.REJUVINATION, loc);
                case SHUTTLE -> shuttleImpact(loc);
                case SOYUZ -> soyuzImpact(loc);
            }
        }

        private void detonate(BallistixExplosiveType explosiveType, Vec3 loc) {
            BallistixExplosionHandlers.detonate(explosiveType, level(), loc, this.getOwner());
        }

        private void scatter(BallistixExplosiveType explosiveType, Vec3 loc, int count, double spread) {
            for (int i = 0; i < count; i++) {
                Vec3 offset = loc.add(
                        (level().random.nextDouble() - 0.5D) * spread,
                        0.05D + level().random.nextDouble() * 0.8D,
                        (level().random.nextDouble() - 0.5D) * spread);
                BallistixExplosionHandlers.detonate(explosiveType, level(), offset, this.getOwner());
            }
        }

        private void drillingImpact(Vec3 loc, int depth, float basePower, boolean fire) {
            for (int i = 0; i < depth; i++) {
                float power = Math.max(3.0F, basePower - i * 0.15F);
                level().explode(this, loc.x, loc.y - i, loc.z, power, fire, Level.ExplosionInteraction.BLOCK);
                if (i % 4 == 0) {
                    BallistixExplosionHandlers.detonate(BallistixExplosiveType.BREACHING, level(), new Vec3(loc.x, loc.y - i, loc.z), this.getOwner());
                }
            }
            scatter(BallistixExplosiveType.SHRAPNEL, loc, Math.max(6, depth / 2), 8.0D);
        }

        private void clusterImpact(Vec3 loc, int count, double spread, boolean fiery) {
            level().explode(this, loc.x, loc.y, loc.z, fiery ? 20.0F : 10.0F, fiery, Level.ExplosionInteraction.BLOCK);
            detonate(BallistixExplosiveType.FRAGMENTATION, loc);
            for (int i = 0; i < count; i++) {
                Vec3 offset = loc.add(
                        (level().random.nextDouble() - 0.5D) * spread,
                        0.15D + level().random.nextDouble() * 1.2D,
                        (level().random.nextDouble() - 0.5D) * spread);
                BallistixExplosiveType type;
                if (fiery && i % 3 == 0) {
                    type = BallistixExplosiveType.EXOTHERMIC;
                } else if (i % 4 == 0) {
                    type = BallistixExplosiveType.CONDENSIVE;
                } else {
                    type = BallistixExplosiveType.SHRAPNEL;
                }
                BallistixExplosionHandlers.detonate(type, level(), offset, this.getOwner());
            }
        }

        private void volcanoImpact(Vec3 loc) {
            detonate(BallistixExplosiveType.EXOTHERMIC, loc);
            detonate(BallistixExplosiveType.THERMOBARIC, loc);
            BlockPos center = BlockPos.containing(loc);
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    int maxY = (x == 0 && z == 0) ? 2 : 1;
                    for (int y = -1; y <= maxY; y++) {
                        BlockPos pos = center.offset(x, y, z);
                        if (level().getBlockState(pos).is(Blocks.BEDROCK)) {
                            continue;
                        }
                        level().setBlock(pos, Blocks.LAVA.defaultBlockState(), 11);
                    }
                }
            }
            level().setBlock(center, Blocks.MAGMA_BLOCK.defaultBlockState(), 11);
        }

        private void spreadTaint(Vec3 loc, int attempts, int radius) {
            BlockPos center = BlockPos.containing(loc);
            for (int i = 0; i < attempts; i++) {
                int dx = level().random.nextInt(radius * 2 + 1) - radius;
                int dy = level().random.nextInt(7) - 3;
                int dz = level().random.nextInt(radius * 2 + 1) - radius;
                BlockPos target = center.offset(dx, dy, dz);
                if (!level().isLoaded(target)) {
                    continue;
                }
                if (level().getBlockState(target).isAir()) {
                    continue;
                }
                if (!level().getBlockState(target).isSolidRender(level(), target)) {
                    continue;
                }
                level().setBlock(target, ModBlocks.TAINT.get().defaultBlockState(), 11);
            }
        }

        private void applyFallout(Vec3 loc, int radius, float chunkRad, float directDose) {
            BlockPos center = BlockPos.containing(loc);
            for (int dx = -radius; dx <= radius; dx += 8) {
                for (int dz = -radius; dz <= radius; dz += 8) {
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    if (dist > radius) {
                        continue;
                    }
                    float falloff = (float) (1.0D - dist / radius);
                    float rad = chunkRad * Math.max(0.1F, falloff);
                    ChunkRadiationManager.proxy.incrementRad(level(), center.offset(dx, 0, dz), rad);
                }
            }

            List<LivingEntity> victims = level().getEntitiesOfClass(
                    LivingEntity.class,
                    new AABB(loc, loc).inflate(radius + 20.0D),
                    LivingEntity::isAlive);
            for (LivingEntity living : victims) {
                double dist = living.position().distanceTo(loc);
                float falloff = (float) Math.max(0.1D, 1.0D - dist / (radius + 20.0D));
                ContaminationUtil.contaminate(living,
                        ContaminationUtil.HazardType.RADIATION,
                        ContaminationUtil.ContaminationType.RAD_BYPASS,
                        directDose * falloff);
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 24, 2));
                living.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * 8, 1));
            }
        }

        private void spawnNuclearPayload(Vec3 loc, int configuredRadius, boolean antimatterShock, float torexScaleMul) {
            int radius = Math.max(25, configuredRadius);
            level().playSound(null, loc.x, loc.y, loc.z, ModSounds.WEAPON_NUCLEAR_EXPLOSION.get(), SoundSource.HOSTILE,
                    antimatterShock ? 6.0F : 5.0F,
                    antimatterShock ? 0.78F : 1.0F);
            if (!ConfigBomb.allowNukes) {
                level().explode(this, loc.x, loc.y, loc.z, Math.min(8.0F, radius / 12.0F), true, Level.ExplosionInteraction.TNT);
                return;
            }
            EntityNukeExplosionMK5 nuke = EntityNukeExplosionMK5.statFac(level(), radius, loc);
            level().addFreshEntity(nuke);
            float cloudScale = Math.max(18.0F, radius * (antimatterShock ? Math.max(torexScaleMul, 1.0F) : torexScaleMul));
            level().addFreshEntity(new EntityNukeTorex(level(), loc.add(0.0D, 4.5D, 0.0D), cloudScale));
        }

        private void shuttleImpact(Vec3 loc) {
            level().explode(this, loc.x, loc.y, loc.z, 20.0F, false, Level.ExplosionInteraction.BLOCK);
            if (level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, loc.x, loc.y + 1.0D, loc.z, 180, 2.8D, 1.4D, 2.8D, 0.04D);
                server.sendParticles(ParticleTypes.EXPLOSION_EMITTER, loc.x, loc.y + 1.0D, loc.z, 8, 1.0D, 0.5D, 1.0D, 0.0D);
            }
            level().playSound(null, loc.x, loc.y, loc.z, ModSounds.WEAPON_ROBIN_EXPLOSION.get(), SoundSource.HOSTILE, 4.0F, 0.72F);
        }

        private void soyuzImpact(Vec3 loc) {
            level().explode(this, loc.x, loc.y, loc.z, 24.0F, true, Level.ExplosionInteraction.BLOCK);
            detonate(BallistixExplosiveType.THERMOBARIC, loc);
            if (level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.FLAME, loc.x, loc.y + 0.5D, loc.z, 220, 3.5D, 1.2D, 3.5D, 0.06D);
            }
        }

        private void legacyTestContamination(Vec3 loc) {
            level().explode(this, loc.x, loc.y, loc.z, 6.0F, false, Level.ExplosionInteraction.BLOCK);
            if (ModBlocks.SELLAFIELD_SLAKED == null) {
                return;
            }
            BlockPos center = BlockPos.containing(loc);
            int range = 10;
            for (BlockPos pos : BlockPos.betweenClosed(center.offset(-range, -range, -range), center.offset(range, range, range))) {
                if (center.distSqr(pos) > range * range) {
                    continue;
                }
                if (!level().isLoaded(pos)) {
                    continue;
                }
                if (level().getBlockState(pos).isAir()) {
                    continue;
                }
                if (level().random.nextFloat() > 0.35F) {
                    continue;
                }
                level().setBlock(pos, ModBlocks.SELLAFIELD_SLAKED.get().defaultBlockState(), 11);
            }
        }

        @Override
        protected void readAdditionalSaveData(CompoundTag nbt) {
            super.readAdditionalSaveData(nbt);
            this.payload = Payload.byId(nbt.getString("legacyPayload"));
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag nbt) {
            super.addAdditionalSaveData(nbt);
            nbt.putString("legacyPayload", this.payload.id());
        }

        public enum Payload {
            TEST("test"),
            GENERIC("generic"),
            DECOY("decoy"),
            MICRO("micro"),
            STRONG("strong"),
            BUSTER("buster"),
            BUSTER_STRONG("buster_strong"),
            DRILL("drill"),
            BURST("burst"),
            CLUSTER("cluster"),
            CLUSTER_STRONG("cluster_strong"),
            EMP("emp"),
            EMP_STRONG("emp_strong"),
            INCENDIARY("incendiary"),
            INCENDIARY_STRONG("incendiary_strong"),
            INFERNO("inferno"),
            STEALTH("stealth"),
            SCHRABIDIUM("schrabidium"),
            BHOLE("bhole"),
            TAINT("taint"),
            VOLCANO("volcano"),
            RAIN("rain"),
            NUCLEAR("nuclear"),
            NUCLEAR_CLUSTER("nuclear_cluster"),
            DOOMSDAY("doomsday"),
            DOOMSDAY_RUSTED("doomsday_rusted"),
            SHUTTLE("shuttle"),
            SOYUZ("soyuz"),
            REJUVENATION("rejuvenation");

            private final String id;

            Payload(String id) {
                this.id = id;
            }

            public String id() {
                return id;
            }

            public static Payload byId(String id) {
                if (id == null || id.isEmpty()) {
                    return TEST;
                }
                for (Payload payload : values()) {
                    if (payload.id.equals(id)) {
                        return payload;
                    }
                }
                return TEST;
            }
        }
    }
}
