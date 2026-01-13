package com.hbm.block.machine.pile;

import net.minecraft.world.level.block.state.properties.BooleanProperty;

public final class ChicagoPileStateProperties {

    private ChicagoPileStateProperties() {
    }

    public static final BooleanProperty SHIELDED = BooleanProperty.create("shielded");
    public static final BooleanProperty EXTENDED = BooleanProperty.create("extended");
    public static final BooleanProperty TRIGGERED = BooleanProperty.create("triggered");
}
