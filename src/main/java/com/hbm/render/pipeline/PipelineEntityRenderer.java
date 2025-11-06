package com.hbm.render.pipeline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Shared GeckoLib renderer that derives its resources and render behaviour
 * from {@link GeoRenderPipeline}. Concrete entity renderers only need to
 * supply a constructor calling {@code super(context)}.
 */
public class PipelineEntityRenderer<T extends net.minecraft.world.entity.Entity & GeoEntity & PipelineKeyProvider>
        extends GeoEntityRenderer<T> {

    public PipelineEntityRenderer(final EntityRendererProvider.Context context) {
        super(context, new PipelineGeoModel<>());
    }

    @Override
    public RenderType getRenderType(final T animatable, final ResourceLocation texture, final MultiBufferSource bufferSource, final float partialTick) {
        final AnimatableRenderDefinition definition = GeoRenderPipeline.INSTANCE.definition(animatable.getPipelineKey());
        return definition.renderLayer().resolve(texture);
    }

    @Override
    public void preRender(final PoseStack poseStack,
                          final T animatable,
                          final BakedGeoModel model,
                          final MultiBufferSource bufferSource,
                          final VertexConsumer buffer,
                          final boolean isReRender,
                          final float partialTick,
                          final int packedLight,
                          final int packedOverlay,
                          final float red,
                          final float green,
                          final float blue,
                          final float alpha) {
        final AnimatableRenderDefinition definition = GeoRenderPipeline.INSTANCE.definition(animatable.getPipelineKey());
        this.shadowRadius = definition.shadowRadius();
        this.scaleHeight = definition.scale();
        this.scaleWidth = definition.scale();
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
