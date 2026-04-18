package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.rbmk.RBMKNumitronEntity;
import com.hbm.reactor.rbmk.RBMKMonitorMetric;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

/**
 * Lightweight numeric face renderer for the RBMK numitron panel.
 */
public class RBMKNumitronRenderer implements BlockEntityRenderer<RBMKNumitronEntity> {

    private static final float PANEL_SCALE = 1.0F / 128.0F;
    private static final float FACE_Z = 0.5030F;
    private static final int PANEL_FILL = 0xFF101410;
    private static final int PANEL_BORDER = 0xFF314531;
    private static final int WINDOW_FILL = 0xFF070C07;
    private static final int WINDOW_BORDER = 0xFF2B3E2B;
    private static final int LABEL_COLOR = 0xFF8EBC8E;

    public RBMKNumitronRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RBMKNumitronEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        Font font = Minecraft.getInstance().font;
        BlockState state = blockEntity.getBlockState();

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        DirectionUtils.generalMachineRotate(poseStack, state.getValue(com.hbm.block.machine.BaseSingleBlockMachine.FACING), 0.0F, 0.0F);
        poseStack.translate(0.0D, 0.0D, FACE_Z);
        poseStack.scale(PANEL_SCALE, -PANEL_SCALE, PANEL_SCALE);

        int emissiveLight = LightTexture.FULL_BRIGHT;
        renderWindow(font, poseStack.last().pose(), buffer, blockEntity.getMetric(0), blockEntity.getDisplay(0), blockEntity.getColor(0), -58.0F, -58.0F, emissiveLight);
        renderWindow(font, poseStack.last().pose(), buffer, blockEntity.getMetric(1), blockEntity.getDisplay(1), blockEntity.getColor(1), -58.0F, 2.0F, emissiveLight);

        poseStack.popPose();
    }

    private static void renderWindow(Font font, Matrix4f matrix, MultiBufferSource buffer, RBMKMonitorMetric metric,
                                     String value, int color, float x, float y, int packedLight) {
        float x1 = x + 116.0F;
        float y1 = y + 56.0F;
        RBMKPanelRenderHelper.drawFrame(matrix, buffer, x, y, x1, y1, PANEL_FILL, PANEL_BORDER);

        float windowX0 = x + 4.0F;
        float windowY0 = y + 18.0F;
        float windowX1 = x1 - 4.0F;
        float windowY1 = y1 - 6.0F;
        RBMKPanelRenderHelper.drawFrame(matrix, buffer, windowX0, windowY0, windowX1, windowY1, WINDOW_FILL, WINDOW_BORDER);

        Matrix4f textMatrix = new Matrix4f(matrix).translate(0.0F, 0.0F, 0.0020F);
        Matrix4f digitMatrix = new Matrix4f(matrix).translate(0.0F, 0.0F, 0.0010F);
        String label = metric.shortLabel();
        font.drawInBatch(label, x + 4.0F, y + 7.0F, LABEL_COLOR, false, textMatrix, buffer, Font.DisplayMode.NORMAL, 0, packedLight);
        font.drawInBatch(value, x1 - 4.0F - font.width(value), y + 7.0F, color, false, textMatrix, buffer, Font.DisplayMode.NORMAL, 0, packedLight);

        String padded = normalizeDisplay(value);
        float digitX = windowX0 + 6.0F;
        float digitY = windowY0 + 4.0F;
        for (int i = 0; i < padded.length(); i++) {
            drawNumitronChar(font, digitMatrix, buffer, padded.charAt(i), digitX + i * 14.0F, digitY,
                    RBMKPanelRenderHelper.argb(255, color), packedLight);
        }
    }

    private static String normalizeDisplay(String value) {
        if (value == null || value.isBlank() || "----".equals(value)) {
            return "-------";
        }
        String normalized = value.trim();
        while (normalized.length() < 7) {
            normalized = "0" + normalized;
        }
        if (normalized.length() > 7) {
            normalized = normalized.substring(normalized.length() - 7);
        }
        return normalized;
    }

    private static void drawNumitronChar(Font font, Matrix4f matrix, MultiBufferSource buffer, char c,
                                         float x, float y, int argb, int packedLight) {
        float width = 10.0F;
        float height = 20.0F;
        float thick = 1.6F;

        boolean[] segments = switch (c) {
            case '0' -> bits(true, true, true, false, true, true, true);
            case '1' -> bits(false, false, true, false, false, true, false);
            case '2' -> bits(true, false, true, true, true, false, true);
            case '3' -> bits(true, false, true, true, false, true, true);
            case '4' -> bits(false, true, true, true, false, true, false);
            case '5' -> bits(true, true, false, true, false, true, true);
            case '6' -> bits(true, true, false, true, true, true, true);
            case '7' -> bits(true, false, true, false, false, true, false);
            case '8' -> bits(true, true, true, true, true, true, true);
            case '9' -> bits(true, true, true, true, false, true, true);
            case '-' -> bits(false, false, false, true, false, false, false);
            default -> null;
        };

        if (segments != null) {
            if (segments[0]) RBMKPanelRenderHelper.drawRect(matrix, buffer, x + thick, y, x + width - thick, y + thick, argb);
            if (segments[1]) RBMKPanelRenderHelper.drawRect(matrix, buffer, x, y + thick, x + thick, y + height * 0.5F - thick * 0.5F, argb);
            if (segments[2]) RBMKPanelRenderHelper.drawRect(matrix, buffer, x + width - thick, y + thick, x + width, y + height * 0.5F - thick * 0.5F, argb);
            if (segments[3]) RBMKPanelRenderHelper.drawRect(matrix, buffer, x + thick, y + height * 0.5F - thick * 0.5F, x + width - thick, y + height * 0.5F + thick * 0.5F, argb);
            if (segments[4]) RBMKPanelRenderHelper.drawRect(matrix, buffer, x, y + height * 0.5F + thick * 0.5F, x + thick, y + height - thick, argb);
            if (segments[5]) RBMKPanelRenderHelper.drawRect(matrix, buffer, x + width - thick, y + height * 0.5F + thick * 0.5F, x + width, y + height - thick, argb);
            if (segments[6]) RBMKPanelRenderHelper.drawRect(matrix, buffer, x + thick, y + height - thick, x + width - thick, y + height, argb);
            return;
        }

        if (c == '.') {
            RBMKPanelRenderHelper.drawRect(matrix, buffer, x + width - 2.2F, y + height - 2.2F, x + width, y + height, argb);
            return;
        }

        font.drawInBatch(String.valueOf(c), x + 1.0F, y + 6.0F, argb & 0x00FFFFFF,
                false, matrix, buffer, Font.DisplayMode.NORMAL, 0, packedLight);
    }

    private static boolean[] bits(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f, boolean g) {
        return new boolean[]{a, b, c, d, e, f, g};
    }

    @Override
    public boolean shouldRenderOffScreen(RBMKNumitronEntity blockEntity) {
        return true;
    }
}
