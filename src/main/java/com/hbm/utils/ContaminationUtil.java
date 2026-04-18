package com.hbm.utils;

import com.hbm.HBMLang;
import com.hbm.addational_data.AdditionalDataManager;
import com.hbm.addational_data.DataEntry;
import com.hbm.addational_data.chunk.RadiationManager;
import com.hbm.api.badthing.HazmatRegistry;
import com.hbm.handler.radiation.ChunkRadiationManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ContaminationUtil {
    /**
     * Calculates how much radiation can be applied to this entity by calculating resistance
     * @param entity
     * @return
     */
    public static float calculateRadiationMod(LivingEntity entity) {

        if(entity instanceof Player) {
            Player player = (Player)entity;

            float koeff = 10.0F;
            return (float) Math.pow(koeff, -HazmatRegistry.getResistance(player));
        }

        return 1;
    }
    public static boolean isRadImmune(Entity e) {
//
//        if(e instanceof LivingEntity && ((LivingEntity)e).isPotionActive(HbmPotion.mutation))
//            return true;
//
//        if(immuneEntities.isEmpty()) {
//            immuneEntities.add(EntityCreeperNuclear.class);
//            immuneEntities.add(EntityMooshroom.class);
//            immuneEntities.add(EntityZombie.class);
//            immuneEntities.add(EntitySkeleton.class);
//            immuneEntities.add(EntityQuackos.class);
//            immuneEntities.add(EntityOcelot.class);
//            immuneEntities.add(IRadiationImmune.class);
//        }
//
//        Class entityClass = e.getClass();
//
//        for(Class clazz : immuneEntities) {
//            if(clazz.isAssignableFrom(entityClass)) return true;
//        }
//
//        if("cyano.lootable.entities.EntityLootableBody".equals(entityClass.getName())) return true;

        return false;
    }
    public static void incrementRadiation(Entity entity, float amount){
        Float rad = AdditionalDataManager.getEntityData(entity, DataEntry.RADIATION).map(o -> (float) o).orElse(0f);
        AdditionalDataManager.setEntityData(entity, DataEntry.RADIATION, rad + amount);
    }
    public static void incrementDigamma(Entity entity, float amount){
        Float rad = AdditionalDataManager.getEntityData(entity, DataEntry.DIGMMA).map(o -> (float) o).orElse(0f);
        AdditionalDataManager.setEntityData(entity, DataEntry.DIGMMA, rad + amount);
    }
    public static enum HazardType {
        RADIATION,
        DIGAMMA
    }

    public static enum ContaminationType {
        FARADAY,			//preventable by metal armor
        HAZMAT,				//preventable by hazmat
        HAZMAT2,			//preventable by heavy hazmat
        DIGAMMA,			//preventable by fau armor or stability
        DIGAMMA2,			//preventable by robes
        CREATIVE,			//preventable by creative mode, for rad calculation armor piece bonuses still apply
        RAD_BYPASS,			//same as creative but will not apply radiation resistance calculation
        NONE				//not preventable
    }
    public static boolean contaminate(LivingEntity entity, HazardType hazard, ContaminationType cont, float amount){
        if(hazard == HazardType.RADIATION) {
            float radEnv = AdditionalDataManager.getEntityData(entity, DataEntry.RADIATION_ENV).map(o -> (float) o).orElse(0f);
            AdditionalDataManager.setEntityData(entity, DataEntry.RADIATION_ENV, radEnv + amount);
        }

        if(entity instanceof Player) {

            Player player = (Player)entity;

            switch(cont) {
//                case FARADAY:			if(ArmorUtil.checkForFaraday(player))	return false; break;
//                case HAZMAT:			if(ArmorUtil.checkForHazmat(player))	return false; break;
//                case HAZMAT2:			if(ArmorUtil.checkForHaz2(player))		return false; break;
//                case DIGAMMA:			if(ArmorUtil.checkForDigamma(player))	return false; if(ArmorUtil.checkForDigamma2(player))	return false; break;
//                case DIGAMMA2:			if(ArmorUtil.checkForDigamma2(player))	return false; break;
            }

            if(player.isCreative() && cont != ContaminationType.NONE && cont != ContaminationType.DIGAMMA2)
                return false;

            if(player.tickCount < 200)
                return false;
        }

        if(hazard == HazardType.RADIATION && isRadImmune(entity))
            return false;

        switch(hazard) {
            case RADIATION: incrementRadiation(entity, amount * (cont == ContaminationType.RAD_BYPASS ? 1 : calculateRadiationMod(entity))); break;
            case DIGAMMA: incrementDigamma(entity, amount); break;
        }
        return true;
    }

    public static void printGeigerData(Player player) {

        Level level = player.level();

        float eRad = AdditionalDataManager.getEntityData(player, DataEntry.RADIATION).map(o -> (float) o).orElse(0f);
        float env = AdditionalDataManager.getEntityData(player, DataEntry.RADIATION_BUF).map(o -> (float) o).orElse(0f);
        float rads = RadiationManager.getRadiation(level, player.getOnPos());

        double res = ((int)(10000D - ContaminationUtil.calculateRadiationMod(player) * 10000D)) / 100D;
        double resKoeff = ((int)(HazmatRegistry.getResistance(player) * 100D)) / 100D;

        ChatFormatting chunkFormat = getPreffixFromRad(rads);
        ChatFormatting envFormat = getPreffixFromRad(rads);
        ChatFormatting radPrefix;
        ChatFormatting resPrefix = ChatFormatting.WHITE;

        if(eRad < 200)
            radPrefix = ChatFormatting.GREEN;
        else if(eRad < 400)
            radPrefix = ChatFormatting.YELLOW;
        else if(eRad < 600)
            radPrefix = ChatFormatting.GOLD;
        else if(eRad < 800)
            radPrefix = ChatFormatting.RED;
        else if(eRad < 1000)
            radPrefix = ChatFormatting.DARK_RED;
        else
            radPrefix = ChatFormatting.DARK_GRAY;

        if(resKoeff > 0)
            resPrefix = ChatFormatting.GREEN;

        //localization and server-side restrictions have turned this into a painful mess
        //a *functioning* painful mess, nonetheless
        player.sendSystemMessage(Component.literal("===== ☢ ").append(Component.translatable(HBMLang.TOOLTIP_GEIGER0.key())).append(Component.literal(" ☢ =====")).withStyle(ChatFormatting.GOLD));
        player.sendSystemMessage(Component.translatable(HBMLang.TOOLTIP_GEIGER1.key()).append(Component.literal(" " + rads + " RAD/s").withStyle(chunkFormat)).withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.translatable(HBMLang.TOOLTIP_GEIGER2.key()).append(Component.literal(" " + env + " RAD/s").withStyle(envFormat)).withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.translatable(HBMLang.TOOLTIP_GEIGER3.key()).append(Component.literal(" " + eRad + " RAD").withStyle(radPrefix)).withStyle(ChatFormatting.YELLOW));
        player.sendSystemMessage(Component.translatable(HBMLang.TOOLTIP_GEIGER4.key()).append(Component.literal(" " + res + " %").withStyle(resPrefix)).withStyle(ChatFormatting.YELLOW));
    }
    public static ChatFormatting getPreffixFromRad(double rads) {
        if(rads == 0)
            return ChatFormatting.GREEN;
        else if(rads < 1)
            return ChatFormatting.YELLOW;
        else if(rads < 10)
            return ChatFormatting.GOLD;
        else if(rads < 100)
            return ChatFormatting.RED;
        else if(rads < 1000)
            return ChatFormatting.DARK_RED;
        else
            return ChatFormatting.DARK_GRAY;
    }
}
