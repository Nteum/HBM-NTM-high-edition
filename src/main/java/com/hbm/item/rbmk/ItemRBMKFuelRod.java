package com.hbm.item.rbmk;

import net.minecraft.world.item.Item;

/**
 * Minimal RBMK fuel rod item used by the modernized RBMK fuel channel.
 */
public class ItemRBMKFuelRod extends Item {

    private final double heatPerSecond;
    private final int burnTimeTicks;

    public ItemRBMKFuelRod(Properties properties, double heatPerSecond, int burnTimeTicks) {
        super(properties);
        this.heatPerSecond = Math.max(0.0D, heatPerSecond);
        this.burnTimeTicks = Math.max(0, burnTimeTicks);
    }

    public double heatPerSecond() {
        return heatPerSecond;
    }

    public int burnTimeTicks() {
        return burnTimeTicks;
    }

    public double fastFluxPerSecond() {
        return Math.max(1.0D, heatPerSecond * 0.55D);
    }

    public double slowFluxPerSecond() {
        return Math.max(0.0D, heatPerSecond * 0.15D);
    }

    public double coreMaxHeat() {
        return Math.max(1_200.0D, heatPerSecond * 80.0D);
    }
}
