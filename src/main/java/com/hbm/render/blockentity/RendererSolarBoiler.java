package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.SolarBoilerEntity;
import com.hbm.config.ClientConfig;
import com.hbm.core.client.model.CustomPartsModel;
import com.hbm.render.RenderUtils;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class RendererSolarBoiler implements BlockEntityRenderer<SolarBoilerEntity> {
    BakedModel model;
    public RendererSolarBoiler(BlockEntityRendererProvider.Context pContext){
    }
    @Override
    public void render(SolarBoilerEntity pBlockEntity, float pPartialTick, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockState blockState = pBlockEntity.getBlockState();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        if (model == null) model = blockRenderer.getBlockModel(blockState);
        poseStack.pushPose();
        DirectionUtils.generalMachineRotate(poseStack, blockState);
        if (model instanceof CustomPartsModel.Baked customPartsModel) {
            RenderUtils.renderModel(customPartsModel.getPart("Base"), poseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            if (Minecraft.getInstance().options.graphicsMode().get() == GraphicsStatus.FANCY) {
                // 当前图形设置为“精细”或“极佳”

                Level level = pBlockEntity.getLevel();
                if (level == null) return;

                // 2. 选择 RenderType：lightning() 适用于无纹理、混合、透明的线条或光束
                VertexConsumer vertexConsumer = pBuffer.getBuffer(RenderType.lightning());

                int beamLimit = ClientConfig.RENDER_HELIOSTAT_BEAM_LIMIT.get(); // 你的配置
                int beamCount = 0;

                BlockPos origin = pBlockEntity.getBlockPos();

                for (BlockPos target : pBlockEntity.secondary) {
                    if (beamCount++ >= beamLimit) break;

                    int dx = origin.getX() - target.getX();
                    int dy = origin.getY() - target.getY();
                    int dz = origin.getZ() - target.getZ();

                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (dist < 0.1) continue;

                    float minAlpha = 0.005F;
                    float maxAlpha = 0.01F;

                    // 3. 矩阵变换（对应旧版的 GL 操作）
                    poseStack.pushPose();

                    // 平移到目标相对位置（旧版 glTranslated(-dx, -dy, -dz)）
                    poseStack.translate(-dx, -dy, -dz);

                    // 计算旋转角度（与旧版算法一致）
                    double pitch = Math.toDegrees(-Math.asin((dy + 0.5) / dist)) + 90;
                    double yaw = Math.toDegrees(-Math.atan2(dz, dx)) + 180;

                    // 应用旋转（旧版 glTranslated(0,1,0); glRotated(yaw,0,1,0); glRotated(pitch,0,0,1); glTranslated(0,-1,0)）
                    poseStack.translate(0, 1, 0);
                    poseStack.mulPose(Axis.YP.rotationDegrees((float) yaw));
                    poseStack.mulPose(Axis.ZP.rotationDegrees((float) pitch));
                    poseStack.translate(0, -1, 0);

                    // 4. 绘制四个面（四边形），每个面用两个三角形
                    // 定义矩形的四个角（局部坐标）
                    // 近端（起点）y = 1.0625，远端（终点）y = dist
                    float yNear = 1.0625f;
                    float yFar = (float) dist;

                    // 绘制四个面
                    drawQuad(vertexConsumer, poseStack,
                            0.5f, yNear, -0.5f, 0.5f, yNear, 0.5f,
                            0.5f, yFar, 0.5f, 0.5f, yFar, -0.5f,
                            maxAlpha, minAlpha);
                    drawQuad(vertexConsumer, poseStack,
                            -0.5f, yNear, -0.5f, -0.5f, yNear, 0.5f,
                            -0.5f, yFar, 0.5f, -0.5f, yFar, -0.5f,
                            maxAlpha, minAlpha);
                    drawQuad(vertexConsumer, poseStack,
                            -0.5f, yNear, 0.5f, 0.5f, yNear, 0.5f,
                            0.5f, yFar, 0.5f, -0.5f, yFar, 0.5f,
                            maxAlpha, minAlpha);
                    drawQuad(vertexConsumer, poseStack,
                            -0.5f, yNear, -0.5f, 0.5f, yNear, -0.5f,
                            0.5f, yFar, -0.5f, -0.5f, yFar, -0.5f,
                            maxAlpha, minAlpha);

                    poseStack.popPose();
                }
            }
        }
        poseStack.popPose();
    }

    // 绘制一个面（四边形），根据 x 或 z 固定
    private void drawQuadFace(VertexConsumer consumer, PoseStack poseStack,
                              float fixedCoord, float yNear, float yFar,
                              float varStart, float varEnd,
                              float alphaNear, float alphaFar) {
        // 固定维度为 x（当 isZFixed=false）或 z（当 isZFixed=true）
        // 为简化，我们实现两个方法或使用标志
        // 这里用标志：isZFixed = true 表示固定 z，false 表示固定 x
        drawQuadFace(consumer, poseStack, fixedCoord, yNear, yFar, varStart, varEnd, alphaNear, alphaFar, false);
    }

    private void drawQuadFace(VertexConsumer consumer, PoseStack poseStack,
                              float fixedCoord, float yNear, float yFar,
                              float varStart, float varEnd,
                              float alphaNear, float alphaFar,
                              boolean isZFixed) {
        // 顶点顺序：近端两个顶点（alphaNear），远端两个顶点（alphaFar）
        // 三角形1: (0,1,2) ; 三角形2: (0,2,3)
        // 顶点0: (varStart, yNear, fixed) 或 (fixed, yNear, varStart)
        // 顶点1: (varEnd,   yNear, fixed) 或 (fixed, yNear, varEnd)
        // 顶点2: (varEnd,   yFar,  fixed) 或 (fixed, yFar,  varEnd)
        // 顶点3: (varStart, yFar,  fixed) 或 (fixed, yFar,  varStart)
        float x0, y0, z0, x1, y1, z1, x2, y2, z2, x3, y3, z3;
        if (isZFixed) {
            // 固定 z，x 变化
            x0 = varStart; y0 = yNear; z0 = fixedCoord;
            x1 = varEnd;   y1 = yNear; z1 = fixedCoord;
            x2 = varEnd;   y2 = yFar;  z2 = fixedCoord;
            x3 = varStart; y3 = yFar;  z3 = fixedCoord;
        } else {
            // 固定 x，z 变化
            x0 = fixedCoord; y0 = yNear; z0 = varStart;
            x1 = fixedCoord; y1 = yNear; z1 = varEnd;
            x2 = fixedCoord; y2 = yFar;  z2 = varEnd;
            x3 = fixedCoord; y3 = yFar;  z3 = varStart;
        }

        // 添加六个顶点（两个三角形）
        var matrix = poseStack.last().pose();
        // 三角形1: 0-1-2
        consumer.vertex(matrix, x0, y0, z0).color(1f, 1f, 1f, alphaNear).endVertex();
        consumer.vertex(matrix, x1, y1, z1).color(1f, 1f, 1f, alphaNear).endVertex();
        consumer.vertex(matrix, x2, y2, z2).color(1f, 1f, 1f, alphaFar).endVertex();
        // 三角形2: 0-2-3
        consumer.vertex(matrix, x0, y0, z0).color(1f, 1f, 1f, alphaNear).endVertex();
        consumer.vertex(matrix, x2, y2, z2).color(1f, 1f, 1f, alphaFar).endVertex();
        consumer.vertex(matrix, x3, y3, z3).color(1f, 1f, 1f, alphaFar).endVertex();
    }

    private void drawQuad(VertexConsumer consumer, PoseStack poseStack,
                          float x0, float y0, float z0,
                          float x1, float y1, float z1,
                          float x2, float y2, float z2,
                          float x3, float y3, float z3,
                          float alphaNear, float alphaFar) {
        var matrix = poseStack.last().pose();
        // 三角形1: 顶点0-1-2
        consumer.vertex(matrix, x0, y0, z0).color(1f, 1f, 1f, alphaNear).endVertex();
        consumer.vertex(matrix, x1, y1, z1).color(1f, 1f, 1f, alphaNear).endVertex();
        consumer.vertex(matrix, x2, y2, z2).color(1f, 1f, 1f, alphaFar).endVertex();
        // 三角形2: 顶点0-2-3
        consumer.vertex(matrix, x0, y0, z0).color(1f, 1f, 1f, alphaNear).endVertex();
        consumer.vertex(matrix, x2, y2, z2).color(1f, 1f, 1f, alphaFar).endVertex();
        consumer.vertex(matrix, x3, y3, z3).color(1f, 1f, 1f, alphaFar).endVertex();
    }
}
