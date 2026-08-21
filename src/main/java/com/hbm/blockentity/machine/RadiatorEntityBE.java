package com.hbm.blockentity.machine;

import com.hbm.blockentity.HBMTiles;
import com.hbm.blockentity.base.DummyableBE;
import com.hbm.blockentity.machine.component.CondenserLogic;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 散热器方块实体。
 * 移植自旧版 TileEntityRadiator：废蒸汽 → 水（复用 CondenserLogic）。
 */
public class RadiatorEntityBE extends DummyableBE {
    private static final int STEAM_CAPACITY = 500;
    private static final int WATER_CAPACITY = 500;
    private static final int CONVERSION_RATE = 100;

    private final CondenserLogic logic;

    public RadiatorEntityBE(BlockPos pos, BlockState state) {
        super(HBMTiles.getTypeById("machine_radiator"), pos, state);
        this.items = NonNullList.withSize(0, ItemStack.EMPTY);
        this.multiblockData = MultiblockData.mapping.get(ModBlocks.MACHINE_RADIATOR.get());
        this.logic = new CondenserLogic(STEAM_CAPACITY, WATER_CAPACITY, CONVERSION_RATE);
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, logic.handler());
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (level != null && logic.tick(level)){
            setChanged();
        }
    }

    public IFluidHandler getFluidHandler(){
        return logic.handler();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Fluids", logic.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Fluids")) logic.deserializeNBT(tag.getCompound("Fluids"));
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_radiator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null; // 无 GUI
    }
}
