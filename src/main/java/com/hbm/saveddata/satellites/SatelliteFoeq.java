package com.hbm.saveddata.satellites;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SatelliteFoeq extends Satellite {

    public SatelliteFoeq() {
        this.satIface = Interfaces.NONE;
    }

    @Override
    public void onOrbit(Level world, double x, double y, double z) {
        // TODO(port): MainRegistry.achFOEQ achievement not yet ported
        // for (Player p : world.players())
        //     p.awardStat(MainRegistry.achFOEQ);
    }
}
