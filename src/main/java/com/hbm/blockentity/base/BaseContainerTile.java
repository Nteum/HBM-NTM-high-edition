package com.hbm.blockentity.base;

import com.hbm.HBMKey;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.LockCode;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public abstract class BaseContainerTile extends CapabilityBlockEntity{
    protected LockCode lockKey = LockCode.NO_LOCK;
    protected ItemStackHandler items;
    public BaseContainerTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public ItemStackHandler getItems(){
        return this.items;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(HBMKey.ITEM, this.items.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains(HBMKey.ITEM)) this.items.deserializeNBT(nbt.getCompound(HBMKey.ITEM));
    }
}
