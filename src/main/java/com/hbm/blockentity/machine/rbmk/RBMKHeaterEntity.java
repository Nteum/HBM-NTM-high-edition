package com.hbm.blockentity.machine.rbmk;

import com.hbm.HBM;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.reactor.rbmk.RBMKColumnState;
import com.hbm.reactor.rbmk.RBMKLevelContext;
import com.hbm.reactor.rbmk.RBMKManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.Optional;

/**
 * 简化的 RBMK 加热器逻辑：当被激活时，每 tick 向其下方的 RBMK 列注入少量热量。
 */
public class RBMKHeaterEntity extends BaseMachineBlockEntity {

    private static final double HEAT_PER_SECOND = 5.0D;
    private static final double TIME_STEP = 1.0D / 20.0D;

    private boolean active;
    private double heatBuffer;

    public RBMKHeaterEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RBMK_HEATER_ENTITY.get(), pos, state);
        this.items = net.minecraft.core.NonNullList.create();
        this.slotModes = Collections.emptyList();
    }

    public void setActive(boolean active) {
        this.active = active;
        setChanged();
    }

    public boolean isActive() {
        return active;
    }

    public void serverTick() {
        if (!active || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos below = worldPosition.below();
        RBMKLevelContext context = RBMKManager.context(serverLevel);
        Optional<RBMKColumnState> column = context.column(below);
        column.ifPresent(state -> {
            if (!context.addHeat(below, HEAT_PER_SECOND * TIME_STEP)) {
                return;
            }
            heatBuffer += HEAT_PER_SECOND * TIME_STEP;
            if (heatBuffer >= HEAT_PER_SECOND) {
                HBM.LOGGER.debug("RBMK heater at {} delivered {} heat to column {} (heat={})",
                        worldPosition, heatBuffer, state.corePosition(), state.heat());
                heatBuffer = 0.0D;
            }
        });
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.active = tag.getBoolean("Active");
        this.heatBuffer = tag.getDouble("HeatBuffer");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("Active", active);
        tag.putDouble("HeatBuffer", heatBuffer);
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_rbmk_heater");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }
}
