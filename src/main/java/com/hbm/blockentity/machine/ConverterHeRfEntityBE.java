package com.hbm.blockentity.machine;

import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.HybridEnergyStorage;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.blockentity.HBMTiles;
import com.hbm.blockentity.base.BaseMachineBE;
import com.hbm.registries.HBMCaps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

/**
 * HE ↔ RF 能量转换器方块实体。
 * 移植自旧版 TileEntityConverterHeRf：
 * - 接收 HBM 自定义能量（HE），按比例转换为 Forge Energy（FE）输出
 * - 无物品、无流体、无 GUI
 *
 * 能力接入：
 * - HBMCaps.LONG_ENERGY（输入）：暴露 IEnergyContainer，电网可向其充电
 * - ForgeCapabilities.ENERGY（输出）：暴露 HybridEnergyStorage，FE 设备可从中取电
 */
public class ConverterHeRfEntityBE extends BaseMachineBE {
    // 转换比例：旧版 heInput=5 HE → rfOutput=1 RF
    public static final long HE_INPUT = 5;
    public static final int RF_OUTPUT = 1;
    public static final long MAX_POWER = 5_000_000;
    public static final int RF_CAPACITY = 1_000_000;

    private final BasicEnergyContainer heBuffer;    // HE 侧缓冲（从电网接收）
    private final BasicEnergyContainer rfBuffer;    // RF 侧缓冲（转为 FE 存储）
    private final HybridEnergyStorage forgeEnergy;  // 暴露给 Forge 的 FE 接口

    public ConverterHeRfEntityBE(BlockPos pPos, BlockState pBlockState) {
        super(HBMTiles.getTypeById("machine_converter_he_rf"), pPos, pBlockState);
        this.items = NonNullList.withSize(0, ItemStack.EMPTY);
        this.heBuffer = new BasicEnergyContainer(MAX_POWER);
        this.rfBuffer = new BasicEnergyContainer(RF_CAPACITY);
        this.forgeEnergy = new HybridEnergyStorage(rfBuffer);
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(heBuffer));
        this.capabilitiesContent.addCapability(ForgeCapabilities.ENERGY, forgeEnergy);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        // HE → RF 转换：把 HE 缓冲的能量按比例转成 RF 缓冲
        long rfCreated = Math.min(rfBuffer.getNeeded(), heBuffer.getEnergy() / HE_INPUT * RF_OUTPUT);
        if (rfCreated > 0){
            heBuffer.setEnergy(heBuffer.getEnergy() - rfCreated * HE_INPUT / RF_OUTPUT);
            rfBuffer.setEnergy(rfBuffer.getEnergy() + rfCreated);
            this.setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("heBuffer", heBuffer.serializeNBT());
        pTag.put("rfBuffer", rfBuffer.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("heBuffer")) heBuffer.deserializeNBT(pTag.getCompound("heBuffer"));
        if (pTag.contains("rfBuffer")) rfBuffer.deserializeNBT(pTag.getCompound("rfBuffer"));
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_converter_he_rf");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null; // 无 GUI
    }

    // 需要实现 getContainerSize 等（items 为空）
    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public int getSlots() {
        return 0;
    }
}
