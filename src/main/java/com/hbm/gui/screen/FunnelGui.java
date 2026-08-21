package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.FunnelEntityBE;
import com.hbm.gui.menu.FunnelMenu;
import com.hbm.network.packet.toserver.C2SSyncTileMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 组合漏斗 GUI。
 */
public class FunnelGui extends BaseMachineGui<FunnelMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_funnel.png");
    private final FunnelEntityBE be;

    public FunnelGui(FunnelMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 168;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        gui.blit(TEXTURE, leftPos + 159, topPos + 73, 176, be.mode * 10, 10, 10);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 4210752);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (checkClick((int) mouseX, (int) mouseY, 159, 73, 10, 10)){
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("toggle", true);
            com.hbm.core.network.HBMNetwork.sendToServer(new C2SSyncTileMessage(this.be.getBlockPos(), tag));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
