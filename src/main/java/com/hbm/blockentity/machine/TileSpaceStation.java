package com.hbm.blockentity.machine;

import com.hbm.HBMLang;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.registries.ModBlocks;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class TileSpaceStation extends DummyableBlockEntity {
    public TileSpaceStation(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.TILE_SPACE_STATION.get(), pPos, pBlockState);
        multiblockData = MultiblockData.mapping.get(ModBlocks.SPACE_STATION_BASE.get());
    }

    @Override
    public Component getDefaultName() {
        return HBMLang.CONTAINER_SPACE_STATION_DOCKER.translate();
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;
    }
}
