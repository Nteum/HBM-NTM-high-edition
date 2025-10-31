package net.mcreator.nuclearcraft.procedures;

import net.mcreator.nuclearcraft.init.BigExplosivesModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/CoordStorerEntitySwingsItemProcedure.class */
public class CoordStorerEntitySwingsItemProcedure {
    public static void execute(LevelAccessor world, ItemStack itemstack) {
        if (world instanceof ServerLevel) {
            ServerLevel _level = (ServerLevel) world;
            Entity entityToSpawn = ((EntityType) BigExplosivesModEntities.FIVE_BOMB.get()).m_262496_(_level, BlockPos.m_274561_(itemstack.m_41784_().m_128459_("itemX"), 400.0d, itemstack.m_41784_().m_128459_("itemZ")), MobSpawnType.MOB_SUMMONED);
            if (entityToSpawn != null) {
                entityToSpawn.m_20334_(0.0d, 0.0d, 0.0d);
            }
        }
    }
}
