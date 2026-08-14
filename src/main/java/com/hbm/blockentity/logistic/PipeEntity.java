package com.hbm.blockentity.logistic;

import com.hbm.HBMKey;
import com.hbm.blockentity.HBMTiles;
import com.hbm.blockentity.base.BasePipeBlockEntity;
import com.hbm.core.contents.transport_net.FluidBackupSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class PipeEntity extends BasePipeBlockEntity {
    public FluidBackupSystem.NetWork network;
    int oldColor = -1;
    @OnlyIn(Dist.CLIENT) private Fluid clientFluid = Fluids.EMPTY;

    public PipeEntity(BlockPos pPos, BlockState pBlockState) {
        super(HBMTiles.PIPE_ENTITY.get(), pPos, pBlockState);
    }

    public Fluid getFluid(){
        return network != null ? network.getFluid() : Fluids.EMPTY;
    }
    @OnlyIn(Dist.CLIENT)
    public Fluid getClientFluid(){
        return this.clientFluid;
    }

    public void syncToClient(){
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
        sendUpdatePacket();
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        tag.put(HBMKey.FLUIDS, new FluidStack(this.getFluid(), 1000).writeToNBT(new CompoundTag()));
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        if (tag.contains(HBMKey.FLUIDS, Tag.TAG_COMPOUND)) {
            this.clientFluid = FluidStack.loadFluidStackFromNBT(tag.getCompound(HBMKey.FLUIDS)).getFluid();
            int newColor = this.clientFluid == Fluids.EMPTY ? -1 : IClientFluidTypeExtensions.of(this.clientFluid).getTintColor();
            if (newColor != this.oldColor) {
                this.oldColor = newColor;
                // 3. 【核心修复】：通知渲染引擎，这个方块的渲染数据过期了，需要重新绘制方块模型
                BlockState state = this.getBlockState();
                // 仅仅调用这个可能不够灵敏，最稳妥的是同时触发底层区块的物理重绘
                this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_CLIENTS);
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.hasLevel() && !this.getLevel().isClientSide()) {
            FluidBackupSystem.getOrCreate(this.getLevel()).join(this);
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (this.hasLevel() && !this.getLevel().isClientSide()) {
            FluidBackupSystem.getOrCreate(this.getLevel()).leave(this);
        }
    }

    // 获取流体颜色
    public int getFluidColor() {
        Fluid fluid = getClientFluid();
        // 纯粹返回颜色，不要在这里做任何 sendBlockUpdated 的操作
        this.oldColor = fluid == Fluids.EMPTY ? -1 : IClientFluidTypeExtensions.of(fluid).getTintColor();
        return this.oldColor;
    }
}
