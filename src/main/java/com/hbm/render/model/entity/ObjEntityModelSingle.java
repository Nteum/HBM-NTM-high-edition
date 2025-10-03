package com.hbm.render.model.entity;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hbm.render.model.IObjModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.IRenderable;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ObjEntityModelSingle extends Model implements IObjModel {
    public CompositeRenderable renderable;
    public ObjEntityModelSingle(ObjModel objModel) {
        super(RenderType::entityCutoutNoCull);
        renderable = objModel.bakeRenderable(StandaloneGeometryBakingContext.INSTANCE);
    }
    public ObjEntityModelSingle(){
        super(RenderType::entityCutoutNoCull);
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
    }

    @Override
    public IRenderable<CompositeRenderable.Transforms> getRenderable() {
        return renderable;
    }

    @Override
    public void setRenderable(IRenderable renderable) {
        this.renderable = (CompositeRenderable) renderable;
    }
}
