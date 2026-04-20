package com.hbm.gui.menu;

import com.hbm.blockentity.logistic.TileConveyorExtractor;
import com.hbm.gui.ModMenuType;
import com.hbm.gui.menu.slot.FilterSlot;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MenuConveyorExtractor extends BaseMachineMenu<TileConveyorExtractor>{
    public MenuConveyorExtractor(int pContainerId, Inventory playerInventory, TileConveyorExtractor blockEntity, ContainerData containerData1){
        super(ModMenuType.MENU_CONVEYOR_EXTRACTOR.get(),pContainerId, playerInventory, blockEntity, containerData1);
        ItemStackHandler items = this.be.getItems();
        this.slotNum = 20;
        // 过滤器
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new FilterSlot(items, j + i * 3, 71 + j * 18, 17 + i * 18));
            }
        }
        // 物品槽
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new SlotItemHandler(items, j + i * 3 + 9, 8 + j * 18, 17 + i * 18));
            }
        }
        // 升级
        this.addSlot(new SlotItemHandler(items, 18, 152, 23));
        this.addSlot(new SlotItemHandler(items, 19, 152, 47));
        // 玩家
        addPlayerSlot(playerInventory, 18, 19);
    }
    public MenuConveyorExtractor(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(TileConveyorExtractor.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(2));
    }

    @Override
    public boolean innerMovePlayer2Container(int pIndex, ItemStack itemStack) {
        return this.moveItemStackTo(itemStack, 9, 18, false);
    }

    public boolean isWhitelist(){
        return this.containerData.get(0) > 0;
    }

    @Override
    public void clicked(int slotId, int button, ClickType pClickType, Player pPlayer) {
        // 检查是否点击的是虚影槽
        if (slotId >= 0 && getSlot(slotId) instanceof FilterSlot filterSlot) {
            ItemStack carried = getCarried(); // 玩家鼠标上抓着的物品

            if (button == 1) {
                // 右键：清空槽位
                filterSlot.set(ItemStack.EMPTY);
            } else {
                // 左键：用抓着的物品替换虚影
                if (!carried.isEmpty()) {
                    filterSlot.set(carried.copy());
                }
            }
            return; // 拦截原生逻辑，不执行提取/放入
        }
        super.clicked(slotId, button, pClickType, pPlayer);
    }
}
