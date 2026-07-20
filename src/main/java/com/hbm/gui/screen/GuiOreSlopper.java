package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.blockentity.machine.TileMinerLarge;
import com.hbm.blockentity.machine.TileOreSloppper;
import com.hbm.gui.menu.MenuOreSlopper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class GuiOreSlopper extends BaseMachineGui<MenuOreSlopper> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_ore_slopper.png");
    public GuiOreSlopper(MenuOreSlopper pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        this.imageHeight = 204;
        super.init();
        this.titleLabelY += 38;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        // 能量条
        int i = (int) (menu.getPower() * 52 / TileOreSloppper.maxPower);
        pGuiGraphics.blit(TEXTURE, leftPos + 8, topPos + 70 - i, 176, 86 - i, 16, i);
        // 流体条
        IFluidHandler fluidHandler = menu.getFluidHandler();
        FluidStack fluidStack = fluidHandler.getFluidInTank(0);
        RenderUtils.fluidTank(leftPos + 26, topPos + 70, 16, 52, (float) fluidStack.getAmount() / TileOreSloppper.TANK_CAPACITY, pGuiGraphics, fluidStack.getFluid());
        fluidStack = fluidHandler.getFluidInTank(1);
        RenderUtils.fluidTank(leftPos + 116, topPos + 70, 16, 52, (float) fluidStack.getAmount() / TileOreSloppper.TANK_CAPACITY, pGuiGraphics, fluidStack.getFluid());
        // 进度条
        i = (int) (menu.getProgress() * 35);
        pGuiGraphics.blit(TEXTURE, leftPos + 62, topPos + 52 - i, 176, 34 - i, 34, i);
        // 状态指标
        if (menu.getPower() >= menu.getConsumption())
            pGuiGraphics.blit(TEXTURE, leftPos + 12, topPos + 4, 202, 34, 9, 12);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        showBgTexture(pGuiGraphics, TEXTURE);
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
        List<Component> tooltips = new ArrayList<>();
        if (isHovering(26, 18,34, 52, pX, pY)){
            FluidStack tank0 = menu.getFluidHandler().getFluidInTank(0);
            tooltips.add(HBMLang.GUI_TOOLTIP_FLUID.translate(Component.translatable(tank0.getFluid().getFluidType().getDescriptionId()), tank0.getAmount()));
        }else if (isHovering(116, 18,16, 52, pX, pY)){
            FluidStack tank1 = menu.getFluidHandler().getFluidInTank(1);
            tooltips.add(HBMLang.GUI_TOOLTIP_FLUID.translate(Component.translatable(tank1.getFluid().getFluidType().getDescriptionId()), tank1.getAmount()));
        }else if (isHovering(8, 18,16, 52, pX, pY)){
            tooltips.add(HBMLang.GUI_TOOLTIP_ENERGY.translate(menu.getPower(), TileOreSloppper.maxPower));
        }
        if (!tooltips.isEmpty()) pGuiGraphics.renderComponentTooltip(font, tooltips, pX, pY);
    }
}
