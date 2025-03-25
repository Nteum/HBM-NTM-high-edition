package com.hbm.main;

import com.hbm.HBM;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HBM.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public void worldTick(TickEvent.LevelTickEvent event){
        if (event != null && !event.level.isClientSide){

        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event){
        if (event.phase.equals(TickEvent.Phase.START)){

        }else if (event.phase.equals(TickEvent.Phase.END)){

        }
    }
}
