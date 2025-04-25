package com.hbm.gui.recipebook;

import net.minecraft.stats.RecipeBook;
import net.minecraft.world.inventory.RecipeBookType;

public class HBMClientRecipeBook extends RecipeBook {
    private boolean isOpen;
    @Override
    public boolean isOpen(RecipeBookType pBookType) {
        return isOpen;
    }

    @Override
    public void setOpen(RecipeBookType pBookType, boolean pOpen) {
        this.isOpen = pOpen;
    }
}
