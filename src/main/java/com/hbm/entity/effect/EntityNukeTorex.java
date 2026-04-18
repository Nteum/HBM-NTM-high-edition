package com.hbm.entity.effect;

import com.hbm.entity.ModEntityType;
import com.hbm.registries.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

/*
 * Toroidial Convection Simulation Explosion Effect
 * Tor                             Ex
 */
public class EntityNukeTorex extends Entity {

    public static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.defineId(EntityNukeTorex.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Byte> DATA_TYPE = SynchedEntityData.defineId(EntityNukeTorex.class, EntityDataSerializers.BYTE);

    public static final int FIRST_CONDENSE_HEIGHT = 130;
    public static final int SECOND_CONDENSE_HEIGHT = 170;
    public static final int MAX_CLOUDLETS = 20_000;

    public static final double NR1 = 2.5D;
    public static final double NG1 = 1.3D;
    public static final double NB1 = 0.4D;
    public static final double NR2 = 0.1D;
    public static final double NG2 = 0.075D;
    public static final double NB2 = 0.05D;

    public static final double BR1 = 1D;
    public static final double BG1 = 2D;
    public static final double BB1 = 0.5D;
    public static final double BR2 = 0.1D;
    public static final double BG2 = 0.1D;
    public static final double BB2 = 0.1D;

    public double coreHeight = 3D;
    public double convectionHeight = 3D;
    public double torusWidth = 3D;
    public double rollerSize = 1D;
    public double heat = 1D;
    public double lastSpawnY = -1D;
    public final ArrayList<Cloudlet> cloudlets = new ArrayList<>();
    public int lastRenderSortTick = Integer.MIN_VALUE;
    public int maxAge = 1000;
    public float humidity = -1F;

    public boolean didPlaySound = false;
    public boolean didShake = false;

    public EntityNukeTorex(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noCulling = true;
        this.noPhysics = true;
        this.blocksBuilding = true;
        this.setNoGravity(true);
    }

    public EntityNukeTorex(Level level) {
        this(ModEntityType.ENTITY_NUKE_TOREX.get(), level);
    }

    public EntityNukeTorex(Level level, Vec3 pos, float scale) {
        this(level);
        this.setPos(pos);
        this.setScale(Mth.clamp(scale * 0.01F, 0.25F, 5F));
    }

    public EntityNukeTorex(Level level, Vec3 pos, float scale, boolean type) {
        this(level, pos, scale);
        if (type) {
            this.entityData.set(DATA_TYPE, (byte) 1);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            double posX = getX();
            double posY = getY();
            double posZ = getZ();
            double scale = getScale();
            double cloudScale = 1.5D;

            if (tickCount == 1) {
                this.setScale((float) scale);
            }

            if (humidity == -1F) {
                humidity = level().getBiome(blockPosition()).value().getModifiedClimateSettings().downfall();
            }

            if (lastSpawnY == -1D) {
                lastSpawnY = posY - 3D;
            }

            int spawnTarget = Math.max(level().getHeight(Heightmap.Types.WORLD_SURFACE, Mth.floor(posX), Mth.floor(posZ)) - 3, 1);
            double moveSpeed = 0.5D;

            if (Math.abs(spawnTarget - lastSpawnY) < moveSpeed) {
                lastSpawnY = spawnTarget;
            } else {
                lastSpawnY += moveSpeed * Math.signum(spawnTarget - lastSpawnY);
            }

            double range = (torusWidth - rollerSize) * 0.5D;
            double simSpeed = getSimulationSpeed();
            int lifetime = Math.min((tickCount * tickCount) + 200, maxAge - tickCount + 200);
            int toSpawn = (int) (0.6D * Math.min(Math.max(0, MAX_CLOUDLETS - cloudlets.size()),
                    Math.ceil(10D * simSpeed * simSpeed * Math.min(1D, 1200D / (double) lifetime))));

            for (int i = 0; i < toSpawn; i++) {
                double x = posX + random.nextGaussian() * range;
                double z = posZ + random.nextGaussian() * range;
                Cloudlet cloud = new Cloudlet(x, lastSpawnY, z, (float) (random.nextDouble() * Math.PI * 2D), 0, lifetime);
                float start = (float) (Math.sqrt(scale) * 3D + tickCount * 0.0025D * scale);
                float grow = (float) (Math.sqrt(scale) * 3D + tickCount * 0.0025D * 6D * cloudScale * scale);
                cloud.setScale(start, grow);
                cloudlets.add(cloud);
            }

            if (tickCount < 120D * scale) {
                level().setSkyFlashTime(2);
            }

            if (tickCount < 150) {
                int cloudCount = Math.min(tickCount * 2, 100);
                int shockLife = Math.max(400 - tickCount * 20, 50);

                for (int i = 0; i < cloudCount; i++) {
                    Vec3 vec = new Vec3((tickCount + random.nextDouble() * 2D) * 1.5D, 0D, 0D);
                    float rot = (float) (Math.PI * 2D * random.nextDouble());
                    vec = vec.yRot(rot);
                    cloudlets.add(new Cloudlet(
                            vec.x + posX,
                            level().getHeight(Heightmap.Types.WORLD_SURFACE, (int) (vec.x + posX) + 1, (int) (vec.z + posZ)),
                            vec.z + posZ,
                            rot,
                            0,
                            shockLife,
                            TorexType.SHOCK
                    ).setScale((float) scale * 5F, (float) scale * 2F).setMotion(Mth.clamp(0.25D * tickCount - 5D, 0D, 1D)));
                }

                if (!didPlaySound) {
                    tryPlayClientSound(posX, posY, posZ);
                }
            }

            if (tickCount < 200) {
                lifetime = (int) (lifetime * scale);
                for (int i = 0; i < 2; i++) {
                    Cloudlet cloud = new Cloudlet(posX, posY + coreHeight, posZ, (float) (random.nextDouble() * Math.PI * 2D), 0, lifetime, TorexType.RING);
                    float start = (float) (Math.sqrt(scale) * cloudScale + tickCount * 0.0015D * scale);
                    float grow = (float) (Math.sqrt(scale) * cloudScale + tickCount * 0.0015D * 6D * cloudScale * scale);
                    cloud.setScale(start, grow);
                    cloudlets.add(cloud);
                }
            }

            if (humidity > 0F && tickCount < 220) {
                spawnCondensationClouds(tickCount, humidity, FIRST_CONDENSE_HEIGHT, 80, 4, scale, cloudScale);
                spawnCondensationClouds(tickCount, humidity, SECOND_CONDENSE_HEIGHT, 80, 2, scale, cloudScale);
            }

            for (int i = cloudlets.size() - 1; i >= 0; i--) {
                Cloudlet cloud = cloudlets.get(i);
                if (cloud.isDead) {
                    cloudlets.remove(i);
                    continue;
                }
                cloud.update();
            }

            coreHeight += 0.15D;
            torusWidth += 0.05D;
            rollerSize = torusWidth * 0.35D;
            convectionHeight = coreHeight + rollerSize;

            int maxHeat = (int) (50D * scale * scale);
            heat = maxHeat - Math.pow((maxHeat * (double) tickCount) / maxAge, 0.6D);
        }

        if (!level().isClientSide && tickCount > maxAge) {
            discard();
        }
    }

    public void spawnCondensationClouds(int age, float humidity, int height, int count, int spreadAngle, double scale, double cloudScale) {
        if ((getY() + age) > height) {
            for (int i = 0; i < (int) (5F * humidity * count / (double) spreadAngle); i++) {
                for (int j = 1; j < spreadAngle; j++) {
                    float angle = (float) (Math.PI * 2D * random.nextDouble());
                    Vec3 vec = new Vec3(0D, age, 0D);
                    vec = vec.zRot((float) Math.acos((height - getY()) / age) + (float) Math.toRadians(humidity * humidity * 90F * j * (0.1D * random.nextDouble() - 0.05D)));
                    vec = vec.yRot(angle);
                    Cloudlet cloud = new Cloudlet(getX() + vec.x, getY() + vec.y, getZ() + vec.z, angle, 0,
                            (int) ((20 + age / 10D) * (1D + random.nextDouble() * 0.1D)), TorexType.CONDENSATION);
                    cloud.setScale(3F * (float) (cloudScale * scale), 4F * (float) (cloudScale * scale));
                    cloudlets.add(cloud);
                }
            }
        }
    }

    public EntityNukeTorex setScale(float scale) {
        if (!level().isClientSide) {
            this.entityData.set(DATA_SCALE, scale);
        }
        this.coreHeight = this.coreHeight * scale;
        this.convectionHeight = this.convectionHeight * scale;
        this.torusWidth = this.torusWidth * scale;
        this.rollerSize = this.rollerSize * scale;
        this.maxAge = (int) (45 * 20 * scale);
        return this;
    }

    public EntityNukeTorex setType(int type) {
        this.entityData.set(DATA_TYPE, (byte) type);
        return this;
    }

    public float getEntityScale() {
        return entityData.get(DATA_SCALE);
    }

    public int getEntityMaxAge() {
        return maxAge;
    }

    public double getScale() {
        return entityData.get(DATA_SCALE);
    }

    public byte getCloudType() {
        return entityData.get(DATA_TYPE);
    }

    public double getSimulationSpeed() {
        int simSlow = maxAge / 4;
        int life = tickCount;

        if (life > maxAge) {
            return 0D;
        }

        if (life > simSlow) {
            return 1D - ((double) (life - simSlow) / (double) (maxAge - simSlow));
        }

        return 1D;
    }

    public float getAlpha() {
        int fadeOut = maxAge * 3 / 4;
        int life = tickCount;

        if (life > fadeOut) {
            float factor = (float) (life - fadeOut) / (float) (maxAge - fadeOut);
            return 1F - factor;
        }

        return 1F;
    }

    @OnlyIn(Dist.CLIENT)
    private void tryPlayClientSound(double posX, double posY, double posZ) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        double soundRange = (tickCount * 1.5D + 1D) * 1.5D;
        if (player.distanceToSqr(posX, posY, posZ) < soundRange * soundRange) {
            level().playLocalSound(posX, posY, posZ, ModSounds.WEAPON_NUCLEAR_EXPLOSION.get(), SoundSource.HOSTILE, 10_000F, 1F, false);
            didPlaySound = true;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_SCALE, 1F);
        this.entityData.define(DATA_TYPE, (byte) 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("scale")) {
            setScale(compound.getFloat("scale"));
        }
        if (compound.contains("type")) {
            this.entityData.set(DATA_TYPE, compound.getByte("type"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("scale", entityData.get(DATA_SCALE));
        compound.putByte("type", entityData.get(DATA_TYPE));
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    public enum TorexType {
        STANDARD,
        RING,
        CONDENSATION,
        SHOCK
    }

    public static void statFac(Level level, double x, double y, double z, float scale) {
        EntityNukeTorex torex = new EntityNukeTorex(level).setScale(Mth.clamp(scale * 0.01F, 0.25F, 5F));
        torex.setPos(x, y, z);
        level.addFreshEntity(torex);
    }

    public static void statFacBale(Level level, double x, double y, double z, float scale) {
        EntityNukeTorex torex = new EntityNukeTorex(level).setScale(Mth.clamp(scale * 0.01F, 0.25F, 5F)).setType(1);
        torex.setPos(x, y, z);
        level.addFreshEntity(torex);
    }

    public class Cloudlet {

        public double posX;
        public double posY;
        public double posZ;
        public double prevPosX;
        public double prevPosY;
        public double prevPosZ;
        public double motionX;
        public double motionY;
        public double motionZ;
        public int age;
        public int cloudletLife;
        public float angle;
        public boolean isDead = false;
        float rangeMod = 1F;
        public float colorMod = 1F;
        public double colorR;
        public double colorG;
        public double colorB;
        public double prevColorR;
        public double prevColorG;
        public double prevColorB;
        public double renderSortDistanceSq;
        public TorexType type;
        private float startingScale = 3F;
        private float growingScale = 5F;
        private double computedMotionX;
        private double computedMotionY;
        private double computedMotionZ;

        private double motionMult = 1D;
        private double motionConvectionMult = 0.5D;
        private double motionLiftMult = 0.625D;
        private double motionRingMult = 0.5D;
        private double motionCondensationMult = 1D;
        private double motionShockwaveMult = 1D;

        public Cloudlet(double posX, double posY, double posZ, float angle, int age, int maxAge) {
            this(posX, posY, posZ, angle, age, maxAge, TorexType.STANDARD);
        }

        public Cloudlet(double posX, double posY, double posZ, float angle, int age, int maxAge, TorexType type) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.age = age;
            this.cloudletLife = maxAge;
            this.angle = angle;
            this.rangeMod = 0.3F + random.nextFloat() * 0.7F;
            this.colorMod = 0.8F + random.nextFloat() * 0.2F;
            this.type = type;
            this.updateColor();
        }

        private void update() {
            age++;

            if (age > cloudletLife) {
                this.isDead = true;
            }

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            double simDeltaX = EntityNukeTorex.this.getX() - this.posX;
            double simDeltaZ = EntityNukeTorex.this.getZ() - this.posZ;
            double simPosX = EntityNukeTorex.this.getX() + Math.sqrt(simDeltaX * simDeltaX + simDeltaZ * simDeltaZ);

            if (this.type == TorexType.STANDARD) {
                getConvectionMotion(simPosX);
                double convectionX = this.computedMotionX;
                double convectionY = this.computedMotionY;
                double convectionZ = this.computedMotionZ;
                getLiftMotion(simPosX);

                double factor = Mth.clamp((this.posY - EntityNukeTorex.this.getY()) / EntityNukeTorex.this.coreHeight, 0D, 1D);
                double inverseFactor = 1D - factor;
                this.motionX = convectionX * factor + this.computedMotionX * inverseFactor;
                this.motionY = convectionY * factor + this.computedMotionY * inverseFactor;
                this.motionZ = convectionZ * factor + this.computedMotionZ * inverseFactor;
            } else if (this.type == TorexType.RING) {
                getRingMotion(simPosX);
                this.motionX = this.computedMotionX;
                this.motionY = this.computedMotionY;
                this.motionZ = this.computedMotionZ;
            } else if (this.type == TorexType.CONDENSATION) {
                getCondensationMotion();
                this.motionX = this.computedMotionX;
                this.motionY = this.computedMotionY;
                this.motionZ = this.computedMotionZ;
            } else if (this.type == TorexType.SHOCK) {
                getShockwaveMotion();
                this.motionX = this.computedMotionX;
                this.motionY = this.computedMotionY;
                this.motionZ = this.computedMotionZ;
            }

            double mult = this.motionMult * getSimulationSpeed();

            this.posX += this.motionX * mult;
            this.posY += this.motionY * mult;
            this.posZ += this.motionZ * mult;

            this.updateColor();
        }

        private void getCondensationMotion() {
            double speed = motionCondensationMult * EntityNukeTorex.this.getScale() * 0.125D;
            setNormalizedMotion(this.posX - EntityNukeTorex.this.getX(), 0D, this.posZ - EntityNukeTorex.this.getZ(), speed);
        }

        private void getShockwaveMotion() {
            double speed = motionShockwaveMult * EntityNukeTorex.this.getScale() * 0.25D;
            setNormalizedMotion(this.posX - EntityNukeTorex.this.getX(), 0D, this.posZ - EntityNukeTorex.this.getZ(), speed);
        }

        private void getRingMotion(double simPosX) {
            if (simPosX > EntityNukeTorex.this.getX() + torusWidth * 2D) {
                setComputedMotion(0D, 0D, 0D);
                return;
            }

            double torusPosX = EntityNukeTorex.this.getX() + torusWidth;
            double torusPosY = EntityNukeTorex.this.getY() + coreHeight * 0.5D;

            double deltaX = torusPosX - simPosX;
            double deltaY = torusPosY - this.posY;

            double roller = EntityNukeTorex.this.rollerSize * this.rangeMod * 0.25D;
            double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY) / roller - 1D;

            double func = 1D - Math.exp(-dist);
            float angle = (float) (func * Math.PI * 0.5D);

            double rotX = -deltaX / dist;
            double rotY = -deltaY / dist;
            float sin = Mth.sin(angle);
            float cos = Mth.cos(angle);
            double rotatedX = rotX * cos + rotY * sin;
            double rotatedY = rotY * cos - rotX * sin;

            setNormalizedMotion(torusPosX + rotatedX - simPosX, torusPosY + rotatedY - this.posY, 0D, motionRingMult * 0.5D);
            rotateComputedMotionAroundY();
        }

        private void getConvectionMotion(double simPosX) {
            if (simPosX > EntityNukeTorex.this.getX() + torusWidth * 2D) {
                setComputedMotion(0D, 0D, 0D);
                return;
            }

            double torusPosX = EntityNukeTorex.this.getX() + torusWidth;
            double torusPosY = EntityNukeTorex.this.getY() + coreHeight;

            double deltaX = torusPosX - simPosX;
            double deltaY = torusPosY - this.posY;

            double roller = EntityNukeTorex.this.rollerSize * this.rangeMod;
            double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY) / roller - 1D;

            double func = 1D - Math.exp(-dist);
            float angle = (float) (func * Math.PI * 0.5D);

            double rotX = -deltaX / dist;
            double rotY = -deltaY / dist;
            float sin = Mth.sin(angle);
            float cos = Mth.cos(angle);
            double rotatedX = rotX * cos + rotY * sin;
            double rotatedY = rotY * cos - rotX * sin;

            setNormalizedMotion(torusPosX + rotatedX - simPosX, torusPosY + rotatedY - this.posY, 0D, motionConvectionMult);
            rotateComputedMotionAroundY();
        }

        private void getLiftMotion(double simPosX) {
            double scale = Mth.clamp(1D - (simPosX - (EntityNukeTorex.this.getX() + torusWidth)), 0D, 1D) * motionLiftMult;

            setNormalizedMotion(
                    EntityNukeTorex.this.getX() - this.posX,
                    (EntityNukeTorex.this.getY() + convectionHeight) - this.posY,
                    EntityNukeTorex.this.getZ() - this.posZ,
                    scale
            );
        }

        private void setComputedMotion(double x, double y, double z) {
            this.computedMotionX = x;
            this.computedMotionY = y;
            this.computedMotionZ = z;
        }

        private void setNormalizedMotion(double x, double y, double z, double speed) {
            double lengthSq = x * x + y * y + z * z;
            if (lengthSq < 1.0E-8D) {
                setComputedMotion(0D, 0D, 0D);
                return;
            }

            double scale = speed / Math.sqrt(lengthSq);
            setComputedMotion(x * scale, y * scale, z * scale);
        }

        private void rotateComputedMotionAroundY() {
            float cos = Mth.cos(this.angle);
            float sin = Mth.sin(this.angle);
            double motionX = this.computedMotionX;
            double motionZ = this.computedMotionZ;
            this.computedMotionX = motionX * cos + motionZ * sin;
            this.computedMotionZ = motionZ * cos - motionX * sin;
        }

        private void updateColor() {
            this.prevColorR = this.colorR;
            this.prevColorG = this.colorG;
            this.prevColorB = this.colorB;

            double exX = EntityNukeTorex.this.getX();
            double exY = EntityNukeTorex.this.getY() + EntityNukeTorex.this.coreHeight;
            double exZ = EntityNukeTorex.this.getZ();

            double distX = exX - posX;
            double distY = exY - posY;
            double distZ = exZ - posZ;

            double distSq = distX * distX + distY * distY + distZ * distZ;
            distSq /= this.type == TorexType.SHOCK ? EntityNukeTorex.this.heat * 3D : EntityNukeTorex.this.heat;

            double col = 2D / Math.max(distSq, 1D);

            byte type = EntityNukeTorex.this.getCloudType();
            if (type == 0) {
                this.colorR = NR2 + (NR1 - NR2) * col;
                this.colorG = NG2 + (NG1 - NG2) * col;
                this.colorB = NB2 + (NB1 - NB2) * col;
                return;
            }

            this.colorR = BR2 + (BR1 - BR2) * col;
            this.colorG = BG2 + (BG1 - BG2) * col;
            this.colorB = BB2 + (BB1 - BB2) * col;
        }

        public Vec3 getInterpPos(float interp) {
            return new Vec3(
                    prevPosX + (posX - prevPosX) * interp,
                    prevPosY + (posY - prevPosY) * interp,
                    prevPosZ + (posZ - prevPosZ) * interp
            );
        }

        public Vec3 getInterpColor(float interp) {
            if (this.type == TorexType.CONDENSATION) {
                return new Vec3(1D, 1D, 1D);
            }

            double greying = this.type == TorexType.RING ? 0.05D : 0D;
            return new Vec3(
                    (prevColorR + (colorR - prevColorR) * interp) + greying,
                    (prevColorG + (colorG - prevColorG) * interp) + greying,
                    (prevColorB + (colorB - prevColorB) * interp) + greying
            );
        }

        public float getAlpha() {
            float alpha = (1F - ((float) age / (float) cloudletLife)) * EntityNukeTorex.this.getAlpha();
            if (this.type == TorexType.CONDENSATION) {
                alpha *= 0.25F;
            }
            return Mth.clamp(alpha, 0.0001F, 1F);
        }

        public float getScale() {
            return startingScale + ((float) age / (float) cloudletLife) * growingScale;
        }

        public float getStartingScale() {
            return this.startingScale;
        }

        public float getGrowingScale() {
            return this.growingScale;
        }

        public Cloudlet setScale(float start, float grow) {
            this.startingScale = start;
            this.growingScale = grow;
            return this;
        }

        public Cloudlet setMotion(double mult) {
            this.motionMult = mult;
            return this;
        }
    }
}
