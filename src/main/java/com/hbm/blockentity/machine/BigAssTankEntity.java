package com.hbm.blockentity.machine;

import com.hbm.block.machine.BlockBigAssTank;
import com.hbm.core.blockentity.BEDummyable;
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
 * 大型储罐（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineBigAssTank：1600 万 mB 单流体储罐。
 */
public class BigAssTankEntity extends BEDummyable {
    public static final int CAPACITY = 16_000_000;

    private final FluidTank tank;

    public BigAssTankEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(0);
        this.fluidHandler = new BasicFluidHandler(1, CAPACITY);
        this.tank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.tank.setCapacity(CAPACITY);
        this.setMultiblockData(BlockBigAssTank.class);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        this.setChanged();
    }

    public FluidTank getTank(){ return tank; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null; // 无 GUI
    }
}
