package com.hbm.blockentity.logistic;

import com.hbm.HBMKey;
import com.hbm.block.logistic.ConveyorExtractor;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.CapabilityBlockEntity;
import com.hbm.utils.DirectionUtils;
import com.hbm.utils.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class TileConveyorExtractor extends CapabilityBlockEntity {
    private ItemStackHandler items = new ItemStackHandler(27){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public TileConveyorExtractor(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.TILE_CONVEYOR_EXTRACTOR.get(), pos, state);
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, items);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        BlockState blockState = this.getBlockState();
        Direction mainPortSide = blockState.getValue(ConveyorExtractor.MAIN_PORT_SIDE);
        Direction secondaryPortSide = DirectionUtils.relativeDir2Dir(mainPortSide, blockState.getValue(ConveyorExtractor.SECONDARY_PORT_SIDE));
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this.worldPosition.relative(mainPortSide));
        if (blockEntity != null){
            blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, mainPortSide.getOpposite()).ifPresent(iItemHandler -> InventoryUtils.insertNoCheckSlots(iItemHandler, this.items));
        }
        blockEntity = this.getLevel().getBlockEntity(this.worldPosition.relative(secondaryPortSide));
        if (blockEntity != null){
            blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, secondaryPortSide.getOpposite()).ifPresent(iItemHandler -> InventoryUtils.insertNoCheckSlots(this.items, iItemHandler));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(HBMKey.ITEM, this.items.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains(HBMKey.ITEM)) this.items.deserializeNBT(nbt);
    }
}
