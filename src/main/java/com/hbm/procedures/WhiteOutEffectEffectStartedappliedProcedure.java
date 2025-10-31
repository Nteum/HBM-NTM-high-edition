package net.mcreator.nuclearcraft.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/WhiteOutEffectEffectStartedappliedProcedure.class */
public class WhiteOutEffectEffectStartedappliedProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z) {
        if (world instanceof Level) {
            Level _level = (Level) world;
            if (!_level.m_5776_()) {
                _level.m_5594_((Player) null, BlockPos.m_274561_(x, y, z), (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:ear-ringingshort")), SoundSource.NEUTRAL, 1.0f, 1.0f);
            } else {
                _level.m_7785_(x, y, z, (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:ear-ringingshort")), SoundSource.NEUTRAL, 1.0f, 1.0f, false);
            }
        }
    }
}
