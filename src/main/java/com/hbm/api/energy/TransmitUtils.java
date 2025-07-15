package com.hbm.api.energy;

import com.hbm.api.energy.fe.IHBMEnergyStorage;
import com.hbm.capabilities.Capabilities;
import com.hbm.lib.MthHelper;
import com.hbm.registries.ModTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

// 处理LONG_ENERGY能量
public class TransmitUtils {
    //吸取物品槽的电力
    public static void dischargeItem(BlockEntity pBlockEntity, ItemStack itemStack){
        IEnergyHandler energyHandler = pBlockEntity.getCapability(Capabilities.LONG_ENERGY).orElse(null);
        if (energyHandler == null)return;
        IEnergyHandler itemEnergy = itemStack.getCapability(Capabilities.LONG_ENERGY).orElse(null);
        if (itemEnergy == null){
            // 物品使用forge能量的情况下，就转换成hbm能量
            IEnergyStorage FEStorage = itemStack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
            itemEnergy = new ProxyEnergyHandler(new FEAdapter(FEStorage));
        }else {
            return;
        }

        long needed = energyHandler.getEnergyContainer().getNeeded();
        energyHandler.receive(itemEnergy.extract(needed,false),false);
    }
    //为物品槽中的物品充电
    public static void chargeItem(BlockEntity pBlockEntity, ItemStack itemStack){
        IEnergyHandler energyHandler = pBlockEntity.getCapability(Capabilities.LONG_ENERGY).orElse(null);
        if (energyHandler == null)return;
        IEnergyHandler itemEnergy = itemStack.getCapability(Capabilities.LONG_ENERGY).orElse(null);
        if (itemEnergy == null){
            IEnergyStorage FEStorage = itemStack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
            itemEnergy = new ProxyEnergyHandler(new FEAdapter(FEStorage));
        }else {
            return;
        }

        energyHandler.extract(itemEnergy.receive(energyHandler.getStored(),false),false);
    }
}
