package com.hbm.blockentity.machine;

import com.hbm.blockentity.ModBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AssemblerEntity extends BaseMachineBlockEntity {
    public AssemblerEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.ASSEMBLER_ENTITY.get(), pPos, pBlockState);
        items = NonNullList.withSize(10,ItemStack.EMPTY);
    }

    @Override
    protected Component getDefaultName() {
        return null;
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {

    }
}
