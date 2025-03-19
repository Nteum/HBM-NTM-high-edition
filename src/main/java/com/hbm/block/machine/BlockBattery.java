package com.hbm.block.machine;

import com.google.common.collect.Lists;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.machine.BatteryEntity;
import com.hbm.blockentity.machine.DifurnaceEntity;
import com.hbm.item.BatteryBlockItem;
import com.hbm.modsetting.capability.Capabilities;
import com.hbm.modsetting.energy.ItemEnergyProxy;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
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

public class BlockBattery extends BaseSingleBlockMachine {
    public long maxPower;
    public BlockBattery(Properties pProperties, long maxPower) {
        super(pProperties);
        this.maxPower = maxPower;
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

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == ModBlockEntityType.BATTERY_ENTITY.get() ? BatteryEntity::tick : null;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof BatteryEntity battery){
                ItemStack itemStack = new ItemStack(pState.getBlock());
                battery.getCapability(Capabilities.ENERGY).ifPresent(cap -> ItemEnergyProxy.setEnergy(itemStack,cap));
                Containers.dropItemStack(pLevel,pPos.getX(),pPos.getY(),pPos.getZ(),itemStack);
                Containers.dropContents(pLevel, pPos, (Container)blockEntity);
            }
            pLevel.removeBlockEntity(pPos);
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
//        return super.getDrops(pState, pParams);
        return List.of();
    }
}
