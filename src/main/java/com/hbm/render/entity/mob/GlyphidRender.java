package com.hbm.render.entity.mob;

import com.hbm.entity.mob.EntityGlyphid;
import com.hbm.main.ResourceManager;
import com.hbm.render.model.Models;
import com.hbm.render.model.entity.ModelGlyphid;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GlyphidRender<T extends EntityGlyphid> extends MobRenderer<T, ModelGlyphid<T>> {
    public GlyphidRender(EntityRendererProvider.Context pContext, ModelGlyphid<T> pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    public GlyphidRender(EntityRendererProvider.Context context) {
        this(context, (ModelGlyphid<T>) Models.getEntityModel(Models.GLYPHID), 1.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(T pEntity) {
        return ResourceManager.glyphid_tex;
    }
}
