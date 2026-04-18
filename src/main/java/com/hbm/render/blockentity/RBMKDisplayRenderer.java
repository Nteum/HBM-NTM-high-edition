package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.rbmk.RBMKDisplayEntity;
import com.hbm.blockentity.machine.rbmk.RBMKPeripheralEntity;
import com.hbm.reactor.rbmk.RBMKColumnType;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

/**
 * In-world renderer for the RBMK display panel. This mirrors the legacy 1.7.10
 * renderer style: untextured colored cells + small status dots instead of
 * floating font glyphs.
 */
public class RBMKDisplayRenderer implements BlockEntityRenderer<RBMKDisplayEntity> {

    private static final int GRID_SIZE = 7;
    private static final float PANEL_PUSH = 1.0F / 128.0F;
    private static final float CELL_X = 0.28125F + PANEL_PUSH;
    private static final float CELL_Y_BASE = 0.875F;
    private static final float CELL_Z_BASE = 0.375F;
    private static final float CELL_STEP = 0.125F;
    private static final float CELL_HALF = 0.0625F * 0.75F;
    private static final float BG_CELL_HALF = CELL_HALF * 0.88F;
    private static final float DOT_HALF = 0.03125F;
    private static final float DOT_EDGE = 0.022097F;
    private static final int EMPTY_CELL_A = 0xFFCBCBCB;
    private static final int EMPTY_CELL_B = 0xFFBEBEBE;
    private static final int BG_CELL_A = 0xAA6F6F6F;
    private static final int BG_CELL_B = 0xAA5F5F5F;

    public RBMKDisplayRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RBMKDisplayEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        DirectionUtils.generalMachineRotate(poseStack, state.getValue(com.hbm.block.machine.BaseSingleBlockMachine.FACING), 0.0F, 0.0F);
        // Match legacy display transform (1.7.10): translate to display face, then scale 8/7.
        poseStack.translate(0.0D, 0.5D, 0.0D);
        poseStack.scale(1.0F, 8.0F / 7.0F, 8.0F / 7.0F);
        poseStack.translate(0.0D, -0.5D, 0.0D);

