package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.BarrelEntity;
import com.hbm.gui.menu.BarrelMenu;
import com.hbm.gui.screen.widget.BarFluid;
import com.hbm.gui.screen.widget.MultiStateButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class BarrelGui extends BaseMachineGui<BarrelMenu> {
    private final ResourceLocation TEXTURE = HBM.rl("textures/gui/gui_barrel.png");
    private MultiStateButton modBtn;
    private BarFluid fluidBar;
    public BarrelGui(BarrelMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        modBtn = new MultiStateButton(this.leftPos+151,this.topPos+34,18,18,176,0,4,2,TEXTURE,(button)->{
            ((MultiStateButton)button).stateNow = menu.changeMode();this.modBtn.setFocused(false);
        });
        fluidBar = new BarFluid(() -> leftPos+71,() -> topPos + 17, 34, 52, Fluids.EMPTY);
        this.addRenderableWidget(modBtn);
        this.addRenderableWidget(fluidBar);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.modBtn.updateData(this.menu.getMode());
        if (this.menu.be instanceof BarrelEntity entity){
            FluidTank tank = entity.getFluidTanks().get(0);
            fluidBar.fluid = tank.getFluid().getFluid();
            fluidBar.progress = tank.getFluidAmount();
            fluidBar.maxProgress = tank.getCapacity();
            fluidBar.updateData();
            // 同步tile数据
            this.menu.syncTile();
        }
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        showBgTexture(pGuiGraphics, TEXTURE);
    }
}
