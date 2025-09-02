package com.hbm.render.model.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;

import java.util.Map;

public class ObjEntityModelSingle extends Model {
    private ObjModel model;
    private Map<String, ObjModelPart> parts = Maps.newLinkedHashMap();
    public CompositeRenderable renderable;
    public ObjEntityModelSingle(ObjModel objModel) {
        super(RenderType::entityCutoutNoCull);
        this.model = objModel;
        renderable = objModel.bakeRenderable(StandaloneGeometryBakingContext.INSTANCE);
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
    }

    public void renderModel(PoseStack poseStack, MultiBufferSource bufferSource, int lightmap, float partialTick){
        renderable.render(poseStack, bufferSource, RenderType::entityCutoutNoCull, lightmap, OverlayTexture.NO_OVERLAY, partialTick, CompositeRenderable.Transforms.EMPTY);
    }
}
