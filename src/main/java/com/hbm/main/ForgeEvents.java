package com.hbm.main;

import com.hbm.HBM;
import com.hbm.api.badthing.ContaminationUtil;
import com.hbm.api.badthing.HbmLivingProps;
import com.hbm.api.badthing.hazard.HazardSystem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.Random;

@Mod.EventBusSubscriber(modid = HBM.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {
    private static Random rand = new Random();
    /**
     * 玩家登录事件
     * - 打印mod版本信息，添加网站访问。
     * */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {

    }
    /**
     * 玩家重生事件
     *
     * */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {

    }
    /**
     * 世界更新事件
     * - 更新辐射对生物和玩家的影响。
     * */
    public static boolean didSit = false;
    public static Field reference = null;
    @SubscribeEvent
    public static void worldTick(TickEvent.LevelTickEvent event) {
        /// RADIATION STUFF START ///
        if(event.level != null && !event.level.isClientSide) {

            if(reference != null) {
                for(Player player : event.level.players()) {
                    didSit = player.isPassenger();
                }
                if(didSit && event.level.getGameTime() % (20 * 20) == 0) {
                    try { reference.setFloat(null, (float) (rand.nextGaussian() * 0.1 + Math.PI)); } catch(Throwable e) { }
                }
            }

//            int thunder = AuxSavedData.getThunder(event.world);
//
//            if(thunder > 0)
//                AuxSavedData.setThunder(event.world, thunder - 1);

            //获取所有要加载的实体。
            Iterable<Entity> entities = ((ServerLevel) event.level).getAllEntities();
            for (Entity e : entities) {
                //处理生物
                if (e instanceof LivingEntity entity){
                    if (entity instanceof Player && ((Player)entity).isCreative())continue;
                    float eRad = HbmLivingProps.getRadiation(entity);
                    //苦力怕
                    if (entity instanceof Creeper creeper && eRad >= 200 && creeper.isAlive()){

                    }//牛
                    else if (entity instanceof Cow cow && !(cow instanceof MushroomCow) && eRad >= 50){

                    }//村民
                    else if (entity instanceof AbstractVillager villager && eRad >= 500){

                    }//鸭子

                    if (eRad < 200 || ContaminationUtil.isRadImmune(entity))continue;

                    if (eRad > 2500)HbmLivingProps.setRadiation(entity, 2500);
                    else if (eRad > 1000){

                    }else if (eRad > 800){

                    }else if (eRad > 600){

                    }else if (eRad > 400){

                    }else if (eRad >= 200){

                    }
                }//处理掉落物实体
                else if (e instanceof ItemEntity item){
                    HazardSystem.updateDroppedItem(item);
                }
            }

            /// RADIATION STUFF END ///

            if(event.phase == TickEvent.Phase.END) {
//                EntityRailCarBase.updateMotion(event.world);
            }
        }

        if(event.phase == TickEvent.Phase.START) {
//            BossSpawnHandler.rollTheDice(event.world);
//            TimedGenerator.automaton(event.world, 100);
        }
    }
}
