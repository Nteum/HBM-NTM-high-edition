package com.hbm.gui.menu;

import com.hbm.blockentity.machine.WoodBurnerBlockEntity;
import com.hbm.gui.ModMenuType;
import com.hbm.gui.menu.slot.BatterySlot;
import com.hbm.gui.menu.slot.OutputSlot;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class WoodBurnerMenu extends BaseMachineMenu {

    private final WoodBurnerBlockEntity blockEntity;
    private final Level level;
    private final BlockPos blockPos;

    public WoodBurnerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, resolve(playerInventory, buf));
    }

    public WoodBurnerMenu(int containerId, Inventory playerInventory, WoodBurnerBlockEntity blockEntity, ContainerData data) {
        this(containerId, playerInventory, new Context(blockEntity, blockEntity != null ? blockEntity.getBlockPos() : playerInventory.player.blockPosition()),
                blockEntity != null ? blockEntity.getLevel() : playerInventory.player.level(), data != null ? data : new SimpleContainerData(7));
    }

    private WoodBurnerMenu(int containerId, Inventory playerInventory, Context context) {
        this(containerId, playerInventory, context, playerInventory.player.level(), new SimpleContainerData(7));
    }

    private WoodBurnerMenu(int containerId, Inventory playerInventory, Context context, Level level, ContainerData data) {
        super(ModMenuType.WOOD_BURNER_MENU.get(), containerId, resolveContainer(context), resolveData(data));
        WoodBurnerBlockEntity blockEntity = context.blockEntity();
        BlockPos pos = context.pos();
        this.blockEntity = blockEntity;
        this.level = level;
        this.blockPos = pos;
        this.slotNum = 3;

        this.addSlot(new Slot(this.container, 0, 44, 53) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return blockEntity != null && blockEntity.canPlaceItem(0, stack);
            }
        });
        this.addSlot(new OutputSlot(this.container, 1, 116, 53));
        this.addSlot(new BatterySlot(this.container, 2, 80, 71));

        addPlayerSlot(playerInventory, 6, 16);
        this.addDataSlots(this.containerData);
    }

    private static Context resolve(Inventory inventory, FriendlyByteBuf buf) {
        if (buf == null) {
            return new Context(null, inventory.player.blockPosition());
        }
        BlockPos pos = buf.readBlockPos();
        WoodBurnerBlockEntity blockEntity = null;
        Level level = inventory.player.level();
        if (level != null) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof WoodBurnerBlockEntity wood) {
                blockEntity = wood;
            }
        }
        return new Context(blockEntity, pos);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0 && blockEntity != null && !player.level().isClientSide) {
            blockEntity.toggleEnabled();
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockPos), player, ModBlocks.machine_wood_burner.get());
    }

    public int getBurnTimeScaled(int pixels) {
        int max = containerData.get(1);
        if (max <= 0) {
            return 0;
        }
        return Math.min(pixels, (int) (containerData.get(0) * (long) pixels / max));
    }

    public int getEnergyScaled(int pixels) {
        long energy = getEnergyLong();
        long capacity = getMaxEnergyLong();
        if (capacity <= 0) {
            return 0;
        }
        return (int) Math.min(pixels, energy * pixels / capacity);
    }

    public long getEnergyLong() {
        long lo = Integer.toUnsignedLong(containerData.get(2));
        long hi = Integer.toUnsignedLong(containerData.get(3));
        return (hi << 32) | lo;
    }

    public long getMaxEnergyLong() {
        long lo = Integer.toUnsignedLong(containerData.get(4));
        long hi = Integer.toUnsignedLong(containerData.get(5));
        return (hi << 32) | lo;
    }

    public boolean isLit() {
        return containerData.get(0) > 0;
    }

    public boolean isEnabled() {
        return containerData.get(6) > 0;
    }

    public int getBurnTime() {
        return containerData.get(0);
    }

    public int getMaxBurnTime() {
        return containerData.get(1);
    }

    private record Context(WoodBurnerBlockEntity blockEntity, BlockPos pos) {}

    private static Container resolveContainer(Context context) {
        return context.blockEntity() != null ? context.blockEntity() : new SimpleContainer(3);
    }

    private static ContainerData resolveData(ContainerData data) {
        return data != null ? data : new SimpleContainerData(7);
    }
}
