package com.hbm.gui.menu;

import com.hbm.gui.ModMenuType;
import com.hbm.item.rbmk.ItemRBMKFuelRod;
import com.hbm.blockentity.machine.rbmk.RBMKFuelChannelEntity;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class RBMKFuelChannelMenu extends BaseMachineMenu {

    private static final int DATA_FIELDS = 12;
    private static final int SLOT_COUNT = 2;
    private static final int FUEL_SLOT_INDEX = 0;     // 燃料棒槽位
    private static final int OUTPUT_SLOT_INDEX = 1;   // 废燃料槽位

    // 对齐旧版 RBMK GUI（中心列 3 格中的上/下格）
    private static final int INPUT_SLOT_X = 80;
    private static final int INPUT_SLOT_Y = 45;
    private static final int OUTPUT_SLOT_X = 80;
    private static final int OUTPUT_SLOT_Y = 45;

    private static final int PLAYER_INV_X_OFFSET = -1;
    private static final int PLAYER_INV_Y_OFFSET = 19;
    private static final int TANK_CAPACITY = 16_000; // RBMK Base 的冷却水/蒸汽容量

    private final ContainerLevelAccess access;

    public RBMKFuelChannelMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, (RBMKFuelChannelEntity) null);
    }

    public RBMKFuelChannelMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        this(containerId, playerInventory, null, data);
    }

    public RBMKFuelChannelMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, resolveFuelChannel(playerInventory, buf));
    }

    public RBMKFuelChannelMenu(int containerId, Inventory playerInventory, RBMKFuelChannelEntity channel) {
        this(containerId, playerInventory, channel, channel != null ? channel.getContainerData() : new SimpleContainerData(DATA_FIELDS));
    }

    public RBMKFuelChannelMenu(int containerId, Inventory playerInventory, RBMKFuelChannelEntity channel, ContainerData data) {
        super(ModMenuType.RBMK_FUEL_CHANNEL_MENU.get(), containerId, channel != null ? channel : new SimpleContainer(SLOT_COUNT), data);
        this.access = channel != null && channel.getLevel() != null
                ? ContainerLevelAccess.create(channel.getLevel(), channel.getBlockPos())
                : ContainerLevelAccess.NULL;
        this.slotNum = SLOT_COUNT;
        this.checkContainerSize(this.container, SLOT_COUNT);
        this.checkContainerDataCount(data, DATA_FIELDS);

        // 燃料输入槽：仅允许 RBMK 燃料棒进入
        this.addSlot(new Slot(this.container, FUEL_SLOT_INDEX, INPUT_SLOT_X, INPUT_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isFuelRod(stack);
            }

            @Override
            public boolean isActive() {
                return RBMKFuelChannelMenu.this.container.getItem(OUTPUT_SLOT_INDEX).isEmpty();
            }
        });

        // 废燃料输出槽：只负责产物输出，禁止放入
        this.addSlot(new Slot(this.container, OUTPUT_SLOT_INDEX, OUTPUT_SLOT_X, OUTPUT_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean isActive() {
                return this.hasItem();
            }
        });

        addPlayerSlot(playerInventory, PLAYER_INV_X_OFFSET, PLAYER_INV_Y_OFFSET);
        this.addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.machine_rbmk_fuel_channel.get());
    }

    // data[0]：列当前热量（单位 0.1 kHE）
    public float getHeat() {
        return containerData.get(0) / 10F;
    }

    // data[1]：熔毁阈值（单位 0.1 kHE）
    public float getMeltdownThreshold() {
        return containerData.get(1) / 10F;
    }

    // data[2]：RBMK 底座当前能量
    public int getEnergyStored() {
        return containerData.get(2);
    }

    // data[3]：RBMK 底座能量容量
    public int getEnergyCapacity() {
        return containerData.get(3);
    }

    // data[4]：水冷回路液量
    public int getWaterAmount() {
        return containerData.get(4);
    }

    // data[5]：蒸汽缓冲液量
    public int getSteamAmount() {
        return containerData.get(5);
    }

    // data[6]：剩余燃烧时间（tick）
    public int getBurnTimeRemaining() {
        return containerData.get(6);
    }

    // data[7]：整支燃料棒的总燃烧时间（tick）
    public int getBurnTimeTotal() {
        return containerData.get(7);
    }

    // data[8]：燃烧状态标记
    public boolean isBurning() {
        return containerData.get(8) > 0;
    }

    // data[9]：最新换热功率（0.1 HE/s）
    public float getHeatPerSecond() {
        return containerData.get(9) / 10F;
    }

    // data[10]：本列控制棒插入百分比
    public int getLocalControlPercent() {
        return containerData.get(10);
    }

    // data[11]：全球平均控制棒插入百分比
    public int getGlobalControlPercent() {
        return containerData.get(11);
    }

    public float getHeatRatio() {
        float meltdown = getMeltdownThreshold();
        if (meltdown <= 0.0F) {
            return 0.0F;
        }
        return Mth.clamp(getHeat() / meltdown, 0.0F, 1.0F);
    }

    public float getEnergyRatio() {
        int capacity = getEnergyCapacity();
        if (capacity <= 0) {
            return 0.0F;
        }
        return Mth.clamp((float) getEnergyStored() / (float) capacity, 0.0F, 1.0F);
    }

    public float getCoolantRatio() {
        return Mth.clamp((float) getWaterAmount() / (float) TANK_CAPACITY, 0.0F, 1.0F);
    }

    public float getSteamRatio() {
        return Mth.clamp((float) getSteamAmount() / (float) TANK_CAPACITY, 0.0F, 1.0F);
    }

    public int getBurnProgressScaled(int width) {
        if (getBurnTimeTotal() <= 0) {
            return 0;
        }
        float consumed = (getBurnTimeTotal() - getBurnTimeRemaining()) / (float) getBurnTimeTotal();
        return (int) (width * Math.min(1.0F, Math.max(0.0F, consumed)));
    }

    public int getBurnProgressPercent() {
        if (getBurnTimeTotal() <= 0) {
            return 0;
        }
        float consumed = (getBurnTimeTotal() - getBurnTimeRemaining()) / (float) getBurnTimeTotal();
        return Math.max(0, Math.min(100, Math.round(consumed * 100F)));
    }

    public float getFuelProgress() {
        if (getBurnTimeTotal() <= 0) {
            return 0.0F;
        }
        return Mth.clamp((getBurnTimeTotal() - getBurnTimeRemaining()) / (float) getBurnTimeTotal(), 0.0F, 1.0F);
    }

    public int getBurnSecondsRemaining() {
        return Math.max(0, getBurnTimeRemaining() / 20);
    }

    public int getBurnSecondsTotal() {
        return Math.max(0, getBurnTimeTotal() / 20);
    }

    public boolean hasColumnData() {
        return containerData.get(1) > 0;
    }

    @Override
    public boolean innerMovePlayer2Container(int index, ItemStack stack) {
        if (isFuelRod(stack)) {
            return this.moveItemStackTo(stack, FUEL_SLOT_INDEX, FUEL_SLOT_INDEX + 1, false);
        }
        return false;
    }

    private static RBMKFuelChannelEntity resolveFuelChannel(Inventory playerInventory, FriendlyByteBuf buf) {
        Objects.requireNonNull(buf, "buffer missing block position");
        BlockPos pos = buf.readBlockPos();
        if (playerInventory.player.level().getBlockEntity(pos) instanceof RBMKFuelChannelEntity entity) {
            return entity;
        }
        return null;
    }

    private static boolean isFuelRod(ItemStack stack) {
        return stack.getItem() instanceof ItemRBMKFuelRod;
    }
}
