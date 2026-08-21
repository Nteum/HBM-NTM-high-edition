package com.hbm.blockentity.machine;

import com.hbm.Inventory.fluid.trait.FT_Combustible;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.blockentity.HBMTiles;
import com.hbm.blockentity.base.BaseMachineBE;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.registries.HBMCaps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

/**
 * 柴油发电机（单方块）。
 * 移植自旧版 TileEntityMachineDiesel：燃烧柴油类燃料发电。
 * - 4 槽：0燃料桶、1空桶、2电池、3罐类型
 * - 1 燃料罐
 * - 简化：移除污染/空气/过载爆炸
 */
public class DieselEntityBE extends BaseMachineBE {
    public static final long MAX_POWER = 50_000;
    public static final int FUEL_CAPACITY = 16_000;

    public boolean isOn;
    public long power;
    public long powerCap = MAX_POWER;
    private final BasicEnergyContainer energyContainer;
    private final FluidTank tank;

    public DieselEntityBE(BlockPos pos, BlockState state) {
        super(HBMTiles.getTypeById("machine_diesel"), pos, state);
        this.items = NonNullList.withSize(4, ItemStack.EMPTY);
        this.energyContainer = new BasicEnergyContainer(MAX_POWER, MAX_POWER, 0);
        this.tank = new FluidTank(FUEL_CAPACITY);
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(energyContainer));
        this.capabilitiesContent.addCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER, tank);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        TransmitUtils.dischargeItem(this, items.get(2));
        this.power = energyContainer.getEnergy();

        if (isOn && !this.getLevel().hasNeighborSignal(this.worldPosition)){
            if (!tank.getFluid().isEmpty()){
                FT_Combustible trait = getCombustible(tank.getFluid());
                if (trait != null){
                    tank.drain(1, FluidTank.FluidAction.EXECUTE);
                    long energy = (long) (trait.getCombustionEnergy() / 1000D);
                    energyContainer.setEnergy(Math.min(MAX_POWER, energyContainer.getEnergy() + energy));
                    this.setChanged();
                }
            }
        }
    }

    private FT_Combustible getCombustible(net.minecraftforge.fluids.FluidStack fluid){
        if (fluid.isEmpty()) return null;
        net.minecraftforge.fluids.FluidType type = fluid.getFluid().getFluidType();
        if (type instanceof com.hbm.Inventory.fluid.ExtendedFluidType extended){
            return extended.getTrait(FT_Combustible.class);
        }
        return null;
    }

    public long getHEFromFuel(){
        if (tank.getFluid().isEmpty()) return 0;
        FT_Combustible trait = getCombustible(tank.getFluid());
        return trait == null ? 0 : (long) (trait.getCombustionEnergy() / 1000D);
    }

    public void toggle(){
        this.isOn = !this.isOn;
        this.setChanged();
    }

    @Override
    public CompoundTag getClientSyncTag() {
        CompoundTag tag = super.getClientSyncTag();
        tag.putBoolean("isOn", isOn);
        return tag;
    }

    @Override
    public void handleClientPacket(CompoundTag tag) {
        if (tag.contains("toggle")) toggle();
    }

    public MachineItemHandler getItemHandler(){
        return new MachineItemHandler(4){
            @Override
            public int getSlots() { return items.size(); }
            @Override
            public ItemStack getStackInSlot(int slot) { return items.get(slot); }
            @Override
            public void setStackInSlot(int slot, ItemStack stack) { items.set(slot, stack); onContentsChanged(slot); }
        };
    }

    public FluidTank getTank(){ return tank; }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("isOn", isOn);
        tag.put("energy", energyContainer.serializeNBT());
        tag.put("fuel", tank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        isOn = tag.getBoolean("isOn");
        if (tag.contains("energy")) energyContainer.deserializeNBT(tag.getCompound("energy"));
        if (tag.contains("fuel")) tank.readFromNBT(tag.getCompound("fuel"));
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_diesel");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.DieselMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(0));
    }
}
