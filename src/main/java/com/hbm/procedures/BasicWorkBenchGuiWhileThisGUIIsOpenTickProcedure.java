package net.mcreator.nuclearcraft.procedures;

import java.util.Map;
import java.util.function.Supplier;
import net.mcreator.nuclearcraft.init.BigExplosivesModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/BasicWorkBenchGuiWhileThisGUIIsOpenTickProcedure.class */
public class BasicWorkBenchGuiWhileThisGUIIsOpenTickProcedure {
    public static void execute(Entity entity) {
        if (entity == null) {
            return;
        }
        if (TenKgBombCutomRecipeProcedure.execute(entity)) {
            if (entity instanceof Player) {
                Player _player = (Player) entity;
                Supplier supplier = _player.f_36096_;
                if (supplier instanceof Supplier) {
                    Supplier _current = supplier;
                    Object obj = _current.get();
                    if (obj instanceof Map) {
                        Map _slots = (Map) obj;
                        ItemStack _setstack = new ItemStack((ItemLike) BigExplosivesModItems.TEN_KG_BOMB.get()).m_41777_();
                        _setstack.m_41764_(1);
                        ((Slot) _slots.get(Integer.valueOf((int) 7.0d))).m_5852_(_setstack);
                        _player.f_36096_.m_38946_();
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        if (TenkgFireCustomRecipeProcedure.execute(entity)) {
            if (entity instanceof Player) {
                Player _player2 = (Player) entity;
                Supplier supplier2 = _player2.f_36096_;
                if (supplier2 instanceof Supplier) {
                    Supplier _current2 = supplier2;
                    Object obj2 = _current2.get();
                    if (obj2 instanceof Map) {
                        Map _slots2 = (Map) obj2;
                        ItemStack _setstack2 = new ItemStack((ItemLike) BigExplosivesModItems.TEN_KG_NAPALM_BOMB.get()).m_41777_();
                        _setstack2.m_41764_(1);
                        ((Slot) _slots2.get(Integer.valueOf((int) 7.0d))).m_5852_(_setstack2);
                        _player2.f_36096_.m_38946_();
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        if (TwoHundredAndFiftyCustomRecipeProcedure.execute(entity)) {
            if (entity instanceof Player) {
                Player _player3 = (Player) entity;
                Supplier supplier3 = _player3.f_36096_;
                if (supplier3 instanceof Supplier) {
                    Supplier _current3 = supplier3;
                    Object obj3 = _current3.get();
                    if (obj3 instanceof Map) {
                        Map _slots3 = (Map) obj3;
                        ItemStack _setstack3 = new ItemStack((ItemLike) BigExplosivesModItems.TWO_HUNDRED_AND_FIFTY_KG_BOMB.get()).m_41777_();
                        _setstack3.m_41764_(1);
                        ((Slot) _slots3.get(Integer.valueOf((int) 7.0d))).m_5852_(_setstack3);
                        _player3.f_36096_.m_38946_();
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        if (entity instanceof Player) {
            Player _player4 = (Player) entity;
            Supplier supplier4 = _player4.f_36096_;
            if (supplier4 instanceof Supplier) {
                Supplier _current4 = supplier4;
                Object obj4 = _current4.get();
                if (obj4 instanceof Map) {
                    Map _slots4 = (Map) obj4;
                    ((Slot) _slots4.get(Integer.valueOf((int) 7.0d))).m_5852_(ItemStack.f_41583_);
                    _player4.f_36096_.m_38946_();
                }
            }
        }
    }
}
