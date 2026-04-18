package com.hbm.render.blockentity;

import com.hbm.render.ModRenderTypes;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

final class RBMKPanelRenderHelper {

    private RBMKPanelRenderHelper() {
    }

    static void drawRect(Matrix4f matrix, MultiBufferSource buffer, float x0, float y0, float x1, float y1, int argb) {
        drawRect(matrix, buffer, x0, y0, x1, y1, 0.0F, argb);
    }

    static void drawRect(Matrix4f matrix, MultiBufferSource buffer, float x0, float y0, float x1, float y1, float z, int argb) {
        VertexConsumer consumer = buffer.getBuffer(ModRenderTypes.RBMK_PANEL);
        float a = ((argb >> 24) & 0xFF) / 255.0F;
        float r = ((argb >> 16) & 0xFF) / 255.0F;
        float g = ((argb >> 8) & 0xFF) / 255.0F;
        float b = (argb & 0xFF) / 255.0F;

        consumer.vertex(matrix, x0, y1, z).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, x1, y1, z).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, x1, y0, z).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, x0, y0, z).color(r, g, b, a).endVertex();
    }

    static void drawFrame(Matrix4f matrix, MultiBufferSource buffer, float x0, float y0, float x1, float y1,
                          int fillColor, int borderColor) {
        drawRect(matrix, buffer, x0, y0, x1, y1, 0.0002F, fillColor);
        drawRect(matrix, buffer, x0, y0, x1, y0 + 1.0F, 0.0004F, borderColor);
        drawRect(matrix, buffer, x0, y1 - 1.0F, x1, y1, 0.0004F, borderColor);
        drawRect(matrix, buffer, x0, y0, x0 + 1.0F, y1, 0.0004F, borderColor);
        drawRect(matrix, buffer, x1 - 1.0F, y0, x1, y1, 0.0004F, borderColor);
    }

    static int argb(int alpha, int rgb) {
        return ((alpha & 0xFF) << 24) | (rgb & 0x00FFFFFF);
    }
}
