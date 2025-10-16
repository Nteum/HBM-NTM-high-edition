package com.hbm.render.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.IRenderable;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IObjModel {
    List<String> textureTags = new ArrayList<>(List.of("texture0", "#texture0", "#layer0"));
    ModelPart EMPTY = new ModelPart(List.of(), Map.of());
    ModelPart DUMMY_HUMANOID = new ModelPart(List.of(), Map.of("head",EMPTY, "hat",EMPTY, "body", EMPTY, "right_arm",EMPTY, "left_arm",EMPTY, "right_leg", EMPTY, "left_leg", EMPTY));
    IRenderable getRenderable();
    void setRenderable(IRenderable renderable);
    default void parseJson(ResourceLocation jsonPath){
        Gson gson = new Gson();
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        try {
            Resource resource = resourceManager.getResourceOrThrow(jsonPath.withSuffix(".json"));
            try (InputStreamReader reader = new InputStreamReader(resource.open())){
                JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
                ObjModel objModel = ObjLoader.INSTANCE.read(jsonObject, null);
                Map<String, ResourceLocation> textureMap = new HashMap<>();
                if (jsonObject.has("texture0")) textureMap.put("#texture0", new ResourceLocation(jsonObject.get("texture0").getAsString()));
                if (jsonObject.has("#layer0")) textureMap.put("#layer0", new ResourceLocation(jsonObject.get("layer0").getAsString()));
//                for (String string : textureTags) {
//                    textureMap.put(string, new ResourceLocation(jsonObject.get(string).getAsString()));
//                }
                setRenderable(objModel.bakeRenderable(StandaloneGeometryBakingContext.create(textureMap)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    default void renderModel(PoseStack poseStack, MultiBufferSource bufferSource, int lightmap, float partialTick){
        getRenderable().render(poseStack, bufferSource, RenderType::entityCutoutNoCull, lightmap, OverlayTexture.NO_OVERLAY, partialTick, CompositeRenderable.Transforms.EMPTY);
    }
}
