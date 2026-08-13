package com.hbm.space.dim;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Client-side meteor animation system.
 *
 * Meteors spawn high in the sky and fall downwards, generating smoke
 * particles along their path. They are purely decorative and have no
 * gameplay impact.
 *
 * Extracted from 1.7.10 WorldProviderCelestial.Meteor.
 */
@OnlyIn(Dist.CLIENT)
public class CelestialMeteor {

    public static final ArrayList<Meteor> meteors = new ArrayList<>();

    /** Spawn a new meteor near the current player (if in a celestial dimension). */
    public static void addMeteor() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        Level level = player.level();
        meteors.add(new Meteor(
            player.getX() + level.random.nextInt(16000) - 8000,
            2017,
            player.getZ() + level.random.nextInt(16000) - 8000
        ));
    }

    /**
     * Called each client tick from the dimension's sky update.
     * Advances all meteor positions and removes dead ones.
     */
    public static void update(Level level) {
        ListIterator<Meteor> iterator = meteors.listIterator();
        while (iterator.hasNext()) {
            Meteor meteor = iterator.next();
            Meteor fragment = meteor.update(level.random);
            if (meteor.isDead) iterator.remove();
            if (fragment != null) iterator.add(fragment);
        }
    }

    // === Meteor inner class ===

    public static class Meteor {

        public double posX, posY, posZ;
        public double prevPosX, prevPosY, prevPosZ;
        public double motionX, motionY, motionZ;
        public boolean isDead = false;
        public long age;
        public MeteorType type;

        public Meteor(double posX, double posY, double posZ) {
            this(posX, posY, posZ, MeteorType.STANDARD, -31.2, -20.8, 20);
        }

        public Meteor(double posX, double posY, double posZ,
                MeteorType type, double motionX, double motionY, double motionZ) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.type = type;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
        }

        /** Advance one tick. Returns a smoke fragment if applicable, or null. */
        private Meteor update(RandomSource rand) {
            // Standard meteors die when they reach y=500
            if (this.posY <= 500 && this.type != MeteorType.SMOKE) {
                this.isDead = true;
            }

            // Smoke particles age and die after ~60 ticks
            if (this.type == MeteorType.SMOKE) {
                this.age++;
                if (this.age >= 60) this.isDead = true;
            }

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;

            // Non-smoke, non-fragment meteors spawn smoke trails
            if (this.type != MeteorType.SMOKE && this.type != MeteorType.FRAGMENT) {
                return new Meteor(
                    (this.posX + rand.nextInt(16)) - 8,
                    (this.posY + rand.nextInt(16)),
                    (this.posZ + rand.nextInt(16)) - 8,
                    MeteorType.SMOKE, 0, 0, 0);
            }

            return null;
        }
    }

    public enum MeteorType {
        STANDARD,
        FRAGMENT,
        SMOKE
    }
}
