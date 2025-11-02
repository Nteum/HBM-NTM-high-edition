package net.mcreator.nuclearcraft.block.renderer;

import net.mcreator.nuclearcraft.block.display.GeckoAdvancedWorkbenchDisplayItem;
import net.mcreator.nuclearcraft.block.model.GeckoAdvancedWorkbenchDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/block/renderer/GeckoAdvancedWorkbenchDisplayItemRenderer.class */
public class GeckoAdvancedWorkbenchDisplayItemRenderer extends GeoItemRenderer<GeckoAdvancedWorkbenchDisplayItem> {
    public GeckoAdvancedWorkbenchDisplayItemRenderer() {
        super(new GeckoAdvancedWorkbenchDisplayModel());
    }

    public RenderType getRenderType(GeckoAdvancedWorkbenchDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.m_110473_(getTextureLocation(animatable));
    }
}
