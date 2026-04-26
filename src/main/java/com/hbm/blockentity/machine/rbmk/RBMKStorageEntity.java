package com.hbm.blockentity.machine.rbmk;

import com.hbm.api.Mode;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.item.rbmk.ItemRBMKFuelRod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class RBMKStorageEntity extends BaseMachineBlockEntity {

    public RBMKStorageEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RBMK_STORAGE_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(12, ItemStack.EMPTY);
        this.slotModes = java.util.Collections.nCopies(12, Mode.BOTH);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return stack.getItem() instanceof ItemRBMKFuelRod;
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.rbmk_storage");
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;
    }
}
