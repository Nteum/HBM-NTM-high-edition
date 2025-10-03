package com.hbm.render.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.IRenderable;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
/**
 * 基于forge的CompositeRenderable修改，逻辑没有改变，只是把内部类变成public的
 * 通过反射解决此问题，除此之外似乎别无他法。
 * */
public class AccessableRenderable implements IRenderable<CompositeRenderable.Transforms> {
    public List<AccessableRenderable.Component> components = new ArrayList<>();

    private AccessableRenderable() { }

    public AccessableRenderable(CompositeRenderable renderable){
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
                    components.add(parseComponent(o, new Component(), classComponent, classMesh, field_name, field_chlidren, field_meshes, field_texture, field_quads));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Component parseComponent(Object o, Component component, Class<?> classComponent, Class<?> classMesh, Field field_name,
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
                        component.children.add(parseComponent(child, new Component(), classComponent, classMesh, field_name, field_chlidren, field_meshes, field_texture,field_quads));
                    }
                }
            }
        } else return component;
        return component;
    }
    
    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, float partialTick, CompositeRenderable.Transforms context) {
        for (var component : components)
            component.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, context);
    }
    public static class Component
    {
        public String name;
        public List<AccessableRenderable.Component> children = new ArrayList<>();
        public List<AccessableRenderable.Mesh> meshes = new ArrayList<>();

        public Component(){ }
        public Component(String name)
        {
            this.name = name;
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, CompositeRenderable.Transforms context)
        {
            Matrix4f matrix = context.getTransform(name);
            if (matrix != null)
            {
                poseStack.pushPose();
                poseStack.mulPoseMatrix(matrix);
            }

            for (var part : children)
                part.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, context);

            for (var mesh : meshes)
                mesh.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay);

            if (matrix != null)
                poseStack.popPose();
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

        public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay)
        {
            var consumer = bufferSource.getBuffer(textureRenderTypeLookup.get(texture));
            for (var quad : quads)
            {
                consumer.putBulkData(poseStack.last(), quad, 1, 1, 1, 1, lightmap, overlay, true);
            }
        }
    }
}
