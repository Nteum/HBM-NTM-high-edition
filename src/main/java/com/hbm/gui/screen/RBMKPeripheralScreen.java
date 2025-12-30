package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.RBMKPeripheralMenu;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RBMKPeripheralScreen extends AbstractRBMKScreen<RBMKPeripheralMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(HBM.MODID, "textures/gui/rbmk/rbmk_console.png");
    private final boolean consoleScreen;
    private Button az5Button;

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
            int buttonX = guiX(CONTROL_X + 4);
            int buttonY = guiY(CONTROL_Y + 16);
            az5Button = Button.builder(Component.translatable("gui.hbm.rbmk.az5"), b -> sendAz5())
                    .bounds(buttonX, buttonY, 90, 20)
                    .build();
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
        if (consoleScreen) {
            y = drawValueLine(graphics, CONTROL_X + 4, y,
                    Component.translatable("gui.hbm.rbmk.console.az5_hint"),
                    0xFF7777);
        } else {
            y = drawValueLine(graphics, CONTROL_X + 4, y,
                    Component.translatable("gui.hbm.rbmk.peripheral_kind", menu.getPeripheralType().displayName()),
                    0xFFFFFF);
            drawValueLine(graphics, CONTROL_X + 4, y,
                    Component.translatable("gui.hbm.rbmk.control.no_manual"),
                    0xB0B0B0);
        }
    }

    @Override
    protected void renderAdvancedDetails(GuiGraphics graphics, RBMKReadings readings, int x, int startY) {
        int y = drawValueLine(graphics, x, startY,
                Component.translatable("gui.hbm.rbmk.control_local", menu.getLocalControlPercent()),
                0xFFD580);
        y = drawValueLine(graphics, x, y,
                Component.translatable("gui.hbm.rbmk.control_global", menu.getGlobalControlPercent()),
                0xFFD580);
        drawValueLine(graphics, x, y,
                Component.translatable("gui.hbm.rbmk.columns_online", menu.getColumnCount()),
                0xFFFFFF);
    }

    @Override
    protected void updateInteractionState(boolean hasData) {
        if (az5Button != null) {
            az5Button.active = hasData;
            az5Button.visible = true;
        }
    }
}
