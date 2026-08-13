package com.hbm.item;

import com.hbm.space.dim.CelestialBody;
import net.minecraft.world.item.Item;

public class ItemVOVTdrive extends Item {


    public ItemVOVTdrive(Properties pProperties) {
        super(pProperties);
    }
    public static class Target {

        public CelestialBody body;
        public boolean inOrbit;
        public boolean isValid;

        public Target(CelestialBody body, boolean inOrbit, boolean isValid) {
            this.body = body;
            this.inOrbit = inOrbit;
            this.isValid = isValid;
        }

    }
}
