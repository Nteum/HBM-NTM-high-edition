package com.hbm.blockentity.machine;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class CrucibleEntity extends BaseMachineBlockEntity {
    public int heat;
    public int progress;
    public static int processTime = 20_000;
    public static double diffusion = 0.25D;
    public static int maxHeat = 100_000;
    public static final AABB BOX = AABB.of(new BoundingBox(-1,0,-1,2,2,2));
    public CrucibleEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.CRUCIBLE_ENTITY.get(), pPos, pBlockState);
        items = NonNullList.withSize(10,ItemStack.EMPTY);   //物品栏，10个栏位
    }

    public static void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
        if (!level.isClientSide()){

        }
    }

    //GUI上显示的名字
    @Override
    protected Component getDefaultName() {
        return Component.translatable("hbmxx.container.crucible");
    }
    //创建对应的菜单类
    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        //模板栏只能输入模板
        if (pIndex == 0){
            return pStack.is(ModItems.crucible_template.get());
        }
        //其他栏位看物品是否可被熔化
        return isItemSmeltable(pStack);
    }
    //判断物品是否可被熔化
    public boolean isItemSmeltable(ItemStack itemStack){
        return true;
    }

//    @Override
//    public AABB getRenderBoundingBox() {
//        //规定碰撞箱
//        return BOX;
//    }
    @Override
    public int getMaxStackSize() {
        //坩埚一个栏位只能放一个东西
        return 1;
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        super.setItem(pSlot, pStack);
    }

    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return false;
    }
}
