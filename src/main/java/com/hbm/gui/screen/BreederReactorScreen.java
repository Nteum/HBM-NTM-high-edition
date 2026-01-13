package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.BreederReactorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BreederReactorScreen extends BaseMachineGui<BreederReactorMenu> {

    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_breeder.png");

    public BreederReactorScreen(BreederReactorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        int progress = Math.min(70, Math.round(menu.getProgressPercent() * 0.7F));
        if (progress > 0) {
            graphics.blit(TEXTURE, leftPos + 53, topPos + 32, 176, 0, progress, 20);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        if (isHovering(88 - 8, 20, 16, 16, mouseX, mouseY)) {
            graphics.renderTooltip(font, Component.literal("Flux: " + menu.getFlux() + " n/t"), mouseX, mouseY);
        }
        if (isHovering(53, 32, 70, 20, mouseX, mouseY)) {
            graphics.renderTooltip(font, Component.literal("Progress: " + menu.getProgressPercent() + "%"), mouseX, mouseY);
        }
    }
}
