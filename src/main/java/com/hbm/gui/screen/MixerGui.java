package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.MixerEntity;
import com.hbm.gui.menu.MixerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 混合机 GUI。
 */
public class MixerGui extends BaseMachineGui<MixerMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_mixer.png");
    private final MixerEntity be;

    public MixerGui(MixerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 204;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int power = (int) (be.power * 52 / MixerEntity.MAX_POWER);
        gui.blit(TEXTURE, leftPos + 12, topPos + 70 - power, 176, 52 - power, 16, power);

        if (be.processTime > 0 && be.progress > 0){
            int j = be.progress * 52 / be.processTime;
            gui.blit(TEXTURE, leftPos + 71, topPos + 31, 192, 0, j, 44);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 4210752);
    }
}
