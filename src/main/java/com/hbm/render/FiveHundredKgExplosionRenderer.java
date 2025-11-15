package com.hbm.render;

import com.hbm.render.pipeline.PipelineEntityRenderer;
import com.hbm.render.entity.FiveHundredKgExplosionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class FiveHundredKgExplosionRenderer extends PipelineEntityRenderer<FiveHundredKgExplosionEntity> {
    public FiveHundredKgExplosionRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
