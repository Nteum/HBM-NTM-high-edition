package com.hbm.blockentity.machine;

import com.hbm.registries.ModItems;
import com.hbm.block.machine.BlockDifurnace;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.gui.menu.DifurnaceMenu;
import com.hbm.Inventory.recipe.BlastFurnaceRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 熔炉（difurnace）对应的方块实体
 * */
public class DifurnaceEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible {
    public int progress = 0;
    public int fuel = 0;
    public static final int maxFuel = 12800;
    public static final int maxProcess = 400;
    //0：燃料槽；1：合成槽1；2：合成槽2；3：产出槽
    private static final int[] slots = new int[] { 0, 1, 2, 3 };
    //所有物品槽的物品表
    public NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    public static Map<ItemLike,Integer> fuelPower = new HashMap<>();
    public static final RecipeManager.CachedCheck<Container, BlastFurnaceRecipe> quickCheck = RecipeManager.createCheck(BlastFurnaceRecipe.Type.INSTANCE);
//    public byte sideFuel = 1;
//    public byte sideUpper = 1;
//    public byte sideLower = 1;
    static {
        fuelPower.put(Items.COAL,200);
        fuelPower.put(Blocks.COAL_BLOCK,2000);
        fuelPower.put(Items.LAVA_BUCKET,12800);
        fuelPower.put(Items.BLAZE_ROD,1000);    //烈焰棒
        fuelPower.put(Items.BLAZE_POWDER,300);  //烈焰粉
        fuelPower.put(ModItems.LIGNITE.get(),150);
        fuelPower.put(ModItems.POWDER_LIGNITE.get(),150);
        fuelPower.put(ModItems.POWDER_COAL.get(),200);
        fuelPower.put(ModItems.COKE_COAL.get(),400);        //煤焦炭
        fuelPower.put(ModItems.COKE_LIGNITE.get(),400);
        fuelPower.put(ModItems.COKE_PETROLEUM.get(),400);
        fuelPower.put(ModItems.SOLID_FUEL.get(),400);
        fuelPower.put(ModItems.BRIQUETTE_COAL.get(),200);   //煤球
        fuelPower.put(ModItems.BRIQUETTE_LIGNITE.get(),200);
        fuelPower.put(ModItems.BRIQUETTE_WOOD.get(),200);
    }
    //用于和menu传递的消息。
    protected final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> progress ;
                case 1 -> fuel;
                default -> 0;
            };
        }
        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex){
                case 0 -> progress = pValue;
                case 1 -> fuel = pValue;
            }
        }
        @Override
        public int getCount() {
            return 2;
        }
    };
    public DifurnaceEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.DIFURNACE_ENTITY.get(), pPos, pBlockState);
    }

    /**
     * 继承自BlockEntity的函数
     * */
    //存储数据
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("progress",progress);
        pTag.putInt("fuel",fuel);
        ContainerHelper.saveAllItems(pTag, this.items);
    }
    //加载数据
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(pTag, this.items);
        this.progress = pTag.getInt("progress");
        this.fuel = pTag.getInt("fuel");
    }

    /**
     * 时钟函数，每tick调取，由方块类中的getTicker获取。
     * 这个函数没有继承任何类，我也不知道为什么这样
     * 有的实体也写成serverTick
     * */
    public static void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (!(pBlockEntity instanceof DifurnaceEntity))return;
        DifurnaceEntity entity = (DifurnaceEntity) pBlockEntity;
        if (!level.isClientSide){
            boolean isChanged = false;
            boolean isWorking = false;
            /** 添加燃料 */
            ItemStack stack0 = entity.items.get(0);
            ItemStack stack1 = entity.items.get(1);
            ItemStack stack2 = entity.items.get(2);
            Integer itemPower = getFuel(stack0);
            if (itemPower > 0 && itemPower <= maxFuel - entity.fuel){
                entity.fuel += itemPower;
                stack0.setCount(stack0.getCount() - 1);
                isChanged = true;
                if (stack0.isEmpty())stack0 = ItemStack.EMPTY;
            }
            /** 加工物品 */
            //判断是否可以处理
            boolean canProcess = false;
            ItemStack stack3 = entity.items.get(3);
            ItemStack output = ItemStack.EMPTY;
            BlastFurnaceRecipe recipe;
            if (!entity.items.get(1).isEmpty() && !entity.items.get(2).isEmpty() && entity.fuel > 0
                    && stack3.getCount() < stack3.getMaxStackSize()){
                recipe = quickCheck.getRecipeFor(entity, level).orElse(null);
                output = recipe.recipeItems.getZ();
                if (output.is(stack3.getItem()) || output.getCount() + stack3.getCount() <= stack3.getMaxStackSize()) {
                    canProcess = true;
                }
            }
            if (canProcess){
                entity.progress += 1;
                entity.fuel -= 1;

                if (entity.progress == maxProcess){
                    if (stack3.isEmpty()){
                        entity.items.set(3,output.copy());
//                        stack3 = output.copy();
                    }
                    else stack3.setCount(stack3.getCount() + output.getCount());
                    stack1.setCount(stack1.getCount() - 1);
                    if (stack1.isEmpty())stack1 = ItemStack.EMPTY;
                    stack2.setCount(stack2.getCount() - 1);
                    if (stack2.isEmpty())stack2 = ItemStack.EMPTY;
                    entity.progress = 0;
                    /*
                    * 让方块灭掉
                    *  */
                    pState = pState.setValue(BlockDifurnace.LIT,Boolean.FALSE);
                    level.setBlock(pPos,pState,3);
                    isChanged = true;
                }
            }else {
                entity.progress = 0;
            }
            /*
            * 让炉子亮起来
            * */
            if (entity.progress == 1){
                pState = pState.setValue(BlockDifurnace.LIT,Boolean.TRUE);
                level.setBlock(pPos,pState,3);
                isChanged = true;
            }
            if (!canProcess && pState.getValue(BlockDifurnace.LIT) == Boolean.TRUE){
                pState = pState.setValue(BlockDifurnace.LIT,Boolean.FALSE);
                level.setBlock(pPos,pState,3);
                isChanged = true;
            }
            /** 保存更改 */
            if (isChanged)setChanged(level,pPos,pState);
        }
    }
    public static int getFuel(ItemStack itemStack){
        Integer fuel_power = fuelPower.get(itemStack.getItem());
        return fuel_power == null?0:fuel_power;
    }

    /** 继承自BaseContainerBlockEntity */
    @Override
    protected Component getDefaultName() {
        return Component.translatable("hbmxx.container.difurnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new DifurnaceMenu(pContainerId,pInventory,this,containerData);
    }

    /**
     * WorldLyContainer 接口函数
     * - getSlotsForFace：不同面对应哪些槽
     * - canPlaceItemThroughFace：通过侧面输入物品
     * - canTakeItemThroughFace：通过侧面输出物品
     * */
    @Override
    public int[] getSlotsForFace(Direction pSide) {
        if (pSide == Direction.DOWN)return new int[]{3};
        else return pSide == Direction.UP ? null : new int[]{1,2};
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        if (pIndex == 3)return false;
        else{
            ItemStack itemStack = this.items.get(pIndex);
            return pItemStack.is(itemStack.getItem());
        }
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        if (pDirection != Direction.UP && pIndex == 3){
            return true;
        }else return false;
    }
    /**
     * Container 接口函数
     * 实现WorldLyContainer似乎不得不修改它们，大部分借鉴的 AbstractFurnaceBlockEntity
     * */
    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return this.items.get(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(this.items, pSlot, pAmount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(this.items, pSlot);
    }

    /**
     * 将实体的物品槽设为给定的物品
     * 这个应该就是相当于玩家直接拿着一些物品点击物品槽，不管物品槽有没有物品都会直接被玩家手里的替换掉
     * 如果修改合成物品，则要修改燃烧进度。
     * */
    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        ItemStack itemStack = this.items.get(pSlot);
        boolean flag = !pStack.isEmpty() && ItemStack.isSameItemSameTags(itemStack,pStack);
        this.items.set(pSlot,pStack);
        if (pStack.getCount() > this.getMaxStackSize()){
            pStack.setCount(this.getMaxStackSize());
        }

        if ((pSlot == 1 || pSlot == 2) && !flag){
            this.progress = 0;
            this.setChanged();  //标识数据改变，需要保存
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return Container.stillValidBlockEntity(this, pPlayer);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        if (pIndex == 3){
            return false;
        }
        else return true;
    }

    /** 继承自StackedContentsCompatible */
    @Override
    public void fillStackedContents(StackedContents pContents) {
        for(ItemStack itemstack : this.items) {
            pContents.accountStack(itemstack);
        }
    }
}
