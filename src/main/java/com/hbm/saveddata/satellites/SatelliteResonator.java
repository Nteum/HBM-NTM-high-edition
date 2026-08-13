package com.hbm.saveddata.satellites;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SatelliteResonator extends Satellite {

    public SatelliteResonator() {
        this.coordAcs.add(CoordActions.HAS_Y);
        this.satIface = Interfaces.SAT_COORD;
    }

    @Override
    public void onCoordAction(Level world, Player player, int x, int y, int z) {
        if (!(player instanceof ServerPlayer serverPlayer))
            return;

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

        player.stopRiding();
        serverPlayer.connection.teleport(x + 0.5D, y, z + 0.5D,
            player.getYRot(), player.getXRot());

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
