package com.hbm.gui.screen;

import com.hbm.HBM;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;

import java.util.HashMap;
import java.util.Map;

public class RenderUtils {
    public static final ResourceLocation WATER = HBM.rl("textures/fluid/water_overlay.png");
    private static final ResourceLocation WATER_OPAQUE = HBM.rl("textures/gui/fluids/water_opaque_base.png");
    public static final Map<Fluid, Integer> fluidColor = new HashMap<>();
    public static void init() {
        fluidColor.put(Fluids.EMPTY, 0xffffff);
        fluidColor.put(Fluids.WATER, MapColor.WATER.col);
        fluidColor.put(Fluids.LAVA, MapColor.FIRE.col);
    }
    public static void fluidTank(int x, int y, int width, int height, float ratio, GuiGraphics pGuiGraphics, Fluid fluid){
        if (ratio == 0.0)return;
        int minX = x, maxX = x + width;
        int minY = y - (int) (height * ratio), maxY = y;
        int maxV = (int) (16 * ratio);
        Integer color = fluidColor.get(fluid);
        float r = ((color & 0xff0000) >> 16) / 255F;
        float g = ((color & 0x00ff00) >> 8) / 255F;
        float b = ((color & 0x0000ff) >> 0) / 255F;
        pGuiGraphics.setColor(r,g,b, 1.0F);
        pGuiGraphics.blit(WATER,minX,minY,width,(int) (height * ratio),0,0,16,maxV,16,16);
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
