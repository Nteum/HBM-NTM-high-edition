package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.api.Mode;
import com.hbm.api.fluid.*;
import com.hbm.block.machine.BlockFluidBarrel;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.BaseMachineBlockEntity;
import com.hbm.gui.menu.BarrelMenu;
import com.hbm.gui.menu.IPacketUpdate;
import com.hbm.utils.DirectionUtils;
import com.hbm.utils.EnumUtils;
import com.hbm.utils.InventoryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BarrelEntity extends BaseMachineBlockEntity implements IPacketUpdate, MenuProvider {
    BlockFluidBarrel.BarrelProperties properties;
//    BarrelMode mode = BarrelMode.FORBID;
    BasicFluidTank tank;
    private SingleFluidHandler fluidHandler;
//    public ISidedFluidHandler fluidHandler;
    public int[] slotIn = new int[]{0,2};
    public int[] slotOut = new int[]{1,3};
    private ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> fluidHandler.getMode().ordinal();
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex){
                case 0 -> fluidHandler.setMode(Mode.values()[pValue]);
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };
    public BarrelEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.BARREL_ENTITY.get(), pPos, pBlockState);
        this.items = NonNullList.withSize(4,ItemStack.EMPTY);
        this.properties = ((BlockFluidBarrel)pBlockState.getBlock()).barrelProperties;
        this.fluidHandler = new SingleFluidHandler(properties.capacity, Mode.OUTPUT);
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this.fluidHandler);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        // 1. 处理界面内的流体物品
        InventoryUtils.handleItems(this, itemStack -> this.fluidHandler.drainItem(itemStack), 0, 1);
        InventoryUtils.handleItems(this, itemStack -> this.fluidHandler.fillItem(itemStack), 2, 3);
//        IFluidHandler fluidHandler = this.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
//        ItemStack itemStack0 = this.items.get(0);
//        ItemStack itemStack1 = this.items.get(1);
//        if (allowContainerTrans(itemStack0,itemStack1)){
//            if (itemStack0.getItem() instanceof BucketItem bucketItem){
//                int filled = fluidHandler.fill(new FluidStack(bucketItem.getFluid(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
//                if (filled == FluidType.BUCKET_VOLUME){
//                    fluidHandler.fill(new FluidStack(bucketItem.getFluid(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
//                    itemStack0.shrink(1);
//                    items.set(0,itemStack0.isEmpty() ? ItemStack.EMPTY : itemStack0);
//                    if (itemStack1.isEmpty())itemStack1 = new ItemStack(Items.BUCKET);
//                    else itemStack1.grow(1);
//                    items.set(1,itemStack1);
//                }
//            }
//        }
//        ItemStack itemStack2 = this.items.get(2);
//        ItemStack itemStack3 = this.items.get(3);
//        if (allowContainerTrans(itemStack2,itemStack3)){
//            if (itemStack2.getItem() instanceof BucketItem && itemStack3.isEmpty()){
//                FluidStack fluidStack = fluidHandler.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
//                if (fluidStack.getAmount() == FluidType.BUCKET_VOLUME){
//                    fluidHandler.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
//                    itemStack2.shrink(1);
//                    items.set(2,itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2);
//                    items.set(3,new ItemStack(fluidStack.getFluid().getBucket()));
//                }
//            }
//        }
        // 2. 处理界面上的流体，临界物品如果可以接收液体则接收液体
        BlockEntity neighbour;
        if (this.fluidHandler.allowOutput(0)){
            for (Direction direction : EnumUtils.DIRECTIONS) {
                if (this.hasLevel() && (neighbour = this.getLevel().getBlockEntity(this.getBlockPos().relative(direction))) != null){
                    neighbour.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).ifPresent(handler -> {
                        int maxDrain = this.fluidHandler.getFluidInTank(0).getAmount() / 10;
                        FluidStack fluidStack = this.fluidHandler.drain(maxDrain, IFluidHandler.FluidAction.SIMULATE);
                        int filled = handler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                        this.fluidHandler.drain(fluidStack.getAmount() - filled, IFluidHandler.FluidAction.EXECUTE);
                    });
                }
            }
        }

        sendUpdatePacket();
    }

    boolean allowContainerTrans(ItemStack item1, ItemStack item2){
        if (item2.isEmpty())return true;
        else if (item1.isEmpty() || item2.getCount()==item2.getMaxStackSize())return false;
        else if (item1.getItem() instanceof BucketItem && item2.is(Items.BUCKET))return true;
        else return true;
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
//        compoundTag.putInt("containerId",this.containerId);
        compoundTag.put(HBMKey.FLUIDS, this.fluidHandler.serializeNBT());
        return super.getReducedUpdateTag().merge(compoundTag);
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
//        this.containerId = tag.getInt("containerId");
//        this.tank.readFromNBT(tag);
        this.fluidHandler.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
    }

    public List<FluidTank> getFluidTanks(){
        return this.fluidHandler.getFluidTanks();
    }
    //=============
    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return pSide == Direction.DOWN ? slotOut : slotIn;
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        if (pDirection == null) return true;
        return (pIndex == 0 || pIndex == 2)  && pDirection != Direction.DOWN ;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        if (pDirection == null) return true;
        return (pIndex == 1 || pIndex == 3) && pDirection == Direction.DOWN;
    }
    //================
    @Override
    public Component getDefaultName() {
        return Component.translatable(HBMLang.BARREL.getTranslationKey());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory) {
        return new BarrelMenu(pContainerId,pPlayerInventory,this,this.containerData);
    }

    public int getRate(){
        return Integer.MAX_VALUE;
    }
}
