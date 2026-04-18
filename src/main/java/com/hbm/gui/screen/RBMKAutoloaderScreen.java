package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.RBMKAutoloaderMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RBMKAutoloaderScreen extends AbstractContainerScreen<RBMKAutoloaderMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(HBM.MODID, "textures/gui/machine/gui_autoloader.png");
    private static final int BUTTON_Y = 36;
    private static final int MINUS_X = 74;
    private static final int PLUS_X = 90;
    private static final int BUTTON_SIZE = 12;

    public RBMKAutoloaderScreen(RBMKAutoloaderMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 182;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, (this.imageWidth - this.font.width(this.title)) / 2, 6, 0xFFFFFF, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.inventoryLabelY, 0x404040, false);
        String percent = menu.getCycle() + "%";
        graphics.drawString(this.font, percent, (this.imageWidth - this.font.width(percent)) / 2, 23, 0x00FF00, false);

        int statusY = 82;
        Component linked = menu.isLinked()
                ? Component.translatable("gui.hbm.rbmk.status.running")
                : Component.translatable("gui.hbm.rbmk.status.offline");
        Component working = menu.isWorking()
                ? Component.translatable("gui.hbm.rbmk.autoloader.cycle_active")
                : Component.translatable("gui.hbm.rbmk.autoloader.cycle_idle");
        graphics.drawString(this.font, linked, 17, statusY, menu.isLinked() ? 0x00FF00 : 0xFF4040, false);
        graphics.drawString(this.font, working, 17, statusY + 10, menu.isWorking() ? 0x00FF00 : 0xAAAAAA, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);

        if (isHoveringButton(mouseX, mouseY, MINUS_X)) {
            graphics.renderTooltip(this.font, Component.translatable("gui.hbm.rbmk.autoloader.cycle_minus"), mouseX, mouseY);
        } else if (isHoveringButton(mouseX, mouseY, PLUS_X)) {
            graphics.renderTooltip(this.font, Component.translatable("gui.hbm.rbmk.autoloader.cycle_plus"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (isHoveringButton(mouseX, mouseY, MINUS_X)) {
                sendButton(RBMKAutoloaderMenu.BUTTON_MINUS);
                return true;
            }
            if (isHoveringButton(mouseX, mouseY, PLUS_X)) {
                sendButton(RBMKAutoloaderMenu.BUTTON_PLUS);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isHoveringButton(double mouseX, double mouseY, int localX) {
        int x = this.leftPos + localX;
        int y = this.topPos + BUTTON_Y;
        return mouseX >= x && mouseX < x + BUTTON_SIZE && mouseY >= y && mouseY < y + BUTTON_SIZE;
    }

    private void sendButton(int id) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }
}
