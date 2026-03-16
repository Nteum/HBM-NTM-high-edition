package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.GasTurbineBlockEntity;
import com.hbm.gui.menu.GasTurbineMenu;
import com.hbm.gui.screen.widget.BarFluid;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GasTurbineScreen extends BaseMachineGui<GasTurbineMenu> {

    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/generators/gui_turbinegas.png");
    private static final ResourceLocation GAUGE_TEXTURE = HBM.rl("textures/gui/gauges/button_big.png");

    private static final int SLIDER_X = 36;
    private static final int SLIDER_Y = 37;
    private static final int SLIDER_WIDTH = 16;
    private static final int SLIDER_HEIGHT = 66;

    private final List<BarFluid> fluidBars = new ArrayList<>(4);
    private boolean draggingSlider;

    public GasTurbineScreen(GasTurbineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 223;
    }

    @Override
    protected void init() {
        super.init();
        fluidBars.clear();
        fluidBars.add(new BarFluid(() -> leftPos + 8, () -> topPos + 65, 16, 48, Fluids.EMPTY));
        fluidBars.add(new BarFluid(() -> leftPos + 8, () -> topPos + 103, 16, 32, Fluids.EMPTY));
        fluidBars.add(new BarFluid(() -> leftPos + 147, () -> topPos + 98, 16, 36, Fluids.EMPTY));
        fluidBars.add(new BarFluid(() -> leftPos + 147, () -> topPos + 58, 16, 36, Fluids.EMPTY));
        fluidBars.forEach(bar -> {
            bar.active = false;
            bar.visible = true;
            this.addRenderableWidget(bar);
        });
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        GasTurbineBlockEntity entity = menu.getBlockEntity();
        if (entity != null) {
            List<FluidTank> tanks = entity.getFluids().getFluidTanks();
            if (tanks.size() >= 4) {
                updateBar(fluidBars.get(0), tanks.get(0));
                updateBar(fluidBars.get(1), tanks.get(1));
                updateBar(fluidBars.get(2), tanks.get(2));
                updateBar(fluidBars.get(3), tanks.get(3));
            }
        }
    }

    private static void updateBar(BarFluid bar, FluidTank tank) {
        bar.fluid = tank.getFluid().getFluid();
        bar.progress = tank.getFluidAmount();
        bar.maxProgress = tank.getCapacity();
        bar.updateData();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        drawAutoButton(graphics);
        drawStateButton(graphics);
        drawSlider(graphics);
        drawEnergyBar(graphics);
        drawPowerDisplay(graphics);
        drawThermometer(graphics);
        drawRpmGauge(graphics);
    }

    private void drawAutoButton(GuiGraphics graphics) {
        int v = menu.isAutoMode() && menu.isRunning() ? 11 : 24;
        graphics.blit(TEXTURE, leftPos + 74, topPos + 86, 194, v, 29, 13);
    }

    private void drawStateButton(GuiGraphics graphics) {
        int state = menu.getState();
        int u = switch (state) {
            case -1 -> 194;
            case 1 -> 210;
            default -> 178;
        };
        graphics.blit(TEXTURE, leftPos + 80, topPos + 32, u, 38, 16, 16);
    }

    private void drawSlider(GuiGraphics graphics) {
        graphics.blit(TEXTURE, leftPos + SLIDER_X, topPos + 97 - menu.getSliderPosition(), 178, 0, SLIDER_WIDTH, 6);
    }

    private void drawEnergyBar(GuiGraphics graphics) {
        long energy = menu.getEnergy();
        int width = (int) Math.min(142, energy * 142L / GasTurbineBlockEntity.CAPACITY);
        if (width > 0) {
            graphics.blit(TEXTURE, leftPos + 26, topPos + 109, 0, 223, width, 16);
        }
    }

    private void drawPowerDisplay(GuiGraphics graphics) {
        int display = menu.getState() == -1 ? 8_888_888 : Math.max(0, menu.getInstantPowerOutput() * 20);
        int[] digits = new int[7];
        int value = display;
        for (int i = 6; i >= 0; i--) {
            digits[i] = value % 10;
            value /= 10;
            graphics.blit(TEXTURE, leftPos + 65 + i * 7, topPos + 71, 194 + digits[i] * 5, 0, 5, 11);
        }
        int firstNonZero = -1;
        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                firstNonZero = i;
                break;
            }
        }
        if (firstNonZero == -1) {
            firstNonZero = digits.length - 1;
        }
        for (int i = 0; i < firstNonZero; i++) {
            graphics.blit(TEXTURE, leftPos + 65 + i * 7, topPos + 71, 244, 0, 5, 11);
        }
    }

    private void drawThermometer(GuiGraphics graphics) {
        int maxTemp = 800;
        int temp = Mth.clamp(menu.getTemperature(), 0, maxTemp);
        int filled = (int) Math.round(64.0D * temp / maxTemp);
        if (filled > 0) {
            graphics.blit(TEXTURE, leftPos + 136, topPos + 28 + 64 - filled, 176, 64 - filled, 2, filled);
        }
    }

    private void drawRpmGauge(GuiGraphics graphics) {
        int rpm = Mth.clamp(menu.getRpm(), 0, 100);
        graphics.blit(GAUGE_TEXTURE, leftPos + 64, topPos + 16, rpm * 48, 0, 48, 48, 4848, 48);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (isInsideStartButton(mouseX, mouseY)) {
                sendButton(0);
                return true;
            }
            if (menu.isRunning() && isInsideAutoButton(mouseX, mouseY)) {
                sendButton(1);
                return true;
            }
            if (menu.isRunning() && isInsideSlider(mouseX, mouseY)) {
                draggingSlider = true;
                sendSlider(mouseY);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingSlider) {
            if (menu.isRunning()) {
                sendSlider(mouseY);
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingSlider) {
            draggingSlider = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void sendButton(int id) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }

    private void sendSlider(double mouseY) {
        if (this.minecraft == null || this.minecraft.gameMode == null || !menu.isRunning()) {
            return;
        }
        int value = computeSliderValue(mouseY);
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, GasTurbineMenu.sliderButtonId(value));
    }

    private int computeSliderValue(double mouseY) {
        double localY = mouseY - this.topPos;
        int slider = (int) Math.round(97 - localY);
        return Mth.clamp(slider, 0, 60);
    }

    private boolean isInsideSlider(double mouseX, double mouseY) {
        return isInside(mouseX, mouseY, SLIDER_X, SLIDER_Y, SLIDER_WIDTH, SLIDER_HEIGHT);
    }

    private boolean isInsideStartButton(double mouseX, double mouseY) {
        double centerX = this.leftPos + 88;
        double centerY = this.topPos + 40;
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        return dx * dx + dy * dy <= 64.0D;
    }

    private boolean isInsideAutoButton(double mouseX, double mouseY) {
        return isInside(mouseX, mouseY, 74, 86, 29, 13);
    }

    private boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
        int relX = this.leftPos + x;
        int relY = this.topPos + y;
        return mouseX >= relX && mouseX < relX + width && mouseY >= relY && mouseY < relY + height;
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        if (isInside(mouseX, mouseY, 26, 109, 142, 16)) {
            List<Component> energyTip = List.of(
                    Component.literal(String.format(Locale.ROOT, "%,d / %,d HE", menu.getEnergy(), GasTurbineBlockEntity.CAPACITY))
                            .withStyle(ChatFormatting.GREEN));
            graphics.renderComponentTooltip(this.font, energyTip, mouseX, mouseY);
        }
        if (isInsideSlider(mouseX, mouseY)) {
            List<Component> sliderTip = List.of(
                    Component.literal("Throttle: " + menu.getThrottle() + "%").withStyle(ChatFormatting.YELLOW),
                    Component.literal(menu.isAutoMode() ? "Auto Mode: ON" : "Auto Mode: OFF")
                            .withStyle(menu.isAutoMode() ? ChatFormatting.AQUA : ChatFormatting.GRAY));
            graphics.renderComponentTooltip(this.font, sliderTip, mouseX, mouseY);
        }
        if (isInsideAutoButton(mouseX, mouseY)) {
            Component auto = Component.literal(menu.isAutoMode() ? "Auto Mode Enabled" : "Auto Mode Disabled")
                    .withStyle(menu.isAutoMode() ? ChatFormatting.GREEN : ChatFormatting.RED);
            graphics.renderTooltip(this.font, auto, mouseX, mouseY);
        }
        if (isInsideStartButton(mouseX, mouseY)) {
            Component status = Component.literal(menu.getState() == 0 ? "Start Turbine" : "Stop Turbine")
                    .withStyle(ChatFormatting.YELLOW);
            graphics.renderTooltip(this.font, status, mouseX, mouseY);
        }
    }
}
