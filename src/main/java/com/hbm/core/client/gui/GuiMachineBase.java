package com.hbm.core.client.gui;

import com.hbm.HBM;
import com.hbm.HBMLang;
import com.hbm.blockentity.interfaces.IUpgradeInfoProvider;
import com.hbm.utils.math.BobMth;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用机器 GUI 基类。
 *
 * 对应旧版 com.hbm.inventory.gui.GuiInfoContainer，把几乎所有机器 GUI 都需要的公共逻辑
 * 泛化到本类：
 * - 能量条/流体条的悬浮信息（drawElectricityInfo / drawFluidInfo）
 * - 信息图标面板（drawInfoPanel，对应 gui_utility.png 上 I/!/ 图标）
 * - 点击区域判定（checkClick / isHovering）
 * - 带层级的物品渲染（renderItem）
 * - 升级信息收集（getUpgradeInfo）
 *
 * 新机器的 GUI 继承本类，只需实现：
 * - 构造：传入 Menu 与背景贴图
 * - renderBg：画背景与进度条
 * - drawScreen/render：画悬浮信息与动态部件
 * 相比旧版每台机器重复复制这些方法的做法，本类一次写好、处处复用。
 */
public abstract class GuiMachineBase<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    boolean firstInit = true;
    protected final ResourceLocation GUI_UTIL = HBM.rl("textures/gui/gui_utility.png");

    public GuiMachineBase(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        if (firstInit){
            firstInit();
            firstInit = false;
        }
        super.init();
    }

    protected void firstInit(){
        titleLabelX = (imageWidth - font.width(title)) / 2;  //标题居中
        inventoryLabelY += imageHeight - 166;   // 修改“物品栏”三字位置
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        renderTooltip(pGuiGraphics,pMouseX,pMouseY);
    }

    //====================== 悬浮信息 ======================

    /** 在能量条上显示悬浮信息（对应旧 drawElectricityInfo）。 */
    public void drawElectricityInfo(GuiGraphics gui, int mouseX, int mouseY, int x, int y, int width, int height, long power, long maxPower){
        if (isMouseInside(mouseX, mouseY, x, y, width, height)){
            gui.renderComponentTooltip(font, List.of(
                    Component.translatable(HBMLang.GUI_TOOLTIP_ENERGY.key(), BobMth.getShortNumber(power) + "/" + BobMth.getShortNumber(maxPower))), mouseX, mouseY);
        }
    }

    /** 在流体条上显示悬浮信息（对应旧 renderTankInfo 的 tooltip 部分）。 */
    public void drawFluidInfo(GuiGraphics gui, int mouseX, int mouseY, int x, int y, int width, int height, FluidStack stack){
        if (isMouseInside(mouseX, mouseY, x, y, width, height)){
            String fluidName = stack.isEmpty() ? "N/A" : stack.getDisplayName().getString();
            int amount = stack.getAmount();
            gui.renderComponentTooltip(font, List.of(Component.literal(fluidName), Component.literal(BobMth.getShortNumber(amount) + " mB")), mouseX, mouseY);
        }
    }

    /** 在进度条上显示悬浮信息。 */
    public void drawProgressInfo(GuiGraphics gui, int mouseX, int mouseY, int x, int y, int width, int height, int progress, int maxProgress){
        if (isMouseInside(mouseX, mouseY, x, y, width, height)){
            gui.renderComponentTooltip(font, List.of(Component.literal(progress + "/" + maxProgress)), mouseX, mouseY);
        }
    }

    /** 通用悬浮信息（任意多行文本）。 */
    public void drawCustomInfoStat(GuiGraphics gui, int mouseX, int mouseY, int x, int y, int width, int height, List<Component> tooltips){
        if (isMouseInside(mouseX, mouseY, x, y, width, height)){
            gui.renderComponentTooltip(font, tooltips, mouseX, mouseY);
        }
    }

    /** 多行文本 + 物品的复杂悬浮框（对应旧 drawStackText）。 */
    public void drawStackText(GuiGraphics gui, List<Component> lines, List<ItemStack> stacks, int x, int y){
        List<Component> combined = new ArrayList<>(lines);
        for (ItemStack stack : stacks){
            combined.add(Component.literal(" ").append(stack.getHoverName()));
        }
        gui.renderComponentTooltip(font, combined, x, y);
    }

    //====================== 信息图标 ======================

    /** 画 gui_utility.png 中的信息图标（对应旧 drawInfoPanel）。 */
    public void drawInfoPanel(GuiGraphics gui, int x, int y, int type) {
        switch(type) {
            case 0: gui.blit(GUI_UTIL, x, y, 0, 0, 8, 8); break;   //Small blue I
            case 1: gui.blit(GUI_UTIL, x, y, 0, 8, 8, 8); break;   //Small green I
            case 2: gui.blit(GUI_UTIL, x, y, 8, 0, 16, 16); break; //Large blue I
            case 3: gui.blit(GUI_UTIL, x, y, 24, 0, 16, 16); break;//Large green I
            case 4: gui.blit(GUI_UTIL, x, y, 0, 16, 8, 8); break;  //Small red !
            case 5: gui.blit(GUI_UTIL, x, y, 0, 24, 8, 8); break;  //Small yellow !
            case 6: gui.blit(GUI_UTIL, x, y, 8, 16, 16, 16); break;//Large red !
            case 7: gui.blit(GUI_UTIL, x, y, 24, 16, 16, 16); break;//Large yellow !
            case 8: gui.blit(GUI_UTIL, x, y, 0, 32, 8, 8); break;  //Small blue *
            case 9: gui.blit(GUI_UTIL, x, y, 0, 40, 8, 8); break;  //Small grey *
            case 10: gui.blit(GUI_UTIL, x, y, 8, 32, 16, 16); break;//Large blue *
            case 11: gui.blit(GUI_UTIL, x, y, 24, 32, 16, 16); break;//Large grey *
        }
    }
    /** 兼容旧签名（width 参数在 gui_utility 图标中无意义）。 */
    public void drawInfoPanel(GuiGraphics gui, int x, int y, int width, int type) {
        this.drawInfoPanel(gui, x, y, type);
    }

    //====================== 交互判定 ======================

    /** 鼠标是否在相对 GUI 左上角的区域内（leftPos/topPos 偏移已处理）。 */
    protected boolean checkClick(int mouseX, int mouseY, int left, int top, int sizeX, int sizeY){
        return leftPos + left <= mouseX && leftPos + left + sizeX > mouseX && topPos + top < mouseY && topPos + top + sizeY >= mouseY;
    }

    /** 鼠标是否悬停在给定区域内（绝对坐标）。 */
    protected boolean isMouseInside(int mouseX, int mouseY, int x, int y, int width, int height){
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    //====================== 物品渲染 ======================

    /** 在 GUI 中渲染物品（含光影设置，对应旧 renderItem）。 */
    public void renderItem(GuiGraphics gui, ItemStack stack, int x, int y){
        gui.renderItem(stack, leftPos + x, topPos + y);
    }

    /** 在 GUI 中渲染物品及其数量角标。 */
    public void renderItemWithCount(GuiGraphics gui, ItemStack stack, int x, int y){
        gui.renderItem(stack, leftPos + x, topPos + y);
        gui.renderItemDecorations(font, stack, leftPos + x, topPos + y);
    }

    //====================== 升级信息 ======================

    /** 从方块实体收集升级信息用于显示（对应旧 getUpgradeInfo）。 */
    public List<Component> getUpgradeInfo(BlockEntity tile) {
        List<Component> lines = new ArrayList<>();
        if (tile instanceof IUpgradeInfoProvider provider){
            lines.add(Component.translatable("upgrade.gui.title").withStyle(ChatFormatting.GOLD));
            for (var entry : provider.getValidUpgrades().entrySet()){
                int tier = entry.getValue();
                if (provider.canProvideInfo(entry.getKey(), tier)){
                    provider.provideInfo(entry.getKey(), tier, lines);
                }
            }
        }
        return lines;
    }

    //=======================背景============================
    protected void showBgTexture(GuiGraphics pGuiGraphics, ResourceLocation texture){
        pGuiGraphics.blit(texture,leftPos,topPos,0,0,imageWidth,imageHeight);
    }
}
