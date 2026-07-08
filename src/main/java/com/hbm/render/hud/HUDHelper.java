package com.hbm.render.hud;

import com.hbm.config.ClientConfig;
import com.hbm.network.ClientMsgHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import java.util.List;

public class HUDHelper {
    public static boolean shouldDraw(ResourceLocation overlayId){
        ClientHUDDataCache clientHUDDataCache = ClientMsgHandler.getOrCreate().getTempData().get(overlayId);
        if (clientHUDDataCache == null) return false;
        if (System.currentTimeMillis() - clientHUDDataCache.startTime() > clientHUDDataCache.displayMillis()) {
            ClientMsgHandler.getOrCreate().getTempData().remove(overlayId);
            return false;
        }
        return true;
    }
    /*
    * 绘制可以显示文字的方形框
    * */
    public void drawInfoBoard(List<Component> infos, ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight){
        int longest = 0;
        for (Component info : infos) {
            int width = Minecraft.getInstance().font.width(info);
            if (width > longest) longest = width;
        }
        int mode = ClientConfig.INFO_POSITION.get();
        int pX = mode == 0 ? 15 : mode == 1 ? (screenWidth - longest - 15) : mode == 2 ? (screenWidth / 2 + 7) : (screenWidth / 2 - longest - 6);
        int pZ = mode == 0 ? 15 : mode == 1 ? 15 : screenHeight / 2 + 7;
        pX += ClientConfig.INFO_OFFSET_HORIZONTAL.get();
        pZ += ClientConfig.INFO_OFFSET_VERTICAL.get();
        int height = infos.size() * (gui.getFont().lineHeight + 2) + pZ + 2;

        // 绘制背景
        guiGraphics.fill(pX - 5, pZ - 5, pX + 5 + longest, pZ + 5 + height, FastColor.ABGR32.color(127, 63, 63, 63));
        // 绘制文字
        drawText(infos, pX, pZ, gui, guiGraphics, screenWidth, screenHeight);
    }

    public void drawText(List<Component> desc, int pX, int pY, ForgeGui gui, GuiGraphics graphics, int screenWidth, int screenHeight){
        if (desc == null || desc.isEmpty()) {
            return;
        }
        int fontHeight = gui.getFont().lineHeight;
        int lineSpace = 2;  // 暂时把间距设为固定值
        int renderX = pX;
        int renderY = pY;
        for (Component component : desc) {
            if (component == null) {
                continue;
            }
            graphics.drawString(gui.getFont(), component.getVisualOrderText(), renderX, renderY, 0xFFFFFF);
            renderY += fontHeight + lineSpace;
        }
    }
}
