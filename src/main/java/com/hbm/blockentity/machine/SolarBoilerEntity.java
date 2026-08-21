package com.hbm.blockentity.machine;

import com.hbm.block.machine.BlockSolarBoiler;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.registries.ModBlocks;
import com.hbm.space.dim.CelestialBody;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

/**
 * 太阳能锅炉（core 体系多方块机器）。
 * 移植自旧版 TileEntitySolarBoiler：利用太阳功率把水转化为蒸汽。
 * - 2 罐：0 水输入、1 蒸汽输出
 */
public class SolarBoilerEntity extends BEDummyable {
    public int heat;

    private final FluidTank waterTank;
    private final FluidTank steamTank;

    @OnlyIn(Dist.CLIENT) public HashSet<BlockPos> primary = new HashSet<>();
    @OnlyIn(Dist.CLIENT) public HashSet<BlockPos> secondary = new HashSet<>();

    public SolarBoilerEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(0);
        this.fluidHandler = new BasicFluidHandler(2, 10_000);
        this.waterTank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.steamTank = ((BasicFluidHandler) fluidHandler).getFluidTank(1);
        this.waterTank.setCapacity(100);
        this.steamTank.setCapacity(10_000);
        this.setMultiblockData(BlockSolarBoiler.class);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        float sunPower = CelestialBody.getBody(this.getLevel()).getSunPower();

        int process = (int)(heat * sunPower) / 50;
        process = Math.min(process, waterTank.getFluidAmount());
        process = Math.min(process, (steamTank.getCapacity() - steamTank.getFluidAmount()) / 100);
        if (process < 0) process = 0;

        waterTank.drain(process, FluidTank.FluidAction.EXECUTE);
        steamTank.fill(new FluidStack(HBMFluids.STEAM.source().get(), process * 100), FluidTank.FluidAction.EXECUTE);

        heat = 0;
        this.setChanged();
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        this.secondary.clear();
        this.secondary.addAll(this.primary);
        this.primary.clear();
    }

    public FluidTank getWaterTank(){ return waterTank; }
    public FluidTank getSteamTank(){ return steamTank; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null; // 无 GUI
    }
}
