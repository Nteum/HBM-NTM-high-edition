package com.hbm.blockentity.base2;

import com.hbm.HBMKey;
import com.hbm.api.multiblock.HBMMultiData;
import com.hbm.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class DummyableBlockEntity extends BaseMachineBlockEntity {
    public boolean isCore = false;
    public boolean isFormed = false;
    public BlockPos corePos;
    HBMMultiData multiData;
    public DummyableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // 被Block的onRemove调用
    // blockEntity会在这之后被销毁
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston){
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putBoolean(HBMKey.IS_CORE, isCore);
        pTag.putBoolean(HBMKey.IS_FORMED, isFormed);
        pTag.putIntArray(HBMKey.CORE_POS, NBTUtils.blockpos2intarr(corePos));
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        isCore = nbt.getBoolean(HBMKey.IS_CORE);
        isFormed = nbt.getBoolean(HBMKey.IS_FORMED);
        corePos = NBTUtils.intarr2blockpos(nbt.getIntArray(HBMKey.CORE_POS));
    }
}
