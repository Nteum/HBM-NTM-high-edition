package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.rbmk.RBMKKeypadEntity;
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
 * Simple 2x2 keypad overlay renderer.
 */
public class RBMKKeypadRenderer implements BlockEntityRenderer<RBMKKeypadEntity> {

    private static final float PANEL_SCALE = 1.0F / 128.0F;
    private static final float FACE_Z = 0.5030F;
    private static final int PANEL_FILL = 0xFF101410;
    private static final int PANEL_BORDER = 0xFF314531;

    public RBMKKeypadRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RBMKKeypadEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        Font font = Minecraft.getInstance().font;
        BlockState state = blockEntity.getBlockState();

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        DirectionUtils.generalMachineRotate(poseStack, state.getValue(com.hbm.block.machine.BaseSingleBlockMachine.FACING), 0.0F, 0.0F);
        poseStack.translate(0.0D, 0.0D, FACE_Z);
        poseStack.scale(PANEL_SCALE, -PANEL_SCALE, PANEL_SCALE);

        int emissiveLight = LightTexture.FULL_BRIGHT;
        Matrix4f matrix = poseStack.last().pose();
        Matrix4f textMatrix = new Matrix4f(matrix).translate(0.0F, 0.0F, 0.0020F);
        RBMKPanelRenderHelper.drawFrame(matrix, buffer, -58.0F, -58.0F, 58.0F, 58.0F, PANEL_FILL, PANEL_BORDER);

        renderButton(font, matrix, textMatrix, buffer, blockEntity, 0, -52.0F, -44.0F, emissiveLight);
        renderButton(font, matrix, textMatrix, buffer, blockEntity, 1, 2.0F, -44.0F, emissiveLight);
        renderButton(font, matrix, textMatrix, buffer, blockEntity, 2, -52.0F, -10.0F, emissiveLight);
        renderButton(font, matrix, textMatrix, buffer, blockEntity, 3, 2.0F, -10.0F, emissiveLight);

        String status = blockEntity.isControlColumn()
                ? String.format("CR %03d%%", blockEntity.currentPercent())
                : "NO LINKED CONTROL ROD";
        int statusWidth = font.width(status);
        font.drawInBatch(status, -statusWidth * 0.5F, 34.0F,
                blockEntity.isControlColumn() ? 0xA0FFA0 : 0x707070, false, textMatrix, buffer, Font.DisplayMode.NORMAL, 0, emissiveLight);

        poseStack.popPose();
    }

    private static void renderButton(Font font, Matrix4f matrix, Matrix4f textMatrix, MultiBufferSource buffer,
                                     RBMKKeypadEntity blockEntity,
                                     int slot, float x, float y, int packedLight) {
        if (!blockEntity.isActive(slot)) {
            return;
        }
        boolean linked = blockEntity.isControlColumn();
        boolean pressed = blockEntity.isPressed(slot);
        int rgb = blockEntity.color(slot);
        int fill = pressed ? RBMKPanelRenderHelper.argb(255, 0xDADADA) : RBMKPanelRenderHelper.argb(255, (rgb & 0x00FFFFFF));
        int border = pressed ? 0xFFFFFFFF : RBMKPanelRenderHelper.argb(255, darken(rgb, linked ? 0.55F : 0.75F));

        float yOffset = pressed ? 1.2F : 0.0F;
        float x0 = x;
        float y0 = y + yOffset;
        float x1 = x + 50.0F;
        float y1 = y0 + 24.0F;
        RBMKPanelRenderHelper.drawFrame(matrix, buffer, x0, y0, x1, y1, fill, border);

        String label = blockEntity.label(slot);
        if (label == null || label.isBlank()) {
            label = Integer.toString(blockEntity.preset(slot));
        }
        int textColor = pressed ? 0x303030 : (linked ? 0x101010 : 0x909090);
        int labelWidth = font.width(label);
        font.drawInBatch(label, x + 25.0F - labelWidth * 0.5F, y0 + 8.0F, textColor, false, textMatrix, buffer, Font.DisplayMode.NORMAL, 0, packedLight);
    }

    private static int darken(int color, float factor) {
        int r = Math.max(0, Math.min(255, Math.round(((color >> 16) & 0xFF) * factor)));
        int g = Math.max(0, Math.min(255, Math.round(((color >> 8) & 0xFF) * factor)));
        int b = Math.max(0, Math.min(255, Math.round((color & 0xFF) * factor)));
        return (r << 16) | (g << 8) | b;
    }

    @Override
    public boolean shouldRenderOffScreen(RBMKKeypadEntity blockEntity) {
        return true;
    }
}
