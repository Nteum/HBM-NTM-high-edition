package com.hbm.blockentity.base;

import com.hbm.api.energy.IEnergyContainer;
import com.hbm.api.energy.IEnergyHandler;
import com.hbm.api.fluid.IExtendedFluidTank;
import com.hbm.api.fluid.ISidedFluidHandler;
import com.hbm.api.inventory.ISidedItemHandler;
import com.hbm.blockentity.ModBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TileProxyCombo extends TileProxyBase implements ISidedItemHandler, ISidedFluidHandler, IEnergyHandler,
        WorldlyContainer, Nameable {
    public BlockEntity tile;
    private TileProxyCombo(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
//        this.lookTooltip = Component.literal("proxy block");
    }

    public TileProxyCombo(BlockPos pos, BlockState blockState) {
        this(ModBlockEntityType.PROXY_ENTITY.get(), pos, blockState);
    }

    @Override
    public BlockEntity getBlockEntity() {
        if (tile != null || (tile = super.getBlockEntity()) != null)
            return tile;
        else
            return null;
    }

    @Override
    public void onContentsChanged() {

    }

    @Override
    public IEnergyContainer getEnergyContainer() {
        return null;
    }

    @Override
    public List<IExtendedFluidTank> getFluidTanks(@Nullable Direction side) {
        return null;
    }

    // =================Container===================
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
    public int getContainerSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return null;
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return null;
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {

    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return false;
    }

    @Override
    public void clearContent() {

    }

    @Override
    public Component getName() {
        if (getBlockEntity() != null && getBlockEntity() instanceof Nameable nameEntity){
            return nameEntity.getName();
        }else return Component.empty();
    }


}
