package net.mcreator.nuclearcraft.procedures;

import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/ItemTakenFromAdvancedOutputProcedure.class */
public class ItemTakenFromAdvancedOutputProcedure {
    public static double execute(Entity entity) {
        if (entity == null) {
            return 0.0d;
        }
        double slor = 0.0d;
        for (int index0 = 0; index0 < 16; index0++) {
            if (entity instanceof Player) {
                Player _player = (Player) entity;
                Supplier supplier = _player.f_36096_;
                if (supplier instanceof Supplier) {
                    Supplier _current = supplier;
                    Object obj = _current.get();
                    if (obj instanceof Map) {
                        Map _slots = (Map) obj;
                        ((Slot) _slots.get(Integer.valueOf((int) slor))).m_6201_(1);
                        _player.f_36096_.m_38946_();
                    }
                }
            }
            slor += 1.0d;
        }
        return 0.0d;
    }
}
