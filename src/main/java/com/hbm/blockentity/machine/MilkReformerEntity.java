package com.hbm.blockentity.machine;

import com.hbm.api.energy.TransmitUtils;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.energy.BasicEnergyHandler;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

/**
 * 牛奶改质器（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineMilkReformer：用电能将牛奶改质为
 * 脱脂乳（EMILK）/浓缩乳（CMILK）/奶油（CREAM）。
 * - 11 槽：0电池、1牛奶桶入、2牛奶桶出、3-8 三种产物桶出入、9牛奶罐ID、10空
 * - 4 罐：0牛奶输入、1脱脂乳、2浓缩乳、3奶油
 * - 简化：移除污染
 */
public class MilkReformerEntity extends BEDummyable {
    public static final long MAX_POWER = 1_000_000;
    public static final int USAGE = 10_000;
    public long power;

    private final FluidTank milkTank;
    private final FluidTank emilkTank;
    private final FluidTank cmilkTank;
    private final FluidTank creamTank;

    public MilkReformerEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(11);
        this.energyContainer = createEnergyHandler(MAX_POWER, BasicEnergyHandler.INPUT);
        this.fluidHandler = new BasicFluidHandler(4, 64_000);
        this.milkTank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.emilkTank = ((BasicFluidHandler) fluidHandler).getFluidTank(1);
        this.cmilkTank = ((BasicFluidHandler) fluidHandler).getFluidTank(2);
        this.creamTank = ((BasicFluidHandler) fluidHandler).getFluidTank(3);
        this.milkTank.setCapacity(64_000);
        this.emilkTank.setCapacity(32_000);
        this.cmilkTank.setCapacity(32_000);
        this.creamTank.setCapacity(32_000);
        this.milkTank.setFluid(new net.minecraftforge.fluids.FluidStack(HBMFluids.MILK.source().get(), 0));
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_MILK_REFORMER.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        TransmitUtils.dischargeItem(this, items.getStackInSlot(0));
        this.power = energyContainer.getEnergy();

        refine();
        this.setChanged();
    }

    private void refine() {
        if (power < USAGE) return;
        if (milkTank.getFluidAmount() < 100) return;
        if (emilkTank.getFluidAmount() + 50 > emilkTank.getCapacity()) return;
        if (cmilkTank.getFluidAmount() + 35 > cmilkTank.getCapacity()) return;
        if (creamTank.getFluidAmount() + 15 > creamTank.getCapacity()) return;

        power -= USAGE;
        energyContainer.setEnergy(power);
        milkTank.drain(100, FluidTank.FluidAction.EXECUTE);
        emilkTank.fill(new net.minecraftforge.fluids.FluidStack(HBMFluids.EMILK.source().get(), 50), FluidTank.FluidAction.EXECUTE);
        cmilkTank.fill(new net.minecraftforge.fluids.FluidStack(HBMFluids.CMILK.source().get(), 35), FluidTank.FluidAction.EXECUTE);
        creamTank.fill(new net.minecraftforge.fluids.FluidStack(HBMFluids.CREAM.source().get(), 15), FluidTank.FluidAction.EXECUTE);
    }

    public MachineItemHandler getItemHandler(){ return items; }
    public FluidTank getMilkTank(){ return milkTank; }
    public FluidTank getEmilkTank(){ return emilkTank; }
    public FluidTank getCmilkTank(){ return cmilkTank; }
    public FluidTank getCreamTank(){ return creamTank; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.MilkReformerMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(0));
    }
}
