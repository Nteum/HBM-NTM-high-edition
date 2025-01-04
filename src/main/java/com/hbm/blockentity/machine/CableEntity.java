package com.hbm.blockentity.machine;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CableEntity extends BlockEntity {
    public CableEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.CABLE_ENTITY.get(), pPos, pBlockState);
    }
    public static void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (pState.is(ModBlocks.RED_CABLE.get()) && pBlockEntity instanceof CableEntity cableEntity){
            Map<BlockEntity,Integer> energyNeed = new HashMap<>();
            int energyStored = cableEntity.ENERGY_STORAGE.getEnergyStored();
            PipeBlock.PROPERTY_BY_DIRECTION.forEach((dir,prop)->{
                if (pState.getValue(prop)) {
                    BlockEntity blockEntity = level.getBlockEntity(pPos.relative(dir));
                    if (blockEntity != null){
                        blockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
                            int energyStored1 = cap.getEnergyStored();
                            if (energyStored1 < energyStored && energyStored1 < cap.getMaxEnergyStored()){
                                energyNeed.put(blockEntity, energyStored1);
                            }
                        });
                    }
                }
            });
            int sum = energyNeed.values().stream().mapToInt(Integer::intValue).sum();
            int avg = (sum + energyStored) / (energyNeed.size() + 1);
            for (Map.Entry<BlockEntity, Integer> entry : energyNeed.entrySet()) {
                entry.getKey().getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
                    Integer energy = entry.getValue();
                    int receivedEnergy = cap.receiveEnergy(avg - energy, false);
                    cableEntity.ENERGY_STORAGE.extractEnergy(receivedEnergy,false);
                });
            }
//            AtomicInteger resident = new AtomicInteger();
//            energyNeed.forEach((entity,energy)->{
//                entity.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
//                    int receivedEnergy = cap.receiveEnergy((int) (avg - energy), false);
//                    resident.addAndGet(avg - energy - receivedEnergy);
//                });
//            });
//            cableEntity.ENERGY_STORAGE.receiveEnergy(avg + resident.get(), false);
        }
    }

    //=============以下是储能相关的信息=============================
    private final EnergyStorage ENERGY_STORAGE = new EnergyStorage(10000);
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY){
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(cap, side);
    }
    //=======================================================

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
    //====================管理数据加载========================
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("cable.energy",ENERGY_STORAGE.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        ENERGY_STORAGE.deserializeNBT(pTag.get("cable.energy"));
    }
}
