package com.hbm.entity.effect;

import com.hbm.entity.ModEntityType;
import com.hbm.particle.type.HBMSmokeParticle;
import com.hbm.particle.ModParticleTypes;
import com.hbm.utils.BobMth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.ArrayList;

/**
 * 生成爆炸之后的蘑菇云
 * */
public class EntityNukeTorex extends Entity {
    public static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.defineId(EntityNukeTorex.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> DATA_TYPE = SynchedEntityData.defineId(EntityNukeTorex.class, EntityDataSerializers.INT);
    public double coreHeight = 3;
    public double convectionHeight = 3;
    public double torusWidth = 3;
    public double rollerSize = 1;
    public double heat = 1;
    public double lastSpawnY = - 1;
    public ArrayList<Cloudlet> cloudlets = new ArrayList<>();
    //public static int cloudletLife = 200;

    public boolean didPlaySound = false;
    public boolean didShake = false;
    public EntityNukeTorex(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public EntityNukeTorex(Level pLevel) {
        super(ModEntityType.ENTITY_NUKE_TOREX.get(), pLevel);
    }
    public EntityNukeTorex(Level pLevel, Vec3 pos, float scale) {
        this(pLevel);
        this.setPos(pos);
        this.setScale(Mth.clamp((float) BobMth.squirt(scale * 0.01) * 1.5F, 0.5F, 5F));
        //以下部分有待研究
//        torex.forceSpawn = true;
//        world.spawnEntityInWorld(torex);
//        TrackerUtil.setTrackingRange(world, torex, 1000);
    }
    public EntityNukeTorex(Level pLevel, Vec3 pos, float scale, boolean type) {
        this(pLevel,pos,scale);
        if (type)
            this.entityData.set(DATA_TYPE,1);
        //以下部分有待研究
//        torex.forceSpawn = true;
//        world.spawnEntityInWorld(torex);
//        TrackerUtil.setTrackingRange(world, torex, 1000);
    }

    @Override
    public void tick() {
        super.tick();
        double posX = position().x;
        double posY = position().y;
        double posZ = position().z;

        double s = 1.5; //this.getScale();
        double cs = 1.5;
        int maxAge = this.getMaxAge();
        if (level().isClientSide){
            if(tickCount == 1) this.setScale((float) s);

            if(lastSpawnY == -1) {
                lastSpawnY = position().y - 3;
            }
            /** 获取当前地表高度 */
            int spawnTarget = Math.max(level().getHeight(Heightmap.Types.WORLD_SURFACE,(int) Math.floor(position().x), (int) Math.floor(position().z)) - 3, 1);
            double moveSpeed = 0.5D;

            if(Math.abs(spawnTarget - lastSpawnY) < moveSpeed) {
                lastSpawnY = spawnTarget;
            } else {
                lastSpawnY += moveSpeed * Math.signum(spawnTarget - lastSpawnY);
            }
            // 生成蘑菇云
            double range = (torusWidth - rollerSize) * 0.25;
            double simSpeed = getSimulationSpeed();
            int toSpawn = (int) Math.ceil(10 * simSpeed * simSpeed);
            int lifetime = Math.min((tickCount * tickCount) + 200, maxAge - tickCount + 200);

            for(int i = 0; i < toSpawn; i++) {
                double x = posX + random.nextGaussian() * range;
                double z = posZ + random.nextGaussian() * range;
                Cloudlet cloud = new Cloudlet(x, lastSpawnY, z, (float)(random.nextDouble() * 2D * Math.PI), 0, lifetime);
                cloud.setScale(1F + this.tickCount * 0.005F * (float) cs, 5F * (float) cs);
                cloudlets.add(cloud);
            }

            // spawn shock clouds
            if(tickCount < 150) {
                int cloudCount = tickCount * 5;
                int shockLife = Math.max(300 - tickCount * 20, 50);

                for(int i = 0; i < cloudCount; i++) {
                    Vec3 vec = new Vec3((tickCount * 1.5 + random.nextDouble()) * 1.5, 0, 0);
                    float rot = (float) (Math.PI * 2 * random.nextDouble());
                    vec = vec.yRot(rot);
                    this.cloudlets.add(new Cloudlet(vec.x + posX, level().getHeight(Heightmap.Types.WORLD_SURFACE,(int) (vec.x + posX) + 1, (int) (vec.z + posZ)), vec.z + posZ, rot, 0, shockLife, TorexType.SHOCK)
                            .setScale(7F, 2F)
                            .setMotion(tickCount > 15 ? 0.75 : 0));
                }

//                if(!didPlaySound) {
//                    if(MainRegistry.proxy.me() != null && MainRegistry.proxy.me().getDistanceToEntity(this) < (tickCount * 1.5 + 1) * 1.5) {
//                        MainRegistry.proxy.playSoundClient(posX, posY, posZ, "hbm:weapon.nuclearExplosion", 10_000F, 1F);
//                        didPlaySound = true;
//                    }
//                }
            }

            // spawn ring clouds
            if(tickCount < 130 * s) {
                lifetime *= s;
                for(int i = 0; i < 2; i++) {
                    Cloudlet cloud = new Cloudlet(posX, posY + coreHeight, posZ, (float)(random.nextDouble() * 2D * Math.PI), 0, lifetime, TorexType.RING);
                    cloud.setScale(1F + this.tickCount * 0.0025F * (float) (cs * cs), 3F * (float) (cs * cs));
                    cloudlets.add(cloud);
                }
            }

            // spawn condensation clouds 生成冷凝云
            //这两个除了生成高度差30格外似乎没有差别
            if(tickCount > 130 * s && tickCount < 600 * s) {
                for(int i = 0; i < 20; i++) {
                    for(int j = 0; j < 4; j++) {
                        float angle = (float) (Math.PI * 2 * random.nextDouble());
                        Vec3 vec = new Vec3(torusWidth + rollerSize * (5 + random.nextDouble()), 0, 0);
                        vec = vec.zRot((float) (Math.PI / 45 * j)).yRot(angle);
//                        vec = vec.yRot(angle);
                        Cloudlet cloud = new Cloudlet(posX + vec.x, posY + coreHeight - 5 + j * s, posZ + vec.z, angle, 0, (int) ((20 + tickCount / 10) * (1 + random.nextDouble() * 0.1)), TorexType.CONDENSATION);
                        cloud.setScale(0.125F * (float) (cs), 3F * (float) (cs));
                        cloudlets.add(cloud);
                    }
                }
            }
            if(tickCount > 200 * s && tickCount < 600 * s) {
                for(int i = 0; i < 20; i++) {
                    for(int j = 0; j < 4; j++) {
                        float angle = (float) (Math.PI * 2 * random.nextDouble());
                        Vec3 vec = new Vec3(torusWidth + rollerSize * (3 + random.nextDouble() * 0.5), 0, 0);
                        vec = vec.zRot((float) (Math.PI / 45 * j)).yRot(angle);
//                        vec = vec.yRot(angle);
                        Cloudlet cloud = new Cloudlet(posX + vec.x, posY + coreHeight + 25 + j * cs, posZ + vec.z, angle, 0, (int) ((20 + tickCount / 10) * (1 + random.nextDouble() * 0.1)), TorexType.CONDENSATION);
                        cloud.setScale(0.125F * (float) (cs), 3F * (float) (cs));
                        cloudlets.add(cloud);
                    }
                }
            }

            for(Cloudlet cloud : cloudlets) {
                cloud.update();
            }
            coreHeight += 0.15 / s;
            torusWidth += 0.05 / s;
            rollerSize = torusWidth * 0.35;
            convectionHeight = coreHeight + rollerSize;

            int maxHeat = (int) (50 * cs);
            heat = maxHeat - Math.pow((double) (maxHeat * this.tickCount) / maxAge, 1);

            cloudlets.removeIf(x -> x.isDead);
        }

        if(!level().isClientSide && this.tickCount > maxAge) {
            this.discard();
        }
    }
    private float getScale(){
        return this.entityData.get(DATA_SCALE);
    }
    private EntityNukeTorex setScale(float scale){
        if (!level().isClientSide) this.entityData.set(DATA_SCALE, scale);
        this.coreHeight = this.coreHeight / 1.5D * scale;
        this.convectionHeight = this.convectionHeight / 1.5D * scale;
        this.torusWidth = this.torusWidth / 1.5D * scale;
        this.rollerSize = this.rollerSize / 1.5D * scale;
        return this;
    }
    public double getGreying() {
        int lifetime = getMaxAge();
        int greying = lifetime * 3 / 4;

        if(tickCount > greying) {
            return 1 + ((double)(tickCount - greying) / (double)(lifetime - greying));
        }

        return 1D;
    }

    public float getAlpha() {

        int lifetime = getMaxAge();
        int fadeOut = lifetime * 3 / 4;
        int life = EntityNukeTorex.this.tickCount;

        if(life > fadeOut) {
            float fac = (float)(life - fadeOut) / (float)(lifetime - fadeOut);
            return 1F - fac;
        }

        return 1.0F;
    }
    private int getMaxAge(){
        double s = this.getScale();
        return (int) (45 * 20 * s);
    }
    /** 模拟蘑菇云的速度。simSlow之前是原速，simSlow到simStop逐渐减到0 */
    public double getSimulationSpeed() {
        int lifetime = getMaxAge();
        int simSlow = lifetime / 4;
        int simStop = lifetime / 2;
        int life = EntityNukeTorex.this.tickCount;

        if(life > simStop) {
            return 0D;
        }

        if(life > simSlow) {
            return 1D - ((double)(life - simSlow) / (double)(simStop - simSlow));
        }

        return 1.0D;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_SCALE,1.0F);
        this.entityData.define(DATA_TYPE,0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        setScale(pCompound.getFloat("scale"));
        this.entityData.set(DATA_TYPE,pCompound.getInt("type"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putFloat("scale",getScale());
        pCompound.putInt("type",this.entityData.get(DATA_TYPE));
    }
    /** 蘑菇云的类型 */
    public static enum TorexType {
        STANDARD,
        SHOCK,
        RING,
        CONDENSATION
    }
    /**
     * 这个类表示云的一部分
     * */
    public class Cloudlet{
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
        float rangeMod = 1.0F;
        public float colorMod = 1.0F;
        public Vec3 color;
        public Vec3 prevColor;
        public TorexType type;

//        Method methodMakeParticle;
//        Random rand;

        public Cloudlet(double posX, double posY, double posZ, float angle, int age, int maxAge) {
            this(posX, posY, posZ, angle, age, maxAge, TorexType.STANDARD);
        }

        public Cloudlet(double posX, double posY, double posZ, float angle, int age, int maxAge, TorexType type) {
//            rand = new Random();
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
//            try {
//                methodMakeParticle = ParticleEngine.class.getDeclaredMethod("makeParticle", ParticleOptions.class, double.class, double.class, double.class, double.class, double.class, double.class);
//                methodMakeParticle.setAccessible(true);
//            }catch (Exception e){}

        }

        private void update() {
            age++;

            if(age > cloudletLife) {
                this.isDead = true;
            }

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            Vec3 simPos = new Vec3(EntityNukeTorex.this.position().x - this.posX, 0, EntityNukeTorex.this.position().z - this.posZ);
            double simPosX = EntityNukeTorex.this.position().x + simPos.length();
            double simPosZ = EntityNukeTorex.this.position().z + 0D;

            if(this.type == TorexType.STANDARD) {
                Vec3 convection = getConvectionMotion(simPosX, simPosZ);
                Vec3 lift = getLiftMotion(simPosX, simPosZ);

                double factor = Mth.clamp((this.posY - EntityNukeTorex.this.position().x) / EntityNukeTorex.this.coreHeight, 0, 1);
                this.motionX = convection.x * factor + lift.x * (1D - factor);
                this.motionY = convection.y * factor + lift.y * (1D - factor);
                this.motionZ = convection.z * factor + lift.z * (1D - factor);
            } else if(this.type == TorexType.SHOCK) {
                double factor = Mth.clamp((this.posY - EntityNukeTorex.this.position().y) / EntityNukeTorex.this.coreHeight, 0, 1);
                Vec3 motion = new Vec3(1, 0, 0).yRot(this.angle);
//                motion = motion.yRot(this.angle);
                this.motionX = motion.x * factor;
                this.motionY = motion.y * factor;
                this.motionZ = motion.z * factor;
            } else if(this.type == TorexType.RING) {
                Vec3 motion = getRingMotion(simPosX, simPosZ);
                this.motionX = motion.x;
                this.motionY = motion.y;
                this.motionZ = motion.z;
            } else if(this.type == TorexType.CONDENSATION) {
                Vec3 motion = getCondensationMotion();
                this.motionX = motion.x;
                this.motionY = motion.y;
                this.motionZ = motion.z;
            }

            double mult = this.motionMult * getSimulationSpeed();

            this.posX += this.motionX * mult;
            this.posY += this.motionY * mult;
            this.posZ += this.motionZ * mult;

            this.updateColor();
            //这段是我加的，原版bob使用自己的render以便利地控制粒子颜色和深浅，绕过原版的粒子渲染，但我直接复刻代码的尝试失败了。
            //因此我的解决方法是使用原版粒子机制，但通过修改权限来中途设置粒子的颜色、大小、透明度。
            try {
                ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
                HBMSmokeParticle particle = (HBMSmokeParticle)particleEngine.makeParticle(ModParticleTypes.HBM_SMOKE.get(),posX,posY,posZ,motionX,motionY,motionZ);
                float brightness = type == TorexType.CONDENSATION ? 0.9F : 0.75F * colorMod;
                Vec3 vecColor = getInterpColor(0.5F).scale(brightness);
                //这个步骤是必须的，似乎来源于低版本和高版本的差别，高版本输入的颜色值大于1.0会溢出，可能会重新从0开始计值，导致原版的黄色变成红色、蓝色等。
                //因此使用clamp函数防止溢出
                vecColor = new Vec3(Mth.clamp(vecColor.x,0.0,1.0),Mth.clamp(vecColor.y,0.0,1.0),Mth.clamp(vecColor.z,0.0,1.0));
                assert particle != null;
                particle.setColor((float) vecColor.x, (float) vecColor.y, (float) vecColor.z);
                particle.scale(getScale());
                particle.setAlpha(getAlpha());
                particleEngine.add(particle);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        /** 冷凝云的运动（向周围轻微扩散） */
        private Vec3 getCondensationMotion() {
            Vec3 delta = new Vec3(posX - EntityNukeTorex.this.position().x, 0, posZ - EntityNukeTorex.this.position().z);
            double speed = 0.00002 * EntityNukeTorex.this.tickCount;
            delta = new Vec3(delta.x * speed, delta.y, delta.z * speed);
            return delta;
        }
        /** 环状云的运动 */
        private Vec3 getRingMotion(double simPosX, double simPosZ) {

            if(simPosX > EntityNukeTorex.this.position().x + torusWidth * 2)
                return new Vec3(0, 0, 0);

            /* the position of the torus' outer ring center */
            Vec3 torusPos = new Vec3(
                    (EntityNukeTorex.this.position().x + torusWidth),
                    (EntityNukeTorex.this.position().y + coreHeight * 0.5),
                    EntityNukeTorex.this.position().z);

            /* the difference between the cloudlet and the torus' ring center */
            Vec3 delta = new Vec3(torusPos.x - simPosX, torusPos.y - this.posY, torusPos.z - simPosZ);

            /* the distance this cloudlet wants to achieve to the torus' ring center */
            double roller = EntityNukeTorex.this.rollerSize * this.rangeMod * 0.25;
            /* the distance between this cloudlet and the torus' outer ring perimeter */
            double dist = delta.length() / roller - 1D;

            /* euler function based on how far the cloudlet is away from the perimeter */
            double func = 1D - Math.pow(Math.E, -dist); // [0;1]
            /* just an approximation, but it's good enough */
            float angle = (float) (func * Math.PI * 0.5D); // [0;90°]

            /* vector going from the ring center in the direction of the cloudlet, stopping at the perimeter */
            Vec3 rot = new Vec3(-delta.x / dist, -delta.y / dist, -delta.z / dist);
            /* rotate by the approximate angle */
            rot = rot.zRot(angle);

            /* the direction from the cloudlet to the target position on the perimeter */
            Vec3 motion = new Vec3(
                    torusPos.x + rot.x - simPosX,
                    torusPos.y + rot.y - this.posY,
                    torusPos.z + rot.z - simPosZ);

            double speed = 0.001D;
            motion = motion.scale(speed).normalize().yRot(this.angle);

            return motion;
        }

        /* simulated on a 2D-plane along the X/Y axis */
        private Vec3 getConvectionMotion(double simPosX, double simPosZ) {

            /* the position of the torus' outer ring center */
            Vec3 torusPos = new Vec3(
                    (EntityNukeTorex.this.position().x + torusWidth),
                    (EntityNukeTorex.this.position().y + coreHeight),
                    EntityNukeTorex.this.position().z);

            /* the difference between the cloudlet and the torus' ring center */
            Vec3 delta = new Vec3(torusPos.x - simPosX, torusPos.y - this.posY, torusPos.z - simPosZ);

            /* the distance this cloudlet wants to achieve to the torus' ring center */
            double roller = EntityNukeTorex.this.rollerSize * this.rangeMod;
            /* the distance between this cloudlet and the torus' outer ring perimeter */
            double dist = delta.length() / roller - 1D;

            /* euler function based on how far the cloudlet is away from the perimeter */
            double func = 1D - Math.pow(Math.E, -dist); // [0;1]
            /* just an approximation, but it's good enough */
            float angle = (float) (func * Math.PI * 0.5D); // [0;90°]

            /* vector going from the ring center in the direction of the cloudlet, stopping at the perimeter */
            Vec3 rot = new Vec3(-delta.x / dist, -delta.y / dist, -delta.z / dist);
            /* rotate by the approximate angle */
            rot = rot.zRot(angle);

            /* the direction from the cloudlet to the target position on the perimeter */
            Vec3 motion = new Vec3(
                    torusPos.x + rot.x - simPosX,
                    torusPos.y + rot.y - this.posY,
                    torusPos.z + rot.z - simPosZ);

            motion = motion.normalize();
            motion = motion.yRot(this.angle);

            return motion;
        }

        private Vec3 getLiftMotion(double simPosX, double simPosZ) {
            double scale = Mth.clamp(1D - (simPosX - (EntityNukeTorex.this.position().x + torusWidth)), 0, 1);

            Vec3 motion = new Vec3(EntityNukeTorex.this.position().x - this.posX, (EntityNukeTorex.this.position().y + convectionHeight) - this.posY, EntityNukeTorex.this.position().z - this.posZ);

            motion = motion.normalize().scale(scale);

            return motion;
        }

        private void updateColor() {
            this.prevColor = this.color;

            double exX = EntityNukeTorex.this.position().x;
            double exY = EntityNukeTorex.this.position().y + EntityNukeTorex.this.coreHeight;
            double exZ = EntityNukeTorex.this.position().z;

            double distX = exX - posX;
            double distY = exY - posY;
            double distZ = exZ - posZ;

            double distSq = distX * distX + distY * distY + distZ * distZ;
            distSq /= EntityNukeTorex.this.heat;
            double dist = Math.sqrt(distSq);

            dist = Math.max(dist, 1);
            double col = 2D / dist;

            int type = EntityNukeTorex.this.entityData.get(DATA_TYPE);

            if(type == 1) {
                this.color = new Vec3(
                        Math.max(col * 1, 0.25),
                        Math.max(col * 2, 0.25),
                        Math.max(col * 0.5, 0.25)
                );
            } else if(type == 2) {
                Color color = Color.getHSBColor(this.angle / 2F / (float) Math.PI, 1F, 1F);
                if(this.type == TorexType.RING) {
                    this.color = new Vec3(
                            Math.max(col * 1, 0.25),
                            Math.max(col * 1, 0.25),
                            Math.max(col * 1, 0.25)
                    );
                } else {
                    this.color = new Vec3(color.getRed() / 255D, color.getGreen() / 255D, color.getBlue() / 255D);
                }
            } else {
                this.color = new Vec3(
                        Math.max(col * 2, 0.25),
                        Math.max(col * 1.5, 0.25),
                        Math.max(col * 0.5, 0.25)
                );
            }
        }

        public Vec3 getInterpPos(float interp) {
            float scale = (float) EntityNukeTorex.this.getScale();
            Vec3 base = new Vec3(
                    prevPosX + (posX - prevPosX) * interp,
                    prevPosY + (posY - prevPosY) * interp,
                    prevPosZ + (posZ - prevPosZ) * interp);

            if(this.type != TorexType.SHOCK) { //no rescale for the shockwave as this messes with the positions
                base = base.subtract(EntityNukeTorex.this.position()).scale(scale).add(EntityNukeTorex.this.position());
            }

            return base;
        }

        public Vec3 getInterpColor(float interp) {

            if(this.type == TorexType.CONDENSATION) {
                return new Vec3(1F, 1F, 1F);
            }

            double greying = EntityNukeTorex.this.getGreying();

            if(this.type == TorexType.RING) {
                greying += 1;
            }

            return new Vec3(
                    (prevColor.x + (color.x - prevColor.x) * interp) * greying,
                    (prevColor.y + (color.y - prevColor.y) * interp) * greying,
                    (prevColor.z + (color.z - prevColor.z) * interp) * greying);
        }

        public float getAlpha() {
            float alpha = (1F - ((float)age / (float)cloudletLife)) * EntityNukeTorex.this.getAlpha();
            if(this.type == TorexType.CONDENSATION) alpha *= 0.25;
            return alpha;
        }

        private float startingScale = 1;
        private float growingScale = 5F;

        public float getScale() {
            float base = startingScale + ((float)age / (float)cloudletLife) * growingScale;
            if(this.type != TorexType.SHOCK) base *= (float) EntityNukeTorex.this.getScale();
            return base;
        }

        public Cloudlet setScale(float start, float grow) {
            this.startingScale = start;
            this.growingScale = grow;
            return this;
        }

        private double motionMult = 1F;

        public Cloudlet setMotion(double mult) {
            this.motionMult = mult;
            return this;
        }
    }
}
