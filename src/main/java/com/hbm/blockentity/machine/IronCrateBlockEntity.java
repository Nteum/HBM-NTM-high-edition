package com.hbm.blockentity.machine;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.gui.menu.IronCrateMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;

/**
 * Base implementation for storage crates that preserve their inventory when broken.
 */
public class IronCrateBlockEntity extends BaseMachineBlockEntity {

    private static final int DEFAULT_SLOT_COUNT = 36;
    private final int slotCount;

    public IronCrateBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntityType.IRON_CRATE_ENTITY.get(), pos, state, DEFAULT_SLOT_COUNT);
    }

    protected IronCrateBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int slotCount) {
        super(type, pos, state);
        this.slotCount = slotCount;
        this.items = NonNullList.withSize(slotCount, ItemStack.EMPTY);
        this.slotModes = Collections.emptyList();
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.crate_iron");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new IronCrateMenu(containerId, inventory, this);
    }

    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void saveToItem(ItemStack stack) {
        CompoundTag beTag = new CompoundTag();
        ContainerHelper.saveAllItems(beTag, this.items, true);
        if (!beTag.isEmpty()) {
            stack.addTagElement("BlockEntityTag", beTag);
        }
        if (this.hasCustomName()) {
            stack.setHoverName(this.getCustomName());
        }
    }

    public void loadFromItem(ItemStack stack) {
        CompoundTag beTag = stack.getTagElement("BlockEntityTag");
        if (beTag != null) {
            ContainerHelper.loadAllItems(beTag, this.items);
        }
        if (stack.hasCustomHoverName()) {
            this.setCustomName(stack.getHoverName());
        }
    }

    protected int getSlotCount() {
        return slotCount;
    }
    
}
