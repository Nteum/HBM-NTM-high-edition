package com.hbm.render.model.entity;

import com.hbm.HBM;
import com.hbm.entity.effect.EntityCoreExplosion;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CoreExplosionModel extends GeoModel<EntityCoreExplosion> {
    @Override
    public ResourceLocation getModelResource(EntityCoreExplosion animatable) {
        return HBM.rl("geo/" + animatable.getVariant().resourceKey() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EntityCoreExplosion animatable) {
        return HBM.rl("textures/entity/" + animatable.getVariant().resourceKey() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(EntityCoreExplosion animatable) {
        return HBM.rl("animations/" + animatable.getVariant().resourceKey() + ".animation.json");
    }
}
