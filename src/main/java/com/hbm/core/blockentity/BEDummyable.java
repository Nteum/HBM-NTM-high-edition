package com.hbm.core.blockentity;

import com.hbm.HBMKey;
import com.hbm.utils.DirectionUtils;
import com.hbm.core.contents.multiblock.MultiblockModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

/**
 * 新的模板，默认作用于处理配方的机器上
 * */
public abstract class BEDummyable extends BEMachineBase {

    protected MultiblockModule multiblockModule;
    public BEDummyable(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (multiblockModule != null){
            if (!multiblockModule.distributed && (multiblockModule.isFormed = multiblockModule.isFormed && checkProxy())){
                distributeCapabilities();
                multiblockModule.distributed = true;
            }
        }
    }
    public boolean checkProxy(){
        if (!this.hasLevel() || this.multiblockModule == null) return false;
        for (Vec3i offset : DirectionUtils.offsetRot(multiblockModule.offsets, Direction.SOUTH, this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))) {
            if (!(this.level.getBlockEntity(this.getBlockPos().offset(offset)) instanceof BEProxy)) return false;
        }
        return true;
    }

    public void setFormed(boolean isFormed){
        this.multiblockModule.isFormed = isFormed;
    }

    public void distributeCapabilities(){}

    public void onLeftClick(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit){
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (this.multiblockModule != null)
            pTag.putBoolean(HBMKey.IS_FORM, this.multiblockModule.isFormed);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains(HBMKey.IS_FORM) && this.multiblockModule != null)
            this.multiblockModule.isFormed = nbt.getBoolean(HBMKey.IS_FORM);
    }
}
