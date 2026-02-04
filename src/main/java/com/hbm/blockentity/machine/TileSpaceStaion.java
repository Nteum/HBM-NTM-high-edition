package com.hbm.blockentity.machine;

import com.hbm.HBMLang;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.BaseMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileSpaceStaion extends BaseMachineBlockEntity {
    public TileSpaceStaion(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.TILE_SPACE_STATION.get(), pPos, pBlockState);
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;
    }

    @Override
    public Component getDefaultName() {
        return HBMLang.CONTAINER_SPACE_STATION_DOCKER.translate();
    }
}
