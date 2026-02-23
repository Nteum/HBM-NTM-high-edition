package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.HBMLang;
import com.hbm.blockentity.machine.AssemblerEntity;
import com.hbm.gui.menu.AssemblerMenu;
import com.hbm.gui.recipebook.AssemblerBookComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.FurnaceMenu;

public class AssemblerGui extends BaseMachineGui<AssemblerMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/gui_assembler.png");
    private static final ResourceLocation TEMPLATE_BUTTON_TEXTURE = HBM.rl("textures/gui/button_machine.png");
    public final AssemblerBookComponent recipeBookComponent;
    private boolean widthTooNarrow;
    public AssemblerGui(AssemblerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = 222;
        this.recipeBookComponent = new AssemblerBookComponent();
    }

    @Override
    protected void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
//        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        this.addRenderableWidget(new ImageButton(this.leftPos + 78, this.topPos + 53, 20, 18, 0, 0, 19, TEMPLATE_BUTTON_TEXTURE, (button) -> {
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            button.setPosition(this.leftPos + 78, this.topPos + 53);
        }));
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
            this.recipeBookComponent.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        } else {
            this.recipeBookComponent.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
            this.recipeBookComponent.renderGhostRecipe(pGuiGraphics, this.leftPos, this.topPos, true, pPartialTick);
        }

        if (isMouseOver(pMouseX,pMouseY)){
            pGuiGraphics.drawString(font, Component.translatable(com.hbm.HBMLang.TOOLTIP_ENERGY.key(), menu.getEnergy()), pMouseX, pMouseY, 4210752);
        }
        renderTooltip(pGuiGraphics,pMouseX,pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        //渲染背景图
        showBgTexture(pGuiGraphics, TEXTURE);
        //进度条
        if (menu.getProgress() != 0) pGuiGraphics.blit(TEXTURE,leftPos+45,topPos+82,2,222, (int) (menu.getProgress()),32);
        //电池条
        int colorPart = (int) (menu.getEnergyRate()*52);
        pGuiGraphics.blit(TEXTURE,leftPos+116, topPos+70-colorPart,176,52-colorPart, 16,colorPart);
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
        if (isHovering(45, 82, 100, 32, pX, pY)){
            MutableComponent component = HBMLang.GUI_TOOLTIP_PROGRESS.translate(menu.getProgress());
            pGuiGraphics.renderTooltip(font, component, pX, pY);
        }
        if (isHovering(116, 18, 16, 52, pX, pY)){
            MutableComponent component = HBMLang.GUI_TOOLTIP_ENERGY.translate(menu.getEnergy(), menu.getEnergyCapacity());
            pGuiGraphics.renderTooltip(font, component, pX, pY);
        }
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        if (pMouseX >= leftPos+116&&pMouseX<=leftPos+132&&pMouseY>topPos+18&&pMouseY<=topPos+70){
            return true;
        }
        return false;
    }
}
