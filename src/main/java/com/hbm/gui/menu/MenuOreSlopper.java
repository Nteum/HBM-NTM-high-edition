package com.hbm.gui.menu;

import com.hbm.block.machine.MachineOreSlopper;
import com.hbm.blockentity.machine.TileOreSloppper;
import com.hbm.gui.HBMMenus;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModTags;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MenuOreSlopper extends BaseMachineMenu<TileOreSloppper> {
    public MenuOreSlopper(int pContainerId, Inventory playerInventory, TileOreSloppper blockEntity, ContainerData containerData1) {
        super(HBMMenus.typesMaps.get("menu_" + MachineOreSlopper.name).get(), pContainerId, playerInventory, blockEntity, containerData1);
        ItemStackHandler items = blockEntity.getItems();
        // 控制物品进出由SlotItemHandler实现
        //Battery
        this.addSlot(new SlotItemHandler(items, 0, 8, 72));
        //Fluid ID
        this.addSlot(new SlotItemHandler(items, 1, 26, 72));
        //Input
        this.addSlot(new SlotItemHandler(items, 2, 71, 27));
        //Outputs
        this.addSlot(new SlotItemHandler(items, 3, 134, 18));
        this.addSlot(new SlotItemHandler(items, 4, 152, 18));
        this.addSlot(new SlotItemHandler(items, 5, 134, 36));
        this.addSlot(new SlotItemHandler(items, 6, 152, 36));
        this.addSlot(new SlotItemHandler(items, 7, 134, 54));
        this.addSlot(new SlotItemHandler(items, 8, 152, 54));
        //Upgrades
        this.addSlot(new SlotItemHandler(items, 9, 62, 72));
        this.addSlot(new SlotItemHandler(items, 10, 80, 72));

        this.addPlayerSlot(playerInventory, 0, 38);
    }
    public MenuOreSlopper(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(TileOreSloppper.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(3));
    }

    @Override
    public boolean innerMovePlayer2Container(int pIndex, ItemStack itemStack) {
        boolean result;
        if (itemStack.is(ModItems.ORE_BEDROCK_RAW.get())) result = this.moveItemStackTo(itemStack, 2, 3, false);
        else if (itemStack.is(ModTags.Items.UPGRADE)) result = this.moveItemStackTo(itemStack, 9, 11, false);
        else if (itemStack.is(ModTags.Items.CHARGEABLE)) result = this.moveItemStackTo(itemStack, 0, 1, false);
        else result = this.moveItemStackTo(itemStack, 3, 9, false);
        return result || super.innerMovePlayer2Container(pIndex, itemStack);
    }

    public IFluidHandler getFluidHandler(){
        return this.be.getFluidHandler();
    }

    public int getPower(){
        return this.containerData.get(0);
    }

    public float getProgress(){
        return Float.intBitsToFloat(this.containerData.get(2));
    }

    public int getConsumption(){
        return this.containerData.get(2);
    }
}
