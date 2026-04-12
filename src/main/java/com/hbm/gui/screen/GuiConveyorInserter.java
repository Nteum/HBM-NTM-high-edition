package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.MenuConveyorInserter;
import com.hbm.gui.screen.widget.MultiStateButton;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toserver.C2SSyncTileMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GuiConveyorInserter extends BaseMachineGui<MenuConveyorInserter> {
    private static ResourceLocation TEXTURE = HBM.rl("textures/gui/storage/gui_crane_inserter.png");
    private MultiStateButton button;
    public GuiConveyorInserter(MenuConveyorInserter pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        this.imageHeight = 185;
        super.init();
        this.button = new MultiStateButton(this.leftPos + 151, this.topPos + 34, 18, 18, 176, 0, 2, TEXTURE, button -> {
            this.button.changeState();
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("destroyer", this.button.stateNow > 0);
            ModMessages.sendToServer(new C2SSyncTileMessage(this.menu.getPos(), tag));
        });
        this.addRenderableWidget(this.button);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
        showBgTexture(pGuiGraphics, TEXTURE);
    }
}
