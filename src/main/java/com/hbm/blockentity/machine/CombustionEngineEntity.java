package com.hbm.blockentity.machine;

import com.hbm.Inventory.fluid.trait.FT_Combustible;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.energy.BasicEnergyHandler;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

/**
 * 内燃机（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineCombustionEngine：燃烧可燃流体发电。
 * - 5 槽：0 桶入、1 桶出、2 活塞、3 电池、4 罐ID
 * - 1 罐：燃料
 * - 简化：移除污染/声音
 */
public class CombustionEngineEntity extends BEDummyable {
    public static final long MAX_POWER = 2_500_000;
    public static final int TANK_CAPACITY = 24_000;
    public long power;
    public boolean isOn;
    public boolean wasOn;
    public int setting = 1;

    private final FluidTank tank;

    public CombustionEngineEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(5);
        this.energyContainer = createEnergyHandler(MAX_POWER, BasicEnergyHandler.OUTPUT);
        this.fluidHandler = new BasicFluidHandler(1, TANK_CAPACITY);
        this.tank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.tank.setCapacity(TANK_CAPACITY);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_COMBUSTION_ENGINE.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        wasOn = false;
        TransmitUtils.chargeItem(this, items.getStackInSlot(3));
        this.power = energyContainer.getEnergy();

        if (isOn && setting > 0 && items.getStackInSlot(2).is(ModItems.PISTON_SET.get()) && !tank.getFluid().isEmpty()){
            FT_Combustible trait = getCombustibleTrait(tank.getFluid());
            if (trait != null){
                double eff = 1.0;
                if (eff > 0){
                    if (getLevel().getGameTime() % 5 == 0 || true){
                        int speed = setting * 2;
                        int toBurn = Math.min(tank.getFluidAmount() * 10, speed);
                        if (toBurn > 0){
                            this.power += (long)(toBurn * (trait.getCombustionEnergy() / 10_000D) * eff);
                            tank.drain(toBurn / 10, FluidTank.FluidAction.EXECUTE);
                            wasOn = true;
                        }
                    }
                }
            }
        }

        if (power > MAX_POWER) power = MAX_POWER;
        energyContainer.setEnergy(power);

        TransmitUtils.outputOnly(this);
        this.power = energyContainer.getEnergy();
        this.setChanged();
    }

    private FT_Combustible getCombustibleTrait(FluidStack stack){
        if (stack == null || stack.isEmpty()) return null;
        net.minecraftforge.fluids.FluidType type = stack.getFluid().getFluidType();
        if (type instanceof com.hbm.Inventory.fluid.ExtendedFluidType extended){
            return extended.getTrait(FT_Combustible.class);
        }
        return null;
    }

    public void setOn(boolean on){ this.isOn = on; }
    public void setSetting(int setting){ this.setting = setting; }

    public MachineItemHandler getItemHandler(){ return items; }
    public FluidTank getTank(){ return tank; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.CombustionEngineMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(4));
    }
}
