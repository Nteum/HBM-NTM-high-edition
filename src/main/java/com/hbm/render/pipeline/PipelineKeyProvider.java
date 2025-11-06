package com.hbm.render.pipeline;
import net.minecraft.resources.ResourceLocation;

public interface PipelineKeyProvider {
    ResourceLocation getPipelineKey();
    default String getPipelineTextureKey() {
        return "default";
    }
}
