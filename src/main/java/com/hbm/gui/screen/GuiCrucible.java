package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.CrucibleFluidHandler;
import com.hbm.Inventory.material.HBMMatter;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.alloy.CrucibleRecipe;
import com.hbm.blockentity.machine.CrucibleEntity;
import com.hbm.gui.menu.MenuCrucible;
import com.hbm.gui.screen.page.recipe.RecipePage;
import com.hbm.gui.screen.page.recipe.RecipePageCrucible;
import com.hbm.gui.screen.widget.BounceButton;
import com.hbm.gui.screen.widget.MultiStateButton;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toserver.C2SSyncTileMessage;
import com.hbm.registries.HBMMatters;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class GuiCrucible extends BaseMachineGui<MenuCrucible> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/gui_crucible.png");
    private RecipePageCrucible<CrucibleRecipe> recipePage;
    private BounceButton btnStore2Alloy;
    private BounceButton btnAlloy2Store;
    private MultiStateButton btnRecipe;
    private FluidStack mouseOverMatter;
    private int clickedStackId = -1;
    private CompoundTag syncTag;
    public GuiCrucible(MenuCrucible pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.recipePage = new RecipePageCrucible<>();
        this.syncTag = new CompoundTag();
    }

    @Override
    protected void init() {
        this.imageHeight = 214;
        super.init();
        this.btnStore2Alloy = new BounceButton(leftPos + 52, topPos + 18, 9, 9, 0, 214, 5, TEXTURE, button -> {
            syncTag.putInt(HBMKey.BTN, 0);
        });
        this.addRenderableWidget(this.btnStore2Alloy);
        this.btnAlloy2Store = new BounceButton(leftPos + 52, topPos + 30, 9, 9, 12, 214, 5, TEXTURE, button -> {
            syncTag.putInt(HBMKey.BTN, 1);
        });
        this.addRenderableWidget(this.btnAlloy2Store);
        this.btnRecipe = new MultiStateButton(leftPos + 107, topPos + 81, 18, 18, 23, 214, 2, TEXTURE, button -> {
            this.btnRecipe.changeState();
            this.recipePage.toggleVisible();
            this.leftPos = this.recipePage.updateScreenPosition(this.width, this.imageWidth);
            button.setPosition(leftPos + 107, topPos + 81);
            this.btnStore2Alloy.setPosition(leftPos + 52, topPos + 18);
            this.btnAlloy2Store.setPosition(leftPos + 52, topPos + 30);
        });
        this.addRenderableWidget(this.btnRecipe);
        this.recipePage.init(this.minecraft == null ? Minecraft.getInstance() : this.minecraft, new ResourceLocation("textures/gui/recipe_book.png"),
                147, 166, this.width, this.height, this.imageWidth / 2, CrucibleRecipe.recipes.values().stream().toList(), recipe -> true);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (this.btnStore2Alloy != null) this.btnStore2Alloy.tick();
        if (this.btnAlloy2Store != null) this.btnAlloy2Store.tick();
        this.recipePage.tick();
        if (this.clickedStackId >= 0 && this.mouseOverMatter != null){
            syncTag.putInt(HBMKey.CLICK, this.clickedStackId);
            syncTag.put(HBMKey.FLUIDS, this.mouseOverMatter.writeToNBT(new CompoundTag()));
            this.clickedStackId = -1;
        }
        if (this.recipePage.recipeChosen != null){
            syncTag.putString(HBMKey.RECIPE_NOW, this.recipePage.recipeChosen.getId().toString());
            this.recipePage.recipeChosen = null;
        }
        if (!syncTag.isEmpty()){
            ModMessages.sendToServer(new C2SSyncTileMessage(this.menu.getPos(), syncTag));
            syncTag = new CompoundTag();
        }

    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.recipePage.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
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
        this.mouseOverMatter = null;
        CrucibleEntity crucibleEntity = menu.be;
        if (crucibleEntity != null){
            renderMoltenMatters(pGuiGraphics, crucibleEntity.getStoreStack(), 17, 97, pMouseX, pMouseY);
            renderMoltenMatters(pGuiGraphics, crucibleEntity.getAlloyStack(), 62, 97, pMouseX, pMouseY);
        }
        pGuiGraphics.setColor(1, 1, 1, 1);
    }

    private void renderMoltenMatters(GuiGraphics pGuiGraphics, CrucibleFluidHandler moltenStack, int xTop, int yBottom, int pMouseX, int pMouseY){
        int accAmount = 0;
        int layerHeight;
        for (int i = 0; i < moltenStack.size(); i++) {
            FluidStack fluidInTank = moltenStack.getFluidInTank(i);
            layerHeight = fluidInTank.getAmount() * 79 / moltenStack.getTankCapacity(0);
            accAmount += layerHeight;
            HBMMatter matter = HBMMatters.getMatterFromFluid(fluidInTank);
            if (matter == null) continue;
            int color = matter.moltenColor;
            int offset = matter.smeltProperty == 4 ? 34 : 0;
            pGuiGraphics.setColor(FastColor.ARGB32.red(color) / 255.0f, FastColor.ARGB32.green(color) / 255.0f, FastColor.ARGB32.blue(color) / 255.0f, 1.0f);
            pGuiGraphics.blit(TEXTURE, leftPos + xTop, topPos + yBottom - accAmount, 176 + offset, 89 - accAmount, 34, layerHeight);
            if (isHovering(xTop, yBottom - accAmount, 34, layerHeight, pMouseX, pMouseY)){
                this.mouseOverMatter = fluidInTank;
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
        }else if (isHovering(18, 18, 34, 79, pX, pY)){      // 存储槽
            if (this.mouseOverMatter != null){
                tooltips.add(HBMLang.GUI_TOOLTIP_FLUID.translate(Component.translatable(this.mouseOverMatter.getFluid().getFluidType().getDescriptionId()), this.mouseOverMatter.getAmount()));
            }else {
                tooltips.add(HBMLang.GUI_TOOLTIP_CRUCIBLE_CAPACITY.translate(CrucibleEntity.CAPACITY));
            }
        }else if (isHovering(62, 18, 34, 79, pX, pY)){
            if (this.mouseOverMatter != null){
                tooltips.add(HBMLang.GUI_TOOLTIP_FLUID.translate(Component.translatable(this.mouseOverMatter.getFluid().getFluidType().getDescriptionId()), this.mouseOverMatter.getAmount()));
            }else {
                tooltips.add(HBMLang.GUI_TOOLTIP_CRUCIBLE_CAPACITY.translate(CrucibleEntity.CAPACITY));
            }
        }else if (isHovering(107, 81, 18, 18, pX, pY)){
            tooltips.add(HBMLang.GUI_TOOLTIP_CRUCIBLE_BUTTON1.translate());
        }
        if (!tooltips.isEmpty()) pGuiGraphics.renderComponentTooltip(font, tooltips, pX, pY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.recipePage.mouseClicked(pMouseX, pMouseY, pButton)){
            return true;
        }
        if (isHovering(18, 18, 34, 79, pMouseX, pMouseY)){
            this.clickedStackId = 0;
        }else if (isHovering(62, 18, 34, 79, pMouseX, pMouseY)){
            this.clickedStackId = 1;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.recipePage.keyPressed(pKeyCode, pScanCode, pModifiers)){
            return false;   // 这里返回值应该为false，以免对搜索框的输入影响界面，比如搜索框里输入个e，如果返回值为true，会触发页面退出。
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (this.recipePage.charTyped(pCodePoint, pModifiers)){
            return true;
        }
        return super.charTyped(pCodePoint, pModifiers);
    }
}
