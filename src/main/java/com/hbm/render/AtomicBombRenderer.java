package net.mcreator.nuclearcraft.client.renderer;

import com.hbm.render.pipeline.PipelineEntityRenderer;
import net.mcreator.nuclearcraft.entity.AtomicBombEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class AtomicBombRenderer extends PipelineEntityRenderer<AtomicBombEntity> {
    public AtomicBombRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
