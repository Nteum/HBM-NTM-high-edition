package net.mcreator.nuclearcraft.procedures;

import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/ItemTakenFromBasicOutputSlotProcedure.class */
public class ItemTakenFromBasicOutputSlotProcedure {
    public static void execute(Entity entity) {
        if (entity == null) {
            return;
        }
        double slot = 0.0d;
        for (int index0 = 0; index0 < 7; index0++) {
            if (entity instanceof Player) {
                Player _player = (Player) entity;
                Supplier supplier = _player.f_36096_;
                if (supplier instanceof Supplier) {
                    Supplier _current = supplier;
                    Object obj = _current.get();
                    if (obj instanceof Map) {
                        Map _slots = (Map) obj;
                        ((Slot) _slots.get(Integer.valueOf((int) slot))).m_6201_(1);
                        _player.f_36096_.m_38946_();
                    }
                }
            }
            slot += 1.0d;
        }
    }
}
