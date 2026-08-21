package com.hbm.blockentity.machine;

import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.HybridEnergyStorage;
import com.hbm.api.energy.IEnergyContainer;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.blockentity.HBMTiles;
import com.hbm.blockentity.base.BaseMachineBE;
import com.hbm.registries.HBMCaps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 微波炉。
 * 移植自旧版 TileEntityMicrowave：单方块、无流体、无自定义物品、无多方块。
 * - 输入槽（0）：可熔炼的食物
 * - 输出槽（1）：熔炼结果
 * - 电池槽（2）：给机器供电
 * - 速度等级 speed（0-5）通过 GUI 按钮调节，速度过高会爆炸
 */
public class MicrowaveEntityBE extends BaseMachineBE {
    public static final long MAX_POWER = 50000;
    public static final int CONSUMPTION = 50;
    public static final int MAX_TIME = 300;
    public static final int MAX_SPEED = 5;
    public int time;
    public int speed;

    private final BasicEnergyContainer energyContainer;
    private final HybridEnergyStorage forgeEnergy;
    private final ItemStackHandler handler;

    public MicrowaveEntityBE(BlockPos pPos, BlockState pBlockState) {
        super(HBMTiles.getTypeById("machine_microwave"), pPos, pBlockState);
        this.items = NonNullList.withSize(3, ItemStack.EMPTY);
        this.energyContainer = new BasicEnergyContainer(MAX_POWER);
        this.forgeEnergy = new HybridEnergyStorage(energyContainer);
        this.handler = new ItemStackHandler() {
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
                if (slot == 1) return stack;
                if (!isItemValid(slot, stack)) return stack;
                ItemStack current = items.get(slot);
                int max = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
                if (!current.isEmpty() && !ItemStack.isSameItemSameTags(current, stack)) return stack;
                int canInsert = max - current.getCount();
                if (canInsert <= 0) return stack;
                int inserted = Math.min(canInsert, stack.getCount());
                if (!simulate){
                    if (current.isEmpty()) items.set(slot, stack.copyWithCount(inserted));
                    else current.grow(inserted);
                    onContentsChanged(slot);
                }
                ItemStack remaining = stack.copy();
                remaining.shrink(inserted);
                return remaining;
            }
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot == 0) return ItemStack.EMPTY;
                ItemStack current = items.get(slot);
                if (current.isEmpty()) return ItemStack.EMPTY;
                int extracted = Math.min(amount, current.getCount());
                if (!simulate){
                    current.shrink(extracted);
                    if (current.isEmpty()) items.set(slot, ItemStack.EMPTY);
                    onContentsChanged(slot);
                }
                return current.copyWithCount(extracted);
            }
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return slot == 0 && getSmeltingResult(stack) != null;
            }
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(energyContainer));
        this.capabilitiesContent.addCapability(ForgeCapabilities.ENERGY, forgeEnergy);
    }

    @Override
    public IEnergyContainer getEnergyContainer() {
        return energyContainer;
    }

    public ItemStackHandler getItemHandler() {
        return handler;
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;
        Level level = this.getLevel();

        // 从电池槽放电
        com.hbm.api.energy.TransmitUtils.dischargeItem(this, items.get(2));

        if (canProcess()){
            if (speed >= MAX_SPEED){
                // 过热爆炸
                level.explode(null, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, 5.0F, true, Level.ExplosionInteraction.BLOCK);
                this.getLevel().removeBlock(this.worldPosition, false);
                return;
            }
            if (time >= MAX_TIME){
                process();
                time = 0;
            }
            if (canProcess()){
                this.energyContainer.extract(CONSUMPTION, false);
                time += speed * 2;
            }
        } else {
            time = 0;
        }
        this.setChanged();
    }

    private void process(){
        ItemStack result = getSmeltingResult(items.get(0));
        if (result == null || result.isEmpty()) return;
        ItemStack stack = result.copy();
        if (items.get(1).isEmpty()){
            items.set(1, stack);
        } else {
            items.get(1).grow(stack.getCount());
        }
        items.get(0).shrink(1);
        if (items.get(0).getCount() <= 0) items.set(0, ItemStack.EMPTY);
    }

    private boolean canProcess(){
        if (speed == 0) return false;
        if (energyContainer.getEnergy() < CONSUMPTION) return false;
        ItemStack input = items.get(0);
        ItemStack result = getSmeltingResult(input);
        if (input.isEmpty() || result == null || result.isEmpty()) return false;
        // 只处理食物
        if (!input.getItem().isEdible() && !result.getItem().isEdible()) return false;
        if (items.get(1).isEmpty()) return true;
        if (!items.get(1).is(result.getItem())) return false;
        return items.get(1).getCount() + result.getCount() <= result.getMaxStackSize();
    }

    @Nullable
    private ItemStack getSmeltingResult(ItemStack stack){
        if (stack == null || stack.isEmpty() || this.getLevel() == null) return null;
        return this.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainerWrapper(stack), this.getLevel()).map(r -> r.getResultItem(this.getLevel().registryAccess())).orElse(null);
    }

    /** 调整速度（GUI 按钮触发） */
    public void handleButtonPacket(int value){
        if (value == 0) speed++;
        if (value == 1) speed--;
        speed = Math.max(0, Math.min(speed, MAX_SPEED));
        this.setChanged();
    }

    @Override
    public CompoundTag getClientSyncTag() {
        CompoundTag tag = super.getClientSyncTag();
        tag.putInt("speed", speed);
        return tag;
    }

    @Override
    public void handleClientPacket(CompoundTag tag) {
        if (tag.contains("btnSpeed")) handleButtonPacket(tag.getInt("btnSpeed"));
    }

    public long getPowerScaled(int i){ return (energyContainer.getEnergy() * i) / MAX_POWER; }
    public int getProgressScaled(int i){ return (time * i) / MAX_TIME; }
    public int getSpeedScaled(int i){ return (speed * i) / MAX_SPEED; }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("time", time);
        pTag.putInt("speed", speed);
        pTag.put("energy", energyContainer.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        time = pTag.getInt("time");
        speed = pTag.getInt("speed");
        if (pTag.contains("energy")) energyContainer.deserializeNBT(pTag.getCompound("energy"));
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_microwave");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.MicrowaveMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(4));
    }

    private static class SimpleContainerWrapper extends net.minecraft.world.SimpleContainer {
        SimpleContainerWrapper(ItemStack stack){
            super(1);
            this.setItem(0, stack);
        }
    }
}
