package com.hbm.blockentity.machine;

import com.hbm.api.badthing.ContaminationUtil;
import com.hbm.api.badthing.HbmLivingProps;
import com.hbm.core.blockentity.BEMachineBase;
import com.hbm.core.capability.item.MachineItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 净化装置（单方块）。
 * 移植自旧版 TileEntityDecon：清除范围内生物的辐射。
 */
public class DeconEntity extends BEMachineBase {
    public DeconEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(0);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        AABB box = new AABB(this.worldPosition.getX() - 0.5, this.worldPosition.getY(), this.worldPosition.getZ() - 0.5,
                this.worldPosition.getX() + 1.5, this.worldPosition.getY() + 2, this.worldPosition.getZ() + 1.5);
        List<LivingEntity> entities = this.getLevel().getEntitiesOfClass(LivingEntity.class, box);
        for (LivingEntity e : entities){
            HbmLivingProps.incrementRadiation(e, -0.5F);
            HbmLivingProps.getCont(e).clear();
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null; // 无 GUI
    }
}
