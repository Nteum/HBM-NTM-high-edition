package com.hbm.blockentity.base;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.api.energy.IEnergyContainer;
import com.hbm.api.energy.IEnergyHandler;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.fluid.IExtendedFluidHandler;
import com.hbm.block.machine.MachineOreSlopper;
import com.hbm.blockentity.interfaces.IDummyable;
import com.hbm.blockentity.machine.TileMinerLarge;
import com.hbm.registries.HBMCaps;
import com.hbm.utils.DirectionUtils;
import com.hbm.utils.multiblock.MultiblockModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * 新的模板，默认作用于处理配方的机器上
 * */
public abstract class DefaultMachineBE extends BaseMenuTile implements IDummyable {
    protected boolean running = false;
    protected IEnergyContainer energyContainer;
    protected IExtendedFluidHandler fluidHandler;
    protected MultiblockModule multiblockModule;
    public DefaultMachineBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        initCapabilities();
    }

    protected void initCapabilities(){
        if (this.items != null)
            this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, this.items);
        if (this.energyContainer != null)
            this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(this.energyContainer));
        if (this.fluidHandler != null)
            this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this.fluidHandler);
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
        preWork();
        if (canProcess())
            process();
        else
            noProcess();
        afterWork();
    }
    // 铺垫工作
    protected void preWork(){}
    // 是否刻意处理
    protected boolean canProcess(){return true;}
    // 正常处理过程
    protected void process(){}
    // 不处理的状态
    protected void noProcess(){}
    // 收尾工作
    protected void afterWork(){}
    public boolean checkProxy(){
        if (!this.hasLevel() || this.multiblockModule == null) return false;
        for (Vec3i offset : DirectionUtils.offsetRot(multiblockModule.offsets, Direction.SOUTH, this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING))) {
            if (!(this.level.getBlockEntity(this.getBlockPos().offset(offset)) instanceof TileProxyBase)) return false;
        }
        return true;
    }

    public void setFormed(boolean isFormed){
        this.multiblockModule.isFormed = isFormed;
    }

    public void distributeCapabilities(){}

    public void onLeftClick(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit){
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (this.multiblockModule != null)
            pTag.putBoolean(HBMKey.IS_FORMED, this.multiblockModule.isFormed);
        if (this.energyContainer != null)
            pTag.put(HBMKey.ENERGY, this.energyContainer.serializeNBT());
        if (this.fluidHandler != null)
            pTag.put(HBMKey.FLUIDS, this.fluidHandler.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains(HBMKey.IS_FORMED) && this.multiblockModule != null)
            this.multiblockModule.isFormed = nbt.getBoolean(HBMKey.IS_FORMED);
        if (nbt.contains(HBMKey.ENERGY, Tag.TAG_COMPOUND))
            this.energyContainer.deserializeNBT(nbt.getCompound(HBMKey.ENERGY));
        if (nbt.contains(HBMKey.FLUIDS, Tag.TAG_COMPOUND))
            this.fluidHandler.deserializeNBT(nbt.getCompound(HBMKey.FLUIDS));
    }

}
