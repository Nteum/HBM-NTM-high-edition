package com.hbm.gui.menu;

import com.hbm.blockentity.logistic.TileConveyorRouter;
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

public class MenuConveyorRouter extends BaseMachineMenu<TileConveyorRouter> {
    public MenuConveyorRouter(int pContainerId, Inventory playerInventory, TileConveyorRouter blockEntity, ContainerData containerData1) {
        super(ModMenuType.MENU_CONVEYOR_ROUTER.get(), pContainerId, playerInventory, blockEntity, containerData1);

        ItemStackHandler items = blockEntity.getItems();
        for(int j = 0; j < 2; j++) {
            for(int i = 0; i < 3; i++) {
                for(int k = 0; k < 5; k++) {
                    this.addSlot(new FilterSlot(items, k + j * 15 + i * 5, 34 + k * 18 + j * 98, 17 + i * 26));
                }
            }
        }
//        playerInv(invPlayer, 47, 119, 177);
        addPlayerSlot(playerInventory, 39, 35);
    }

    public MenuConveyorRouter(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(TileConveyorRouter.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(2));
    }

    public int getMode(){
        return this.containerData.get(1);
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
