package com.hbm.gui.menu;

import com.hbm.block.machine.IronCrateBlock;
import com.hbm.blockentity.machine.IronCrateBlockEntity;
import com.hbm.gui.ModMenuType;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * 36-slot storage container with protected player inventory slots to avoid self-containment exploits.
 */
public class IronCrateMenu extends AbstractContainerMenu {

    private static final int CRATE_ROWS = 4;
    private static final int CRATE_COLUMNS = 9;
    private static final int CRATE_SLOT_COUNT = CRATE_ROWS * CRATE_COLUMNS;
    private static final int PLAYER_INV_OFFSET = CRATE_SLOT_COUNT;
    private static final int PLAYER_HOTBAR_OFFSET = PLAYER_INV_OFFSET + 27;
    private static final int TOTAL_SLOTS = PLAYER_HOTBAR_OFFSET + 9;

    private final IronCrateBlockEntity blockEntity;
    private final Level level;
    private final ItemStack crateSignature;

    public IronCrateMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory, inventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public IronCrateMenu(int id, Inventory inventory, BlockEntity entity) {
        super(ModMenuType.IRON_CRATE_MENU.get(), id);
        if (entity instanceof IronCrateBlockEntity crate) {
            this.blockEntity = crate;
        } else {
            this.blockEntity = new IronCrateBlockEntity(BlockPos.ZERO, ModBlocks.crate_iron.get().defaultBlockState());
        }
        this.level = inventory.player.level();
        this.crateSignature = new ItemStack(ModBlocks.crate_iron.get());
        addCrateSlots(this.blockEntity, 8, 18);
        addPlayerInventory(inventory, 8, 104);
        addPlayerHotbar(inventory, 8, 162);
    }

    private void addCrateSlots(IronCrateBlockEntity crate, int startX, int startY) {
        crate.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            for (int row = 0; row < CRATE_ROWS; row++) {
                for (int col = 0; col < CRATE_COLUMNS; col++) {
                    int index = row * CRATE_COLUMNS + col;
                    this.addSlot(new SlotItemHandler(handler, index, startX + col * 18, startY + row * 18));
                }
            }
        });
    }

    private void addPlayerInventory(Inventory inventory, int startX, int startY) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int index = col + row * 9 + 9;
                this.addSlot(new ProtectedSlot(inventory, index, startX + col * 18, startY + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory, int startX, int startY) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new ProtectedSlot(inventory, col, startX + col * 18, startY));
        }
    }

    private class ProtectedSlot extends Slot {

        public ProtectedSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPickup(Player player) {
            ItemStack stack = this.getItem();
            return !ItemStack.isSameItemSameTags(stack, crateSignature);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return !ItemStack.isSameItemSameTags(stack, crateSignature);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.crate_iron.get());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot sourceSlot = this.slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return empty;
        }
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        if (index < CRATE_SLOT_COUNT) {
            if (!this.moveItemStackTo(sourceStack, PLAYER_INV_OFFSET, TOTAL_SLOTS, true)) {
                return empty;
            }
        } else {
            if (!this.moveItemStackTo(sourceStack, 0, CRATE_SLOT_COUNT, false)) {
                return empty;
            }
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return copy;
    }
}
