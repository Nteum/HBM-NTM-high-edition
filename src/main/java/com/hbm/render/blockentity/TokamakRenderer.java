package com.hbm.render.blockentity;

import com.hbm.HBM;
import com.hbm.blockentity.machine.tokamak.TokamakControllerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;

/**
 * 托卡马克等离子体渲染：
 * - 读取控制器提供的半径/亮度/旋转速度/不稳定度
 * - 使用能量紊流渲染类型模拟 Geckolib shader 样式
 * - 若资源缺失则自动使用紫黑方格，不影响功能
 */
public class TokamakRenderer implements BlockEntityRenderer<TokamakControllerBlockEntity> {

    private static final ResourceLocation PLASMA_TEX = new ResourceLocation(HBM.MODID, "textures/effects/tokamak_plasma.png");

    public TokamakRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(TokamakControllerBlockEntity tile, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (tile == null) return;
        Level level = tile.getLevel();
        if (level == null) return;
        float radius = tile.getPlasmaRadius();
        float brightness = tile.getBrightness();
        float instability = tile.getInstabilityFactor();
        float swirlSpeed = tile.getSwirlSpeed();
        float time = (level.getGameTime() + partialTicks) * swirlSpeed * 0.02F;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.6, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(time * 360));

        poseStack.scale(radius, radius * 0.6F, radius);
        RenderType type = RenderType.energySwirl(PLASMA_TEX, time, time * 0.5F);
        VertexConsumer vc = buffer.getBuffer(type);
        Matrix4f mat = poseStack.last().pose();

        // 使用两个交叉的平面模拟环形发光带
        float alpha = Mth.clamp(brightness + instability * 0.5F, 0.2F, 1.0F);
        float color = Mth.clamp(0.5F + brightness, 0.5F, 1.0F);
        drawQuad(mat, vc, color, alpha, packedLight);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        mat = poseStack.last().pose();
        drawQuad(mat, vc, color, alpha * 0.9F, packedLight);

        poseStack.popPose();
    }

    private void drawQuad(Matrix4f mat, VertexConsumer vc, float color, float alpha, int light) {
        vc.vertex(mat, -1, 0, -1).color(color, color * 0.6F, 1.0F, alpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
        vc.vertex(mat, -1, 0, 1).color(color, color * 0.6F, 1.0F, alpha).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
        vc.vertex(mat, 1, 0, 1).color(color, color * 0.6F, 1.0F, alpha).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
        vc.vertex(mat, 1, 0, -1).color(color, color * 0.6F, 1.0F, alpha).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
    }
}
