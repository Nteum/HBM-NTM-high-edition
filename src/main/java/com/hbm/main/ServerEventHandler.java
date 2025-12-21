package com.hbm.main;

import com.hbm.entity.ModEntityType;
import com.hbm.entity.mob.EntityGlyphid;
import com.hbm.item.HBMComponent;
import com.hbm.item.HBMItems;
import com.hbm.item.env.ItemEggGlyphid;
import com.hbm.network.ServerMsgHandler;
import com.hbm.registries.HBMDamage;
import com.hbm.utils.transport_net.FluidNetworkSystem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

//@Mod.EventBusSubscriber(modid = HBM.MODID)
public class ServerEventHandler {

    public static void registerEvents(IEventBus forgeBus, IEventBus modBus){
        modBus.addListener(ServerEventHandler::onServerSetup);
        modBus.addListener(ServerEventHandler::createEntityAttribute);
        forgeBus.addListener(ServerEventHandler::worldTick);
        forgeBus.addListener(ServerEventHandler::serverTick);
        forgeBus.addListener(ServerEventHandler::onPlayerClickInventory);
        forgeBus.addListener(ServerEventHandler::onPlayerTossItem);
    }
    @SubscribeEvent
    public static void onServerSetup(FMLDedicatedServerSetupEvent event) {
        HBMDamage.clearLocalData();
    }

    @SubscribeEvent
    public static void worldTick(TickEvent.LevelTickEvent event){
    }

    @SubscribeEvent
    public static void createEntityAttribute(EntityAttributeCreationEvent event){
        event.put(ModEntityType.GLYPHID.get(), EntityGlyphid.createMobAttributes().build());
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
        if (itemStack.is(HBMItems.EGG_GLYPHID.get()) || itemStack.is(HBMItems.EGG_GLYPHID_TO_BIRTH.get())){
            event.getPlayer().addItem(itemStack);
            event.setCanceled(true);
        }
    }
}
