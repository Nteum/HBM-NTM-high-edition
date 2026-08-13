package com.hbm.saveddata.satellites;

import java.util.Random;

import com.hbm.space.dim.CelestialBody;
import com.hbm.space.dim.trait.CBT_War;
import com.hbm.space.dim.trait.CBT_War.Projectile;
import com.hbm.space.dim.trait.CBT_War.ProjectileType;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

/**
 * Railgun war satellite — launches projectiles at targeted celestial bodies.
 *
 * Rendering (GL11/BeamPronter/ResourceManager) is stubbed until
 * the render system is fully ported.
 */
public class SatelliteRailgun extends SatelliteWar {

    private boolean canFire   = false;
    private boolean hasTarget = false;

    public long lastOp;
    public float interp;
    public int cooldown;
    private CelestialBody target;

    public SatelliteRailgun() {
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
        fireAtTarget(target);
        canFire = hasTarget;
    }

    @Override
    public void fire() {
        if (canFire) {
            interp += 0.5f;
            interp = Math.min(100.0f, interp + 0.3f * (100.0f - interp) * 0.15f);

            if (interp >= 100) {
                interp = 0;
                canFire = false;
            }
        }
    }

    @Override
    public void setTarget(CelestialBody body) {
        if (body != null && body.dimension != null) {
            target = CelestialBody.getBody(body.dimension);
            if (target != null) {
                hasTarget = true;
            }
        }
    }

    @Override
    public void fireAtTarget(CelestialBody body) {
        if (!hasTarget) return;
        if (body == null) return;

        if (!target.hasTrait(CBT_War.class)) {
            target.modifyTraits(new CBT_War(100, 0));
        } else {
            CBT_War war = target.getTrait(CBT_War.class);
            if (war != null) {
                float rand = (new Random()).nextFloat();

                // TODO(port): be able to choose projectile types
                Projectile projectile = new Projectile(
                    100, 20, 50,
                    28 * rand * 5, 55, 20,
                    ProjectileType.SMALL,
                    // Target dimension hashCode as identifier
                    body.dimension != null ? body.dimension.location().hashCode() : 0
                );
                projectile.GUIangle = (int) (rand * 360);
                war.launchProjectile(projectile);
            }
        }
    }

    // TODO(port): rendering — playsound, render() use client-side APIs (Minecraft.getMinecraft, GL11, BeamPronter, ResourceManager)
    // public void playsound() { ... }
    // public int magSize() { return 0; }
    // public void render(float partialTicks, ClientLevel world, Minecraft mc, float solarAngle, long id) { ... }

    @Override
    public void serialize(FriendlyByteBuf buf) {
        super.serialize(buf);
        buf.writeFloat(interp);
    }

    @Override
    public void deserialize(FriendlyByteBuf buf) {
        super.deserialize(buf);
        this.interp = buf.readFloat();
    }
}
