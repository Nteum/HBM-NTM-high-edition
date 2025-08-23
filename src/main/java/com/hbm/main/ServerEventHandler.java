package com.hbm.main;

import com.hbm.HBM;
import com.hbm.item.HBMComponent;
import com.hbm.item.env.ItemEggGlyphid;
import com.hbm.item.env.ItemEggGlyphidToBirth;
import com.hbm.utils.transport_net.FluidNetworkSystem;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HBM.MODID)
public class ServerEventHandler {
    @SubscribeEvent
    public static void worldTick(TickEvent.LevelTickEvent event){
        if (event != null && !event.level.isClientSide){

        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if (event.phase.equals(TickEvent.Phase.START)){

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
