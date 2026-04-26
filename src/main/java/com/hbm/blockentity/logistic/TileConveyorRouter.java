package com.hbm.blockentity.logistic;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.filter.HBMFilter;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMenuTile;
import com.hbm.gui.menu.MenuConveyorRouter;
import com.hbm.utils.InventoryUtils;
import com.hbm.utils.math.BitUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TileConveyorRouter extends BaseMenuTile {
    public static int SLOT_NUM = 5 * 6;
    HBMFilter.RootFilter[] filters = new HBMFilter.RootFilter[6];
    boolean routered = false;
    int sequenceIndex = 0;      // 用于设计
    ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> isFull() ? 1 : 0;
                case 1 -> getMode();
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {}

        @Override
        public int getCount() {
            return 2;
        }
    };
    public TileConveyorRouter(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.TILE_CONVEYOR_ROUTER.get(), pos, state);
        for (int i = 0; i < 6; i++) {
            filters[i] = HBMFilter.create(5);
        }
        this.items = new ItemStackHandler(SLOT_NUM + 1) {
            @Override
            protected void onContentsChanged(int slot) {
                if (slot < SLOT_NUM) filters[slot / 5].set(slot, HBMFilter.create(this.getStackInSlot(slot)));
                setChanged();
            }
        };
        // 留一个额外的空位用于表明
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, new RangedWrapper(this.items, SLOT_NUM, SLOT_NUM + 1));
    }
    // 判定机器是否拥堵（slot不为空，且上一tick并未输出物品）
    boolean isFull(){
        return !this.items.getStackInSlot(SLOT_NUM).isEmpty() && !routered;
    }

    int getMode(){
        int result = 0;
        for (int i = 0; i < filters.length; i++) {
            result = BitUtil.set(result, i * 2, 2, filters[i].getMode());
        }
        return result;
    }

    void setMode(int mode){
        for (int i = 0; i < filters.length; i++) {
            filters[i].setMode(BitUtil.get(mode, i * 2, 2));
        }
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        this.routered = false;
        ItemStack stack;
        if (!(stack = this.items.extractItem(SLOT_NUM, 1, true)).isEmpty()){
            for (int i = 0; i < 6; i++) {
                int side = (sequenceIndex + 1 + i) % 6;
                if (filters[side].test(stack) && tryExportTo(stack, Direction.from3DDataValue(side))) {
                    this.items.extractItem(SLOT_NUM, 1, false);
                    this.routered = true;
                    sequenceIndex = side;
                    break;
                }
            }
        }else {
            // 吸收落在上面的掉落物，主要用于对接可掉落物品
            if (this.getLevel().getGameTime() % 10 == 0){
                // 搜索掉落在传送带上的物品，如果传送带是空的，就把它放入传送带
                List<ItemEntity> list = new ArrayList<>(1);
                this.getLevel().getEntities(EntityTypeTest.forClass(ItemEntity.class), new AABB(this.worldPosition.relative(Direction.UP)), entity -> true, list, 1);
                ItemEntity itemEntity;
                if (!list.isEmpty() && (itemEntity = list.get(0)).getDeltaMovement().length() < 0.1){   // 确保物品实体状态是近乎静止的
                    ItemStack itemStack = itemEntity.getItem();
                    itemStack.shrink(1 - this.items.insertItem(SLOT_NUM, itemStack.copyWithCount(1), false).getCount());
                    if (itemStack.isEmpty()){
                        itemEntity.discard();
                    }else {
                        itemEntity.setItem(itemStack);
                    }
                }
            }
        }
    }

    protected boolean tryExportTo(ItemStack stack, Direction direction){
        BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.relative(direction));
        if (blockEntity != null){
            LazyOptional<IItemHandler> lazyOptional = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite());
            if (lazyOptional.isPresent() && lazyOptional.resolve().isPresent()){
                return InventoryUtils.insertNoCheckSlots(stack, lazyOptional.resolve().get()) > 0;
            }
        }
        return false;
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MenuConveyorRouter(pContainerId, pInventory, this, containerData);
    }

    @Override
    public Component getName() {
        return HBMLang.CONTAINER_CONVEYOR_ROUTER.translate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        for (int i = 0; i < 6; i++) {
            tag.put(i + "", filters[i].serializeNBT());
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        for (int i = 0; i < 6; i++) {
            if (nbt.contains(i + "")) filters[i].deserializeNBT(nbt.getCompound(i + ""));
        }
    }

    @Override
    public void handleClientPacket(@NotNull CompoundTag tag) {
        super.handleClientPacket(tag);
        if (tag.contains(HBMKey.MODE, Tag.TAG_INT)) this.setMode(tag.getInt(HBMKey.MODE));
    }
}
