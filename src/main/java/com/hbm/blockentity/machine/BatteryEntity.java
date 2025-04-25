package com.hbm.blockentity.machine;

import com.hbm.api.NBTConstants;
import com.hbm.api.energy.fe.HBMEnergyStorage;
import com.hbm.api.energy.fe.IHBMEnergyStorage;
import com.hbm.api.energy.fe.SidedEnergyWrapper;
import com.hbm.block.machine.BlockBattery;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BatteryEntity extends BaseMachineBlockEntity implements WorldlyContainer {
    public final BlockBattery.BatteryType type;
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
                case 3 -> (int) (getEnergy().getLongStore());
                case 4 -> (int) (getEnergy().getLongStore() >> 32);
                case 5 -> (int) (getEnergy().getLongCapacity());
                case 6 -> (int) (getEnergy().getLongCapacity() >> 32);
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
        this.items = NonNullList.withSize(2, ItemStack.EMPTY);
        this.capabilitiesCache.addCapabilityResolver(new SidedEnergyWrapper(new HBMEnergyStorage(type.getMaxEnergy(),type.getOutput(),type.getOutput())));
    }
    public IHBMEnergyStorage getEnergy(){
        return (IHBMEnergyStorage) this.getCapability(ForgeCapabilities.ENERGY,null).orElse(null);
    }

    private double[] powerWeight = new double[]{0.8,0.5,0.2};
    public static void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (!level.isClientSide() && pState.is(ModTags.Blocks.BATTERY) && pBlockEntity instanceof BatteryEntity entity){
            //与周围电力交互
            if (entity.connPriority == 0){          //吸电
                for (Direction value : Direction.values()) {
                    BlockEntity blockEntity = level.getBlockEntity(pPos.relative(value));
                    if (blockEntity != null){
                        blockEntity.getCapability(Capabilities.ENERGY,value.getOpposite()).ifPresent(cap->{
                            long energyStored = cap.getEnergy();
                            long receivedEnergy = entity.getEnergy().receiveEnergy(energyStored,false);
                            cap.extract(receivedEnergy,false);
                        });
                    }
                }
            }else if (entity.connPriority > 0){    //放电
                if (entity.getEnergy().getLongStore() > 0){
                    for (Direction value : Direction.values()) {
                        BlockEntity blockEntity = level.getBlockEntity(pPos.relative(value));
                        if (blockEntity != null){
                            blockEntity.getCapability(Capabilities.ENERGY,value.getOpposite()).ifPresent(cap->{
                                if (cap.getEnergy() < cap.getMaxEnergy()){
                                    long receivedEnergy = cap.insert(entity.getEnergy().getLongStore());
                                    entity.getEnergy().extractEnergy(receivedEnergy,false);
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
                entity.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
                    IEnergyStorage iEnergyStorage = itemStack0.getCapability(ForgeCapabilities.ENERGY).orElse(null);
                    ((IHBMEnergyStorage)cap).receiveEnergy(((IHBMEnergyStorage)iEnergyStorage).extractEnergy(10000,false),false);
                });
            }
            if (itemStack1.is(ModTags.Items.BATTERY)){
                entity.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap ->{
                    IEnergyStorage iEnergyStorage = itemStack1.getCapability(ForgeCapabilities.ENERGY).orElse(null);
                    long extractEnergy = ((IHBMEnergyStorage) cap).extractEnergy(10000, true);
                    long receivedEnergy = ((IHBMEnergyStorage) iEnergyStorage).receiveEnergy(extractEnergy, false);
                    ((IHBMEnergyStorage) cap).extractEnergy(receivedEnergy, false);
                });
            }
            level.sendBlockUpdated(pPos,pState,pState,2);
        }
    }

    //===========数据===================
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (this.items!=null){
            ContainerHelper.saveAllItems(pTag, this.items);
        }
        pTag.putInt("redLow",redLow);
        pTag.putInt("redHeight",redHeight);
        pTag.putInt("connPriority",connPriority);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (this.items!=null){
            this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
            ContainerHelper.loadAllItems(pTag, this.items);
        }
        redLow = pTag.getInt("redLow");
        redHeight = pTag.getInt("redHeight");
        connPriority = pTag.getInt("connPriority");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag updateTag = super.getUpdateTag();
        return updateTag;
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
