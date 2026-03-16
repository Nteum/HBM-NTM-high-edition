package com.hbm.blockentity.machine;

import com.hbm.HBMLang;
import com.hbm.gui.menu.MenuFirebox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class TileFireBox extends TileFireboxBase{
    public static int baseHeat = 100;
    public static double timeMult = 1D;
    public static int maxHeatEnergy = 100_000;
    public TileFireBox(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public int getBaseHeat() {
        return baseHeat;
    }

    @Override
    public double getTimeMult() {
        return timeMult;
    }

    @Override
    public int getMaxHeat() {
        return maxHeatEnergy;
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MenuFirebox(pContainerId, pInventory, this, containerData);
    }

    @Override
    public Component getDefaultName() {
        return HBMLang.CONTAINER_FIREBOX.translate();
    }
}
