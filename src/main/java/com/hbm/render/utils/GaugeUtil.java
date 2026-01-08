package com.hbm.render.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class GaugeUtil {
    private GaugeUtil() {
    }

    public static void drawSmoothGauge(GuiGraphics graphics, int x, int y, float z, double progress,
                                       double tipLength, double backLength, double backSide,
                                       int color, int colorOuter) {
        progress = Mth.clamp(progress, 0D, 1D);
        double angle = Math.toRadians(-progress * 270D - 45D);

        double tipX = 0D;
        double tipY = tipLength;
        double leftX = backSide;
        double leftY = -backLength;
        double rightX = -backSide;
        double rightY = -backLength;

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        double tipXR = tipX * cos - tipY * sin;
        double tipYR = tipX * sin + tipY * cos;
        double leftXR = leftX * cos - leftY * sin;
        double leftYR = leftX * sin + leftY * cos;
        double rightXR = rightX * cos - rightY * sin;
        double rightYR = rightX * sin + rightY * cos;

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        Matrix4f matrix = graphics.pose().last().pose();
        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        addTriangle(buffer, matrix, x, y, z, tipXR * 1.5, tipYR * 1.5, leftXR * 1.5, leftYR * 1.5, rightXR * 1.5, rightYR * 1.5, colorOuter);
        addTriangle(buffer, matrix, x, y, z, tipXR, tipYR, leftXR, leftYR, rightXR, rightYR, color);

        tessellator.end();
        RenderSystem.disableBlend();
    }

    private static void addTriangle(BufferBuilder buffer, Matrix4f matrix, int x, int y, float z,
                                    double tipX, double tipY, double leftX, double leftY, double rightX, double rightY,
                                    int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        buffer.vertex(matrix, (float) (x + tipX), (float) (y + tipY), z).color(r, g, b, 255).endVertex();
        buffer.vertex(matrix, (float) (x + leftX), (float) (y + leftY), z).color(r, g, b, 255).endVertex();
        buffer.vertex(matrix, (float) (x + rightX), (float) (y + rightY), z).color(r, g, b, 255).endVertex();
    }
}
