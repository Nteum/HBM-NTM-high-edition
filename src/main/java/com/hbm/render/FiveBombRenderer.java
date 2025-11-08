package com.hbm.render;

import com.hbm.render.pipeline.PipelineEntityRenderer;
import com.hbm.render.entity.FiveBombEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class FiveBombRenderer extends PipelineEntityRenderer<FiveBombEntity> {
    public FiveBombRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
