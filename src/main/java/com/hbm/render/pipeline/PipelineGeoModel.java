package com.hbm.render.pipeline;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

/**
 * Generic {@link GeoModel} implementation that retrieves its resources from
 * {@link GeoRenderPipeline} using the identifier exposed by the animatable.
 */
public class PipelineGeoModel<T extends GeoAnimatable & PipelineKeyProvider> extends GeoModel<T> {

    @Override
    public ResourceLocation getModelResource(final T animatable) {
        return GeoRenderPipeline.INSTANCE.definition(animatable.getPipelineKey()).model();
    }

    @Override
    public ResourceLocation getTextureResource(final T animatable) {
        final AnimatableRenderDefinition definition = GeoRenderPipeline.INSTANCE.definition(animatable.getPipelineKey());
        return definition.texture(animatable.getPipelineTextureKey());
    }

    @Override
    public ResourceLocation getAnimationResource(final T animatable) {
        final AnimatableRenderDefinition definition = GeoRenderPipeline.INSTANCE.definition(animatable.getPipelineKey());
        final ResourceLocation animation = definition.animation();
        return animation != null ? animation : GeoRenderPipeline.INSTANCE.fallbackAnimation();
    }
}
