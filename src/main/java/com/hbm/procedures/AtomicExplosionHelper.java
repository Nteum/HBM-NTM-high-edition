package com.hbm.procedures;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbm.compat.bigexplosives.BigExplosivesMod;
import com.hbm.init.BigExplosivesModEntities;
import com.hbm.init.BigExplosivesModSounds;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toclient.S2CAtomicFlashPacket;
import com.hbm.render.entity.AtomicBombExplosionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class AtomicExplosionHelper {

    private static final double DEFAULT_DAMAGE = 2000.0D;
    private static final double EFFECT_RADIUS = 140.0D;

    private AtomicExplosionHelper() {
    }

    static void detonate(LevelAccessor world, double x, double y, double z, Entity source) {
        if (!(world instanceof Level level) || level.isClientSide) {
            return;
        }

        double baseDamage = readConfiguredDamage();
        Vec3 center = new Vec3(x, y, z);

        level.explode(source, x, y, z, 60.0F, ExplosionInteraction.TNT);
        triggerEffects(level, center, baseDamage);
    }

    /**
     * Plays the lingering atomic flash visuals and applies screen-darkening
     * potion effects without spawning another explosion. Use this when a
     * machine performs its own damage logic but should reuse the shared
     * nuclear shockwave presentation.
     */
    public static void triggerEffects(Level level, Vec3 center) {
        if (level == null || level.isClientSide) {
            return;
        }
        double baseDamage = readConfiguredDamage();
        triggerEffects(level, center, baseDamage);
    }

    private static void triggerEffects(Level level, Vec3 center, double baseDamage) {
        if (level == null || level.isClientSide) {
            return;
        }
        playBlastSounds(level, center);
        spawnExplosionEntity(level, center);
        applyEffects(level, center, baseDamage);
    }

    private static void playBlastSounds(Level level, Vec3 center) {
        level.playSound(null, BlockPos.containing(center), BigExplosivesModSounds.BOOM.get(), SoundSource.NEUTRAL, 80.0F, 1.0F);
        level.playSound(null, BlockPos.containing(center), BigExplosivesModSounds.ATOM_BOMB_CLOSE.get(), SoundSource.NEUTRAL, 200.0F, 1.0F);
        BigExplosivesMod.queueServerWork(2, () ->
                level.playSound(null, BlockPos.containing(center), BigExplosivesModSounds.ATOM_BOMB_FAR.get(), SoundSource.NEUTRAL, 400.0F, 1.0F));
        BigExplosivesMod.queueServerWork(4, () ->
                level.playSound(null, BlockPos.containing(center), BigExplosivesModSounds.ATOM_BOMB_EXTREMELY_FAR.get(), SoundSource.NEUTRAL, 600.0F, 1.0F));
        BigExplosivesMod.queueServerWork(6, () ->
                level.playSound(null, BlockPos.containing(center), BigExplosivesModSounds.SUPER_FAR_EXPLOSION.get(), SoundSource.NEUTRAL, 1000.0F, 1.0F));
    }

    private static void spawnExplosionEntity(Level level, Vec3 center) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        AtomicBombExplosionEntity explosion = BigExplosivesModEntities.ATOMIC_BOMB_EXPLOSION.get().create(serverLevel);
        if (explosion != null) {
            explosion.moveTo(center.x, center.y - 3.0D, center.z, 0.0F, 0.0F);
            serverLevel.addFreshEntity(explosion);
        }
    }

    private static void applyEffects(Level level, Vec3 center, double baseDamage) {
        AABB area = new AABB(center, center).inflate(EFFECT_RADIUS);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAlive);
        for (LivingEntity target : targets) {
            double distance = Math.sqrt(target.distanceToSqr(center));
            double factor = Math.max(0.0D, 1.0D - (distance / EFFECT_RADIUS));
            if (factor <= 0.0D) {
                continue;
            }

            float damage = (float) (baseDamage * factor * factor);
            if (damage > 0.0F) {
                DamageSource source = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(DamageTypes.EXPLOSION));
                target.hurt(source, damage);
            }

            int nauseaDuration = (int) Mth.clamp(60 + 140 * factor, 40, 200);
            int witherDuration = (int) (6000 * factor);

            if (nauseaDuration > 0) {
                target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, nauseaDuration, 0, false, false));
            }
            if (witherDuration > 0) {
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, Math.max(200, witherDuration), 0, false, true));
            }

            if (target instanceof ServerPlayer serverPlayer) {
                float alpha = Mth.clamp(0.35F + 0.65F * (float) factor, 0.2F, 1.0F);
                int flashDuration = (int) Mth.clamp(60 + (200 * factor), 60, 260);
                ModMessages.sendToPlayer(new S2CAtomicFlashPacket(alpha, flashDuration), serverPlayer);
            }
        }
    }

    private static double readConfiguredDamage() {
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("bigexplosivesconfig.json");
        if (!Files.exists(configPath)) {
            return DEFAULT_DAMAGE;
        }
        try (Reader reader = Files.newBufferedReader(configPath)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            if (json.has("AtomicBombDamage")) {
                return json.get("AtomicBombDamage").getAsDouble();
            }
        } catch (IOException | IllegalStateException ex) {
            BigExplosivesMod.LOGGER.warn("Failed to read bigexplosivesconfig.json", ex);
        }
        return DEFAULT_DAMAGE;
    }
}
