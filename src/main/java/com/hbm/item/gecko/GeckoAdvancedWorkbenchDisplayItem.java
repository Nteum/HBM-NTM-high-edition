package com.hbm.item.gecko;

import net.minecraft.world.item.Item;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Placeholder item used only to satisfy legacy Gecko render hooks kept from the
 * original Big Explosives content pack.
 */
public class GeckoAdvancedWorkbenchDisplayItem extends Item implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GeckoAdvancedWorkbenchDisplayItem(Properties properties) {
        super(properties);
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        // Legacy asset has no active animations; keep controller list empty.
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object instance) {
        return 0.0D;
    }
}
