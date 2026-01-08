package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.ICFPressMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class ICFPressScreen extends BaseMachineGui<ICFPressMenu> {

    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_icf_press.png");
    private static final int TANK_CAPACITY = 16_000;

    public ICFPressScreen(ICFPressMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 186;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        showBgTexture(graphics, TEXTURE);
        drawMuonBar(graphics, leftPos + 26, topPos + 20);
        drawFluidBar(graphics, leftPos + 62, topPos + 18, menu.getLeftFluid());
        drawFluidBar(graphics, leftPos + 134, topPos + 18, menu.getRightFluid());
    }

    private void drawMuonBar(GuiGraphics graphics, int x, int y) {
        int height = 50;
        int filled = menu.getMuonCharge() * height / 16;
        graphics.fill(x, y + (height - filled), x + 12, y + height, 0xFF7FE1FF);
    }

    private void drawFluidBar(GuiGraphics graphics, int x, int y, int amount) {
        int height = 52;
        int filled = Math.min(height, (int) ((double) amount / (double) TANK_CAPACITY * height));
        if (filled > 0) {
            graphics.fill(x, y + (height - filled), x + 12, y + height, 0xAA00AEEF);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        drawCustomInfoStat(graphics, mouseX, mouseY, leftPos + 26, topPos + 20, 12, 50,
                List.of(Component.translatable("gui.hbm.icf_press.muon", menu.getMuonCharge(), 16)));
        drawCustomInfoStat(graphics, mouseX, mouseY, leftPos + 62, topPos + 18, 12, 52,
                List.of(Component.translatable("gui.hbm.icf_press.left", menu.getLeftFluid(), TANK_CAPACITY)));
        drawCustomInfoStat(graphics, mouseX, mouseY, leftPos + 134, topPos + 18, 12, 52,
                List.of(Component.translatable("gui.hbm.icf_press.right", menu.getRightFluid(), TANK_CAPACITY)));
    }
}
