package com.hbm.item.armor;

import com.hbm.api.item.IGasMask;
import com.hbm.compat.Compat;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ArmorUtils {
    public static void register() {

    }
    private static void registerIfExists(String domain, String name, ArmorRegistry.HazardClass... classes) {
        Item item = Compat.tryLoadItem(domain, name);
        if (item != Items.AIR) ArmorRegistry.registerHazard(item, classes);
    }
    // 检查是否是一整套盔甲
    public static boolean checkArmor(LivingEntity entity, Item... armor) {
        for(int i = 0; i < 4; i++) {
            if(!checkArmorPiece(entity, armor[i], 3 - i))
                return false;
        }

        return true;
    }

    public static boolean checkArmorPiece(LivingEntity entity, Item armor, int slot) {
        return ArmorRegistry.getArmor(entity, slot).is(armor);
    }
    // 对盔甲造成损坏，到达最大损坏值就直接被破坏
    public static void damageSuit(LivingEntity entity, int slot, int amount) {
        ItemStack armor = ArmorRegistry.getArmor(entity, slot);
        if (!armor.isEmpty() && armor.hurt(amount, entity.getRandom(), entity instanceof ServerPlayer ? (ServerPlayer) entity : null)){
            armor.setCount(0);
        }
    }
//    // 消耗防毒面具耐久
//    public static void damageGasMaskFilter(LivingEntity entity, int damage) {
//        ItemStack mask = ArmorRegistry.getArmor(entity, 3);
//        if(mask == null || mask.isEmpty()) return;
//        if (!(mask.getItem() instanceof IGasMask)){
//            if(ArmorModHandler.hasMods(mask)) {
//                ItemStack mods[] = ArmorModHandler.pryMods(mask);
//                if(mods[ArmorModHandler.helmet_only] != null && mods[ArmorModHandler.helmet_only].getItem() instanceof IGasMask)
//                    mask = mods[ArmorModHandler.helmet_only];
//            }
//        }
//        if (mask.getItem() instanceof IGasMask){
//            damageGasMaskFilter(mask, damage);
//        }
//    }
//    public static void damageGasMaskFilter(ItemStack mask, int damage) {
//        ItemStack filter = getGasMaskFilter(mask);
//
//        if(filter == null) {
//            if(ArmorModHandler.hasMods(mask)) {
//                ItemStack mods[] = ArmorModHandler.pryMods(mask);
//
//                if(mods[ArmorModHandler.helmet_only] != null && mods[ArmorModHandler.helmet_only].getItem() instanceof IGasMask)
//                    filter = getGasMaskFilter(mods[ArmorModHandler.helmet_only]);
//            }
//        }
//
//        if(filter == null || filter.getMaxDamage() == 0)
//            return;
//
//        filter.setItemDamage(filter.getItemDamage() + damage);
//
//        if(filter.getItemDamage() > filter.getMaxDamage())
//            removeFilter(mask);
//        else
//            installGasMaskFilter(mask, filter);
//    }
    // 修改飞行时间
    // 暂时不清楚其具体对应高版本什么设定
    public static void resetFlightTime(Player player) {
        if(player instanceof ServerPlayer serverPlayer) {
//            ReflectionHelper.setPrivateValue(NetHandlerPlayServer.class, mp.playerNetServerHandler, 0, "floatingTickCount", "field_147365_f");
        }
    }

    /*
     * The more horrifying part
     */
    public static boolean checkForHazmat(LivingEntity player) {
        return false;
    }
    public static boolean checkForHaz2(LivingEntity player) {
        return false;
    }

}
