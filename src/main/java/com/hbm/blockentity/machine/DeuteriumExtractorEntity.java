package com.hbm.blockentity.machine;

import com.hbm.core.blockentity.BEMachineBase;
import com.hbm.core.capability.energy.BasicEnergyHandler;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.fluid.HBMFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

/**
 * 氘提取器（单方块）。
 * 移植自旧版 TileEntityDeuteriumExtractor：水 → 重水。
 * - 无物品槽、2 罐（水输入/重水输出）
 */
public class DeuteriumExtractorEntity extends BEMachineBase {
    public static final long MAX_POWER = 10_000;
    public long power;

    private final FluidTank waterTank;
    private final FluidTank heavyWaterTank;

    public DeuteriumExtractorEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(0);
        this.energyContainer = createEnergyHandler(MAX_POWER, BasicEnergyHandler.INPUT);
        this.fluidHandler = new BasicFluidHandler(2, 1_000);
        this.waterTank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.heavyWaterTank = ((BasicFluidHandler) fluidHandler).getFluidTank(1);
        this.waterTank.setCapacity(1_000);
        this.heavyWaterTank.setCapacity(100);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        this.power = energyContainer.getEnergy();

        if (power > 200 && waterTank.getFluidAmount() >= 100 && heavyWaterTank.getFluidAmount() < heavyWaterTank.getCapacity()){
            int convert = Math.min(heavyWaterTank.getCapacity(), waterTank.getFluidAmount()) / 50;
            convert = Math.min(convert, heavyWaterTank.getCapacity() - heavyWaterTank.getFluidAmount());

            waterTank.drain(convert * 50, FluidTank.FluidAction.EXECUTE);
            heavyWaterTank.fill(new net.minecraftforge.fluids.FluidStack(HBMFluids.HEAVYWATER.source().get(), convert), FluidTank.FluidAction.EXECUTE);
            power -= MAX_POWER / 100;
            energyContainer.setEnergy(power);
        }
        this.setChanged();
    }

    public FluidTank getWaterTank(){ return waterTank; }
    public FluidTank getHeavyWaterTank(){ return heavyWaterTank; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null; // 无 GUI
    }
}
