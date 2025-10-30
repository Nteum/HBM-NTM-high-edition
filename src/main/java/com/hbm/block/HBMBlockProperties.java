package com.hbm.block;

import com.hbm.HBMKey;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class HBMBlockProperties {
    public static final BooleanProperty IS_CORE = BooleanProperty.create(HBMKey.IS_CORE);
    public static final IntegerProperty VARIANT3 = IntegerProperty.create("variant3", 0, 2);
}
