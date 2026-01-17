package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.RBMKControlRodMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RBMKControlRodScreen extends AbstractContainerScreen<RBMKControlRodMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(HBM.MODID, "textures/gui/reactors/gui_rbmk_control.png");
    private static final int GAUGE_X = 75;
    private static final int GAUGE_Y = 29;
    private static final int GAUGE_WIDTH = 8;
    private static final int GAUGE_HEIGHT = 56;
    private static final int LEVEL_BUTTON_X = 118;
    private static final int LEVEL_BUTTON_Y = 26;
    private static final int LEVEL_BUTTON_WIDTH = 30;
    private static final int LEVEL_BUTTON_HEIGHT = 10;
    private static final int COLOR_BUTTON_X = 28;
    private static final int COLOR_BUTTON_Y = 26;
    private static final int COLOR_BUTTON_WIDTH = 12;
    private static final int COLOR_BUTTON_HEIGHT = 10;
    private static final int BUTTON_SPACING = 11;

    public RBMKControlRodScreen(RBMKControlRodMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 186;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        if (isHoveringArea(GAUGE_X - 4, GAUGE_Y, GAUGE_WIDTH + 8, GAUGE_HEIGHT, mouseX, mouseY)) {
            graphics.renderTooltip(this.font, Component.literal(menu.getInsertionPercent() + "%"), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        int filled = (int) (GAUGE_HEIGHT * (1.0F - menu.getInsertionPercent() / 100.0F));
        if (filled > 0) {
            graphics.blit(TEXTURE, this.leftPos + GAUGE_X, this.topPos + GAUGE_Y, 176, GAUGE_HEIGHT - filled, GAUGE_WIDTH, filled);
        }

        int colorIndex = menu.getSelectedColor();
        if (colorIndex >= 0) {
            graphics.blit(TEXTURE,
                    this.leftPos + COLOR_BUTTON_X,
                    this.topPos + COLOR_BUTTON_Y + colorIndex * BUTTON_SPACING,
                    184, colorIndex * COLOR_BUTTON_HEIGHT,
                    COLOR_BUTTON_WIDTH, COLOR_BUTTON_HEIGHT);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // 旧版 RBMK 控制棒 GUI 无标题/文本叠加
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int levelButton = getButtonIndex(mouseX, mouseY, LEVEL_BUTTON_X, LEVEL_BUTTON_Y, LEVEL_BUTTON_WIDTH, LEVEL_BUTTON_HEIGHT);
            if (menu.hasControlRodItem() && !menu.isAz5CoolingDown()
                    && levelButton >= 0 && levelButton < RBMKControlRodMenu.LEVEL_COUNT) {
                sendMenuButton(RBMKControlRodMenu.levelButtonId(levelButton));
                return true;
            }

            int colorButton = getButtonIndex(mouseX, mouseY, COLOR_BUTTON_X, COLOR_BUTTON_Y, COLOR_BUTTON_WIDTH, COLOR_BUTTON_HEIGHT, RBMKControlRodMenu.COLOR_COUNT);
            if (colorButton >= 0 && colorButton < RBMKControlRodMenu.COLOR_COUNT) {
                sendMenuButton(RBMKControlRodMenu.colorButtonId(colorButton));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int getButtonIndex(double mouseX, double mouseY, int x, int y, int width, int height) {
        return getButtonIndex(mouseX, mouseY, x, y, width, height, RBMKControlRodMenu.LEVEL_COUNT);
    }

    private int getButtonIndex(double mouseX, double mouseY, int x, int y, int width, int height, int count) {
        for (int i = 0; i < count; i++) {
            int bx = this.leftPos + x;
            int by = this.topPos + y + i * BUTTON_SPACING;
            if (mouseX >= bx && mouseX < bx + width && mouseY >= by && mouseY < by + height) {
                return i;
            }
        }
        return -1;
    }

    private boolean isHoveringArea(int x, int y, int width, int height, double mouseX, double mouseY) {
        int bx = this.leftPos + x;
        int by = this.topPos + y;
        return mouseX >= bx && mouseX < bx + width && mouseY >= by && mouseY < by + height;
    }

    private void sendMenuButton(int id) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }
}
