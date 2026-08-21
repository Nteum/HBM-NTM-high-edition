package com.hbm.core.contents.machine;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 将机器模块的物品 handler 包装为 Container 视图，供配方 matches/assemble 使用。
 * 与 vanilla 的 RecipeWrapper 类似，但直接复用模块内的 MachineItemHandler。
 */
public class ModuleContainer implements Container {
    private final MachineModuleBase<?> module;

    public ModuleContainer(MachineModuleBase<?> module){
        this.module = module;
    }

    @Override
    public int getContainerSize() {
        return module.items != null ? module.items.getSlots() : 0;
    }

    @Override
    public boolean isEmpty() {
        if (module.items == null) return true;
        for (int i = 0; i < module.items.getSlots(); i++){
            if (!module.items.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int pSlot) {
        return module.items != null ? module.items.getStackInSlot(pSlot) : ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItem(int pSlot, int pAmount) {
        return module.items != null ? module.items.extractItem(pSlot, pAmount, false) : ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int pSlot) {
        return module.items != null ? module.items.extractItem(pSlot, 64, false) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int pSlot, @NotNull ItemStack pStack) {
        if (module.items != null) module.items.setStackInSlot(pSlot, pStack);
    }

    @Override
    public void setChanged() {
        if (module.items != null) module.items.notifyContentsChanged(0);
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return true;
    }

    @Override
    public void clearContent() {
        if (module.items != null){
            for (int i = 0; i < module.items.getSlots(); i++) module.items.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
}
