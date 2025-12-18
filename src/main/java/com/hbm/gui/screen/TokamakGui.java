package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.TokamakMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;

/**
 * 托卡马克控制器界面。
 * 显示 B 场、温度、密度、约束评分、功率以及启停按钮。
 * 中英双语提示用于快速调试。
 */
public class TokamakGui extends AbstractContainerScreen<TokamakMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(HBM.MODID, "textures/gui/gui_tokamak.png");
    private Button startButton;
    private Button stopButton;

    public TokamakGui(TokamakMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 212;
        this.imageHeight = 192;
    }

    @Override
    protected void init() {
        super.init();
        int x = this.leftPos;
        int y = this.topPos;
        startButton = Button.builder(Component.translatable("gui.hbm.tokamak.start"), b -> sendToggle(true))
                .bounds(x + 140, y + 24, 60, 20)
                .build();
        stopButton = Button.builder(Component.translatable("gui.hbm.tokamak.stop"), b -> sendToggle(false))
                .bounds(x + 140, y + 48, 60, 20)
                .build();
        addRenderableWidget(startButton);
        addRenderableWidget(stopButton);
    }

    private void sendToggle(boolean start) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, start ? 1 : 0);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        ContainerData data = this.menu.containerData;
        float b = data.get(0) / 100f;
        float t = data.get(1) * 100f;
        float n = data.get(2) / 10000f;
        float score = data.get(3) / 100f;
        int power = data.get(4);
        boolean running = data.get(5) == 1;

        int textX = leftPos + 90;
        int textY = topPos + 20;
        graphics.drawString(this.font, Component.literal(String.format("B = %.2f T", b)), textX, textY, 0x66CCFF, false);
        graphics.drawString(this.font, Component.literal(String.format("T = %.0f K", t)), textX, textY + 12, 0xFFAA33, false);
        graphics.drawString(this.font, Component.literal(String.format("n = %.3f", n)), textX, textY + 24, 0xEEEEEE, false);
        graphics.drawString(this.font, Component.literal(String.format("Score = %.1f", score)), textX, textY + 36, 0xAA66FF, false);
        graphics.drawString(this.font, Component.literal(String.format("Stored = %d HE", power)), textX, textY + 48, 0x66FF66, false);
        graphics.drawString(this.font, Component.translatable(running ? "gui.hbm.tokamak.running" : "gui.hbm.tokamak.idle"), textX, textY + 60, running ? 0x55FF55 : 0xFF5555, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
