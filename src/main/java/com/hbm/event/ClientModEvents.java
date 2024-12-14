package com.hbm.event;

import com.hbm.main.HBMxx;
import com.hbm.registries.ModItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HBMxx.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    /** 这个事件用于给模组中的物品添加颜色 */
    @SubscribeEvent
    public static void registerItemColor(RegisterColorHandlersEvent.Item event){
        event.register((itemstack,color)->0xEC9A63, ModItems.BEDROCK_ORE.get());
    }
}
