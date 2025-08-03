package com.hbm.blockentity.base2;

import com.hbm.api.inventory.SlotAccCtl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

/**
 * 大部分机器的父类，大量功能直接来自BaseContainerBlockEntity
 * */
public abstract class BaseMachineBlockEntity extends HBMBlockEntity implements WorldlyContainer, MenuProvider {
    //机器内部存储的物品，需要在子类中初始化
    private LockCode lockKey = LockCode.NO_LOCK;
    public NonNullList<ItemStack> items;
    public boolean running = false;    // 运行状态

    protected BaseMachineBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }
    //存储数据。会将机器中的物品保存
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        this.lockKey.addToTag(pTag);
        if (this.items!=null){
            ContainerHelper.saveAllItems(pTag, this.items);
        }
    }
    //加载之前存储的数据。
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
//        CompoundTag dataMap = ItemDataUtils.getDataMapIfPresent(pTag);
//        capabilitiesCache.deserializeNBT(pTag);
        this.lockKey = LockCode.fromTag(pTag);
        if (this.items!=null){
            this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
            ContainerHelper.loadAllItems(pTag, this.items);
        }
    }
    // 客户端更新
    protected void onUpdateClient(){}
    // 服务器更新
    protected void onUpdateServer(){}
    public static void clientTicker(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (pBlockEntity instanceof BaseMachineBlockEntity)
            ((BaseMachineBlockEntity)pBlockEntity).onUpdateClient();
    }
    public static void serverTicker(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (pBlockEntity instanceof BaseMachineBlockEntity)
            ((BaseMachineBlockEntity)pBlockEntity).onUpdateServer();
    }
    public boolean canOpen(Player pPlayer) {
        return canUnlock(pPlayer, this.lockKey, this.getDisplayName());
    }

    public static boolean canUnlock(Player pPlayer, LockCode pCode, Component pDisplayName) {
        if (!pPlayer.isSpectator() && !pCode.unlocksWith(pPlayer.getMainHandItem())) {
            pPlayer.displayClientMessage(Component.translatable("container.isLocked", pDisplayName), true);
            pPlayer.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
            return false;
        } else {
            return true;
        }
    }
    @javax.annotation.Nullable
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return this.canOpen(pPlayer) ? this.createMenu(pContainerId, pPlayerInventory) : null;
    }

    public abstract AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory);
    @NotNull
    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    //==================WorldlyContainer===================
    // 实际上我不太喜欢实现这个接口，但原版的漏斗就认这个接口
    @Override
    public int @NotNull [] getSlotsForFace(Direction pSide) {
        // 默认所有口都可以访问
        return IntStream.range(0, this.items.size()-1).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return allowInput(pIndex, pDirection) && isItemValid(pIndex, pItemStack, pDirection);
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return allowOutput(pIndex, pDirection);
    }

    @Override
    public int getContainerSize() {
        return getSlots();
    }

    @Override
    public boolean isEmpty() {
        return inventoryEmpty();
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return getStackInSlot(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
//        return extractItem(pSlot, pAmount, false);
        return ContainerHelper.removeItem(this.items, pSlot, pAmount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
//        return setStackInSlot(pSlot, ItemStack.EMPTY, null);
        return ContainerHelper.takeItem(this.items, pSlot);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        setStackInSlot(pSlot, pStack);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return Container.stillValidBlockEntity(this, pPlayer);
    }
    @Override
    public void clearContent() {
        getItems().clear();
    }
}
