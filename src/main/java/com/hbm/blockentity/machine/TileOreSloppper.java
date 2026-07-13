package com.hbm.blockentity.machine;

import com.hbm.HBMLang;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.IEnergyHandler;
import com.hbm.api.fluid.BasicFluidHandler;
import com.hbm.block.machine.MachineOreSlopper;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.DefaultMachineBE;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.gui.menu.MenuOreSlopper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileOreSloppper extends DummyableBlockEntity {
    public static final long maxPower = 100_000;
    public BasicEnergyContainer energyContainer;

    public static final int waterUsedBase = 1_000;
    public int waterUsed = waterUsedBase;
    public static final long consumptionBase = 200;
    public long consumption = consumptionBase;

    public float progress;
    public boolean processing;

//    public SlopperAnimation animation = SlopperAnimation.LOWERING;
//    public float slider;
//    public float prevSlider;
//    public float bucket;
//    public float prevBucket;
//    public float blades;
//    public float prevBlades;
//    public float fan;
//    public float prevFan;
//    public int delay;
//
//    BasicFluidHandler fluidHandler;
//    public double[] ores = new double[CelestialBedrockOre.getAllTypes().size()];
//    private SolarSystem.Body fromBody;
//
//    public UpgradeManagerNT upgradeManager = new UpgradeManagerNT();
//
//    public TileOreSloppper() {
//        super(11);
//        tanks = new FluidTank[2];
//        tanks[0] = new FluidTank(Fluids.WATER, 16_000);
//        tanks[1] = new FluidTank(Fluids.SLOP, 16_000);
//    }

    private ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return 0;
        }

        @Override
        public void set(int pIndex, int pValue) {}

        @Override
        public int getCount() {
            return 0;
        }
    };
    public TileOreSloppper(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.tileTypes.get("tile_" + MachineOreSlopper.name).get(), pos, state);

    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MenuOreSlopper(pContainerId, pInventory, this, containerData);
    }

    @Override
    public Component getDefaultName() {
        return HBMLang.CONTAINER_ORE_SLOPPER.translate();
    }
}
