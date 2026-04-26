package com.hbm.blockentity.logistic;

import com.hbm.HBMKey;
import com.hbm.Inventory.filter.SidedItemManager;
import com.hbm.block.logistic.Conveyor;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.CapabilityBlockEntity;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TileConveyor extends CapabilityBlockEntity {
    static int DOUBLE_CLICK_TIME = 10;
    public static int MAX_TRANSPORT_PROGRESS = 40;
    static float CONVEYOR_HEIGHT = 5.0f/16;
    int transPortProgress = -1;      // 物品输送的进度（不等于计时器）
    int doubleClickTimer = -1;       // 判断玩家双击的计时器
    public Direction inDir;                // 物品输入的方向，默认就是null，当需要更新时才有取值。
//    int variant;                    // 输送带状态，可以从blockstate获取，这样做是为了更新便利。
//    Vec3 joinLoc;
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
    SidedItemManager sidedItemManager;
    public TileConveyor(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.TILE_CONVEYOR.get(), pos, state);
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        int variant = state.getValue(Conveyor.VARIANT);
        Direction outputDir = Conveyor.getOutputDir(state);
        sidedItemManager = new SidedItemManager(items);
        for (Direction direction : Direction.values()) {
            this.capabilitiesContent.forceAddCapability(ForgeCapabilities.ITEM_HANDLER, sidedItemManager.get(direction), direction);
        }
        sidedItemManager.disable(outputDir);    // 输出口保留能力，但被禁用
//        // 只能传送带来的方向接收物品
//        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, this.items, facing.getOpposite());
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        int variant = this.getBlockState().getValue(Conveyor.VARIANT);
        this.inDir = this.sidedItemManager.sideUpdate();    // 每tick更新以免出现问题，由于facing的用法是
        // 更新双击定时器，到时间未更新则停止
        if (doubleClickTimer >= DOUBLE_CLICK_TIME){
            doubleClickTimer = -1;
        }else if (doubleClickTimer >= 0) doubleClickTimer ++;
        // 更新输送定时器，若定时器时间到了，则试图向邻接的传送带传送物品
        int oldValue = this.transPortProgress;
        if (!this.isEmpty()){
            if (oldValue == -1) {                               // 检测到有物品传入，放在传送带初始位置
                // 如果放置方向和输送带方向是正交的，则视为放置在正中间了
                if (Conveyor.isOrthogonal(this.getBlockState(), inDir)) this.transPortProgress = getMaxTransportProgress() / 2;
                else this.transPortProgress = 0;
            }else if (this.transPortProgress < getMaxTransportProgress() - 2){ // 尚未到终点，在传送带上移动
                this.transPortProgress += 2;
            }else{                                              // 到达传送带出口，尝试将物品传递给目标方向
                Direction outDir = Conveyor.getOutputDir(this.getBlockState());                 // 输出口的方向
                BlockPos outNeighbour = this.worldPosition.relative(outDir);
                BlockEntity blockEntity = this.getLevel().getBlockEntity(outNeighbour);
                if ((blockEntity == null && this.getLevel().getBlockState(outNeighbour).getCollisionShape(this.getLevel(), outNeighbour).isEmpty())
                        || (blockEntity != null && blockEntity.getBlockState().getBlock() instanceof Conveyor && blockEntity.getBlockState().getValue(Conveyor.VARIANT) == 6)){
                    ItemStack stackInSlot = this.items.extractItem(0, 1, false);
                    Vec3 center = outNeighbour.getCenter();
                    ItemEntity itemEntity = new ItemEntity(this.getLevel(), center.x, center.y, center.z, stackInSlot);
                    itemEntity.setDeltaMovement(0,0,0);
                    this.getLevel().addFreshEntity(itemEntity);
                }else if (blockEntity != null){
                    blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, outDir.getOpposite()).ifPresent(iItemHandler -> InventoryUtils.insertNoCheckSlots(this.items, iItemHandler, 0, 1));
                }
            }
        }
        // 传送后再判断一次，确保不漏tick
        if (this.isEmpty()){
            this.transPortProgress = -1;                // 传送完了，进度重新设为0
            if (this.getLevel().getGameTime() % 5 == 0 && (variant < 3 || variant == 7)){   // 3,4,5,6变体根本不接受掉落物
                // 搜索掉落在传送带上的物品，如果传送带是空的，就把它放入传送带
                List<ItemEntity> list = new ArrayList<>(1);
                this.getLevel().getEntities(EntityTypeTest.forClass(ItemEntity.class), new AABB(this.worldPosition), entity -> true, list, 1);
                ItemEntity itemEntity;
                if (!list.isEmpty() && (itemEntity = list.get(0)).getDeltaMovement().length() < 0.1){   // 确保物品实体状态是近乎静止的
                    ItemStack itemStack = itemEntity.getItem();
                    this.items.insertItem(0, itemStack.copyWithCount(1), false);
                    itemStack.shrink(1);
                    if (itemStack.isEmpty()){
                        itemEntity.discard();
                    }else {
                        itemEntity.setItem(itemStack);
                    }
                    this.transPortProgress = calcTransProgress(itemEntity.position());
                }
            }
        }
        if (this.transPortProgress != oldValue)
            sendUpdatePacket();
    }
    // 计算掉落在传送带上的物品对应的传送位置
    protected int calcTransProgress(Vec3 itemPos){
        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        int variant = this.getBlockState().getValue(Conveyor.VARIANT);
        switch (variant){
            case 0,7 -> {
                return (int) (DirectionUtils.locToSideDist(itemPos, facing.getOpposite()) * getMaxTransportProgress());
            }
            case 1,2 -> {
                return (int) (DirectionUtils.locToCornerAngle(itemPos, facing.getOpposite(), DirectionUtils.leftAndRightDir(facing, variant).getOpposite()) * 2 / Math.PI * getMaxTransportProgress());
            }
        }
        return -1;
    }

    public int getMaxTransportProgress(){
        int variant = this.getBlockState().getValue(Conveyor.VARIANT);
        return variant == 4 ? MAX_TRANSPORT_PROGRESS * 2 : variant == 5 ? MAX_TRANSPORT_PROGRESS * 2 / 3 : MAX_TRANSPORT_PROGRESS;
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
        tag.putInt("transPortProgress", this.transPortProgress);
        if (this.inDir != null) tag.putInt("inDir", this.inDir.get3DDataValue());
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        if (tag.contains(HBMKey.ITEM, Tag.TAG_COMPOUND)) this.items.deserializeNBT(tag.getCompound(HBMKey.ITEM));
        this.transPortProgress = tag.getInt("transPortProgress");
        if (tag.contains("inDir", Tag.TAG_INT)){
            this.inDir = Direction.from3DDataValue(tag.getInt("inDir"));
        }
    }

    public ItemStackHandler getItems(){
        return this.items;
    }

    public boolean isEmpty(){
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

    public int getTransPortProgress(){
        return this.transPortProgress;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return super.getCapability(cap, side);
    }
}
