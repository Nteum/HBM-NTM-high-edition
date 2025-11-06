package com.hbm.render.pipeline;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hbm.HBM;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Client-side loader that maps {@link PipelineKeyProvider} identifiers to
 * {@link AnimatableRenderDefinition} metadata. Reloaded automatically when
 * resource packs change.
 */
public final class GeoRenderPipeline extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final GeoRenderPipeline INSTANCE = new GeoRenderPipeline();
    private static final Logger LOGGER = LogManager.getLogger("HBM-GeoRenderPipeline");

    private static final ResourceLocation FALLBACK_MODEL = HBM.rl("geo/pipeline_placeholder.geo.json");
    private static final ResourceLocation FALLBACK_ANIMATION = HBM.rl("animations/pipeline_generic.animation.json");
    private static final ResourceLocation FALLBACK_TEXTURE = HBM.rl("textures/entity/duck.png");
    private static final AnimatableRenderDefinition FALLBACK_DEFINITION = new AnimatableRenderDefinition(
            HBM.rl("fallback_placeholder"),
            FALLBACK_MODEL,
            FALLBACK_ANIMATION,
            Map.of("default", FALLBACK_TEXTURE),
            RenderLayerType.CUTOUT,
            0.5F,
            1.0F
    );

    private volatile Map<ResourceLocation, AnimatableRenderDefinition> definitions = Map.of();
    private final Set<ResourceLocation> reportedMissing = Collections.synchronizedSet(new HashSet<>());

    private GeoRenderPipeline() {
        super(GSON, "render_pipeline");
    }

    @Override
    protected void apply(final Map<ResourceLocation, JsonElement> jsons, final ResourceManager manager, final ProfilerFiller profiler) {
        final Map<ResourceLocation, AnimatableRenderDefinition> resolved = new HashMap<>();

        jsons.forEach((fileId, element) -> {
            if (!element.isJsonObject()) {
                LOGGER.warn("Ignoring non-object render pipeline definition at {}", fileId);
                return;
            }

            final JsonObject json = element.getAsJsonObject();
            if (json.has("entries")) {
                json.getAsJsonArray("entries").forEach(entry -> readEntry(fileId, entry, resolved));
            } else {
                readEntry(fileId, json, resolved);
            }
        });

        this.definitions = Map.copyOf(resolved);
        this.reportedMissing.clear();
        LOGGER.info("Loaded {} GeckoLib render definitions", this.definitions.size());
    }

    private void readEntry(final ResourceLocation fileId, final JsonElement element, final Map<ResourceLocation, AnimatableRenderDefinition> sink) {
        if (!element.isJsonObject()) {
            LOGGER.warn("Skipping malformed render pipeline entry in {}", fileId);
            return;
        }
        final JsonObject entryJson = element.getAsJsonObject();
        final AnimatableRenderDefinition definition = AnimatableRenderDefinition.fromJson(
                new ResourceLocation(fileId.getNamespace(), fileId.getPath()),
                entryJson
        );
        sink.put(definition.id(), definition);
    }

    public AnimatableRenderDefinition definition(final ResourceLocation id) {
        final AnimatableRenderDefinition definition = definitions.get(id);
        if (definition != null) {
            return definition;
        }
        if (reportedMissing.add(id)) {
            LOGGER.warn("Missing GeckoLib render definition for {}, using fallback placeholder", id);
        }
        return FALLBACK_DEFINITION;
    }

    public Map<ResourceLocation, AnimatableRenderDefinition> definitions() {
        return definitions;
    }

    public ResourceLocation fallbackModel() {
        return FALLBACK_MODEL;
    }

    public ResourceLocation fallbackAnimation() {
        return FALLBACK_ANIMATION;
    }

    public ResourceLocation fallbackTexture() {
        return FALLBACK_TEXTURE;
    }
}
