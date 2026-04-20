package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.gui.menu.MenuConveyorExtractor;
import com.hbm.gui.menu.slot.FilterSlot;
import com.hbm.gui.screen.widget.MultiStateButton;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toserver.C2SSyncTileMessage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class GuiConveyorExtractor extends BaseMachineGui<MenuConveyorExtractor>{
    public static final ResourceLocation TEXTURE = HBM.rl("textures/gui/storage/gui_crane_ejector.png");
    boolean isWhitelist = false;
    MultiStateButton button;
    public GuiConveyorExtractor(MenuConveyorExtractor pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        this.imageWidth = 212;
        this.imageHeight = 185;
        this.inventoryLabelX += 18;
        super.init();
        //guiLeft + 187, guiTop + 34, 212, 0, 18, 18
        this.button = new MultiStateButton(this.leftPos + 187, this.topPos + 34, 18, 18, 212, 0, 2, TEXTURE, button -> {
            this.button.changeState();
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("maxEject", this.button.stateNow != 0);
            ModMessages.sendToServer(new C2SSyncTileMessage(this.menu.getPos(), tag));
        });
        this.addRenderableWidget(this.button);
//        this.isWhitelist = menu.containerData.get(0) > 0;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        // 更新数据的状态
        this.isWhitelist = menu.isWhitelist();
        this.button.setState(menu.containerData.get(1));
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
        showBgTexture(pGuiGraphics, TEXTURE);

        int i = isWhitelist ? 33 : 47;
        pGuiGraphics.blit(TEXTURE, this.leftPos + 139, this.topPos + i, 212, 36 ,3, 6);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        boolean oldValue = isWhitelist;
        if (isHovering(128, 30, 14, 13, pMouseX, pMouseY)){
            this.isWhitelist = true;
        }else if (isHovering(128, 44, 14, 13, pMouseX, pMouseY)){
            this.isWhitelist = false;
        }
        //247.5 75.5
        if (oldValue != this.isWhitelist){
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("isWhitelist", this.isWhitelist);
            ModMessages.sendToServer(new C2SSyncTileMessage(this.menu.getPos(), tag));
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
}
