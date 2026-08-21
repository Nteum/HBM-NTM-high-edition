package com.hbm.gui.menu;

import com.hbm.blockentity.machine.MilkReformerEntity;
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
 * 牛奶改质器菜单。
 * 槽位：0 电池、1 牛奶桶入、2 牛奶桶出、3-8 三种产物桶出入。
 */
public class MilkReformerMenu extends BaseMachineMenu<MilkReformerEntity> {
    public MilkReformerMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(MilkReformerEntity.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(0));
    }
    public MilkReformerMenu(int pContainerId, Inventory pPlayerInventory, MilkReformerEntity be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_milk_reformer"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 9;
        var handler = be.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, 0, 79, 8));
        this.addSlot(new SlotItemHandler(handler, 1, 45, 88));
        this.addSlot(new SlotItemHandler(handler, 2, 45, 106) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new SlotItemHandler(handler, 3, 95, 88));
        this.addSlot(new SlotItemHandler(handler, 4, 95, 106) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new SlotItemHandler(handler, 5, 122, 88));
        this.addSlot(new SlotItemHandler(handler, 6, 122, 106) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new SlotItemHandler(handler, 7, 149, 88));
        this.addSlot(new SlotItemHandler(handler, 8, 149, 106) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        addPlayerSlot(pPlayerInventory, 0, 72);
        this.addDataSlots(containerData1);
    }

    public MilkReformerEntity getEntity(){
        return this.be;
    }
}
