package com.hbm.blockentity.machine;

import com.hbm.api.energy.IEnergyConductor;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.capabilities.Capabilities;
import com.hbm.api.energy.IEnergyContainer;
import com.hbm.capabilities.energy.BasicEnergyContainer;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CableEntity extends BlockEntity implements IEnergyConductor {
    public byte[] forbidDir;
    public CableEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.CABLE_ENTITY.get(), pPos, pBlockState);
        forbidDir = new byte[6];
    }
    public static void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (pState.is(ModBlocks.RED_CABLE.get()) && pBlockEntity instanceof CableEntity cableEntity){
            Map<BlockEntity,Long> energyNeed = new HashMap<>();
            long energyStored = cableEntity.ENERGY_STORAGE.getEnergy();
            PipeBlock.PROPERTY_BY_DIRECTION.forEach((dir,prop)->{
                if (pState.getValue(prop)) {
                    BlockEntity blockEntity = level.getBlockEntity(pPos.relative(dir));
                    if (blockEntity != null){
                        blockEntity.getCapability(Capabilities.ENERGY).ifPresent(cap -> {
                            long energyStored1 = cap.getEnergy();
                            if (energyStored1 < energyStored && energyStored1 < cap.getMaxEnergy()){
                                energyNeed.put(blockEntity, energyStored1);
                            }
                        });
                    }
                }
            });
            long sum = energyNeed.values().stream().mapToLong(Long::longValue).sum();
            long avg = (sum + energyStored) / (energyNeed.size() + 1);
            for (Map.Entry<BlockEntity, Long> entry : energyNeed.entrySet()) {
                entry.getKey().getCapability(Capabilities.ENERGY).ifPresent(cap -> {
                    long energy = entry.getValue();
                    long receivedEnergy = cap.insert(avg - energy);
                    cableEntity.ENERGY_STORAGE.extract(receivedEnergy);
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
    private final BasicEnergyContainer ENERGY_STORAGE = new BasicEnergyContainer(10000);
    private LazyOptional<IEnergyContainer> lazyEnergyHandler = LazyOptional.empty();
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
//        if (cap == ForgeCapabilities.ENERGY){
//            return lazyEnergyHandler.cast();
//        }
        if (cap == Capabilities.ENERGY){
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
        pTag.putByteArray("forbidDir",forbidDir);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        ENERGY_STORAGE.deserializeNBT(pTag);
        forbidDir = pTag.getByteArray("forbidDir");
    }
    //==================连接方向问题==================

    @Override
    public List<BlockPos> getConnection() {
        List<BlockPos> conn = new ArrayList<>();
        BlockPos blockPos = getBlockPos();
        BlockState blockState = getBlockState();
        if (blockState.is(ModBlocks.RED_CABLE.get())){
            PipeBlock.PROPERTY_BY_DIRECTION.forEach((direction, booleanProperty) -> {
                if (!(forbidDir[direction.get3DDataValue()]==1)&&blockState.getValue(booleanProperty)==Boolean.TRUE){
                    conn.add(blockPos.relative(direction));
                }
            });
        }
        return conn;
    }
}
