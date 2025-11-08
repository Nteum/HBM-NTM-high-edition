package com.hbm.render;

import com.hbm.item.gecko.GeckoAdvancedWorkbenchDisplayItem;
import com.hbm.render.model.GeckoAdvancedWorkbenchDisplayModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;


public class GeckoAdvancedWorkbenchDisplayItemRenderer extends GeoItemRenderer<GeckoAdvancedWorkbenchDisplayItem> {
    public GeckoAdvancedWorkbenchDisplayItemRenderer() {
        super(new GeckoAdvancedWorkbenchDisplayModel());
    }

    public RenderType getRenderType(GeckoAdvancedWorkbenchDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
