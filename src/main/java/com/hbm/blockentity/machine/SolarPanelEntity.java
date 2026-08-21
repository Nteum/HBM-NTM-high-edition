package com.hbm.blockentity.machine;

import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.energy.BasicEnergyHandler;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.registries.ModBlocks;
import com.hbm.space.dim.CelestialBody;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 太阳能板方块实体（多方块核心，新 core 体系 BEDummyable）。
 * 移植自旧版 TileEntityMachineSolarPanel：白天天空亮度高时发电并输出到电网。
 * 太阳功率按所在天体的 CelestialBody.getSunPower() 计算（地球=1，其它天体按日距平方反比）。
 */
public class SolarPanelEntity extends BEDummyable {
    public static final long MAX_POWER = 1_000;

    public SolarPanelEntity(BlockPos pPos, BlockState pBlockState) {
        super(pPos, pBlockState);
        this.items = new com.hbm.core.capability.item.MachineItemHandler(0);
        this.energyContainer = createEnergyHandler(MAX_POWER, BasicEnergyHandler.BOTH);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_SOLAR.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        int sun = this.getLevel().getBrightness(LightLayer.SKY, this.worldPosition.above()) - 11;
        if (sun <= 0 || !this.getLevel().canSeeSky(this.worldPosition.above())){
            return;
        }
        float sunPower = CelestialBody.getBody(this.getLevel()).getSunPower();
        long output = (long) Math.ceil(sun * 25 * sunPower);
        long newEnergy = Math.min(MAX_POWER, this.energyContainer.getEnergy() + output);
        this.energyContainer.setEnergy(newEnergy);
        this.setChanged();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null; // 无 GUI
    }
}
