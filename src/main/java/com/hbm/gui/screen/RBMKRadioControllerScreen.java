package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.RBMKRadioControllerMenu;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toserver.C2SSyncTileMessage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class RBMKRadioControllerScreen extends AbstractContainerScreen<RBMKRadioControllerMenu> {

    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/machine/gui_rtty_controller.png");

    private EditBox channelField;
    private boolean polling;

    public RBMKRadioControllerScreen(RBMKRadioControllerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 256;
        this.imageHeight = 42;
        this.inventoryLabelY = 9999;
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
        polling = menu.isPolling();

        channelField = new EditBox(this.font, leftPos + 25 + 4, topPos + 17 + 4, 90 - 8, 14, Component.empty());
        channelField.setBordered(false);
        channelField.setTextColor(0x00FF00);
        channelField.setMaxLength(16);
        channelField.setValue(menu.getChannel());
        addRenderableWidget(channelField);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        if (polling) {
            graphics.blit(TEXTURE, leftPos + 173, topPos + 17, 0, 42, 18, 18);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, 6, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        if (isWithinLocal(mouseX, mouseY, 173, 17, 18, 18)) {
            graphics.renderTooltip(this.font,
                    Component.literal(polling ? "Polling" : "State Change"),
                    mouseX, mouseY);
        } else if (isWithinLocal(mouseX, mouseY, 137, 17, 18, 18)) {
            graphics.renderComponentTooltip(this.font, List.of(
                    Component.literal("Usable functions:"),
                    Component.literal("setrods!percent"),
                    Component.literal("extendrods!percent")), mouseX, mouseY);
        } else if (isWithinLocal(mouseX, mouseY, 209, 17, 18, 18)) {
            graphics.renderTooltip(this.font, Component.literal("Save Settings"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (isWithinLocal(mouseX, mouseY, 173, 17, 18, 18)) {
                polling = !polling;
                return true;
            }
            if (isWithinLocal(mouseX, mouseY, 209, 17, 18, 18)) {
                sendConfig();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void sendConfig() {
        if (menu.getPos() == null) {
            return;
        }
        CompoundTag tag = new CompoundTag();
        tag.putString("Channel", channelField.getValue() == null ? "" : channelField.getValue().trim());
        tag.putBoolean("Polling", polling);
        ModMessages.sendToServer(new C2SSyncTileMessage(menu.getPos(), tag));
    }

    private boolean isWithinLocal(double mouseX, double mouseY, int localX, int localY, int width, int height) {
        int x = leftPos + localX;
        int y = topPos + localY;
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String value = channelField != null ? channelField.getValue() : "";
        super.resize(minecraft, width, height);
        if (channelField != null) {
            channelField.setValue(value);
        }
    }
}
