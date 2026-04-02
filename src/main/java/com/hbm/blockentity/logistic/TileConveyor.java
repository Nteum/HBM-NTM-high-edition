package com.hbm.blockentity.logistic;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.CapabilityBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class TileConveyor extends CapabilityBlockEntity {
    public ItemStackHandler items = new ItemStackHandler(1);
    public TileConveyor(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.TILE_CONVEYOR.get(), pPos, pBlockState);
    }
    public ItemStackHandler getItems(){
        return this.items;
    }
}
