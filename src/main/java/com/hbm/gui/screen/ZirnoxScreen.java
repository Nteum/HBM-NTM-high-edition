package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.ZirnoxReactorBlockEntity;
import com.hbm.gui.menu.ZirnoxMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.Locale;

public class ZirnoxScreen extends BaseMachineGui<ZirnoxMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/reactors/gui_zirnox.png");

    public ZirnoxScreen(ZirnoxMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 203;
        this.imageHeight = 256;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        int steam = menu.getSteam();
        int co2 = menu.getCarbonDioxide();
        int water = menu.getWater();
        int heat = menu.getHeat();
        int pressure = menu.getPressure();

        int steamGauge = scale(steam, ZirnoxReactorBlockEntity.STEAM_CAPACITY, 6);
        graphics.blit(TEXTURE, leftPos + 160, topPos + 108, 238, 12 * steamGauge, 18, 12);

        int co2Gauge = scale(co2, ZirnoxReactorBlockEntity.CO2_CAPACITY, 6);
        graphics.blit(TEXTURE, leftPos + 142, topPos + 108, 238, 12 * co2Gauge, 18, 12);

        int waterGauge = scale(water, ZirnoxReactorBlockEntity.WATER_CAPACITY, 6);
        graphics.blit(TEXTURE, leftPos + 178, topPos + 108, 238, 12 * waterGauge, 18, 12);

        int heatGauge = scale(heat, ZirnoxReactorBlockEntity.MAX_HEAT, 12);
        graphics.blit(TEXTURE, leftPos + 160, topPos + 33, 220, 18 * heatGauge, 18, 17);

        int pressureGauge = scale(pressure, ZirnoxReactorBlockEntity.MAX_PRESSURE, 12);
        graphics.blit(TEXTURE, leftPos + 178, topPos + 33, 220, 18 * pressureGauge, 18, 17);

        if (menu.isOn()) {
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    graphics.blit(TEXTURE, leftPos + 7 + 36 * x, topPos + 15 + 36 * y, 238, 238, 18, 18);
                }
            }
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    graphics.blit(TEXTURE, leftPos + 25 + 36 * x, topPos + 33 + 36 * y, 238, 238, 18, 18);
                }
            }
            graphics.blit(TEXTURE, leftPos + 142, topPos + 15, 220, 238, 18, 18);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderCustomTooltips(graphics, mouseX, mouseY);
    }

    private void renderCustomTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        int temp = (int) Math.round(menu.getHeat() * 0.00001D * 780D + 20D);
        int pressureBar = (int) Math.round(menu.getPressure() * 0.00001D * 30D);

        drawCustomInfoStat(graphics, mouseX, mouseY, leftPos + 160, topPos + 33, 18, 17,
                List.of(Component.literal("Temperature: " + temp + "°C")));
        drawCustomInfoStat(graphics, mouseX, mouseY, leftPos + 178, topPos + 33, 18, 17,
                List.of(Component.literal("Pressure: " + pressureBar + " bar")));

        drawCustomInfoStat(graphics, mouseX, mouseY, leftPos + 160, topPos + 108, 18, 12,
                List.of(Component.literal("Steam: " + format(menu.getSteam()) + " / " + format(ZirnoxReactorBlockEntity.STEAM_CAPACITY) + " mB")));
        drawCustomInfoStat(graphics, mouseX, mouseY, leftPos + 142, topPos + 108, 18, 12,
                List.of(Component.literal("CO2: " + format(menu.getCarbonDioxide()) + " / " + format(ZirnoxReactorBlockEntity.CO2_CAPACITY) + " mB")));
        drawCustomInfoStat(graphics, mouseX, mouseY, leftPos + 178, topPos + 108, 18, 12,
                List.of(Component.literal("Water: " + format(menu.getWater()) + " / " + format(ZirnoxReactorBlockEntity.WATER_CAPACITY) + " mB")));
    }

    private static String format(int value) {
        return String.format(Locale.ROOT, "%,d", value);
    }

    private static int scale(int value, int max, int steps) {
        if (max <= 0) {
            return 0;
        }
        return Mth.clamp(value * steps / max, 0, steps);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = super.mouseClicked(mouseX, mouseY, button);
        if (button == 0) {
            if (mouseX >= leftPos + 144 && mouseX < leftPos + 158 && mouseY >= topPos + 35 && mouseY < topPos + 49) {
                sendMenuButton(0);
                return true;
            }
            if (mouseX >= leftPos + 151 && mouseX < leftPos + 187 && mouseY >= topPos + 51 && mouseY < topPos + 87) {
                sendMenuButton(1);
                return true;
            }
        }
        return handled;
    }

    private void sendMenuButton(int id) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }
}
