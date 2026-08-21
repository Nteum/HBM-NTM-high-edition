package com.hbm.blockentity.machine;

import com.hbm.Inventory.fluid.trait.FT_Coolable;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.energy.BasicEnergyHandler;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

/**
 * 蒸汽机（core 体系多方块机器）。
 * 移植自旧版 TileEntitySteamEngine：蒸汽 → 废蒸汽 + HE。
 * - 2 罐：0 蒸汽输入、1 废蒸汽输出
 */
public class SteamEngineEntity extends BEDummyable {
    public static final double EFFICIENCY = 0.85D;
    public static final long MAX_POWER = 1_000_000;
    public long powerBuffer;

    private final FluidTank steamTank;
    private final FluidTank spentTank;

    public SteamEngineEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(0);
        this.energyContainer = createEnergyHandler(MAX_POWER, BasicEnergyHandler.OUTPUT);
        this.fluidHandler = new BasicFluidHandler(2, 2_000);
        this.steamTank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.spentTank = ((BasicFluidHandler) fluidHandler).getFluidTank(1);
        this.steamTank.setCapacity(2_000);
        this.spentTank.setCapacity(20);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_STEAM_ENGINE.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        this.powerBuffer = 0;

        FT_Coolable trait = getCoolableTrait();
        if (trait != null){
            double eff = trait.getEfficiency(FT_Coolable.CoolingType.TURBINE) * EFFICIENCY;
            int inputOps = steamTank.getFluidAmount() / trait.amountReq;
            int outputOps = (spentTank.getCapacity() - spentTank.getFluidAmount()) / trait.amountProduced;
            int ops = Math.min(inputOps, outputOps);

            steamTank.drain(ops * trait.amountReq, FluidTank.FluidAction.EXECUTE);
            spentTank.fill(new net.minecraftforge.fluids.FluidStack(trait.amountProduced > 0 ? com.hbm.core.contents.fluid.HBMFluids.SPENTSTEAM.source().get() : net.minecraft.world.level.material.Fluids.WATER, ops * trait.amountProduced), FluidTank.FluidAction.EXECUTE);
            this.powerBuffer += (long)(ops * trait.heatEnergy * eff);
        }

        this.energyContainer.setEnergy(this.powerBuffer);
        TransmitUtils.outputOnly(this);
        this.powerBuffer = this.energyContainer.getEnergy();
        this.setChanged();
    }

    private FT_Coolable getCoolableTrait(){
        if (steamTank.getFluid().isEmpty()) return null;
        net.minecraftforge.fluids.FluidType type = steamTank.getFluid().getFluid().getFluidType();
        if (type instanceof com.hbm.Inventory.fluid.ExtendedFluidType extended){
            return extended.getTrait(FT_Coolable.class);
        }
        return null;
    }

    public FluidTank getSteamTank(){ return steamTank; }
    public FluidTank getSpentTank(){ return spentTank; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null; // 无 GUI
    }
}
