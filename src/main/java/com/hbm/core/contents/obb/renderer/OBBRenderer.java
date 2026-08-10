package com.hbm.core.contents.obb.renderer;

import com.hbm.core.contents.obb.OBB;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.joml.Quaterniond;
import org.joml.Quaternionf;

import java.util.List;

/**
 * OBB 碰撞箱调试渲染器 —— 在 F3+B 调试模式下绘制 OBB 碰撞箱。
 *
 * <p>通过 Mixin 注入到 {@code EntityRenderDispatcher.renderHitbox()} 中，
 * 当实体实现了 {@link com.hbm.core.contents.obb.OBBEntity} 且未启用 AABB 模式时，
 * 渲染其所有 OBB 碰撞箱以替代原版的 AABB 线框。
 *
 * <p>不同部件类型使用不同颜色：
 * <ul>
 *   <li>绿色（默认）—— 一般 OBB（BODY, TURRET 等）</li>
 *   <li>橙色 —— INTERACTIVE 类型</li>
 *   <li>红色 —— COLLISION 类型</li>
 * </ul>
 *
 * <p>使用方式（在 Mixin 中）：
 * <pre>{@code
 * @Inject(method = "renderHitbox", at = @At("RETURN"))
 * private static void renderHitbox(PoseStack poseStack, VertexConsumer buffer,
 *         Entity entity, float partialTicks, CallbackInfo ci) {
 *     if (entity instanceof OBBEntity obbEntity && !obbEntity.enableAABB()) {
 *         OBBRenderer.render(obbEntity.getOBBs(), entity, poseStack, buffer, partialTicks);
 *     }
 * }
 * }</pre>
 */
public final class OBBRenderer {

    private OBBRenderer() {} // 工具类禁止实例化

    /**
     * 渲染实体的所有 OBB 碰撞箱。
     *
     * @param obbList       OBB 列表
     * @param entity        所属实体（用于计算渲染偏移）
     * @param poseStack     姿态矩阵栈
     * @param buffer        顶点缓冲
     * @param partialTicks  部分 tick 插值
     */
    public static void render(List<OBB> obbList, Entity entity,
                              PoseStack poseStack, VertexConsumer buffer,
                              float partialTicks) {
        // 计算实体渲染位置（插值后的位置）
        double renderX = entity.xOld + (entity.getX() - entity.xOld) * partialTicks;
        double renderY = entity.yOld + (entity.getY() - entity.yOld) * partialTicks;
        double renderZ = entity.zOld + (entity.getZ() - entity.zOld) * partialTicks;

        for (OBB obb : obbList) {
            double centerX = obb.center.x - renderX;
            double centerY = obb.center.y - renderY;
            double centerZ = obb.center.z - renderZ;

            // 根据部件类型选择颜色
            float r, g, b, a;
            switch (obb.part) {
                case INTERACTIVE:
                    r = 1.0f; g = 0.8f; b = 0.0f; a = 1.0f; // 橙色
                    break;
                case COLLISION:
                    r = 1.0f; g = 0.0f; b = 0.0f; a = 1.0f; // 红色
                    break;
                default:
                    r = 0.0f; g = 1.0f; b = 0.0f; a = 1.0f; // 绿色
                    break;
            }

            renderSingleOBB(poseStack, buffer,
                centerX, centerY, centerZ,
                obb.rotation,
                obb.extents.x, obb.extents.y, obb.extents.z,
                r, g, b, a);
        }
    }

    /**
     * 渲染单个 OBB 线框。
     *
     * @param poseStack 姿态矩阵栈
     * @param buffer    顶点缓冲
     * @param centerX   OBB 中心 X（世界空间，相对于实体渲染位置）
     * @param centerY   OBB 中心 Y
     * @param centerZ   OBB 中心 Z
     * @param rotation  OBB 的旋转四元数
     * @param halfX     X 半长
     * @param halfY     Y 半长
     * @param halfZ     Z 半长
     * @param red       红色分量 [0, 1]
     * @param green     绿色分量 [0, 1]
     * @param blue      蓝色分量 [0, 1]
     * @param alpha     不透明度 [0, 1]
     */
    public static void renderSingleOBB(PoseStack poseStack, VertexConsumer buffer,
                                        double centerX, double centerY, double centerZ,
                                        Quaterniond rotation,
                                        double halfX, double halfY, double halfZ,
                                        float red, float green, float blue, float alpha) {
        poseStack.pushPose();

        // 平移到 OBB 中心
        poseStack.translate(centerX, centerY, centerZ);

        // 应用 OBB 旋转（转换为 float 精度四元数）
        poseStack.mulPose(new Quaternionf(
            (float) rotation.x,
            (float) rotation.y,
            (float) rotation.z,
            (float) rotation.w
        ));

        // 使用 Minecraft 内置的线框盒子渲染
        LevelRenderer.renderLineBox(
            poseStack, buffer,
            -halfX, -halfY, -halfZ,
            halfX, halfY, halfZ,
            red, green, blue, alpha
        );

        poseStack.popPose();
    }
}
