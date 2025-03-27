package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.AssemblerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.FurnaceMenu;

public class AssemblerGui extends AbstractContainerScreen<AssemblerMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/gui_assembler.png");
    private static final ResourceLocation TEMPLATE_BUTTON_TEXTURE = HBM.rl("textures/gui/button_machine.png");
    public AssemblerGui(AssemblerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        this.imageHeight = 222;
        this.topPos -= 28;
        this.addRenderableWidget(new ImageButton(this.leftPos + 79, this.topPos + 52, 20, 18, 0, 0, 19, TEMPLATE_BUTTON_TEXTURE, (button) -> {

        }));
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        renderTooltip(pGuiGraphics,pMouseX,pMouseY);
        if (isMouseOver(pMouseX,pMouseY)){
            pGuiGraphics.drawString(font,Component.literal("Energy: "+menu.getEnergy()),pMouseX,pMouseY,4210752);
        }
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        //渲染背景图
        pGuiGraphics.blit(TEXTURE,leftPos,topPos,0,0,imageWidth,imageHeight);
        //进度条
        if (menu.getProgress() != 0)
            pGuiGraphics.blit(TEXTURE,leftPos+45,topPos+82,2,222, (int) (menu.getProgress()*83),32);
        //电池条
        int colorPart = (int) (menu.getEnergyRate()*52);
        pGuiGraphics.blit(TEXTURE,leftPos+116, topPos+70-colorPart,176,52-colorPart, 16,colorPart);
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        if (pMouseX >= leftPos+116&&pMouseX<=leftPos+132&&pMouseY>topPos+18&&pMouseY<=topPos+70){
            return true;
        }
        return false;
    }
}
