package com.hbm.gui.recipebook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class AssemblerBookComponent  {
    protected static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    private int xOffset;
    private int width;
    private int height;
    private boolean visible;
    private boolean widthTooNarrow;
    private HBMClientRecipeBook book;
    private AbstractContainerMenu menu;
    private Minecraft minecraft;
    private final RecipeBookPage recipeBookPage = new RecipeBookPage();
    //初始化
    public void init(int pWidth, int pHeight, Minecraft pMinecraft, boolean widthTooNarrow, AbstractContainerMenu menu){
        this.width = pWidth;
        this.height = pHeight;
        this.minecraft = pMinecraft;
        this.book = new HBMClientRecipeBook();
        this.visible = isVisibleAccordingToBookData();
        this.widthTooNarrow = widthTooNarrow;
        this.menu = menu;
    }
    public void initVisuals() {
        this.xOffset = this.widthTooNarrow ? 0 : 86;
    }
    public void toggleVisibility() {
        this.setVisible(!this.isVisible());
    }
    protected void setVisible(boolean pVisible) {
        if (pVisible) {
            this.initVisuals();
        }

        this.visible = pVisible;
        this.book.setOpen(null, pVisible);
        if (!pVisible) {
            this.recipeBookPage.setInvisible();
        }

        this.sendUpdateSettings();
    }
    protected void sendUpdateSettings() {
        if (this.minecraft.getConnection() != null) {
        }
    }
    //
    public int updateScreenPosition(int pWidth, int pImageWidth) {
        int i;
        if (this.isVisible() && !this.widthTooNarrow) {
            i = 177 + (pWidth - pImageWidth - 200) / 2;
        } else {
            i = (pWidth - pImageWidth) / 2;
        }

        return i;
    }
    public boolean isVisible() {
        return this.visible;
    }
    private boolean isVisibleAccordingToBookData() {
        return this.book.isOpen(null);
    }
    //=========================================
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.isVisible()){
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
            int i = (this.width - 147) / 2 - this.xOffset;
            int j = (this.height - 166) / 2;
            pGuiGraphics.blit(RECIPE_BOOK_LOCATION, i, j, 1, 1, 147, 166);
            pGuiGraphics.pose().popPose();
        }
    }
    public void renderGhostRecipe(GuiGraphics pGuiGraphics, int pLeftPos, int pTopPos, boolean p_283495_, float pPartialTick) {
    }
}
