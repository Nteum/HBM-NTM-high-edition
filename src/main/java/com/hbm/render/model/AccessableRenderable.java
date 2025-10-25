package com.hbm.render.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.IRenderable;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 基于forge的CompositeRenderable修改，逻辑没有改变，只是把内部类变成public的
 * 通过反射解决此问题，除此之外似乎别无他法。
 * */
public class AccessableRenderable {
    public Function<ResourceLocation, RenderType> renderType;
    public Map<String, Component> components = new HashMap<>();
    public AccessableRenderable(CompositeRenderable renderable) {
        this(renderable, RenderType::entityCutoutNoCull);
    }

    public AccessableRenderable(CompositeRenderable renderable, Function<ResourceLocation, RenderType> renderType){
        this.renderType = renderType;
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
                        components.put(name, parseComponent(o, new Component(this.renderType), classComponent, classMesh, field_name, field_chlidren, field_meshes, field_texture, field_quads));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Component parseComponent(Object o, Component component, Class<?> classComponent, Class<?> classMesh, Field field_name,
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
                            component.children.put(nameInner, parseComponent(child, new Component(component.getRenderType()), classComponent, classMesh, field_name, field_chlidren, field_meshes, field_texture,field_quads));
                        }
                    }
                }
            }
        } else return component;
        return component;
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation rl, int lightmap, int overlay, float partialTick) {
        render(poseStack, bufferSource, (ResourceLocation) this.renderType, lightmap, overlay, partialTick);
    }
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, float partialTick) {
        for (var component : components.values())
            component.render(poseStack, bufferSource.getBuffer(textureRenderTypeLookup.get(null)), lightmap, overlay);
    }
    public static class Component extends Model
    {
        public String name;
        public Map<String, Component> children = new HashMap();
        public List<Mesh> meshes = new ArrayList<>();
//        public ModelPartTransform trans = ModelPartTransform.DEFAULT;
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

        public Component(Function<ResourceLocation, RenderType> renderType){
            super(renderType);
        }
//        public Component(String name)
//        {
//            this.name = name;
//        }

        public Component copyPose(ModelPart modelPart){
            this.x = modelPart.x;
            this.y = modelPart.y;
            this.z = modelPart.z;
            this.xRot = modelPart.xRot;
            this.yRot = modelPart.yRot;
            this.zRot = modelPart.zRot;
            this.xScale = modelPart.xScale;
            this.yScale = modelPart.yScale;
            this.zScale = modelPart.zScale;
            this.visible = modelPart.visible;
            return this;
        }
        public Component setRotPoint(float x, float y, float z){
            xRotPoint = x;
            yRotPoint = y;
            zRotPoint = z;
            return this;
        }
        public Component resetX(){
            this.x = 0;
            return this;
        }
        public Component resetY(){
            this.y = 0;
            return this;
        }
        public Component resetZ(){
            this.z = 0;
            return this;
        }
        public Component adjXYZ(float xDelta, float yDelta, float zDelta){
            this.x += xDelta;
            this.y += yDelta;
            this.z += zDelta;
            return this;
        }

        public Function<ResourceLocation, RenderType> getRenderType(){
            return this.renderType;
        }

        public void render(PoseStack poseStack, VertexConsumer consumer, int lightmap, int overlay)
        {
            if (visible){
                poseStack.pushPose();

                poseStack.translate(x / 16.0F, y / 16.0F, z / 16.0F);
                if (xRot != 0.0F || yRot != 0.0F || zRot != 0.0F) {
                    poseStack.translate(xRotPoint/ 16, yRotPoint/ 16, zRotPoint/ 16);
                    poseStack.mulPose((new Quaternionf()).rotationZYX(zRot, yRot, xRot));
                    poseStack.translate(-xRotPoint/ 16, -yRotPoint/ 16, -zRotPoint/ 16);
                }

                poseStack.scale(xScale / 16.0f, yScale / 16.0f, zScale / 16.0f);

                for (var part : children.values())
                    part.render(poseStack, consumer, lightmap, overlay);

                for (var mesh : meshes)
                    mesh.render(poseStack, consumer, lightmap, overlay);

                poseStack.popPose();
            }
        }

        public void renderGUI(PoseStack poseStack, VertexConsumer consumer, int lightmap, int overlay)
        {
            poseStack.pushPose();

            for (var part : children.values())
                part.render(poseStack, consumer, lightmap, overlay);

            for (var mesh : meshes)
                mesh.render(poseStack, consumer, lightmap, overlay);

            poseStack.popPose();
        }

        @Override
        public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {

        }
    }

    public static class Mesh
    {
        public final ResourceLocation texture;
        public final List<BakedQuad> quads = new ArrayList<>();

        public Mesh(ResourceLocation texture)
        {
            this.texture = texture;
        }
        public Mesh(ResourceLocation texture, List<BakedQuad> quads){
            this.texture = texture;
            this.quads.addAll(quads);
        }

        public void render(PoseStack poseStack, VertexConsumer consumer, int lightmap, int overlay)
        {
            for (var quad : quads)
            {
                consumer.putBulkData(poseStack.last(), quad, 1, 1, 1, 1, lightmap, overlay, true);
            }
        }
    }
}
