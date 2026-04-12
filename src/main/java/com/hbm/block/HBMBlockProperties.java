package com.hbm.block;

import com.hbm.HBMKey;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class HBMBlockProperties {
    // 多方块机器中是否位于中心方块
    public static final BooleanProperty IS_CORE = BooleanProperty.create(HBMKey.IS_CORE);
    // 任意存在三种变体的形态
    // 对于异虫巢，表示三种异虫巢方块
    // 对于传送带，0 - 向前传送；1 - 左弯；2 - 右弯
    public static final IntegerProperty VARIANT3 = IntegerProperty.create("variant3", 0, 2);
    public static final IntegerProperty VARIANT5 = IntegerProperty.create("variant3", 0, 4);
    public static final IntegerProperty VARIANT8 = IntegerProperty.create("variant3", 0, 7);
    // 相对方向，指某个面相对于已知面的方向
    // 0 - 对面，1 - 左面 2 - 右面 3 - 上面 4 - 下面
    public static final IntegerProperty RELATIVE_DIRECTION = IntegerProperty.create("relative_dir", 0, 4);
}
