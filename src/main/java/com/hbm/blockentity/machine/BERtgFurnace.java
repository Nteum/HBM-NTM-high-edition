package com.hbm.blockentity.machine;

import com.hbm.api.Mode;
import com.hbm.core.blockentity.BEMachineBase;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.gui.menu.MenuRtgFurnace;
import com.hbm.utils.RTGUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class BERtgFurnace extends BEMachineBase {
    public int dualCookTime;
    public boolean hasHeat;
    public static final int processingSpeed = 1000;

    private static final int[] slots_top = new int[] {0};
    private static final int[] slots_bottom = new int[] {4};
    private static final int[] slots_side = new int[] {1, 2, 3};

    @Override
    protected ContainerData createContainerData() {
        return new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex){
                    case 0 -> hasHeat ? 1 : 0;
                    case 1 -> dualCookTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {}

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public BERtgFurnace(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(5);
        this.sideAccess.setItemMode(Direction.UP, Mode.INPUT, 0)
                .setItemMode(Direction.DOWN, Mode.OUTPUT, 4)
                .setItemMode(Direction.NORTH, Mode.INPUT, 1, 2, 3)
                .setItemMode(Direction.SOUTH, Mode.INPUT, 1, 2, 3)
                .setItemMode(Direction.EAST, Mode.INPUT, 1, 2, 3)
                .setItemMode(Direction.WEST, Mode.INPUT, 1, 2, 3);
        this.addCapability(ForgeCapabilities.ITEM_HANDLER, this.items);
    }

    private SmeltingRecipe recipeNow = null;
    private RecipeManager.CachedCheck<Container, ? extends SmeltingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.SMELTING);
    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        hasHeat = RTGUtil.hasHeat(this.items, slots_side);
        boolean flag1 = false;
        boolean isRun = this.dualCookTime > 0;

        boolean canProcess = !this.items.getStackInSlot(0).isEmpty()
                && (recipeNow = quickCheck.getRecipeFor(new RecipeWrapper(this.items), this.level).orElse(null)) != null
                && this.items.insertItem(4, recipeNow.getResultItem(this.level.registryAccess()), true).isEmpty();
        if (hasHeat && canProcess){
            dualCookTime += RTGUtil.updateRTGs(this.items, slots_side);

            if(this.dualCookTime >= processingSpeed)
            {
                this.dualCookTime = 0;
                this.items.extractItem(0, 1, false);
                this.items.insertItem(4, this.recipeNow.getResultItem(this.level.registryAccess()).copy(), false);  // 配方结果必须要复制，不能直接传入
                flag1 = true;
            }
        }else{
            dualCookTime = 0;
            RTGUtil.updateRTGs(this.items, slots_side);
        }

        if(isRun ^ this.dualCookTime > 0)
        {
            flag1 = true;
            this.level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(BlockStateProperties.LIT, this.dualCookTime > 0));
        }

        if(flag1) this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putShort("cookTime", (short) dualCookTime);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        dualCookTime = tag.getShort("CookTime");
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new MenuRtgFurnace(pContainerId, pInventory, this, getContainerData());
    }
}
