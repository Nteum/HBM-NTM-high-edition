package com.hbm.render.blockentity;

import com.hbm.block.base.BlockContainerBase;
import com.hbm.blockentity.machine.rbmk.RBMKPeripheralEntity;
import com.hbm.reactor.rbmk.RBMKColumnType;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import com.hbm.render.ModRenderTypes;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
 * Legacy-like RBMK console in-world overlay (15x15 map + six status screens).
 * Coordinates are aligned to the old 1.7.10 renderer and pushed outward by
 * 1/16 block to avoid z-fighting with the console model surface.
 */
public class RBMKPeripheralRenderer implements BlockEntityRenderer<RBMKPeripheralEntity> {

    private static final double PANEL_PUSH = 1.0D / 16.0D;
    private static final double GRID_X = -0.3725D + PANEL_PUSH;
    private static final double GRID_Y_BASE = 3.625D;
    private static final double GRID_Z_BASE = 0.875D;
    private static final double GRID_STEP = 0.125D;
    private static final double CELL_HALF = 0.0625D * 0.75D;
    private static final double DOT_HALF = 0.03125D;
    private static final double DOT_EDGE = 0.022097D;

    private static final double SCREEN_X = -0.42D + PANEL_PUSH;
    private static final double SCREEN_Y_BASE = 3.5D;
    private static final double SCREEN_Y_STEP = 0.75D;
    private static final double SCREEN_Z_LEFT = 1.75D;
    private static final double SCREEN_Z_RIGHT = -1.75D;
    private static final double SCREEN_HALF_HEIGHT = 0.12D;
    private static final double SCREEN_HALF_WIDTH = 0.40D;
    private static final double SCREEN_BORDER = 0.01D;
    private static final int SCREEN_BG_FILL = 0xA0101010;
    private static final int SCREEN_BG_BORDER = 0xD0353535;

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
        DirectionUtils.generalMachineRotate(poseStack, state.getValue(BlockContainerBase.FACING), 0.0F, 0.0F);
        poseStack.translate(0.5D, 0.0D, 0.0D);

        renderGrid(blockEntity, poseStack, buffer);
        renderScreens(blockEntity, font, poseStack, buffer, LightTexture.FULL_BRIGHT);

        poseStack.popPose();
    }

    private static void renderGrid(RBMKPeripheralEntity blockEntity, PoseStack poseStack, MultiBufferSource buffer) {
        Matrix4f matrix = poseStack.last().pose();
        for (int index = 0; index < 15 * 15; index++) {
            RBMKPeripheralEntity.ConsoleColumn column = blockEntity.getConsoleColumn(index);
            if (column == null) {
                continue;
            }
            int row = index / 15;
            int col = index % 15;

            double x = GRID_X;
            double y = -(row * GRID_STEP) + GRID_Y_BASE;
            double z = -(col * GRID_STEP) + GRID_Z_BASE;

            int fill = cellColor(column, index);
            drawColumn(matrix, buffer, x, y, z, RBMKPanelRenderHelper.argb(255, fill));

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

            String display = screen.display();
            if (display == null || display.isBlank()) {
                display = defaultReadout(blockEntity, i);
            }
            if (display == null || display.isBlank()) {
                continue;
            }
            drawScreenText(font, poseStack, buffer, display, y, z, packedLight);
        }
    }

    private static void drawScreenText(Font font, PoseStack poseStack, MultiBufferSource buffer, String text,
                                       double y, double z, int packedLight) {
        int width = Math.max(1, font.width(text));
        float scale = Math.min(0.03F, 0.8F / width);

        poseStack.pushPose();
        poseStack.translate(SCREEN_X + 0.003D, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        poseStack.scale(scale, -scale, scale);
        font.drawInBatch(text, -width * 0.5F, -font.lineHeight * 0.5F, 0x00FF00, false,
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
    }

    private static void drawColumn(Matrix4f matrix, MultiBufferSource buffer, double x, double y, double z, int argb) {
        drawYZRect(matrix, buffer, x, y - CELL_HALF, z - CELL_HALF, y + CELL_HALF, z + CELL_HALF, argb);
    }

    private static void drawDot(Matrix4f matrix, MultiBufferSource buffer, double x, double y, double z, int argb) {
        drawYZRect(matrix, buffer, x + 0.0002D, y - DOT_HALF, z - DOT_EDGE, y + DOT_HALF, z + DOT_EDGE, argb);
        drawYZRect(matrix, buffer, x + 0.0002D, y - DOT_EDGE, z - DOT_HALF, y + DOT_EDGE, z + DOT_HALF, argb);
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
            case 0 -> String.format("STM %04d", blockEntity.getTelemetrySteam());
            case 1 -> String.format("H2O %04d", blockEntity.getTelemetryWater());
            case 2 -> String.format("HT %.1f", blockEntity.getTelemetryHeat() / 10.0F);
            case 3 -> String.format("FX %04d", blockEntity.getTelemetryFlux());
            case 4 -> String.format("CR %03d%%", blockEntity.getTelemetryControl());
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

        if (column.type() == RBMKColumnType.BLANK) {
            return (index & 1) == 0 ? 0xB5B5B5 : 0xA8A8A8;
        }
        double base = 0.65D + (index % 2) * 0.05D;
        double maxHeat = column.data().getDouble("maxHeat");
        double heatRatio = maxHeat <= 0.0D ? 0.0D : Mth.clamp(column.data().getDouble("heat") / maxHeat, 0.0D, 1.0D);
        int red = (int) Mth.clamp((base + (1.0D - base) * heatRatio) * 255.0D, 0.0D, 255.0D);
        int gb = (int) Mth.clamp(base * 255.0D, 0.0D, 255.0D);
        return (red << 16) | (gb << 8) | gb;
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
