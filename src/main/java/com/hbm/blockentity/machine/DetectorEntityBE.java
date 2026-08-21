package com.hbm.blockentity.machine;

import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.IEnergyContainer;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.block.machine.BlockDetector;
import com.hbm.blockentity.HBMTiles;
import com.hbm.blockentity.base.BaseMachineBE;
import com.hbm.registries.HBMCaps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 能量检测器方块实体。
 * 移植自旧版 TileEntityMachineDetector：收到电网能量则点亮并输出红石信号。
 */
public class DetectorEntityBE extends BaseMachineBE {
    private final BasicEnergyContainer energyContainer;

    public DetectorEntityBE(BlockPos pPos, BlockState pBlockState) {
        super(HBMTiles.getTypeById("machine_detector"), pPos, pBlockState);
        this.items = NonNullList.withSize(0, ItemStack.EMPTY);
        this.energyContainer = new BasicEnergyContainer(5);
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(energyContainer));
    }

    @Override
    public IEnergyContainer getEnergyContainer() {
        return energyContainer;
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        boolean lit = energyContainer.getEnergy() > 0;
        if (lit) energyContainer.setEnergy(energyContainer.getEnergy() - 1);

        BlockState state = this.getBlockState();
        if (state.getValue(BlockDetector.LIT) != lit){
            this.getLevel().setBlock(this.worldPosition, state.setValue(BlockDetector.LIT, lit), 3);
            this.getLevel().updateNeighborsAt(this.worldPosition, state.getBlock());
        }
        if (lit) this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("energy", energyContainer.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("energy")) energyContainer.deserializeNBT(pTag.getCompound("energy"));
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_detector");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null; // 无 GUI
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public int getSlots() {
        return 0;
    }
}
