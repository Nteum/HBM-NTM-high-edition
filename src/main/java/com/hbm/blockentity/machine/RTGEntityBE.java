package com.hbm.blockentity.machine;

import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.blockentity.HBMTiles;
import com.hbm.blockentity.base.BaseMachineBE;
import com.hbm.item.misc.ItemRTGPellet;
import com.hbm.registries.HBMCaps;
import com.hbm.utils.RTGUtil;
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
 * RTG 发电机。
 * 移植自旧版 TileEntityMachineRTG：
 * - 15 个 RTG 燃料棒槽位（只接受 ItemRTGPellet）
 * - 燃料棒产生热量 heat，heat 每 tick 乘以 5 转为 HE 能量
 * - 通过相邻方块输出能量
 */
public class RTGEntityBE extends BaseMachineBE {
    public static final int HEAT_MAX = 200;
    public static final long POWER_MAX = 100000;
    public static final int[] slot_io = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 };

    public int heat;
    public long power;

    private final BasicEnergyContainer energyContainer;
    private final net.minecraftforge.items.ItemStackHandler handler;

    public RTGEntityBE(BlockPos pPos, BlockState pBlockState) {
        super(HBMTiles.getTypeById("machine_rtg"), pPos, pBlockState);
        this.items = NonNullList.withSize(15, ItemStack.EMPTY);
        this.energyContainer = new BasicEnergyContainer(POWER_MAX, 0, POWER_MAX);
        this.energyContainer.setListener(this::setChanged);
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(energyContainer));
        this.capabilitiesContent.addCapability(ForgeCapabilities.ENERGY, new com.hbm.api.energy.HybridEnergyStorage(energyContainer));
        this.handler = new net.minecraftforge.items.ItemStackHandler() {
            @Override
            public int getSlots() {
                return items.size();
            }
            @Override
            public ItemStack getStackInSlot(int slot) {
                return items.get(slot);
            }
            @Override
            public void setStackInSlot(int slot, ItemStack stack) {
                items.set(slot, stack);
                onContentsChanged(slot);
            }
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack.getItem() instanceof ItemRTGPellet;
            }
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, handler);
    }

    public net.minecraftforge.items.ItemStackHandler getItemHandler() { return handler; }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        this.heat = RTGUtil.updateRTGs(this.items.toArray(new ItemStack[0]), slot_io);
        if (this.heat > HEAT_MAX)
            this.heat = HEAT_MAX;

        this.power += this.heat * 5L;
        if (this.power > POWER_MAX)
            this.power = POWER_MAX;

        this.energyContainer.setEnergy(this.power);
        TransmitUtils.outputOnly(this);
        this.power = this.energyContainer.getEnergy();
        this.setChanged();
    }

    public long getPowerScaled(long i){ return (this.power * i) / POWER_MAX; }
    public int getHeatScaled(int i){ return (this.heat * i) / HEAT_MAX; }
    public boolean hasPower(){ return this.power > 0; }
    public boolean hasHeat(){ return RTGUtil.hasHeat(this.items.toArray(new ItemStack[0]), slot_io); }

    @Override
    public int getSlots() {
        return 15;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return stack.getItem() instanceof ItemRTGPellet;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean allowOutput(int slot) {
        return false;
    }

    @Override
    public boolean allowInput(int slot) {
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putLong("power", power);
        pTag.putInt("heat", heat);
        pTag.put("energy", energyContainer.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        power = pTag.getLong("power");
        heat = pTag.getInt("heat");
        if (pTag.contains("energy")) energyContainer.deserializeNBT(pTag.getCompound("energy"));
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_rtg");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.RTGMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(2));
    }
}
