package net.mcreator.nuclearcraft.procedures;

import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.mcreator.nuclearcraft.init.BigExplosivesModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/NukeTestEntityFallsProcedure.class */
public class NukeTestEntityFallsProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z) {
        if (world instanceof Level) {
            Level _level = (Level) world;
            if (!_level.m_5776_()) {
                _level.m_5594_((Player) null, BlockPos.m_274561_(x, y, z), (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:medium_bomb")), SoundSource.NEUTRAL, 100.0f, 1.0f);
            } else {
                _level.m_7785_(x, y, z, (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:medium_bomb")), SoundSource.NEUTRAL, 100.0f, 1.0f, false);
            }
        }
        if (world instanceof Level) {
            Level _level2 = (Level) world;
            if (!_level2.m_5776_()) {
                _level2.m_254849_((Entity) null, x, y, z, 8.0f, Level.ExplosionInteraction.TNT);
            }
        }
        BigExplosivesMod.queueServerWork(1, () -> {
            if (world instanceof ServerLevel) {
                ServerLevel _level3 = (ServerLevel) world;
                Entity entityToSpawn = ((EntityType) BigExplosivesModEntities.TWO_HUNDRED_FIFTY_KG_EXPLOSION.get()).m_262496_(_level3, BlockPos.m_274561_(x, y, z), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.m_20334_(0.0d, 0.0d, 0.0d);
                }
            }
        });
    }
}
