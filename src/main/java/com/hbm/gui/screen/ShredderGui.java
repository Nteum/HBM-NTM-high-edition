package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.HBMLang;
import com.hbm.gui.menu.ShredderMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class ShredderGui extends AbstractContainerScreen<ShredderMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(HBM.MODID, "textures/gui/gui_shredder.png");
    private static final int ENERGY_BAR_HEIGHT = 88;

    public ShredderGui(ShredderMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 233;
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
        inventoryLabelY = imageHeight - 96 + 2;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // energy bar
        int energyStored = menu.getEnergyStored();
        int energyCapacity = Math.max(menu.getEnergyCapacity(), 1);
        if (energyStored > 0) {
            int bar = (int) Math.ceil((double) energyStored / energyCapacity * ENERGY_BAR_HEIGHT);
            if (bar > 0) {
                graphics.blit(TEXTURE, leftPos + 8, topPos + 106 - bar, 176, 160 - bar, 16, bar);
            }
        }

        // progress bar
        int maxProgress = Math.max(menu.getMaxProgress(), 1);
        if (menu.getProgress() > 0) {
            int bar = (int) Math.ceil((double) menu.getProgress() / maxProgress * 34);
            graphics.blit(TEXTURE, leftPos + 63, topPos + 89, 176, 54, Math.min(bar, 34) + 1, 18);
        }

        renderBladeState(graphics, menu.getLeftBladeState(), true);
        renderBladeState(graphics, menu.getRightBladeState(), false);

        if (hasBladeIssue()) {
            graphics.fill(leftPos - 16, topPos + 36, leftPos, topPos + 52, 0xA0FF0000);
        }
    }

    private void renderBladeState(GuiGraphics graphics, int state, boolean left) {
        if (state <= 0) {
            return;
        }
        int u = left ? 176 : 194;
        int v = switch (state) {
            case 1 -> 0;
            case 2 -> 18;
            default -> 36;
        };
        int x = left ? leftPos + 43 : leftPos + 79;
        graphics.blit(TEXTURE, x, topPos + 71, u, v, 18, 18);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);

        List<Component> tooltips = new ArrayList<>();
        if (isHoveringEnergy(mouseX, mouseY)) {
            tooltips.add(Component.translatable(HBMLang.TOOLTIP_ENERGY.key(), menu.getEnergyStored(), menu.getEnergyCapacity()));
        }
        if (hasBladeIssue() && isHoveringWarning(mouseX, mouseY)) {
            tooltips.add(Component.translatable("gui.hbm.shredder.no_blade"));
        }
        if (!tooltips.isEmpty()) {
            graphics.renderComponentTooltip(font, tooltips, mouseX, mouseY);
        }
        renderTooltip(graphics, mouseX, mouseY);
    }

    private boolean isHoveringEnergy(int mouseX, int mouseY) {
        int x = leftPos + 8;
        int y = topPos + 18;
        return mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + ENERGY_BAR_HEIGHT;
    }

    private boolean isHoveringWarning(int mouseX, int mouseY) {
        int x = leftPos - 16;
        int y = topPos + 36;
        return mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16;
    }

    private boolean hasBladeIssue() {
        return menu.getLeftBladeState() == 0 || menu.getLeftBladeState() == 3
                || menu.getRightBladeState() == 0 || menu.getRightBladeState() == 3;
    }
}
