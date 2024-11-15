package com.hbm.render.entity;

import com.hbm.entity.logic.EntityNukeExplosionMK5;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
/**
 * 没有任何作用的render，用于给一些不需要渲染的实体注册。
 * */
@OnlyIn(Dist.CLIENT)
public class EntityBlankRender extends EntityRenderer<Entity> {
    public EntityBlankRender(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity pEntity) {
        return null;
    }
}
