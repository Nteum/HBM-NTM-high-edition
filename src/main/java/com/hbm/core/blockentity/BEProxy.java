package com.hbm.core.blockentity;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.blockentity.HBMTiles;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BEProxy extends BECapabilities {
    public BlockPos cachedPos;
    public BEProxy(BlockPos pos, BlockState state) {
        super(HBMTiles.PROXY.get(), pos, state);
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
        cachedPos = nbt.contains(HBMKey.CORE) ? NbtUtils.readBlockPos(nbt.getCompound(HBMKey.CORE)) : null;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (cachedPos == null){
            HBM.LOGGER.info("Proxy entity core pos is null, in pos {}",this.worldPosition);
            return;
        }
        pTag.put(HBMKey.CORE, NbtUtils.writeBlockPos(cachedPos));
    }
}
