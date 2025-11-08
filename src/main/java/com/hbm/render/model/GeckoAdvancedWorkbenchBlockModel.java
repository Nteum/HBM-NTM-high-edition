package com.hbm.render.model;

import com.hbm.blockentity.dummy.GeckoAdvancedWorkbenchTileEntity;
import com.hbm.compat.bigexplosives.BigExplosivesMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;


public class GeckoAdvancedWorkbenchBlockModel extends GeoModel<GeckoAdvancedWorkbenchTileEntity> {
    public ResourceLocation getAnimationResource(GeckoAdvancedWorkbenchTileEntity animatable) {
        return new ResourceLocation(BigExplosivesMod.MODID, "animations/advancedworkbench_1.animation.json");
    }

    public ResourceLocation getModelResource(GeckoAdvancedWorkbenchTileEntity animatable) {
        return new ResourceLocation(BigExplosivesMod.MODID, "geo/advancedworkbench_1.geo.json");
    }

    public ResourceLocation getTextureResource(GeckoAdvancedWorkbenchTileEntity animatable) {
        return new ResourceLocation(BigExplosivesMod.MODID, "textures/block/texture.png");
    }
}
