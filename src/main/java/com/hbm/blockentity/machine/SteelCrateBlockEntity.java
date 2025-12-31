package com.hbm.blockentity.machine;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.gui.menu.SteelCrateMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 54-slot crate implementation that reuses the base crate storage logic.
 */
public class SteelCrateBlockEntity extends IronCrateBlockEntity {

    private static final int SLOT_COUNT = 54;

    public SteelCrateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.STEEL_CRATE_ENTITY.get(), pos, state, SLOT_COUNT);
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.crate_steel");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new SteelCrateMenu(containerId, inventory, this);
    }
}
