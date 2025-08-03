package com.hbm.blockentity.base2;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.utils.NBTUtils;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileProxyBase extends CapabilityBlockEntity{
    public BlockPos cachedPos;
    public TileProxyBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BlockEntity getBlockEntity(){
        if (cachedPos == null)
            return null;
        BlockEntity tile;
        if (this.hasLevel() && (tile = WorldUtils.getTileEntity(this.getLevel(), cachedPos))!=null){
            return tile;
        }else {
            return null;
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        cachedPos = NBTUtils.intarr2blockpos(nbt.getIntArray(HBMKey.CORE_POS));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (cachedPos == null){
            HBM.LOGGER.info("Proxy entity core pos is null, in pos {}",this.worldPosition);
        }
        pTag.putIntArray(HBMKey.CORE_POS, NBTUtils.blockpos2intarr(cachedPos));
    }
}
