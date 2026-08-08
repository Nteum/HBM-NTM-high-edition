package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.HBMLang;
import com.hbm.blockentity.machine.TileCrystallizer;
import com.hbm.blockentity.machine.TileMinerLarge;
import com.hbm.gui.menu.MenuCrystallizer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class GuiCrystallizer extends BaseMachineGui<MenuCrystallizer> {
    public static ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_crystallizer_alt.png");
    public GuiCrystallizer(MenuCrystallizer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void firstInit() {
        this.imageHeight = 204;
        super.firstInit();
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        showBgTexture(pGuiGraphics, TEXTURE);
        int i = (int) ((float)menu.getEnergy() / TileCrystallizer.maxPower * 52);
        if (i > 0) pGuiGraphics.blit(TEXTURE, leftPos + 152, topPos + 70 - i, 176, 64 - i, 16, i);
        i = (int) ((float)menu.getProgress() / menu.getDuration() * 28);
        if (i > 0) pGuiGraphics.blit(TEXTURE, leftPos + 80, topPos + 47, 176, 0, i, 12);
        drawInfoPanel(pGuiGraphics, leftPos + 117, topPos + 22, 8, 8);
        FluidStack fluidStack = menu.getFluidStack();
        RenderUtils.fluidTank(leftPos + 35, topPos + 70, 16, 52, (float) fluidStack.getAmount() / TileMinerLarge.MAX_TANK_CAPACITY, pGuiGraphics, fluidStack.getFluid());
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
        List<Component> list = new ArrayList<>();
        if (isHovering(152, 18, 16, 52, pX, pY)){
            list.add(HBMLang.GUI_TOOLTIP_ENERGY.translate(this.menu.getEnergy(), TileCrystallizer.maxPower));
        }else if (isHovering(35, 18, 16, 52, pX, pY)){
            FluidStack fluidStack = menu.getFluidStack();
            list.add(fluidStack.isEmpty() ? HBMLang.GUI_TOOLTIP_NO_FLUID.translate() : HBMLang.GUI_TOOLTIP_FLUID.translate(fluidStack.getFluid().getFluidType().getDescriptionId(), fluidStack.getAmount()));
        }else if (isHovering(117, 22, 8, 8, pX, pY)){
            pGuiGraphics.renderComponentTooltip(this.font, List.of(
                    HBMLang.GUI_DESC_UPGRADE.translate(),HBMLang.GUI_DESC_UPGRADE_SPEED.translate(),HBMLang.GUI_DESC_UPGRADE_EFFECTIVENESS.translate(),HBMLang.GUI_DESC_UPGRADE_OVERDRIVE.translate()
            ), leftPos + 200, topPos + 45);
        }
        if (!list.isEmpty()) pGuiGraphics.renderComponentTooltip(this.font, list, pX, pY);
    }
}
