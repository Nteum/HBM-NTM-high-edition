package com.hbm.blockentity.machine;

import com.hbm.HBMLang;
import com.hbm.api.Mode;
import com.hbm.api.fluid.*;
import com.hbm.block.machine.BlockFluidBarrel;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.BaseMachineBlockEntity;
import com.hbm.gui.menu.BarrelMenu;
import com.hbm.gui.menu.IPacketUpdate;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BarrelEntity extends BaseMachineBlockEntity implements IPacketUpdate, MenuProvider {
    BlockFluidBarrel.BarrelProperties properties;
    BarrelMode mode = BarrelMode.FORBID;
    BasicFluidTank tank;
    private SingleFluidHandler fluidHandler;
//    public ISidedFluidHandler fluidHandler;
    private ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex){
                case 0 -> mode.ordinal();
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex){
                case 0 -> mode = BarrelMode.values()[pValue];
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
//        this.tank = new BasicFluidTank(properties.capacity);
        this.fluidHandler = new SingleFluidHandler(properties.capacity, Mode.OUTPUT);
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this.fluidHandler);
//        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, new BaseFluidHandler(1, properties.capacity));
//        capabilitiesCache.addCapabilityResolver(new SidedFluidWrapper(new BaseFluidHandler(1, properties.capacity)));
    }

//    @Override
//    public List<IExtendedFluidTank> getFluidTanks(@Nullable Direction side) {
//        return List.of(this.tank);
//    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        // 1. 处理界面内的流体物品
        IFluidHandler fluidHandler = this.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
        ItemStack itemStack0 = this.items.get(0);
        ItemStack itemStack1 = this.items.get(1);
        if (allowContainerTrans(itemStack0,itemStack1)){
            if (itemStack0.getItem() instanceof BucketItem bucketItem){
                int filled = fluidHandler.fill(new FluidStack(bucketItem.getFluid(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
                if (filled == FluidType.BUCKET_VOLUME){
                    fluidHandler.fill(new FluidStack(bucketItem.getFluid(), FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
                    itemStack0.shrink(1);
                    items.set(0,itemStack0.isEmpty() ? ItemStack.EMPTY : itemStack0);
                    if (itemStack1.isEmpty())itemStack1 = new ItemStack(Items.BUCKET);
                    else itemStack1.grow(1);
                    items.set(1,itemStack1);
                }
            }
        }
        ItemStack itemStack2 = this.items.get(2);
        ItemStack itemStack3 = this.items.get(3);
        if (allowContainerTrans(itemStack2,itemStack3)){
            if (itemStack2.getItem() instanceof BucketItem && itemStack3.isEmpty()){
                FluidStack fluidStack = fluidHandler.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
                if (fluidStack.getAmount() == FluidType.BUCKET_VOLUME){
                    fluidHandler.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
                    itemStack2.shrink(1);
                    items.set(2,itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2);
                    items.set(3,new ItemStack(fluidStack.getFluid().getBucket()));
                }
            }
        }
        // 2. 处理界面上的流体

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
        compoundTag.putInt("containerId",this.containerId);
        getCapability(ForgeCapabilities.FLUID_HANDLER,null).ifPresent(cap -> {
            cap.getFluidInTank(0).writeToNBT(compoundTag);
        });
        return super.getReducedUpdateTag().merge(compoundTag);
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        this.containerId = tag.getInt("containerId");
//        this.capabilitiesCache.getCapability(ForgeCapabilities.FLUID_HANDLER,null).ifPresent(cap -> {
//            ((BaseFluidHandler)cap).setTankByNbt(0,tag);
//        });
        this.tank.readFromNBT(tag);
        //通过这种方式将内容更新的menu中，我们期望这段运行在客户端
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player.containerMenu != null && player.containerMenu.containerId == this.containerId) {
            ((BarrelMenu)player.containerMenu).container = this;
        }
    }

    //=============
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
    //================
    @Override
    public Component getDefaultName() {
        return Component.translatable(HBMLang.BARREL.getTranslationKey());
    }
    public int containerId;

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory) {
        this.containerId = pContainerId;
        return new BarrelMenu(pContainerId,pPlayerInventory,this,this.containerData);
    }

    public static enum BarrelMode{IN,INOUT,OUT,FORBID}
//    public int getCapacity(){
//        return properties.capacity;
//    }
    public boolean isCreative(){
        return properties.isCreative;
    }
    public int getRate(){
        return Integer.MAX_VALUE;
    }
    public boolean getActive(){
        return true;
    }
}
