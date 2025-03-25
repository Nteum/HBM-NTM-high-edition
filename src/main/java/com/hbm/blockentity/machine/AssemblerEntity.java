package com.hbm.blockentity.machine;

import com.hbm.api.multiblock.BedLikeData;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.capabilities.Capabilities;
import com.hbm.capabilities.energy.BasicEnergyContainer;
import com.hbm.gui.menu.AssemblerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

public class AssemblerEntity extends BaseMachineBlockEntity {
    private final Tuple<BasicEnergyContainer,LazyOptional<BasicEnergyContainer>> energyCap;
//    private final BasicEnergyContainer ENERGY_STORAGE;
//    private LazyOptional<IEnergyContainer> lazyEnergyHandler = LazyOptional.empty();
    public int progress;
    static final BedLikeData bedLikeData = new BedLikeData()
            .putCapability(Capabilities.ENERGY,new Vec3i(-1,-1,-1),Direction.SOUTH)
            .putCapability(Capabilities.ENERGY,new Vec3i(0,-1,-1),Direction.SOUTH)
            .putCapability(Capabilities.ENERGY,new Vec3i(-1,-1,2),Direction.NORTH)
            .putCapability(Capabilities.ENERGY,new Vec3i(0,-1,2),Direction.NORTH);
    protected final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> progress;
                case 1 -> energyCap.getA().getEnergyRate();
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
        }

        @Override
        public int getCount() {
            return 2;
        }
    };
    public AssemblerEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.ASSEMBLER_ENTITY.get(), pPos, pBlockState);
        items = NonNullList.withSize(10,ItemStack.EMPTY);
//        ENERGY_STORAGE = new BasicEnergyContainer(100_000);
        energyCap = new Tuple<>(new BasicEnergyContainer(100_000),LazyOptional.empty());

    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == Capabilities.ENERGY){
            return energyCap.getB().cast();
        }
        return super.getCapability(cap, side);
    }
    public static void tick(Level level, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {

    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("hbm.machine.assembler");
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new AssemblerMenu(pContainerId,pInventory,this,containerData);
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
    public void onLoad() {
        super.onLoad();
//        lazyEnergyHandler = LazyOptional.of(()->ENERGY_STORAGE);
        energyCap.setB(LazyOptional.of(energyCap::getA));
    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
//        lazyEnergyHandler.invalidate();
        energyCap.getB().invalidate();
    }
}
