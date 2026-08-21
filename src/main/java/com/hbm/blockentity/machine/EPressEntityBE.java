package com.hbm.blockentity.machine;

import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.RecipePress;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.blockentity.HBMTiles;
import com.hbm.blockentity.base.BaseMachineBE;
import com.hbm.item.tool.ItemStamp;
import com.hbm.registries.HBMCaps;
import com.hbm.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 电动锻压机。
 * 移植自旧版 TileEntityMachineEPress：
 * - 槽位：0 电池、1 模板、2 输入、3 输出、4 升级
 * - 用电能驱动锻压（每次操作消耗 100 HE/tick）
 * - 配方复用 ModRecipes.PRESS
 */
public class EPressEntityBE extends BaseMachineBE {
    public static final long MAX_POWER = 50000;
    public static final int CONSUMPTION = 100;
    public static final int MAX_PRESS = 200;

    public long power;
    public int press;
    public double renderPress;
    public double lastPress;
    private int syncPress;
    private int turnProgress;
    boolean isRetracting = false;
    private int delay;

    private final BasicEnergyContainer energyContainer;
    private final ItemStackHandler handler;

    public EPressEntityBE(BlockPos pPos, BlockState pBlockState) {
        super(HBMTiles.getTypeById("machine_epress"), pPos, pBlockState);
        this.items = NonNullList.withSize(5, ItemStack.EMPTY);
        this.energyContainer = new BasicEnergyContainer(MAX_POWER);
        this.energyContainer.setListener(this::setChanged);
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(energyContainer));
        this.capabilitiesContent.addCapability(ForgeCapabilities.ENERGY, new com.hbm.api.energy.HybridEnergyStorage(energyContainer));
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
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if (stack.getItem() instanceof ItemStamp)
                    return slot == 1;
                return slot == 2;
            }
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, handler);
    }

    public ItemStackHandler getItemHandler() { return handler; }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        TransmitUtils.dischargeItem(this, items.get(0));
        this.power = this.energyContainer.getEnergy();

        boolean canProcess = this.canProcess();

        if((canProcess || this.isRetracting || this.delay > 0) && power >= CONSUMPTION) {

            this.energyContainer.extract(CONSUMPTION, false);
            this.power = this.energyContainer.getEnergy();

            if(delay <= 0) {

                int stampSpeed = this.isRetracting ? 20 : 45;

                if(this.isRetracting) {
                    this.press -= stampSpeed;

                    if(this.press <= 0) {
                        this.isRetracting = false;
                        this.delay = 5;
                    }
                } else if(canProcess) {
                    this.press += stampSpeed;

                    if(this.press >= MAX_PRESS) {
                        RecipeWrapper wrapper = new RecipeWrapper(this.handler);
                        Optional<RecipePress> recipe = this.getLevel().getRecipeManager().getRecipeFor(ModRecipes.PRESS.type().get(), wrapper, this.getLevel());
                        if (recipe.isPresent()) {
                            Vec3 center = this.worldPosition.getCenter();
                            this.level.playSound(null, center.x, center.y, center.z, ModSounds.BLOCK_PRESS_OPERATE.get(), SoundSource.BLOCKS, 1.5f, 1.0F);
                            this.handler.insertItem(3, recipe.get().assemble(wrapper, this.getLevel().registryAccess()), false);
                            this.handler.extractItem(2, 1, false);
                            ItemStack stampStack = this.handler.getStackInSlot(1);
                            if (stampStack.hurt(1, this.level.random, null)) {
                                stampStack.shrink(1);
                            }
                            this.setChanged();
                        }
                        this.isRetracting = true;
                        this.delay = 5;
                    }
                } else if(this.press > 0){
                    this.isRetracting = true;
                }
            } else {
                delay--;
            }
        } else if (this.press > 0) {
            this.isRetracting = true;
        }

        this.sendUpdatePacket();
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        this.lastPress = this.renderPress;

        if(this.turnProgress > 0) {
            this.renderPress = this.renderPress + ((this.syncPress - this.renderPress) / (double) this.turnProgress);
            --this.turnProgress;
        } else {
            this.renderPress = this.syncPress;
        }
    }

    public boolean canProcess() {
        if(power < CONSUMPTION) return false;
        if(items.get(1).isEmpty() || items.get(2).isEmpty()) return false;

        RecipeWrapper wrapper = new RecipeWrapper(this.handler);
        Optional<RecipePress> recipe = this.getLevel().getRecipeManager().getRecipeFor(ModRecipes.PRESS.type().get(), wrapper, this.getLevel());

        if (recipe.isEmpty()) return false;

        ItemStack output = recipe.get().getResultItem(this.getLevel().registryAccess());
        if(items.get(3).isEmpty()) return true;
        if(items.get(3).getCount() + output.getCount() <= items.get(3).getMaxStackSize() && ItemStack.isSameItemSameTags(items.get(3), output)) return true;
        return false;
    }

    @Override
    public CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        tag.put("item", this.handler.getStackInSlot(2).serializeNBT());
        tag.putInt("press", this.press);
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        this.handler.setStackInSlot(2, ItemStack.of((CompoundTag) tag.get("item")));
        this.syncPress = tag.getInt("press");
        this.turnProgress = 2;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putLong("power", power);
        pTag.putInt("press", press);
        pTag.putBoolean("ret", isRetracting);
        pTag.put("energy", energyContainer.serializeNBT());
        pTag.put("items", this.handler.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        power = pTag.getLong("power");
        press = pTag.getInt("press");
        isRetracting = pTag.getBoolean("ret");
        if (pTag.contains("energy")) energyContainer.deserializeNBT(pTag.getCompound("energy"));
        try {
            this.handler.deserializeNBT((CompoundTag) pTag.get("items"));
        }catch (Exception ignored){ }
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_epress");
    }

    public long getPowerScaled(int i){ return (power * i) / MAX_POWER; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.EPressMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(2));
    }
}
