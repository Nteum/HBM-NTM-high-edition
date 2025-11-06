package net.mcreator.nuclearcraft.client.renderer;

import com.hbm.render.pipeline.PipelineEntityRenderer;
import net.mcreator.nuclearcraft.entity.AtomicBombExplosionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class AtomicBombExplosionRenderer extends PipelineEntityRenderer<AtomicBombExplosionEntity> {
    public AtomicBombExplosionRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
