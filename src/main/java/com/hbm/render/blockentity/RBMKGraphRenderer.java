package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.rbmk.RBMKGraphEntity;
import com.hbm.reactor.rbmk.RBMKMonitorMetric;
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
 * Two-channel RBMK graph panel renderer. Uses sampled history curves like the
 * legacy panel instead of generic text spark-lines.
 */
public class RBMKGraphRenderer implements BlockEntityRenderer<RBMKGraphEntity> {

    private static final float PANEL_SCALE = 1.0F / 128.0F;
    private static final float FACE_Z = 0.5030F;
    private static final int PANEL_FILL = 0xFF101410;
    private static final int PANEL_BORDER = 0xFF314531;
    private static final int CHART_FILL = 0xFF071007;
    private static final int CHART_BORDER = 0xFF2A402A;
    private static final int LABEL_COLOR = 0xFF8EBC8E;
    private static final int AXIS_COLOR = 0xFF486648;
    private static final int DRAW_POINTS = 15;

    public RBMKGraphRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RBMKGraphEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        Font font = Minecraft.getInstance().font;
        BlockState state = blockEntity.getBlockState();

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        DirectionUtils.generalMachineRotate(poseStack, state.getValue(com.hbm.block.machine.BaseSingleBlockMachine.FACING), 0.0F, 0.0F);
        poseStack.translate(0.0D, 0.0D, FACE_Z);
        poseStack.scale(PANEL_SCALE, -PANEL_SCALE, PANEL_SCALE);

        int emissiveLight = LightTexture.FULL_BRIGHT;
        renderChannel(font, poseStack, buffer, blockEntity.getMetric(0), blockEntity.getDisplay(0),
                blockEntity.getHistory(0), blockEntity.getColor(0), -58.0F, -58.0F, emissiveLight);
        renderChannel(font, poseStack, buffer, blockEntity.getMetric(1), blockEntity.getDisplay(1),
                blockEntity.getHistory(1), blockEntity.getColor(1), -58.0F, 2.0F, emissiveLight);

        poseStack.popPose();
    }

    private static void renderChannel(Font font, PoseStack poseStack, MultiBufferSource buffer, RBMKMonitorMetric metric,
                                      String display, int[] history, int color, float x, float y, int packedLight) {
        Matrix4f matrix = poseStack.last().pose();
        float x1 = x + 116.0F;
        float y1 = y + 56.0F;
        RBMKPanelRenderHelper.drawFrame(matrix, buffer, x, y, x1, y1, PANEL_FILL, PANEL_BORDER);

        float chartX0 = x + 4.0F;
        float chartY0 = y + 18.0F;
        float chartX1 = x1 - 4.0F;
        float chartY1 = y1 - 5.0F;
        RBMKPanelRenderHelper.drawFrame(matrix, buffer, chartX0, chartY0, chartX1, chartY1, CHART_FILL, CHART_BORDER);
        RBMKPanelRenderHelper.drawRect(matrix, buffer, chartX0 + 1.0F, chartY1 - 1.0F, chartX1 - 1.0F, chartY1, AXIS_COLOR);
        RBMKPanelRenderHelper.drawRect(matrix, buffer, chartX0 + 1.0F, chartY0 + 1.0F, chartX0 + 2.0F, chartY1 - 1.0F, AXIS_COLOR);

        int[] samples = downsample(history, DRAW_POINTS);
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int sample : samples) {
            min = Math.min(min, sample);
            max = Math.max(max, sample);
        }
        if (min == Integer.MAX_VALUE) {
            min = 0;
            max = 0;
        }
        int range = Math.max(1, max - min);
        float chartHeight = (chartY1 - chartY0) - 4.0F;
        float chartWidth = (chartX1 - chartX0) - 6.0F;

        float prevX = 0.0F;
        float prevY = 0.0F;
        for (int i = 0; i < samples.length; i++) {
            float t = (samples[i] - min) / (float) range;
            float px = chartX0 + 3.0F + chartWidth * (i / (float) Math.max(1, samples.length - 1));
            float py = chartY1 - 2.0F - t * chartHeight;
            if (i > 0) {
                drawSegment(poseStack, buffer, prevX, prevY, px, py, 1.6F, RBMKPanelRenderHelper.argb(255, color));
            }
            prevX = px;
            prevY = py;
        }

        String metricName = metric.shortLabel();
        int valueWidth = font.width(display);
        Matrix4f textMatrix = new Matrix4f(matrix).translate(0.0F, 0.0F, 0.0020F);
        font.drawInBatch(metricName, x + 4.0F, y + 7.0F, LABEL_COLOR, false, textMatrix, buffer, Font.DisplayMode.NORMAL, 0, packedLight);
        font.drawInBatch(display, x1 - 4.0F - valueWidth, y + 7.0F, color, false, textMatrix, buffer, Font.DisplayMode.NORMAL, 0, packedLight);

        String minText = Integer.toString(min);
        String maxText = Integer.toString(max);
        font.drawInBatch(maxText, chartX1 - 2.0F - font.width(maxText), chartY0 + 2.0F, LABEL_COLOR, false, textMatrix, buffer, Font.DisplayMode.NORMAL, 0, packedLight);
        font.drawInBatch(minText, chartX1 - 2.0F - font.width(minText), chartY1 - 9.0F, LABEL_COLOR, false, textMatrix, buffer, Font.DisplayMode.NORMAL, 0, packedLight);
    }

    private static int[] downsample(int[] history, int points) {
        if (points <= 0) {
            return new int[0];
        }
        if (history.length == 0) {
            return new int[points];
        }

        int[] sampled = new int[points];
        for (int i = 0; i < points; i++) {
            int start = (int) Math.floor(i * history.length / (double) points);
            int end = (int) Math.floor((i + 1) * history.length / (double) points);
            if (i == points - 1) {
                end = history.length;
            }
            if (end <= start) {
                end = Math.min(history.length, start + 1);
            }

            int sample = Integer.MIN_VALUE;
            for (int j = start; j < end; j++) {
                sample = Math.max(sample, history[j]);
            }
            sampled[i] = sample == Integer.MIN_VALUE ? 0 : sample;
        }
        return sampled;
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
    public boolean shouldRenderOffScreen(RBMKGraphEntity blockEntity) {
        return true;
    }
}
