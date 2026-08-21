package com.hbm.core.blockentity;

import com.hbm.HBMKey;
import com.hbm.blockentity.base.TileProxyBase;
import com.hbm.core.block.BlockDummyable;
import com.hbm.utils.DirectionUtils;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.core.contents.multiblock.MultiblockModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 新的模板，默认作用于处理配方的机器上
 * */
public abstract class BEDummyable extends BEMachineBase {

    protected MultiblockModule multiblockModule;
    public BEDummyable(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    /** 从 BlockDummyable 的 MultiblockData 构建 module 并启用多方块 */
    protected void setMultiblockData(Class<? extends BlockDummyable> clazz){
        setMultiblockData(clazz.cast(this.getBlockState().getBlock()).getMultiblockData());
    }
    protected void setMultiblockData(MultiblockData data){
        this.multiblockModule = new MultiblockModule(data);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (multiblockModule != null){
            if (multiblockModule.distributed){
                // 已分配能力：每次 tick 复查代理是否完好，缺失则重置状态，等待重新成型
                if (!checkProxy()){
                    multiblockModule.isFormed = false;
                    multiblockModule.distributed = false;
                }
            }else if (checkProxy()){
                // 尚未分配能力且代理齐备：成型并分配
                multiblockModule.isFormed = true;
                distributeCapabilities();
                multiblockModule.distributed = true;
            }
        }
    }
    public boolean checkProxy(){
        if (!this.hasLevel() || this.multiblockModule == null) return false;
        for (Vec3i offset : DirectionUtils.offsetRot(multiblockModule.offsets, Direction.SOUTH, this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))) {
            BlockEntity proxy = this.level.getBlockEntity(this.getBlockPos().offset(offset));
            if (!(proxy instanceof BEProxy) && !(proxy instanceof TileProxyBase)) return false;
        }
        return true;
    }

    public void setFormed(boolean isFormed){
        this.multiblockModule.isFormed = isFormed;
    }

    /** 将核心能力下发到各代理方块（默认按 MultiblockData 的 capsMap 分配） */
    public void distributeCapabilities(){
        if (this.multiblockModule != null) this.multiblockModule.distributeCaps(this);
    }

    /** 给某个代理方块添加一项能力（由 distributeCapabilities 调用） */
    public void giveProxyCapabilities(Vec3i defaultOffset, TileProxyBase proxy, Capability<?> cap, Set<Direction> directions){
        this.getCapability(cap).ifPresent(handler -> proxy.capabilitiesContent.addCapability(cap, handler, directions));
    }

    public void onLeftClick(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit){
    }

    @Override
    public @NotNull AABB getRenderBoundingBox() {
        if (this.multiblockModule == null) {
            return super.getRenderBoundingBox();
        }
        AABB box = new AABB(this.worldPosition, this.worldPosition.offset(1, 1, 1));
        for (Vec3i offset : DirectionUtils.offsetRot(multiblockModule.offsets, Direction.SOUTH, this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))) {
            BlockPos offsetPos = this.worldPosition.offset(offset);
            box = box.minmax(new AABB(offsetPos, offsetPos.offset(1, 1, 1)));
        }
        return box;
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
