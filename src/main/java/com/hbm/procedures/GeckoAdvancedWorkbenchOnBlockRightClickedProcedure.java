package net.mcreator.nuclearcraft.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/GeckoAdvancedWorkbenchOnBlockRightClickedProcedure.class */
public class GeckoAdvancedWorkbenchOnBlockRightClickedProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z) {
        for (int index0 = 0; index0 < 1; index0++) {
            BlockPos _pos = BlockPos.m_274561_(x, y, z);
            BlockState _bs = world.m_8055_(_pos);
            IntegerProperty integerPropertyM_61081_ = _bs.m_60734_().m_49965_().m_61081_("animation");
            if (integerPropertyM_61081_ instanceof IntegerProperty) {
                IntegerProperty _integerProp = integerPropertyM_61081_;
                if (_integerProp.m_6908_().contains(1)) {
                    world.m_7731_(_pos, (BlockState) _bs.m_61124_(_integerProp, 1), 3);
                }
            }
        }
    }
}
