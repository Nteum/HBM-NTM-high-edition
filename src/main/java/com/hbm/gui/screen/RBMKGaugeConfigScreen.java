package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.RBMKGaugeConfigMenu;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.Locale;

public class RBMKGaugeConfigScreen extends AbstractContainerScreen<RBMKGaugeConfigMenu> {

    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/machine/gui_rbmk_gauge.png");
    private static final int CHANNELS = 4;

    private final EditBox[] colorFields = new EditBox[CHANNELS];
    private final EditBox[] labelFields = new EditBox[CHANNELS];
    private final EditBox[] channelFields = new EditBox[CHANNELS];
    private final EditBox[] minFields = new EditBox[CHANNELS];
    private final EditBox[] maxFields = new EditBox[CHANNELS];
    private final boolean[] active = new boolean[CHANNELS];
    private final boolean[] polling = new boolean[CHANNELS];

    public RBMKGaugeConfigScreen(RBMKGaugeConfigMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 256;
        this.imageHeight = 204;
        this.inventoryLabelY = 9999;
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;

        for (int i = 0; i < CHANNELS; i++) {
            active[i] = menu.isActive(i);
            polling[i] = menu.isPolling(i);
        }

        int insetX = 4;
        int insetY = 4;
        for (int i = 0; i < CHANNELS; i++) {
            colorFields[i] = createField(27 + insetX, 55 + insetY + i * 36, 72 - insetX * 2, 6,
                    String.format(Locale.ROOT, "%06x", menu.getColor(i)));
            labelFields[i] = createField(175 + insetX, 55 + insetY + i * 36, 72 - insetX * 2, 15, menu.getLabel(i));
            channelFields[i] = createField(27 + insetX, 73 + insetY + i * 36, 72 - insetX * 2, 16, menu.getChannel(i));
            minFields[i] = createField(121 + insetX, 73 + insetY + i * 36, 52 - insetX * 2, 10, Integer.toString(menu.getMinValue(i)));
            maxFields[i] = createField(195 + insetX, 73 + insetY + i * 36, 52 - insetX * 2, 10, Integer.toString(menu.getMaxValue(i)));
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        for (int i = 0; i < CHANNELS; i++) {
            if (active[i]) {
                graphics.blit(TEXTURE, leftPos + 111, topPos + i * 36 + 54, 18, 204, 16, 16);
            }
            if (polling[i]) {
                graphics.blit(TEXTURE, leftPos + 128, topPos + i * 36 + 53, 0, 204, 18, 18);
            }
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
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < CHANNELS; i++) {
                if (isWithinLocal(mouseX, mouseY, 111, i * 36 + 54, 16, 16)) {
                    active[i] = !active[i];
                    return true;
                }
                if (isWithinLocal(mouseX, mouseY, 128, i * 36 + 53, 18, 18)) {
                    polling[i] = !polling[i];
                    return true;
                }
            }
            if (isWithinLocal(mouseX, mouseY, 209, 17, 18, 18)) {
                sendConfig();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private EditBox createField(int localX, int localY, int width, int maxLen, String value) {
        EditBox field = new EditBox(this.font, leftPos + localX, topPos + localY, width, 14, Component.empty());
        field.setBordered(false);
        field.setTextColor(0x00FF00);
        field.setMaxLength(maxLen);
        field.setValue(value == null ? "" : value);
        addRenderableWidget(field);
        return field;
    }

    private void sendConfig() {
        if (menu.getPos() == null) {
            return;
        }
        CompoundTag tag = new CompoundTag();
        int activeMask = 0;
        int pollingMask = 0;
        for (int i = 0; i < CHANNELS; i++) {
            if (active[i]) {
                activeMask |= (1 << i);
            }
            if (polling[i]) {
                pollingMask |= (1 << i);
            }
        }
        tag.putInt("CfgActiveMask", activeMask);
        tag.putInt("CfgPollingMask", pollingMask);

        for (int i = 0; i < CHANNELS; i++) {
            tag.putInt("CfgColor" + i, parseHexColor(colorFields[i].getValue(), menu.getColor(i)));
            tag.putString("CfgLabel" + i, trimText(labelFields[i].getValue(), 15));
            tag.putString("CfgChannel" + i, trimText(channelFields[i].getValue(), 16));
            tag.putInt("CfgMin" + i, parseInt(minFields[i].getValue(), menu.getMinValue(i)));
            tag.putInt("CfgMax" + i, parseInt(maxFields[i].getValue(), menu.getMaxValue(i)));
        }

        ModMessages.sendToServer(new C2SSyncTileMessage(menu.getPos(), tag));
    }

    private boolean isWithinLocal(double mouseX, double mouseY, int localX, int localY, int width, int height) {
        int x = leftPos + localX;
        int y = topPos + localY;
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private static String trimText(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        String trimmed = text.trim();
        return trimmed.length() <= maxLength ? trimmed : trimmed.substring(0, maxLength);
    }

    private static int parseInt(String text, int fallback) {
        if (text == null || text.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static int parseHexColor(String text, int fallback) {
        if (text == null) {
            return Mth.clamp(fallback, 0, 0xFFFFFF);
        }
        String normalized = text.trim();
        if (normalized.startsWith("#")) {
            normalized = normalized.substring(1);
        }
        try {
            return Mth.clamp(Integer.parseInt(normalized, 16), 0, 0xFFFFFF);
        } catch (NumberFormatException ignored) {
            return Mth.clamp(fallback, 0, 0xFFFFFF);
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String[] color = snapshot(colorFields);
        String[] label = snapshot(labelFields);
        String[] channel = snapshot(channelFields);
        String[] min = snapshot(minFields);
        String[] max = snapshot(maxFields);
        super.resize(minecraft, width, height);
        restore(colorFields, color);
        restore(labelFields, label);
        restore(channelFields, channel);
        restore(minFields, min);
        restore(maxFields, max);
    }

    private static String[] snapshot(EditBox[] boxes) {
        String[] values = new String[boxes.length];
        for (int i = 0; i < boxes.length; i++) {
            values[i] = boxes[i] != null ? boxes[i].getValue() : "";
        }
        return values;
    }

    private static void restore(EditBox[] boxes, String[] values) {
        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i] != null && i < values.length) {
                boxes[i].setValue(values[i]);
            }
        }
    }
}
