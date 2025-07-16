package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.api.energy.fe.HBMEnergyStorage;
import com.hbm.api.energy.fe.IHBMEnergyStorage;
import com.hbm.api.energy.fe.SidedEnergyWrapper;
import com.hbm.api.energy.fe.TransmitHelper;
import com.hbm.block.machine.BlockBattery;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.BaseMachineBlockEntity;
import com.hbm.capabilities.Capabilities;
import com.hbm.gui.menu.BatteryMenu;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BatteryEntity extends BaseMachineBlockEntity {
    public final BlockBattery.BatteryType type;
    public int redLow = 0;
    public int redHeight = 2;
    public int connPriority = 0;
    private static final int[] SLOTS_FOR_UP = new int[]{0};
    private static final int[] SLOTS_FOR_DOWN = new int[]{0,1};
    private static final int[] SLOTS_FOR_SIDES = new int[]{1};
    private BasicEnergyContainer energyContainer;
//    protected NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    protected final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> redLow;
                case 1 -> redHeight;
                case 2 -> connPriority;
                case 3 -> (int) (energyContainer.getEnergy());
                case 4 -> (int) (energyContainer.getEnergy() >> 32);
                case 5 -> (int) (energyContainer.getCapacity());
                case 6 -> (int) (energyContainer.getCapacity() >> 32);
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
        this.items = NonNullList.withSize(2, ItemStack.EMPTY);
        BlockBattery block = (BlockBattery)pBlockState.getBlock();
        type = block.type;
        energyContainer = new BasicEnergyContainer(type.getMaxEnergy(), type.getOutput());
        this.capabilitiesContent.addCapability(Capabilities.LONG_ENERGY, new ProxyEnergyHandler(this.energyContainer));
//        this.capabilitiesCache.addCapabilityResolver(new SidedEnergyWrapper(new HBMEnergyStorage(type.getMaxEnergy(),type.getOutput(),type.getOutput())));
    }

    private double[] powerWeight = new double[]{0.8,0.5,0.2};

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        BlockState blockState = this.getBlockState();
//        TransmitHelper.batteryTransmit(level,this.worldPosition, blockState,this);
        TransmitUtils.outputOnly(this);
        //对电池充放电
        ItemStack itemStack0 = getStackInSlot(0);
        ItemStack itemStack1 = getStackInSlot(1);
        TransmitUtils.dischargeItem(this, itemStack0);
        TransmitUtils.chargeItem(this, itemStack1);
        level.sendBlockUpdated(this.worldPosition,blockState,blockState,2);
    }

//    public static void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
//        if (!level.isClientSide() && pState.is(ModTags.Blocks.BATTERY) && pBlockEntity instanceof BatteryEntity entity){
//            TransmitHelper.batteryTransmit(level,pPos,pState,pBlockEntity);
//            //对电池充放电
//            ItemStack itemStack0 = entity.items.get(0);
//            ItemStack itemStack1 = entity.items.get(1);
//            TransmitUtils.dischargeItem(pBlockEntity, itemStack0);
//            TransmitUtils.chargeItem(pBlockEntity, itemStack1);
////            TransmitHelper.dischargeItem(pBlockEntity,itemStack0);
////            TransmitHelper.chargeItem(pBlockEntity,itemStack1);
//            level.sendBlockUpdated(pPos,pState,pState,2);
//        }
//    }

    //===========数据===================
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (this.items!=null){
            ContainerHelper.saveAllItems(pTag, this.items);
        }
        pTag.put(HBMKey.ENERGY, this.energyContainer.serializeNBT());
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
        this.energyContainer.deserializeNBT(pTag.getCompound(HBMKey.ENERGY));
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
        return Component.translatable(HBMLang.BARREL.getTranslationKey());
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
//    @Override
//    public int getContainerSize() {
//        return this.items.size();
//    }
//    @Override
//    public boolean isEmpty() {
//        for(ItemStack itemstack : this.items) {
//            if (!itemstack.isEmpty()) {
//                return false;
//            }
//        }
//        return true;
//    }
//    @Override
//    public ItemStack getItem(int pSlot) {
//        return this.items.get(pSlot);
//    }
//    @Override
//    public ItemStack removeItem(int pSlot, int pAmount) {
//        return ContainerHelper.removeItem(this.items, pSlot, pAmount);
//    }
//    @Override
//    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
//        return pStack.is(ModTags.Items.CHARGEABLE);
//    }
//    @Override
//    public void setItem(int pSlot, ItemStack pStack) {
//        this.items.set(pSlot, pStack);
//        if (!pStack.isEmpty() && pStack.getCount() > this.getMaxStackSize()) {
//            pStack.setCount(this.getMaxStackSize());
//        }
//        //是否任何变化都需要setChange呢？
//        this.setChanged();
//    }
//    @Override
//    public ItemStack removeItemNoUpdate(int pSlot) {
//        return ContainerHelper.takeItem(this.items, pSlot);
//    }
//    @Override
//    public boolean stillValid(Player pPlayer) {
//        return Container.stillValidBlockEntity(this, pPlayer);
//    }
//    @Override
//    public void clearContent() {
//        this.items.clear();
//    }

    //====================================
}
