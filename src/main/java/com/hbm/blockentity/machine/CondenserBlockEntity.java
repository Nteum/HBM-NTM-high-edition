package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.blockentity.machine.component.CondenserLogic;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simple two-tank condenser that converts spent steam back into water. Operates
 * autonomously and does not expose a GUI.
 */
public class CondenserBlockEntity extends BaseMachineBlockEntity {

    private static final int STEAM_CAPACITY = 8_000;
    private static final int WATER_CAPACITY = 8_000;
    private static final int CONVERSION_RATE = 200;

    private final CondenserLogic logic;

    public CondenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.CONDENSER_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(0, ItemStack.EMPTY);
        this.slotModes = NonNullList.create();
        this.logic = new CondenserLogic(STEAM_CAPACITY, WATER_CAPACITY, CONVERSION_RATE);
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, logic.handler());
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (level != null && logic.tick(level)) {
            setChanged();
            sendUpdatePacket();
        }
    }

    public IFluidHandler getFluidHandler() {
        return logic.handler();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(HBMKey.FLUIDS, logic.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains(HBMKey.FLUIDS)) {
            logic.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
        }
    }

    @NotNull
    @Override
    public CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        logic.writeSyncTag(tag);
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        logic.readSyncTag(tag);
    }

    @Override
    public Component getDisplayName() {
        return getDefaultName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        // No GUI for standalone condensers.
        return null;
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_condenser");
    }
}
