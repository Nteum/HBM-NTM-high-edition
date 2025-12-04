package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.generator.TileWoodBurner;
import com.hbm.blockentity.machine.PressEntity;
import com.hbm.gui.menu.MenuWoodBurner;
import com.hbm.gui.screen.component.ExtendedButton;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toserver.C2SSyncTileMessage;
import com.hbm.utils.BurnSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Fallable;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class GuiWoodBurner extends BaseMachineGui<MenuWoodBurner>{
    private static ResourceLocation TEXTURE = HBM.rl("textures/gui/gui_wood_burner.png");

    ExtendedButton btn1;
    ExtendedButton btn2;
    public GuiWoodBurner(MenuWoodBurner pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        imageHeight = 186;
        super.init();
        btn1 = new ExtendedButton(leftPos + 53, topPos + 17, 16, 15, 53, 17, 196, 0, TEXTURE, button -> ((ExtendedButton) button).toggle());
        this.addRenderableWidget(btn1);
        btn2 = new ExtendedButton(leftPos + 46, topPos + 37, 30, 14, 46, 37, 206, 72, TEXTURE, button -> ((ExtendedButton) button).toggle());
        this.addRenderableWidget(btn2);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        showBgTexture(pGuiGraphics, TEXTURE);
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        int x = this.leftPos;
        int y = this.topPos;

        List<Component> tooltips = new ArrayList<>();
        if (isMouseInside(pX, pY, x + 143, y + 18, 16, 34)){
            tooltips.add(HBMLang.GUI_TOOLTIP_ENERGY.translate(menu.getPower(), menu.getCapacity()));
        }else if (isMouseInside(pX, pY, x + 80, y + 18, 16, 52)){
            FluidStack fluid = menu.be.getFluid();
            tooltips.add(fluid.isEmpty() ? HBMLang.GUI_TOOLTIP_NO_FLUID.translate() : HBMLang.GUI_TOOLTIP_FLUID.translate(fluid.getDisplayName(), fluid.getAmount()));
        }else if (isMouseInside(pX, pY, x + 16, x + 17, 8, 54)){
            tooltips.add(HBMLang.GUI_TOOLTIP_BURN_TIME.translate(menu.getBurntime() / 20));
        }else if (isHovering(53, 17, 16, 15, pX, pY)){
            tooltips.add(menu.getIsOn() ? Component.literal("ON") : Component.literal("OFF"));
        } else if (this.hoveredSlot != null && this.hoveredSlot.hasItem()){
            this.hoveredSlot.getItem();
            tooltips.addAll(BurnSystem.getBurnDesc(tooltips, this.hoveredSlot.getItem(), ModBlockEntityType.WOOD_BURNER.get()));
        }

        super.renderTooltip(pGuiGraphics, pX, pY);
    }
    // 注意，这里面pButton是指鼠标按键不是你注册的button，值为0表示左键点击
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (super.mouseClicked(pMouseX, pMouseY, pButton)) return true;
        boolean consumed = false;
        CompoundTag tag = new CompoundTag();
        if (btn1.isMouseOver(pMouseX, pMouseY) && pButton == 0){
            minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.get());
            tag.putBoolean("toggle", !menu.getIsOn());
        }
        if (btn2.isMouseOver(pMouseX, pMouseY) && pButton == 0){
            minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.get());
            tag.putBoolean("switch", !menu.isLiquidBurn());
        }

        if (!tag.isEmpty()){
            ModMessages.sendToServer(new C2SSyncTileMessage(menu.be.getBlockPos(), tag));
        }
        return consumed;
    }
}
