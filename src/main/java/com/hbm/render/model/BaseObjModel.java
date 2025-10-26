package com.hbm.render.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BaseObjModel extends Model {
    public String name;
//    public BaseObjModel root;
    public Map<String, BaseObjModel> children = new HashMap();
    public List<Mesh> meshes = new ArrayList<>();
//    public boolean enableMultiRenderType = false;
    public RenderType tempRenderType;
    public float x = 0;
    public float y = 0;
    public float z = 0;
    public float xRot = 0;
    public float yRot = 0;
    public float zRot = 0;
    public float xScale = 1.0f;
    public float yScale = 1.0f;
    public float zScale = 1.0f;
    public boolean visible = true;
    public float xRotPoint = 0;
    public float yRotPoint = 0;
    public float zRotPoint = 0;
    // 用于对模型原本的尺度进行一定的缩放，hbm有的模型需要缩放16，有的不需要
    public float size = 16.0f;
    public BaseObjModel(Function<ResourceLocation, RenderType> pRenderType) {
        this(pRenderType, "");
    }
    public BaseObjModel(BaseObjModel root, String name) {
        this(root.renderType, name);
    }
    public BaseObjModel(Function<ResourceLocation, RenderType> pRenderType, String name) {
        super(pRenderType);
        this.name = name;
    }

    public static BaseObjModel create(ResourceLocation jsonPath, Function<ResourceLocation, RenderType> renderType){
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
                CompositeRenderable renderable = objModel.bakeRenderable(StandaloneGeometryBakingContext.create(textureMap));
                return create(renderable, renderType);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new BaseObjModel(renderType);
    }

    public static BaseObjModel create(CompositeRenderable renderable, Function<ResourceLocation, RenderType> renderType){
        BaseObjModel model = new BaseObjModel(renderType);
        try {
            Class<CompositeRenderable> classRenderable = CompositeRenderable.class;
            Field field_components = classRenderable.getDeclaredField("components");
            field_components.setAccessible(true);
            Class<?>[] declaredClasses = classRenderable.getDeclaredClasses();
            Class<?> classComponent = null;
            Class<?> classMesh = null;
            for (Class<?> declaredClass : declaredClasses) {
                if (declaredClass.getName().contains("Component")) {
                    classComponent = declaredClass;
                }else if (declaredClass.getName().contains("Mesh")){
                    classMesh = declaredClass;
                }
            }
            if (classComponent != null && classMesh != null){
                Field field_name = classComponent.getDeclaredField("name");
                Field field_chlidren = classComponent.getDeclaredField("children");
                Field field_meshes = classComponent.getDeclaredField("meshes");
                Field field_texture = classMesh.getDeclaredField("texture");
                Field field_quads = classMesh.getDeclaredField("quads");
                field_name.setAccessible(true);
                field_chlidren.setAccessible(true);
                field_meshes.setAccessible(true);
                field_texture.setAccessible(true);
                field_quads.setAccessible(true);
                List<?> list1 = (List<?>) field_components.get(renderable);
                for (Object o : list1) {
                    if (classComponent.isInstance(o) && field_name.get(o) instanceof String name){
                        model.children.put(name, parseComponent(o, new BaseObjModel(model, name), classComponent, classMesh, field_name, field_chlidren, field_meshes, field_texture, field_quads));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return model;
    }

    public static BaseObjModel parseComponent(Object o, BaseObjModel component, Class<?> classComponent, Class<?> classMesh, Field field_name,
                                                                Field field_chlidren, Field field_meshes, Field field_texture, Field field_quads) throws IllegalAccessException {
        if (classComponent.isInstance(o) && field_name.get(o) instanceof String name){
            component.name = name;
            if (field_meshes.get(o) instanceof List<?> meshes) {
                for (Object mesh : meshes) {
                    if (classMesh.isInstance(mesh) && field_texture.get(mesh) instanceof ResourceLocation texture && field_quads.get(mesh) instanceof List<?> quads
                            && field_quads.getGenericType() instanceof ParameterizedType && ((ParameterizedType) field_quads.getGenericType()).getActualTypeArguments()[0] == BakedQuad.class) {
                        component.meshes.add(new Mesh(texture, (List<BakedQuad>) quads));
                    }
                }
            }
            if (field_chlidren.get(o) instanceof List<?> children) {
                if (children.isEmpty()){
                    return component;
                } else {
                    for (Object child : children) {
                        if (classComponent.isInstance(child) && field_name.get(child) instanceof String nameInner){
                            component.children.put(nameInner, parseComponent(child, new BaseObjModel(component, nameInner), classComponent, classMesh, field_name, field_chlidren, field_meshes, field_texture,field_quads));
                        }
                    }
                }
            }
        } else return component;
        return component;
    }
    public BaseObjModel getChild(String name){
        return this.children.get(name);
    }

    public BaseObjModel copyPose(ModelPart modelPart){
        this.x = modelPart.x;
        this.y = modelPart.y;
        this.z = modelPart.z;
        // 三维旋转用的是弧度
        this.xRot = modelPart.xRot;
        this.yRot = modelPart.yRot;
        this.zRot = modelPart.zRot;
        this.xScale = modelPart.xScale;
        this.yScale = modelPart.yScale;
        this.zScale = modelPart.zScale;
        this.visible = modelPart.visible;
        return this;
    }
    public BaseObjModel setRotPoint(float x, float y, float z){
        xRotPoint = x;
        yRotPoint = y;
        zRotPoint = z;
        return this;
    }
    public BaseObjModel resetX(){
        this.x = 0;
        return this;
    }
    public BaseObjModel resetY(){
        this.y = 0;
        return this;
    }
    public BaseObjModel resetZ(){
        this.z = 0;
        return this;
    }
    public BaseObjModel adjXYZ(float xDelta, float yDelta, float zDelta, String ... names){
        this.x += xDelta;
        this.y += yDelta;
        this.z += zDelta;
        for (String name : names) {
            this.children.get(name).adjXYZ(xDelta, yDelta, zDelta);
        }
        return this;
    }

    public BaseObjModel setRot(float xRot, float yRot, float zRot){
        this.xRot = xRot;
        this.yRot = yRot;
        this.zRot = zRot;
        return this;
    }

    public BaseObjModel scale(float xScale, float yScale, float zScale){
        this.xScale = xScale;
        this.yScale = yScale;
        this.zScale = zScale;
        return this;
    }
    public BaseObjModel scale(float scale){
        xScale = yScale = zScale = scale;
        this.children.forEach((n, child) -> child.scale(scale));
        return this;
    }
    public BaseObjModel visible(boolean visible, String ... names)
    {
        for (String name : names) {
            this.children.get(name).visible(visible);
        }
        return this;
    }
    public BaseObjModel size(float size, String... names){
        this.size = size;
        if (names.length == 0){
            this.children.values().forEach(child -> child.size(size));
        }else {
            for (String name : names) {
                this.children.get(name).size(size);
            }
        }
        return this;
    }
    public BaseObjModel bindTexture(ResourceLocation texture){
        return bindRenderType(this.renderType(texture));
    }

    public BaseObjModel bindRenderType(RenderType renderType){
        this.tempRenderType = renderType;
        return this;
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, float partialTick, CompositeRenderable.Transforms context){
        render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, context);
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, CompositeRenderable.Transforms context)
    {
        Matrix4f matrix = context.getTransform(name);
        if (matrix != null)
        {
            poseStack.pushPose();
            poseStack.mulPoseMatrix(matrix);
        }

        for (var part : children.values())
            part.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, context);

        for (var mesh : meshes)
            mesh.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay);

        if (matrix != null)
            poseStack.popPose();
    }

    public void renderStatic(PoseStack poseStack, VertexConsumer consumer, int lightmap, int overlay)
    {
        poseStack.pushPose();

        for (var part : children.values())
            part.renderToBuffer(poseStack, consumer, lightmap, overlay);

        for (var mesh : meshes)
            mesh.render(poseStack, consumer, lightmap, overlay);

        poseStack.popPose();
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int lightmap, int overlay){
        renderToBuffer(poseStack, consumer, lightmap, overlay, 1,1,1,1);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer consumer, int lightmap, int overlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        if (visible){
            poseStack.pushPose();

            poseStack.translate(x / size, y / size, z / size);
            if (xRot != 0.0F || yRot != 0.0F || zRot != 0.0F) {
//                poseStack.translate(xRotPoint / size, yRotPoint/ size, zRotPoint/ size);
                poseStack.translate(xRotPoint, yRotPoint, zRotPoint);
                poseStack.mulPose((new Quaternionf()).rotationZYX(zRot, yRot, xRot));
//                poseStack.translate(-xRotPoint / size, -yRotPoint / size, -zRotPoint / size);
                poseStack.translate(-xRotPoint, -yRotPoint, -zRotPoint);
            }

            poseStack.scale(xScale / size, yScale / size, zScale / size);

            if (this.tempRenderType == null){
                for (var part : children.values())
                    part.renderToBuffer(poseStack, consumer, lightmap, overlay, pRed, pGreen, pBlue, pAlpha);
                for (var mesh : meshes)
                    mesh.render(poseStack, consumer, lightmap, overlay, pRed, pGreen, pBlue, pAlpha);
            } else {
                // 通过局部BufferSource渲染
                MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                try {
                    VertexConsumer consumer1 = bufferSource.getBuffer(this.tempRenderType);
                    for (var part : children.values())
                        part.renderToBuffer(poseStack, consumer1, lightmap, overlay, pRed, pGreen, pBlue, pAlpha);
                    for (var mesh : meshes)
                        mesh.render(poseStack, consumer1, lightmap, overlay, pRed, pGreen, pBlue, pAlpha);
                } finally {
                    bufferSource.endBatch();
                }
            }

            poseStack.popPose();
        }

        this.tempRenderType = null;
    }

    public static class Mesh
    {
        public ResourceLocation texture;
        public final List<BakedQuad> quads = new ArrayList<>();

        public Mesh(ResourceLocation texture)
        {
            this.texture = texture;
        }
        public Mesh(ResourceLocation texture, List<BakedQuad> quads){
            this.texture = texture;
            this.quads.addAll(quads);
        }

        public void render(PoseStack poseStack, VertexConsumer consumer, int lightmap, int overlay){
            render(poseStack, consumer, lightmap, overlay, 1,1,1,1);
        }

        public void render(PoseStack poseStack, VertexConsumer consumer, int lightmap, int overlay, float pRed, float pGreen, float pBlue, float pAlpha)
        {
            for (var quad : quads)
            {
                consumer.putBulkData(poseStack.last(), quad, pRed, pGreen, pBlue, pAlpha, lightmap, overlay, true);
            }
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay)
        {
            var consumer = bufferSource.getBuffer(textureRenderTypeLookup.get(texture));
            for (var quad : quads)
            {
                consumer.putBulkData(poseStack.last(), quad, 1, 1, 1, 1, lightmap, overlay, true);
            }
        }
    }

    public static void renderItem(BaseObjModel model, ResourceLocation texture, float size, float xOffset, float yOffset, float zOffset, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay){
        float scale = 0.625f / size;
        float xRot = 30;
        float yRot = 225;
        if (pDisplayContext == ItemDisplayContext.GROUND){
            xRot = yRot = 0;
            scale = 0.25f / size;
        } else if (pDisplayContext == ItemDisplayContext.FIXED){
            xRot = yRot = 0;
            scale = 0.5f / size;
        }else if (pDisplayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
            xRot = 75; yRot = 45;
            scale = 0.375f / size;
        }else if (pDisplayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND){
            xRot = 75;
            scale = 0.375f / size;
        }else if (pDisplayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND){
            yRot = 45;
            scale = 0.40f / size;
        }else if (pDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND){
            scale = 0.40f / size;
        }

        pPoseStack.translate(xOffset, yOffset, zOffset);
        pPoseStack.mulPose(Axis.XN.rotationDegrees(xRot));
        pPoseStack.mulPose(Axis.YN.rotationDegrees(yRot));
        pPoseStack.scale(scale, scale, scale);
        VertexConsumer buffer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.renderStatic(pPoseStack, buffer, pPackedLight, pPackedOverlay);
    }
}
