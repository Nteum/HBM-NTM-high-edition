package com.hbm.blockentity.machine;

import com.hbm.HBMLang;
import com.hbm.api.energy.fe.SidedEnergyWrapper;
import com.hbm.api.fluid.BaseFluidHandler;
import com.hbm.api.fluid.SidedFluidWrapper;
import com.hbm.block.machine.BlockChemplant;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.blockentity.base.BedLikeBlockEntity;
import com.hbm.lib.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class ChemplantEntity extends BedLikeBlockEntity {
    protected final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> getProgress();
                case 1 -> getCapability(ForgeCapabilities.ENERGY).orElse(null).getEnergyStored();
                case 2 -> getCapability(ForgeCapabilities.ENERGY).orElse(null).getMaxEnergyStored();
                default -> 0;
            };
        }
        @Override
        public void set(int pIndex, int pValue) {

        }
        @Override
        public int getCount() {
            return 3;
        }
    };
    public ChemplantEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.CHEMPLANT_ENTITY.get(), pPos, pBlockState);
        this.items = NonNullList.withSize(21, ItemStack.EMPTY);
        this.capabilitiesCache.addCapabilityResolver(new SidedFluidWrapper(new BaseFluidHandler(4,24_000)));
    }


    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();

    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        Direction facing = this.getBlockState().getValue(BlockChemplant.FACING);
        Direction rot = DirectionUtils.horizRot(Direction.SOUTH, facing, Direction.EAST);
        double x = getBlockPos().getX() + 0.5 + facing.getStepX() * 1.125 + rot.getStepX() * 0.125;
        double y = getBlockPos().getY() + 3;
        double z = getBlockPos().getZ() + 0.5 + facing.getStepZ() * 1.125 + rot.getStepZ() * 0.125;
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.1, 0.0);
    }

    protected int getProgress(){return 0;}

    //===================wroldly container
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
    //===============
    @Override
    protected Component getDefaultName() {
        return Component.translatable(HBMLang.CHEMPLANT.getTranslationKey());
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;
    }
}
