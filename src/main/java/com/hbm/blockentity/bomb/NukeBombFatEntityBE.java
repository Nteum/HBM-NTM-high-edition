package com.hbm.blockentity.bomb;

import com.hbm.blockentity.HBMTiles;
import com.hbm.core.capability.item.MachineItemHandler;
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

public class NukeBombFatEntityBE extends EntityNukeBombBE {
    public static final AABB BOX = AABB.of(new BoundingBox(-1,0,-1,2,1,1));
    public NukeBombFatEntityBE(BlockPos pPos, BlockState pBlockState) {
        super(pPos, pBlockState);
        this.items = new MachineItemHandler(4);
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;
    }
}
