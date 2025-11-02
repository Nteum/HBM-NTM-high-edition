package net.mcreator.nuclearcraft.procedures;

import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelAccessor;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/TwoFiddyButtonItemInInventoryTickProcedure.class */
public class TwoFiddyButtonItemInInventoryTickProcedure {
    public static void execute(LevelAccessor world, ItemStack itemstack) {
        Map<Enchantment, Integer> _enchantments = EnchantmentHelper.m_44831_(itemstack);
        if (_enchantments.containsKey(Enchantments.f_44963_)) {
            _enchantments.remove(Enchantments.f_44963_);
            EnchantmentHelper.m_44865_(_enchantments, itemstack);
        }
        Map<Enchantment, Integer> _enchantments2 = EnchantmentHelper.m_44831_(itemstack);
        if (_enchantments2.containsKey(Enchantments.f_44962_)) {
            _enchantments2.remove(Enchantments.f_44962_);
            EnchantmentHelper.m_44865_(_enchantments2, itemstack);
        }
        Map<Enchantment, Integer> _enchantments3 = EnchantmentHelper.m_44831_(itemstack);
        if (_enchantments3.containsKey(Enchantments.f_44986_)) {
            _enchantments3.remove(Enchantments.f_44986_);
            EnchantmentHelper.m_44865_(_enchantments3, itemstack);
        }
    }
}
