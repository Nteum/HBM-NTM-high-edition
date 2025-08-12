package com.hbm.gui.menu;

import com.hbm.blockentity.machine.ChemplantEntity;
import com.hbm.gui.ModMenuType;
import com.hbm.gui.menu.slot.BatterySlot;
import com.hbm.gui.menu.slot.OutputSlot;
import com.hbm.gui.menu.slot.UpgradeSlot;
import com.hbm.registries.ModTags;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public class ChemplantMenu extends BaseMachineMenu implements ITileAccess{
    public BlockEntity be;
    public ChemplantMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, new SimpleContainer(20), new SimpleContainerData(4));
    }

    public ChemplantMenu(int pContainerId, Inventory pInventory, Container inContainer, ContainerData containerData) {
        super(ModMenuType.CHEMPLANT_MENU.get(), pContainerId, inContainer, containerData);
        slotNum = 20;
        Player player = pInventory.player;
//        TransientCraftingContainer craftingContainer = new TransientCraftingContainer(this, 2, 2, NonNullList.of(container.getItem(13), container.getItem(14), container.getItem(15), container.getItem(16)));
        // battery
        this.addSlot(new BatterySlot(container, 0, 80, 18));
        // upgrades
        this.addSlot(new UpgradeSlot(container, 1, 116, 18));
        this.addSlot(new UpgradeSlot(container, 2, 116, 36));
        this.addSlot(new UpgradeSlot(container, 3, 116, 54));
//         Schematic
//        this.addSlot(new Slot(container, 4, 80, 54));
        // Outputs
        this.addSlot(new OutputSlot(container, 4, 134, 90));
        this.addSlot(new OutputSlot(container, 5, 152, 90));
        this.addSlot(new OutputSlot(container, 6, 134, 108));
        this.addSlot(new OutputSlot(container, 7, 152, 108));
        // Fluid Output In
        this.addSlot(new Slot(container, 8, 134, 54));
        this.addSlot(new Slot(container, 9, 152, 54));
        // Fluid Outputs Out
        this.addSlot(new OutputSlot(container, 10, 134, 72));
        this.addSlot(new OutputSlot(container, 11, 152, 72));
        // Input
        this.addSlot(new Slot(container, 12, 8, 90));
        this.addSlot(new Slot(container, 13, 26, 90));
        this.addSlot(new Slot(container, 14, 8, 108));
        this.addSlot(new Slot(container, 15, 26, 108));
        // Fluid Input In
        this.addSlot(new Slot(container, 16, 8, 54));
        this.addSlot(new Slot(container, 17, 26, 54));
        // Fluid Input Out
        this.addSlot(new OutputSlot(container, 18, 8, 72));
        this.addSlot(new OutputSlot(container, 19, 26, 72));
        //player
        addPlayerSlot(pInventory,0,56);
        this.addDataSlots(containerData);
    }

    @Override
    public boolean innerMovePlayer2Container(int pIndex, ItemStack itemStack) {
        int[] temp = new int[]{0,0};
        if (itemStack.is(ModTags.Items.CHARGEABLE))
            temp = new int[]{0,1};
        else if (itemStack.is(ModTags.Items.UPGRADE))
            temp = new int[]{1,4};
        else if (itemStack.is(Items.BUCKET))
            temp = new int[]{18,20};
        else if (itemStack.getItem() instanceof BucketItem)
            temp = new int[]{16,18};
        return this.moveItemStackTo(itemStack, temp[0], temp[1], false) || this.moveItemStackTo(itemStack, 12, 16, false);
    }

    @Override
    public void setTile(BlockEntity blockEntity) {
        this.be = blockEntity;
    }
}
