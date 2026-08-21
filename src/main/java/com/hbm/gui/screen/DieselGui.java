package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.DieselEntityBE;
import com.hbm.gui.menu.DieselMenu;
import com.hbm.network.packet.toserver.C2SSyncTileMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 柴油发电机 GUI。
 */
public class DieselGui extends BaseMachineGui<DieselMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_diesel.png");
    private final DieselEntityBE be;

    public DieselGui(DieselMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 204;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int power = (int) (be.power * 52 / be.MAX_POWER);
        gui.blit(TEXTURE, leftPos + 134, topPos + 70 - power, 176, 52 - power, 16, power);

        int fuel = be.getTank().getCapacity() == 0 ? 0 : be.getTank().getFluidAmount() * 16 / be.getTank().getCapacity();
        gui.blit(TEXTURE, leftPos + 61, topPos + 54, 176, 52, fuel, 16);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 0xC7C1A3);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (checkClick((int) mouseX, (int) mouseY, 80, 54, 20, 20)){
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("toggle", true);
            com.hbm.core.network.HBMNetwork.sendToServer(new C2SSyncTileMessage(this.be.getBlockPos(), tag));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
