package com.hbm.main;

import com.hbm.addational_data.Pollution;
import com.hbm.entity.ModEntityType;
import com.hbm.entity.mob.EntityGlyphid;
import com.hbm.item.HBMComponent;
import com.hbm.item.env.ItemEggGlyphid;
import com.hbm.network.ServerMsgHandler;
import com.hbm.registries.HBMDamage;
import com.hbm.registries.ModCommands;
import com.hbm.registries.ModItems;
import com.hbm.utils.transport_net.FluidNetworkSystem;
import net.minecraft.commands.Commands;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public class ServerEventHandler {

    public static void registerEvents(IEventBus forgeBus, IEventBus modBus){
        modBus.addListener(ServerEventHandler::onServerSetup);
        modBus.addListener(ServerEventHandler::createEntityAttribute);
        modBus.addListener(ServerEventHandler::onSpawnPlacementRegisterEvent);
        forgeBus.addListener(ServerEventHandler::worldTick);
        forgeBus.addListener(ServerEventHandler::registerCommands);
        forgeBus.addListener(ServerEventHandler::serverTick);
        forgeBus.addListener(ServerEventHandler::onPlayerClickInventory);
        forgeBus.addListener(ServerEventHandler::onPlayerTossItem);
//        forgeBus.addListener(ServerEventHandler::onFinialSpawn);
    }
    @SubscribeEvent
    public static void onServerSetup(FMLDedicatedServerSetupEvent event) {
        HBMDamage.clearLocalData();
    }

    @SubscribeEvent
    public static void worldTick(TickEvent.LevelTickEvent event){
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        ModCommands.registerServerCommands(event);
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
        if (itemStack.is(ModItems.EGG_GLYPHID.get()) || itemStack.is(ModItems.EGG_GLYPHID_TO_BIRTH.get())){
            event.getPlayer().addItem(itemStack);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onSpawnPlacementRegisterEvent(SpawnPlacementRegisterEvent event){
        Pollution.rampantScoutPopulator(event);
    }

//    @SubscribeEvent
//    public static void onFinialSpawn(MobSpawnEvent.FinalizeSpawn event){
//        Pollution.enforceMob(event);
//    }
}
