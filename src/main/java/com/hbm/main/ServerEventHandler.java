package com.hbm.main;

import com.hbm.addational_data.Pollution;
import com.hbm.config.ClientConfig;
import com.hbm.config.ServerConfig;
import com.hbm.dev.AssetConsistencyChecker;
import com.hbm.dev.ModelValidator;
import com.hbm.dim.orbit.CelestialBodies;
import com.hbm.dim.orbit.Space;
import com.hbm.entity.ModEntityType;
import com.hbm.entity.mob.EntityGlyphid;
import com.hbm.registries.HBMMatters;
import com.hbm.registries.ModItems;
import com.hbm.item.env.ItemEggGlyphid;
import com.hbm.network.ServerMsgHandler;
import com.hbm.registries.HBMDamage;
import com.hbm.registries.ModCommands;
import com.hbm.utils.transport_net.EnergyNetworkSystem;
import com.hbm.utils.transport_net.FluidNetworkSystem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public class ServerEventHandler {

    public static void registerEvents(IEventBus forgeBus, IEventBus modBus){
        modBus.addListener(ServerEventHandler::onServerSetup);
        modBus.addListener(ServerEventHandler::onLoadComplete);
        modBus.addListener(ServerEventHandler::createEntityAttribute);
        modBus.addListener(ServerEventHandler::onSpawnPlacementRegisterEvent);
        forgeBus.addListener(ServerEventHandler::onTagsUpdated);
        forgeBus.addListener(ServerEventHandler::levelTick);
        forgeBus.addListener(ServerEventHandler::levelUnload);
        forgeBus.addListener(ServerEventHandler::registerCommands);
        forgeBus.addListener(ServerEventHandler::serverTick);
        forgeBus.addListener(ServerEventHandler::onPlayerClickInventory);
        forgeBus.addListener(ServerEventHandler::onPlayerTossItem);
        forgeBus.addListener(ServerEventHandler::onEntityJoin);
        forgeBus.addListener(ServerEventHandler::onFinialSpawn);
    }


    @SubscribeEvent
    public static void onServerSetup(FMLDedicatedServerSetupEvent event) {
        HBMDamage.clearLocalData();
    }

    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        ClientConfig.initConfig();
        ServerConfig.initConfig();
        event.enqueueWork(() -> {
            AssetConsistencyChecker.runIfRequested();
            ModelValidator.runIfRequested();
        });
    }

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        HBMMatters.buildCache();
    }

    @SubscribeEvent
    public static void levelTick(TickEvent.LevelTickEvent event){
        if (event.phase == TickEvent.Phase.END){
            Level level = event.level;
            if (level.dimension() == Space.LEVEL_KEY){
                CelestialBodies.runServer(level);
            }
            // 能量系统
            if (EnergyNetworkSystem.has(level)) EnergyNetworkSystem.getOrCreate(level).tick();
        }
    }

    @SubscribeEvent
    public static void levelUnload(LevelEvent.Unload event){
        if (!event.getLevel().isClientSide()){
            EnergyNetworkSystem.INSTANCES.remove(event.getLevel());
        }
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        ModCommands.registerServerCommands(event);
    }

    @SubscribeEvent
    public static void createEntityAttribute(EntityAttributeCreationEvent event){
        event.put(ModEntityType.GLYPHID.get(), EntityGlyphid.createMobAttributes().build());
        event.put(ModEntityType.GLYPHID_SCOUT.get(), EntityGlyphid.createMobAttributes().build());
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
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        double gravityBase = 0.08;
        boolean isNoGravity = false;

        if (event.getLevel().dimension() == Space.LEVEL_KEY){
            gravityBase = 0;
            isNoGravity = true;
        }

        if (entity instanceof LivingEntity living) {
            var gravity = living.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
            if (gravity != null) {
                // 将重力设为 0（默认是 0.08 左右）
                gravity.setBaseValue(gravityBase);
            }
        }else {
            entity.setNoGravity(isNoGravity);
        }
    }

    @SubscribeEvent
    public static void onSpawnPlacementRegisterEvent(SpawnPlacementRegisterEvent event){
        Pollution.rampantScoutPopulator(event);
    }

    @SubscribeEvent
    public static void onFinialSpawn(MobSpawnEvent.FinalizeSpawn event){
        Pollution.enforceMob(event);
    }
}
