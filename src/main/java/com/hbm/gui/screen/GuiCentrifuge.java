package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.HBMLang;
import com.hbm.blockentity.machine.TileMachineCentrifuge;
import com.hbm.gui.menu.MenuCentrifuge;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class GuiCentrifuge extends BaseMachineGui<MenuCentrifuge> {
    public static ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_centrifuge.png");
    public GuiCentrifuge(MenuCentrifuge pMenu, Inventory pPlayerInventory, Component pTitle) {
        // 深色背景，贴图颜色设成白的
        super(pMenu, pPlayerInventory, pTitle.copy().withStyle(ChatFormatting.WHITE));
    }

    @Override
    protected void firstInit() {
        this.imageHeight = 186;
        this.titleLabelY = -12;
        super.firstInit();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        showBgTexture(pGuiGraphics, TEXTURE);
        int power = this.menu.containerData.get(0);
        if (power > 0){
            int i = (int) ((float)power / TileMachineCentrifuge.maxPower * 35);
            pGuiGraphics.blit(TEXTURE, this.leftPos + 9 ,topPos + 48 - i, 176, 35 - i, 16, i);
        }

        int process = this.menu.containerData.get(1);
        if (process > 0){
            int p = (int) ((float) process / TileMachineCentrifuge.processingSpeed * 145);
            for (int i1 = 0; i1 < 4; i1++) {
                int h = Math.min(p, 36);
                pGuiGraphics.blit(TEXTURE, leftPos + 65 + i1 * 20, topPos + 50 - h, 176, 71 - h, 12, h);
                p -= h;
                if (p <= 0) break;
            }
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
        List<Component> list = new ArrayList<>();
        if (isHovering(9, 13, 16, 34, pX, pY)){
            list.add(HBMLang.GUI_TOOLTIP_ENERGY.translate(this.menu.containerData.get(0), TileMachineCentrifuge.maxPower));
        }
        if (!list.isEmpty()) pGuiGraphics.renderComponentTooltip(font, list, pX, pY);
    }
}
