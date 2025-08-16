package com.hbm.blockentity.base2;

import com.hbm.HBMKey;
import com.hbm.utils.multiblock.HBMMultiData;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class DummyableBlockEntity extends BaseMachineBlockEntity {
    public boolean isFormed = false;
    // 不需要序列化，每次重载都需要重新分配
    public boolean distributed = false;
    HBMMultiData multiData;
    public MultiblockData multiblockData;
    public DummyableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        // 为填充方块分配能力，我本来想在onLoad里调用，然而onLoad调用时填充方块尚未被填充，因此只能放在这里。
        if (!distributed && isFormed && multiblockData!=null){
            multiblockData.distributeCaps(this);
            distributed = true;
        }
    }
    //    @Override
//    public void onLoad() {
//        super.onLoad();
//        if (isFormed && !distributed){
//            distributeCapabilities();
//            distributed = true;
//        }
//    }

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
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        isFormed = nbt.getBoolean(HBMKey.IS_FORMED);
    }
    //===================wroldly container
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
