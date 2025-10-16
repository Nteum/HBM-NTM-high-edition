package com.hbm.blockentity.weapon;

import com.hbm.blockentity.ModBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class NukeBombFatEntity extends EntityNukeBomb {
    public static final AABB BOX = AABB.of(new BoundingBox(-1,0,-1,2,1,1));
    public NukeBombFatEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.NUKE_BOMB_FAT_ENTITY.get(),pPos, pBlockState);
        this.items = NonNullList.withSize(4, ItemStack.EMPTY);
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

    @Override
    public Component getDefaultName() {
        return null;
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;
    }

//    @Override
//    public AABB getRenderBoundingBox() {
//        return BOX;
//    }
}
