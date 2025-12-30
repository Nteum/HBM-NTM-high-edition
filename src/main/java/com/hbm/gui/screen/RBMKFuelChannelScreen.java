package com.hbm.gui.screen;

import com.hbm.gui.menu.RBMKFuelChannelMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.Locale;

public class RBMKFuelChannelScreen extends AbstractContainerScreen<RBMKFuelChannelMenu> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");
    private static final int STATUS_PANEL_Y = 6;
    private static final int STATUS_PANEL_HEIGHT = 34;
    private static final int ADVANCED_PANEL_Y = 78;
    private static final int ADVANCED_PANEL_HEIGHT = 32;
    private static final int PANEL_MARGIN = 7;

    private static final int HEAT_BAR_X = 22;
    private static final int HEAT_BAR_Y = 18;
    private static final int HEAT_BAR_WIDTH = 6;
    private static final int HEAT_BAR_HEIGHT = 26;

    private static final int FUEL_BAR_X = 46;
    private static final int FUEL_BAR_Y = 66;
    private static final int FUEL_BAR_WIDTH = 84;
    private static final int FUEL_BAR_HEIGHT = 6;

    public RBMKFuelChannelScreen(RBMKFuelChannelMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        drawPanel(graphics, STATUS_PANEL_Y, STATUS_PANEL_HEIGHT, 0x6C111111);
        drawPanel(graphics, ADVANCED_PANEL_Y, ADVANCED_PANEL_HEIGHT, 0x55131313);
        drawFuelBar(graphics);
        drawHeatBar(graphics);
    }

    private void drawPanel(GuiGraphics graphics, int localY, int height, int color) {
        int left = this.leftPos + PANEL_MARGIN;
        int right = this.leftPos + this.imageWidth - PANEL_MARGIN;
        int top = this.topPos + localY;
        graphics.fill(left, top, right, top + height, color);
    }

    // 燃料槽进度条，展示燃烧剩余时间
    private void drawFuelBar(GuiGraphics graphics) {
        int x = this.leftPos + FUEL_BAR_X;
        int y = this.topPos + FUEL_BAR_Y;
        graphics.fill(x, y, x + FUEL_BAR_WIDTH, y + FUEL_BAR_HEIGHT, 0xFF0F0F0F);
        int filled = Math.round(menu.getFuelProgress() * (FUEL_BAR_WIDTH - 2));
        if (filled > 0) {
            graphics.fill(x + 1, y + 1, x + 1 + filled, y + FUEL_BAR_HEIGHT - 1, 0xFFE5A13A);
        }
    }

    // 热量条，显示当前温度占熔毁阈值的百分比
    private void drawHeatBar(GuiGraphics graphics) {
        int x = this.leftPos + HEAT_BAR_X;
        int y = this.topPos + HEAT_BAR_Y;
        graphics.fill(x, y, x + HEAT_BAR_WIDTH, y + HEAT_BAR_HEIGHT, 0xFF0F0F0F);
        int pixels = Math.round(menu.getHeatRatio() * (HEAT_BAR_HEIGHT - 2));
        if (pixels > 0) {
            int color = menu.getHeatRatio() >= 0.9F ? 0xFFCC3333 : 0xFFF6B74A;
            graphics.fill(x + 1, y + HEAT_BAR_HEIGHT - 1 - pixels, x + HEAT_BAR_WIDTH - 1, y + HEAT_BAR_HEIGHT - 1, color);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xE0E0E0, false);
        renderStatusSection(graphics);
        renderAdvancedSection(graphics);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, this.inventoryLabelY, 0xE0E0E0, false);
    }

    private void renderStatusSection(GuiGraphics graphics) {
        if (!menu.hasColumnData()) {
            graphics.drawString(this.font, Component.translatable("gui.hbm.rbmk.no_column"), 8, STATUS_PANEL_Y + 10, 0xFF5555, false);
            return;
        }
        int cursorY = STATUS_PANEL_Y + 10;
        Component statusText = Component.translatable("gui.hbm.rbmk.status",
                Component.translatable(menu.isBurning() ? "gui.hbm.rbmk.status.running" : "gui.hbm.rbmk.status.offline"));
        graphics.drawString(this.font, statusText, 32, cursorY, 0xFFFFFF, false);
        cursorY += 11;
        graphics.drawString(this.font,
                Component.translatable("gui.hbm.rbmk.heat",
                        formatOneDecimal(menu.getHeat()), formatOneDecimal(menu.getMeltdownThreshold())),
                32, cursorY, 0xFFDF814D, false);
        cursorY += 11;
        graphics.drawString(this.font,
                Component.translatable("gui.hbm.rbmk.energy",
                        formatEnergy(menu.getEnergyStored()), formatEnergy(menu.getEnergyCapacity())),
                32, cursorY, 0xFF72E072, false);

        int rightX = this.imageWidth - 72;
        int rightY = STATUS_PANEL_Y + 10;
        graphics.drawString(this.font,
                Component.translatable("gui.hbm.rbmk.coolant", menu.getWaterAmount()), rightX, rightY, 0xFF5CC5F2, false);
        rightY += 11;
        graphics.drawString(this.font,
                Component.translatable("gui.hbm.rbmk.steam", menu.getSteamAmount()), rightX, rightY, 0xFFB5B5FF, false);
        rightY += 11;
        graphics.drawString(this.font,
                Component.translatable("gui.hbm.rbmk.control_local", menu.getLocalControlPercent()), rightX, rightY, 0xFFCFCFCF, false);
        rightY += 11;
        graphics.drawString(this.font,
                Component.translatable("gui.hbm.rbmk.control_global", menu.getGlobalControlPercent()), rightX, rightY, 0xFFCFCFCF, false);
    }

    private void renderAdvancedSection(GuiGraphics graphics) {
        int cursorY = ADVANCED_PANEL_Y + 10;
        graphics.drawString(this.font,
                Component.translatable("gui.hbm.rbmk.section.advanced"),
                8, cursorY - 10, 0xE0E0E0, false);
        if (!menu.hasColumnData()) {
            graphics.drawString(this.font, Component.translatable("gui.hbm.rbmk.no_column"), 12, cursorY, 0xFF5555, false);
            return;
        }

        graphics.drawString(this.font,
                Component.translatable("gui.hbm.rbmk.heat_rate", formatOneDecimal(menu.getHeatPerSecond())),
                12, cursorY, 0xFFFFFF, false);
        cursorY += 11;
        graphics.drawString(this.font,
                Component.translatable("gui.hbm.rbmk.fuel_progress",
                        menu.getBurnTimeRemaining(), menu.getBurnTimeTotal(), menu.getBurnProgressPercent()),
                12, cursorY, 0xFFFFFF, false);
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
        if (isHovering(FUEL_BAR_X, FUEL_BAR_Y, FUEL_BAR_WIDTH, FUEL_BAR_HEIGHT, mouseX, mouseY)) {
            Component tooltip = Component.translatable("gui.hbm.rbmk.fuel_progress",
                    menu.getBurnTimeRemaining(), menu.getBurnTimeTotal(), menu.getBurnProgressPercent()).withStyle(ChatFormatting.GOLD);
            graphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
        if (isHovering(HEAT_BAR_X, HEAT_BAR_Y, HEAT_BAR_WIDTH, HEAT_BAR_HEIGHT, mouseX, mouseY)) {
            Component tooltip = Component.translatable("gui.hbm.rbmk.heat",
                    formatOneDecimal(menu.getHeat()), formatOneDecimal(menu.getMeltdownThreshold())).withStyle(ChatFormatting.RED);
            graphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    private static String formatOneDecimal(float value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private static String formatEnergy(int value) {
        return String.format(Locale.ROOT, "%,d", Math.max(0, value));
    }
}
