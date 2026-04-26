package com.hbm.blockentity.base;

import com.hbm.capabilities.network.TransmitterNetworkRegistry;
import com.hbm.api.providers.IBlockProvider;
import com.hbm.block.interfaces.IHasTileEntity;
import com.hbm.capabilities.network.ConnType;
import com.hbm.block.states.TransmitterType;
import com.hbm.capabilities.network.transmitter.Transmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TransmitterBlockEntity extends CapabilityBlockEntity {
//    public static final ModelProperty<TransmitterModelData> TRANSMITTER_PROPERTY = new ModelProperty<>();

    private final Transmitter<?, ?, ?> transmitter;
    private boolean forceUpdate = true;
    private boolean loaded = false;
    public TransmitterBlockEntity(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(((IHasTileEntity<? extends TransmitterBlockEntity>) blockProvider.getBlock()).getTileType(), pos, state);
        this.transmitter = createTransmitter(blockProvider);
        cacheCoord();
//        addCapabilityResolver(BasicCapabilityResolver.constant(Capabilities.ALLOY_INTERACTION, this));
//        addCapabilityResolver(new BasicSidedCapabilityResolver<>(this, Capabilities.CONFIGURABLE, ProxyConfigurable::new));
    }
    protected abstract Transmitter<?, ?, ?> createTransmitter(IBlockProvider blockProvider);

    public Transmitter<?, ?, ?> getTransmitter() {
        return transmitter;
    }

    public void setForceUpdate() {
        forceUpdate = true;
    }

    public abstract TransmitterType getTransmitterType();

    protected void onUpdateServer() {
        if (forceUpdate) {
            getTransmitter().refreshConnections();
            forceUpdate = false;
        }
    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, TransmitterBlockEntity transmitter) {
        transmitter.onUpdateServer();
    }

    @NotNull
    @Override
    public CompoundTag getReducedUpdateTag() {
        return getTransmitter().getReducedUpdateTag(super.getReducedUpdateTag());
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag) {
        super.handleUpdateTag(tag);
        getTransmitter().handleUpdateTag(tag);
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        //Delay requesting the model data update and actually updating the packet until we have finished parsing the update tag
        updateModelData();
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        getTransmitter().read(nbt);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbtTags) {
        super.saveAdditional(nbtTags);
        getTransmitter().write(nbtTags);
    }

    public void onNeighborTileChange(Direction side) {
        getTransmitter().onNeighborTileChange(side);
    }

    public void onNeighborBlockChange(Direction side) {
        getTransmitter().onNeighborBlockChange(side);
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        onWorldJoin(false);
    }

    @Override
    public void onChunkUnloaded() {
        if (!isRemote()) {
            //Only take the transmitter's share if it was unloaded and not if we are being removed
            getTransmitter().takeShare();
        }
        super.onChunkUnloaded();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        onWorldSeparate(false);
        getTransmitter().remove();
    }

    public void onAdded() {
        onWorldJoin(false);
        getTransmitter().refreshConnections();
    }

    private void onWorldJoin(boolean wasPresent) {
        if (!isRemote() && !wasPresent) {
            //If we weren't already present, and we are on the server, track this transmitter
            TransmitterNetworkRegistry.trackTransmitter(getTransmitter());
        }
        if (!loaded) {
            //Only load it if it wasn't already loaded
            loaded = true;
            if (!isRemote()) {
                TransmitterNetworkRegistry.registerOrphanTransmitter(getTransmitter());
            }
        }
    }

    private void onWorldSeparate(boolean stillPresent) {
        if (!isRemote() && !stillPresent) {
            //If we aren't still present, and we are on the server, stop tracking this transmitter
            TransmitterNetworkRegistry.untrackTransmitter(getTransmitter());
        }
        if (loaded) {
            //Only unload it if it was actually loaded
            loaded = false;
            if (isRemote()) {
                getTransmitter().setTransmitterNetwork(null);
            } else {
                TransmitterNetworkRegistry.invalidateTransmitter(getTransmitter());
            }
        }
    }

    public void chunkAccessibilityChange(boolean loaded) {
        if (loaded) {
            //Chunk went from "unloaded" to loaded
            onWorldJoin(true);
        } else {
            //Chunk went from loaded to "unloaded", need to take the share first like normally happens when it unloads
            getTransmitter().validateAndTakeShare();
            onWorldSeparate(true);
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

//    public Direction getSideLookingAt(Player player, Direction fallback) {
//        Direction side = getSideLookingAt(player);
//        return side == null ? fallback : side;
//    }
//    //
//    @Nullable
//    public Direction getSideLookingAt(Player player) {
//        MultipartUtils.AdvancedRayTraceResult result = MultipartUtils.collisionRayTrace(player, getBlockPos(), getCollisionBoxes());
//        if (result != null && result.valid()) {
//            List<Direction> list = new ArrayList<>(EnumUtils.DIRECTIONS.length);
//            byte connections = getTransmitter().getAllCurrentConnections();
//            for (Direction dir : EnumUtils.DIRECTIONS) {
//                if (Transmitter.connectionMapContainsSide(connections, dir)) {
//                    list.add(dir);
//                }
//            }
//            int boxIndex = result.subHit + 1;
//            if (boxIndex < list.size()) {
//                return list.get(boxIndex);
//            }
//        }
//        return null;
//    }

//    @NotNull
//    @Override
//    public InteractionResult onSneakRightClick(@NotNull Player player, @NotNull Direction side) {
//        if (!isRemote()) {
//            Direction hitSide = getSideLookingAt(player);
//            if (hitSide == null) {
//                if (transmitter.getConnTypeRaw(side) != ConnType.FORBID) {
//                    InteractionResult result = onConfigure(player, side);
//                    if (result.consumesAction()) {
//                        //Refresh/notify so that we actually update the block and how it can connect given color or things might have changed
//                        getTransmitter().refreshConnections();
//                        getTransmitter().notifyTileChange();
//                        return result;
//                    }
//                }
//                hitSide = side;
//            }
//            transmitter.setConnTypeRaw(hitSide, transmitter.getConnTypeRaw(hitSide).getNext());
//            //Note: This stuff happens here and not in sideChanged because we don't want it to happen on load
//            // or things which also would cause sideChanged to be called
//            getTransmitter().onModeChange(Direction.from3DDataValue(hitSide.ordinal()));
//            getTransmitter().refreshConnections();
//            getTransmitter().notifyTileChange();
//            player.displayClientMessage(MekanismLang.CONNECTION_TYPE.translateColored(EnumColor.GRAY, transmitter.getConnTypeRaw(hitSide)), true);
//            sendUpdatePacket();
//        }
//        return InteractionResult.SUCCESS;
//    }

    protected InteractionResult onConfigure(Player player, Direction side) {
        //TODO: Move some of this stuff back into the tiles?
        return getTransmitter().onConfigure(player, side);
    }

//    @NotNull
//    @Override
//    public InteractionResult onRightClick(@NotNull Player player, @NotNull Direction side) {
//        return getTransmitter().onRightClick(player, side);
//    }

//    public List<VoxelShape> getCollisionBoxes() {
//        List<VoxelShape> list = new ArrayList<>();
//        boolean isSmall = getTransmitterType().getSize() == TransmitterType.Size.SMALL;
//        for (Direction side : EnumUtils.DIRECTIONS) {
//            ConnType connectionType = getTransmitter().getConnType(side);
//            if (connectionType != ConnType.FORBID) {
//                if (isSmall) {
//                    list.add(BlockSmallTransmitter.getSideForType(connectionType, side));
//                } else {
//                    list.add(BlockLargeTransmitter.getSideForType(connectionType, side));
//                }
//            }
//        }
//        //Center position
//        list.add(isSmall ? BlockSmallTransmitter.CENTER : BlockLargeTransmitter.CENTER);
//        return list;
//    }

    @NotNull
    @Override
    public AABB getRenderBoundingBox() {
        //If any of the block is in view, then allow rendering the contents
        return new AABB(worldPosition, worldPosition.offset(1, 1, 1));
    }

//    @NotNull
//    @Override
//    public ModelData getModelData() {
//        TransmitterModelData data = initModelData();
//        updateModelData(data);
//        return ModelData.builder().with(TRANSMITTER_PROPERTY, data).build();
//    }
//
//    protected void updateModelData(TransmitterModelData modelData) {
//        //Update the data, using information about if there is actually a connection on a given side
//        for (Direction side : EnumUtils.DIRECTIONS) {
//            modelData.setConnectionData(side, getTransmitter().getConnType(side));
//        }
//    }
//
//    @NotNull
//    protected TransmitterModelData initModelData() {
//        return new TransmitterModelData();
//    }

//    @Override
//    public void onAlloyInteraction(Player player, ItemStack stack, @NotNull AlloyTier tier) {
//        if (getLevel() != null && getTransmitter().hasTransmitterNetwork()) {
//            DynamicNetwork<?, ?, ?> transmitterNetwork = getTransmitter().getTransmitterNetwork();
//            List<Transmitter<?, ?, ?>> list = new ArrayList<>(transmitterNetwork.getTransmitters());
//            list.sort((o1, o2) -> {
//                if (o1 != null && o2 != null) {
//                    return Double.compare(o1.getTilePos().distSqr(worldPosition), o2.getTilePos().distSqr(worldPosition));
//                }
//                return 0;
//            });
//            boolean sharesSet = false;
//            int upgraded = 0;
//            for (Transmitter<?, ?, ?> transmitter : list) {
//                if (transmitter instanceof IUpgradeableTransmitter<?> upgradeableTransmitter && upgradeableTransmitter.canUpgrade(tier)) {
//                    TransmitterBlockEntity transmitterTile = transmitter.getTransmitterTile();
//                    BlockState state = transmitterTile.getBlockState();
//                    BlockState upgradeState = transmitterTile.upgradeResult(state, tier.getBaseTier());
//                    if (state == upgradeState) {
//                        //Skip if it would not actually upgrade anything
//                        continue;
//                    }
//                    if (!sharesSet) {
//                        if (transmitterNetwork instanceof DynamicBufferedNetwork dynamicNetwork) {
//                            //Ensure we save the shares to the tiles so that they can properly take them, and they don't get voided
//                            dynamicNetwork.validateSaveShares((BufferedTransmitter<?, ?, ?, ?>) transmitter);
//                        }
//                        sharesSet = true;
//                    }
//                    transmitter.startUpgrading();
//                    TransmitterUpgradeData upgradeData = upgradeableTransmitter.getUpgradeData();
//                    BlockPos transmitterPos = transmitter.getTilePos();
//                    Level transmitterWorld = transmitter.getTileWorld();
//                    if (upgradeData == null) {
//                        HBM.LOGGER.warn("Got no upgrade data for transmitter at position: {} in {} but it said it would be able to provide some.",
//                                transmitterPos, transmitterWorld);
//                    } else {
//                        transmitterWorld.setBlockAndUpdate(transmitterPos, upgradeState);
//                        TransmitterBlockEntity upgradedTile = WorldUtils.getTileEntity(TransmitterBlockEntity.class, transmitterWorld, transmitterPos);
//                        if (upgradedTile == null) {
//                            HBM.LOGGER.warn("Error upgrading transmitter at position: {} in {}.", transmitterPos, transmitterWorld);
//                        } else {
//                            Transmitter<?, ?, ?> upgradedTransmitter = upgradedTile.getTransmitter();
//                            if (upgradedTransmitter instanceof IUpgradeableTransmitter) {
//                                transferUpgradeData((IUpgradeableTransmitter<?>) upgradedTransmitter, upgradeData);
//                            } else {
//                                HBM.LOGGER.warn("Unhandled upgrade data.", new IllegalStateException());
//                            }
//                            upgraded++;
//                            if (upgraded == 8) {
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//            if (upgraded > 0) {
//                //Invalidate the network so that it properly has new references to everything
//                transmitterNetwork.invalidate(null);
//                if (!player.isCreative()) {
//                    stack.shrink(1);
//                }
//                if (player instanceof ServerPlayer serverPlayer) {
//                    MekanismCriteriaTriggers.ALLOY_UPGRADE.trigger(serverPlayer);
//                }
//            }
//        }
//    }

//    private <DATA extends TransmitterUpgradeData> void transferUpgradeData(IUpgradeableTransmitter<DATA> upgradeableTransmitter, TransmitterUpgradeData data) {
//        if (upgradeableTransmitter.dataTypeMatches(data)) {
//            upgradeableTransmitter.parseUpgradeData((DATA) data);
//        } else {
//            HBM.LOGGER.warn("Unhandled upgrade data.", new IllegalStateException());
//        }
//    }

//    @NotNull
//    protected BlockState upgradeResult(@NotNull BlockState current, @NotNull BaseTier tier) {
//        return current;
//    }

    public void sideChanged(@NotNull Direction side, @NotNull ConnType old, @NotNull ConnType type) {
    }

    /**
     * Called if the transmitter handles redstone and the redstone activity state has changed.
     */
    public void redstoneChanged(boolean powered) {
    }

    protected InteractPredicate getExtractPredicate() {
        return (tank, side) -> {
            if (side == null) {
                //Note: We return true here, but extraction isn't actually allowed and gets blocked by the read only handler
                return true;
            }
            //If we have a side only allow extracting if our connection allows it
            ConnType connectionType = getTransmitter().getConnType(side);
            return connectionType == ConnType.NORMAL || connectionType == ConnType.OUT;
        };
    }

    protected InteractPredicate getInsertPredicate() {
        return (tank, side) -> {
            if (side == null) {
                //Note: We return true here, but insertion isn't actually allowed and gets blocked by the read only handler
                return true;
            }
            //If we have a side only allow inserting if our connection allows it
            ConnType connectionType = getTransmitter().getConnType(side);
            return connectionType == ConnType.NORMAL || connectionType == ConnType.IN;
        };
    }

    @FunctionalInterface
    public interface InteractPredicate {

        InteractPredicate ALWAYS_TRUE = (tank, side) -> true;

        boolean test(int tank, @Nullable Direction side);
    }
}
