package com.hbm.blockentity.machine;

import com.hbm.blockentity.HBMTiles;
import com.hbm.blockentity.base.BaseMachineBE;
import com.hbm.item.tool.ItemKeyPin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 钥匙锻造台方块实体。
 * 移植自旧版 TileEntityMachineKeyForge：
 * - 槽0：模板钥匙（提供 pin）
 * - 槽1：待复制的钥匙（复制槽0的 pin）
 * - 槽2：待随机生成的钥匙（生成随机 pin）
 */
public class KeyForgeEntityBE extends BaseMachineBE {
    private final ItemStackHandler handler;

    public KeyForgeEntityBE(BlockPos pPos, BlockState pBlockState) {
        super(HBMTiles.getTypeById("machine_keyforge"), pPos, pBlockState);
        this.items = NonNullList.withSize(3, ItemStack.EMPTY);
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
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack.getItem() instanceof ItemKeyPin;
            }
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    public ItemStackHandler getItemHandler() {
        return handler;
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        // 复制：把槽0的 pin 复制到槽1
        ItemStack template = items.get(0);
        ItemStack target = items.get(1);
        if (!template.isEmpty() && !target.isEmpty()
                && template.getItem() instanceof ItemKeyPin src && src.canTransfer
                && target.getItem() instanceof ItemKeyPin dst && dst.canTransfer){
            ItemKeyPin.setPins(target, ItemKeyPin.getPins(template));
            this.setChanged();
        }

        // 随机：给槽2生成随机 pin
        ItemStack random = items.get(2);
        if (!random.isEmpty() && random.getItem() instanceof ItemKeyPin r && r.canTransfer){
            ItemKeyPin.setPins(random, this.getLevel().random.nextInt(900) + 100);
            this.setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_keyforge");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.KeyForgeMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(0));
    }
}
