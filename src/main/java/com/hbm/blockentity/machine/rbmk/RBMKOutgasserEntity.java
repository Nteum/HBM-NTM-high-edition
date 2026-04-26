package com.hbm.blockentity.machine.rbmk;

import com.hbm.api.Mode;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.reactor.rbmk.RBMKColumnState;
import com.hbm.reactor.rbmk.RBMKLevelContext;
import com.hbm.reactor.rbmk.RBMKManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class RBMKOutgasserEntity extends BaseMachineBlockEntity implements RBMKTickableEntity {

    public static final int REQUIRED_FLUX = 10_000;
    private double progress;

    public RBMKOutgasserEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RBMK_OUTGASSER_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(2, ItemStack.EMPTY);
        this.slotModes = java.util.List.of(Mode.INPUT, Mode.OUTPUT);
    }

    @Override
    public void serverTick() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        RBMKLevelContext context = RBMKManager.context(serverLevel);
        RBMKColumnState column = context.column(worldPosition.below()).orElse(null);
        if (column == null || items.get(0).isEmpty()) {
            progress = 0.0D;
            return;
        }
        progress += column.fastFlux() + column.slowFlux();
        if (progress >= REQUIRED_FLUX && items.get(1).isEmpty()) {
            items.set(1, items.get(0).copyWithCount(1));
            items.get(0).shrink(1);
            if (items.get(0).isEmpty()) {
                items.set(0, ItemStack.EMPTY);
            }
            progress = 0.0D;
            setChanged();
        }
    }

    public double progress() {
        return progress;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getDouble("Progress");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putDouble("Progress", progress);
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.rbmk_outgasser");
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;
    }
}