        Matrix4f pose = poseStack.last().pose();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                float x = CELL_X - 0.0002F;
                float y = CELL_Y_BASE - row * CELL_STEP;
                float z = CELL_Z_BASE - col * CELL_STEP;
                int bg = ((row + col) & 1) == 0 ? BG_CELL_A : BG_CELL_B;
                drawYZRect(pose, buffer, x, y - BG_CELL_HALF, z - BG_CELL_HALF, y + BG_CELL_HALF, z + BG_CELL_HALF, bg);
            }
        }

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int index = row * GRID_SIZE + col;
                RBMKPeripheralEntity.ConsoleColumn column = blockEntity.getConsoleColumn(index);
                renderCell(buffer, pose, index, column);
            }
        }
        poseStack.popPose();
    }

    private static void renderCell(MultiBufferSource buffer, Matrix4f pose, int index,
                                   RBMKPeripheralEntity.ConsoleColumn column) {
        float x = CELL_X;
        float y = CELL_Y_BASE - (index / GRID_SIZE) * CELL_STEP;
        float z = CELL_Z_BASE - (index % GRID_SIZE) * CELL_STEP;

        if (column == null) {
            int empty = (index & 1) == 0 ? EMPTY_CELL_A : EMPTY_CELL_B;
            drawYZRect(pose, buffer, x, y - CELL_HALF, z - CELL_HALF, y + CELL_HALF, z + CELL_HALF, RBMKPanelRenderHelper.argb(255, empty));
            return;
        }

        int cellColor = baseColor(column, index);
        drawYZRect(pose, buffer, x, y - CELL_HALF, z - CELL_HALF, y + CELL_HALF, z + CELL_HALF,
                RBMKPanelRenderHelper.argb(255, cellColor));

        int indicator = column.data().getByte("indicator");
        if (indicator > 0) {
            drawDot(buffer, pose, x + 0.01F, y, z, 0xFFFF00);
            return;
        }

        switch (column.type()) {
            case FUEL, FUEL_SIM -> {
                float enrichment = (float) clamp(column.data().getDouble("enrichment"), 0.0D, 1.0D);
                int color = rgb(0.0F, 0.25F + enrichment * 0.75F, 0.0F);
                drawDot(buffer, pose, x + 0.01F, y, z, color);
            }
            case CONTROL -> {
                float level = (float) clamp(column.data().getDouble("level"), 0.0D, 1.0D);
                drawDot(buffer, pose, x + 0.01F, y, z, rgb(level, level, 0.0F));
            }
            case CONTROL_AUTO -> {
                float level = (float) clamp(column.data().getDouble("level"), 0.0D, 1.0D);
                drawDot(buffer, pose, x + 0.01F, y, z, rgb(level, 0.0F, level));
            }
            default -> {
            }
        }
    }

    private static void drawDot(MultiBufferSource buffer, Matrix4f pose, float x, float y, float z, int rgb) {
        int argb = RBMKPanelRenderHelper.argb(255, rgb);
        drawYZRect(pose, buffer, x, y - DOT_HALF, z - DOT_EDGE, y + DOT_HALF, z + DOT_EDGE, argb);
        drawYZRect(pose, buffer, x, y - DOT_EDGE, z - DOT_HALF, y + DOT_EDGE, z + DOT_HALF, argb);
        drawYZRect(pose, buffer, x + 0.0002F, y - DOT_EDGE * 0.8F, z - DOT_EDGE * 0.8F,
                y + DOT_EDGE * 0.8F, z + DOT_EDGE * 0.8F, RBMKPanelRenderHelper.argb(230, rgb));
    }

    private static void drawYZRect(Matrix4f matrix, MultiBufferSource buffer, float x, float y0, float z0,
                                   float y1, float z1, int argb) {
        VertexConsumer consumer = buffer.getBuffer(com.hbm.render.ModRenderTypes.RBMK_PANEL);
        float a = ((argb >> 24) & 0xFF) / 255.0F;
        float r = ((argb >> 16) & 0xFF) / 255.0F;
        float g = ((argb >> 8) & 0xFF) / 255.0F;
        float b = (argb & 0xFF) / 255.0F;

        consumer.vertex(matrix, x, y1, z0).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, x, y1, z1).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, x, y0, z1).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, x, y0, z0).color(r, g, b, a).endVertex();
    }

    private static int baseColor(RBMKPeripheralEntity.ConsoleColumn column, int index) {
        if (column.data().contains("color", Tag.TAG_ANY_NUMERIC)) {
            int color = column.data().getInt("color");
            return switch (color) {
                case 0 -> 0xFF0000;
                case 1 -> 0xFFFF00;
                case 2 -> 0x008000;
                case 3 -> 0x0000FF;
                case 4 -> 0x8000FF;
                default -> 0xF0F0F0;
            };
        }
        return switch (column.type()) {
            case FUEL, FUEL_SIM -> 0x55FF55;
            case CONTROL -> 0xCFCF3D;
            case CONTROL_AUTO -> 0xAA50E8;
            case BOILER -> 0x6EC6FF;
            case MODERATOR -> 0xBEBEBE;
            case ABSORBER -> 0x7A4A20;
            case REFLECTOR -> 0xE0E0E0;
            case OUTGASSER -> 0xF39C12;
            case BREEDER -> 0xFF66CC;
            case STORAGE -> 0x909090;
            case COOLER -> 0x66FFFF;
            case HEATEX -> 0xFF9966;
            case BLANK -> (index & 1) == 0 ? 0xB5B5B5 : 0xA8A8A8;
        };
    }

    private static int rgb(float r, float g, float b) {
        int ri = (int) (clamp(r, 0.0D, 1.0D) * 255.0D);
        int gi = (int) (clamp(g, 0.0D, 1.0D) * 255.0D);
        int bi = (int) (clamp(b, 0.0D, 1.0D) * 255.0D);
        return (ri << 16) | (gi << 8) | bi;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public boolean shouldRenderOffScreen(RBMKDisplayEntity blockEntity) {
        return true;
    }
}
