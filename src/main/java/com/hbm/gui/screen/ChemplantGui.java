package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.ChemplantEntity;
import com.hbm.gui.menu.ChemplantMenu;
import com.hbm.gui.screen.widget.BarEnergy;
import com.hbm.gui.screen.widget.BarFluid;
import com.hbm.gui.screen.widget.BarProgress;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.IFluidTank;

import java.util.ArrayList;
import java.util.List;

public class ChemplantGui extends BaseMachineGui<ChemplantMenu> {
    public static final ResourceLocation TEXTURE = HBM.rl("textures/gui/gui_chemplant.png");
    BarProgress barProgress;
    BarEnergy barEnergy;
    List<BarFluid> barFluids = new ArrayList<>();
    public ChemplantGui(ChemplantMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = 222;
    }

    @Override
    protected void init() {
        super.init();
        barProgress = new BarProgress(leftPos + 43, topPos + 89, 90, 18, 0, 222, 256, 256, TEXTURE, Component.empty(),false);
        barEnergy = new BarEnergy(leftPos + 44, topPos + 18, 16, 52, 176, 0, TEXTURE, Component.empty());
        barFluids.add(new BarFluid(() -> leftPos + 8, () -> topPos + 18, 16, 34, Fluids.EMPTY));
        barFluids.add(new BarFluid(() -> leftPos + 26, () -> topPos + 18, 16, 34, Fluids.EMPTY));
        barFluids.add(new BarFluid(() -> leftPos + 134, () -> topPos + 18, 16, 34, Fluids.EMPTY));
        barFluids.add(new BarFluid(() -> leftPos + 152, () -> topPos + 18, 16, 34, Fluids.EMPTY));
        this.addRenderableWidget(barProgress);
        this.addRenderableWidget(barEnergy);
        barFluids.forEach(this::addRenderableWidget);
    }
    /** 每tick更新data
     * render的频率是帧，它一般比每秒20次高，和服务端同步用这个tick函数效率高一点
     * */
    @Override
    protected void containerTick() {
        this.barProgress.progress = this.menu.containerData.get(0);
        this.barProgress.maxProgress = this.menu.containerData.get(1);
        this.barProgress.updateData();
        this.barEnergy.progress = this.menu.containerData.get(2);
        this.barEnergy.maxProgress = this.menu.containerData.get(3);
        this.barEnergy.updateData();
        if (this.menu.be instanceof ChemplantEntity entity){
            List fluidTanks = entity.getFluidTanks(null);
            for (int i = 0; i < fluidTanks.size(); i++) {
                barFluids.get(i).fluid = ((IFluidTank)fluidTanks.get(i)).getFluid().getFluid();
                barFluids.get(i).progress = ((IFluidTank)fluidTanks.get(i)).getFluidAmount();
                barFluids.get(i).maxProgress = ((IFluidTank)fluidTanks.get(i)).getCapacity();
                barFluids.get(i).updateData();
            }
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        showBgTexture(pGuiGraphics, TEXTURE);
    }
}
