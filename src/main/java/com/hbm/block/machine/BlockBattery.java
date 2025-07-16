package com.hbm.block.machine;

import com.hbm.api.energy.fe.IHBMEnergyStorage;
import com.hbm.block.base.BaseMachineBlock;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.machine.BatteryEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockBattery extends BaseMachineBlock {
    public final BatteryType type;
    public BlockBattery(Properties pProperties, BatteryType type) {
        super(pProperties);
        this.type = type;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
//            pPlayer.openMenu(pState.getMenuProvider(pLevel,pPos));
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof BatteryEntity){
                pPlayer.openMenu((MenuProvider) blockEntity);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return pState.getBlock() instanceof BlockBattery ? new BatteryEntity(pPos, pState) : null;
    }

//    @Nullable
//    @Override
//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
//        return pBlockEntityType == ModBlockEntityType.BATTERY_ENTITY.get() ? BatteryEntity::tick : null;
//    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())) {
//            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
//            if (!pLevel.isClientSide()){
//                if (blockEntity instanceof BatteryEntity battery){
////                    ItemStack itemStack = new ItemStack(pState.getBlock());
////                    battery.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
////                        //实际上函数里找不到它的capability，具体怎么改暂时不知道
////                        itemStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(iEnergyStorage->((HBMEnergyStorage)iEnergyStorage).setEnergy(cap.getEnergyStored()));
////                    });
////                    Containers.dropItemStack(pLevel,pPos.getX(),pPos.getY(),pPos.getZ(),itemStack);
//                    Containers.dropContents(pLevel, pPos, (Container)blockEntity);
//                }
//                pLevel.removeBlockEntity(pPos);
//            }
            pLevel.removeBlockEntity(pPos);
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (!level.isClientSide()){
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BatteryEntity battery){
                if (!player.isCreative()){
                    ItemStack itemStack = new ItemStack(state.getBlock());
                    battery.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
                        //实际上函数里找不到它的capability，具体怎么改暂时不知道
                        itemStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(iEnergyStorage -> {
                            long stored = ((IHBMEnergyStorage) cap).getLongStore();
                            ((IHBMEnergyStorage)iEnergyStorage).setEnergy(stored);
                        });
                    });
                    Containers.dropItemStack(level,pos.getX(),pos.getY(),pos.getZ(),itemStack);
                }
                Containers.dropContents(level, pos, (Container)blockEntity);
            }
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
//        return super.getDrops(pState, pParams);
        return List.of();
    }

    public enum BatteryType{
        BASIC(1_000_000L,10_000L),
        LITHIUM(50_000_000L,500_000L),
        SCHRABIDIUM(25_000_000_000L,2_500_000_000L),
        DINEUTRONIUM(1_000_000_000_000L,1_000_000_000L),
        CREATIVE(Long.MAX_VALUE,Long.MAX_VALUE);
        private final long baseMaxEnergy;
        private final long baseOutput;
        BatteryType(long maxEnergy,long maxOutput){
            baseMaxEnergy = maxEnergy;
            baseOutput = maxOutput;
        }
        public long getMaxEnergy(){return baseMaxEnergy;}
        public long getOutput(){return baseOutput;}
    }
}
