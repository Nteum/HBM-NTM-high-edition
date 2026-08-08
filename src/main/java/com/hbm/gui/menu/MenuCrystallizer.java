package com.hbm.gui.menu;

import com.hbm.block.machine.MachineCrystallizer;
import com.hbm.blockentity.machine.TileCrystallizer;
import com.hbm.gui.HBMMenus;
import com.hbm.gui.menu.slot.OutputSlotItemHandler;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MenuCrystallizer extends BaseMachineMenu<TileCrystallizer> {
    public MenuCrystallizer(int pContainerId, Inventory playerInventory, TileCrystallizer blockEntity, ContainerData containerData1) {
        super(HBMMenus.getById(MachineCrystallizer.id), pContainerId, playerInventory, blockEntity, containerData1);
        ItemStackHandler items = blockEntity.getItems();
        //Input
        this.addSlot(new SlotItemHandler(items, 0, 62, 45));
        //Battery
        this.addSlot(new SlotItemHandler(items, 1, 152, 72));
        //Output
        this.addSlot(new OutputSlotItemHandler(items, 2, 113, 45));
        //Fluid slots
        this.addSlot(new SlotItemHandler(items, 3, 17, 18));
        this.addSlot(new OutputSlotItemHandler(items, 4, 17, 54));
        //Upgrades
        this.addSlot(new SlotItemHandler(items, 5, 80, 18));
        this.addSlot(new SlotItemHandler(items, 6, 98, 18));
        //Fluid ID
        this.addSlot(new OutputSlotItemHandler(items, 7, 35, 72));

        addPlayerSlot(playerInventory, 0, 38);
    }

    public MenuCrystallizer(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(TileCrystallizer.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(TileCrystallizer.CONTAINER_DATA_SIZE));
    }
    public int getProgress(){
        return this.containerData.get(0);
    }
    public int getEnergy(){
        return this.containerData.get(1);
    }
    public int getDuration(){
        return this.containerData.get(2);
    }
    public FluidStack getFluidStack(){
        return this.be.getFluids().getFluidInTank(0);
    }
}
