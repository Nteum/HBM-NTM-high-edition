package com.hbm.core.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * 渲染工具类。
 *
 * 原版 hbm 移植时残留大量 1.7.10 的 OpenGL 立即模式调用（GL11.glBegin/glVertex/glColor 等），
 * 这些在 1.20.1 的 RenderSystem 中没有对应 API。本类将这些"拿不准"的旧调用封装为
 * 1.20.1 的 Tesselator/BufferBuilder 现代写法，移植时遇到无法直接替换的 GL 调用，
 * 优先在此类添加封装，并附上对应旧 API 的注释说明。
 *
 * 已封装：
 * - begin/vertex/end 立即模式（对应 GL11.glBegin/glEnd + glVertex3f/glNormal3f/glTexCoord2f）
 * - 简单方块/平面绘制
 * - 状态机切换（深度测试、混合等，对应 GL11 对应开关）
 */
public class CoreRenderUtil {

    private CoreRenderUtil(){}

    //====================== 立即模式封装 ======================

    /**
     * 对应旧 GL11.glBegin(GLenum mode)。
     * 旧代码：GL11.glBegin(GL11.GL_QUADS); GL11.glVertex3f(...); ...; GL11.glEnd();
     * 新代码：Tesselator 的 begin(VertexFormat.Mode, DefaultVertexFormat.POSITION_COLOR)
     */
    public static Tesselator begin(VertexFormat.Mode mode){
        return Tesselator.getInstance();
    }

    /**
     * 对应旧 GL11.glEnd()。提交缓冲并渲染。
     */
    public static void end(Tesselator tesselator){
        tesselator.end();
    }

    /**
     * 对应旧 GL11.glVertex3f(x, y, z) + glColor4f(r,g,b,a)。
     * 需要在 begin/end 之间调用。
     */
    public static void vertex(VertexConsumer consumer, float x, float y, float z, float r, float g, float b, float a){
        consumer.vertex(x, y, z).color(r, g, b, a).endVertex();
    }

    /**
     * 对应旧 GL11.glVertex3f + glNormal3f。
     */
    public static void vertexNormal(VertexConsumer consumer, float x, float y, float z, float nx, float ny, float nz){
        consumer.vertex(x, y, z).normal(nx, ny, nz).endVertex();
    }

    /**
     * 对应旧 GL11.glVertex3f + glTexCoord2f。
     */
    public static void vertexUV(VertexConsumer consumer, float x, float y, float z, float u, float v, float r, float g, float b, float a){
        consumer.vertex(x, y, z).color(r, g, b, a).uv(u, v).endVertex();
    }

    //====================== 方块绘制 ======================

    /**
     * 绘制一个纯色立方体（对应旧 GL11 手绘方块）。
     * @param matrix 模型视图投影矩阵
     * @param minX/maxX 包围盒
     */
    public static void drawBox(Matrix4f matrix, float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                               float r, float g, float b, float a){
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // 六个面
        drawQuadX(matrix, buffer, maxX, minY, minZ, maxZ, maxY, Direction.EAST, r, g, b, a);
        drawQuadX(matrix, buffer, minX, minY, minZ, maxZ, maxY, Direction.WEST, r, g, b, a);
        drawQuadY(matrix, buffer, minX, maxY, minZ, maxX, maxZ, Direction.UP, r, g, b, a);
        drawQuadY(matrix, buffer, minX, minY, minZ, maxX, maxZ, Direction.DOWN, r, g, b, a);
        drawQuadZ(matrix, buffer, minX, minY, maxZ, maxX, maxY, Direction.SOUTH, r, g, b, a);
        drawQuadZ(matrix, buffer, minX, minY, minZ, maxX, maxY, Direction.NORTH, r, g, b, a);

        tesselator.end();
    }

    private static void drawQuadX(Matrix4f matrix, VertexConsumer consumer, float x, float y0, float z0, float z1, float y1, Direction dir, float r, float g, float b, float a){
        consumer.vertex(matrix, x, y0, z1).color(r,g,b,a).endVertex();
        consumer.vertex(matrix, x, y1, z1).color(r,g,b,a).endVertex();
        consumer.vertex(matrix, x, y1, z0).color(r,g,b,a).endVertex();
        consumer.vertex(matrix, x, y0, z0).color(r,g,b,a).endVertex();
    }
    private static void drawQuadY(Matrix4f matrix, VertexConsumer consumer, float x0, float y, float z0, float x1, float z1, Direction dir, float r, float g, float b, float a){
        consumer.vertex(matrix, x1, y, z0).color(r,g,b,a).endVertex();
        consumer.vertex(matrix, x1, y, z1).color(r,g,b,a).endVertex();
        consumer.vertex(matrix, x0, y, z1).color(r,g,b,a).endVertex();
        consumer.vertex(matrix, x0, y, z0).color(r,g,b,a).endVertex();
    }
    private static void drawQuadZ(Matrix4f matrix, VertexConsumer consumer, float x0, float y0, float z, float x1, float y1, Direction dir, float r, float g, float b, float a){
        consumer.vertex(matrix, x1, y0, z).color(r,g,b,a).endVertex();
        consumer.vertex(matrix, x1, y1, z).color(r,g,b,a).endVertex();
        consumer.vertex(matrix, x0, y1, z).color(r,g,b,a).endVertex();
        consumer.vertex(matrix, x0, y0, z).color(r,g,b,a).endVertex();
    }

    //====================== 状态机切换 ======================

    /** 对应旧 GL11.glEnable(GL_DEPTH_TEST) / glDisable */
    public static void depthTest(boolean enable){
        if (enable) RenderSystem.enableDepthTest();
        else RenderSystem.disableDepthTest();
    }
    /** 对应旧 GL11.glEnable(GL_BLEND) / glDisable，并设置混合因子 */
    public static void blend(boolean enable){
        if (enable) RenderSystem.enableBlend();
        else RenderSystem.disableBlend();
    }
    /** 对应旧 GL11.glEnable(GL_CULL_FACE) / glDisable */
    public static void cullFace(boolean enable){
        if (enable) RenderSystem.enableCull();
        else RenderSystem.disableCull();
    }
    /** 对应旧 GL11.glColor4f(r,g,b,a)，设置着色器颜色 */
    public static void color(float r, float g, float b, float a){
        RenderSystem.setShaderColor(r, g, b, a);
    }
    /** 对应旧 GL11.glPushMatrix / glPopMatrix（矩阵栈，新版本一般通过 Matrix4f 传递） */
    public static Matrix4f pushMatrix(Matrix4f base){
        return new Matrix4f(base);
    }
    /** 对应旧 GL11.glTranslatef，返回平移后的矩阵 */
    public static Matrix4f translate(Matrix4f matrix, float x, float y, float z){
        return matrix.translate(new Vector3f(x, y, z));
    }
}
