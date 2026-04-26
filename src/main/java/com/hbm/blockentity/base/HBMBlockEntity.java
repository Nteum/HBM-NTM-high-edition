package com.hbm.blockentity.base;


import com.hbm.api.energy.IEnergyContainer;
import com.hbm.api.energy.IEnergyHandler;
import com.hbm.api.inventory.IExtendedItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class HBMBlockEntity extends CapabilityBlockEntity implements IExtendedItemHandler, IEnergyHandler, WorldlyContainer, Nameable {

    @Nullable
    private Component name;

    public HBMBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void setCustomName(Component pName) {
        this.name = pName;
    }

    public Component getName() {
        return this.name != null ? this.name : this.getDefaultName();
    }

    public @NotNull Component getDisplayName() {
        return this.getName();
    }

    @javax.annotation.Nullable
    public Component getCustomName() {
        return this.name;
    }

    public abstract Component getDefaultName();

    @Override
    public IEnergyContainer getEnergyContainer() {
        return null;
    }

    @Override
    public void onContentsChanged() {
        this.setChanged();
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson(nbt.getString("CustomName"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (this.name != null) {
            pTag.putString("CustomName", Component.Serializer.toJson(this.name));
        }
    }
}
