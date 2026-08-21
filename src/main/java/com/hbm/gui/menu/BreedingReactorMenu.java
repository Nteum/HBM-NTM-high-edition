package com.hbm.gui.menu;

import com.hbm.blockentity.machine.BreedingReactorEntity;
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
 * 增殖反应堆菜单。
 * 槽位：0 输入棒、1 输出棒。
 */
public class BreedingReactorMenu extends BaseMachineMenu<BreedingReactorEntity> {
    public BreedingReactorMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(BreedingReactorEntity.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(2));
    }
    public BreedingReactorMenu(int pContainerId, Inventory pPlayerInventory, BreedingReactorEntity be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_reactor"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 2;
        var handler = be.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, 0, 35, 35));
        this.addSlot(new SlotItemHandler(handler, 1, 125, 35) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        addPlayerSlot(pPlayerInventory, 0, 0);
        this.addDataSlots(containerData1);
    }

    public BreedingReactorEntity getEntity(){ return this.be; }
}
