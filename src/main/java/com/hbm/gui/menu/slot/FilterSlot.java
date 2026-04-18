package com.hbm.gui.menu.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class FilterSlot extends SlotItemHandler {
    public FilterSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    // 关键 1：当玩家点击物品放入时，我们只存入一个“副本”
    @Override
    public void set(@NotNull ItemStack stack) {
        if (!stack.isEmpty()) {
            // 复制一份，并将数量设为 1（虚影通常不需要显示数量）
            ItemStack ghostStack = stack.copy();
            ghostStack.setCount(1);
            super.set(ghostStack);
        } else {
            super.set(ItemStack.EMPTY);
        }
    }

    // 关键 2：禁止玩家把虚影“拿起来”
    @Override
    public boolean mayPickup(Player playerIn) {
        return false;
    }

    // 关键 3：允许玩家通过点击来“覆盖”或“清除”
    // 在 Container/Menu 层处理点击逻辑更佳，但 Slot 层可以限制放入规则
    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return true;
    }

    // 关键 4：不要重写 getItem()！
    // 渲染系统需要靠 getItem() 拿到 ItemStack 来画那个图标。
}
