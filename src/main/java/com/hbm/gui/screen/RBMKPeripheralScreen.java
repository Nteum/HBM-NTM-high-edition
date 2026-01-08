package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.RBMKPeripheralMenu;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RBMKPeripheralScreen extends AbstractRBMKScreen<RBMKPeripheralMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(HBM.MODID, "textures/gui/reactors/gui_rbmk_console.png");
    private static final int AZ5_BUTTON_X = 26;
    private static final int AZ5_BUTTON_Y = 143;
    private static final int AZ5_BUTTON_WIDTH = 28;
    private static final int AZ5_BUTTON_HEIGHT = 15;
    private final boolean consoleScreen;
    private AbstractButton az5Button;

    public RBMKPeripheralScreen(RBMKPeripheralMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 256;
        this.imageHeight = 256;
        this.consoleScreen = menu.getPeripheralType() == RBMKPeripheralType.CONSOLE;
    }

    @Override
    protected void init() {
        super.init();
        if (consoleScreen) {
            int buttonX = guiX(AZ5_BUTTON_X);
            int buttonY = guiY(AZ5_BUTTON_Y);
            az5Button = new InvisibleButton(buttonX, buttonY, AZ5_BUTTON_WIDTH, AZ5_BUTTON_HEIGHT, this::sendAz5);
            addRenderableWidget(az5Button);
        }
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
        y = drawValueLine(graphics, CONTROL_X, y,
                Component.translatable("gui.hbm.rbmk.peripheral_kind", menu.getPeripheralType().displayName()), SECTION_COLOR);
        y = drawValueLine(graphics, CONTROL_X, y,
                Component.translatable("gui.hbm.rbmk.control_local", menu.getLocalControlPercent()), SECTION_COLOR);
        y = drawValueLine(graphics, CONTROL_X, y,
                Component.translatable("gui.hbm.rbmk.control_global", menu.getGlobalControlPercent()), SECTION_COLOR);
        Component consoleHint = consoleScreen
                ? Component.translatable("gui.hbm.rbmk.console.az5_hint")
                : Component.translatable("gui.hbm.rbmk.control.no_manual");
        drawValueLine(graphics, CONTROL_X, y, consoleHint, SECTION_COLOR);
    }

    @Override
    protected void renderAdvancedDetails(GuiGraphics graphics, RBMKReadings readings, int x, int startY) {
        int y = startY;
        y = drawValueLine(graphics, x, y,
                Component.translatable("gui.hbm.rbmk.columns_online", menu.getColumnCount()), SECTION_COLOR);
        if (menu.getColumnCount() <= 0) {
            y = drawValueLine(graphics, x, y, Component.translatable("gui.hbm.rbmk.console.idle"), SECTION_COLOR);
        }
        drawValueLine(graphics, x, y, recommendAction(readings, menu.getLocalControlPercent(), menu.getGlobalControlPercent()), SECTION_COLOR);
    }

    @Override
    protected void updateInteractionState(boolean hasData) {
        if (az5Button != null) {
            az5Button.active = hasData && consoleScreen;
            az5Button.visible = hasData && consoleScreen;
        }
    }

    private static final class InvisibleButton extends AbstractButton {
        private final Runnable onPress;

        private InvisibleButton(int x, int y, int width, int height, Runnable onPress) {
            super(x, y, width, height, Component.empty());
            this.onPress = onPress;
        }

        @Override
        public void onPress() {
            onPress.run();
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            // Intentionally invisible; click region aligns with legacy texture button.
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            // No narration for invisible legacy button.
        }
    }
}
