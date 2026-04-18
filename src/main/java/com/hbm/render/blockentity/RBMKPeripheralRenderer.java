package com.hbm.render.blockentity;

import com.hbm.block.base.BlockContainerBase;
import com.hbm.blockentity.machine.rbmk.RBMKPeripheralEntity;
import com.hbm.reactor.rbmk.RBMKColumnType;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import com.hbm.render.ModRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.jetbrains.annotations.Nullable;

/**
 * RBMK console in-world overlay (map + six status screens), tuned to the
 * current Forge model orientation and with dedicated H2O/CR trend screens.
 */
public class RBMKPeripheralRenderer implements BlockEntityRenderer<RBMKPeripheralEntity> {

    private static final double PANEL_PUSH = 1.0D / 128.0D;
    private static final double GRID_X = -0.3725D + PANEL_PUSH;
    private static final double GRID_Y_BASE = 3.625D;
    private static final double GRID_Z_BASE = 0.875D;
    private static final double GRID_STEP = 0.125D;
    private static final int GRID_BG_SIZE = 16;
    private static final double GRID_BG_Y_BASE = GRID_Y_BASE + GRID_STEP * 0.5D;
    private static final double GRID_BG_Z_BASE = GRID_Z_BASE + GRID_STEP * 0.5D;
    private static final double CELL_HALF = 0.0625D * 0.82D;
    private static final double BG_CELL_HALF = CELL_HALF * 0.86D;
    private static final double DOT_HALF = 0.03125D;
    private static final double DOT_EDGE = 0.022097D;
    private static final int GRID_BG_LIGHT = 0xAA737373;
    private static final int GRID_BG_DARK = 0xAA5F5F5F;
    private static final int GRID_EMPTY_A = 0xFFC5C5C5;
    private static final int GRID_EMPTY_B = 0xFFB1B1B1;

    private static final double SCREEN_X = -0.42D + PANEL_PUSH;
    private static final double SCREEN_Y_BASE = 3.5D;
    private static final double SCREEN_Y_STEP = 0.75D;
    private static final double SCREEN_Z_LEFT = 1.75D;
    private static final double SCREEN_Z_RIGHT = -1.75D;
    private static final double SCREEN_HALF_HEIGHT = 0.12D;
    private static final double SCREEN_HALF_WIDTH = 0.40D;
    private static final double SCREEN_BORDER = 0.01D;
    private static final int SCREEN_BG_FILL = 0xD0000000;
    private static final int SCREEN_BG_BORDER = 0xE03C3C3C;
    private static final int SCREEN_GUIDE = 0x783F7C3F;
    private static final int SCREEN_TEXT = 0x00FF00;
    private static final int SCREEN_H2O = 0x2BC8FF;
    private static final int SCREEN_CR = 0x9AFF3D;
    private static final double TREND_POINT_SIZE = 0.0045D;
    private static final int TREND_SAMPLES = 40;

    public RBMKPeripheralRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RBMKPeripheralEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        if (blockEntity.getPeripheralType() != RBMKPeripheralType.CONSOLE) {
            return;
        }

        BlockState state = blockEntity.getBlockState();
        if (!state.hasProperty(BlockContainerBase.FACING)) {
            return;
        }

        Font font = Minecraft.getInstance().font;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        rotateLikeConsoleBlockstate(poseStack, state.getValue(BlockContainerBase.FACING));
        poseStack.translate(0.5D, 0.0D, 0.0D);

        renderGrid(blockEntity, poseStack, buffer);
        renderScreens(blockEntity, font, poseStack, buffer, LightTexture.FULL_BRIGHT);

