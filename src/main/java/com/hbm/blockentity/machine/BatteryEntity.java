package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.api.energy.*;
import com.hbm.block.machine.BlockBattery;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.registries.HBMCaps;
import com.hbm.gui.menu.BatteryMenu;
import com.hbm.registries.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
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
//    private boolean reloadFlag = false;
    private byte reloadTimer = 0;
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
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new SidedEnergyHandler(this.energyContainer));
        this.capabilitiesContent.addCapability(ForgeCapabilities.ENERGY, new HybridEnergyStorage(this.energyContainer));
    }

    private double[] powerWeight = new double[]{0.8,0.5,0.2};

    @Override
    protected void onUpdateServer() {
        this.getCapability(HBMCaps.LONG_ENERGY, null).ifPresent(ienergyhandler -> reloadTimer = ((SidedEnergyHandler) ienergyhandler).checkNeighbourIfLoad(this.getLevel(), this.getTilePos()) ? 3 : reloadTimer);
        if (reloadTimer > 0){
            reloadTimer--;
            energyReload();
        }
        super.onUpdateServer();
        BlockState blockState = this.getBlockState();
        //向接触面的方块充放电（电缆等传输通过电网进行）
        TransmitUtils.batteryTransmit(level, (SidedEnergyHandler) getCapability(HBMCaps.LONG_ENERGY).orElse(null));
        //对物品槽中的电池充放电
        ItemStack itemStack0 = getStackInSlot(0);
        ItemStack itemStack1 = getStackInSlot(1);
        TransmitUtils.dischargeItem(this, itemStack0);
        TransmitUtils.chargeItem(this, itemStack1);
        //发送更新信息
        level.sendBlockUpdated(this.worldPosition,blockState,blockState,2);
    }

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
    public Component getDefaultName() {
        return Component.translatable(HBMLang.BATTERY.key());
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new BatteryMenu(pContainerId,pInventory,this,containerData);
    }

    public long getEnergy(){
        return this.energyContainer.getEnergy();
    }

    public void onNeighbourChanged(BlockPos neighbor){
        this.reloadTimer = 3;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        energyReload();
    }

    private void energyReload(){
        this.getCapability(HBMCaps.LONG_ENERGY, null).ifPresent(ienergyhandler -> ((SidedEnergyHandler) ienergyhandler).rebuildSideData(this.getLevel(), this.getTilePos()));
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
}
