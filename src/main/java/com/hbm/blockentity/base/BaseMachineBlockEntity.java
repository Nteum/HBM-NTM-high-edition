package com.hbm.blockentity.base;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.capabilities.CapabilityCache;
import com.hbm.lib.ItemDataUtils;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toclient.UpdateTilePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseMachineBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    //机器内部存储的物品，需要在子类中初始化
    public NonNullList<ItemStack> items;
    public boolean running = false;    // 运行状态

    protected final CapabilityCache capabilitiesCache = new CapabilityCache();

    protected BaseMachineBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }
    //存储数据。会将机器中的物品保存
    //不实现这个函数，机器脱离并重新加载或者游戏重启会丢失信息
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (!pTag.contains(HBMKey.DATA, Tag.TAG_COMPOUND)) {
            pTag.put(HBMKey.DATA,new CompoundTag());
        }
//        CompoundTag dataMap = ItemDataUtils.getDataMap(pTag);
//        dataMap.put(HBMKey.CAPS, capabilitiesCache.serializeNBT());
        pTag.merge(capabilitiesCache.serializeNBT());
        if (this.items!=null){
            ContainerHelper.saveAllItems(pTag, this.items);
        }
    }
    //加载之前存储的数据。
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        CompoundTag dataMap = ItemDataUtils.getDataMapIfPresent(pTag);
        capabilitiesCache.deserializeNBT(pTag);
//        if (dataMap!=null && dataMap.contains(HBMKey.CAPS))
//            capabilitiesCache.deserializeNBT((CompoundTag) dataMap.get(HBMKey.CAPS));
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
        ((BaseMachineBlockEntity)pBlockEntity).onUpdateClient();
    }
    public static void serverTicker(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        ((BaseMachineBlockEntity)pBlockEntity).onUpdateServer();
    }

    //=======================Container==========================
    @Override
    public int getContainerSize() {
        return this.items.size();
    }
    @Override
    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    @Override
    public ItemStack getItem(int pSlot) {
        return this.items.get(pSlot);
    }
    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(this.items, pSlot, pAmount);
    }
    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        this.items.set(pSlot, pStack);
        if (!pStack.isEmpty() && pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }
        //是否任何变化都需要setChange呢？
        this.setChanged();
    }
    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(this.items, pSlot);
    }
    @Override
    public boolean stillValid(Player pPlayer) {
        return Container.stillValidBlockEntity(this, pPlayer);
    }
    @Override
    public void clearContent() {
        this.items.clear();
    }
    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return true;
    }
    //======================update=======================
    @NotNull
    public CompoundTag getReducedUpdateTag() {
        //Add the base update tag information
        return super.getUpdateTag();
    }
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        handleUpdateTag(tag);
    }
    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag) {
        //We don't want to do a full read from NBT so simply call the super's read method to let Forge do whatever
        // it wants, but don't treat this as if it was the full saved NBT data as not everything has to be synced to the client
        super.load(tag);
    }
    //方块被载入时同步数据用
    @Override
    public CompoundTag getUpdateTag() {
        return getReducedUpdateTag();
    }
    public void sendUpdatePacket() {
        sendUpdatePacket(this);
    }

    public void sendUpdatePacket(BlockEntity tracking) {
        if (level.isClientSide()) {
            HBM.LOGGER.warn("Update packet call requested from client side", new IllegalStateException());
        } else if (isRemoved()) {
            HBM.LOGGER.warn("Update packet call requested for removed tile", new IllegalStateException());
        } else {
            //Note: We use our own update packet/channel to avoid chunk trashing and minecraft attempting to rerender
            // the entire chunk when most often we are just updating a TileEntityRenderer, so the chunk itself
            // does not need to and should not be redrawn
            ModMessages.sendToAllTracking(new UpdateTilePacket(this), tracking);
        }
    }
    //==========================Capabilities==================================
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return capabilitiesCache.getCapability(cap,side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        capabilitiesCache.invalidateAll();
    }
}
