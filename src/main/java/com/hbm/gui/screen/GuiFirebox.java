package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.HBMLang;
import com.hbm.blockentity.machine.PressEntity;
import com.hbm.gui.menu.MenuFirebox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GuiFirebox extends BaseMachineGui<MenuFirebox> {
    private static ResourceLocation TEXTURE = HBM.rl("textures/gui/machine/gui_firebox.png");

    public GuiFirebox(MenuFirebox pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
        showBgTexture(pGuiGraphics, TEXTURE);
        pGuiGraphics.blit(TEXTURE, leftPos + 81, topPos + 28, 176, 0, menu.getHeatEnergy() * 69 / menu.getMaxHeat(), 5);
        pGuiGraphics.blit(TEXTURE, leftPos + 81, topPos + 37, 176, 5, menu.getBurnTime() * 70 / Math.max(menu.getMaxBurnTime(), 1), 5);
        if (menu.isBurn()) pGuiGraphics.blit(TEXTURE, leftPos + 25, topPos + 26, 176, 10, 18, 18);
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
        List<Component> tooltips = new ArrayList<>();
        if (isHovering(80, 27, 71, 7, pX, pY)){
            tooltips.add(HBMLang.GUI_TOOLTIP_PARTIAL.translate(menu.getHeatEnergy(), menu.getMaxHeat()).append("TU"));
        }else if (isHovering(80, 36, 71, 7, pX, pY)){
            tooltips.add(Component.literal(menu.getBurnHeat() + "TU/t, " + menu.getBurnTime() / 20.0f + "s"));
        }
        if (!tooltips.isEmpty()) pGuiGraphics.renderComponentTooltip(font, tooltips, pX, pY);
    }
}
