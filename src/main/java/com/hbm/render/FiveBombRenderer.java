package net.mcreator.nuclearcraft.client.renderer;

import com.hbm.render.pipeline.PipelineEntityRenderer;
import net.mcreator.nuclearcraft.entity.FiveBombEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class FiveBombRenderer extends PipelineEntityRenderer<FiveBombEntity> {
    public FiveBombRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
