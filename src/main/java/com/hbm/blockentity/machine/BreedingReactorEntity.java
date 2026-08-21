package com.hbm.blockentity.machine;

import com.hbm.Inventory.recipe.BreederRecipes;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 增殖反应堆（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineReactorBreeding：从相邻研究反应堆取中子通量，增殖燃料棒。
 * - 2 槽：0 输入棒、1 输出棒
 * - 简化：flux 从相邻方块（research reactor）读取
 */
public class BreedingReactorEntity extends BEDummyable {
    public int flux;
    public float progress;

    public BreedingReactorEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(2);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_REACTOR_BREEDING.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        this.flux = 0;
        getInteractions();

        if (canProcess()){
            progress += 0.0025F;
            if (this.progress >= 1.0F){
                this.progress = 0F;
                processItem();
            }
        } else {
            progress = 0.0F;
        }
        this.setChanged();
    }

    private void getInteractions(){
        // 简化：flux 由测试/外部注入，或从相邻 research reactor 读取
        com.hbm.blockentity.machine.research.ResearchReactorBE reactor = com.hbm.utils.WorldUtils.getTileEntity(com.hbm.blockentity.machine.research.ResearchReactorBE.class, this.getLevel(), this.worldPosition.south());
        if (reactor != null){
            this.flux += reactor.getFlux();
        }
    }

    private boolean canProcess(){
        ItemStack input = items.getStackInSlot(0);
        if (input.isEmpty()) return false;
        com.hbm.Inventory.recipe.BreederRecipes.BreederRecipe recipe = BreederRecipes.getOutput(input);
        if (recipe == null) return false;
        if (this.flux < recipe.flux()) return false;

        ItemStack out = items.getStackInSlot(1);
        if (out.isEmpty()) return true;
        if (!out.is(recipe.createOutput(input).getItem())) return false;
        return out.getCount() < out.getMaxStackSize();
    }

    private void processItem(){
        ItemStack input = items.getStackInSlot(0);
        com.hbm.Inventory.recipe.BreederRecipes.BreederRecipe recipe = BreederRecipes.getOutput(input);
        if (recipe == null) return;
        ItemStack outItem = recipe.createOutput(input);
        ItemStack out = items.getStackInSlot(1);
        if (out.isEmpty()) items.setStackInSlot(1, outItem.copy());
        else if (out.is(outItem.getItem())) out.grow(outItem.getCount());

        items.setStackInSlot(0, recipe.consumeInput(input));
    }

    public MachineItemHandler getItemHandler(){ return items; }
    public int getProgressScaled(int i){ return (int)(this.progress * i); }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.BreedingReactorMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(2));
    }
}