        poseStack.popPose();
    }

    /**
     * Keep the in-world console overlay aligned with the console block model's
     * blockstate Y-rotation mapping.
     */
    private static void rotateLikeConsoleBlockstate(PoseStack poseStack, Direction facing) {
        switch (facing) {
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
            default -> {
            }
        }
    }

    private static void renderGrid(RBMKPeripheralEntity blockEntity, PoseStack poseStack, MultiBufferSource buffer) {
        Matrix4f matrix = poseStack.last().pose();
        for (int row = 0; row < GRID_BG_SIZE; row++) {
            for (int col = 0; col < GRID_BG_SIZE; col++) {
                double x = GRID_X;
                double y = -(row * GRID_STEP) + GRID_BG_Y_BASE;
                double z = -(col * GRID_STEP) + GRID_BG_Z_BASE;
                int color = ((row + col) & 1) == 0 ? GRID_BG_LIGHT : GRID_BG_DARK;
                drawRectCell(matrix, buffer, x - 0.0002D, y, z, BG_CELL_HALF, color);
            }
        }

        for (int index = 0; index < 15 * 15; index++) {
            RBMKPeripheralEntity.ConsoleColumn column = blockEntity.getConsoleColumn(index);
            int row = index / 15;
            int col = index % 15;

            double x = GRID_X;
            double y = -(row * GRID_STEP) + GRID_Y_BASE;
            double z = -(col * GRID_STEP) + GRID_Z_BASE;

            if (column == null) {
                int emptyColor = (index & 1) == 0 ? GRID_EMPTY_A : GRID_EMPTY_B;
                drawRectCell(matrix, buffer, x, y, z, CELL_HALF, RBMKPanelRenderHelper.argb(255, emptyColor));
                continue;
            }

            int fill = cellColor(column, index);
            drawRectCell(matrix, buffer, x, y, z, CELL_HALF, RBMKPanelRenderHelper.argb(255, fill));

            int dotColor = dotColor(column);
            if (dotColor >= 0) {
                drawDot(matrix, buffer, x + 0.01D, y, z, RBMKPanelRenderHelper.argb(255, dotColor));
            }
        }
    }

    private static void renderScreens(RBMKPeripheralEntity blockEntity, Font font, PoseStack poseStack, MultiBufferSource buffer,
                                      int packedLight) {
        Matrix4f matrix = poseStack.last().pose();
        for (int i = 0; i < 6; i++) {
            RBMKPeripheralEntity.ConsoleScreen screen = blockEntity.getScreen(i);

            double y = SCREEN_Y_BASE - (i / 2) * SCREEN_Y_STEP;
            double z = (i % 2 == 0) ? SCREEN_Z_LEFT : SCREEN_Z_RIGHT;
            drawScreenFrame(matrix, buffer, y, z);

            if (i == 0) {
                drawTrendScreen(font, poseStack, buffer, blockEntity.getWaterBuffer(), y, z,
                        "H2O", "", SCREEN_H2O, packedLight, 1, 0);
                continue;
            }
            if (i == 1) {
                drawTrendScreen(font, poseStack, buffer, blockEntity.getControlBuffer(), y, z,
                        "CR", "%", SCREEN_CR, packedLight, 0, 100);
                continue;
            }

            String display = screen.display();
            if (display == null || display.isBlank()) {
                display = defaultReadout(blockEntity, i);
            }
            if (display == null || display.isBlank()) {
                continue;
            }
            drawScreenText(font, poseStack, buffer, display, y, z, packedLight, SCREEN_TEXT, 0.78F);
        }
    }

    private static void drawScreenText(Font font, PoseStack poseStack, MultiBufferSource buffer, String text, double y, double z,
                                       int packedLight, int color, float maxWidth) {
        int width = Math.max(1, font.width(text));
        float scale = Math.min(0.03F, maxWidth / width);

        poseStack.pushPose();
        poseStack.translate(SCREEN_X + 0.004D, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        poseStack.scale(scale, -scale, scale);
        font.drawInBatch(text, -width * 0.5F, -font.lineHeight * 0.5F, color, false,
                poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight);
        poseStack.popPose();
    }

    private static void drawScreenFrame(Matrix4f matrix, MultiBufferSource buffer, double centerY, double centerZ) {
        double y0 = centerY - SCREEN_HALF_HEIGHT;
        double y1 = centerY + SCREEN_HALF_HEIGHT;
        double z0 = centerZ - SCREEN_HALF_WIDTH;
        double z1 = centerZ + SCREEN_HALF_WIDTH;

        drawYZRect(matrix, buffer, SCREEN_X, y0, z0, y1, z1, SCREEN_BG_FILL);
        drawYZRect(matrix, buffer, SCREEN_X + 0.0004D, y1 - SCREEN_BORDER, z0, y1, z1, SCREEN_BG_BORDER);
        drawYZRect(matrix, buffer, SCREEN_X + 0.0004D, y0, z0, y0 + SCREEN_BORDER, z1, SCREEN_BG_BORDER);
        drawYZRect(matrix, buffer, SCREEN_X + 0.0004D, y0, z0, y1, z0 + SCREEN_BORDER, SCREEN_BG_BORDER);
        drawYZRect(matrix, buffer, SCREEN_X + 0.0004D, y0, z1 - SCREEN_BORDER, y1, z1, SCREEN_BG_BORDER);

        for (double ratio : new double[]{0.25D, 0.50D, 0.75D}) {
            double yGuide = y0 + (y1 - y0) * ratio;
            drawYZRect(matrix, buffer, SCREEN_X + 0.0008D, yGuide - 0.0011D, z0 + 0.010D, yGuide + 0.0011D, z1 - 0.010D, SCREEN_GUIDE);
        }
    }

    private static void drawRectCell(Matrix4f matrix, MultiBufferSource buffer, double x, double y, double z, double half, int argb) {
        drawYZRect(matrix, buffer, x, y - half, z - half, y + half, z + half, argb);
    }

    private static void drawDot(Matrix4f matrix, MultiBufferSource buffer, double x, double y, double z, int argb) {
        drawYZRect(matrix, buffer, x + 0.0002D, y - DOT_HALF, z - DOT_EDGE, y + DOT_HALF, z + DOT_EDGE, argb);
        drawYZRect(matrix, buffer, x + 0.0002D, y - DOT_EDGE, z - DOT_HALF, y + DOT_EDGE, z + DOT_HALF, argb);
    }

    private static void drawTrendScreen(Font font, PoseStack poseStack, MultiBufferSource buffer, int[] values,
                                        double centerY, double centerZ, String title, String unit, int color, int packedLight,
                                        int fixedMin, int fixedMax) {
        int[] samples = downsample(values, TREND_SAMPLES);
        if (samples.length <= 0) {
            return;
        }

        int min = fixedMin <= fixedMax ? fixedMin : Integer.MAX_VALUE;
        int max = fixedMin <= fixedMax ? fixedMax : Integer.MIN_VALUE;
        if (fixedMin > fixedMax) {
            for (int sample : samples) {
                min = Math.min(min, sample);
                max = Math.max(max, sample);
            }
        }
        if (min == Integer.MAX_VALUE || max == Integer.MIN_VALUE) {
            min = 0;
            max = 1;
        }
        if (max <= min) {
            max = min + 1;
        }

        double zStart = centerZ - SCREEN_HALF_WIDTH + SCREEN_BORDER * 2.0D;
        double zEnd = centerZ + SCREEN_HALF_WIDTH - SCREEN_BORDER * 2.0D;
        double yLow = centerY - SCREEN_HALF_HEIGHT + SCREEN_BORDER * 2.0D;
        double yHigh = centerY + SCREEN_HALF_HEIGHT - SCREEN_BORDER * 2.0D;

        Matrix4f matrix = poseStack.last().pose();
        double prevY = yLow;
        double prevZ = zStart;
        for (int i = 0; i < samples.length; i++) {
            double t = (samples[i] - min) / (double) (max - min);
            t = clamp(t, 0.0D, 1.0D);
            double y = yLow + (yHigh - yLow) * t;
            double z = zStart + (zEnd - zStart) * (i / (double) Math.max(1, samples.length - 1));
            drawRectCell(matrix, buffer, SCREEN_X + 0.0011D, y, z, TREND_POINT_SIZE, RBMKPanelRenderHelper.argb(255, color));
            if (i > 0) {
                drawTrendSegment(matrix, buffer, prevY, prevZ, y, z, 0.0018D, RBMKPanelRenderHelper.argb(220, color));
            }
            prevY = y;
            prevZ = z;
        }

        int latest = samples[samples.length - 1];
        drawScreenText(font, poseStack, buffer, title, centerY - 0.062D, centerZ - 0.31D, packedLight, color, 0.42F);
        drawScreenText(font, poseStack, buffer, latest + unit, centerY + 0.062D, centerZ + 0.30D, packedLight, color, 0.60F);
    }

    private static void drawTrendSegment(Matrix4f matrix, MultiBufferSource buffer, double y0, double z0, double y1, double z1,
                                         double halfWidth, int argb) {
        double dy = y1 - y0;
        double dz = z1 - z0;
        double length = Math.sqrt(dy * dy + dz * dz);
        if (length < 0.0001D) {
            return;
        }
        double py = (dz / length) * halfWidth;
        double pz = (-dy / length) * halfWidth;

        VertexConsumer consumer = buffer.getBuffer(ModRenderTypes.RBMK_PANEL);
        float a = ((argb >> 24) & 0xFF) / 255.0F;
        float r = ((argb >> 16) & 0xFF) / 255.0F;
        float g = ((argb >> 8) & 0xFF) / 255.0F;
        float b = (argb & 0xFF) / 255.0F;
        float x = (float) (SCREEN_X + 0.0010D);

        consumer.vertex(matrix, x, (float) (y0 + py), (float) (z0 + pz)).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, x, (float) (y0 - py), (float) (z0 - pz)).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, x, (float) (y1 - py), (float) (z1 - pz)).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, x, (float) (y1 + py), (float) (z1 + pz)).color(r, g, b, a).endVertex();
    }

    private static int[] downsample(int[] values, int points) {
        if (values == null || values.length == 0 || points <= 0) {
            return new int[0];
        }
        int[] sampled = new int[points];
        for (int i = 0; i < points; i++) {
            int idx = (int) Math.round(i * (values.length - 1) / (double) Math.max(1, points - 1));
            sampled[i] = values[Mth.clamp(idx, 0, values.length - 1)];
        }
        return sampled;
    }

    private static void drawYZRect(Matrix4f matrix, MultiBufferSource buffer, double x, double y0, double z0, double y1, double z1, int argb) {
        VertexConsumer consumer = buffer.getBuffer(ModRenderTypes.RBMK_PANEL);
        float a = ((argb >> 24) & 0xFF) / 255.0F;
        float r = ((argb >> 16) & 0xFF) / 255.0F;
        float g = ((argb >> 8) & 0xFF) / 255.0F;
        float b = (argb & 0xFF) / 255.0F;

        float xf = (float) x;
        float y0f = (float) y0;
        float y1f = (float) y1;
        float z0f = (float) z0;
        float z1f = (float) z1;

        consumer.vertex(matrix, xf, y1f, z0f).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, xf, y1f, z1f).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, xf, y0f, z1f).color(r, g, b, a).endVertex();
        consumer.vertex(matrix, xf, y0f, z0f).color(r, g, b, a).endVertex();
    }

    @Nullable
    private static String defaultReadout(RBMKPeripheralEntity blockEntity, int slot) {
        return switch (slot) {
            case 2 -> String.format("STM %04d", blockEntity.getTelemetrySteam());
            case 3 -> String.format("HT %.1f", blockEntity.getTelemetryHeat() / 10.0F);
            case 4 -> String.format("FX %04d", blockEntity.getTelemetryFlux());
            case 5 -> String.format("RD %03d", blockEntity.getTelemetryFuelRods());
            default -> null;
        };
    }

    private static int dotColor(RBMKPeripheralEntity.ConsoleColumn column) {
        if (column.data().getByte("indicator") > 0) {
            return 0xFFFF00;
        }
        return switch (column.type()) {
            case FUEL, FUEL_SIM -> {
                float enrichment = (float) clamp(column.data().getDouble("enrichment"), 0.0D, 1.0D);
                yield rgb(0.0F, 0.25F + enrichment * 0.75F, 0.0F);
            }
            case CONTROL -> {
                float level = (float) clamp(column.data().getDouble("level"), 0.0D, 1.0D);
                yield rgb(level, level, 0.0F);
            }
            case CONTROL_AUTO -> {
                float level = (float) clamp(column.data().getDouble("level"), 0.0D, 1.0D);
                yield rgb(level, 0.0F, level);
            }
            default -> -1;
        };
    }

    private static int cellColor(RBMKPeripheralEntity.ConsoleColumn column, int index) {
        if (column.data().contains("color", Tag.TAG_ANY_NUMERIC)) {
            return switch (column.data().getInt("color")) {
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
    public boolean shouldRenderOffScreen(RBMKPeripheralEntity blockEntity) {
        return blockEntity.getPeripheralType() == RBMKPeripheralType.CONSOLE;
    }
}
