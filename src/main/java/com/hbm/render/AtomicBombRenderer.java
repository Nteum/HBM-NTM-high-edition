package com.hbm.render;

import com.hbm.render.pipeline.PipelineEntityRenderer;
import com.hbm.render.entity.AtomicBombEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class AtomicBombRenderer extends PipelineEntityRenderer<AtomicBombEntity> {
    public AtomicBombRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
