package com.hbm.blockentity.machine;

import com.hbm.blockentity.HBMTiles;
import com.hbm.blockentity.base.BaseMachineBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 组合漏斗。
 * 移植自旧版 TileEntityMachineFunnel：把 9 个（或 4 个）相同物品自动压缩为合成产物。
 * - 18 槽：0-8 输入、9-17 输出
 * - mode：0=3x3优先再2x2、1=仅3x3、2=仅2x2
 * - 无流体、无能量、单方块（OBJ 渲染简化为普通模型）
 */
public class FunnelEntityBE extends BaseMachineBE {
    public int mode = 0;
    public static final int MODE_ALL = 0;
    public static final int MODE_3x3 = 1;
    public static final int MODE_2x2 = 2;

    private final ItemStackHandler handler;

    public FunnelEntityBE(BlockPos pPos, BlockState pBlockState) {
        super(HBMTiles.getTypeById("machine_funnel"), pPos, pBlockState);
        this.items = NonNullList.withSize(18, ItemStack.EMPTY);
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
                if (slot > 8) return false;
                if (!items.get(slot).isEmpty()) return true;
                return getFrom9(stack) != null || getFrom4(stack) != null;
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

        for (int i = 0; i < 9; i++){
            ItemStack slot = items.get(i);
            if (slot.isEmpty()) continue;
            int stacksize = 9;
            ItemStack compressed = (mode == MODE_2x2 || slot.getCount() < 9) ? null : getFrom9(slot);
            if (compressed == null){
                compressed = (mode == MODE_3x3 || slot.getCount() < 4) ? null : getFrom4(slot);
                stacksize = 4;
            }
            if (compressed != null && slot.getCount() >= stacksize){
                ItemStack out = items.get(i + 9);
                if (out.isEmpty()){
                    items.set(i + 9, compressed.copy());
                    slot.shrink(stacksize);
                    if (slot.getCount() <= 0) items.set(i, ItemStack.EMPTY);
                    this.setChanged();
                } else if (out.is(compressed.getItem()) && out.getCount() + compressed.getCount() <= out.getMaxStackSize()){
                    out.grow(compressed.getCount());
                    slot.shrink(stacksize);
                    if (slot.getCount() <= 0) items.set(i, ItemStack.EMPTY);
                    this.setChanged();
                }
            }
        }
    }

    // 合成结果缓存
    private final Map<ItemStack, ItemStack> cache4 = new HashMap<>();
    private final Map<ItemStack, ItemStack> cache9 = new HashMap<>();

    public ItemStack getFrom4(ItemStack ingredient){
        if (cache4.containsKey(ingredient)) return cache4.get(ingredient);
        ItemStack result = getMatch(ingredient, 2);
        cache4.put(ingredient.copy(), result != null ? result.copy() : null);
        return result;
    }

    public ItemStack getFrom9(ItemStack ingredient){
        if (cache9.containsKey(ingredient)) return cache9.get(ingredient);
        ItemStack result = getMatch(ingredient, 3);
        cache9.put(ingredient.copy(), result != null ? result.copy() : null);
        return result;
    }

    private ItemStack getMatch(ItemStack ingredient, int size){
        if (this.getLevel() == null) return null;
        CraftingContainer grid = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
            @Override
            public ItemStack quickMoveStack(net.minecraft.world.entity.player.Player player, int index) {
                return ItemStack.EMPTY;
            }
            @Override
            public boolean stillValid(net.minecraft.world.entity.player.Player player) {
                return false;
            }
        }, size, size);
        for (int i = 0; i < size * size; i++) grid.setItem(i, ingredient.copy());
        java.util.Optional<CraftingRecipe> recipe = this.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, grid, this.getLevel());
        if (recipe.isPresent()){
            return recipe.get().getResultItem(this.getLevel().registryAccess());
        }
        return null;
    }

    public void nextMode(){
        mode++;
        if (mode > 2) mode = 0;
        this.setChanged();
    }

    @Override
    public CompoundTag getClientSyncTag() {
        CompoundTag tag = super.getClientSyncTag();
        tag.putInt("mode", mode);
        return tag;
    }

    @Override
    public void handleClientPacket(CompoundTag tag) {
        if (tag.contains("toggle")) nextMode();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("mode", mode);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        mode = pTag.getInt("mode");
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_funnel");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.FunnelMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(1));
    }
}
