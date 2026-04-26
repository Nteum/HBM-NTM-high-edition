package com.hbm.blockentity.dummy;

import com.hbm.blockentity.base.TileProxyCombo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Lightweight stub used by the legacy Gecko renderer/model pipeline. The actual
 * advanced workbench logic was never ported, but geckolib still expects a
 * BlockEntity type parameter.
 */
public class GeckoAdvancedWorkbenchTileEntity extends TileProxyCombo implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GeckoAdvancedWorkbenchTileEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "noop", 0, event -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object instance) {
        return level != null ? level.getGameTime() : 0.0D;
    }
}
