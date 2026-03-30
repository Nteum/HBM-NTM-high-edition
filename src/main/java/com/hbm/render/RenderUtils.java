package com.hbm.render;

import com.hbm.render.model.AccessableRenderable;
import com.hbm.utils.EnumUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class RenderUtils {
    //参考blockRenderDispatcher的renderSingleBlock对单个模型进行渲染
    public static void renderBlockModel(BakedModel model, BlockState state, ModelBlockRenderer modelRenderer,
                                        PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, RenderType renderType){
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        int i = blockColors.getColor(state, (BlockAndTintGetter)null, (BlockPos)null, 0);
        float f = (float)(i >> 16 & 255) / 255.0F;
        float f1 = (float)(i >> 8 & 255) / 255.0F;
        float f2 = (float)(i & 255) / 255.0F;
        for (net.minecraft.client.renderer.RenderType rt : model.getRenderTypes(state, RandomSource.create(42), (ModelData) null))
            modelRenderer.renderModel(pPoseStack.last(), pBuffer.getBuffer(renderType != null ? renderType : net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(rt, false)),
                    state, model, f,f1,f2, pPackedLight, pPackedOverlay, ModelData.EMPTY, rt);
    }
    /**
     * 参考原版 ModelBlockRender#renderModel
     * */
    public static void renderModel(BakedModel model, PoseStack pPose, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, RenderType renderType){
        VertexConsumer buffer;
        if (renderType == null || (buffer = pBuffer.getBuffer(renderType)) == null) return;
        float pRed = 1.0F;
        float pGreen = 1.0f;
        float pBlue = 1.0f;
        BlockState dummyState = Blocks.AIR.defaultBlockState();
        RandomSource randomSource = RandomSource.create(42L);
        for(Direction direction : EnumUtils.DIRECTIONS) {
            renderQuadList(pPose.last(), buffer, pRed, pGreen, pBlue, model.getQuads(dummyState, direction, randomSource), pPackedLight, pPackedOverlay);
        }

        renderQuadList(pPose.last(), buffer, pRed, pGreen, pBlue, model.getQuads(dummyState, null, randomSource), pPackedLight, pPackedOverlay);
    }
    private static void renderQuadList(PoseStack.Pose pPose, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, List<BakedQuad> pQuads, int pPackedLight, int pPackedOverlay) {
        for(BakedQuad bakedquad : pQuads) {
            float f;
            float f1;
            float f2;
            if (bakedquad.isTinted()) {
                f = Mth.clamp(pRed, 0.0F, 1.0F);
                f1 = Mth.clamp(pGreen, 0.0F, 1.0F);
                f2 = Mth.clamp(pBlue, 0.0F, 1.0F);
            } else {
                f = 1.0F;
                f1 = 1.0F;
                f2 = 1.0F;
            }

            pConsumer.putBulkData(pPose, bakedquad, f, f1, f2, pPackedLight, pPackedOverlay);
        }
    }
    public static void renderLeash(Vec3 start, Vec3 end, PoseStack pPose, MultiBufferSource pBuffer,float pPartialTick){
        pPose.pushPose();
        double d0 = (Mth.lerp(pPartialTick, start.y, end.y) * ((float)Math.PI / 180F)) + (Math.PI / 2D);
        double d1 = Math.cos(d0) * end.z + Math.sin(d0) * end.x;
        double d2 = Math.sin(d0) * end.z - Math.cos(d0) * end.x;
        pPose.translate(d1, end.y, d2);
        float distX = (float)(start.x - end.x);
        float distY = (float)(start.y - end.y);
        float distZ = (float)(start.z - end.z);
        float f3 = 0.025F;
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.leash());
        Matrix4f matrix4f = pPose.last().pose();
        float f4 = Mth.invSqrt(distX * distX + distZ * distZ) * f3 / 2.0F;
        float f5 = distZ * f4;
        float f6 = distX * f4;
        ClientLevel level = Minecraft.getInstance().level;
        int i = level == null ? 0 : level.getBrightness(LightLayer.BLOCK, new BlockPos((int) start.x, (int) start.y, (int) start.z));
        int j = level == null ? 0 : level.getBrightness(LightLayer.BLOCK, new BlockPos((int) end.x, (int) end.y, (int) end.z));
        int k = level == null ? 0 : level.getBrightness(LightLayer.SKY, new BlockPos((int) start.x, (int) start.y, (int) start.z));
        int l = level == null ? 0 : level.getBrightness(LightLayer.SKY, new BlockPos((int) end.x, (int) end.y, (int) end.z));

        for(int i1 = 0; i1 <= 24; ++i1) {
            addVertexPair(vertexconsumer, matrix4f, distX, distY, distZ, i, j, k, l, 0.025F, 0.025F, f5, f6, i1, false);
        }

        for(int j1 = 24; j1 >= 0; --j1) {
            addVertexPair(vertexconsumer, matrix4f, distX, distY, distZ, i, j, k, l, 0.025F, 0.0F, f5, f6, j1, true);
        }

        pPose.popPose();
    }
    private static void addVertexPair(VertexConsumer consumer, Matrix4f matrix, float deltaX, float deltaY, float deltaZ, int pEntityBlockLightLevel, int pLeashHolderBlockLightLevel,
                                      int pEntitySkyLightLevel, int pLeashHolderSkyLightLevel, float p_174317_, float p_174318_, float p_174319_, float p_174320_, int pIndex, boolean flagSide) {
        float pointOrder = (float)pIndex / 24.0F;
        int i = (int)Mth.lerp(pointOrder, (float)pEntityBlockLightLevel, (float)pLeashHolderBlockLightLevel);
        int j = (int)Mth.lerp(pointOrder, (float)pEntitySkyLightLevel, (float)pLeashHolderSkyLightLevel);
        int k = LightTexture.pack(i, j);
        float f1 = pIndex % 2 == (flagSide ? 1 : 0) ? 0.7F : 1.0F;
        float f2 = 0.5F * f1;
        float f3 = 0.4F * f1;
        float f4 = 0.3F * f1;
        float f5 = deltaX * pointOrder;
        float f6 = deltaY > 0.0F ? deltaY * pointOrder * pointOrder : deltaY - deltaY * (1.0F - pointOrder) * (1.0F - pointOrder);
        float f7 = deltaZ * pointOrder;
        consumer.vertex(matrix, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
        consumer.vertex(matrix, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_).color(f2, f3, f4, 1.0F).uv2(k).endVertex();
    }
    public static void renderLine(Vec3 start, Vec3 end, PoseStack pPose, MultiBufferSource pBuffer,float pPartialTick){
        pPose.pushPose();
        VertexConsumer buffer = pBuffer.getBuffer(RenderType.lineStrip());
        Matrix4f matrix4f = pPose.last().pose();
        Matrix3f normal = pPose.last().normal();
        end = end.subtract(start);
        start = new Vec3(start.x - Math.floor(start.x), start.y - Math.floor(start.y), start.z - Math.floor(start.z));
        pPose.translate(start.x, start.y, start.z);
        buffer.vertex(matrix4f, 0, 0, 0).color(255,0,0,255).normal(0, 1, 0).endVertex();
        buffer.vertex(matrix4f, (float) end.x, (float) end.y, (float) end.z).color(255,0,0,255).normal(0, 1, 0).endVertex();
        pPose.popPose();
    }
    /**
     * 独立建立局部multisource进行渲染，适用于只有VertexConsumer传进来，但需要单独的texture或rendertype渲染的情况
     * */
    public static void renderStandalone(PoseStack poseStack, RenderType renderType, AccessableRenderable.Component component, int lightmap, int overlay){
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        try {
            VertexConsumer buffer = bufferSource.getBuffer(renderType);
            component.render(poseStack, buffer, lightmap, overlay);
        } finally {
            bufferSource.endBatch();
        }
    }
    // 渲染一个柱状物，主要是用于渲染流动的液体
    public static void renderRectPillar(PoseStack poseStack, VertexConsumer consumer, int color, Vec3 start, Vec3 end, float width, int light, int overlay){
        Vec3 dir = end.subtract(start);
        if (dir.length() == 0) return;
        float fullLength = (float) dir.length();
        Vec3 center = new Vec3((start.x + end.x) / 2, (start.y + end.y) / 2, (start.z + end.z) / 2);
        dir = dir.normalize();

        poseStack.pushPose();
        // 1. 平移到起点
        poseStack.translate(center.x, center.y, center.z);
        // 2. 旋转使局部 Y 轴指向 dir
        Quaternionf rotation = getRotationBetween(new Vector3f(0, 1, 0), new Vector3f((float) dir.x, (float) dir.y, (float) dir.z));
        poseStack.mulPose(rotation);
        // 3. 缩放：X/Z 方向为 width，Y 方向为 fullLength
        poseStack.scale(width, fullLength, width);

        // 四个侧面：每个面都是矩形，尺寸为 1x1（在局部坐标中覆盖整个面）
        float rectWidth = 1.0f;   // 沿 n1 方向（例如 Z 轴）
        float rectLength = 1.0f;  // 沿 n2 方向（例如 Y 轴）
        float offset = 0.5f;      // 矩形中心到轴线的距离（半宽）
        // 定义局部坐标系中的基向量（单位）
        Vec3 localX = new Vec3(1, 0, 0);
        Vec3 localY = new Vec3(0, 1, 0);
        Vec3 localZ = new Vec3(0, 0, 1);

        addRectQuad(poseStack, consumer, color, 1, 1, offset, localX, localZ, localY, light, overlay);
        addRectQuad(poseStack, consumer, color, 1, 1, offset, localX.scale(-1), localZ, localY, light, overlay);
        addRectQuad(poseStack, consumer, color, 1, 1, offset, localZ, localX, localY, light, overlay);
        addRectQuad(poseStack, consumer, color, 1, 1, offset, localZ.scale(-1), localX, localY, light, overlay);

        poseStack.popPose();
    }
    // 渲染一个矩形quad
    public static void addRectQuad(PoseStack poseStack, VertexConsumer consumer, int color, float width, float length, float height, Vec3 n, Vec3 n1, Vec3 n2, int light, int overlay){
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        float red = FastColor.ARGB32.red(color) / 255.0f;
        float green = FastColor.ARGB32.green(color) / 255.0f;
        float blue = FastColor.ARGB32.blue(color) / 255.0f;
        float alpha = FastColor.ARGB32.alpha(color) / 255.0f;
        if (alpha == 0f) alpha = 1.0f;

        // 半长宽
        float halfW = width * 0.5f;
        float halfL = length * 0.5f;

        // 矩形中心
        float cx = (float) (n.x * height);
        float cy = (float) (n.y * height);
        float cz = (float) (n.z * height);

        float[][] offsets = {{-halfW, -halfL}, {-halfW,  halfL}, { halfW,  halfL}, { halfW, -halfL}};

        float deltaV = length * (float) Minecraft.getInstance().level.getGameTime() % 32 / 32;

        for (int i = 0; i < 4; i++) {
            float offU = offsets[i][0];
            float offV = offsets[i][1];
            float px = (float) (cx + offU * n1.x + offV * n2.x);
            float py = (float) (cy + offU * n1.y + offV * n2.y);
            float pz = (float) (cz + offU * n1.z + offV * n2.z);
            // UV 坐标（简单映射，U 沿 width 方向从 0 到 1，V 沿 length 方向从 0 到 1）
            float u = (offU + halfW) / width;   // 0~1
            float v = (offV + halfL) / width - deltaV;  // 0~1
//            v = v - Mth.floor(v);
            consumer.vertex(pose, px, py, pz).color(red, green, blue, alpha).uv(u, v).overlayCoords(overlay).uv2(light).normal(normal, (float) n.x, (float) n.y, (float) n.z).endVertex();
        }
    }
//    // 渲染一个单独的面
//    public static void renderSingleSide(PoseStack poseStack, VertexConsumer consumer, int color,
//                                        float x1, float y1, float z1,
//                                        float x2, float y2, float z2,
//                                        float x3, float y3, float z3,
//                                        float x4, float y4, float z4,
//                                        float nx, float ny, float nz,
//                                        int light, int overlay){
//        Matrix4f pose = poseStack.last().pose();
//        Matrix3f normal = poseStack.last().normal();
//        float red = FastColor.ARGB32.red(color) / 255.0f;
//        float green = FastColor.ARGB32.green(color) / 255.0f;
//        float blue = FastColor.ARGB32.blue(color) / 255.0f;
//        float alpha = FastColor.ARGB32.alpha(color) / 255.0f;
//        if (alpha == 0f) alpha = 1.0f;
//
//        consumer.vertex(pose, x1, y1, z1).color(red, green, blue, alpha).uv(0, 1).overlayCoords(overlay).uv2(light).normal(normal, nx, ny, nz).endVertex();
//        consumer.vertex(pose, x2, y2, z2).color(red, green, blue, alpha).uv(1, 1).overlayCoords(overlay).uv2(light).normal(normal, nx, ny, nz).endVertex();
//        consumer.vertex(pose, x3, y3, z3).color(red, green, blue, alpha).uv(1, 0).overlayCoords(overlay).uv2(light).normal(normal, nx, ny, nz).endVertex();
//        consumer.vertex(pose, x4, y4, z4).color(red, green, blue, alpha).uv(0, 0).overlayCoords(overlay).uv2(light).normal(normal, nx, ny, nz).endVertex();
//    }

    // 计算旋转四元数
    private static Quaternionf getRotationBetween(Vector3f from, Vector3f to) {
        from = new Vector3f(from); from.normalize();
        to = new Vector3f(to); to.normalize();
        float dot = from.dot(to);
        if (dot > 0.99999f) return new Quaternionf();
        if (dot < -0.99999f) {
            // 相反方向，旋转180度，绕X轴
            return new Quaternionf().setAngleAxis((float) Math.PI, 1, 0, 0);
        }
        Vector3f axis = from.cross(to);
        axis.normalize();
        float angle = (float) Math.acos(dot);
        return new Quaternionf().setAngleAxis(angle, axis.x(), axis.y(), axis.z());
    }
}
