package com.hbm.render.pipeline;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public enum RenderLayerType {
    CUTOUT {
        @Override
        public RenderType resolve(final ResourceLocation texture) {
            return RenderType.entityCutoutNoCull(texture);
        }
    },
    TRANSLUCENT {
        @Override
        public RenderType resolve(final ResourceLocation texture) {
            return RenderType.entityTranslucent(texture);
        }
    },
    EYES {
        @Override
        public RenderType resolve(final ResourceLocation texture) {
            return RenderType.eyes(texture);
        }
    };

    public RenderType resolve(final ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }

    public static RenderLayerType byName(final String name) {
        for (RenderLayerType value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return CUTOUT;
    }
}
