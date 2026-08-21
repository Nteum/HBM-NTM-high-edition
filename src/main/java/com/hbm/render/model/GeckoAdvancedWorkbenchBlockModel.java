package com.hbm.render.model;

import com.hbm.blockentity.dummy.GeckoAdvancedWorkbenchTileEntityBE;
import com.hbm.compat.bigexplosives.BigExplosivesMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;


public class GeckoAdvancedWorkbenchBlockModel extends GeoModel<GeckoAdvancedWorkbenchTileEntityBE> {
    public ResourceLocation getAnimationResource(GeckoAdvancedWorkbenchTileEntityBE animatable) {
        return ResourceLocation.fromNamespaceAndPath(BigExplosivesMod.MODID, "animations/advancedworkbench_1.animation.json");
    }

    public ResourceLocation getModelResource(GeckoAdvancedWorkbenchTileEntityBE animatable) {
        return ResourceLocation.fromNamespaceAndPath(BigExplosivesMod.MODID, "geo/advancedworkbench_1.geo.json");
    }

    public ResourceLocation getTextureResource(GeckoAdvancedWorkbenchTileEntityBE animatable) {
        return ResourceLocation.fromNamespaceAndPath(BigExplosivesMod.MODID, "textures/block/texture.png");
    }
}
