package net.mcreator.nuclearcraft.block.renderer;

import net.mcreator.nuclearcraft.block.entity.GeckoAdvancedWorkbenchTileEntity;
import net.mcreator.nuclearcraft.block.model.GeckoAdvancedWorkbenchBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/block/renderer/GeckoAdvancedWorkbenchTileRenderer.class */
public class GeckoAdvancedWorkbenchTileRenderer extends GeoBlockRenderer<GeckoAdvancedWorkbenchTileEntity> {
    public GeckoAdvancedWorkbenchTileRenderer() {
        super(new GeckoAdvancedWorkbenchBlockModel());
    }

    public RenderType getRenderType(GeckoAdvancedWorkbenchTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.m_110473_(getTextureLocation(animatable));
    }
}
