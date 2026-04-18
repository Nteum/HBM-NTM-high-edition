package com.hbm.gui.menu;

import com.hbm.Inventory.fluid.CrucibleFluidHandler;
import com.hbm.blockentity.machine.CrucibleEntity;
import com.hbm.gui.ModMenuType;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MenuCrucible extends BaseMachineMenu<CrucibleEntity>{
    public CrucibleEntity be;
    public MenuCrucible(int pContainerId, Inventory pPlayerInventory, CrucibleEntity tile, ContainerData containerData) {
        super(ModMenuType.MENU_CRUCIBLE.get(), pContainerId, tile, containerData);
        this.slotNum = 9;
        be = tile;
        ItemStackHandler itemHandler = be.getItemHandler();
        //input
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                this.addSlot(new SlotItemHandler(itemHandler, j + i * 3, 107 + j * 18, 18 + i * 18));
            }
        }
        addPlayerSlot(pPlayerInventory, 0, 48);
    }
    public MenuCrucible(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(CrucibleEntity.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(9));
    }

    @Override
    public boolean innerMovePlayer2Container(int pIndex, ItemStack itemStack) {
        int i = 0;
        while (i < slotNum && !itemStack.isEmpty() && this.moveItemStackTo(itemStack.copyWithCount(1), 0, slotNum, false)){
            itemStack.shrink(1);
            i++;
        }
        return i > 0;
    }

    public int getHeat(){
        return this.containerData.get(0);
    }

    public int getProgress(){
        return containerData.get(1);
    }

    public BlockPos getPos(){
        return this.be.getTilePos();
    }
}
