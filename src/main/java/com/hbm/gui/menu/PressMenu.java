package com.hbm.gui.menu;

import com.hbm.block.machine.BlockPress;
import com.hbm.blockentity.machine.PressEntity;
import com.hbm.gui.ModMenuType;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * 火力锻压机GUI
 * */
public class PressMenu extends AbstractContainerMenu {
    public PressEntity pressEntity;
    public ContainerData containerData;
    Level level;
    public PressMenu(int pContainerId, Inventory pPlayerInventory, PressEntity press, ContainerData containerData){
        super(ModMenuType.PRESS_MENU.get(), pContainerId);

        this.pressEntity = press;
        this.level = press.getLevel();
        IItemHandler handler = press.getItemHandler();
        // 输入槽
        this.addSlot(new SlotItemHandler(handler, 0,26,53));
        this.addSlot(new SlotItemHandler(handler, 1,80,17));
        this.addSlot(new SlotItemHandler(handler, 2,80,53));
        // 输出槽（禁止放入）
        this.addSlot(new SlotItemHandler(handler, 3,140,35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(pPlayerInventory, k, 8 + k * 18, 142));
        }
        this.addDataSlots(containerData);
        this.containerData = containerData;
    }

    // 客户端构造器：Forge 会在客户端使用 (int, Inventory, FriendlyByteBuf) 的构造器
    public PressMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, getClientBlockEntity(playerInventory, buf), new SimpleContainerData(3));
    }

    private static PressEntity getClientBlockEntity(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.level().getBlockEntity(pos);
        if (!(be instanceof PressEntity machine))
            throw new IllegalStateException("BlockEntity is not MachineBlockEntity");
        return machine;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem().copy();
        if (index < 4) { // machine slots -> player
            if (!this.moveItemStackTo(stack, 4, this.slots.size(), true)) return ItemStack.EMPTY;
            slot.onTake(player, stack);
        } else {
            // player -> try machine input
            if (!this.moveItemStackTo(stack, 0, 2, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return stack;
    }

    @Override
    public boolean stillValid(Player player) {
        if (level == null) return false;
        BlockPos pos = pressEntity.getBlockPos();

        if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64) return false;
        return level.getBlockState(pos).getBlock() == ModBlocks.machine_press.get();
    }

    public float getSpeed(){
        return this.containerData.get(0);
    }
    public float getBurnTime(){
        return this.containerData.get(1);
    }
    public float getPress(){
        return this.containerData.get(2);
    }
}