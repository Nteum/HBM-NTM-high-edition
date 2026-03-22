package com.hbm.gui.screen.page.recipe;

import com.hbm.HBM;
import com.hbm.Inventory.recipe.alloy.CrucibleRecipe;
import com.hbm.registries.HBMMatters;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
@OnlyIn(Dist.CLIENT)
public class RecipePageCrucible<T extends CrucibleRecipe> extends RecipePage<T> {
    static ResourceLocation GRAY_ICON = HBM.rl("textures/gui/fluids/custom_lava.png");
    @Override
    List<Component> genTooltip(T recipe, RegistryAccess reg) {
        return recipe.description();
    }

    @Override
    void renderRecipe(GuiGraphics pGuiGraphics, T recipe, int posX, int posY) {
        List<FluidStack> output = recipe.getOutput();
        int accFluidHeight = 0;
        int lenPart = Math.max(1, 16 / output.size());
        for (int i = 0; i < output.size(); i++) {
            int color = HBMMatters.getMatterFromFluid(output.get(i)).moltenColor;
            int lenRender = i == output.size() - 1 ? 16 - accFluidHeight : lenPart;
            pGuiGraphics.setColor(FastColor.ARGB32.red(color) / 255.0f, FastColor.ARGB32.green(color) / 255.0f, FastColor.ARGB32.blue(color) / 255.0f, 0.3f);
            pGuiGraphics.blit(GRAY_ICON, posX + accFluidHeight, posY, 0, 0, lenRender, 16);
            accFluidHeight += lenRender;
        }
        pGuiGraphics.setColor(1, 1, 1, 1);
    }

    @Override
    boolean filterRecipeName(T recipe, String query) {
        for (FluidStack fluidStack : recipe.getOutput()) {
            if (fluidStack.getDisplayName().getString().toLowerCase().contains(query)) return true;
        }
        return false;
    }
}
