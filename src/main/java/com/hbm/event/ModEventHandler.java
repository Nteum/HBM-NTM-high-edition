package com.hbm.event;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModEventHandler {
    @SubscribeEvent
    public void worldTick(TickEvent.LevelTickEvent event){
        if (event != null && !event.level.isClientSide){

        }
    }
}
