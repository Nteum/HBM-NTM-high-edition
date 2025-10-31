package net.mcreator.nuclearcraft.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/CakeBombEntityFallsProcedure.class */
public class CakeBombEntityFallsProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z) {
        if (world instanceof ServerLevel) {
            ((ServerLevel) world).m_8767_(ParticleTypes.f_123748_, x, y, z, 1000, 10.0d, 1.0d, 10.0d, 1.0d);
        }
        for (int index0 = 0; index0 < 50; index0++) {
            world.m_7731_(BlockPos.m_274561_(x + Mth.m_216263_(RandomSource.m_216327_(), -10.0d, 10.0d), y, z + Mth.m_216263_(RandomSource.m_216327_(), -10.0d, 10.0d)), Blocks.f_50145_.m_49966_(), 3);
        }
        if (world instanceof Level) {
            Level _level = (Level) world;
            if (!_level.m_5776_()) {
                _level.m_5594_((Player) null, BlockPos.m_274561_(x, y, z), (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:partyhorn")), SoundSource.NEUTRAL, 5.0f, 1.0f);
            } else {
                _level.m_7785_(x, y, z, (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:partyhorn")), SoundSource.NEUTRAL, 5.0f, 1.0f, false);
            }
        }
    }
}
