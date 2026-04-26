package com.hbm.blockentity.machine.rbmk;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.reactor.rbmk.RBMKLevelContext;
import com.hbm.reactor.rbmk.RBMKManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class RBMKCoolerEntity extends BaseMachineBlockEntity implements RBMKTickableEntity {

    public RBMKCoolerEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RBMK_COOLER_ENTITY.get(), pos, state);
        this.items = NonNullList.create();
        this.slotModes = java.util.List.of();
    }

    @Override
    public void serverTick() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        RBMKLevelContext context = RBMKManager.context(serverLevel);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                context.addHeat(worldPosition.below().offset(dx, 0, dz), -10.0D / 25.0D);
            }
        }
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.rbmk_cooler");
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;
    }
}
