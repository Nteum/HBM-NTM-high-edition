package com.hbm.render.model.engine;

import com.google.gson.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.obj.ObjModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.joml.Quaterniond;

/**
 * forge 的 OjbLoader的问题：
 * 1. 默认的流程生成SimpleBakedModel，这种模型取消了obj中模型分体的细节，无法被分块渲染
 * 2. 特殊流程生成CompositeRenderable，可以分体渲染，用于实体，但是所有mesh都只能套一个贴图，无法处理多个贴图的物品。
 * 3. 有读mtl文件的能力，但只能从mtl中读入贴图位置，mtl的其他功能没有被使用
 * */
public class CustomModelLoader implements IGeometryLoader<ObjModel>, ResourceManagerReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final CustomModelLoader INSTANCE = new CustomModelLoader();
    private static final Logger LOGGER = LogManager.getLogger("HBM-CustomModelLoader");
    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
    }

    @Override
    public ObjModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        return null;
    }
}
