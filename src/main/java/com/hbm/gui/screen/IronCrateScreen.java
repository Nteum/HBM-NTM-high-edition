package com.hbm.gui.screen;

import com.hbm.gui.menu.IronCrateMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class IronCrateScreen extends AbstractContainerScreen<IronCrateMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private static final int ROWS = 4;

    public IronCrateScreen(IronCrateMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 114 + ROWS * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int topHeight = ROWS * 18 + 17;
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, topHeight);
        graphics.blit(TEXTURE, this.leftPos, this.topPos + topHeight, 0, 126, this.imageWidth, 96);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
