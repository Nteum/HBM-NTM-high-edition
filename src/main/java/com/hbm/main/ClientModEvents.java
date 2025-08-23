package com.hbm.main;

import com.hbm.HBM;
import com.hbm.registries.ModItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.swing.event.MenuEvent;

@Mod.EventBusSubscriber(modid = HBM.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    /** 这个事件用于给模组中的物品添加颜色 */
    @SubscribeEvent
    public static void registerItemColor(RegisterColorHandlersEvent.Item event){
        event.register((itemstack,color)->0xEC9A63, ModItems.BEDROCK_ORE.get());
    }

}
