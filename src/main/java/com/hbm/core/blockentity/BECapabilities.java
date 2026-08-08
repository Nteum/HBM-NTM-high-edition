package com.hbm.core.blockentity;

import com.hbm.HBMKey;
import com.hbm.core.api.HBMEnergyHandler;
import com.hbm.core.capability.energy.BasicEnergyHandler;
import com.hbm.core.capability.energy.SidedEnergyWrapper;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.capability.item.SidedItemWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

public class BECapabilities extends BEUpdateable {
    protected final Map<Capability<?>, Pair<Function<Direction, LazyOptional<?>>, LazyOptional<?>[]>> lazyMap = new IdentityHashMap<>();
//    private static final Pair<Function<Direction, LazyOptional<?>>, LazyOptional<?>[]> PAIR_EMPTY = Pair.of(dir -> LazyOptional.empty(), new LazyOptional<?>[7]);

    protected MachineItemHandler items;
    protected HBMEnergyHandler energyContainer;
    protected IFluidHandler fluidHandler;

    public BECapabilities(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * 添加能力
     */
    public <T>void addCapability(Capability<T> capability, T handler, Direction ... directions){
        addCapability(capability, (Function<Direction, LazyOptional<?>>) dir -> LazyOptional.of(() -> handler), directions);
    }

    public <T>void addCapability(Capability<T> capability, Function<Direction, LazyOptional<?>> factory){
        lazyMap.put(capability, Pair.of(factory, new LazyOptional<?>[7]));
    }

    public <T>void addCapability(Capability<T> capability, Function<Direction, LazyOptional<?>> factory, Direction ... directions){
        Function<Direction, LazyOptional<?>> finalFactory = factory;
        if (directions.length > 0) {
            Set<Direction> dirSet = Set.of(directions);
            finalFactory = dir -> dirSet.contains(dir) ? factory.apply(dir) : LazyOptional.empty();
        }
        addCapability(capability, finalFactory);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (!lazyMap.containsKey(cap)) return LazyOptional.empty();
        else {
            Pair<Function<Direction, LazyOptional<?>>, LazyOptional<?>[]> pair = lazyMap.get(cap);
            int idx = side == null ? 6 : side.ordinal();
            LazyOptional<?> lazyOptional = pair.getRight()[idx];
            if (lazyOptional == null) lazyOptional = pair.getRight()[idx] = pair.getLeft().apply(side);
            return lazyOptional.cast();
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (var pair : lazyMap.values())
            for (LazyOptional<?> lo : pair.getRight())
                if (lo != null) lo.invalidate();
    }

    /**
     * 具体能力类型的管理
     */
    // 物品
    protected ItemStackHandler createItemHandler(int slotNum){
        return new ItemStackHandler(slotNum){
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }
    // 暂时先默认所有的槽位都是可入可出的，后面再继承
    protected LazyOptional<IItemHandler> getItemHandler(Direction side) {
        return LazyOptional.of(() -> new SidedItemWrapper(items, side, IntStream.range(0, items.getSlots()).toArray(), IntStream.range(0, items.getSlots()).toArray()));
    }
    // 能量
    protected HBMEnergyHandler createEnergyHandler(long capacity, int IOMode){
        return new BasicEnergyHandler(capacity){
            @Override
            public void onContentsChanged() {
                setChanged();
            }
        }.setIO(IOMode);
    }
    protected LazyOptional<HBMEnergyHandler> getEnergyHandler(Direction side) {
        return LazyOptional.of(() -> new SidedEnergyWrapper(energyContainer, side, BasicEnergyHandler.BOTH));
    }
    // 流体，单一流体槽只需FluidTank即可。
    protected IFluidHandler createFluidHandler(int tankNum, int capacity){
        return tankNum > 1 ? new BasicFluidHandler(tankNum, capacity){
            @Override
            public void onContentsChanged() {
                setChanged();
            }
        } : new FluidTank(capacity){
            @Override
            protected void onContentsChanged() {
                setChanged();
            }
        };
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (this.items != null)
            tag.put(HBMKey.ITEM, this.items.serializeNBT());
        if (this.energyContainer != null)
            tag.put(HBMKey.ENERGY, this.energyContainer.serializeNBT());
        if (this.fluidHandler != null && fluidHandler instanceof INBTSerializable<?> serializable)
            tag.put(HBMKey.FLUID, serializable.serializeNBT());
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (items != null && tag.contains(HBMKey.ITEM, Tag.TAG_COMPOUND))
            this.items.deserializeNBT(tag.getCompound(HBMKey.ITEM));
        if (this.energyContainer != null && tag.contains(HBMKey.ENERGY, Tag.TAG_COMPOUND))
            this.energyContainer.deserializeNBT(tag.getCompound(HBMKey.ENERGY));
        if (this.fluidHandler != null && fluidHandler instanceof INBTSerializable serializable && tag.contains(HBMKey.FLUID, Tag.TAG_COMPOUND))
            serializable.deserializeNBT(tag.getCompound(HBMKey.FLUID));
    }
}
