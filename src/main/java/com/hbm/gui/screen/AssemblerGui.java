package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.recipe.AssemblerRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.gui.menu.AssemblerMenu;
import com.hbm.gui.screen.page.recipe.RecipePageVanilla;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toserver.C2SSyncTileMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class AssemblerGui extends BaseMachineGui<AssemblerMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/gui_assembler.png");
    private static final ResourceLocation TEMPLATE_BUTTON_TEXTURE = HBM.rl("textures/gui/button_machine.png");
    private final RecipePageVanilla<Container, AssemblerRecipe> recipePage;
    private boolean widthTooNarrow;
    public AssemblerGui(AssemblerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = 222;
        this.recipePage = new RecipePageVanilla<>();
    }

    @Override
    protected void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipePage.init(this.minecraft, new ResourceLocation("textures/gui/recipe_book.png"), 147, 166, this.width, this.height, this.imageWidth / 2, ModRecipes.ASSEMBLER.type().get());
        this.addRenderableWidget(new ImageButton(this.leftPos + 78, this.topPos + 53, 20, 18, 0, 0, 19, TEMPLATE_BUTTON_TEXTURE, (button) -> {
            this.recipePage.toggleVisible();
            this.leftPos = this.recipePage.updateScreenPosition(this.width, this.imageWidth);
            button.setPosition(this.leftPos + 78, this.topPos + 53);
        }));
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        this.recipePage.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        if (isHovering(leftPos+116, topPos+18, 16, 52,pMouseX,pMouseY)){
            pGuiGraphics.drawString(font, Component.translatable(com.hbm.HBMLang.TOOLTIP_ENERGY.key(), menu.getEnergy()), pMouseX, pMouseY, 4210752);
        }
        renderTooltip(pGuiGraphics,pMouseX,pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        //渲染背景图
        showBgTexture(pGuiGraphics, TEXTURE);
        //进度条
        if (menu.getProgress() != 0) pGuiGraphics.blit(TEXTURE,leftPos+45,topPos+82,2,222, (int) (menu.getProgress() * 83 / 100),32);
        //电池条
        int colorPart = (int) (menu.getEnergyRate()*52);
        pGuiGraphics.blit(TEXTURE,leftPos+116, topPos+70-colorPart,176,52-colorPart, 16,colorPart);
        // 当前配方的结果
        AssemblerRecipe recipeNow = this.menu.getRecipeNow();
        if (recipeNow != null){
            ItemStack itemStack = recipeNow.getResultItem(minecraft.level.registryAccess()).copyWithCount(1);
            pGuiGraphics.renderFakeItem(itemStack, leftPos + 134, topPos + 112);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
        if (isHovering(45, 82, 83, 32, pX, pY)){
            MutableComponent component = HBMLang.GUI_TOOLTIP_PROGRESS.translate(menu.getProgress());
            pGuiGraphics.renderTooltip(font, component, pX, pY);
        }
        if (isHovering(116, 18, 16, 52, pX, pY)){
            MutableComponent component = HBMLang.GUI_TOOLTIP_ENERGY.translate(menu.getEnergy(), menu.getEnergyCapacity());
            pGuiGraphics.renderTooltip(font, component, pX, pY);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.recipePage.mouseClicked(pMouseX, pMouseY, pButton)){
            return true;
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

    @Override
    protected void containerTick() {
        super.containerTick();
        this.recipePage.tick();
        if (this.recipePage.recipeChosen != null){
            CompoundTag tag = new CompoundTag();
            tag.putString(HBMKey.RECIPE_NOW, this.recipePage.recipeChosen.getId().toString());
            ModMessages.sendToServer(new C2SSyncTileMessage(this.menu.getPos(), tag));
            this.recipePage.recipeChosen = null;
        }
    }
}
