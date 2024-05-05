package com.hbm.block.genetic;

import net.minecraft.block.Block;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.state.property.BooleanProperty;

public class ReinforcedLamp extends Block {
    public static final BooleanProperty isOn = BooleanProperty.of("isOn");

    public ReinforcedLamp(Settings settings) {
        super(settings);
    }
}
