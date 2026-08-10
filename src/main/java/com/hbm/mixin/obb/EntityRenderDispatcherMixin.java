package com.hbm.mixin.obb;

import com.hbm.core.contents.obb.OBBEntity;
import com.hbm.core.contents.obb.renderer.OBBRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 为 OBB 实体注入 F3+B 调试碰撞箱渲染。
 *
 * <p>当实体实现了 {@link OBBEntity} 且未启用 AABB 模式时，
 * 在 F3+B 模式下渲染其所有 OBB 碰撞箱，替代原版的 AABB 线框。
 *
 * <p>不同颜色含义：
 * <ul>
 *   <li>绿色 —— 一般 OBB（BODY, TURRET 等）</li>
 *   <li>橙色 —— INTERACTIVE 类型</li>
 *   <li>红色 —— COLLISION 类型</li>
 * </ul>
 */
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    /**
     * 在原版 {@code renderHitbox} 之后渲染 OBB 碰撞箱。
     *
     * <p>使用 {@code At("RETURN")} 确保在原版 AABB 线框渲染后叠加 OBB，
     * 这样两种渲染方式都能看到，方便对比调试。
     *
     * @param poseStack     姿态矩阵栈
     * @param buffer        顶点缓冲
     * @param entity        当前渲染的实体
     * @param partialTicks  部分 tick 插值
     * @param ci            Mixin 回调信息
     */
    @Inject(method = "renderHitbox(Lcom/mojang/blaze3d/vertex/PoseStack;"
            + "Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            + "Lnet/minecraft/world/entity/Entity;"
            + "F)V",
            at = @At("RETURN"))
    private static void onRenderHitbox(PoseStack poseStack, VertexConsumer buffer,
                                        Entity entity, float partialTicks,
                                        CallbackInfo ci) {
        // 仅对 OBBEntity 且非 AABB 模式时渲染 OBB 碰撞箱
        if (entity instanceof OBBEntity obbEntity && !obbEntity.enableAABB()) {
            OBBRenderer.render(
                obbEntity.getOBBs(),
                entity,
                poseStack,
                buffer,
                partialTicks
            );
        }
    }
}
