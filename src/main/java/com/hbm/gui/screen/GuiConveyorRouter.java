package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.blockentity.machine.PressEntity;
import com.hbm.gui.menu.MenuConveyorRouter;
import com.hbm.gui.screen.widget.MultiStateButton;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toserver.C2SSyncTileMessage;
import com.hbm.utils.math.BitUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class GuiConveyorRouter extends BaseMachineGui<MenuConveyorRouter> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/storage/gui_crane_router.png");
    private static final ResourceLocation TEXTURE_BUTTON = HBM.rl("textures/gui/button_machine.png");
    MultiStateButton[] buttons = new MultiStateButton[6];
    boolean already = false;
    public GuiConveyorRouter(MenuConveyorRouter pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        this.imageWidth = 256;
        this.imageHeight = 201;
        this.inventoryLabelX += 39;
        super.init();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i * 3 + j] = new MultiStateButton(this.leftPos + 7 + i * 222, this.topPos + 16 + j * 26, 18, 18, 62, 0, 4, i * 3 + j, TEXTURE_BUTTON, button -> {
                    if (button instanceof MultiStateButton multiStateButton){
                        multiStateButton.changeState();
                        CompoundTag tag = new CompoundTag();
                        tag.putInt(HBMKey.MODE, BitUtil.set(this.getMenu().getMode(), multiStateButton.getOrder() * 2, 2, multiStateButton.stateNow));
                        ModMessages.sendToServer(new C2SSyncTileMessage(this.menu.getPos(), tag));
                    }
                });
                this.addRenderableWidget(buttons[i * 3 + j]);
            }
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (!already){
            int mode = this.menu.getMode();
            for (int i = 0; i < 6; i++) {
                this.buttons[i].setState(BitUtil.get(mode, i * 2, 2));
            }
            already = true;
        }
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        if (this.menu.containerData.get(0) > 0){
            pGuiGraphics.drawString(this.font, HBMLang.GUI_TOOLTIP_CONVEYOR_ROUTER_WARNING.translate(), this.inventoryLabelX + 40, this.inventoryLabelY, 0xffffffff);
        }
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
        showBgTexture(pGuiGraphics, TEXTURE);
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        List<Component> tooltips = new ArrayList<>();
        for (MultiStateButton button : buttons) {
            if (button.isHover(pX, pY)){
                tooltips.add(switch (button.stateNow){
                    case 1 -> HBMLang.GUI_TOOLTIP_CONVEYOR_ROUTER_BUTTON1.translate();
                    case 2 -> HBMLang.GUI_TOOLTIP_CONVEYOR_ROUTER_BUTTON2.translate();
                    case 3 -> HBMLang.GUI_TOOLTIP_CONVEYOR_ROUTER_BUTTON3.translate();
                    default -> Component.literal("OFF");
                });
                break;
            }
        }

        if (!tooltips.isEmpty()) pGuiGraphics.renderComponentTooltip(font, tooltips, pX, pY);
        super.renderTooltip(pGuiGraphics, pX, pY);
    }
}
