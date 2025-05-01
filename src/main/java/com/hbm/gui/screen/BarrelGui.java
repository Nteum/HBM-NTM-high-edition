package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.BarrelEntity;
import com.hbm.gui.menu.BarrelMenu;
import com.hbm.gui.menu.BatteryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class BarrelGui extends AbstractContainerScreen<BarrelMenu> {
    private ResourceLocation TEXTURE = HBM.rl("textures/gui/gui_barrel.png");
    private ImageButton modBtn;
    public BarrelGui(BarrelMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        modBtn = new ImageButton(this.leftPos+151,this.topPos+34,18,18,176,menu.getMode()*18,TEXTURE,(button)->{} );
        this.addRenderableWidget(modBtn);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(TEXTURE,leftPos,topPos,0,0,imageWidth,imageHeight);
        if ((menu).container instanceof BarrelEntity barrelEntity){
            IFluidHandler fluidTank = barrelEntity.getFluidTank();
            if (fluidTank != null){
                FluidStack fluidInTank = fluidTank.getFluidInTank(0);
                int tankCapacity = fluidTank.getTankCapacity(0);
                RenderUtils.fluidTank(leftPos+71,topPos + 69, 34, 52, (float) fluidInTank.getAmount() / tankCapacity,pGuiGraphics, fluidInTank.getFluid());
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton > 0){
            menu.changeMode();
            this.modBtn.yTexStart = menu.getMode()*18;
            this.modBtn.yDiffTex = menu.getMode() == 3 ? -54 : 18;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
}
