package com.hbm.Inventory.recipe.alloy;

import com.hbm.HBM;
import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.CrucibleFluidHandler;
import com.hbm.Inventory.material.HBMMatForm;
import com.hbm.datagen.recipe.ingredient.FluidStackIngredient;
import com.hbm.registries.HBMMatters;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;

public class CrucibleRecipe {
    public static Map<ResourceLocation, CrucibleRecipe> recipes = new HashMap<>();
    static {
        int n = HBMMatForm.NUGGET.quantity;
        int i = HBMMatForm.INGOT.quantity;
        // TODO: 默认配方
        List.of(
                new CrucibleRecipe(HBMMatters.STEEL.name(), 2)
                        .input(FluidStackIngredient.of(HBMMatters.IRON.fluidTag(), n * 2), FluidStackIngredient.of(HBMMatters.CARBON.fluidTag(), n))
                        .output(new FluidStack(HBMMatters.STEEL.fluid(), n * 2)),
                new CrucibleRecipe(HBMMatters.HEMATITE.name(), 6)
                        .input(FluidStackIngredient.of(HBMMatters.HEMATITE.fluidTag(), i * 2), FluidStackIngredient.of(HBMMatters.FLUX.fluidTag(), n * 2))
                        .output(new FluidStack(HBMMatters.IRON.fluid(), i), new FluidStack(HBMMatters.SLAG.fluid(), n * 3)),
                new CrucibleRecipe(HBMMatters.MALACHITE.name(), 6)
                        .input(FluidStackIngredient.of(HBMMatters.MALACHITE.fluidTag(), i * 2), FluidStackIngredient.of(HBMMatters.FLUX.fluidTag(), n * 2))
                        .output(new FluidStack(HBMMatters.COPPER.fluid(), i), new FluidStack(HBMMatters.SLAG.fluid(), n * 3)),
                new CrucibleRecipe(HBMMatters.COPPER.name(), 2)
                        .input(FluidStackIngredient.of(HBMMatters.COPPER.fluidTag(), n), FluidStackIngredient.of(HBMMatters.REDSTONE.fluidTag(), n))
                        .output(new FluidStack(HBMMatters.MINGRADE.fluid(), n * 2)),
                new CrucibleRecipe(HBMMatters.ALLOY.name(), 2)
                        .input(FluidStackIngredient.of(HBMMatters.STEEL.fluidTag(), n), FluidStackIngredient.of(HBMMatters.MINGRADE.fluidTag(), n))
                        .output(new FluidStack(HBMMatters.ALLOY.fluid(), n * 2)),
                new CrucibleRecipe("hss", 9)
                        .input(FluidStackIngredient.of(HBMMatters.STEEL.fluidTag(), n * 5), FluidStackIngredient.of(HBMMatters.TUNGSTEN.fluidTag(), n * 3), FluidStackIngredient.of(HBMMatters.COBALT.fluidTag(), n * 1))
                        .output(new FluidStack(HBMMatters.DURA.fluid(), n * 9)),
                new CrucibleRecipe("hsss", 12)
                        .input(FluidStackIngredient.of(HBMMatters.STAINLESS.fluidTag(), n * 5), FluidStackIngredient.of(HBMMatters.TUNGSTEN.fluidTag(), n * 3), FluidStackIngredient.of(HBMMatters.COBALT.fluidTag(), n * 1))
                        .output(new FluidStack(HBMMatters.DURA.fluid(), i * 2)),
                new CrucibleRecipe(HBMMatters.FERRO.name(), 3)
                        .input(FluidStackIngredient.of(HBMMatters.STEEL.fluidTag(), n * 2), FluidStackIngredient.of(HBMMatters.U238.fluidTag(), n))
                        .output(new FluidStack(HBMMatters.FERRO.fluid(), n * 3)),
                new CrucibleRecipe(HBMMatters.TCALLOY.name(), 9)
                        .input(FluidStackIngredient.of(HBMMatters.STEEL.fluidTag(), n * 8), FluidStackIngredient.of(HBMMatters.TECHNETIUM.fluidTag(), n))
                        .output(new FluidStack(HBMMatters.TCALLOY.fluid(), i)),
                new CrucibleRecipe(HBMMatters.CDALLOY.name(), 9)
                        .input(FluidStackIngredient.of(HBMMatters.STEEL.fluidTag(), n * 8), FluidStackIngredient.of(HBMMatters.CADMIUM.fluidTag(), n))
                        .output(new FluidStack(HBMMatters.CDALLOY.fluid(), i)),
                new CrucibleRecipe(HBMMatters.BROMINE.name(), 9)
                        .input(FluidStackIngredient.of(HBMMatters.COPPER.fluidTag(), n * 8), FluidStackIngredient.of(HBMMatters.BISMUTH.fluidTag(), n), FluidStackIngredient.of(HBMMatters.FLUX.fluidTag(), n * 3))
                        .output(new FluidStack(HBMMatters.BBRONZE.fluid(), i), new FluidStack(HBMMatters.SLAG.fluid(), n * 3)),
                new CrucibleRecipe(HBMMatters.ABRONZE.name(), 9)
                        .input(FluidStackIngredient.of(HBMMatters.COPPER.fluidTag(), n * 8), FluidStackIngredient.of(HBMMatters.ARSENIC.fluidTag(), n), FluidStackIngredient.of(HBMMatters.FLUX.fluidTag(), n * 3))
                        .output(new FluidStack(HBMMatters.ABRONZE.fluid(), i), new FluidStack(HBMMatters.SLAG.fluid(), n * 3)),
                new CrucibleRecipe(HBMMatters.CMB.name(), 3)
                        .input(FluidStackIngredient.of(HBMMatters.MAGTUNG.fluidTag(), n * 6), FluidStackIngredient.of(HBMMatters.MUD.fluidTag(), n * 3))
                        .output(new FluidStack(HBMMatters.CMB.fluid(), i)),
                new CrucibleRecipe(HBMMatters.MAGTUNG.name(), 3)
                        .input(FluidStackIngredient.of(HBMMatters.TUNGSTEN.fluidTag(), i), FluidStackIngredient.of(HBMMatters.SCHRABIDIUM.fluidTag(), n * 1))
                        .output(new FluidStack(HBMMatters.MAGTUNG.fluid(), i)),
                new CrucibleRecipe(HBMMatters.BSCCO.name(), 3)
                        .input(
                                FluidStackIngredient.of(HBMMatters.BISMUTH.fluidTag(), n * 2),
                                FluidStackIngredient.of(HBMMatters.STRONTIUM.fluidTag(), n * 2),
                                FluidStackIngredient.of(HBMMatters.CALCIUM.fluidTag(), n * 2),
                                FluidStackIngredient.of(HBMMatters.COPPER.fluidTag(), n * 3)
                        )
                        .output(new FluidStack(HBMMatters.BSCCO.fluid(), i)),
                new CrucibleRecipe(HBMMatters.GAAS.name(), 9)
                        .input(FluidStackIngredient.of(HBMMatters.GALLIUM.fluidTag(), n * 6), FluidStackIngredient.of(HBMMatters.ARSENIC.fluidTag(), n * 3))
                        .output(new FluidStack(HBMMatters.GAAS.fluid(), i)),
                new CrucibleRecipe(HBMMatters.STAINLESS.name(), 2)
                        .input(FluidStackIngredient.of(HBMMatters.STEEL.fluidTag(), n), FluidStackIngredient.of(HBMMatters.NICKEL.fluidTag(), n))
                        .output(new FluidStack(HBMMatters.STAINLESS.fluid(), n * 2))
                /**
                 * 格雷科技联动部分暂时留空
                 * */
        ).forEach(recipe -> recipes.put(recipe.id, recipe));
    }
    private final ResourceLocation id;
    private final NonNullList<FluidStackIngredient> inputFluid;
    private final NonNullList<FluidStack> outputFluid;
    private final int frequent;
    public CrucibleRecipe(String name, int frequent){
        this(HBM.rl("crucible_" + name), frequent);
    }
    public CrucibleRecipe(ResourceLocation id, int frequent){
        this.id = id;
        this.inputFluid = NonNullList.create();
        this.outputFluid = NonNullList.create();
        this.frequent = frequent;
    }
    public CrucibleRecipe input(FluidStackIngredient ... fluidStackIngredients){
        inputFluid.addAll(List.of(fluidStackIngredients));
        return this;
    }
    public CrucibleRecipe output(FluidStack ... fluidStackIngredients){
        outputFluid.addAll(List.of(fluidStackIngredients));
        return this;
    }

