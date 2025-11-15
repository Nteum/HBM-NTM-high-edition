package com.hbm.render;

import com.hbm.blockentity.dummy.GeckoAdvancedWorkbenchTileEntity;
import com.hbm.render.model.GeckoAdvancedWorkbenchBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
public class GeckoAdvancedWorkbenchTileRenderer extends GeoBlockRenderer<GeckoAdvancedWorkbenchTileEntity> {
    public GeckoAdvancedWorkbenchTileRenderer() {
        super(new GeckoAdvancedWorkbenchBlockModel());
    }

    public RenderType getRenderType(GeckoAdvancedWorkbenchTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
