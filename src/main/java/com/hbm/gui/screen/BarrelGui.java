package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.BarrelEntity;
import com.hbm.gui.menu.BarrelMenu;
import com.hbm.gui.menu.BatteryMenu;
import com.hbm.gui.screen.component.FluidBar;
import com.hbm.gui.screen.component.MultiStateButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BarrelGui extends AbstractContainerScreen<BarrelMenu> {
    private ResourceLocation TEXTURE = HBM.rl("textures/gui/gui_barrel.png");
    private MultiStateButton modBtn;
    private FluidBar fluidBar;
    public BarrelGui(BarrelMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        modBtn = new MultiStateButton(this.leftPos+151,this.topPos+34,18,18,176,0,4,menu.getMode(),TEXTURE,(button)->{
            ((MultiStateButton)button).stateNow = menu.changeMode();
        });
//        fluidBar = new FluidBar(leftPos+71,topPos + 17, 34, 52,0,0,16,16,RenderUtils.WATER,Component.empty(),true);
        this.addRenderableWidget(modBtn);
//        this.addRenderableWidget(fluidBar);

    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(TEXTURE,leftPos,topPos,0,0,imageWidth,imageHeight);
        if ((menu).container instanceof BarrelEntity barrelEntity){
            IFluidHandler fluidTank = barrelEntity.getFluidTank();
//            fluidBar.updateFluidTank(fluidTank, 0);
            if (fluidTank != null){
                FluidStack fluidInTank = fluidTank.getFluidInTank(0);
                int tankCapacity = fluidTank.getTankCapacity(0);
                RenderUtils.fluidTank(leftPos+71,topPos + 69, 34, 52, (float) fluidInTank.getAmount() / tankCapacity,pGuiGraphics, fluidInTank.getFluid());
                //显示悬浮字体
                if (pMouseX >= leftPos+71&&pMouseX<=leftPos+105&&pMouseY>=topPos+17&&pMouseY<=topPos+69){
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(Component.translatable(fluidInTank.getFluid().getFluidType().getDescription()+" : "+fluidInTank.getAmount()+" mB"));
                    pGuiGraphics.renderTooltip(this.font,tooltip, Optional.empty(),pMouseX,pMouseY);
                }
            }
        }

    }
}
