package com.hbm.capabilities.holder;

import com.hbm.api.RelativeSide;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
// 基础的能力holder
public class BasicHolder<TYPE> implements IHolder {
    //面向每个方向的能力
    private final Map<RelativeSide, List<TYPE>> directionalSlots = new EnumMap<>(RelativeSide.class);
    //用于内部处理的能力
    private final List<TYPE> inventorySlots = new ArrayList<>();
    protected final Supplier<Direction> facingSupplier;
    //TODO: Allow declaring that some sides will be the same, so can just be the same list in memory??

    protected BasicHolder(Supplier<Direction> facingSupplier) {
        this.facingSupplier = facingSupplier;
    }

    protected void addSlotInternal(@NotNull TYPE slot, RelativeSide... sides) {
        inventorySlots.add(slot);
        for (RelativeSide side : sides) {
            directionalSlots.computeIfAbsent(side, k -> new ArrayList<>()).add(slot);
        }
    }

    @NotNull
    public List<TYPE> getSlots(@Nullable Direction side) {
        if (side == null || directionalSlots.isEmpty()) {
            //If we want the internal OR we have no side specification, give all of our slots
            return inventorySlots;
        }
        List<TYPE> slots = directionalSlots.get(RelativeSide.fromDirections(facingSupplier.get(), side));
        if (slots == null) {
            return Collections.emptyList();
        }
        return slots;
    }
}