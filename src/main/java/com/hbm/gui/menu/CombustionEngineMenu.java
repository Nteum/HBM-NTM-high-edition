package com.hbm.gui.menu;

import com.hbm.blockentity.machine.CombustionEngineEntity;
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
 * 内燃机菜单。
 * 槽位：0 桶入、1 桶出、2 活塞、3 电池、4 罐ID。
 */
public class CombustionEngineMenu extends BaseMachineMenu<CombustionEngineEntity> {
    public CombustionEngineMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(CombustionEngineEntity.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(4));
    }
    public CombustionEngineMenu(int pContainerId, Inventory pPlayerInventory, CombustionEngineEntity be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_combustion_engine"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 5;
        var handler = be.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, 0, 17, 17));
        this.addSlot(new SlotItemHandler(handler, 1, 17, 53) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(handler, 2, 88, 71));
        this.addSlot(new SlotItemHandler(handler, 3, 143, 71));
        this.addSlot(new SlotItemHandler(handler, 4, 35, 71));
        addPlayerSlot(pPlayerInventory, 0, 37);
        this.addDataSlots(containerData1);
    }

    public CombustionEngineEntity getEntity(){ return this.be; }
}
