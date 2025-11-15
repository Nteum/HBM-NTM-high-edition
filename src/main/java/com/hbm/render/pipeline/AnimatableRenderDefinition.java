package com.hbm.render.pipeline;

import com.google.common.collect.ImmutableMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import com.google.gson.JsonObject;

/**
 * Immutable description of a GeckoLib renderable model, animation and texture
 * set. Instances are typically created from JSON definitions at resource-load
 * time.
 */
public record AnimatableRenderDefinition(
        ResourceLocation id,
        ResourceLocation model,
        @Nullable ResourceLocation animation,
        Map<String, ResourceLocation> textures,
        RenderLayerType renderLayer,
        float shadowRadius,
        float scale) {

    private static final String DEFAULT_TEXTURE_KEY = "default";

    public AnimatableRenderDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(model, "model");
        Objects.requireNonNull(textures, "textures");
        if (textures.isEmpty()) {
            throw new IllegalArgumentException("Textures map must not be empty for " + id);
        }
        if (!textures.containsKey(DEFAULT_TEXTURE_KEY)) {
            throw new IllegalArgumentException("Render definition " + id + " is missing a \"default\" texture entry");
        }
        textures = ImmutableMap.copyOf(textures);
        renderLayer = Objects.requireNonNullElse(renderLayer, RenderLayerType.CUTOUT);
        shadowRadius = Math.max(0.0F, shadowRadius);
        scale = scale <= 0.0F ? 1.0F : scale;
    }

    public ResourceLocation texture(final String key) {
        if (key != null && !key.isBlank()) {
            final ResourceLocation variant = textures.get(key);
            if (variant != null) {
                return variant;
            }
            final ResourceLocation normalized = textures.get(key.toLowerCase(Locale.ROOT));
            if (normalized != null) {
                return normalized;
            }
        }
        return textures.get(DEFAULT_TEXTURE_KEY);
    }

    public static AnimatableRenderDefinition fromJson(final ResourceLocation fallbackId, final JsonObject json) {
        final ResourceLocation id = json.has("id")
                ? new ResourceLocation(GsonHelper.getAsString(json, "id"))
                : fallbackId;
        final ResourceLocation model = new ResourceLocation(GsonHelper.getAsString(json, "model"));
        final ResourceLocation animation = json.has("animation")
                ? new ResourceLocation(GsonHelper.getAsString(json, "animation"))
                : null;
        final RenderLayerType layer = json.has("render_type")
                ? RenderLayerType.byName(GsonHelper.getAsString(json, "render_type"))
                : RenderLayerType.CUTOUT;
        final float shadowRadius = GsonHelper.getAsFloat(json, "shadow_radius", 0.5F);
        final float scale = GsonHelper.getAsFloat(json, "scale", 1.0F);

        final JsonObject textureObj = GsonHelper.getAsJsonObject(json, "textures");
        final ImmutableMap.Builder<String, ResourceLocation> textures = ImmutableMap.builder();
        for (String key : textureObj.keySet()) {
            textures.put(key, new ResourceLocation(GsonHelper.getAsString(textureObj, key)));
        }

        return new AnimatableRenderDefinition(id, model, animation, textures.build(), layer, shadowRadius, scale);
    }
}
