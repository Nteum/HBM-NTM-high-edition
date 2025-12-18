package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.RBMKBaseMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Locale;

/**
 * RBMK 核心状态界面：显示热量/冷却/控制棒情况，并提供 AZ-5 停堆按钮。
 */
public class RBMKBaseScreen extends AbstractContainerScreen<RBMKBaseMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(HBM.MODID, "textures/gui/gui_rbmk.png");
    private Button az5Button;

    public RBMKBaseScreen(RBMKBaseMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 212;
        this.imageHeight = 192;
    }

    @Override
    protected void init() {
        super.init();
        int buttonX = this.leftPos + 140;
        int buttonY = this.topPos + 24;
        az5Button = Button.builder(Component.translatable("gui.hbm.rbmk.az5"), b -> sendAz5())
                .bounds(buttonX, buttonY, 60, 20)
                .build();
        addRenderableWidget(az5Button);
    }

    private void sendAz5() {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        int textX = this.leftPos + 20;
        int textY = this.topPos + 20;
        if (!menu.hasColumnData()) {
            graphics.drawString(this.font, Component.translatable("gui.hbm.rbmk.no_column"), textX, textY, 0xFF5555, false);
            return;
        }

        String heatLine = Component.translatable("gui.hbm.rbmk.heat",
                formatOneDecimal(menu.getHeat()), formatOneDecimal(menu.getMeltdownThreshold())).getString();
        String energyLine = Component.translatable("gui.hbm.rbmk.energy",
                menu.getEnergyStored(), menu.getEnergyCapacity()).getString();
        String waterLine = Component.translatable("gui.hbm.rbmk.coolant", menu.getWaterAmount()).getString();
        String steamLine = Component.translatable("gui.hbm.rbmk.steam", menu.getSteamAmount()).getString();
        String localControl = Component.translatable("gui.hbm.rbmk.control_local", menu.getLocalControlPercent()).getString();
        String globalControl = Component.translatable("gui.hbm.rbmk.control_global", menu.getGlobalControlPercent()).getString();

        graphics.drawString(this.font, Component.literal(heatLine), textX, textY, 0xFFAA33, false);
        graphics.drawString(this.font, Component.literal(energyLine), textX, textY + 12, 0x66FF66, false);
        graphics.drawString(this.font, Component.literal(waterLine), textX, textY + 24, 0x66CCFF, false);
        graphics.drawString(this.font, Component.literal(steamLine), textX, textY + 36, 0xAAAADD, false);
        graphics.drawString(this.font, Component.literal(localControl), textX, textY + 48, 0xFFD580, false);
        graphics.drawString(this.font, Component.literal(globalControl), textX, textY + 60, 0xFFD580, false);
    }

    private static String formatOneDecimal(float value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
