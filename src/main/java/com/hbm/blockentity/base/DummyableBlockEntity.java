package com.hbm.blockentity.base;

import com.hbm.HBMKey;
import com.hbm.utils.DirectionUtils;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class DummyableBlockEntity extends BaseMachineBlockEntity {
    private boolean isJoined = false;
    public boolean isFormed = false;
    // 不需要序列化，每次重载都需要重新分配
    public boolean distributed = false;
    public MultiblockData multiblockData;
    public DummyableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        // 为填充方块分配能力，我本来想在onLoad里调用，然而onLoad调用时填充方块尚未被填充，因此只能放在这里。
        if (isJoined) isFormed = checkProxy();
        if (!distributed && isFormed){
            distributeCapabilities();
            distributed = true;
        }
    }

    public boolean checkProxy(){
        if (this.level == null || this.multiblockData == null) {
            return false;
        }
        for (Vec3i offset : DirectionUtils.offsetRot(multiblockData.offsets, Direction.SOUTH, this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))) {
            if (!(this.level.getBlockEntity(this.getBlockPos().offset(offset)) instanceof TileProxyBase)) return false;
        }
        return true;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox() {
        if (this.multiblockData == null) {
            return super.getRenderBoundingBox();
        }
        AABB box = new AABB(this.worldPosition, this.worldPosition.offset(1, 1, 1));
        for (Vec3i offset : DirectionUtils.offsetRot(multiblockData.offsets, Direction.SOUTH, this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))) {
            BlockPos offsetPos = this.worldPosition.offset(offset);
            box = box.minmax(new AABB(offsetPos, offsetPos.offset(1, 1, 1)));
        }
        return box;
    }
    public void distributeCapabilities(){}

    public void giveProxyCapabilities(Vec3i defaultOffset, TileProxyBase proxy, Capability<?> cap, Set<Direction> directions){
        this.getCapability(cap).ifPresent(handler -> proxy.capabilitiesContent.addCapability(cap, handler, directions));
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
        pTag.putBoolean(HBMKey.JOINED, true);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        isJoined = nbt.getBoolean(HBMKey.JOINED);
        isFormed = nbt.getBoolean(HBMKey.IS_FORMED);
    }
    //===================worldly container
    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return null;
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return false;
    }
}
