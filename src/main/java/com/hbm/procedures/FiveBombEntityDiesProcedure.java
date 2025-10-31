package net.mcreator.nuclearcraft.procedures;

import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.mcreator.nuclearcraft.init.BigExplosivesModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/FiveBombEntityDiesProcedure.class */
public class FiveBombEntityDiesProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z) {
        BigExplosivesMod.queueServerWork(2, () -> {
            if (world instanceof ServerLevel) {
                ServerLevel _level = (ServerLevel) world;
                Entity entityToSpawn = ((EntityType) BigExplosivesModEntities.FIVE_HUNDRED_KG_EXPLOSION.get()).m_262496_(_level, BlockPos.m_274561_(x, y, z), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.m_20334_(0.0d, 0.0d, 0.0d);
                }
            }
        });
    }
}
