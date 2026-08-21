package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.MicrowaveEntityBE;
import com.hbm.gui.menu.MicrowaveMenu;
import com.hbm.network.packet.toserver.C2SSyncTileMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 微波炉 GUI。
 * 显示能量条、进度条、速度条，点击上/下按钮调节速度。
 */
public class MicrowaveGui extends BaseMachineGui<MicrowaveMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_microwave.png");
    private final MicrowaveEntityBE be;

    public MicrowaveGui(MicrowaveMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 168;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int power = (int) be.getPowerScaled(34);
        gui.blit(TEXTURE, leftPos + 8, topPos + 51 - power, 176, 34 - power, 16, power);

        int progress = Math.min(be.getProgressScaled(23), 22);
        gui.blit(TEXTURE, leftPos + 104, topPos + 34, 192, 0, progress, 16);

        int speed = be.getSpeedScaled(34);
        gui.blit(TEXTURE, leftPos + 62, topPos + 60 - speed, 214, 34 - speed, 4, speed);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 4210752);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 速度调节按钮
        if (checkClick((int) mouseX, (int) mouseY, 43, 25, 18, 18)){
            sendButton(0);
        } else if (checkClick((int) mouseX, (int) mouseY, 43, 43, 18, 18)){
            sendButton(1);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void sendButton(int value){
        CompoundTag tag = new CompoundTag();
        tag.putInt("btnSpeed", value);
        com.hbm.core.network.HBMNetwork.sendToServer(new C2SSyncTileMessage(this.be.getBlockPos(), tag));
    }
}
