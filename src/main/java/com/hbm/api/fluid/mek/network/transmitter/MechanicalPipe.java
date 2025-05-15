//package com.hbm.api.fluid.mek.network.transmitter;
//
//import com.hbm.HBMKey;
//import com.hbm.api.enums.Action;
//import com.hbm.api.enums.AutomationType;
//import com.hbm.api.fluid.mek.IExtendedFluidTank;
//import com.hbm.api.fluid.mek.IMekanismFluidHandler;
//import com.hbm.api.fluid.mek.network.cache.AcceptorCache;
//import com.hbm.api.fluid.mek.network.network.FluidNetwork;
//import com.hbm.api.math.MathUtils;
//import com.hbm.api.providers.IBlockProvider;
//import com.hbm.block.states.ConnType;
//import com.hbm.block.states.TransmissionType;
//import com.hbm.blockentity.base.TransmitterBlockEntity;
//import com.hbm.capabilities.fluid.mek.BasicFluidTank;
//import com.hbm.utils.NBTUtils;
//import net.minecraft.core.Direction;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.Tag;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraftforge.common.capabilities.ForgeCapabilities;
//import net.minecraftforge.fluids.FluidStack;
//import net.minecraftforge.fluids.capability.IFluidHandler;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.*;
//
//public class MechanicalPipe extends BufferedTransmitter<IFluidHandler, FluidNetwork, FluidStack, MechanicalPipe> implements IMekanismFluidHandler {
//
//    @NotNull
//    public FluidStack saveShare = FluidStack.EMPTY;
//    private final List<IExtendedFluidTank> tanks;
//    public final BasicFluidTank buffer;
//
//    public MechanicalPipe(IBlockProvider blockProvider, TransmitterBlockEntity tile) {
//        super(tile, TransmissionType.FLUID);
//        //TODO: If we make fluids support longs then adjust this
//        buffer = BasicFluidTank.create(MathUtils.clampToInt(getCapacity()), BasicFluidTank.alwaysFalse, BasicFluidTank.alwaysTrue, this);
//        tanks = Collections.singletonList(buffer);
//    }
//
//    @Override
//    public AcceptorCache<IFluidHandler> getAcceptorCache() {
//        //Cast it here to make things a bit easier, as we know createAcceptorCache by default returns an object of type AcceptorCache
//        return (AcceptorCache<IFluidHandler>) super.getAcceptorCache();
//    }
//
//    @Override
//    public void pullFromAcceptors() {
//        Set<Direction> connections = getConnections(ConnType.IN);
//        if (!connections.isEmpty()) {
//            for (IFluidHandler connectedAcceptor : getAcceptorCache().getConnectedAcceptors(connections)) {
//                FluidStack received;
//                //Note: We recheck the buffer each time in case we ended up accepting fluid somewhere
//                // and our buffer changed and is no longer empty
//                FluidStack bufferWithFallback = getBufferWithFallback();
//                if (bufferWithFallback.isEmpty()) {
//                    //If we don't have a fluid stored try pulling as much as we are able to
//                    received = connectedAcceptor.drain(getAvailablePull(), IFluidHandler.FluidAction.SIMULATE);
//                } else {
//                    //Otherwise, try draining the same type of fluid we have stored requesting up to as much as we are able to pull
//                    // We do this to better support multiple tanks in case the fluid we have stored we could pull out of a block's
//                    // second tank but just asking to drain a specific amount
//                    received = connectedAcceptor.drain(new FluidStack(bufferWithFallback, getAvailablePull()), IFluidHandler.FluidAction.SIMULATE);
//                }
//                if (!received.isEmpty() && takeFluid(received, Action.SIMULATE).isEmpty()) {
//                    //If we received some fluid and are able to insert it all, then actually extract it and insert it into our thing.
//                    // Note: We extract first after simulating ourselves because if the target gave a faulty simulation value, we want to handle it properly
//                    // and not accidentally dupe anything, and we know our simulation we just performed on taking it is valid
//                    takeFluid(connectedAcceptor.drain(received.copy(), IFluidHandler.FluidAction.EXECUTE), Action.EXECUTE);
//                }
//            }
//        }
//    }
//
//    private int getAvailablePull() {
//        if (hasTransmitterNetwork()) {
////            return Math.min(tier.getPipePullAmount(), getTransmitterNetwork().fluidTank.getNeeded());
//            return getTransmitterNetwork().fluidTank.getNeeded();
//        }
////        return Math.min(tier.getPipePullAmount(), buffer.getNeeded());
//        return buffer.getNeeded();
//    }
//
//
//    @Override
//    public void read(@NotNull CompoundTag nbtTags) {
//        super.read(nbtTags);
//        if (nbtTags.contains(HBMKey.FLUID_STORED, Tag.TAG_COMPOUND)) {
//            saveShare = FluidStack.loadFluidStackFromNBT(nbtTags.getCompound(HBMKey.FLUID_STORED));
//        } else {
//            saveShare = FluidStack.EMPTY;
//        }
//        buffer.setStack(saveShare);
//    }
//
//    @NotNull
//    @Override
//    public CompoundTag write(@NotNull CompoundTag nbtTags) {
//        super.write(nbtTags);
//        if (hasTransmitterNetwork()) {
//            getTransmitterNetwork().validateSaveShares(this);
//        }
//        if (saveShare.isEmpty()) {
//            nbtTags.remove(HBMKey.FLUID_STORED);
//        } else {
//            nbtTags.put(HBMKey.FLUID_STORED, saveShare.writeToNBT(new CompoundTag()));
//        }
//        return nbtTags;
//    }
//
//    @Override
//    public boolean isValidAcceptor(BlockEntity tile, Direction side) {
//        return super.isValidAcceptor(tile, side) && getAcceptorCache().isAcceptorAndListen(tile, side, ForgeCapabilities.FLUID_HANDLER);
//    }
//
//    @Override
//    public CompatibleTransmitterValidator<IFluidHandler, FluidNetwork, MechanicalPipe> getNewOrphanValidator() {
//        return new CompatibleFluidTransmitterValidator(this);
//    }
//
//    @Override
//    public boolean isValidTransmitter(TransmitterBlockEntity transmitter, Direction side) {
//        if (super.isValidTransmitter(transmitter, side) && transmitter.getTransmitter() instanceof MechanicalPipe other) {
//            FluidStack buffer = getBufferWithFallback();
//            if (buffer.isEmpty() && hasTransmitterNetwork() && getTransmitterNetwork().getPrevTransferAmount() > 0) {
//                buffer = getTransmitterNetwork().lastFluid;
//            }
//            FluidStack otherBuffer = other.getBufferWithFallback();
//            if (otherBuffer.isEmpty() && other.hasTransmitterNetwork() && other.getTransmitterNetwork().getPrevTransferAmount() > 0) {
//                otherBuffer = other.getTransmitterNetwork().lastFluid;
//            }
//            return buffer.isEmpty() || otherBuffer.isEmpty() || buffer.isFluidEqual(otherBuffer);
//        }
//        return false;
//    }
//
//    @Override
//    public FluidNetwork createEmptyNetworkWithID(UUID networkID) {
//        return new FluidNetwork(networkID);
//    }
//
//    @Override
//    public FluidNetwork createNetworkByMerging(Collection<FluidNetwork> networks) {
//        return new FluidNetwork(networks);
//    }
//
//    @Override
//    protected boolean canHaveIncompatibleNetworks() {
//        return true;
//    }
//
//    @Override
//    public long getCapacity() {
//        return tier.getPipeCapacity();
//    }
//
//    @NotNull
//    @Override
//    public FluidStack releaseShare() {
//        FluidStack ret = buffer.getFluid();
//        buffer.setEmpty();
//        return ret;
//    }
//
//    @Override
//    public boolean noBufferOrFallback() {
//        return getBufferWithFallback().isEmpty();
//    }
//
//    @NotNull
//    @Override
//    public FluidStack getBufferWithFallback() {
//        FluidStack buffer = getShare();
//        //If we don't have a buffer try falling back to the network's buffer
//        if (buffer.isEmpty() && hasTransmitterNetwork()) {
//            return getTransmitterNetwork().getBuffer();
//        }
//        return buffer;
//    }
//
//    @NotNull
//    @Override
//    public FluidStack getShare() {
//        return buffer.getFluid();
//    }
//
//    @Override
//    public void takeShare() {
//        if (hasTransmitterNetwork()) {
//            FluidNetwork network = getTransmitterNetwork();
//            if (!network.fluidTank.isEmpty() && !saveShare.isEmpty()) {
//                int amount = saveShare.getAmount();
//                MekanismUtils.logMismatchedStackSize(network.fluidTank.shrinkStack(amount, Action.EXECUTE), amount);
//                buffer.setStack(saveShare);
//            }
//        }
//    }
//
//    @NotNull
//    @Override
//    public List<IExtendedFluidTank> getFluidTanks(@Nullable Direction side) {
//        if (hasTransmitterNetwork()) {
//            return getTransmitterNetwork().getFluidTanks(side);
//        }
//        return tanks;
//    }
//
//    @Override
//    public void onContentsChanged() {
//        getTransmitterTile().setChanged();
//    }
//
//    /**
//     * @return remainder
//     */
//    @NotNull
//    public FluidStack takeFluid(@NotNull FluidStack fluid, Action action) {
//        if (hasTransmitterNetwork()) {
//            return getTransmitterNetwork().fluidTank.insert(fluid, action, AutomationType.INTERNAL);
//        }
//        return buffer.insert(fluid, action, AutomationType.INTERNAL);
//    }
//
//    @Override
//    protected void handleContentsUpdateTag(@NotNull FluidNetwork network, @NotNull CompoundTag tag) {
//        super.handleContentsUpdateTag(network, tag);
//        NBTUtils.setFluidStackIfPresent(tag, HBMKey.FLUID_STORED, network::setLastFluid);
//        NBTUtils.setFloatIfPresent(tag, HBMKey.SCALE, scale -> network.currentScale = scale);
//    }
//}