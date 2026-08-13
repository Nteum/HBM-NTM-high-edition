package com.hbm.space.dim.trait;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.hbm.space.dim.CelestialBody;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * War trait — both planets engaged in interplanetary warfare share this trait.
 * Tracks health, shields, and active projectiles between warring bodies.
 * Effects are all client-side; these variables dictate health, shield state, etc.
 */
public class CBT_War extends CelestialBodyTrait {

    public int health;
    public int shield;
    public List<Projectile> projectiles;

    public CBT_War() {
        this.health = 100;
        this.shield = 0;
        this.projectiles = new ArrayList<>();
    }

    public CBT_War(int health, int shield) {
        this.health = health;
        this.shield = shield;
        this.projectiles = new ArrayList<>();
    }

    public void launchProjectile(Projectile proj) {
        projectiles.add(proj);
    }

    public void launchProjectile(float traveltime, int size, int damage, double x, double y, double z, ProjectileType type, int target) {
        Projectile projectile = new Projectile(traveltime, size, damage, x, y, z, type, target);
        projectiles.add(projectile);
    }

    public void split(int amount, Projectile projectile, ProjectileType type) {
        if (projectile.getTravel() <= 0) {
            for (int j = 0; j < amount; j++) {
                Random rand = new Random();
                float randX = rand.nextFloat() * 160 - 80;
                float randY = rand.nextFloat() * 90 - 80;

                this.launchProjectile(
                    Math.abs(20 + j * 10),
                    projectile.getSize(),
                    projectile.getDamage(),
                    projectile.getTranslateX(),
                    projectile.getTranslateY() - randY * j,
                    projectile.getTranslateZ() + randX * j,
                    type,
                    projectile.getTarget()
                );
                projectile.GUIangle = projectile.GUIangle;
            }
            this.destroyProjectile(projectile);
        }
    }

    @Override
    public void update(boolean isRemote, CelestialBody body) {
        if (!isRemote) {
            for (int i = 0; i < this.getProjectiles().size(); i++) {
                Projectile projectile = this.getProjectiles().get(i);
                projectile.update();

                if (projectile.getTravel() <= 0) {
                    projectile.impact();
                }

                if (projectile.getAnimtime() >= 100) {
                    this.destroyProjectile(projectile);
                    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                    if (server != null) {
                        ServerLevel targetWorld = server.getLevel(
                            com.hbm.registries.HBMDimensions.LEVELS.stream()
                                .filter(k -> k.location().hashCode() == projectile.getTarget())
                                .findFirst().orElse(Level.OVERWORLD)
                        );
                        i--;

                        if (this.health > 0) {
                            CelestialBody.damage(projectile.getDamage(), targetWorld);
                        } else if (this.health <= 0) {
                            CelestialBody target = CelestialBody.getPlanet(targetWorld);
                            target.modifyTraits(new CBT_Destroyed());
                            this.health = 0;
                        }
                    }
                }

                if (projectile.getType() == ProjectileType.SPLITSHOT) {
                    if (projectile.getTravel() <= 0) {
                        this.split(4, projectile, ProjectileType.SMALL);
                        this.destroyProjectile(projectile);
                        i--;
                    }
                }
            }
        }
    }

    public void destroyProjectile(Projectile proj) {
        projectiles.remove(proj);
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        nbt.putInt("health", health);
        nbt.putInt("shield", shield);

        CompoundTag projectilesTag = new CompoundTag();
        for (int i = 0; i < projectiles.size(); i++) {
            CompoundTag projTag = new CompoundTag();
            projectiles.get(i).writeToNBT(projTag);
            projectilesTag.put("projectile" + i, projTag);
        }
        nbt.put("projectiles", projectilesTag);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        shield = nbt.getInt("shield");
        health = nbt.getInt("health");
        CompoundTag projectilesTag = nbt.getCompound("projectiles");
        projectiles = new ArrayList<>();
        for (int i = 0; projectilesTag.contains("projectile" + i); i++) {
            CompoundTag projTag = projectilesTag.getCompound("projectile" + i);
            Projectile projectile = new Projectile();
            projectile.readFromNBT(projTag);
            projectiles.add(projectile);
        }
    }

    @Override
    public void writeToBytes(FriendlyByteBuf buf) {
        buf.writeInt(health);
        buf.writeInt(shield);
        buf.writeInt(projectiles.size());
        for (Projectile projectile : projectiles) {
            projectile.writeToBytes(buf);
        }
    }

