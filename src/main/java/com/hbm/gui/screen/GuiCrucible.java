package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.CrucibleFluidHandler;
import com.hbm.blockentity.machine.CrucibleEntity;
import com.hbm.gui.menu.MenuCrucible;
import com.hbm.gui.screen.page.recipe.RecipePage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class GuiCrucible extends BaseMachineGui<MenuCrucible> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/gui_crucible.png");
    private RecipePage<?, ?> recipePage;
    public GuiCrucible(MenuCrucible pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        this.imageHeight = 214;
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
        showBgTexture(pGuiGraphics, TEXTURE);

        int hGauge = menu.getProgress() * 33 / CrucibleEntity.MAX_PROGRESS;
        if (hGauge > 0)pGuiGraphics.blit(TEXTURE, leftPos + 126, topPos + 82, 176, 0, hGauge, 5);
        hGauge = menu.getHeat() * 33 / CrucibleEntity.MAX_HEAT;
        if (hGauge > 0) pGuiGraphics.blit(TEXTURE, leftPos + 126, topPos + 91, 176, 5, hGauge, 5);
        // 物质条
        CrucibleEntity crucibleEntity = menu.be;
        if (crucibleEntity != null){
            int accAmount = 0;
            int layerHeight = 0;
            CrucibleFluidHandler storeStack = crucibleEntity.getStoreStack();
            for (int i = 0; i < storeStack.getSize(); i++) {
                FluidStack fluidInTank = storeStack.getFluidInTank(i);
                layerHeight = fluidInTank.getAmount() * 79 / storeStack.getTankCapacity(0);
                layerHeight = Math.max(layerHeight, 5);         // 避免物质条太小看不见
                pGuiGraphics.blit(TEXTURE, leftPos + 17, topPos + 97 - accAmount, 176, 89 - accAmount, 34, layerHeight);
                if (isHovering(leftPos + 17, topPos + 97 - accAmount, 34, layerHeight, pMouseX, pMouseY)){
                    pGuiGraphics.renderTooltip(this.font, Component.translatable(fluidInTank.getFluid().getFluidType().getDescriptionId()).append(" : " + fluidInTank.getAmount() + " mB"), pMouseX, pMouseY);
                }
                accAmount += layerHeight;
            }
            if (isHovering(leftPos + 17, topPos + 97, 34, 97 - layerHeight, pMouseX, pMouseY)){
                pGuiGraphics.renderTooltip(this.font, HBMLang.GUI_TOOLTIP_CRUCIBLE_CAPACITY.translate(CrucibleEntity.CAPACITY), pMouseX, pMouseY);
            }
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
        List<Component> tooltips = new ArrayList<>();
        if (isHovering(125, 81, 34, 7, pX, pY)){
            tooltips.add(HBMLang.GUI_TOOLTIP_PARTIAL.translate(menu.getProgress(), CrucibleEntity.MAX_PROGRESS));
        }else if (isHovering(125, 90, 34, 7, pX, pY)){
            tooltips.add(HBMLang.GUI_TOOLTIP_PARTIAL.translate(menu.getHeat(), CrucibleEntity.MAX_HEAT).append("TU"));
        }
        if (!tooltips.isEmpty()) pGuiGraphics.renderComponentTooltip(font, tooltips, pX, pY);
//        drawCustomInfoStat(pGuiGraphics, pX, pY, leftPos + 125, topPos + 81, 34, 7, List.of(HBMLang.GUI_TOOLTIP_PARTIAL.translate(menu.getProgress(), CrucibleEntity.MAX_PROGRESS)));
//        drawCustomInfoStat(pGuiGraphics, pX, pY, leftPos + 125, topPos + 90, 34, 7, List.of(HBMLang.GUI_TOOLTIP_PARTIAL.translate(menu.getHeat(), CrucibleEntity.MAX_HEAT).append("TU")));
    }
}
