package com.hbm.compat.jei;

import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JEIUtils {
    public static BiFunction<List<? extends Ingredient>, Integer, List<ItemStack>> ingredient2list = (ingredients, idx) -> idx < ingredients.size() ? Arrays.stream(ingredients.get(idx).getItems()).toList() : List.of(ItemStack.EMPTY);
    public static int[][] getInputSlotPositions(int count) {

        if(count == 1) return new int[][] { {48, 24} };
        if(count == 2) return new int[][] { {30, 24}, {48, 24} };
        if(count == 3) return new int[][] { {12, 24}, {30, 24}, {48, 24} };
        if(count == 4) return new int[][] { {30, 15}, {48, 15}, {30, 33}, {48, 33} };
        if(count == 5) return new int[][] { {12, 15}, {30, 15}, {48, 15}, {12, 33}, {30, 33} };
        if(count == 6) return new int[][] { {12, 15}, {30, 15}, {48, 15}, {12, 33}, {30, 33}, {48, 33} };

        int[][] slots = new int[count][2];
        int cols = (count + 2) / 3;

        for(int i = 0; i < count; i++) {
            slots[i][0] = 12 + (i % cols) * 18 - (cols == 4 ? 18 : 0);
            slots[i][1] = 6 + (i / cols) * 18;
        }

        return slots;
    }

    public static int[][] getOutputSlotPositions(int count) {
        switch(count) {
            case 1: return new int[][] {
                    {102, 24}
            };
            case 2: return new int[][] {
                    {102, 24},
                    {120, 24}
            };
            case 3: return new int[][] {
                    {102, 24},
                    {120, 24},
                    {138, 24}
            };
            case 4: return new int[][] {
                    {102, 24 - 9},
                    {120, 24 - 9},
                    {102, 24 + 9},
                    {120, 24 + 9}
            };
            case 5: return new int[][] {
                    {102, 24 - 9}, {120, 24 - 9},
                    {102, 24 + 9}, {120, 24 + 9},
                    {138, 24},
            };
            case 6: return new int[][] {
                    {102, 6}, {120, 6},
                    {102, 24}, {120, 24},
                    {102, 42}, {120, 42},
            };
            case 7: return new int[][] {
                    {102, 6}, {120, 6},
                    {102, 24}, {120, 24},
                    {102, 42}, {120, 42},
                    {138, 24},
            };
            case 8: return new int[][] {
                    {102, 6}, {120, 6},
                    {102, 24}, {120, 24},
                    {102, 42}, {120, 42},
                    {138, 24}, {138, 42},
            };
        }
        return new int[count][2];
    }
}
