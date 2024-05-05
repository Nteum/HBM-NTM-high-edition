package com.hbm.item.machine;

import net.minecraft.item.Item;

public class MachineUpgrade extends Item {
    public UpgradeType type;
    public int level;
    public MachineUpgrade(Settings settings,UpgradeType type,int level) {
        super(settings);
        this.type = type;
        this.level = level;
    }

    public enum UpgradeType {
        SPEED,EFFECT,POWER,FORTUNE,AFTERBURN,OVERDRIVE,RADIUS,HEALTH,SMELTER,SHREDDER,CENTRIFUGE,NULLIFIER,SCREM,GC_SPEED,FIVE_G,STACK,EJECTOR,CRYSTALLIZER;
    }
}
