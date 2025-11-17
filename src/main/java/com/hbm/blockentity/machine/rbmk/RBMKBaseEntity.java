package com.hbm.blockentity.machine.rbmk;

import com.hbm.block.machine.rbmk.BlockRBMKBase;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.DummyableBlockEntity;
import com.hbm.reactor.rbmk.RBMKLevelContext;
import com.hbm.reactor.rbmk.RBMKManager;
import com.hbm.reactor.rbmk.RBMKLidType;
import com.hbm.reactor.rbmk.RBMKSettings;
import com.hbm.registries.ModBlocks;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 反应堆核心方块实体。当前负责把方块注册到 {@link RBMKManager}，方便后续接入中子/热模拟。
 */
public class RBMKBaseEntity extends DummyableBlockEntity {

    public RBMKBaseEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntityType.RBMK_BASE_ENTITY.get(), pPos, pState);
        this.multiblockData = MultiblockData.mapping.get(ModBlocks.machine_rbmk_base.get());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel) {
            RBMKLevelContext context = RBMKManager.context(serverLevel);
            RBMKLidType lidType = getLidType();
            context.registerColumn(getBlockPos(), RBMKSettings.forLevel(serverLevel)).setLidType(lidType);
        }
    }

    @Override
    public void onChunkUnloaded() {
        if (level instanceof ServerLevel serverLevel) {
            RBMKManager.context(serverLevel).removeColumn(getBlockPos());
        }
        super.onChunkUnloaded();
    }

    @Override
    public void setRemoved() {
        if (level instanceof ServerLevel serverLevel) {
            RBMKManager.context(serverLevel).removeColumn(getBlockPos());
        }
        super.setRemoved();
    }

    public RBMKLidType getLidType() {
        BlockState state = getBlockState();
        if (state.hasProperty(BlockRBMKBase.LID)) {
            return state.getValue(BlockRBMKBase.LID);
        }
        return RBMKLidType.NONE;
    }

    public void setLidType(RBMKLidType type) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockState state = level.getBlockState(worldPosition);
        if (state.hasProperty(BlockRBMKBase.LID) && state.getValue(BlockRBMKBase.LID) != type) {
            level.setBlock(worldPosition, state.setValue(BlockRBMKBase.LID, type), Block.UPDATE_ALL);
        }
        RBMKManager.context(serverLevel).setLidState(worldPosition, type);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_rbmk_base");
    }
}
