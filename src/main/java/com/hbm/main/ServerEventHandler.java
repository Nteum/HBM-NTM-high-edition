package com.hbm.main;

import com.hbm.item.HBMComponent;
import com.hbm.item.env.ItemEggGlyphid;
import com.hbm.network.ServerMsgHandler;
import com.hbm.utils.transport_net.FluidNetworkSystem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

//@Mod.EventBusSubscriber(modid = HBM.MODID)
public class ServerEventHandler {

    public static void registerEvents(IEventBus forgeBus, IEventBus modBus){
        forgeBus.addListener(ServerEventHandler::worldTick);
        forgeBus.addListener(ServerEventHandler::serverTick);
        forgeBus.addListener(ServerEventHandler::onPlayerClickInventory);
        forgeBus.addListener(ServerEventHandler::onPlayerTossItem);
    }
    @SubscribeEvent
    public static void worldTick(TickEvent.LevelTickEvent event){
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event){
        if (event.phase.equals(TickEvent.Phase.START)){
            ServerMsgHandler.tick(event);
        }else if (event.phase.equals(TickEvent.Phase.END)){
            FluidNetworkSystem.INSTANCES.values().forEach(FluidNetworkSystem::tick);
        }
    }

    @SubscribeEvent
    public static void onPlayerClickInventory(ItemStackedOnOtherEvent event){
        ItemStack carriedItem = event.getCarriedItem();
        if (carriedItem.getItem() instanceof ItemEggGlyphid && event.getPlayer().inventoryMenu.slots.contains(event.getSlot())){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTossItem(ItemTossEvent event){
        ItemStack itemStack = event.getEntity().getItem();
        if (itemStack.is(HBMComponent.EGG_GLYPHID.get()) || itemStack.is(HBMComponent.EGG_GLYPHID_TO_BIRTH.get())){
            event.getPlayer().addItem(itemStack);
            event.setCanceled(true);
        }
    }
}
