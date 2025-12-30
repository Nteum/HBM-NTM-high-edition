package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.WoodBurnerMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class WoodBurnerScreen extends AbstractContainerScreen<WoodBurnerMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(HBM.MODID, "textures/gui/wood_burner.png");

    public WoodBurnerScreen(WoodBurnerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 200;
    }

    @Override
    protected void init() {
        super.init();
        this.topPos -= 20;
        this.titleLabelX = 17;
        this.titleLabelY = 6;
        this.inventoryLabelY = this.imageHeight - 110;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = this.leftPos;
        int y = this.topPos;
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        if (menu.isLit()) {
            graphics.blit(TEXTURE, x + 46, y + 37, 206, 72, 30, 14);
        }

        int burnHeight = menu.getBurnTimeScaled(52);
        if (burnHeight > 0) {
            graphics.blit(TEXTURE, x + 17, y + 18 + 52 - burnHeight, 192, 52 - burnHeight, 4, burnHeight);
        }

        int energyHeight = menu.getEnergyScaled(34);
        if (energyHeight > 0) {
            graphics.blit(TEXTURE, x + 143, y + 18 + 34 - energyHeight, 176, 34 - energyHeight, 16, energyHeight);
        }

        if (!menu.isEnabled()) {
            graphics.blit(TEXTURE, x + 53, y + 17, 196, 0, 16, 16);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        if (isHovering(143, 18, 16, 34, mouseX, mouseY)) {
            Component energy = Component.literal(String.format("%,d / %,d HE", menu.getEnergyLong(), menu.getMaxEnergyLong()))
                    .withStyle(ChatFormatting.GREEN);
            graphics.renderTooltip(this.font, energy, mouseX, mouseY);
        }
        if (isHovering(17, 18, 4, 52, mouseX, mouseY)) {
            if (menu.getMaxBurnTime() > 0) {
                int seconds = menu.getBurnTime() / 20;
                graphics.renderTooltip(this.font, Component.translatable("gui.hbm.wood_burner.time", seconds).withStyle(ChatFormatting.GOLD), mouseX, mouseY);
            } else {
                graphics.renderTooltip(this.font, Component.translatable("gui.hbm.wood_burner.no_fuel").withStyle(ChatFormatting.RED), mouseX, mouseY);
            }
        }
        if (isHovering(53, 17, 16, 16, mouseX, mouseY)) {
            Component status = menu.isEnabled()
                    ? Component.translatable("gui.hbm.wood_burner.enabled").withStyle(ChatFormatting.GREEN)
                    : Component.translatable("gui.hbm.wood_burner.disabled").withStyle(ChatFormatting.RED);
            graphics.renderTooltip(this.font, status, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovering(53, 17, 16, 16, mouseX, mouseY)) {
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
