package com.hbm.blockentity.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class BaseMachineBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    //机器内部存储的物品，需要在子类中初始化
    public NonNullList<ItemStack> items;
    protected BaseMachineBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }
    //存储数据。会将机器中的物品保存
    //不实现这个函数，机器脱离并重新加载或者游戏重启会丢失信息
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        ContainerHelper.saveAllItems(pTag, this.items);
    }
    //加载之前存储的数据。
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, this.items);
    }
    //为了和客户端同步，服务端发送的数据包
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(this);
        return packet;
    }
    //客户端接收数据包（注意：服务端和客户端的实体时不一样的，比如客户端的实体地址24999，服务端可以是25068，虽然同一个类，但有两个实例）
    //方块实体渲染器调用的就是客户端，根据需要进行客户端同步，不是所有数据都需要和客户端同步
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
    }
    //方块被载入时同步数据用
    @Override
    public CompoundTag getUpdateTag() {
        return super.getUpdateTag();
    }
    //获取机器容器的大小（也就是物品槽的个数）
    @Override
    public int getContainerSize() {
        return this.items.size();
    }
    //判断机器是否为空（默认所有物品槽必须都有物品才不为空）
    @Override
    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    //从物品槽中获取物品
    @Override
    public ItemStack getItem(int pSlot) {
        return this.items.get(pSlot);
    }
    //从物品槽中移除物品
    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(this.items, pSlot, pAmount);
    }
    //放入物品（我们默认只是完成放入物品的过程，具体放入过程造成哪些参数后续变化，在子类中实现）
    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        this.items.set(pSlot, pStack);
        if (!pStack.isEmpty() && pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }
        //是否任何变化都需要setChange呢？
        this.setChanged();
    }
    //移除物品且不更新
    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(this.items, pSlot);
    }
    //玩家是否可以打开机器（含义待定）
    @Override
    public boolean stillValid(Player pPlayer) {
        return Container.stillValidBlockEntity(this, pPlayer);
    }
    //清空物品槽内容
    @Override
    public void clearContent() {
        this.items.clear();
    }
    //判断自动输入的物品是否合适
    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return false;
    }

    //从特定面获得slot
    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return new int[0];
    }
    //可以通过某个面输入物品
    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return canPlaceItem(pIndex,pItemStack);
    }
    //判断是否可以通过某个面输出物品
    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return false;
    }
}
