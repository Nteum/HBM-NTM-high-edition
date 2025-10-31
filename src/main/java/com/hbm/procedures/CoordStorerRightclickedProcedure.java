package net.mcreator.nuclearcraft.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/CoordStorerRightclickedProcedure.class */
public class CoordStorerRightclickedProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
        if (entity == null) {
            return;
        }
        if (world.m_46861_(BlockPos.m_274561_(x, y, z))) {
            itemstack.m_41784_().m_128347_("itemX", x);
            itemstack.m_41784_().m_128347_("itemZ", z);
            double dM_128459_ = itemstack.m_41784_().m_128459_("itemX");
            itemstack.m_41784_().m_128459_("itemZ");
            itemstack.m_41714_(Component.m_237113_(" X: " + dM_128459_ + " Z: " + itemstack));
            if (entity instanceof Player) {
                Player _player = (Player) entity;
                if (!_player.m_9236_().m_5776_()) {
                    double dM_128459_2 = itemstack.m_41784_().m_128459_("itemX");
                    itemstack.m_41784_().m_128459_("itemZ");
                    _player.m_5661_(Component.m_237113_(" X: " + dM_128459_2 + " Z: " + _player), false);
                    return;
                }
                return;
            }
            return;
        }
        if (entity instanceof Player) {
            Player _player2 = (Player) entity;
            if (!_player2.m_9236_().m_5776_()) {
                _player2.m_5661_(Component.m_237113_("No satilites found"), false);
            }
        }
    }
}