    public ResourceLocation getId(){
        return this.id;
    }
    public int getFrequent(){
        return this.frequent;
    }

    public List<FluidStackIngredient> getInput(){
        return this.inputFluid;
    }

    public List<FluidStack> getOutput(){
        return this.outputFluid;
    }

    public static CrucibleRecipe findRecipe(CrucibleFluidHandler fluidHandler, Level level){
        if (fluidHandler.size() < 2) return null;
        List<CrucibleRecipe> recipeList = new ArrayList<>();
        NonNullList<FluidStack> fluidStacks = fluidHandler.getContent();
        for (CrucibleRecipe recipe : recipes.values()) {
            if (checkRecipe(recipe, fluidStacks) >= 0 && level.getGameTime() % recipe.frequent == 0) return recipe;
        }
        return null;
    }

    /**
     *
     * */
    public static int checkRecipe(CrucibleRecipe recipe, List<FluidStack> fluidStacks){
        int recipeSize = recipe.inputFluid.size();
        int stackSize = fluidStacks.size();
        if (stackSize < recipeSize) return -1;
        int[] volumes = recipe.inputFluid.stream().mapToInt(FluidStackIngredient::getVolume).toArray();
        // TODO:获得最大公因数的算式，原版是直接按照配方利的比例算的，因此暂时没必要，但不要删
//        int gcd = BobMth.lcm(BobMth.getGCD(volumes), BobMth.getGCD(recipe.outputFluid.stream().mapToInt(FluidStackIngredient::getVolume).toArray()));
//        for (int i = 0; i < volumes.length; i++) volumes[i] /= gcd;
        int[] matchOrders = new int[stackSize];
        Arrays.fill(matchOrders, -1);
        for (int i = 0; i < stackSize; i++) {
            FluidStack fluidStack = fluidStacks.get(i);
            for (int j = 0; j < recipe.inputFluid.size(); j++) {
                if (recipe.inputFluid.get(j).test(fluidStack) && fluidStack.getAmount() >= volumes[j]){
                    matchOrders[i] = j;
                }
            }
        }
        int[] matchTimes = new int[recipeSize];
        int tmp;
        for (int i = 0; i < stackSize; i++) {
            if (i - recipeSize >= 0 && matchOrders[i - recipeSize] != -1) matchTimes[matchOrders[i - recipeSize]] --;
            if (matchOrders[i] != -1) matchTimes[matchOrders[i]] ++;
            tmp = 0;
            while (tmp < recipeSize && matchTimes[tmp] == 1) tmp ++;
            if (tmp == recipeSize) return i;
        }
        return -1;
    }

