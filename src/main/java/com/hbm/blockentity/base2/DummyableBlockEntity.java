package com.hbm.blockentity.base2;

import com.hbm.HBMKey;
import com.hbm.api.multiblock.HBMMultiData;
import com.hbm.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public abstract class DummyableBlockEntity extends BaseMachineBlockEntity {
    public boolean isFormed = false;
    HBMMultiData multiData;
    public DummyableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // 被Block的onRemove调用
    // blockEntity会在这之后被销毁
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston){
    }

    public void onLeftClick(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit){
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putBoolean(HBMKey.IS_FORMED, isFormed);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        isFormed = nbt.getBoolean(HBMKey.IS_FORMED);
    }
}
