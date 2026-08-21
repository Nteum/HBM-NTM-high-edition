package com.hbm.blockentity.machine;

import com.hbm.blockentity.HBMTiles;
import com.hbm.blockentity.base.BaseMachineBE;
import com.hbm.item.ItemEnums;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 灰烬收集器。
 * 移植自旧版 TileEntityAshpit：
 * - 5 个灰烬槽位（只能取出）
 * - 接收灰烬等级（wood/coal/misc/fly/soot），达到阈值后合成为灰烬物品
 */
public class AshpitEntityBE extends BaseMachineBE {
    public int ashLevelWood;
    public int ashLevelCoal;
    public int ashLevelMisc;
    public int ashLevelFly;
    public int ashLevelSoot;

    public static int thresholdWood = 2000;
    public static int thresholdCoal = 2000;
    public static int thresholdMisc = 2000;
    public static int thresholdFly = 2000;
    public static int thresholdSoot = 8000;

    public boolean isFull;

    private final net.minecraftforge.items.ItemStackHandler handler;

    public AshpitEntityBE(BlockPos pPos, BlockState pBlockState) {
        super(HBMTiles.getTypeById("machine_ashpit"), pPos, pBlockState);
        this.items = NonNullList.withSize(5, ItemStack.EMPTY);
        this.handler = new net.minecraftforge.items.ItemStackHandler() {
            @Override
            public int getSlots() {
                return items.size();
            }
            @Override
            public ItemStack getStackInSlot(int slot) {
                return items.get(slot);
            }
            @Override
            public void setStackInSlot(int slot, ItemStack stack) {
                items.set(slot, stack);
                onContentsChanged(slot);
            }
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return stack;
            }
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                ItemStack current = items.get(slot);
                if (current.isEmpty()) return ItemStack.EMPTY;
                int extracted = Math.min(amount, current.getCount());
                if (!simulate) {
                    current.shrink(extracted);
                    if (current.isEmpty()) items.set(slot, ItemStack.EMPTY);
                    onContentsChanged(slot);
                }
                return current.copyWithCount(extracted);
            }
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
        this.capabilitiesContent.addCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER, handler);
    }

    public net.minecraftforge.items.ItemStackHandler getItemHandler() { return handler; }

    /** 供外部机器（火箱、烟囱等）投入灰烬 */
    public void addAsh(ItemEnums.EnumAshType type, int amount) {
        switch (type) {
            case WOOD -> ashLevelWood += amount;
            case COAL -> ashLevelCoal += amount;
            case MISC -> ashLevelMisc += amount;
            case FLY -> ashLevelFly += amount;
            case SOOT -> ashLevelSoot += amount;
            case FULLERENE -> {}
        }
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        if(processAsh(ashLevelWood, ItemEnums.EnumAshType.WOOD, ModItems.POWDER_ASH_WOOD.get(), thresholdWood)) ashLevelWood -= thresholdWood;
        if(processAsh(ashLevelCoal, ItemEnums.EnumAshType.COAL, ModItems.POWDER_ASH_COAL.get(), thresholdCoal)) ashLevelCoal -= thresholdCoal;
        if(processAsh(ashLevelMisc, ItemEnums.EnumAshType.MISC, ModItems.POWDER_ASH_MISC.get(), thresholdMisc)) ashLevelMisc -= thresholdMisc;
        if(processAsh(ashLevelFly, ItemEnums.EnumAshType.FLY, ModItems.POWDER_ASH_FLY.get(), thresholdFly)) ashLevelFly -= thresholdFly;
        if(processAsh(ashLevelSoot, ItemEnums.EnumAshType.SOOT, ModItems.POWDER_ASH_SOOT.get(), thresholdSoot)) ashLevelSoot -= thresholdSoot;

        isFull = false;
        for(int i = 0; i < 5; i++) {
            if(!items.get(i).isEmpty()) {
                isFull = true;
                break;
            }
        }
        this.setChanged();
    }

    protected boolean processAsh(int level, ItemEnums.EnumAshType type, Item item, int threshold) {
        if(level >= threshold) {
            for(int i = 0; i < 5; i++) {
                if(items.get(i).isEmpty()) {
                    items.set(i, new ItemStack(item));
                    return true;
                } else if(items.get(i).getCount() < items.get(i).getMaxStackSize() && items.get(i).getItem() == item) {
                    items.get(i).grow(1);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getSlots() {
        return 5;
    }

    @Override
    public boolean allowInput(int slot) {
        return false;
    }

    @Override
    public boolean allowOutput(int slot) {
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("ashLevelWood", ashLevelWood);
        pTag.putInt("ashLevelCoal", ashLevelCoal);
        pTag.putInt("ashLevelMisc", ashLevelMisc);
        pTag.putInt("ashLevelFly", ashLevelFly);
        pTag.putInt("ashLevelSoot", ashLevelSoot);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        ashLevelWood = pTag.getInt("ashLevelWood");
        ashLevelCoal = pTag.getInt("ashLevelCoal");
        ashLevelMisc = pTag.getInt("ashLevelMisc");
        ashLevelFly = pTag.getInt("ashLevelFly");
        ashLevelSoot = pTag.getInt("ashLevelSoot");
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_ashpit");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.AshpitMenu(pContainerId, pInventory, this);
    }
}