    public static void autoMerge(CrucibleFluidHandler fluidHandler, Level level, @Nullable CrucibleRecipe exceptRecipe){
        fluidHandler.rebuild();
        if (fluidHandler.size() < 2) return;
        NonNullList<FluidStack> fluidStacks = fluidHandler.getContent();
        int matchedIdx = -1;
        CrucibleRecipe matchedRecipe = null;
        for (CrucibleRecipe recipe : recipes.values()) {
            if (recipe == exceptRecipe) continue;
            matchedIdx = checkRecipe(recipe, fluidStacks);
            if (matchedIdx >= 0 && level.getGameTime() % recipe.frequent == 0) {
                matchedRecipe = recipe;
                break;
            }
        }
        if (matchedIdx < 0 || matchedRecipe == null) return;
        for (int idx = matchedIdx; idx >= 0 && idx > matchedIdx - matchedRecipe.inputFluid.size(); idx--) {
            FluidStack fluidInTank = fluidHandler.getFluidInTank(idx);
            for (FluidStackIngredient ingredient : matchedRecipe.inputFluid) {
                if (ingredient.test(fluidInTank)) {
                    fluidInTank.shrink(ingredient.getVolume());
                    break;
                }
            }
        }
        Set<FluidStack> outputSet = new HashSet<>(matchedRecipe.outputFluid);
        int outputSize = outputSet.size();
        int i;
        for (i = matchedIdx + 1; i <= matchedIdx + outputSize && i < fluidHandler.size(); i++) {
            FluidStack fluidInTank = fluidHandler.getFluidInTank(i);
            for (FluidStack fluidStack : outputSet) {
                if (fluidInTank.isFluidEqual(fluidStack)){
                    fluidInTank.grow(fluidStack.getAmount());
                    outputSet.remove(fluidStack);
                    break;
                }
            }
        }
        if (!outputSet.isEmpty()){
            fluidHandler.getContent().addAll(i, outputSet);
        }
        // 清空空的fluidstack以及合并类型重复的fluidstack
        fluidHandler.rebuild();
    }

    public List<Component> description() {
        List<Component> description = new ArrayList<>();
        description.add(Component.literal("Input : "));
        for (FluidStackIngredient ingredient : this.inputFluid) {
            description.add(Component.literal(" - ").append(HBMLang.GUI_TOOLTIP_FLUID.translate(Component.translatable(ingredient.toString()), ingredient.getVolume())));
        }
        description.add(Component.literal("Output : "));
        for (FluidStack stack : this.outputFluid) {
            description.add(Component.literal(" - ").append(HBMLang.GUI_TOOLTIP_FLUID.translate(stack.getDisplayName(), stack.getAmount())));
        }
        return description;
    }

    public static CrucibleRecipe getRecipe(ResourceLocation rl){
        return recipes.get(rl);
    }
}
