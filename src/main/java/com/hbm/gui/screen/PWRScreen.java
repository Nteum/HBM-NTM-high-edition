package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.PWRControllerBlockEntity;
import com.hbm.gui.menu.PWRMenu;

import com.hbm.item.pwr.ItemPWRFuel;
import com.hbm.reactor.pwr.PWRFuelType;
import com.hbm.registries.ModItems;
import com.hbm.render.utils.GaugeUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;

public class PWRScreen extends BaseMachineGui<PWRMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/reactors/gui_pwr.png");
    private static final ResourceLocation COOLANT_TEXTURE = HBM.rl("textures/gui/fluids/coolant.png");
    private static final ResourceLocation HOT_COOLANT_TEXTURE = HBM.rl("textures/gui/fluids/coolant_hot.png");

    private EditBox rodField;

    public PWRScreen(PWRMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 188;
    }

    @Override
    protected void init() {
        super.init();
        rodField = new EditBox(this.font, this.leftPos + 57, this.topPos + 63, 30, 8, Component.empty());
        rodField.setBordered(false);
        rodField.setTextColor(0x00ff00);
        rodField.setMaxLength(3);
        rodField.setValue(String.valueOf(getRodTargetDisplay()));
        addRenderableWidget(rodField);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        rodField.tick();
        if (!rodField.isFocused()) {
            rodField.setValue(String.valueOf(getRodTargetDisplay()));
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderCustomTooltips(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        int coreHeat = menu.getCoreHeat();
        int coreCap = menu.getCoreHeatCapacity();
        int hullHeat = menu.getHullHeat();
        int hullCap = menu.getHullHeatCapacity();

        if ((coreCap > 0 && coreHeat > coreCap * 0.8F) || (hullCap > 0 && hullHeat > hullCap * 0.8F)) {
            graphics.blit(TEXTURE, this.leftPos + 147, this.topPos, 176, 14, 26, 26);
        }

        int processTime = menu.getProcessTime();
        int progress = menu.getProgress();
        int barWidth = processTime > 0 ? (int) (progress * 33.0 / processTime) : 0;
        if (barWidth > 0) {
            graphics.blit(TEXTURE, this.leftPos + 54, this.topPos + 33, 176, 0, barWidth, 14);
        }

        double rodLevel = getRodLevel();
        int rodWidth = (int) (rodLevel * 52.0 / 100.0);
        if (rodWidth > 0) {
            graphics.blit(TEXTURE, this.leftPos + 53, this.topPos + 54, 176, 40, rodWidth, 2);
        }

        double coreRatio = coreCap > 0 ? coreHeat / (double) coreCap : 0D;
        double hullRatio = hullCap > 0 ? hullHeat / (double) hullCap : 0D;
        GaugeUtil.drawSmoothGauge(graphics, this.leftPos + 124, this.topPos + 40, 0F, coreRatio, 5, 2, 1, 0x7F0000, 0x000000);
        GaugeUtil.drawSmoothGauge(graphics, this.leftPos + 160, this.topPos + 40, 0F, hullRatio, 5, 2, 1, 0x7F0000, 0x000000);

        int fuelTypeIndex = menu.getFuelType();
        int amountLoaded = menu.getFuelAmount();
        int rodCount = menu.getRodCount();
        if (fuelTypeIndex >= 0 && amountLoaded > 0) {
            PWRFuelType fuel = PWRFuelType.fromIndex(fuelTypeIndex);
            ItemStack display = ItemPWRFuel.createStack(ModItems.pwr_fuel.get(), fuel);
            graphics.renderItem(display, this.leftPos + 89, this.topPos + 5);
            graphics.renderItemDecorations(this.font, display, this.leftPos + 89, this.topPos + 5,
                    String.format(Locale.ROOT, "%d/%d", amountLoaded, rodCount));
        }

        drawFluid(graphics, COOLANT_TEXTURE, this.leftPos + 8, this.topPos + 57, 16, 52,
                menu.getCoolantAmount(), PWRControllerBlockEntity.COOLANT_CAPACITY);
        drawFluid(graphics, HOT_COOLANT_TEXTURE, this.leftPos + 26, this.topPos + 57, 16, 52,
                menu.getHotCoolantAmount(), PWRControllerBlockEntity.COOLANT_CAPACITY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        double flux = menu.getFluxScaled() / 10D;
        String fluxText = String.format(Locale.ROOT, "%,.1f", flux);
        float scale = 1.25F;
        graphics.pose().pushPose();
        graphics.pose().scale(1F / scale, 1F / scale, 1F);
        int x = (int) ((165 * scale) - this.font.width(fluxText));
        int y = (int) (64 * scale);
        graphics.drawString(this.font, fluxText, x, y, 0x00ff00, false);
        graphics.pose().popPose();
    }

    private void renderCustomTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        int guiLeft = this.leftPos;
        int guiTop = this.topPos;
        int coreHeat = menu.getCoreHeat();
        int coreCap = menu.getCoreHeatCapacity();
        int hullHeat = menu.getHullHeat();
        int hullCap = menu.getHullHeatCapacity();

        drawCustomInfoStat(graphics, mouseX, mouseY, guiLeft + 115, guiTop + 31, 18, 18,
                List.of(Component.literal("Core: " + String.format(Locale.ROOT, "%,d", coreHeat) + " / " + String.format(Locale.ROOT, "%,d", coreCap) + " TU")));
        drawCustomInfoStat(graphics, mouseX, mouseY, guiLeft + 151, guiTop + 31, 18, 18,
                List.of(Component.literal("Hull: " + String.format(Locale.ROOT, "%,d", hullHeat) + " / " + String.format(Locale.ROOT, "%,d", hullCap) + " TU")));

        int processTime = menu.getProcessTime();
        int progress = menu.getProgress();
        int percent = processTime > 0 ? (int) (progress * 100.0 / processTime) : 0;
        drawCustomInfoStat(graphics, mouseX, mouseY, guiLeft + 52, guiTop + 31, 36, 18,
                List.of(Component.literal(percent + "%")));

        double rodLevel = getRodLevel();
        drawCustomInfoStat(graphics, mouseX, mouseY, guiLeft + 52, guiTop + 53, 54, 4,
                List.of(Component.literal("Control rod level: " + (100 - (Math.round(rodLevel * 100) / 100D)) + "%")));

        int fuelTypeIndex = menu.getFuelType();
        int amountLoaded = menu.getFuelAmount();
        if (fuelTypeIndex >= 0 && amountLoaded > 0) {
            int fx = guiLeft + 88;
            int fy = guiTop + 4;
            if (mouseX >= fx && mouseX < fx + 18 && mouseY >= fy && mouseY < fy + 18) {
                PWRFuelType fuel = PWRFuelType.fromIndex(fuelTypeIndex);
                ItemStack display = ItemPWRFuel.createStack(ModItems.pwr_fuel.get(), fuel);
                graphics.renderTooltip(this.font, display, mouseX, mouseY);
            }
        }
    }

    private void drawFluid(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height, int amount, int capacity) {
        if (capacity <= 0 || amount <= 0) {
            return;
        }
        float ratio = Mth.clamp(amount / (float) capacity, 0F, 1F);
        int renderHeight = (int) (height * ratio);
        int startY = y + height - renderHeight;
        int remaining = renderHeight;
        int drawY = startY;
        while (remaining > 0) {
            int drawHeight = Math.min(16, remaining);
            graphics.blit(texture, x, drawY, 0, 16 - drawHeight, width, drawHeight, 16, 16);
            drawY += drawHeight;
            remaining -= drawHeight;
        }
    }

    private int getRodTargetDisplay() {
        int target = (int) Math.round(menu.getRodTargetScaled() / 100D);
        return Mth.clamp(100 - target, 0, 100);
    }

    private double getRodLevel() {
        return menu.getRodLevelScaled() / 100D;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = super.mouseClicked(mouseX, mouseY, button);
        rodField.mouseClicked(mouseX, mouseY, button);

        if (button == 0) {
            int bx = this.leftPos + 88;
            int by = this.topPos + 58;
            if (mouseX >= bx && mouseX < bx + 18 && mouseY >= by && mouseY < by + 18) {
                int level = parseRodField();
                int target = 100 - level;
                sendMenuButton(target);
                return true;
            }
        }
        return handled;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (rodField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (rodField.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    private int parseRodField() {
        String text = rodField.getValue();
        int level = 0;
        try {
            level = Integer.parseInt(text.trim());
        } catch (NumberFormatException ignored) {
        }
        level = Mth.clamp(level, 0, 100);
        rodField.setValue(String.valueOf(level));
        return level;
    }

    private void sendMenuButton(int id) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }
}
