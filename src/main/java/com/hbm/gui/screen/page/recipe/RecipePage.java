package com.hbm.gui.screen.page.recipe;

import com.hbm.Inventory.recipe.RecipeHelper;
import com.hbm.Inventory.recipe.alloy.CrucibleRecipe;
import com.hbm.blockentity.machine.PressEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * 显示和选中配方的页面，原本打算复用原版的RecipeBook的，但太复杂了，所以单独写一个
 * */
@OnlyIn(Dist.CLIENT)
public class RecipePage <T> implements Renderable, GuiEventListener {
    private static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    private static final Component SEARCH_HINT = Component.translatable("gui.recipebook.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
    private ResourceLocation BACKGROUND;
    private int backgroundWidth;
    private int backgroundHeight;
    private int width;
    private int height;
    private int xOffset;
    private boolean visible;
    private boolean ignoreTextInput;
    private List<T> recipes;            // 可以使用的所有配方
    private List<T> recipeToShow;       // 显示在屏幕上的所有配方，因为需要支持搜索功能
    public T recipeChosen;             // 当前被选中的配方
    private int row;
    private int column;
    private int page;
    @Nullable
    private EditBox searchBox;
    private StateSwitchingButton pageUpButton;
    private StateSwitchingButton pageDownButton;
    @Nullable
    protected Minecraft minecraft;
    RegistryAccess registryAccess;


    public void init(Minecraft minecraft, ResourceLocation bg, int imageWidth, int imageHeight, int width, int height, int xOffset, List<T> recipes, Predicate<T> filter){
        this.minecraft = minecraft;
        BACKGROUND = bg;
        this.backgroundWidth = imageWidth;
        this.backgroundHeight = imageHeight;
        this.width = width;
        this.height = height;
        this.xOffset = xOffset;
        this.recipes = recipes.stream().filter(filter).toList();
        this.recipeToShow = this.recipes;
        this.visible = false;       // 一开始默认不打开的.
        this.registryAccess = this.minecraft.level.registryAccess();
        this.page = 0;
        // 按键区：(11,31) -> (?,height-40)，一个格子25
        this.column = (this.backgroundWidth - 22) / 25;
        this.row = (this.backgroundHeight - 60) / 25;
    }

    public void initVisual(){
        int i = (this.width - this.backgroundWidth) / 2 - this.xOffset;
        int j = (this.height - this.backgroundHeight) / 2;
        // 搜索栏
        this.searchBox = new EditBox(this.minecraft.font, i + 26, j + 14, 79, 9 + 3, Component.translatable("itemGroup.search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(16777215);
        this.searchBox.setHint(SEARCH_HINT);
        this.pageUpButton = new StateSwitchingButton(i + this.backgroundWidth / 2 + 19, j + this.backgroundHeight - 29, 12, 17, false);
        this.pageUpButton.initTextureValues(1, 208, 13, 18, RECIPE_BOOK_LOCATION);
        this.pageDownButton = new StateSwitchingButton(i + this.backgroundWidth / 2 - 35, j + this.backgroundHeight - 29, 12, 17, false);
        this.pageDownButton.initTextureValues(14, 208, 13, 18, RECIPE_BOOK_LOCATION);
    }
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (!visible) return;
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
        int leftPos = (this.width - this.backgroundWidth) / 2 - this.xOffset;
        int topPos = (this.height - this.backgroundHeight) / 2;
        pGuiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, backgroundWidth, backgroundHeight);
        this.searchBox.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.pageUpButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.pageDownButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        int idx;
        int borderX = leftPos + this.backgroundWidth / 2 - 25 * this.column / 2;
        int borderY = topPos + 31;
        // 物品高光
        int hoverItemX = pMouseX - borderY < 0 ? -1 : (pMouseX - borderX) / 25;
        int hoverItemY = pMouseY - borderY < 0 ? -1 : (pMouseY - borderY) / 25;
        if (hoverItemX >= 0 && hoverItemX < this.column && hoverItemY >= 0 && hoverItemY < this.row){
            int i = borderX + hoverItemX * 25 + 4;
            int j = borderY + hoverItemY * 25 + 4;
            pGuiGraphics.fillGradient(RenderType.guiOverlay(), i, j, i + 16, j + 16, -2130706433, -2130706433, 0);
            // 配方的tooltip
            idx = this.page * this.row * this.column + this.column * hoverItemY + hoverItemX;
            if (idx < this.recipeToShow.size()){
                T recipe = recipeToShow.get(idx);
                pGuiGraphics.renderTooltip(this.minecraft.font, genTooltip(recipe, this.registryAccess), Optional.empty(), pMouseX, pMouseY);
            }
        }
        // 配方格子
        outer : for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.column; j++) {
                idx = this.page * this.row * this.column + this.column * i + j;
                if (idx >= this.recipeToShow.size()) break outer;
                renderRecipe(pGuiGraphics, this.recipeToShow.get(idx), borderX + 25 * j + 4, borderY + 25 * i + 4);
            }
        }
        pGuiGraphics.pose().popPose();
    }

    List<Component> genTooltip(T recipe, RegistryAccess reg) {
        return null;
    }

    void renderRecipe(GuiGraphics pGuiGraphics, T recipe, int posX, int posY){ }

    boolean filterRecipeName(T recipe, String query){
        return true;
    }

    @Override
    public void setFocused(boolean pFocused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (visible){
            this.initVisual();
        }
    }

    public boolean isVisible(){
        return visible;
    }

    public void toggleVisible(){
        setVisible(!visible);
    }

    public void tick(){
        if (this.visible){
            this.searchBox.tick();
        }
    }

    // 回车的时候才进行搜索
    public void search(String query){
        String lowerQuery = query.toLowerCase().trim();
        if (query.isEmpty()){
            this.recipeToShow = this.recipes;
            return;
        }
        this.recipeToShow = this.recipes.stream().filter(r -> this.filterRecipeName(r, lowerQuery)).toList();
        this.page = 0;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        this.ignoreTextInput = false;
        if (this.isVisible() && !this.minecraft.player.isSpectator()) {
            if (pKeyCode == 256) {
                this.setVisible(false);
                return true;
            } else if (this.searchBox.keyPressed(pKeyCode, pScanCode, pModifiers)) {
                return true;
            } else if (this.searchBox.isFocused() && this.searchBox.isVisible() && pKeyCode != 256) {
                if (pKeyCode == 257 || pKeyCode == 335){
                    this.search(this.searchBox.getValue());
                }
                return true;
            } else if (this.minecraft.options.keyChat.matches(pKeyCode, pScanCode) && !this.searchBox.isFocused()) {
                this.ignoreTextInput = true;
                this.searchBox.setFocused(true);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        this.ignoreTextInput = false;
        return GuiEventListener.super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.visible && !this.minecraft.player.isSpectator()){
            this.recipeChosen = getRecipe(pMouseX, pMouseY);
            if (this.searchBox.mouseClicked(pMouseX, pMouseY, pButton)){
                this.searchBox.setFocused(true);
            }else {
                this.searchBox.setFocused(false);
            }
        }
        return GuiEventListener.super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (this.ignoreTextInput) {
            return false;
        } else if (this.isVisible() && !this.minecraft.player.isSpectator()) {
            if (this.searchBox.charTyped(pCodePoint, pModifiers)) {
                return true;
            } else {
                return GuiEventListener.super.charTyped(pCodePoint, pModifiers);
            }
        } else {
            return false;
        }
    }

    public int updateScreenPosition(int pWidth, int pImageWidth) {
        int i;
        if (this.visible){
            i = (pWidth - pImageWidth + this.backgroundWidth) / 2;
        }else {
            i = (pWidth - pImageWidth) / 2;
        }

        return i;
    }

    public T getRecipe(double pMouseX, double pMouseY){
        int leftPos = (this.width - this.backgroundWidth) / 2 - this.xOffset;
        int topPos = (this.height - this.backgroundHeight) / 2;
        int borderX = leftPos + this.backgroundWidth / 2 - 25 * this.column / 2;
        int borderY = topPos + 31;
        int hoverItemX = pMouseX - borderY < 0 ? -1 : (int) ((pMouseX - borderX) / 25);
        int hoverItemY = pMouseY - borderY < 0 ? -1 : (int) ((pMouseY - borderY) / 25);
        if (hoverItemX >= 0 && hoverItemX < this.column && hoverItemY >= 0 && hoverItemY < this.row){
            int idx = this.page * this.row * this.column + this.column * hoverItemY + hoverItemX;
            return idx < this.recipeToShow.size() ? this.recipeToShow.get(idx) : null;
        }
        return null;
    }
}
