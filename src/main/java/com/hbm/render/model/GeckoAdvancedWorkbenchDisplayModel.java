package com.hbm.render.model;

import com.hbm.compat.bigexplosives.BigExplosivesMod;
import com.hbm.item.gecko.GeckoAdvancedWorkbenchDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;


public class GeckoAdvancedWorkbenchDisplayModel extends GeoModel<GeckoAdvancedWorkbenchDisplayItem> {
    public ResourceLocation getAnimationResource(GeckoAdvancedWorkbenchDisplayItem animatable) {
        return new ResourceLocation(BigExplosivesMod.MODID, "animations/advancedworkbench_1.animation.json");
    }

    public ResourceLocation getModelResource(GeckoAdvancedWorkbenchDisplayItem animatable) {
        return new ResourceLocation(BigExplosivesMod.MODID, "geo/advancedworkbench_1.geo.json");
    }

    public ResourceLocation getTextureResource(GeckoAdvancedWorkbenchDisplayItem entity) {
        return new ResourceLocation(BigExplosivesMod.MODID, "textures/block/texture.png");
    }
}
