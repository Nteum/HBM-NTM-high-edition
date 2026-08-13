package com.hbm.saveddata.satellites;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class SatelliteLaser extends Satellite {

    public long lastOp;

    public SatelliteLaser() {
        this.ifaceAcs.add(InterfaceActions.HAS_MAP);
        this.ifaceAcs.add(InterfaceActions.SHOW_COORDS);
        this.ifaceAcs.add(InterfaceActions.CAN_CLICK);
        this.satIface = Interfaces.SAT_PANEL;
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        super.writeToNBT(nbt);
        nbt.putLong("lastOp", lastOp);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        super.readFromNBT(nbt);
        lastOp = nbt.getLong("lastOp");
    }

    @Override
    public void onClick(Level world, int x, int z) {
        if (lastOp + 10000 < System.currentTimeMillis()) {
            lastOp = System.currentTimeMillis();

            int y = world.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, x, z);

            // TODO(port): EntityDeathBlast not yet ported
            // EntityDeathBlast blast = new EntityDeathBlast(world);
            // blast.setPos(x, y, z);
            // world.addFreshEntity(blast);
        }
    }
}
