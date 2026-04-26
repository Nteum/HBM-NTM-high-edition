package com.hbm.blockentity.logistic;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.HBMUpgrade;
import com.hbm.Inventory.filter.HBMFilter;
import com.hbm.Inventory.filter.ItemFilterWrapper;
import com.hbm.block.HBMBlockProperties;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.gui.menu.MenuConveyorExtractor;
import com.hbm.utils.DirectionUtils;
import com.hbm.utils.InventoryUtils;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;

public class TileConveyorExtractor extends TileConveyorMachine {
    public boolean isWhitelist = false;
    public boolean maxEject = false;
    HBMFilter.RootFilter itemFilter;
    public ItemFilterWrapper itemWrapper;
    protected ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> isWhitelist ? 1 : 0;
                case 1 -> maxEject ? 1 : 0;
                default ->  0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {}

        @Override
        public int getCount() {
            return 2;
        }
    };
    public TileConveyorExtractor(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.TILE_CONVEYOR_EXTRACTOR.get(), pos, state);
        this.itemFilter = HBMFilter.create(9);
        this.items = new ItemStackHandler(20){
            @Override
            protected void onContentsChanged(int slot) {
                if (slot >= 0 && slot < 9) itemFilter.set(slot, HBMFilter.create(this.getStackInSlot(slot)));
                setChanged();
            }
        };
        itemWrapper = new ItemFilterWrapper(new RangedWrapper(this.items, 9, 18), this.itemFilter);
        renewItemCaps(state);
    }

    @Override
    public void renewItemCaps(BlockState state){
        Direction facing = state.getValue(BlockStateProperties.FACING);
        Direction secondaryFacing = DirectionUtils.relativeDir2Dir(facing, state.getValue(HBMBlockProperties.RELATIVE_DIRECTION));
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, itemWrapper, facing, secondaryFacing);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        this.itemFilter.setBlackList(!this.isWhitelist);

        int delay = 20;
        switch (HBMUpgrade.getTier(this.items.getStackInSlot(19))){
            case 1 -> delay = 10;
            case 2 -> delay = 5;
            case 3 -> delay = 2;
        }
        if (this.level.getGameTime() % delay == 0 && !this.level.hasNeighborSignal(this.worldPosition)){
            int amount = switch (HBMUpgrade.getTier(this.items.getStackInSlot(18))){
                case 1 -> 4;
                case 2 -> 16;
                case 3 -> 64;
                default -> 1;
            };
            Direction inputSide = this.getBlockState().getValue(BlockStateProperties.FACING);
            Direction outputSide = getOutputSide();
            BlockEntity be = WorldUtils.getTileEntity(this.level, this.worldPosition.relative(inputSide));
            BlockEntity beOut = WorldUtils.getTileEntity(this.level, this.worldPosition.relative(outputSide));

            if (be != null){
                be.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
                    InventoryUtils.insertNoCheckSlots(iItemHandler, itemWrapper);
                });
            }

            if (beOut != null){
                beOut.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
                    for (int i = 0; i < this.itemWrapper.getSlots(); i++) {
                        ItemStack stackInSlot = this.itemWrapper.getStackInSlot(i);
                        if ((!maxEject && !stackInSlot.isEmpty()) || (maxEject && stackInSlot.getCount() >= amount)){
                            if (InventoryUtils.insertNoCheckSlots(itemWrapper, iItemHandler, i, amount) > 0) break;
                        }
                    }
                });
            }
        }
    }

    @Override
    public Component getName() {
        return HBMLang.CONTAINER_CONVEYOR_EXTRACTOR.translate();
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MenuConveyorExtractor(pContainerId, pInventory, this, containerData);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("isWhitelist", isWhitelist);
        tag.putBoolean("maxEject", maxEject);
        tag.put(HBMKey.FILTER, this.itemFilter.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.isWhitelist = nbt.getBoolean("isWhitelist");
        this.maxEject = nbt.getBoolean("maxEject");
        this.itemFilter.deserializeNBT(nbt.getCompound(HBMKey.FILTER));
    }

    @Override
    public void handleClientPacket(@NotNull CompoundTag tag) {
        super.handleClientPacket(tag);
        if (tag.contains("isWhitelist"))
            this.isWhitelist = tag.getBoolean("isWhitelist");
        if (tag.contains("maxEject"))
            this.maxEject = tag.getBoolean("maxEject");
    }
}
