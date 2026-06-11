package com.hbm.gui.menu;

import com.hbm.blockentity.machine.TileMinerLarge;
import com.hbm.gui.ModMenuType;
import com.hbm.gui.menu.slot.OutputSlot;
import com.hbm.item.misc.ItemDrillbit;
import com.hbm.registries.ModTags;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.SlotItemHandler;

public class MenuMinerLarge extends BaseMachineMenu<TileMinerLarge> {
    public MenuMinerLarge(int pContainerId, Inventory inContainer, TileMinerLarge be, ContainerData containerData1) {
        super(ModMenuType.MENU_MINER_LARGE.get(), pContainerId, inContainer, containerData1);
        this.be = be;
        this.slotNum = be.getItemStackHandler().getSlots();
        //Battery: 0
        this.addSlot(new SlotItemHandler(be.getItemStackHandler(), 0, 220, 72));
        //Fluid ID: 1，没用了
        this.addSlot(new SlotItemHandler(be.getItemStackHandler(), 1, 202, 72));
        //Upgrades: 2-4
        for(int i = 0; i < 3; i++) {
            this.addSlot(new SlotItemHandler(be.getItemStackHandler(), 2 + i, 136 + i * 18, 75));
        }
        //Buffer: 5-13
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                this.addSlot(new SlotItemHandler(be.getItemStackHandler(), 5 + j + i * 3, 136 + j * 18, 5 + i * 18));
            }
        }
        this.addPlayerSlot(inContainer, 33, 38);
        this.addDataSlots(containerData1);
    }
    public MenuMinerLarge(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(TileMinerLarge.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(3));
    }

    @Override
    public boolean innerMovePlayer2Container(int pIndex, ItemStack itemStack) {
        boolean result;
        if (itemStack.is(ModTags.Items.BATTERY)) result = this.moveItemStackTo(itemStack, 0, 1, false);
        else if (itemStack.is(ModTags.Items.UPGRADE) ) result = this.moveItemStackTo(itemStack, 2, 5, false);
        else if (itemStack.getItem() instanceof ItemDrillbit) result = this.moveItemStackTo(itemStack, 4,5, false);
        else result = this.moveItemStackTo(itemStack, 5,14, false);
        return result || super.innerMovePlayer2Container(pIndex, itemStack);
    }
    public int getState(){
        return this.containerData.get(0);
    }
    public int getPower(){
        return this.containerData.get(1);
    }

    public FluidStack getFluidStack(){
        return this.be.getFluidHandler().getFluidInTank(0);
    }

    public ItemDrillbit getDrill(){
        ItemStack stackInSlot = this.slots.get(4).getItem();
        if (stackInSlot.getItem() instanceof ItemDrillbit itemDrillbit) return itemDrillbit;
        return null;
    }
}
