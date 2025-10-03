package com.hbm.item.armor;

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
