package com.hbm.gui.menu;

import com.hbm.Inventory.recipe.AssemblerRecipe;
import com.hbm.blockentity.machine.AssemblerEntity;
import com.hbm.blockentity.machine.CrucibleEntity;
import com.hbm.gui.ModMenuType;
import com.hbm.gui.menu.slot.BatterySlot;
import com.hbm.gui.menu.slot.OutputSlot;
import com.hbm.gui.menu.slot.UpgradeSlot;
import com.hbm.registries.ModTags;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class AssemblerMenu extends BaseMachineMenu{
    private AssemblerEntity be;
    int[][] inputSlotPos = {{8, 18},{26, 18},{8, 36},{26, 36},{8, 54},{26, 54},{8, 72},{26, 72},{8, 90},{26, 90},{8, 108},{26, 108}};
    public AssemblerMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(AssemblerEntity.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(3));
    }
    public AssemblerMenu(int pContainerId, Inventory pPlayerInventory, AssemblerEntity be, ContainerData containerData1) {
        super(ModMenuType.ASSEMBLER_MENU.get(), pContainerId, be, containerData1);
        this.be = be;
        ItemStackHandler handler = be.getItemHandler();
        slotNum = 17;
        //battery
        this.addSlot(new SlotItemHandler(handler, 0, 80, 18));
        //upgrade
        this.addSlot(new SlotItemHandler(handler, 1, 152, 18));
        this.addSlot(new SlotItemHandler(handler, 2, 152, 36));
        this.addSlot(new SlotItemHandler(handler, 3, 152, 54));
        //output
        this.addSlot(new SlotItemHandler(handler, 4, 134, 90));
//        this.addSlot(new ResultSlot(pPlayerInventory.player, this.craftSlots, this.resultSlots, 4, 134, 90));
        //input
        for (int i = 0; i < inputSlotPos.length; i++) {
            this.addSlot(new SlotItemHandler(handler, 5+i, inputSlotPos[i][0], inputSlotPos[i][1]));
        }
        //player
        addPlayerSlot(pPlayerInventory,0,56);
    }

    @Override
    public boolean innerMovePlayer2Container(int pIndex, ItemStack itemStack) {
        int[] temp = new int[]{0,0};
        if (itemStack.is(ModTags.Items.CHARGEABLE)) temp = new int[]{0, 1};
        else if (itemStack.is(ModTags.Items.UPGRADE)) temp = new int[]{1, 4};
        return this.moveItemStackTo(itemStack, temp[0], temp[1], false) || this.moveItemStackTo(itemStack, 5, 17, false);
    }
    //    @Override
//    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
//        ItemStack itemStack = ItemStack.EMPTY;
//        Slot slot = this.slots.get(pIndex);
//        if (slot.hasItem()){
//            ItemStack itemStack1 = slot.getItem();
//            itemStack = itemStack1.copy();
//            //电池
//            if (pIndex>=slotNum && itemStack1.is(ModTags.Items.CHARGEABLE)){
//                if (!this.moveItemStackTo(itemStack1, 0, slotNum, false)){
//                    return ItemStack.EMPTY;
//                }
//                //升级控件
//            }else if (pIndex>=slotNum && itemStack1.is(ModTags.Items.UPGRADE)){
//                if (!this.moveItemStackTo(itemStack1, 1, slotNum, false)){
//                    return ItemStack.EMPTY;
//                }
//            }else {
//                return super.quickMoveStack(pPlayer, pIndex);
//            }
//            if (itemStack1.isEmpty()) {
//                slot.setByPlayer(ItemStack.EMPTY);
//            } else {
//                slot.setChanged();
//            }
//        }
//        return itemStack;
//    }

    public int getEnergy(){
        return containerData.get(1);
    }
    public int getEnergyCapacity(){
        return containerData.get(2);
    }
    public double getProgress(){
        return containerData.get(0);
    }
    public double getEnergyRate(){
        return (double) containerData.get(1) / containerData.get(2);
    }

    public BlockPos getPos(){
        return this.be.getBlockPos();
    }

    public AssemblerRecipe getRecipeNow(){
        return this.be.getRecipeNow();
    }
}
