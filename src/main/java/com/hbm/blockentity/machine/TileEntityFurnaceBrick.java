package com.hbm.blockentity.machine;

import com.hbm.Inventory.material.BurnUtils;
import com.hbm.api.Mode;
import com.hbm.core.blockentity.BEMachineBase;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.gui.menu.MenuFurnaceBrick;
import com.hbm.item.ItemEnums.EnumAshType;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import com.hbm.registries.RegistryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.HashMap;
import java.util.Optional;

public class TileEntityFurnaceBrick extends BEMachineBase {
    private static final int[] slotsTop = new int[] { 0 };
    private static final int[] slotsBottom = new int[] { 2, 1, 3 };
    private static final int[] slotsSides = new int[] {1};
    public TileEntityFurnaceBrick(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(4){
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return super.isItemValid(slot, stack) && (slot != 1 || BurnUtils.isFuel(stack));
            }
        };
        this.sideAccess.setItemMode(Direction.UP, Mode.INPUT, 0)
                .setItemMode(Direction.DOWN, Mode.OUTPUT, 2, 1, 3)
                .setItemMode(Direction.EAST, Mode.INPUT, 1).setItemMode(Direction.WEST, Mode.INPUT, 1)
                .setItemMode(Direction.SOUTH, Mode.INPUT, 1).setItemMode(Direction.NORTH, Mode.INPUT, 1);
        addCapability(ForgeCapabilities.ITEM_HANDLER, this::getItemHandler);
    }

    @Override
    protected ContainerData createContainerData() {
        return new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex){
                    case 0 -> burnTime;
                    case 1 -> maxBurnTime;
                    case 2 -> progress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {

            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    public int burnTime;
    public int maxBurnTime;
    public int progress;

    public int ashLevelWood;
    public int ashLevelCoal;
    public int ashLevelMisc;

    public static HashMap<Ingredient, Integer> burnSpeed = new HashMap();

    static {
        burnSpeed.put(Ingredient.of(Items.CLAY_BALL),				4);
//        burnSpeed.put(ModItems.ball_fireclay,						4);
        burnSpeed.put(Ingredient.of(Blocks.NETHERRACK),     		4);
        burnSpeed.put(Ingredient.of(Blocks.COBBLESTONE),	        2);
//        burnSpeed.put(Ingredient.of(ModBlocks.duna_cobble),	2);
//        burnSpeed.put(Ingredient.of(ModBlocks.dres_rock),	2);
//        burnSpeed.put(Ingredient.of(ModBlocks.ike_regolith), 2);
//        burnSpeed.put(Ingredient.of(ModBlocks.eve_rock),	2);
//        burnSpeed.put(Ingredient.of(ModBlocks.moho_regolith), 2);
        burnSpeed.put(Ingredient.of(ModBlocks.moon_rock.get()),   	2);
//        burnSpeed.put(Ingredient.of(ModBlocks.minmus_regolith), 2);
        burnSpeed.put(Ingredient.of(Blocks.SAND),		           	2);
//        burnSpeed.put(Ingredient.of(ModBlocks.duna_sands),	2);
//        burnSpeed.put(Ingredient.of(ModBlocks.laythe_silt),	2);
//        burnSpeed.put(Ingredient.of(ModBlocks.eve_silt),	2);
        burnSpeed.put(Ingredient.of(ModBlocks.moon_turf.get()),	    2);
        burnSpeed.put(Ingredient.of(ItemTags.LOGS),     			2);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        boolean markDirty = false;
        boolean wasBurning = this.burnTime > 0;
        boolean canOperate = breatheAir(wasBurning && level.getGameTime() % 5 == 0 ? 1 : 0);

        if (burnTime > 0) burnTime --;
        if (burnTime != 0 || !this.items.getStackInSlot(0).isEmpty() && !this.items.getStackInSlot(1).isEmpty()){
            if(canOperate && this.burnTime == 0 && this.canSmelt()) {
                this.maxBurnTime = this.burnTime = ForgeHooks.getBurnTime(this.items.getStackInSlot(1), null);

                if(this.burnTime > 0) {
                    markDirty = true;
                    ItemStack extractItem = this.items.extractItem(1, 1, false);
                    if (!extractItem.isEmpty()){
                        EnumAshType type = BurnUtils.getAshFromFuel(extractItem);
                        if(type == EnumAshType.WOOD) ashLevelWood += burnTime;
                        if(type == EnumAshType.COAL) ashLevelCoal += burnTime;
                        if(type == EnumAshType.MISC) ashLevelMisc += burnTime;
                        int threshold = 2000;
                        if(processAsh(ashLevelWood, EnumAshType.WOOD, threshold)) ashLevelWood -= threshold;
                        if(processAsh(ashLevelCoal, EnumAshType.COAL, threshold)) ashLevelCoal -= threshold;
                        if(processAsh(ashLevelMisc, EnumAshType.MISC, threshold)) ashLevelMisc -= threshold;
                        if (this.items.getStackInSlot(1).isEmpty() && extractItem.hasCraftingRemainingItem())
                            this.items.insertItem(1, extractItem.getCraftingRemainingItem(), false);
                    }
                }
            }

            if(canOperate && this.burnTime > 0 && this.canSmelt()) {
                this.progress += this.getBurnSpeed();

                if(this.progress >= 200) {
                    this.progress = 0;
                    this.smeltItem();
                    markDirty = true;
                }
            } else {
                this.progress = 0;
            }
        }

        if(wasBurning != this.burnTime > 0) {
            markDirty = true;
            BlockState newState = this.getBlockState().setValue(BlockStateProperties.LIT, this.burnTime > 0 ? Boolean.TRUE : Boolean.FALSE);
            this.level.setBlockAndUpdate(this.getBlockPos(), newState);
        }

        if(markDirty) {
            this.setChanged();
        }

        this.sendUpdatePacket();
    }

    public int getBurnSpeed() {
        Integer speed = burnSpeed.get(this.items.getStackInSlot(0).getItem());
        if(speed != null) return speed;
        return 1;
    }

    protected boolean processAsh(int level, EnumAshType type, int threshold) {
        if(level >= threshold) {
            ItemStack resultItems = this.items.insertItem(3, BurnUtils.getAshPowder(type), true);
            if (resultItems.isEmpty()) return true;
        }

        return false;
    }

    private boolean canSmelt() {
        ItemStack inputStack = this.items.getStackInSlot(0);
        Optional<SmeltingRecipe> recipe = this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new RecipeWrapper(this.items), this.level);
        return !inputStack.isEmpty()
                && recipe.isPresent()
                && this.items.insertItem(3, recipe.get().getResultItem(this.getLevel().registryAccess()), true).isEmpty();
    }
    // 假定之前判别过了
    public void smeltItem() {
        if(this.canSmelt()) {
            Optional<SmeltingRecipe> recipe = this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new RecipeWrapper(this.items), this.level);
            this.items.extractItem(0, 1,  false);
            this.items.insertItem(2, recipe.get().getResultItem(this.getLevel().registryAccess()).copy(), false);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("burnTime", this.burnTime);
        nbt.putInt("maxBurn", this.maxBurnTime);
        nbt.putInt("progress", this.progress);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.burnTime = nbt.getInt("burnTime");
        this.maxBurnTime = nbt.getInt("maxBurn");
        this.progress = nbt.getInt("progress");
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MenuFurnaceBrick(pContainerId, pInventory, this, getContainerData());
    }
}
