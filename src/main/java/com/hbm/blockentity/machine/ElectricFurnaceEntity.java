package com.hbm.blockentity.machine;

import com.hbm.HBMLang;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ElectricFurnaceEntity extends BaseMachineBlockEntity {
    public ElectricFurnaceEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.ELECTRIC_FURNACE_ENTITY.get(), pPos, pBlockState);
    }

    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return false;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(HBMLang.ELECTRIC_FURNACE.getTranslationKey());
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;
    }
}
