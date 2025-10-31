package net.mcreator.nuclearcraft.block.model;

import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.mcreator.nuclearcraft.block.entity.GeckoAdvancedWorkbenchTileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/block/model/GeckoAdvancedWorkbenchBlockModel.class */
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
