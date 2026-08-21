package com.hbm.core.menu;

import com.hbm.blockentity.base.BaseMenuTile;
import com.hbm.core.blockentity.BEMachineBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class MenuBase<T extends BEMachineBase> extends AbstractContainerMenu {
    protected T be;
    public Container container;
    public ContainerData containerData;
    public int slotNum = 0;
    // 主要的构造湖是
    public MenuBase(int pContainerId, Inventory playerInventory, T blockEntity, ContainerData containerData1) {
        super(blockEntity.getMenuType(), pContainerId);
        this.be = blockEntity;
        this.container = blockEntity instanceof Container c ? c : null;
        containerData = containerData1;
        this.addDataSlots(containerData1);
    }
    // 用于响应右键点击注册属性
    public MenuBase(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, (T) Minecraft.getInstance().level.getBlockEntity(buf.readBlockPos()), new SimpleContainerData(buf.readInt()));
    }

    /**
     * index排序：额外加入的物品槽...玩家物品槽...
     * */
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()){
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();
            if (pIndex < slotNum){
                // 从机器物品槽向玩家物品槽移动
                if (!this.moveItemStackTo(itemStack1, slotNum, slotNum+36, true)){
                    return ItemStack.EMPTY;
                }
            }else {
                // 从玩家物品槽向机器物品槽移动，默认正序
                if (!innerMovePlayer2Container(pIndex, itemStack1))
                    return ItemStack.EMPTY;
            }
            // 核心校验：如果执行完移动，数量没变，说明移动没成功（比如目标槽满了）
            if (itemStack.getCount() == itemStack1.getCount()) {
                return ItemStack.EMPTY;
            }
            // 执行 Slot 后的收尾逻辑（如扣除耐久、触发成就等）
            slot.onTake(pPlayer, itemStack1);
            if (itemStack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStack;
    }

    public boolean innerMovePlayer2Container(int pIndex, ItemStack itemStack){
        return this.moveItemStackTo(itemStack, 0, slotNum, false);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return this.be == null ? this.container.stillValid(pPlayer) : Container.stillValidBlockEntity(this.be, pPlayer);
    }

    void addSlotWithPos(Container container, int StartIdx, int[][] slotPos){
        for (int i = 0; i < slotPos.length; i++) {
            this.addSlot(new Slot(container, StartIdx+i, slotPos[i][0], slotPos[i][1]));
        }
    }
    protected void addPlayerSlot(Inventory pPlayerInventory,int xOffset,int yOffset){
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18 + xOffset, 84 + i * 18 + yOffset));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(pPlayerInventory, k, 8 + k * 18 + xOffset, 142 + yOffset));
        }
    }

    public BlockPos getPos(){
        return this.be.getBlockPos();
    }
}
