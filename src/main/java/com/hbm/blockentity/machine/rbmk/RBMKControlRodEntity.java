package com.hbm.blockentity.machine.rbmk;

import com.hbm.block.machine.rbmk.BlockRBMKControlRod;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.BaseMachineBlockEntity;
import com.hbm.reactor.rbmk.RBMKLevelContext;
import com.hbm.reactor.rbmk.RBMKManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Minimal control rod block entity. Mirrors the insertion value from block state
 * into the RBMK column below so other components (fuel rods, diagnostics) can
 * read it.
 */
public class RBMKControlRodEntity extends BaseMachineBlockEntity {

    private static final double MAX_INSERTION = BlockRBMKControlRod.MAX_INSERTION;

    public RBMKControlRodEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RBMK_CONTROL_ROD_ENTITY.get(), pos, state);
        this.items = net.minecraft.core.NonNullList.create();
        this.slotModes = java.util.Collections.emptyList();
    }

    public void serverTick() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos corePos = worldPosition.below();
        RBMKLevelContext context = RBMKManager.context(serverLevel);
        BlockState state = getBlockState();
        double insertion = 0.0D;
        if (state.hasProperty(BlockRBMKControlRod.INSERTION)) {
            insertion = state.getValue(BlockRBMKControlRod.INSERTION) / MAX_INSERTION;
        }
        context.setControlRodInsertion(corePos, insertion);
    }

    @Override
    public void onChunkUnloaded() {
        resetColumn();
        super.onChunkUnloaded();
    }

    @Override
    public void setRemoved() {
        resetColumn();
        super.setRemoved();
    }

    private void resetColumn() {
        if (level instanceof ServerLevel serverLevel) {
            RBMKManager.context(serverLevel).setControlRodInsertion(worldPosition.below(), 0.0D);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_rbmk_control_rod");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }
}
