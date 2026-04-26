package com.hbm.blockentity.tools;

import com.hbm.HBMKey;
import com.hbm.blockentity.base.CapabilityBlockEntity;
import com.hbm.item.tool.ItemMold;
import com.hbm.registries.HBMMatters;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public abstract class TileFoundryBase extends CapabilityBlockEntity {
    // 第一个物品是浇筑模板，第二个物品是铸造的物品
    ItemStackHandler items = new ItemStackHandler(2){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            shouldSync = true;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot){
                case 0 -> stack.getItem() instanceof ItemMold;
                default -> false;
            };
        }
    };
    FluidTank tank;
    boolean shouldSync = false;
    public TileFoundryBase(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, items);
    }

    @Override
    protected void onUpdateServer() {
        ItemStack moldStack = this.items.getStackInSlot(0);
        ItemStack resultStack = this.items.getStackInSlot(1);
        if (resultStack.isEmpty() && moldStack.getItem() instanceof ItemMold itemMold){
            int quantity = itemMold.getQuantity();
            Item resultItem = HBMMatters.getItemWithMatForm(this.tank.getFluid(), itemMold.getForm());
            if (this.tank.getFluid().getAmount() >= quantity && resultItem != null){
                this.tank.drain(quantity, IFluidHandler.FluidAction.EXECUTE);
                this.items.setStackInSlot(1, new ItemStack(resultItem, itemMold.getSize()));
            }
        }
        if (shouldSync){
            sendUpdatePacket();
            shouldSync = false;
        }
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.put(HBMKey.ITEM, this.items.serializeNBT());
        if (this.tank != null) this.tank.writeToNBT(tag);
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        if (tag.contains(HBMKey.ITEM, Tag.TAG_COMPOUND)) this.items.deserializeNBT(tag.getCompound(HBMKey.ITEM));
        this.tank.readFromNBT(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (this.items != null) pTag.put(HBMKey.ITEM, this.items.serializeNBT());
        if (this.tank != null) this.tank.writeToNBT(pTag);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains(HBMKey.ITEM, Tag.TAG_COMPOUND)) this.items.deserializeNBT(tag.getCompound(HBMKey.ITEM));
        if (this.tank != null) this.tank.readFromNBT(tag);
    }

    public ItemStackHandler getItems(){
        return this.items;
    }
    public FluidTank getTank(){
        return this.tank;
    }

    public FluidStack pour(FluidStack fluidStack){
        ItemStack moldStack, resultStack;
        if (!(moldStack = this.items.getStackInSlot(0)).isEmpty() && (resultStack = this.items.getStackInSlot(1)).isEmpty() && this.tank.isFluidValid(fluidStack)){
            if (moldStack.getItem() instanceof ItemMold mold){
                int quantity = mold.getQuantity();
                int fillAmount = Math.min(quantity - this.tank.getFluidAmount(), fluidStack.getAmount());
                FluidStack copied = fluidStack.copy();
                copied.setAmount(fillAmount);
                fluidStack.setAmount(this.tank.fill(copied, IFluidHandler.FluidAction.EXECUTE));
                return fluidStack;
            }
        }
        return FluidStack.EMPTY;
    }

    public void leftClick(Player player, InteractionHand hand){
        ItemStack itemInHand = player.getItemInHand(hand);
        ItemStack moldStack, resultStack;
        if (!this.tank.isEmpty()){
            // 如果有液体，并且玩家手持铁锹，可以清除液体并获得废料。
            if (itemInHand.is(ItemTags.SHOVELS)){
                this.tank.drain(this.tank.getFluid(), IFluidHandler.FluidAction.EXECUTE);
                // TODO:暂时用泥土作为废料的替代物（因为废料还没做）
                player.getInventory().add(new ItemStack(Blocks.DIRT.asItem()));
            }
        }else {
            if (!(resultStack = this.items.getStackInSlot(1)).isEmpty()){
                this.items.setStackInSlot(1, ItemStack.EMPTY);
                player.getInventory().add(resultStack.copy());
            }else if (!(moldStack = this.items.getStackInSlot(0)).isEmpty()){
                this.items.setStackInSlot(0, ItemStack.EMPTY);
                player.getInventory().add(moldStack.copy());
            }else if (itemInHand.getItem() instanceof ItemMold){
                if (this.items.isItemValid(0, itemInHand)){
                    this.items.insertItem(0, itemInHand.copy(), false);
                    itemInHand.shrink(1);
                }
            }
        }
    }
}
