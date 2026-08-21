package com.hbm.core.capability.energy;

import com.hbm.api.energy.IEnergyHandler;
import com.hbm.core.api.capability.HBMEnergyHandler;
import com.hbm.registries.HBMCaps;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

/**
 * 能源功能工具类，以新框架 HBMEnergyHandler 为核心。
 * 提供物品充放电、机器间充放电、多接收者分配等常用功能，并兼容 Forge Energy。
 */
public class EnergyTransferUtils {
    private EnergyTransferUtils() {}

    //====================== handler 获取与适配 ======================

    /** 从方块/物品上获取能量 handler：优先旧体系 HBM 自定义能量（HBMCaps.LONG_ENERGY），其次适配 Forge Energy。 */
    public static HBMEnergyHandler getEnergyHandler(ICapabilityProvider provider, Direction side){
        if (provider == null) return null;
        LazyOptional<IEnergyHandler> legacy = provider.getCapability(HBMCaps.LONG_ENERGY, side);
        if (legacy.isPresent()){
            IEnergyHandler handler = legacy.orElse(null);
            if (handler != null)
                return handler instanceof HBMEnergyHandler hbm ? hbm : new LegacyEnergyAdapter(handler);
        }
        IEnergyStorage fe = provider.getCapability(ForgeCapabilities.ENERGY, side).orElse(null);
        return fe == null ? null : new FEEnergyAdapter(fe);
    }
    /** 从方块/物品上获取能量 handler（不带方向，用于物品或内部交互）。 */
    public static HBMEnergyHandler getEnergyHandler(ICapabilityProvider provider){
        return getEnergyHandler(provider, null);
    }
    /** 获取某方块某面的能量 handler，兼容旧体系（IEnergyHandler/HBMCaps.LONG_ENERGY）与新 Forge Energy。 */
    public static HBMEnergyHandler getNeighborHandler(BlockEntity be, Direction side){
        if (be == null) return null;
        return getEnergyHandler(be, side);
    }

    //====================== 物品充放电 ======================

    /** 从物品中提取能量给机器（物品 -> 机器）。返回实际提取量。 */
    public static long dischargeItem(HBMEnergyHandler machine, ItemStack itemStack){
        if (machine == null || itemStack == null || itemStack.isEmpty()) return 0;
        HBMEnergyHandler itemEnergy = getEnergyHandler(itemStack);
        if (itemEnergy == null) return 0;
        long toTransfer = machine.getNeeded();
        if (toTransfer <= 0) return 0;
        long extracted = itemEnergy.extract(toTransfer, true);
        if (extracted <= 0) return 0;
        long received = machine.receive(extracted, false);
        if (received > 0)
            itemEnergy.extract(received, false);
        return received;
    }
    /** 给物品充电（机器 -> 物品）。返回实际充入量。 */
    public static long chargeItem(HBMEnergyHandler machine, ItemStack itemStack){
        if (machine == null || itemStack == null || itemStack.isEmpty()) return 0;
        HBMEnergyHandler itemEnergy = getEnergyHandler(itemStack);
        if (itemEnergy == null) return 0;
        long toTransfer = itemEnergy.getNeeded();
        if (toTransfer <= 0) return 0;
        long extracted = machine.extract(toTransfer, true);
        if (extracted <= 0) return 0;
        long received = itemEnergy.receive(extracted, false);
        if (received > 0)
            machine.extract(received, false);
        return received;
    }

    //====================== 机器间充放电 ======================

    /** 从另一机器拉取能量（source -> this，模拟 then 执行）。返回实际传输量。 */
    public static long receiveFromMachine(HBMEnergyHandler machine, HBMEnergyHandler source){
        return receiveFromMachine(machine, source, Long.MAX_VALUE);
    }
    /** 从另一机器拉取指定量能量。 */
    public static long receiveFromMachine(HBMEnergyHandler machine, HBMEnergyHandler source, long maxReceive){
        if (machine == null || source == null) return 0;
        long toReceive = Math.min(maxReceive, Math.min(source.getEnergy(), machine.getNeeded()));
        if (toReceive <= 0) return 0;
        long extracted = source.extract(toReceive, true);
        if (extracted <= 0) return 0;
        long received = machine.receive(extracted, false);
        if (received > 0)
            source.extract(received, false);
        return received;
    }
    /** 向另一机器输出能量（this -> target）。返回实际传输量。 */
    public static long sendToMachine(HBMEnergyHandler machine, HBMEnergyHandler target){
        return sendToMachine(machine, target, Long.MAX_VALUE);
    }
    /** 向另一机器输出指定量能量。 */
    public static long sendToMachine(HBMEnergyHandler machine, HBMEnergyHandler target, long maxSend){
        if (machine == null || target == null) return 0;
        long toSend = Math.min(maxSend, Math.min(machine.getEnergy(), target.getNeeded()));
        if (toSend <= 0) return 0;
        long extracted = machine.extract(toSend, true);
        if (extracted <= 0) return 0;
        long received = target.receive(extracted, false);
        if (received > 0)
            machine.extract(received, false);
        return received;
    }

    //====================== 多接收者分配 ======================

    /** 将本机器的能量分配给多个接收者，按各接收者需求比例分配，直到耗尽。返回实际分配总量。 */
    public static long distributeToMultiple(HBMEnergyHandler source, List<HBMEnergyHandler> receivers){
        return distributeToMultiple(source, receivers, Long.MAX_VALUE);
    }
    /** 将本机器的能量分配给多个接收者，最大分配 maxSend。 */
    public static long distributeToMultiple(HBMEnergyHandler source, List<HBMEnergyHandler> receivers, long maxSend){
        if (source == null || receivers == null || receivers.isEmpty() || maxSend <= 0) return 0;
        long totalSend = Math.min(maxSend, source.getEnergy());
        if (totalSend <= 0) return 0;
        // 先模拟计算每个接收者的需求占比
        long[] needs = new long[receivers.size()];
        long needSum = 0;
        for (int i = 0; i < receivers.size(); i++) {
            HBMEnergyHandler target = receivers.get(i);
            needs[i] = target == null ? 0 : Math.max(0, target.getNeeded());
            needSum += needs[i];
        }
        if (needSum <= 0) return 0;
        // 计算每接收者实际分配量并执行
        long allocated = 0;
        for (int i = 0; i < receivers.size() && allocated < totalSend; i++) {
            HBMEnergyHandler target = receivers.get(i);
            if (target == null || needs[i] <= 0) continue;
            long share = (long) Math.min(needs[i], (double) needs[i] * totalSend / needSum);
            share = Math.min(share, totalSend - allocated);
            long actual = sendToMachine(source, target, share);
            allocated += actual;
            totalSend -= actual;
        }
        return allocated;
    }
}
