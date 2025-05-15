//package com.hbm.blockentity.base;
//
//import com.hbm.api.multiblock.mek.IMultiblock;
//import com.hbm.api.multiblock.mek.MultiblockData;
//import com.hbm.api.multiblock.mek.Structure;
//import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.block.entity.BlockEntityType;
//import net.minecraft.world.level.block.state.BlockState;
//
//import java.util.UUID;
//
//public abstract class MultiblockBlockEntity<T extends MultiblockData> extends HBMBlockEntity implements IMultiblock<T> {
//    private Structure structure = Structure.INVALID;
//
//    private final T defaultMultiblock = createMultiblock();
//    /**
//     * Whether this multiblock segment is rendering the structure.
//     */
//    private boolean isMaster;
//    // start at 100 to make sure we run the animation
//    private long unformedTicks = 100;
//    public MultiblockBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
//        super(type, pos, state);
//        cacheCoord();
//    }
//    @Override
//    public void setStructure(Structure structure) {
//        this.structure = structure;
//    }
//
//    @Override
//    public Structure getStructure() {
//        return structure;
//    }
//
//    @Override
//    public T getDefaultData() {
//        return defaultMultiblock;
//    }
//    @Override
//    protected void onUpdateClient() {
//        super.onUpdateClient();
//        if (!getMultiblock().isFormed()) {
//            unformedTicks++;
//        } else {
//            unformedTicks = 0;
//        }
//    }
//
//    @Override
//    protected void onUpdateServer() {
//        super.onUpdateServer();
//        boolean needsPacket = false;
//        if (ticker >= 3) {
//            structure.tick(this, ticker % 10 == 0);
//        }
//        T multiblock = getMultiblock();
//        if (isMaster() && multiblock.isFormed() && multiblock.recheckStructure) {
//            multiblock.recheckStructure = false;
//            getStructure().doImmediateUpdate(this, ticker % 10 == 0);
//            multiblock = getMultiblock();
//        }
//        if (multiblock.isFormed()) {
//            if (!prevStructure) {
//                structureChanged(multiblock);
//                prevStructure = true;
//                needsPacket = true;
//            }
//            if (multiblock.inventoryID != null) {
//                UUID oldCachedID = cachedID;
//                cachedID = multiblock.inventoryID;
//                if (oldCachedID != cachedID) {
//                    markForSave();
//                }
//                if (isMaster()) {
//                    if (multiblock.tick(level)) {
//                        needsPacket = true;
//                    }
//                    getManager().handleDirtyMultiblock(multiblock);
//                }
//            }
//        } else {
//            playersUsing.forEach(Player::closeContainer);
//            if (prevStructure) {
//                structureChanged(multiblock);
//                prevStructure = false;
//                needsPacket = true;
//            }
//            isMaster = false;
//        }
//        needsPacket |= onUpdateServer(multiblock);
//        if (needsPacket) {
//            sendUpdatePacket();
//        }
//    }
//}
