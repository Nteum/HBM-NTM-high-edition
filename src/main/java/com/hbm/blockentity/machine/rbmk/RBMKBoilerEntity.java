package com.hbm.blockentity.machine.rbmk;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.reactor.rbmk.RBMKColumnState;
import com.hbm.reactor.rbmk.RBMKLevelContext;
import com.hbm.reactor.rbmk.RBMKManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class RBMKBoilerEntity extends BaseMachineBlockEntity implements RBMKTickableEntity {

    private int compressionStage;

    public RBMKBoilerEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RBMK_BOILER_ENTITY.get(), pos, state);
        this.items = NonNullList.create();
        this.slotModes = java.util.List.of();
    }

    @Override
    public void serverTick() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos corePos = worldPosition.below();
        RBMKLevelContext context = RBMKManager.context(serverLevel);
        RBMKColumnState column = context.column(corePos).orElse(null);
        RBMKBaseEntity base = level.getBlockEntity(corePos) instanceof RBMKBaseEntity rbmkBase ? rbmkBase : null;
        if (column == null || base == null) {
            return;
        }
        int water = base.getWaterAmount();
        int steam = base.getSteamAmount();
        int steamSpace = Math.max(0, base.getSteamCapacity() - steam);
        if (water > 0 && steamSpace > 0 && column.heat() > 100.0D) {
            int conversion = Math.min(Math.min(water, steamSpace), Math.max(1, (int) Math.floor(column.heat() / (20.0D + compressionStage * 20.0D))));
            if (conversion > 0) {
                base.consumeWaterForBoiler(conversion);
                base.produceSteamForBoiler(conversion);
                context.addHeat(corePos, -(20.0D + compressionStage * 20.0D) * conversion);
                setChanged();
            }
        }
    }

    public void cycleCompression() {
        compressionStage = (compressionStage + 1) % 4;
        setChanged();
    }

    public int compressionStage() {
        return compressionStage;
    }

    @Override
    public void load(net.minecraft.nbt.CompoundTag tag) {
        super.load(tag);
        compressionStage = Math.max(0, tag.getInt("Compression"));
    }

    @Override
    protected void saveAdditional(net.minecraft.nbt.CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Compression", compressionStage);
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.rbmk_boiler");
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;
    }
}
