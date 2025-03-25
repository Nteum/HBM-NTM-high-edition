package com.hbm.blockentity.machine;

import com.hbm.api.NBTConstants;
import com.hbm.block.machine.BlockBattery;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.capabilities.energy.BasicEnergyContainer;
import com.hbm.gui.menu.BatteryMenu;
import com.hbm.capabilities.Capabilities;
import com.hbm.api.energy.IEnergyContainer;
import com.hbm.api.energy.ItemEnergyProxy;
import com.hbm.registries.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BatteryEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    private final BasicEnergyContainer ENERGY_STORAGE;
    public final BlockBattery.BatteryType type;
    private LazyOptional<IEnergyContainer> lazyEnergyHandler = LazyOptional.empty();
    public int redLow = 0;
    public int redHeight = 2;
    public int connPriority = 0;
    private static final int[] SLOTS_FOR_UP = new int[]{0};
    private static final int[] SLOTS_FOR_DOWN = new int[]{0,1};
    private static final int[] SLOTS_FOR_SIDES = new int[]{1};
    protected NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    protected final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> redLow;
                case 1 -> redHeight;
                case 2 -> connPriority;
                case 3 -> (int) (ENERGY_STORAGE.getEnergy());
                case 4 -> (int) (ENERGY_STORAGE.getEnergy() >> 32);
                case 5 -> (int) (ENERGY_STORAGE.getMaxEnergy());
                case 6 -> (int) (ENERGY_STORAGE.getMaxEnergy() >> 32);
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex){
                case 0 -> redLow = pValue;
                case 1 -> redHeight = pValue;
                case 2 -> connPriority = pValue;
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };
    public BatteryEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.BATTERY_ENTITY.get(), pPos, pBlockState);
        BlockBattery block = (BlockBattery)pBlockState.getBlock();
        type = block.type;
        ENERGY_STORAGE = new BasicEnergyContainer(type.getMaxEnergy(),type.getOutput(),type.getOutput());
        items = NonNullList.withSize(2, ItemStack.EMPTY);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == Capabilities.ENERGY){
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(cap, side);
    }
    private double[] powerWeight = new double[]{0.8,0.5,0.2};
    public static void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (!level.isClientSide() && pState.is(ModTags.Blocks.BATTERY) && pBlockEntity instanceof BatteryEntity entity){
            //与周围电力交互
//            for (Direction value : Direction.values()) {
//                BlockEntity blockEntity = level.getBlockEntity(pPos.relative(value));
//                if (blockEntity != null){
//                    blockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap->{
//                        int energyStored = cap.getEnergyStored();
//                        int receivedEnergy = entity.ENERGY_STORAGE.receiveEnergy(energyStored, false);
//                        cap.extractEnergy(receivedEnergy,false);
//                    });
//                }
//            }
            if (entity.connPriority == 0){          //吸电
                for (Direction value : Direction.values()) {
                    BlockEntity blockEntity = level.getBlockEntity(pPos.relative(value));
                    if (blockEntity != null){
                        blockEntity.getCapability(Capabilities.ENERGY).ifPresent(cap->{
                            long energyStored = cap.getEnergy();
                            long receivedEnergy = entity.ENERGY_STORAGE.insert(energyStored);
                            cap.extract(receivedEnergy);
                        });
                    }
                }
            }else if (entity.connPriority == 2){    //放电
                if (entity.ENERGY_STORAGE.getEnergy() > 0){
                    for (Direction value : Direction.values()) {
                        BlockEntity blockEntity = level.getBlockEntity(pPos.relative(value));
                        if (blockEntity != null){
                            blockEntity.getCapability(Capabilities.ENERGY).ifPresent(cap->{
                                if (cap.getEnergy() < cap.getMaxEnergy()){
                                    long receivedEnergy = cap.insert(entity.ENERGY_STORAGE.getEnergy());
                                    entity.ENERGY_STORAGE.extract(receivedEnergy);
                                }
                            });
                        }
                    }
                }
            }
            //对电池充放电
            ItemStack itemStack0 = entity.items.get(0);
            ItemStack itemStack1 = entity.items.get(1);
            if (itemStack0.is(ModTags.Items.BATTERY)){
                entity.getCapability(Capabilities.ENERGY).ifPresent(cap -> cap.insert(ItemEnergyProxy.disCharge(itemStack0)));
            }
            if (itemStack1.is(ModTags.Items.BATTERY)){
                entity.getCapability(Capabilities.ENERGY).ifPresent(cap ->{
                    ItemEnergyProxy.charge(itemStack1, cap);
                });
            }
            level.sendBlockUpdated(pPos,pState,pState,2);
        }
    }

    //===========数据===================
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        ContainerHelper.saveAllItems(pTag, this.items);
        pTag.put(NBTConstants.ENERGY,ENERGY_STORAGE.serializeNBT());
//        pTag.put("battery.energy",ENERGY_STORAGE.serializeNBT());
        pTag.putInt("redLow",redLow);
        pTag.putInt("redHeight",redHeight);
        pTag.putInt("connPriority",connPriority);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        ENERGY_STORAGE.deserializeNBT((CompoundTag) pTag.get(NBTConstants.ENERGY));
        redLow = pTag.getInt("redLow");
        redHeight = pTag.getInt("redHeight");
        connPriority = pTag.getInt("connPriority");
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, this.items);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag updateTag = super.getUpdateTag();
        return updateTag;
    }

//    @Override
//    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
//        ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(this);
//        CompoundTag tag = packet.getTag();
//        assert tag != null;
//        tag.putLong("battery.energy",ENERGY_STORAGE.getEnergy());
//        tag.putLong("battery.capacity",ENERGY_STORAGE.getMaxEnergy());
//        return packet;
//    }
//
//    @Override
//    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
//        super.onDataPacket(net, pkt);
//        CompoundTag tag = pkt.getTag();
//        assert tag != null;
//        ENERGY_STORAGE.setEnergy(tag.getLong("battery.energy"));
//        ENERGY_STORAGE.setMaxEnergy(tag.getLong("battery.capacity"));
//    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
//        ENERGY_STORAGE.setEnergy(tag.getLong("battery.energy"));
//        ENERGY_STORAGE.setMaxEnergy(tag.getLong("battery.capacity"));
    }

    //方块加入世界的时候会被调用。
    @Override
    public void onLoad() {
        super.onLoad();
        lazyEnergyHandler = LazyOptional.of(()->ENERGY_STORAGE);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyEnergyHandler.invalidate();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.battery");
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new BatteryMenu(pContainerId,pInventory,this,containerData);
    }
    //======================WorldlyContainer=======================
    @Override
    public int[] getSlotsForFace(Direction pSide) {
        if (pSide == Direction.UP)return SLOTS_FOR_UP;
        else if (pSide == Direction.DOWN)return SLOTS_FOR_DOWN;
        else return SLOTS_FOR_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return items.get(pIndex).isEmpty() && pItemStack.is(ModTags.Items.BATTERY);
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        if (pIndex == 1 && pDirection == Direction.DOWN){
            return pStack.is(ModTags.Items.BATTERY);
        }else {
            return true;
        }
    }
    //====================================================
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
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return pStack.is(ModTags.Items.CHARGEABLE);
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

    //====================================

}
