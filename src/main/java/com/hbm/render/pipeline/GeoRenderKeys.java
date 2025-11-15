package com.hbm.render.pipeline;

import net.minecraft.resources.ResourceLocation;

/**
 * Centralised registry keys describing renderable assets that participate in
 * the GeckoLib automation pipeline. These are referenced from both logical
 * (server) and client code, so keep dependencies minimal.
 */
public final class GeoRenderKeys {

    public static final ResourceLocation FIVE_BOMB = new ResourceLocation("big_explosives", "five_bomb");
    public static final ResourceLocation FIVE_HUNDRED_KG_EXPLOSION = new ResourceLocation("big_explosives", "five_hundred_kg_explosion");
    public static final ResourceLocation ATOMIC_BOMB = new ResourceLocation("big_explosives", "atomic_bomb");
    public static final ResourceLocation ATOMIC_BOMB_EXPLOSION = new ResourceLocation("big_explosives", "atomic_bomb_explosion");

    private GeoRenderKeys() {
    }
}
