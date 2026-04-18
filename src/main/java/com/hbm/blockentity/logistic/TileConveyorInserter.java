package com.hbm.blockentity.logistic;

import com.hbm.HBMLang;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.gui.menu.MenuConveyorInserter;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class TileConveyorInserter extends TileConveyorMachine{
    public static int SLOT_NUM = 21;
    private boolean destroyer = false;
    private ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0-> destroyer ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {}

        @Override
        public int getCount() {
            return 1;
        }
    };
    public TileConveyorInserter(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.TILE_CONVEYOR_INSERTER.get(), pos, state);
        items = new ItemStackHandler(SLOT_NUM + 1){
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
            //溢出槽，在destroyer模式下，输入到溢出槽的物品被销毁。
            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (slot == SLOT_NUM && !destroyer) return stack;
                ItemStack result = super.insertItem(slot, stack, simulate);
                if (destroyer && slot == SLOT_NUM && result.getCount() != stack.getCount() && !simulate){
                    setStackInSlot(SLOT_NUM, ItemStack.EMPTY);
                    return ItemStack.EMPTY;
                }
                return result;
            }
        };
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, this.items);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        boolean isSync = false;
        if (!this.level.hasNeighborSignal(this.worldPosition)) {
            Direction outputSide = getOutputSide();
            BlockEntity be = WorldUtils.getTileEntity(this.level, this.worldPosition.relative(outputSide));
            if (be != null) {
                LazyOptional<IItemHandler> lazyOptional = be.getCapability(ForgeCapabilities.ITEM_HANDLER, outputSide.getOpposite());
                if (lazyOptional.isPresent() && lazyOptional.resolve().isPresent()){
                    isSync = InventoryUtils.insertNoCheckSlots(this.items, lazyOptional.resolve().get());
                }
            }
        }
        if (isSync) this.sendUpdatePacket();
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MenuConveyorInserter(pContainerId, pInventory, this, containerData);
    }

    @Override
    public Component getName() {
        return HBMLang.CONTAINER_CONVEYOR_INSERTER.translate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("destroyer", destroyer);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.destroyer = nbt.getBoolean("destroyer");
    }

    @Override
    public void handleClientPacket(@NotNull CompoundTag tag) {
        super.handleClientPacket(tag);
        this.destroyer = tag.getBoolean("destroyer");
    }

//    @Override
//    public void handleUpdatePacket(@NotNull CompoundTag tag) {
//        super.handleUpdatePacket(tag);
//        this.destroyer = tag.getBoolean("destroyer");
//    }
}
