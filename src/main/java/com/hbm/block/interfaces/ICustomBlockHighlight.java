package com.hbm.block.interfaces;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.opengl.GL11;

public interface ICustomBlockHighlight {
    @OnlyIn(Dist.CLIENT) public boolean shouldDrawHighlight(Level world, BlockPos pPos);
    @OnlyIn(Dist.CLIENT) public void drawHighlight(RenderHighlightEvent event, Level world, BlockPos pPos);

    @OnlyIn(Dist.CLIENT)
    public static void setup() {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770,771,1,0);
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 0.4F);
        RenderSystem.lineWidth(2.0F);
        RenderSystem.disableDepthTest();
        GL11.glDisable(GL11.GL_TEXTURE_2D);     //这个实在没在MC系统里找到，直接用的GL11的东西
        RenderSystem.depthMask(false);
    }

    @OnlyIn(Dist.CLIENT)
    public static void cleanup() {
        RenderSystem.depthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);      //同上
        RenderSystem.disableBlend();
    }
}
