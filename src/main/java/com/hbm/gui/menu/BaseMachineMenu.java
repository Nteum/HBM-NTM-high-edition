package com.hbm.gui.menu;

import com.hbm.blockentity.machine.PressEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class BaseMachineMenu extends AbstractContainerMenu {
    public Container container;
    public ContainerData containerData;
    public int slotNum = 0;

    protected BaseMachineMenu(@Nullable MenuType<?> pMenuType, int pContainerId,Container inContainer, ContainerData containerData1) {
        super(pMenuType, pContainerId);
        container = inContainer;
        containerData = containerData1;
    }
    /**
     * index排序：额外加入的物品槽...玩家物品槽...
     * */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem().copy();
        if (index < slotNum) { // machine slots -> player
            if (!this.moveItemStackTo(stack, 4, this.slots.size(), true)) return ItemStack.EMPTY;
            slot.onTake(player, stack);
        } else {
            // player -> try machine input
            if (!this.innerMovePlayer2Container(index, stack)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return stack;

//        ItemStack itemStack = ItemStack.EMPTY;
//        Slot slot = this.slots.get(pIndex);
//        if (slot.hasItem()){
//            ItemStack itemStack1 = slot.getItem();
//            itemStack = itemStack1.copy();
//            if (pIndex < slotNum){
//                // 从机器物品槽向玩家物品槽移动
//                if (!this.moveItemStackTo(itemStack1, slotNum, slotNum+36, true)){
//                    return ItemStack.EMPTY;
//                }
//            }else {
//                // 从玩家物品槽向机器物品槽移动，默认正序
//                if (!innerMovePlayer2Container(pIndex, itemStack1))
//                    return ItemStack.EMPTY;
//            }
//            if (itemStack1.isEmpty()) {
//                slot.setByPlayer(ItemStack.EMPTY);
//            } else {
//                slot.setChanged();
//            }
//        }
//        return itemStack;
    }

    public boolean innerMovePlayer2Container(int pIndex, ItemStack itemStack){
        return this.moveItemStackTo(itemStack, 0, slotNum, false);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return this.container.stillValid(pPlayer);
    }

    void addSlotWithPos(Container container, int StartIdx, int[][] slotPos){
        for (int i = 0; i < slotPos.length; i++) {
            this.addSlot(new Slot(container, StartIdx+i, slotPos[i][0], slotPos[i][1]));
        }
    }
    void addPlayerSlot(Inventory pPlayerInventory,int xOffset,int yOffset){
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18 + xOffset, 84 + i * 18 + yOffset));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(pPlayerInventory, k, 8 + k * 18 + xOffset, 142 + yOffset));
        }
    }
    protected static <T extends BlockEntity> T getClientBlockEntity(Class<T> beclass, Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity be = inv.player.level().getBlockEntity(pos);
        if (be == null)
            throw new IllegalStateException("No block entity in "+pos.toShortString());
        return beclass.cast(be);
    }
}
