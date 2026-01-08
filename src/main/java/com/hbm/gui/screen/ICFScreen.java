package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.ICFMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class ICFScreen extends BaseMachineGui<ICFMenu> {

    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/reactors/gui_icf.png");

    private static final int COOLANT_CAPACITY = 512_000;

    public ICFScreen(ICFMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 248;
        this.imageHeight = 222;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = leftPos + 10;
        this.titleLabelY = topPos + 6;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        showBgTexture(graphics, TEXTURE);
        drawHeatGauge(graphics);
        drawFluidBar(graphics, leftPos + 44, topPos + 18, menu.getCoolant(), COOLANT_CAPACITY);
        drawFluidBar(graphics, leftPos + 188, topPos + 18, menu.getHotCoolant(), COOLANT_CAPACITY);
    }

    private void drawHeatGauge(GuiGraphics graphics) {
        int barHeight = Math.min(70, Math.max(0, menu.getHeat() / 10));
        if (barHeight > 0) {
            graphics.fill(leftPos + 196, topPos + 98 - barHeight, leftPos + 201, topPos + 98, 0xFF00AF00);
        }
    }

    private void drawFluidBar(GuiGraphics graphics, int x, int y, int amount, int capacity) {
        if (capacity <= 0) {
            return;
        }
        int height = 70;
        int filled = (int) ((double) amount / (double) capacity * height);
        if (filled > 0) {
            graphics.fill(x, y + height - filled, x + 16, y + height, 0xAA1E90FF);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 10, 6, 0x404040, false);
        graphics.drawString(this.font, Component.translatable("container.inventory"), 44, this.imageHeight - 93, 0x404040, false);
        drawCustomInfoStat(graphics, mouseX, mouseY, leftPos + 196, topPos + 28, 18, 70,
                List.of(
                        Component.translatable("gui.hbm.icf.heat", menu.getHeat() / 10),
                        Component.translatable("gui.hbm.icf.laser", menu.getLaser() / 10)
                ));
        drawCustomInfoStat(graphics, mouseX, mouseY, leftPos + 44, topPos + 18, 16, 70,
                List.of(Component.translatable("gui.hbm.icf.coolant", menu.getCoolant(), COOLANT_CAPACITY)));
        drawCustomInfoStat(graphics, mouseX, mouseY, leftPos + 188, topPos + 18, 16, 70,
                List.of(Component.translatable("gui.hbm.icf.coolant_hot", menu.getHotCoolant(), COOLANT_CAPACITY)));
    }
}
