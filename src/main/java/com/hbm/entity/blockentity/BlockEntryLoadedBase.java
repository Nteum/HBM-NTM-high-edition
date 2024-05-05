package com.hbm.entity.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

//HBM自定义的方块实体中最大的父类，似乎是用来管方块的加载的，但在最新版本中似乎还没找到相关方块加载的部分。
public class BlockEntryLoadedBase extends BlockEntity implements ILoadedBlock{
    public boolean isLoaded = true;
    public boolean muffled = false;
    public BlockEntryLoadedBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }


}
