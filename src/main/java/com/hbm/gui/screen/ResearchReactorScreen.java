package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.ResearchReactorMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

public class ResearchReactorScreen extends BaseMachineGui<ResearchReactorMenu> {

    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/reactors/gui_research_reactor.png");
    private EditBox controlField;
    private int confirmationTimer;

    public ResearchReactorScreen(ResearchReactorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void init() {
        super.init();
        controlField = new EditBox(font, leftPos + 8, topPos + 99, 33, 16, Component.empty());
        controlField.setValue(String.valueOf(menu.getTargetPercent()));
        controlField.setBordered(false);
        controlField.setMaxLength(3);
        controlField.setTextColor(0x08FF00);
        addRenderableWidget(controlField);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        controlField.tick();
        if (!controlField.isFocused()) {
            controlField.setValue(String.valueOf(menu.getTargetPercent()));
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        if (menu.getControlPercent() <= 50) {
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    graphics.blit(TEXTURE, leftPos + 81 + 36 * x, topPos + 26 + 36 * y, 176, 0, 8, 8);
                }
            }
            for (int x = 0; x < 2; x++) {
                for (int y = 0; y < 2; y++) {
                    graphics.blit(TEXTURE, leftPos + 99 + 36 * x, topPos + 44 + 36 * y, 176, 0, 8, 8);
                }
            }
        }
        if (confirmationTimer > 0) {
            graphics.blit(TEXTURE, leftPos + 44, topPos + 97, 176, 8, 11, 20);
            confirmationTimer--;
        }
        String flux = String.format(Locale.ROOT, "%04d", menu.getFlux());
        graphics.drawString(font, flux, leftPos + 6, topPos + 24, 0x08FF00, false);
        int temp = (int) Math.round(menu.getHeat() * 0.00002D * 980D + 20D);
        graphics.drawString(font, temp + "°C", leftPos + 6, topPos + 62, 0x08FF00, false);
        graphics.drawString(font, menu.getControlPercent() + "%", leftPos + 6, topPos + 100, 0x08FF00, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = super.mouseClicked(mouseX, mouseY, button);
        controlField.mouseClicked(mouseX, mouseY, button);
        if (button == 0 && isWithinConfirm(mouseX, mouseY)) {
            sendControlUpdate();
            confirmationTimer = 15;
            return true;
        }
        return handled;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (minecraft != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || minecraft.options.keyInventory.matches(keyCode, scanCode)) {
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
        }
        if (controlField != null && controlField.isFocused() && controlField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (controlField != null && controlField.isFocused() && controlField.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    private boolean isWithinConfirm(double mouseX, double mouseY) {
        return mouseX >= leftPos + 44 && mouseX < leftPos + 55 && mouseY >= topPos + 97 && mouseY < topPos + 117;
    }

    private void sendControlUpdate() {
        if (minecraft == null || minecraft.gameMode == null) {
            return;
        }
        int value = parsePercentage(controlField.getValue());
        controlField.setValue(Integer.toString(value));
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1000 + value);
    }

    private static int parsePercentage(String text) {
        try {
            return Mth.clamp(Integer.parseInt(text), 0, 100);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String value = controlField != null ? controlField.getValue() : "0";
        super.resize(minecraft, width, height);
        controlField.setValue(value);
    }
}
