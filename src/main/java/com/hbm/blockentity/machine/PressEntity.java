package com.hbm.blockentity.machine;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.gui.menu.PressMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PressEntity extends BaseContainerBlockEntity {
    public int speed = 0; // speed ticks up once (or four times if preheated) when operating
    public static final int maxSpeed = 400; // max speed ticks for acceleration
    public static final int progressAtMax = 25; // max progress speed when hot
    public int burnTime = 0; // burn ticks of the loaded fuel, 200 ticks equal one operation
    public int press; // extension of the press, operation is completed if maxPress is reached
    public double renderPress; // client-side version of the press var, a double for smoother rendering
    public double lastPress; // for interp
    private int syncPress; // for interp
    private int turnProgress; // for interp 3: revenge of the sith
    public final static int maxPress = 200; // max tick count per operation assuming speed is 1
    boolean isRetracting = false; // direction the press is currently going
    private int delay; // delay between direction changes to look a bit more appealing
    public NonNullList<ItemStack> items = NonNullList.withSize(4,ItemStack.EMPTY);
    protected final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return 0;
        }

        @Override
        public void set(int pIndex, int pValue) {

        }

        @Override
        public int getCount() {
            return 2;
        }
    };
    public PressEntity( BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.PRESS_ENTITY.get(), pPos, pBlockState);
    }
    //每tick调用的函数，里面是运行逻辑
    public static void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        PressEntity entity = (PressEntity)pBlockEntity;
        level.sendBlockUpdated(pPos,level.getBlockState(pPos),level.getBlockState(pPos),3);

        if (entity.getItem(2) != ItemStack.EMPTY){
            if (!entity.isRetracting&&entity.press < 100)entity.press++;
            else if (!entity.isRetracting&&entity.press == 100)entity.isRetracting = true;
            else if (entity.isRetracting&&entity.press > 0) entity.press--;
            else if (entity.press == 0)entity.isRetracting = false;
        }else{
            if (entity.press > 0)entity.press--;
            if (entity.isRetracting&&entity.press==0)entity.isRetracting = false;
        }

    }
    //服务端发送数据包
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(this);
        return packet;
    }

    //客户端接收数据包（注意：服务端和客户端的实体时不一样的，比如客户端的实体地址24999，服务端可以是25068，虽然同一个类，但有两个实例）
    //方块实体渲染器调用的就是客户端
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        CompoundTag tag = pkt.getTag();
        ItemStack itemStack = ItemStack.of(tag);
        this.items.set(2,itemStack);
        press = tag.getInt("press");
        speed = tag.getInt("speed");
        burnTime = tag.getInt("burnTime");
        isRetracting = tag.getBoolean("isRetracting");

//        handleUpdateTag(pkt.getTag());
    }
    //方块被载入时同步数据用
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag updateTag = super.getUpdateTag();
        this.getItem(2).save(updateTag);
        updateTag.putInt("press",press);
        updateTag.putInt("speed",speed);
        updateTag.putInt("burnTime",burnTime);
        updateTag.putBoolean("isRetracting",isRetracting);
        return updateTag;
    }
    //存储数据
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        ContainerHelper.saveAllItems(pTag, this.items);
    }
    //加载数据
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, this.items);
    }
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("hbmxx.container.press");
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new PressMenu(pContainerId,pInventory,this,containerData);
    }

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
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(this.items, pSlot);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        ItemStack itemStack = this.items.get(pSlot);
        boolean flag = !pStack.isEmpty() && ItemStack.isSameItemSameTags(itemStack,pStack);
        this.items.set(pSlot,pStack);
        if (pStack.getCount() > this.getMaxStackSize()){
            pStack.setCount(this.getMaxStackSize());
        }

        if ((pSlot == 1 || pSlot == 2) && !flag){
            this.press = 0;
            this.renderPress = 0;
            this.setChanged();  //标识数据改变，需要保存
        }
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
        if (pIndex == 3)return false;
        else return true;
    }
}
