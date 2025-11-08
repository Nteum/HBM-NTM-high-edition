package com.hbm.render;

import com.hbm.render.pipeline.PipelineEntityRenderer;
import com.hbm.render.entity.AtomicBombExplosionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class AtomicBombExplosionRenderer extends PipelineEntityRenderer<AtomicBombExplosionEntity> {
    public AtomicBombExplosionRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
