package com.hbm.blockentity.base;

import com.hbm.HBMKey;
import com.hbm.api.energy.IEnergyContainer;
import com.hbm.api.energy.IEnergyHandler;
import com.hbm.blockentity.machine.TileMinerLarge;
import com.hbm.utils.DirectionUtils;
import com.hbm.utils.multiblock.MultiblockModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * 新的模板，默认作用于处理配方的机器上
 * */
public abstract class DefaultMachineBE extends BaseMenuTile{
    protected IEnergyContainer energyContainer;
    protected IFluidHandler fluidHandler;
    protected MultiblockModule multiblockModule;
    public DefaultMachineBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (multiblockModule != null){
            if (!multiblockModule.distributed && (multiblockModule.isFormed = multiblockModule.isFormed && checkProxy())){
                distributeCapabilities();
                multiblockModule.distributed = true;
            }
        }
    }

    public boolean checkProxy(){
        if (!this.hasLevel() || this.multiblockModule == null) return false;
        for (Vec3i offset : DirectionUtils.offsetRot(multiblockModule.offsets, Direction.SOUTH, this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))) {
            if (!(this.level.getBlockEntity(this.getBlockPos().offset(offset)) instanceof TileProxyBase)) return false;
        }
        return true;
    }

    public void distributeCapabilities(){}

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (this.multiblockModule != null)
            pTag.putBoolean(HBMKey.IS_FORMED, this.multiblockModule.isFormed);
        if (this.energyContainer != null)
            pTag.put(HBMKey.ENERGY, this.energyContainer.serializeNBT());
        if (this.fluidHandler != null)
            pTag.putInt(HBMKey.FLUIDS, this.fluidHandler.getFluidInTank(0).getAmount());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains(HBMKey.IS_FORMED) && this.multiblockModule != null)
            this.multiblockModule.isFormed = nbt.getBoolean(HBMKey.IS_FORMED);
    }
}
