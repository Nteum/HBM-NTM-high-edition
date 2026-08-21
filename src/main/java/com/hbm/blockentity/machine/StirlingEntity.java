package com.hbm.blockentity.machine;

import com.hbm.api.energy.TransmitUtils;
import com.hbm.blockentity.interfaces.IHeatSource;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.energy.BasicEnergyHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 斯特林机（core 体系多方块机器）。
 * 移植自旧版 TileEntityStirling：从下方热源吸热，把热量按效率转为 HE 输出。
 * 无 GUI，右键可安装/更换齿轮（hasCog）。
 */
public class StirlingEntity extends BEDummyable {
    public static final double DIFFUSION = 0.1D;
    public static final double EFFICIENCY = 0.5D;
    public static final int MAX_HEAT_NORMAL = 300;
    public static final long MAX_POWER = 1_000_000;

    public long powerBuffer;
    public int heat;
    public boolean hasCog = true;

    public StirlingEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(1);
        this.energyContainer = createEnergyHandler(MAX_POWER, BasicEnergyHandler.OUTPUT);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_STIRLING.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        if (hasCog){
            this.powerBuffer = 0;
            tryPullHeat();
            this.powerBuffer = (long)(this.heat * EFFICIENCY);
            this.energyContainer.setEnergy(this.powerBuffer);
        } else {
            if (this.powerBuffer > 0) this.powerBuffer--;
            this.energyContainer.setEnergy(this.powerBuffer);
        }

        TransmitUtils.outputOnly(this);
        this.powerBuffer = this.energyContainer.getEnergy();
        this.heat = 0;
        this.setChanged();
    }

    protected void tryPullHeat(){
        BlockEntity con = this.getLevel().getBlockEntity(this.worldPosition.below());
        if (con instanceof IHeatSource source){
            int heatSrc = (int)(source.getHeatStored() * DIFFUSION);
            if (heatSrc > 0){
                source.useUpHeat(heatSrc);
                this.heat += heatSrc;
                return;
            }
        }
        this.heat = Math.max(this.heat - Math.max(this.heat / 1000, 1), 0);
    }

    public int maxHeat(){
        return MAX_HEAT_NORMAL;
    }

    public void setHasCog(boolean hasCog){
        this.hasCog = hasCog;
        this.setChanged();
    }

    public MachineItemHandler getItemHandler(){ return items; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null; // 无 GUI
    }
}
