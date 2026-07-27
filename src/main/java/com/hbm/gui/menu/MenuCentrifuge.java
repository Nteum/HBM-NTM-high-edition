package com.hbm.gui.menu;

import com.hbm.block.machine.MachineCentrifuge;
import com.hbm.blockentity.machine.TileMachineCentrifuge;
import com.hbm.blockentity.machine.TileOreSloppper;
import com.hbm.gui.ModMenuType;
import com.hbm.gui.menu.slot.OutputSlotItemHandler;
import com.hbm.registries.ModTags;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class MenuCentrifuge extends BaseMachineMenu<TileMachineCentrifuge> {
    public MenuCentrifuge(int pContainerId, Inventory playerInventory, TileMachineCentrifuge blockEntity, ContainerData containerData1){
        super(ModMenuType.getById(MachineCentrifuge.id), pContainerId, playerInventory, blockEntity, containerData1);
        ItemStackHandler items = blockEntity.getItems();
        // 输入槽
        this.addSlot(new SlotItemHandler(items, 0, 36, 50));
        // 电池槽
        this.addSlot(new SlotItemHandler(items, 1, 9, 50));
        // 产物槽
        this.addSlot(new OutputSlotItemHandler(items, 2, 63, 50));
        this.addSlot(new OutputSlotItemHandler(items, 3, 83, 50));
        this.addSlot(new OutputSlotItemHandler(items, 4, 103, 50));
        this.addSlot(new OutputSlotItemHandler(items, 5, 123, 50));
        // 升级槽
        this.addSlot(new SlotItemHandler(items, 6, 149, 22));
        this.addSlot(new SlotItemHandler(items, 7, 149, 40));

        addPlayerSlot(playerInventory, 0, 20);
    }
    public MenuCentrifuge(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(TileMachineCentrifuge.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(TileMachineCentrifuge.CONTAINER_DATA_COUNT));
    }

    @Override
    public boolean innerMovePlayer2Container(int pIndex, ItemStack itemStack) {
        boolean result = false;
        if (itemStack.is(ModTags.Items.CHARGEABLE)) result = this.moveItemStackTo(itemStack, 1, 2, false);
        else if (itemStack.is(ModTags.Items.UPGRADE)) result = this.moveItemStackTo(itemStack, 6, 8, false);
        else result = this.moveItemStackTo(itemStack, 0, 1, false);
        return result;
    }
}
