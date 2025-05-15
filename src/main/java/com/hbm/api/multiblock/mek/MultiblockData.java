//package com.hbm.api.multiblock.mek;
//
//import com.hbm.api.IContentsListener;
//import com.hbm.api.enums.AutomationType;
//import com.hbm.api.fluid.mek.IExtendedFluidTank;
//import com.hbm.api.fluid.mek.IMekanismFluidHandler;
//import com.hbm.api.math.voxel.VoxelCuboid;
//import com.hbm.api.multiblock.HBMMultiData;
//import com.hbm.utils.WorldUtils;
//import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.NbtUtils;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//import com.hbm.api.multiblock.mek.IValveHandler.ValveData;
//
//import java.util.*;
//import java.util.function.BiPredicate;
//import java.util.function.BooleanSupplier;
//import java.util.function.Supplier;
///**
// * 多方块结构的必要数据，主要参考的mek相关代码
// *
// * */
//public class MultiblockData extends HBMMultiData implements IMekanismFluidHandler {
//    private boolean formed;
//    private int currentRedstoneLevel;
//
//    private final BooleanSupplier remoteSupplier;
//    private final Supplier<Level> worldSupplier;
//    private final BiPredicate<Object, @NotNull AutomationType> formedBiPred = (t, automationType) -> isFormed();
//    private final BiPredicate<Object, @NotNull AutomationType> notExternalFormedBiPred = (t, automationType) -> automationType != AutomationType.EXTERNAL && isFormed();
//
//    public MultiblockData(BlockEntity tile) {
//        remoteSupplier = () -> tile.getLevel().isClientSide();
//        worldSupplier = tile::getLevel;
//    }
//
//    @SuppressWarnings("unchecked")
//    public <T> BiPredicate<T, @NotNull AutomationType> formedBiPred() {
//        return (BiPredicate<T, @NotNull AutomationType>) formedBiPred;
//    }
//
//    @SuppressWarnings("unchecked")
//    public <T> BiPredicate<T, @NotNull AutomationType> notExternalFormedBiPred() {
//        return (BiPredicate<T, @NotNull AutomationType>) notExternalFormedBiPred;
//    }
//
//    public boolean isFormed() {
//        return formed;
//    }
//    protected boolean isRemote() {
//        return remoteSupplier.getAsBoolean();
//    }
//
//    protected Level getWorld() {
//        return worldSupplier.get();
//    }
//    protected IContentsListener createSaveAndComparator() {
//        return createSaveAndComparator(this);
//    }
//
//    protected IContentsListener createSaveAndComparator(IContentsListener contentsListener) {
//        return () -> {
//            contentsListener.onContentsChanged();
//            if (!isRemote()) {
//                markDirtyComparator(getWorld());
//            }
//        };
//    }
//
//    // Only call from the server
//    public void markDirtyComparator(Level world) {
//        if (!isFormed()) {
//            return;
//        }
//        int newRedstoneLevel = getMultiblockRedstoneLevel();
//        if (newRedstoneLevel != currentRedstoneLevel) {
//            //Update the comparator value if it changed
//            currentRedstoneLevel = newRedstoneLevel;
//            //And inform all the valves that the level they should be supplying changed
//            notifyAllUpdateComparator(world);
//        }
//    }
//    public void notifyAllUpdateComparator(Level world) {
//        for (ValveData valve : valves) {
//            TileEntityMultiblock<?> tile = WorldUtils.getTileEntity(TileEntityMultiblock.class, world, valve.location);
//            if (tile != null) {
//                tile.markDirtyComparator();
//            }
//        }
//    }
//
//    protected int getMultiblockRedstoneLevel() {
//        return 0;
//    }
//
//    public int getCurrentRedstoneLevel() {
//        return currentRedstoneLevel;
//    }
//}
