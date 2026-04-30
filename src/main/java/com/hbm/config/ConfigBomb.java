package com.hbm.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import static com.hbm.config.CommonConfig.addBoolean;
import static com.hbm.config.CommonConfig.addInt;

public class ConfigBomb {
    public static boolean allowNukes = true;
    public static int gadgetRadius = 150;
    public static int boyRadius = 120;
    public static int manRadius = 175;
    public static int mikeRadius = 250;
    public static int tsarRadius = 500;
    public static int prototypeRadius = 150;
    public static int fleijaRadius = 50;
    public static int soliniumRadius = 150;
    public static int n2Radius = 200;
    public static int missileRadius = 100;
    public static int mirvRadius = 100;
    public static int fatmanRadius = 35;
    public static int nukaRadius = 25;
    public static int aSchrabRadius = 20;

    // max time allowed for mk5 explosion each tick
    public static int mk5 = 50;
    public static int blastSpeed = 1024;
    public static int falloutRange = 100;
    public static int fDelay = 4;
    public static int limitExplosionLifespan = 0;
    // whether to generate new chunks
    public static boolean chunkloading = false;
    // 0 = legacy, 1 = threaded DDA, 2 = 1 = threaded DDA with damage accumulation
    public static int explosionAlgorithm = 2;

    // TODO: Implement config
    public static void addConfig(ForgeConfigSpec.Builder builder){
        builder.push(CommonConfig.CATEGORY_NUKES);
        
        addBoolean(builder, "allowNukes", true, "If false, forbid nuke explosion");
        addInt(builder, "gadgetRadius", 150, "Radius of the Gadget");
        addInt(builder, "boyRadius", 120, "Radius of Little Boy");
        addInt(builder, "manRadius", 175, "Radius of Fat Man");
        addInt(builder, "mikeRadius", 250, "Radius of Ivy Mike");
        addInt(builder, "tsarRadius", 500, "Radius of the Tsar Bomba");
        addInt(builder, "prototypeRadius", 150, "Radius of the Prototype");
        addInt(builder, "fleijaRadius", 50, "Radius of F.L.E.I.J.A.");
        addInt(builder, "missileRadius", 100, "Radius of the nuclear missile");
        addInt(builder, "mirvRadius", 100, "Radius of a MIRV");
        addInt(builder, "fatmanRadius", 35, "Radius of the Fatman Launcher");
        addInt(builder, "nukaRadius", 25, "Radius of the nuka grenade");
        addInt(builder, "aSchrabRadius", 20, "Radius of dropped anti schrabidium");
        addInt(builder, "soliniumRadius", 150, "Radius of the blue rinse");
        addInt(builder, "n2Radius", 200, "Radius of the N2 mine");
        addInt(builder, "limitExplosionLifespan", 0, "How long an explosion can be unloaded until it dies in seconds. Based of system time. 0 disables the effect");
        addInt(builder, "blastSpeed", 1024, "Base speed of MK3 system (old and schrabidium) detonations (Blocks / tick)");
        addInt(builder, "mk5BlastTime", 50, "Minimum amount of milliseconds per tick allocated for mk5 chunk processing");
        addInt(builder, "falloutRange", 100, "Radius of fallout area (base radius * value in percent)");
        addInt(builder, "falloutDelay", 4, "How many ticks to wait for the next fallout chunk computation");
        addBoolean(builder, "enableChunkLoading", false, "Allows procedural explosions to generate new chunks. Keep this disabled to avoid large nukes blocking chunk loading.");
        addInt(builder, "explosionAlgorithm", 2, 0, 2, "Configures the algorithm of mk5 explosion. \n0 = Legacy, 1 = Threaded DDA, 2 = Threaded DDA with damage accumulation.");
        
        builder.pop();
    }

    public static void loadConfig(final ModConfigEvent event){
        
    }
}
