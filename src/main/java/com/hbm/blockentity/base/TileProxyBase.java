package com.hbm.blockentity.base;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.block.interfaces.ICustomLookTooltip;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileProxyBase extends CapabilityBlockEntity implements ICustomLookTooltip {
    public BlockPos cachedPos;
    public Component lookTooltip;
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
        cachedPos = nbt.contains(HBMKey.CORE_POS) ? NbtUtils.readBlockPos(nbt.getCompound(HBMKey.CORE_POS)) : null;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (cachedPos == null){
            HBM.LOGGER.info("Proxy entity core pos is null, in pos {}",this.worldPosition);
            return;
        }
        pTag.put(HBMKey.CORE_POS, NbtUtils.writeBlockPos(cachedPos));
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = new CompoundTag();
        if (this.lookTooltip != null)
            tag.putString(HBMKey.TOOLTIP, Component.Serializer.toJson(lookTooltip));
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        if (tag.contains(HBMKey.TOOLTIP))
            this.lookTooltip = Component.Serializer.fromJson(tag.getString(HBMKey.TOOLTIP));
    }

    @Override
    public Component getLookTooltip() {
        return lookTooltip;
    }
}
