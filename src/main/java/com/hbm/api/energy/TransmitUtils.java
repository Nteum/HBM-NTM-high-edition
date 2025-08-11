package com.hbm.api.energy;

import com.hbm.capabilities.Capabilities;
import com.hbm.item.HBMComponent;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

// 处理LONG_ENERGY能量
public class TransmitUtils {
    // 获取方块的能量，如果不是hbm能量则转换成hbm能量。
    public static IEnergyHandler getEnergyCapability(BlockEntity pBlockEntity, Direction pSide){
        IEnergyHandler energyHandler = null;
        if (pBlockEntity.getCapability(Capabilities.LONG_ENERGY, pSide).isPresent()){
            energyHandler = pBlockEntity.getCapability(Capabilities.LONG_ENERGY,pSide).orElse(null);
        }else if (pBlockEntity.getCapability(ForgeCapabilities.ENERGY,pSide).isPresent()){
            IEnergyStorage iEnergyStorage = pBlockEntity.getCapability(ForgeCapabilities.ENERGY, pSide).orElse(null);
            energyHandler = new ProxyEnergyHandler(new FEAdapter(iEnergyStorage));
        }
        return energyHandler;
    }
    //吸取物品槽的电力
    public static void dischargeItem(BlockEntity pBlockEntity, ItemStack itemStack){
        if (itemStack.isEmpty())return;
        IEnergyHandler energyHandler = pBlockEntity.getCapability(Capabilities.LONG_ENERGY).orElse(null);
        if (energyHandler == null)return;
        IEnergyHandler itemEnergy = itemStack.getCapability(Capabilities.LONG_ENERGY).orElse(null);
        if (itemEnergy == null){
            if (itemStack.getCapability(ForgeCapabilities.ENERGY).isPresent()){
                // 物品使用forge能量的情况下，就转换成hbm能量
                IEnergyStorage FEStorage = itemStack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
                itemEnergy = new ProxyEnergyHandler(new FEAdapter(FEStorage));
            }
        }

        long needed = energyHandler.getEnergyContainer().getNeeded();
        if (itemStack.is(HBMComponent.BATTERY_CREATIVE.get())){
            energyHandler.setEnergy(energyHandler.getCapacity());
            return;
        }
        energyHandler.receive(itemEnergy.extract(needed,false),false);
    }
    //为物品槽中的物品充电
    public static void chargeItem(BlockEntity pBlockEntity, ItemStack itemStack){
        if (itemStack.isEmpty())return;
        IEnergyHandler energyHandler = pBlockEntity.getCapability(Capabilities.LONG_ENERGY).orElse(null);
        if (energyHandler == null)return;
        IEnergyHandler itemEnergy = itemStack.getCapability(Capabilities.LONG_ENERGY).orElse(null);
        if (itemEnergy == null){
            if (itemStack.getCapability(ForgeCapabilities.ENERGY).isPresent()){
                // 物品使用forge能量的情况下，就转换成hbm能量
                IEnergyStorage FEStorage = itemStack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
                itemEnergy = new ProxyEnergyHandler(new FEAdapter(FEStorage));
            }
        }

        if (itemStack.is(HBMComponent.BATTERY_CREATIVE.get())){
            return;
        }
        energyHandler.extract(itemEnergy.receive(energyHandler.getStored(),false),false);
    }
    // 指某个方块仅向外提供能量，用于调试
    public static void outputOnly(BlockEntity pBlockEntity){
        IEnergyHandler energyHandler = getEnergyCapability(pBlockEntity, null);
        if (energyHandler == null || pBlockEntity.getLevel() == null)return;
        IEnergyHandler neighborHandler = null;
        for (Direction direction : Direction.values()) {
            BlockEntity neighborEntity = pBlockEntity.getLevel().getBlockEntity(pBlockEntity.getBlockPos().relative(direction));
            if (neighborEntity == null)continue;
            energyHandler = getEnergyCapability(pBlockEntity, direction);
            neighborHandler = getEnergyCapability(neighborEntity, direction.getOpposite());
            if (energyHandler == null || neighborHandler == null)continue;
            if (energyHandler.canExtract() && neighborHandler.canReceive()){
                energyHandler.extract(neighborHandler.receive(energyHandler.getStored(),false),false);
            }
        }
    }
}
