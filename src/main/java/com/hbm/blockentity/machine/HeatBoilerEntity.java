package com.hbm.blockentity.machine;

import com.hbm.Inventory.fluid.trait.FT_Heatable;
import com.hbm.block.machine.BlockHeatBoiler;
import com.hbm.blockentity.interfaces.IHeatSource;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

/**
 * 热锅炉（core 体系多方块机器）。
 * 移植自旧版 TileEntityHeatBoiler：从下方热源吸热把水转化为蒸汽。
 * - 2 罐：0 水输入、1 蒸汽输出
 * - 简化：移除爆炸
 */
public class HeatBoilerEntity extends BEDummyable {
    public static final int MAX_HEAT = 3_200_000;
    public static final double DIFFUSION = 0.1D;
    public int heat;
    public boolean isOn;

    private final FluidTank waterTank;
    private final FluidTank steamTank;

    public HeatBoilerEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(0);
        this.fluidHandler = new BasicFluidHandler(2, 16_000);
        this.waterTank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.steamTank = ((BasicFluidHandler) fluidHandler).getFluidTank(1);
        this.waterTank.setCapacity(16_000);
        this.steamTank.setCapacity(16_000 * 100);
        this.setMultiblockData(BlockHeatBoiler.class);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        setupTanks();
        tryPullHeat();

        this.isOn = false;
        tryConvert();
        this.setChanged();
    }

    protected void tryPullHeat(){
        BlockEntity con = this.getLevel().getBlockEntity(this.worldPosition.below());
        if (con instanceof IHeatSource source){
            int diff = source.getHeatStored() - this.heat;
            if (diff > 0){
                diff = (int) Math.ceil(diff * DIFFUSION);
                diff = Math.min(diff, MAX_HEAT - this.heat);
                source.useUpHeat(diff);
                this.heat += diff;
                if (this.heat > MAX_HEAT) this.heat = MAX_HEAT;
                return;
            }
        }
        this.heat = Math.max(this.heat - Math.max(this.heat / 1000, 1), 0);
    }

    protected void setupTanks(){
        FT_Heatable trait = getHeatableTrait();
        if (trait != null && trait.getEfficiency(FT_Heatable.HeatingType.BOILER) > 0){
            // 输出统一为蒸汽
            steamTank.setFluid(new FluidStack(com.hbm.core.contents.fluid.HBMFluids.STEAM.source().get(), steamTank.getFluidAmount()));
            return;
        }
        waterTank.setFluid(FluidStack.EMPTY);
        steamTank.setFluid(FluidStack.EMPTY);
    }

    protected void tryConvert(){
        FT_Heatable trait = getHeatableTrait();
        if (trait != null && trait.getEfficiency(FT_Heatable.HeatingType.BOILER) > 0){
            FT_Heatable.HeatingStep entry = trait.getFirstStep();
            int heatReq = (int) Math.max(entry.heatReq / trait.getEfficiency(FT_Heatable.HeatingType.BOILER), 1);
            int inputOps = waterTank.getFluidAmount() / entry.amountReq;
            int outputOps = (steamTank.getCapacity() - steamTank.getFluidAmount()) / entry.amountProduced;
            int heatOps = this.heat / heatReq;
            int ops = Math.min(inputOps, Math.min(outputOps, heatOps));

            waterTank.drain(entry.amountReq * ops, FluidTank.FluidAction.EXECUTE);
            steamTank.fill(new FluidStack(com.hbm.core.contents.fluid.HBMFluids.STEAM.source().get(), entry.amountProduced * ops), FluidTank.FluidAction.EXECUTE);
            this.heat -= heatReq * ops;

            if (ops > 0) this.isOn = true;
        }
    }

    private FT_Heatable getHeatableTrait(){
        if (waterTank.getFluid().isEmpty()) return null;
        net.minecraftforge.fluids.FluidType type = waterTank.getFluid().getFluid().getFluidType();
        if (type instanceof com.hbm.Inventory.fluid.ExtendedFluidType extended){
            return extended.getTrait(FT_Heatable.class);
        }
        return null;
    }

    public FluidTank getWaterTank(){ return waterTank; }
    public FluidTank getSteamTank(){ return steamTank; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null; // 无 GUI
    }
}
