package com.hbm.api.energy.fe;

import com.hbm.utils.math.MthHelper;
import com.hbm.registries.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.HashMap;
import java.util.Map;

//集中记录一下用电器、发电机、电缆、电池控制输电的算法
//暂时过渡使用的体系，不要指望它有很高的效率
//我们默认所有调用此系统的方块实体均使用 IHBMEnergyStorage
public class TransmitHelper {
    //单纯耗电的机器
    public static void machineTransmit(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity){
        if (pBlockEntity == null || pBlockEntity.isRemoved()) {
            return;
        }
        pBlockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
            IHBMEnergyStorage energyStorage = (IHBMEnergyStorage) cap;
            long amountNeed = energyStorage.getMaxInput();
            Map<IEnergyStorage, Long> energySources = new HashMap<>();
            //检查机器的每一个方向
            for (Direction direction : Direction.values()) {
                pBlockEntity.getCapability(ForgeCapabilities.ENERGY,direction).ifPresent(sideCap -> {
                    BlockEntity neighbourEntity = level.getBlockEntity(pPos.relative(direction));
                    if (neighbourEntity == null)return;
                    neighbourEntity.getCapability(ForgeCapabilities.ENERGY,direction.getOpposite()).ifPresent(neighbourCap -> {
                        if (!neighbourCap.canExtract())return;
                        long extractPotential = 0;
                        if (neighbourCap instanceof IHBMEnergyStorage hbmNeighbourCap)
                            extractPotential = hbmNeighbourCap.extractEnergy(amountNeed, true);
                        else
                            extractPotential = neighbourCap.extractEnergy(MthHelper.long2int(amountNeed), true);
                        energySources.put(neighbourCap, extractPotential);
                    });
                });
            }
            if (energySources.isEmpty())return;
            long extractPotentialSum = energySources.values().stream().reduce(Long::sum).orElse(0L);
            long realExtract = 0;
            //限制抽取某个方向的能源，如果只有一个能源，则完全抽取，如果有多个，则每个抽取的占比不能太多，避免净逮着一个抽。
            double restrictRatio = energySources.size()==1 ? 1 : 1.6 / energySources.size();
            for (Map.Entry<IEnergyStorage, Long> entry : energySources.entrySet()) {
                IEnergyStorage energySource = entry.getKey();
                Long amount = entry.getValue();
                amount = (long) (amountNeed * Math.min((double) amount / extractPotentialSum, restrictRatio));
                if (energySource instanceof IHBMEnergyStorage hbmEnergySource)
                    realExtract += hbmEnergySource.extractEnergy(amount, false);
                else
                    realExtract += energySource.extractEnergy(MthHelper.long2int(amount), false);
            }
            energyStorage.receiveEnergy(realExtract,false);
        });
    }
    //发电机，单纯发电
    public static void generatorTransmit(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity){
        if (pBlockEntity == null || pBlockEntity.isRemoved()) {
            return;
        }
        pBlockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
            IHBMEnergyStorage energyStorage = (IHBMEnergyStorage) cap;
            long amountToOutput = energyStorage.getMaxOutput();
            Map<IEnergyStorage, Long> energyDests = new HashMap<>();
            //检查机器的每一个方向
            for (Direction direction : Direction.values()) {
                pBlockEntity.getCapability(ForgeCapabilities.ENERGY,direction).ifPresent(sideCap -> {
                    BlockEntity neighbourEntity = level.getBlockEntity(pPos.relative(direction));
                    if (neighbourEntity == null)return;
                    neighbourEntity.getCapability(ForgeCapabilities.ENERGY,direction.getOpposite()).ifPresent(neighbourCap -> {
                        if (!neighbourCap.canReceive())return;
                        long receivePotential = 0;
                        if (neighbourCap instanceof IHBMEnergyStorage hbmNeighbourCap)
                            receivePotential = hbmNeighbourCap.receiveEnergy(amountToOutput, true);
                        else
                            receivePotential = neighbourCap.receiveEnergy(MthHelper.long2int(amountToOutput), true);
                        energyDests.put(neighbourCap, receivePotential);
                    });
                });
            }
            if (energyDests.isEmpty())return;
            long receivePotentialSum = energyDests.values().stream().reduce(Long::sum).orElse(0L);
            long realReceive = 0;
            double restrictRatio = energyDests.size()==1 ? 1 : 1.6 / energyDests.size();
            for (Map.Entry<IEnergyStorage, Long> entry : energyDests.entrySet()) {
                IEnergyStorage energySource = entry.getKey();
                Long amount = entry.getValue();
                amount = (long) (amountToOutput * Math.min((double) amount / receivePotentialSum, restrictRatio));
                if (energySource instanceof IHBMEnergyStorage hbmEnergySource)
                    realReceive += hbmEnergySource.receiveEnergy(amount, false);
                else
                    realReceive += energySource.receiveEnergy(MthHelper.long2int(amount), false);
            }
            energyStorage.receiveEnergy(realReceive,false);
        });
    }
    //电缆，传导电力
    //暂时只考虑让电缆主动传输给能量低于它的电缆，机器获取能量主要靠的是自己主动抽取
    public static int conductCond = 5;  //启动传输的限定值
    public static void cableTransmit(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity){
        pBlockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
            IHBMEnergyStorage energyStorage = (IHBMEnergyStorage) cap;
            long amountOutput = energyStorage.getMaxOutput();
            Map<IEnergyStorage, Long> energyDests = new HashMap<>();
            //检查机器的每一个方向
            for (Direction direction : Direction.values()) {
                pBlockEntity.getCapability(ForgeCapabilities.ENERGY,direction).ifPresent(sideCap -> {
                    BlockEntity neighbourEntity = level.getBlockEntity(pPos.relative(direction));
                    if (neighbourEntity == null)return;
                    neighbourEntity.getCapability(ForgeCapabilities.ENERGY,direction.getOpposite()).ifPresent(neighbourCap -> {
                        if (!neighbourCap.canReceive())return;
                        //只传给能量比它低的，这里就不考虑存储能量是不是long了，毕竟当前所有管线输出功率到不了int上限
                        //需要差值大到conductCond才能启动传输，避免导线之间来回传输
                        if (neighbourCap.getEnergyStored() >= energyStorage.getEnergyStored()-conductCond)return;
                        long receivePotential;
                        if (neighbourCap instanceof IHBMEnergyStorage hbmNeighbourCap){
                            receivePotential = energyStorage.getLongStore() - hbmNeighbourCap.getLongStore();
                        }
                        else{
                            receivePotential = neighbourCap.getEnergyStored() - energyStorage.getEnergyStored();
                        }
                        energyDests.put(neighbourCap, receivePotential);
                    });
                });
            }
            if (energyDests.isEmpty())return;
            long receivePotentialSum = energyDests.values().stream().reduce(Long::sum).orElse(0L);
            //最终输出值不大于某个线缆和周围其他线缆的差值，也是为了避免频繁切换
            amountOutput = Math.min(amountOutput, energyDests.values().stream().min(Long::compareTo).orElse(0L));
            long realReceive = 0;
            double restrictRatio = energyDests.size()==1 ? 1 : 1.6 / energyDests.size();
            for (Map.Entry<IEnergyStorage, Long> entry : energyDests.entrySet()) {
                IEnergyStorage energySource = entry.getKey();
                Long amount = entry.getValue();
                amount = (long) (amountOutput * Math.min((double) amount / receivePotentialSum, restrictRatio));
                if (energySource instanceof IHBMEnergyStorage hbmEnergySource)
                    realReceive += hbmEnergySource.receiveEnergy(amount, false);
                else
                    realReceive += energySource.receiveEnergy(MthHelper.long2int(amount), false);
            }
            energyStorage.extractEnergy(realReceive,false);
        });
    }
    //电池，储电/输电/保证储电量稳定
    public static void batteryTransmit(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity){
        if (pBlockEntity == null || pBlockEntity.isRemoved()) {
            return;
        }
        double priorityRatio = 1.0;     //这个值比较高则更倾向于吸能，比较低则倾向于放出能量
        double stableRatio = 0.4;       //电池尽可能保证其能量
        pBlockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
            IHBMEnergyStorage energyStorage = (IHBMEnergyStorage) cap;
            long amountInput = energyStorage.getMaxInput(), amountOutput = energyStorage.getMaxOutput();
            //                      input, output
            Map<IEnergyStorage, Tuple<Long,Long>> energyInteract = new HashMap<>();
            //检查机器的每一个方向
            for (Direction direction : Direction.values()) {
                long finalAmountOutput = amountOutput;
                long finalAmountInput = amountInput;
                pBlockEntity.getCapability(ForgeCapabilities.ENERGY,direction).ifPresent(sideCap -> {
                    BlockEntity neighbourEntity = level.getBlockEntity(pPos.relative(direction));
                    if (neighbourEntity == null)return;
                    neighbourEntity.getCapability(ForgeCapabilities.ENERGY,direction.getOpposite()).ifPresent(neighbourCap -> {
                        long extractPotential = 0,receivePotential = 0,energyDiff = 0;
                        long partialDiff = 0;
                        double batteryPartial = 0, neighbourPartial = 0;
                        //暂时只考虑其他导线和电池，机器和发电机会自己从电池取电
                        if (neighbourCap.canReceive() && neighbourCap.canExtract()){
                            if (neighbourCap instanceof IHBMEnergyStorage hbmNeighbourCap){
                                //对于能量小于它的临近方块，仅吸收对方能量比大于40%的部分，若少于40%，则向外输出
                                if (hbmNeighbourCap.getLongStore() <= energyStorage.getLongStore()){
                                    batteryPartial = (double) energyStorage.getLongStore() / energyStorage.getLongCapacity();
                                    partialDiff = (long) (hbmNeighbourCap.getLongCapacity() * batteryPartial / priorityRatio - hbmNeighbourCap.getLongStore());
                                    if (partialDiff > 0){   //能量多于总能量40%则向电池充电
                                        receivePotential = hbmNeighbourCap.receiveEnergy(finalAmountOutput, true);
                                        receivePotential = Math.min(partialDiff, receivePotential);
                                    }else if (partialDiff < 0){                 //能量少于总能40%则电池向他充电
                                        extractPotential = hbmNeighbourCap.extractEnergy(finalAmountInput, true);
                                        extractPotential = Math.min(-partialDiff,extractPotential);
                                    }else if (hbmNeighbourCap.getLongStore() == 0){
                                        receivePotential = hbmNeighbourCap.receiveEnergy(finalAmountOutput, true);
                                        long idealPartial = (long) (energyStorage.getLongStore() * ((double)energyStorage.getLongStore() / (hbmNeighbourCap.getLongCapacity() + energyStorage.getLongCapacity())));
                                        receivePotential = Math.min(idealPartial,receivePotential);
                                    }
                                }else { //对于能量大于它的临近方块，则吸收对方比自己多的能量
                                    energyDiff = hbmNeighbourCap.getLongStore() - energyStorage.getLongStore();
                                    extractPotential = hbmNeighbourCap.extractEnergy(finalAmountInput, true);
                                    extractPotential = Math.min(energyDiff / 2,extractPotential);
                                }
                            }else {
                                if (neighbourCap.getEnergyStored() <= energyStorage.getEnergyStored()){
                                    batteryPartial = (double) energyStorage.getEnergyStored() / energyStorage.getEnergyStored();
                                    partialDiff = (long) (neighbourCap.getMaxEnergyStored() * batteryPartial / priorityRatio - neighbourCap.getMaxEnergyStored());
                                    if (partialDiff > 0){   //能量多于总能量40%则向电池充电
                                        receivePotential = neighbourCap.receiveEnergy(MthHelper.long2int(finalAmountOutput), true);
                                        receivePotential = Math.min(partialDiff, receivePotential);
                                    }else if (partialDiff < 0){                 //能量少于总能40%则电池向他充电
                                        extractPotential = neighbourCap.extractEnergy(MthHelper.long2int(finalAmountInput), true);
                                        extractPotential = Math.min(-partialDiff,extractPotential);
                                    }else if (neighbourCap.getEnergyStored() == 0){
                                        receivePotential = neighbourCap.receiveEnergy(MthHelper.long2int(finalAmountInput), true);
                                        long idealPartial = (long) (energyStorage.getEnergyStored() * ((double)energyStorage.getMaxEnergyStored() / (neighbourCap.getMaxEnergyStored() + energyStorage.getMaxEnergyStored())));
                                        receivePotential = Math.min(idealPartial,receivePotential);
                                    }
                                }else { //对于能量大于它的临近方块，则吸收对方比自己多的能量
                                    energyDiff = neighbourCap.getEnergyStored() - energyStorage.getLongStore();
                                    extractPotential = neighbourCap.extractEnergy(MthHelper.long2int(finalAmountInput), true);
                                    extractPotential = Math.min(energyDiff / 2,extractPotential);
                                }
                            }
                        }
                        energyInteract.put(neighbourCap, new Tuple<>(extractPotential,receivePotential));
                    });
                });
            }
            if (energyInteract.isEmpty())return;
            long extractPotentialSum = energyInteract.values().stream().mapToLong(Tuple::getA).reduce(Long::sum).orElse(0L);
            long receivePotentialSum = energyInteract.values().stream().mapToLong(Tuple::getB).reduce(Long::sum).orElse(0L);
            amountInput = Math.min(amountInput, extractPotentialSum);
            amountOutput = Math.min(amountOutput, receivePotentialSum);
            //最终输出值不大于某个线缆和周围其他线缆的差值，也是为了避免频繁切换
            long realExtract = 0, realReceive = 0;
            for (Map.Entry<IEnergyStorage, Tuple<Long, Long>> entry : energyInteract.entrySet()) {
                IEnergyStorage energySource = entry.getKey();
                Tuple<Long, Long> tuple = entry.getValue();
                if (tuple.getA() > 0){
                    if (extractPotentialSum == 0)continue;
                    long amount = amountInput * tuple.getA() / extractPotentialSum;
                    if (energySource instanceof IHBMEnergyStorage hbmEnergySource)
                        realExtract += hbmEnergySource.extractEnergy(amount, false);
                    else
                        realExtract += energySource.extractEnergy(MthHelper.long2int(amount), false);
                }else {
                    if (receivePotentialSum == 0)continue;
                    long amount = amountOutput * tuple.getB() / receivePotentialSum;
                    if (energySource instanceof IHBMEnergyStorage hbmEnergySource)
                        realReceive += hbmEnergySource.receiveEnergy(amount, false);
                    else
                        realReceive += energySource.receiveEnergy(MthHelper.long2int(amount), false);
                }
            }
            if (realReceive > realExtract)
                energyStorage.extractEnergy(realReceive,false);
            else
                energyStorage.receiveEnergy(realExtract,false);
        });
    }
    //吸取物品槽的电力
    public static void dischargeItem(BlockEntity pBlockEntity, ItemStack itemStack){
        if (itemStack.is(ModTags.Items.BATTERY)){
            pBlockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
                itemStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(itemCap -> {
                    IHBMEnergyStorage energyStorage = (IHBMEnergyStorage) cap;
                    long amountInput = energyStorage.getMaxInput();
                    long amountCharge;
                    if (itemCap instanceof IHBMEnergyStorage hbmItemCap){
                        amountCharge = hbmItemCap.extractEnergy(amountInput,true);
                    }else
                        amountCharge = itemCap.extractEnergy(MthHelper.long2int(amountInput),true);
                    itemCap.extractEnergy(MthHelper.long2int(energyStorage.receiveEnergy(amountCharge,false)),false);
                });
            });
        }
    }
    //为物品槽中的物品充电
    public static void chargeItem(BlockEntity pBlockEntity, ItemStack itemStack){
        if (itemStack.is(ModTags.Items.CHARGEABLE)){
            pBlockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
                itemStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(itemCap -> {
                    IHBMEnergyStorage energyStorage = (IHBMEnergyStorage) cap;
                    long amountOutput = energyStorage.getMaxOutput();
                    long amountCharge;
                    if (itemCap instanceof IHBMEnergyStorage hbmItemCap){
                        amountCharge = hbmItemCap.receiveEnergy(amountOutput,true);
                    }else
                        amountCharge = itemCap.receiveEnergy(MthHelper.long2int(amountOutput),true);
                    itemCap.receiveEnergy(MthHelper.long2int(energyStorage.extractEnergy(amountCharge,false)),false);
                });
            });
        }
    }
}
