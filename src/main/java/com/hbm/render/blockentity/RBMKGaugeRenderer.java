package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.rbmk.RBMKGaugeEntity;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

/**
 * Recreates the legacy 2x2 RBMK gauge panel with independent needle dials.
 */
public class RBMKGaugeRenderer implements BlockEntityRenderer<RBMKGaugeEntity> {

    private static final float PANEL_SCALE = 1.0F / 128.0F;
    private static final float FACE_Z = 0.5030F;
    private static final int PANEL_FILL = 0xFF101410;
    private static final int PANEL_BORDER = 0xFF314531;
    private static final int SCALE_COLOR = 0xFF4A654A;
    private static final int LABEL_COLOR = 0xFF8EBC8E;

    public RBMKGaugeRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RBMKGaugeEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        Font font = Minecraft.getInstance().font;
        BlockState state = blockEntity.getBlockState();

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        DirectionUtils.generalMachineRotate(poseStack, state.getValue(com.hbm.block.machine.BaseSingleBlockMachine.FACING), 0.0F, 0.0F);
        poseStack.translate(0.0D, 0.0D, FACE_Z);
        poseStack.scale(PANEL_SCALE, -PANEL_SCALE, PANEL_SCALE);

        int emissiveLight = LightTexture.FULL_BRIGHT;
        renderGauge(font, poseStack, buffer, blockEntity, 0, -58.0F, -58.0F, emissiveLight);
        renderGauge(font, poseStack, buffer, blockEntity, 1, 2.0F, -58.0F, emissiveLight);
        renderGauge(font, poseStack, buffer, blockEntity, 2, -58.0F, 2.0F, emissiveLight);
        renderGauge(font, poseStack, buffer, blockEntity, 3, 2.0F, 2.0F, emissiveLight);

        poseStack.popPose();
    }

    private static void renderGauge(Font font, PoseStack poseStack, MultiBufferSource buffer, RBMKGaugeEntity entity,
                                    int slot, float x, float y, int packedLight) {
        if (!entity.isActive(slot)) {
            return;
        }
        Matrix4f matrix = poseStack.last().pose();
        float x1 = x + 56.0F;
        float y1 = y + 56.0F;
        RBMKPanelRenderHelper.drawFrame(matrix, buffer, x, y, x1, y1, PANEL_FILL, PANEL_BORDER);

        float cx = x + 28.0F;
        float cy = y + 33.0F;
        for (int i = 0; i <= 10; i++) {
            float angle = (float) Math.toRadians(-70.0F + i * 14.0F);
            float ox = Mth.cos(angle);
            float oy = Mth.sin(angle);
            drawSegment(poseStack, buffer, cx + ox * 13.0F, cy + oy * 13.0F, cx + ox * 17.0F, cy + oy * 17.0F,
                    1.2F, SCALE_COLOR);
        }

        float normalized = Mth.clamp(entity.getNormalized(slot), 0.0F, 1.0F);
        float needleAngle = (float) Math.toRadians(-70.0F + normalized * 140.0F);
        float nx = Mth.cos(needleAngle);
        float ny = Mth.sin(needleAngle);
        int needleColor = RBMKPanelRenderHelper.argb(255, entity.getColor(slot));
        drawSegment(poseStack, buffer, cx, cy, cx + nx * 15.0F, cy + ny * 15.0F, 2.0F, needleColor);
        RBMKPanelRenderHelper.drawRect(matrix, buffer, cx - 1.8F, cy - 1.8F, cx + 1.8F, cy + 1.8F, needleColor);

        String metric = entity.getLabel(slot);
        if (metric == null || metric.isBlank()) {
            metric = entity.getMetric(slot).shortLabel();
        }
        String value = entity.getDisplay(slot);
        String minText = Integer.toString(entity.getMinValue(slot));
        String maxText = Integer.toString(entity.getMaxValue(slot));
        int metricWidth = font.width(metric);
        int valueWidth = font.width(value);
        int maxWidth = font.width(maxText);
        Matrix4f textMatrix = new Matrix4f(matrix).translate(0.0F, 0.0F, 0.0020F);
        font.drawInBatch(metric, cx - metricWidth * 0.5F, y + 7.0F, LABEL_COLOR, false, textMatrix, buffer, Font.DisplayMode.NORMAL, 0, packedLight);
        font.drawInBatch(minText, x + 5.0F, y + 20.0F, 0x809080, false, textMatrix, buffer, Font.DisplayMode.NORMAL, 0, packedLight);
        font.drawInBatch(maxText, x1 - 5.0F - maxWidth, y + 20.0F, 0x809080, false, textMatrix, buffer, Font.DisplayMode.NORMAL, 0, packedLight);
        font.drawInBatch(value, cx - valueWidth * 0.5F, y + 44.0F, entity.getColor(slot), false, textMatrix, buffer, Font.DisplayMode.NORMAL, 0, packedLight);
    }

    private static void drawSegment(PoseStack poseStack, MultiBufferSource buffer, float x0, float y0, float x1, float y1,
                                    float thickness, int argb) {
        float dx = x1 - x0;
        float dy = y1 - y0;
        float len = Mth.sqrt(dx * dx + dy * dy);
        if (len < 0.001F) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate((x0 + x1) * 0.5F, (y0 + y1) * 0.5F, 0.0F);
        poseStack.mulPose(Axis.ZP.rotation((float) Math.atan2(dy, dx)));
        RBMKPanelRenderHelper.drawRect(poseStack.last().pose(), buffer, -len * 0.5F, -thickness * 0.5F,
                len * 0.5F, thickness * 0.5F, argb);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(RBMKGaugeEntity blockEntity) {
        return true;
    }
}
