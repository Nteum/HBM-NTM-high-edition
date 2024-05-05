package com.hbm.client.gui;

import com.hbm.DalisFunMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ElectricFurnaceScreen extends HandledScreen<ElectricFurnaceScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(DalisFunMod.MOD_ID , "textures/gui/gui_electric_furnace.png");
    public ElectricFurnaceScreen(ElectricFurnaceScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        DalisFunMod.LOGGER.info("-------------------新建ElectricFurnaceScreen");
    }
    //绘制背景（并不是绘制一次，貌似每次都会和render一块被调用
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
//        DalisFunMod.LOGGER.info("---------------------渲染背景");
//        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE,x, y, 0, 0, backgroundWidth, backgroundHeight);
        int progress = this.handler.getProgress();
        int power = this.handler.getPower();
        int maxProgress = this.handler.getMaxProgress();
        if (progress > 0){
            context.drawTexture(TEXTURE,x+56,y+35,176,0,16,16,256,256);
            context.drawTexture(TEXTURE,x+78,y+35,176,17,(int) Math.floor((double) (24 * progress) / maxProgress),16,256,256);
        }
        context.drawTexture(TEXTURE,x+20,y+16 + (52 - (int) Math.floor((double) 52 * power / 100000)),200,0,
                16,(int) Math.floor((double) 52 * power / 100000),256,256);
    }
    //使用中的其他渲染，在背景之上
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context,mouseX,mouseY,delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
    //初始化
    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;  //标题居中
    }
}
