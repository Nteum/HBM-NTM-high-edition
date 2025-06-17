package com.hbm.config;

public class BombConfig {
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
    public static boolean chunkloading = true;
    // 0 = legacy, 1 = threaded DDA, 2 = 1 = threaded DDA with damage accumulation
    public static int explosionAlgorithm = 2;

    // TODO: Implement config
}
