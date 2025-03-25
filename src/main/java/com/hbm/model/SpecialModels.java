package com.hbm.model;

import com.hbm.HBM;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.client.model.renderable.CompositeRenderable;

import java.util.HashMap;
import java.util.Map;


/**
 * 特殊模型，主要是加载到游戏里的Obj模型，绕过MC的渲染，直接用于方块实体和实体的渲染。
 * */
public class SpecialModels implements ResourceManagerReloadListener {
    private static final Map<ResourceLocation, CompositeRenderable> modelCache = new HashMap<>();
    private static final Map<ResourceLocation, BakedModel> bakedModelCache = new HashMap<>();


    public static final CompositeRenderable PRESS_BODY = registerModel(modelLoc("block/press_body"));
    public static final CompositeRenderable PRESS_HEAD = registerModel(modelLoc("block/press_head"));

    public static ResourceLocation modelLoc(String s){return ResourceLocation.tryBuild(HBM.MODID,String.format("models/%s.obj",s));}
    public static ResourceLocation getTexture(ResourceLocation id){
        String path = id.getPath();
        if (path.endsWith(".obj")){
            path = path.substring(0,path.length() - 4);
            String[] split = path.split("/");
            return new ResourceLocation(id.getNamespace(),"model/"+split[split.length-1]);
        }
        return null;
    }

    public static CompositeRenderable registerModel(ResourceLocation id){
        CompositeRenderable compositeRenderable = ObjLoader.INSTANCE.loadModel(new ObjModel.ModelSettings(id, true, true, true, false, null))
                .bakeRenderable(StandaloneGeometryBakingContext.create(id,Map.of("#texture0",getTexture(id))));
        modelCache.put(id,compositeRenderable);
        return compositeRenderable;
    }
    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
    }
}
