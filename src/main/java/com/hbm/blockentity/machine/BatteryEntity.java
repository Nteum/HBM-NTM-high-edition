package com.hbm.blockentity.machine;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.gui.menu.BatteryMenu;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModTags;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class BatteryEntity extends BaseMachineBlockEntity{
    private final EnergyStorage ENERGY_STORAGE;
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
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
                case 0 -> ENERGY_STORAGE.getEnergyStored();
                case 1 -> ENERGY_STORAGE.getMaxEnergyStored();
                case 2 -> redLow;
                case 3 -> redHeight;
                case 4 -> connPriority;
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex){
                case 2 -> redLow = pValue;
                case 3 -> redHeight = pValue;
                case 4 -> connPriority = pValue;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };
    public BatteryEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.BATTERY_ENTITY.get(), pPos, pBlockState);
        ENERGY_STORAGE = new EnergyStorage(10000);
    }
    public BatteryEntity(BlockPos pPos, BlockState pBlockState, int capacity) {
        super(ModBlockEntityType.BATTERY_ENTITY.get(), pPos, pBlockState);
        ENERGY_STORAGE = new EnergyStorage(capacity);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY){
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    public static void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (pState.is(ModTags.Blocks.BATTERY) && pBlockEntity instanceof BatteryEntity entity){
            if (entity.connPriority == 0){          //吸电
                for (Direction value : Direction.values()) {
                    BlockEntity blockEntity = level.getBlockEntity(pPos.relative(value));
                    if (blockEntity != null){
                        blockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap->{
                            int energyStored = cap.getEnergyStored();
                            int receivedEnergy = entity.ENERGY_STORAGE.receiveEnergy(energyStored, true);
                            cap.extractEnergy(receivedEnergy,true);
                        });
                    }
                }
            }else if (entity.connPriority == 2){    //放电
                if (entity.ENERGY_STORAGE.getEnergyStored() > 0){
                    for (Direction value : Direction.values()) {
                        BlockEntity blockEntity = level.getBlockEntity(pPos.relative(value));
                        if (blockEntity != null){
                            blockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap->{
                                if (cap.getEnergyStored() < cap.getMaxEnergyStored()){
                                    int receivedEnergy = cap.receiveEnergy(entity.ENERGY_STORAGE.getEnergyStored(), true);
                                    entity.ENERGY_STORAGE.extractEnergy(receivedEnergy,true);
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    //===========数据===================
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("battery.energy",ENERGY_STORAGE.serializeNBT());
        pTag.putInt("redLow",redLow);
        pTag.putInt("redHeight",redHeight);
        pTag.putInt("connPriority",connPriority);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        ENERGY_STORAGE.deserializeNBT(pTag.get("battery.energy"));
        redLow = pTag.getInt("redLow");
        redHeight = pTag.getInt("redHeight");
        connPriority = pTag.getInt("connPriority");
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
}
