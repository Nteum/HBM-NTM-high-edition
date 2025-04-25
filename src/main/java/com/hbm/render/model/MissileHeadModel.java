package com.hbm.render.model;

import com.hbm.HBM;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

import java.util.function.Function;

public class MissileHeadModel extends Model {
    final BakedModel model;
    public static ResourceLocation TEXTURE = HBM.rl("models/missile/part/mp_w_15_balefire");
    public MissileHeadModel(BakedModel model) {
        super(RenderType::entitySolid);
        this.model = model;
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(6));
        Vector3f vector3f = pPoseStack.last().normal().transform(new Vector3f(1.0F, 1.0F, 1.0F));
        Vector4f vector4f = pPoseStack.last().pose().transform(new Vector4f(1.0F, 1.0F, 1.0F, 1.0F));
        for (BakedQuad quad : model.getQuads(null, null, null, null, null)) {
            int[] vertex = quad.getVertices();
            pBuffer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), pRed, pGreen, pBlue, pAlpha, vertex[0], vertex[1], pPackedOverlay, pPackedLight, vector3f.x(),vector3f.y(), vector3f.z());
        }

        pPoseStack.popPose();
    }
}
