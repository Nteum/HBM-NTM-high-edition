package net.mcreator.nuclearcraft.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelAccessor;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/TwoFiddyButtonRightclickedProcedure.class */
public class TwoFiddyButtonRightclickedProcedure {
    public static void execute(LevelAccessor world, Entity entity) {
        if (entity == null) {
            return;
        }
        double Scaling = 0.0d;
        for (int index0 = 0; index0 < 500; index0++) {
            if (!world.m_8055_(new BlockPos(entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(Scaling)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123341_(), entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(Scaling)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123342_(), entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(Scaling)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123343_())).m_60815_()) {
                Scaling += 1.0d;
            }
            world.m_7106_(ParticleTypes.f_123748_, entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(Scaling)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123341_(), entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(Scaling)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123342_(), entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(Scaling)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123343_(), 0.0d, 1.0d, 0.0d);
        }
    }
}
