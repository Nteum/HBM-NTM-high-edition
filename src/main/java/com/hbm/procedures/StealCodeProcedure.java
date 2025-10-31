package net.mcreator.nuclearcraft.procedures;

import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.mcreator.nuclearcraft.network.BigExplosivesModVariables;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/StealCodeProcedure.class */
public class StealCodeProcedure {
    public static void execute(Entity entity) {
        if (entity == null) {
            return;
        }
        BigExplosivesMod.LOGGER.info(Double.valueOf(((BigExplosivesModVariables.PlayerVariables) entity.getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).orElse(new BigExplosivesModVariables.PlayerVariables())).fadevariable));
    }
}
