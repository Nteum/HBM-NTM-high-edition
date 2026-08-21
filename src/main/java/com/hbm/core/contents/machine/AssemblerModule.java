package com.hbm.core.contents.machine;

import com.hbm.Inventory.recipe.AssemblerRecipe;
import com.hbm.api.energy.IEnergyContainer;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

/**
 * 装配机模块示例：演示如何用 MachineModuleBase 快速搭建一个配方机器。
 *
 * 配置：
 * - 输入槽：从 baseSlot 开始的 12 个槽
 * - 输出槽：1 个
 * - 能量：消耗 recipe 所需电力
 *
 * 实际机器 TE 只需持有本模块，并在 tick 中调用 findRecipe/canProcess/process，
 * 物品与流体的进出由 TE 通过 ItemTransferUtils 按 getInputSlots/getOutputSlots 处理。
 */
public class AssemblerModule extends MachineModuleBase<AssemblerRecipe> {

    public AssemblerModule(int index, MachineItemHandler items, IEnergyContainer battery, BasicFluidHandler fluids, int inputBaseSlot){
        super(index, items, battery, fluids);
        this.inputSlots = new int[12];
        for (int i = 0; i < inputSlots.length; i++) inputSlots[i] = inputBaseSlot + i;
        this.outputSlots = new int[]{inputBaseSlot + 12};
        this.inputTanks = new int[0];
        this.outputTanks = new int[0];
    }

    @Override
    protected List<AssemblerRecipe> getRecipes(Level level){
        return level.getRecipeManager().getAllRecipesFor(com.hbm.Inventory.recipe.ModRecipes.ASSEMBLER.type().get()).stream().toList();
    }

    @Override
    protected void consumeInput(Level level){
        if (recipe == null) return;
        for (int i = 0; i < Math.min(recipe.ingredients.size(), inputSlots.length); i++){
            ItemStack slot = items.getStackInSlot(inputSlots[i]);
            int cost = recipe.ingredients.get(i).value.itemStack.getCount();
            slot.shrink(cost);
            if (slot.getCount() <= 0) items.setStackInSlot(inputSlots[i], ItemStack.EMPTY);
        }
    }

    @Override
    protected void produceOutput(Level level){
        if (recipe == null) return;
        ItemStack result = recipe.getResultItem(level.registryAccess());
        ItemStack out = items.getStackInSlot(outputSlots[0]);
        if (out.isEmpty()) items.setStackInSlot(outputSlots[0], result.copy());
        else {
            out.grow(result.getCount());
            items.setStackInSlot(outputSlots[0], out);
        }
    }
}
