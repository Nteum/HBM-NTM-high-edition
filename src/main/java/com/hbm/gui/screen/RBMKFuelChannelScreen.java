package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.RBMKFuelChannelMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class RBMKFuelChannelScreen extends AbstractContainerScreen<RBMKFuelChannelMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(HBM.MODID, "textures/gui/reactors/gui_rbmk_element.png");

    private static final int COOLANT_BAR_X = 36;
    private static final int COOLANT_BAR_Y = 21;
    private static final int COOLANT_BAR_WIDTH = 17;
    private static final int COOLANT_BAR_HEIGHT = 64;

    private static final int STEAM_BAR_X = 123;
    private static final int STEAM_BAR_Y = 21;
    private static final int STEAM_BAR_WIDTH = 20;
    private static final int STEAM_BAR_HEIGHT = 64;

    private static final int CORE_PANEL_X = 60;
    private static final int CORE_PANEL_Y = 21;
    private static final int CORE_PANEL_WIDTH = 56;
    private static final int CORE_PANEL_HEIGHT = 64;

    public RBMKFuelChannelScreen(RBMKFuelChannelMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 186;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        if (menu.hasColumnData()) {
            drawVerticalBar(graphics, COOLANT_BAR_X, COOLANT_BAR_Y, COOLANT_BAR_WIDTH, COOLANT_BAR_HEIGHT,
                    menu.getCoolantRatio(), 0xFF66CCFF);
            drawVerticalBar(graphics, STEAM_BAR_X, STEAM_BAR_Y, STEAM_BAR_WIDTH, STEAM_BAR_HEIGHT,
                    menu.getSteamRatio(), 0xFFAAAADD);
        }
    }

    private void drawVerticalBar(GuiGraphics graphics, int localX, int localY, int width, int height, float ratio, int color) {
        int filled = Math.round(Math.max(0.0F, Math.min(1.0F, ratio)) * height);
        if (filled <= 0) {
            return;
        }
        int x = this.leftPos + localX;
        int y = this.topPos + localY + height - filled;
        graphics.fill(x, y, x + width, this.topPos + localY + height, color);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // 旧版 RBMK 燃料通道 GUI 无文本叠加
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
        renderBarTooltips(graphics, mouseX, mouseY);
    }

    private void renderBarTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!menu.hasColumnData()) {
            return;
        }
        if (isHovering(COOLANT_BAR_X, COOLANT_BAR_Y, COOLANT_BAR_WIDTH, COOLANT_BAR_HEIGHT, mouseX, mouseY)) {
            Component tooltip = Component.translatable("gui.hbm.rbmk.coolant", menu.getWaterAmount());
            graphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
            return;
        }
        if (isHovering(STEAM_BAR_X, STEAM_BAR_Y, STEAM_BAR_WIDTH, STEAM_BAR_HEIGHT, mouseX, mouseY)) {
            Component tooltip = Component.translatable("gui.hbm.rbmk.steam", menu.getSteamAmount());
            graphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
            return;
        }
        if (isHovering(CORE_PANEL_X, CORE_PANEL_Y, CORE_PANEL_WIDTH, CORE_PANEL_HEIGHT, mouseX, mouseY)) {
            List<Component> lines = List.of(
                    Component.translatable("gui.hbm.rbmk.heat",
                            formatOneDecimal(menu.getHeat()), formatOneDecimal(menu.getMeltdownThreshold())),
                    Component.translatable("gui.hbm.rbmk.fuel_progress",
                            menu.getBurnTimeRemaining(), menu.getBurnTimeTotal(), menu.getBurnProgressPercent())
            );
            graphics.renderTooltip(this.font, lines, Optional.empty(), mouseX, mouseY);
        }
    }

    private static String formatOneDecimal(float value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

}
