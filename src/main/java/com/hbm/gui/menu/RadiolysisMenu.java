package com.hbm.gui.menu;

import com.hbm.blockentity.machine.RadiolysisEntity;
import com.hbm.gui.HBMMenus;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

/**
 * 辐射裂解装置菜单。
 * 槽位：0-9 RTG、10 流体桶入、11 流体桶出、12 消毒入、13 消毒出、14 电池。
 */
public class RadiolysisMenu extends BaseMachineMenu<RadiolysisEntity> {
    public RadiolysisMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(RadiolysisEntity.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(0));
    }
    public RadiolysisMenu(int pContainerId, Inventory pPlayerInventory, RadiolysisEntity be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_radiolysis"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 15;
        var handler = be.getItemHandler();
        // RTG 槽（2 行 x 5 列）
        for (byte i = 0; i < 2; i++) {
            for (byte j = 0; j < 5; j++) {
                this.addSlot(new SlotItemHandler(handler, j + i * 5, 188 + i * 18, 8 + j * 18));
            }
        }
        // 流体桶 IO
        this.addSlot(new SlotItemHandler(handler, 10, 34, 17));
        this.addSlot(new SlotItemHandler(handler, 11, 34, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        // 消毒
        this.addSlot(new SlotItemHandler(handler, 12, 148, 17));
        this.addSlot(new SlotItemHandler(handler, 13, 148, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        // 电池
        this.addSlot(new SlotItemHandler(handler, 14, 8, 53));
        addPlayerSlot(pPlayerInventory, 0, 0);
        this.addDataSlots(containerData1);
    }

    public RadiolysisEntity getEntity(){
        return this.be;
    }
}