    @Override
    public void readFromBytes(FriendlyByteBuf buf) {
        shield = buf.readInt();
        health = buf.readInt();
        int numProjectiles = buf.readInt();
        projectiles = new ArrayList<>(numProjectiles);
        for (int i = 0; i < numProjectiles; i++) {
            Projectile projectile = new Projectile();
            projectile.readFromBytes(buf);
            projectiles.add(projectile);
        }
    }

    // === Inner Classes ===

    public enum ProjectileType {
        SMALL,
        MEDIUM,
        HUGE,
        INCENDIARY,
        NUCLEAR,
        SPLITSHOT
    }

    public static class Projectile {

        private float traveltime;
        private int size;
        private int damage;
        private int animtime;
        private float flashtime;
        private double translateX;
        private double translateY;
        private double translateZ;
        private ProjectileType type;
        public int GUIangle;
        private int target;

        public Projectile() {
            this.animtime = 0;
            this.flashtime = 0;
            this.type = ProjectileType.MEDIUM;
        }

        public Projectile(float traveltime, int size, int damage, double posX, double posY, double posZ, ProjectileType type, int target) {
            this.traveltime = traveltime;
            this.size = size;
            this.damage = damage;
            this.animtime = 0;
            this.flashtime = 0;
            this.translateX = posX;
            this.translateY = posY;
            this.translateZ = posZ;
            this.type = type;
            this.target = target;
        }

        public void update() {
            if (traveltime > 0) {
                traveltime--;
            } else {
                traveltime = 0;
                if (this.getType() != ProjectileType.SPLITSHOT) {
                    animtime = Math.min(100, animtime + 1);
                }
            }
        }

        public void impact() {
            flashtime += 0.6f;
            flashtime = Math.min(100.0f, flashtime + 0.1f * (100.0f - flashtime) * 0.15f);

            if (flashtime >= 100) {
                flashtime = 100;
            }
            if (animtime == 0) {
                flashtime = 0;
            }
        }

        public void writeToNBT(CompoundTag nbt) {
            nbt.putInt("damage", damage);
            nbt.putFloat("traveltime", traveltime);
            nbt.putInt("size", size);
            nbt.putDouble("translateX", translateX);
            nbt.putDouble("translateY", translateY);
            nbt.putDouble("translateZ", translateZ);
            nbt.putInt("animtime", animtime);
            nbt.putString("type", type.name());
            nbt.putInt("target", target);
        }

        public void readFromNBT(CompoundTag nbt) {
            damage = nbt.getInt("damage");
            traveltime = nbt.getFloat("traveltime");
            size = nbt.getInt("size");
            translateX = nbt.getDouble("translateX");
            translateY = nbt.getDouble("translateY");
            translateZ = nbt.getDouble("translateZ");
            animtime = nbt.getInt("animtime");
            type = ProjectileType.valueOf(nbt.getString("type"));
            target = nbt.getInt("target");
        }

        public void writeToBytes(FriendlyByteBuf buf) {
            buf.writeInt(damage);
            buf.writeFloat(traveltime);
            buf.writeInt(size);
            buf.writeDouble(translateX);
            buf.writeDouble(translateY);
            buf.writeDouble(translateZ);
            buf.writeInt(animtime);
            buf.writeByte(type.ordinal());
            buf.writeInt(GUIangle);
            buf.writeInt(target);
        }

        public void readFromBytes(FriendlyByteBuf buf) {
            damage = buf.readInt();
            traveltime = buf.readFloat();
            size = buf.readInt();
            translateX = buf.readDouble();
            translateY = buf.readDouble();
            translateZ = buf.readDouble();
            animtime = buf.readInt();
            type = ProjectileType.values()[buf.readByte()];
            GUIangle = buf.readInt();
            target = buf.readInt();
        }

        // Getters / setters
        public ProjectileType getType() { return type; }
        public void setType(ProjectileType type) { this.type = type; }
        public float getFlashtime() { return flashtime; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public int getDamage() { return damage; }
        public float getTravel() { return traveltime; }
        public void setTravel(float travel) { this.traveltime = travel; }
        public void setDamage(int damage) { this.damage = damage; }
        public int getAnimtime() { return animtime; }
        public void setAnimtime(int animtime) { this.animtime = animtime; }
        public void setFlashtime(float flashtime) { this.flashtime = flashtime; }
        public double getTranslateX() { return translateX; }
        public int getTarget() { return target; }
        public double getTranslateY() { return translateY; }
        public double getTranslateZ() { return translateZ; }
    }
}
