package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.RBMKBaseMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * RBMK 核心控制台：聚焦安全状态与紧急 AZ-5 操作，所有详情可折叠显示。
 */
public class RBMKBaseScreen extends AbstractRBMKScreen<RBMKBaseMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(HBM.MODID, "textures/gui/rbmk/rbmk_console.png");

    public RBMKBaseScreen(RBMKBaseMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 256;
        this.imageHeight = 256;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected boolean hasColumnData() {
        return menu.hasColumnData();
    }

    @Override
    protected RBMKReadings primaryReadings() {
        return new RBMKReadings(menu.getHeat(), menu.getMeltdownThreshold(),
                menu.getEnergyStored(), menu.getEnergyCapacity(), menu.getWaterAmount(), menu.getSteamAmount());
    }

    @Override
    protected void renderControlPanel(GuiGraphics graphics, RBMKReadings readings) {
        int y = drawSectionHeader(graphics, CONTROL_X, CONTROL_Y, Component.translatable("gui.hbm.rbmk.section.control"));
        Component nextAction;
        int actionColor;
        if (readings.coolantMb() < 2_000) {
            nextAction = Component.translatable("gui.hbm.rbmk.action.coolant");
            actionColor = 0xFF6666;
        } else if (readings.energyCapacity() > 0 && readings.energyStored() > readings.energyCapacity() * 0.85F) {
            nextAction = Component.translatable("gui.hbm.rbmk.action.dump_power");
            actionColor = 0xFFC107;
        } else {
            nextAction = Component.translatable("gui.hbm.rbmk.action.normal");
            actionColor = 0x9ECFFF;
        }
        y = drawValueLine(graphics, CONTROL_X + 4, y, nextAction, actionColor);
        drawValueLine(graphics, CONTROL_X + 4, y,
                Component.translatable("gui.hbm.rbmk.control.az5_hint"),
                0xFFC107);
    }

    @Override
    protected void renderAdvancedDetails(GuiGraphics graphics, RBMKReadings readings, int x, int startY) {
        int y = drawValueLine(graphics, x, startY,
                Component.translatable("gui.hbm.rbmk.control_local", menu.getLocalControlPercent()),
                0xFFD580);
        drawValueLine(graphics, x, y,
                Component.translatable("gui.hbm.rbmk.control_global", menu.getGlobalControlPercent()),
                0xFFD580);
    }

    @Override
    protected void updateInteractionState(boolean hasData) {
        // Base column GUI is telemetry-only; no interactive controls.
    }
}
