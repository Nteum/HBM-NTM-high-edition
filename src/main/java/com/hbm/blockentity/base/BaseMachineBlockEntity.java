package com.hbm.blockentity.base;

import com.hbm.api.Mode;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

/**
 * 大部分机器的父类，大量功能直接来自BaseContainerBlockEntity
 * */
public abstract class BaseMachineBlockEntity extends HBMBlockEntity implements WorldlyContainer, MenuProvider {
    //机器内部存储的物品，需要在子类中初始化
    private LockCode lockKey = LockCode.NO_LOCK;
    public NonNullList<ItemStack> items = NonNullList.create(); // 默认设置为空用于避免程序崩溃
    public List<Mode> slotModes;

    public boolean running = false;    // 运行状态

    protected BaseMachineBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, this);
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
        this.lockKey = LockCode.fromTag(pTag);
        if (this.items!=null){
            this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
            ContainerHelper.loadAllItems(pTag, this.items);
        }
    }
//    // 客户端更新
//    protected void onUpdateClient(){}
//    // 服务器更新
//    protected void onUpdateServer(){}
//    public static void clientTicker(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
//        if (pBlockEntity instanceof BaseMachineBlockEntity)
//            ((BaseMachineBlockEntity)pBlockEntity).onUpdateClient();
//    }
//    public static void serverTicker(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
//        if (pBlockEntity instanceof BaseMachineBlockEntity)
//            ((BaseMachineBlockEntity)pBlockEntity).onUpdateServer();
//    }
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
    //================itemhandler====================
    @NotNull
    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public Mode getMode(int tank) {
        if (items == null || tank < 0 || tank >= items.size()) {
            return Mode.NONE;
        }
        if (slotModes == null || slotModes.isEmpty()) {
            return Mode.BOTH;
        }
        if (tank >= slotModes.size()) {
            return Mode.BOTH;
        }
        return slotModes.get(tank);
    }

    @Override
    public long getStored() {
        return super.getStored();
    }

    //==================WorldlyContainer===================
    // 实际上我不太喜欢实现这个接口，但原版的漏斗就认这个接口
    @Override
    public int @NotNull [] getSlotsForFace(Direction pSide) {
        // 默认所有口都可以访问
        if (this.items == null || this.items.isEmpty()) {
            return new int[0];
        }
        return IntStream.range(0, this.items.size()).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        // 默认实现判断这个slot是否可以输入，以及该方向是否允许物品能力。
        return allowInput(pIndex) && isItemValid(pIndex, pItemStack) && getCapability(ForgeCapabilities.ITEM_HANDLER, pDirection).isPresent();
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        // 默认实现判断这个slot是否可以输出，以及该方向是否允许物品能力。
        return allowOutput(pIndex) && getCapability(ForgeCapabilities.ITEM_HANDLER, pDirection).isPresent();
    }

    @Override
    public int getContainerSize() {
        return getSlots();
    }

    @Override
    public boolean isEmpty() {
        if (this.items == null || this.items.isEmpty()) {
            return true;
        }
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return getStackInSlot(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(this.items, pSlot, pAmount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(this.items, pSlot);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        setStackInSlot(pSlot, pStack);
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return Container.stillValidBlockEntity(this, pPlayer);
    }
    @Override
    public void clearContent() {
        this.items.clear();
    }
}
