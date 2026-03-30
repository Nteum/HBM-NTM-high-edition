package com.hbm.blockentity.logistic;

import com.hbm.HBMKey;
import com.hbm.block.HBMBlockProperties;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.CapabilityBlockEntity;
import com.hbm.utils.DirectionUtils;
import com.hbm.utils.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TileConveyor extends CapabilityBlockEntity {
    static int DOUBLE_CLICK_TIME = 10;
    static int TRANSPORT_TIME = 20;
    static float CONVEYOR_HEIGHT = 5.0f/16;
    int transPortTimer = -1;         // 判断物品输送的计时器
    int doubleClickTimer = -1;       // 判断玩家双击的计时器
    Vec3 joinLoc;
    ItemStackHandler items = new ItemStackHandler(1){
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            // 只能运输单个物品
            return slot == 0 && stack.getCount() == 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    public TileConveyor(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.TILE_CONVEYOR.get(), pos, state);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        // 更新双击定时器，到时间未更新则停止
        if (doubleClickTimer >= DOUBLE_CLICK_TIME){
            doubleClickTimer = -1;
        }else if (doubleClickTimer >= 0) doubleClickTimer ++;
        // 更新输送定时器，若定时器时间到了，则试图向邻接的传送带传送物品
        int oldValue = this.transPortTimer;
        if (!this.isEmpty()){
            if (oldValue == -1) {
                joinLoc = getDefaultJoinLoc();
            }
            this.transPortTimer ++;
            if (this.transPortTimer >= TRANSPORT_TIME){
                Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
                Integer bend = this.getBlockState().getValue(HBMBlockProperties.VARIANT3);
                Direction outDir = DirectionUtils.leftAndRightDir(facing, bend);
                BlockEntity blockEntity = this.getLevel().getBlockEntity(this.worldPosition.relative(outDir));
                if (blockEntity != null) blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, outDir.getOpposite()).ifPresent(iItemHandler -> InventoryUtils.insertNoCheckSlots(this.items, iItemHandler, 0, 1));
            }
        }
        // 传送后再判断一次，确保不漏tick
        if (this.isEmpty()){
            this.transPortTimer = -1;
            if (this.getLevel().getGameTime() % 5 == 0){
                // 搜索掉落在传送带上的物品，如果传送带是空的，就把它放入传送带
                List<ItemEntity> list = new ArrayList<>(1);
                this.getLevel().getEntities(EntityTypeTest.forClass(ItemEntity.class), new AABB(this.worldPosition), entity -> true, list, 1);
                if (list.size() > 0){
                    ItemEntity itemEntity = list.get(0);
                    ItemStack itemStack = itemEntity.getItem();
                    this.items.insertItem(0, itemStack.copyWithCount(1), false);
                    itemStack.shrink(1);
                    if (itemStack.isEmpty()){
                        itemEntity.discard();
                    }else {
                        itemEntity.setItem(itemStack);
                    }
                    this.transPortTimer = 0;
                    this.joinLoc = new Vec3(itemEntity.getX(), CONVEYOR_HEIGHT, itemEntity.getZ());
                }
            }
        }
        if (this.transPortTimer != oldValue)
            sendUpdatePacket();
    }
    // 默认的物品运输点位置，用于确认物品确实在运输。
    private Vec3 getDefaultJoinLoc(){
        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        return this.worldPosition.getCenter().relative(facing.getOpposite(), 0.5).relative(Direction.DOWN, 3.0 / 16);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(HBMKey.ITEM, this.items.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains(HBMKey.ITEM, Tag.TAG_COMPOUND)) this.items.deserializeNBT(nbt.getCompound(HBMKey.ITEM));
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.put(HBMKey.ITEM, this.items.serializeNBT());
        tag.putInt("transporttimer", this.transPortTimer);
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        if (tag.contains(HBMKey.ITEM, Tag.TAG_COMPOUND)) this.items.deserializeNBT(tag.getCompound(HBMKey.ITEM));
        this.transPortTimer = tag.getInt("transporttimer");
    }

    public ItemStackHandler getItems(){
        return this.items;
    }

    boolean isEmpty(){
        return this.items.getStackInSlot(0).isEmpty();
    }

    public void onLeftClick(Level level, BlockPos pos, BlockState state){
        if (doubleClickTimer < 0) doubleClickTimer = 0;
        else {
            ItemStack itemStack = this.items.extractItem(0, 1, false);
            if (!itemStack.isEmpty()){
                Containers.dropContents(level, pos, NonNullList.of(itemStack));
                level.updateNeighbourForOutputSignal(pos, state.getBlock());
                this.shouldSync = true;
            }
        }
    }
}
