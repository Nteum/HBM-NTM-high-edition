package com.hbm.blockentity.machine;

import com.hbm.core.blockentity.BEMachineBase;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 警报器（单方块，简化版）。
 * 移植自旧版 TileEntityMachineSiren（去掉磁带选择）：红石触发播放固定警笛声。
 */
public class SirenEntity extends BEMachineBase {
    public boolean lock;

    public SirenEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(0);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        boolean active = this.getLevel().hasNeighborSignal(this.worldPosition);

        if (active && !lock){
            lock = true;
            this.getLevel().playSound(null, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(),
                    ModSounds.ALARM_REGULAR_SIREN.get(), SoundSource.BLOCKS, 2.0F, 1.0F);
        }
        if (!active && lock){
            lock = false;
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null; // 无 GUI
    }
}
