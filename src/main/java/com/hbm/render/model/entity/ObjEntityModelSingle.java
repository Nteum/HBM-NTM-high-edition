package com.hbm.render.model.entity;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ObjEntityModelSingle extends Model {
    public CompositeRenderable renderable;
    public ObjEntityModelSingle(ObjModel objModel) {
        super(RenderType::entityCutoutNoCull);
        renderable = objModel.bakeRenderable(StandaloneGeometryBakingContext.INSTANCE);
    }
    public ObjEntityModelSingle(){
        super(RenderType::entityCutoutNoCull);
    }

    public void parseJson(ResourceLocation jsonPath){
        Gson gson = new Gson();
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        try {
            Resource resource = resourceManager.getResourceOrThrow(jsonPath.withSuffix(".json"));
            try (InputStreamReader reader = new InputStreamReader(resource.open())){
                JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
                ObjModel objModel = ObjLoader.INSTANCE.read(jsonObject, null);
                Map<String, ResourceLocation> textureMap = new HashMap<>();
                if (jsonObject.has("#texture0")) textureMap.put("#texture0", new ResourceLocation(jsonObject.get("texture0").getAsString()));
                if (jsonObject.has("#layer0")) textureMap.put("#layer0", new ResourceLocation(jsonObject.get("layer0").getAsString()));
                renderable = objModel.bakeRenderable(StandaloneGeometryBakingContext.create(textureMap));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
    }

    public void renderModel(PoseStack poseStack, MultiBufferSource bufferSource, int lightmap, float partialTick){
        renderable.render(poseStack, bufferSource, RenderType::entityCutoutNoCull, lightmap, OverlayTexture.NO_OVERLAY, partialTick, CompositeRenderable.Transforms.EMPTY);
    }
}
