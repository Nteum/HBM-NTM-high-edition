package net.mcreator.nuclearcraft.block.model;

import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.mcreator.nuclearcraft.block.display.GeckoAdvancedWorkbenchDisplayItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/block/model/GeckoAdvancedWorkbenchDisplayModel.class */
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
