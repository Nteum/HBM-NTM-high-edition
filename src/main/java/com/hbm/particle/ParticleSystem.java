package com.hbm.particle;

import com.hbm.HBMKey;
import com.hbm.particle.type.ParticleExSmoke;
import com.hbm.particle.type.ParticleLetter;
import com.hbm.particle.type.ParticleRocketFlame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

/**
 * 粒子系统，其实只是和粒子相关的东西的大杂烩。
 * bob习惯在客户端直接生成粒子，而不是在服务端发包，我其实很不喜欢这种方式，尊重他的选择。
 * */
@OnlyIn(Dist.CLIENT)
public class ParticleSystem {
    public static final ConcurrentMap<String, BiConsumer<CompoundTag, Vec3>> PARTICLE_COMBOS = new ConcurrentHashMap<>();
    static {
        PARTICLE_COMBOS.put("waterSplash", ParticleSystem::waterSplash);
        PARTICLE_COMBOS.put("ABMContrail", ParticleSystem::contrailABM);
        PARTICLE_COMBOS.put("launchSmoke", ParticleSystem::launchSmoke);
        PARTICLE_COMBOS.put("exKerosene", ParticleSystem::exKerosene);
        PARTICLE_COMBOS.put("exHydrogen", ParticleSystem::exHydrogen);
        PARTICLE_COMBOS.put("exSolid", ParticleSystem::exSolid);
        PARTICLE_COMBOS.put("exBalefire", ParticleSystem::exBalefire);
        PARTICLE_COMBOS.put("radFog", ParticleSystem::radFog);
        PARTICLE_COMBOS.put("smoke", ParticleSystem::smoke);
        PARTICLE_COMBOS.put("exhaust", ParticleSystem::exhaust);
        PARTICLE_COMBOS.put("firework", ParticleSystem::firework);
        PARTICLE_COMBOS.put("vanillaburst", ParticleSystem::vanillaburst);
        PARTICLE_COMBOS.put("vanillaExt", ParticleSystem::vanillaExt);
        PARTICLE_COMBOS.put("vanilla", ParticleSystem::vanilla);
        PARTICLE_COMBOS.put("jetpack", ParticleSystem::jetpack);

    }

    public static void handleParticleCombo(CompoundTag tag){
        if (Minecraft.getInstance().level == null) return;
        if (tag.contains(HBMKey.TYPE, Tag.TAG_STRING) && tag.contains(HBMKey.X, Tag.TAG_DOUBLE) && tag.contains(HBMKey.Y, Tag.TAG_DOUBLE) && tag.contains(HBMKey.Z, Tag.TAG_DOUBLE)){
            String type = tag.getString(HBMKey.TYPE);
            PARTICLE_COMBOS.getOrDefault(type, (tag1, pos) -> {}).accept(tag, new Vec3(tag.getDouble(HBMKey.X), tag.getDouble(HBMKey.Y), tag.getDouble(HBMKey.Z)));
        }
    }

    public static void addRocketFlame(double pX, double pY, double pZ, double movX, double movY, double movZ, @Nullable Float scale, @Nullable Integer lifetime){
        if (Minecraft.getInstance().player.position().distanceTo(new Vec3(pX,pY,pZ)) > 350) return;
        ParticleRocketFlame particle = (ParticleRocketFlame)Minecraft.getInstance().particleEngine.makeParticle(ModParticleTypes.ROCKET_FLAME.get(), pX, pY, pZ, movX, movY, movZ);
        if (particle == null) return;
        if (scale != null) particle.scale(scale);
        if (lifetime != null) particle.setLifetime(lifetime);
        Minecraft.getInstance().particleEngine.add(particle);
    }

    public static void waterSplash(CompoundTag tag, Vec3 position){
        ClientLevel level = Minecraft.getInstance().level;
        for (int i = 0; i < 10; i++) {
            level.addParticle(ParticleTypes.CLOUD, position.x + level.random.nextGaussian(), position.y + level.random.nextGaussian(), position.z + level.random.nextGaussian(), 0, 0, 0);
        }
    }

    public static void contrailABM(CompoundTag tag, Vec3 position){
        ClientLevel level = Minecraft.getInstance().level;
        level.addParticle(ModParticleTypes.ROCKET_FLAME.get(), position.x, position.y, position.z, 0, 0, 0);
    }

    public static void launchSmoke(CompoundTag data, Vec3 position){
        ClientLevel level = Minecraft.getInstance().level;
        double motionX = data.getDouble("moX");
        double motionY = data.getDouble("moY");
        double motionZ = data.getDouble("moZ");
        level.addParticle(ModParticleTypes.LAUNCH_SMOKE.get(), position.x, position.y, position.z, motionX, motionY, motionZ);
    }

    public static void exKerosene(CompoundTag data, Vec3 position){
        Minecraft.getInstance().level.addParticle(ModParticleTypes.CONTRAIL.get(), position.x, position.y, position.z, 0, 0, 0);
    }

    public static void exSolid(CompoundTag data, Vec3 position){
        Minecraft.getInstance().level.addParticle(ModParticleTypes.CONTRAIL.get(), position.x, position.y, position.z, 0.3F, 0.2F, 0.05F);
    }

    public static void exHydrogen(CompoundTag data, Vec3 position){
        Minecraft.getInstance().level.addParticle(ModParticleTypes.CONTRAIL.get(), position.x, position.y, position.z, 0.7F, 0.7F, 0.7F);
    }

    public static void exBalefire(CompoundTag data, Vec3 position){
        Minecraft.getInstance().level.addParticle(ModParticleTypes.CONTRAIL.get(), position.x, position.y, position.z, 0.2F, 0.7F, 0.2F);
    }

    public static void radFog(CompoundTag data, Vec3 position){
        Minecraft.getInstance().level.addParticle(ModParticleTypes.RADIATION_FOG.get(), position.x, position.y, position.z, 0, 0, 0);
    }

    public static void missileContrail(CompoundTag data, Vec3 position){
        if (Minecraft.getInstance().player.position().distanceTo(position) > 350) return;
        double motionX = data.getDouble("moX");
        double motionY = data.getDouble("moY");
        double motionZ = data.getDouble("moZ");

        ParticleRocketFlame particle = (ParticleRocketFlame)Minecraft.getInstance().particleEngine.makeParticle(ModParticleTypes.ROCKET_FLAME.get(), position.x, position.y, position.z, motionX, motionY, motionZ);
        if (particle == null) return;
        if (data.contains("scale", Tag.TAG_FLOAT)) particle.scale(data.getFloat("scale"));
        if (data.contains("lifetime", Tag.TAG_INT)) particle.setLifetime(data.getInt("lifetime"));
        Minecraft.getInstance().particleEngine.add(particle);
    }

    public static void smoke(CompoundTag data, Vec3 position){
        String mode = data.getString("mode");
        int count = Math.max(1, data.getInt("count"));

        ClientLevel level = Minecraft.getInstance().level;
        RandomSource random = level.random;

        switch (mode){
            case "cloud" -> {
                for(int i = 0; i < count; i++) {
                    double movY = random.nextGaussian() * (1 + (double) count / 100);
                    level.addParticle(ModParticleTypes.EX_SMOKE.get(), position.x, position.y, position.z,
                            random.nextGaussian() * (1 + (double) count / 100),
                            random.nextBoolean() ? Math.abs(movY) : movY,
                            random.nextGaussian() * (1 + (double) count / 100));
                }
            }
            case "radical" -> {
                for(int i = 0; i < count; i++) {
                    level.addParticle(ModParticleTypes.EX_SMOKE.get(), position.x, position.y, position.z,
                            random.nextGaussian() * (1 + (double) count / 50),
                            random.nextGaussian() * (1 + (double) count / 50),
                            random.nextGaussian() * (1 + (double) count / 50));
                }
            }
            case "radialDigamma" -> {
                Vec3 vec3 = new Vec3(2, 0, 0).yRot(random.nextFloat() * Mth.PI * 2f);
                for(int i = 0; i < count; i++) {
                    level.addParticle(ModParticleTypes.DIGAMMA_SMOKE.get(), position.x, position.y, position.z, vec3.x, 0, vec3.y);
                    vec3 = vec3.yRot(Mth.PI * 2 / count);
                }
            }
            case "shock" -> {
                double strength = data.getDouble("strength");
                Vec3 vec3 = new Vec3(strength, 0, 0).yRot(random.nextInt(360));
                for(int i = 0; i < count; i++) {
                    level.addParticle(ModParticleTypes.EX_SMOKE.get(), position.x, position.y, position.z, vec3.x, 0, vec3.y);
                    vec3 = vec3.yRot(Mth.PI * 2 / count);
                }
            }
            case "shockRand" -> {
                double strength = data.getDouble("strength");
                Vec3 vec3 = new Vec3(strength, 0, 0).yRot(random.nextInt(360));
                double r;
                for(int i = 0; i < count; i++) {
                    r = random.nextDouble();
                    level.addParticle(ModParticleTypes.EX_SMOKE.get(), position.x, position.y, position.z, vec3.x * r, 0, vec3.y * r);
                    vec3 = vec3.yRot((float) 360 / count);
                }
            }
            case "wave" -> {
                double strength = data.getDouble("range");
                Vec3 vec3 = new Vec3(strength, 0, 0);
                for(int i = 0; i < count; i++) {
                    vec3 = vec3.yRot((float) Math.toRadians(random.nextFloat() * 360));
                    ParticleExSmoke particle = (ParticleExSmoke)Minecraft.getInstance().particleEngine.makeParticle(ModParticleTypes.EX_SMOKE.get(), position.x + vec3.x, position.y, position.z + vec3.z, 0,0,0);
                    particle.setLifetime(50);
                    Minecraft.getInstance().particleEngine.add(particle);
                    vec3 = vec3.yRot((float) 360 / count);
                }
            }
            case "foamSplash" -> {
                double strength = data.getDouble("range");
                Vec3 vec3 = new Vec3(strength, 0, 0);
                for(int i = 0; i < count; i++) {
                    vec3 = vec3.yRot((float) Math.toRadians(random.nextFloat() * 360));
                    ParticleExSmoke particle = (ParticleExSmoke)Minecraft.getInstance().particleEngine.makeParticle(ModParticleTypes.FOAM.get(), position.x + vec3.x, position.y, position.z + vec3.z, 0,0,0);
                    particle.setLifetime(50);
                    Minecraft.getInstance().particleEngine.add(particle);
                    vec3 = vec3.yRot((float) 360 / count);
                }
            }
        }
    }

    public static void exhaust(CompoundTag data, Vec3 position){
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;
        if (position.distanceTo(player.position()) > 350) return;
        String mode = data.getString("mode");
        int count = Math.max(1, data.getInt("count"));
        double width = data.getDouble("width");

        switch (mode){
            case "soyuz" -> {
                for(int i = 0; i < count; i++) {
                    level.addParticle(ModParticleTypes.ROCKET_FLAME.get(), position.x + level.random.nextGaussian() * width, position.y, position.z + level.random.nextGaussian() * width,
                            0, -0.75 + level.random.nextDouble() * 0.5, 0);
                }
            }
            case "meteor" -> {
                for(int i = 0; i < count; i++) {
                    level.addParticle(ModParticleTypes.ROCKET_FLAME.get(), position.x + level.random.nextGaussian() * width, position.y + level.random.nextGaussian() * width, position.z + level.random.nextGaussian() * width, 0, 0, 0);
                }
            }
        }
    }

    public static void firework(CompoundTag data, Vec3 position){
        ClientLevel level = Minecraft.getInstance().level;
        int color = data.getInt("color");
        char c = (char)data.getInt("char");

        ParticleLetter particle = (ParticleLetter) Minecraft.getInstance().particleEngine.makeParticle(ModParticleTypes.LETTER.get(), position.x, position.y, position.z, 0, 0, 0);
        particle.setChar(c);
        particle.setColor(color);
        Minecraft.getInstance().particleEngine.add(particle);

        for(int i = 0; i < 50; i++) {
            Particle firework =  Minecraft.getInstance().particleEngine.makeParticle(ParticleTypes.FIREWORK, 0.4f * level.random.nextGaussian(),0.4f * level.random.nextGaussian(),0.4f * level.random.nextGaussian(), 0, 0, 0);
            firework.setColor(color >> 16 & 255, color >> 8 & 255, color & 255);
            Minecraft.getInstance().particleEngine.add(firework);
        }
    }

    public static void vanillaburst(CompoundTag data, Vec3 position){
        RandomSource rand = Minecraft.getInstance().level.random;
        ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
        double motion = data.getDouble("motion");
        for(int i = 0; i < data.getInt("count"); i++) {
            double mX = rand.nextGaussian() * motion;
            double mY = rand.nextGaussian() * motion;
            double mZ = rand.nextGaussian() * motion;

            Particle fx = null;

            if("flame".equals(data.getString("mode"))) {
                fx = particleEngine.makeParticle(ParticleTypes.FLAME, position.x, position.y, position.z, mX, mY, mZ);
            }

            if("cloud".equals(data.getString("mode"))) {
                fx = particleEngine.makeParticle(ParticleTypes.CLOUD, position.x, position.y, position.z, mX, mY, mZ);
            }

            if("reddust".equals(data.getString("mode"))) {
                fx = particleEngine.makeParticle(DustParticleOptions.REDSTONE, position.x, position.y, position.z, mX, mY, mZ);
            }

            if("bluedust".equals(data.getString("mode"))) {
                fx = particleEngine.makeParticle(DustParticleOptions.REDSTONE, position.x, position.y, position.z, 0.01F, 0.01F, 1F);
            }

            if("greendust".equals(data.getString("mode"))) {
                fx = particleEngine.makeParticle(DustParticleOptions.REDSTONE, position.x, position.y, position.z, 0.01F, 0.5F, 0.1F);
            }

            if("blockdust".equals(data.getString("mode"))) {
                BlockState state = NbtUtils.readBlockState(Minecraft.getInstance().level.holderLookup(Registries.BLOCK), data.getCompound("block"));
                fx = particleEngine.makeParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), position.x, position.y, position.z, mX, mY + 0.2, mZ);
                fx.setLifetime(50 + rand.nextInt(50));
            }

            if(fx != null)
                particleEngine.add(fx);
        }
    }

    public static void vanillaExt(CompoundTag data, Vec3 position) {
        RandomSource rand = Minecraft.getInstance().level.random;
        ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
        double mX = data.getDouble("mX");
        double mY = data.getDouble("mY");
        double mZ = data.getDouble("mZ");

        Particle fx = null;

        if("flame".equals(data.getString("mode"))) {
            fx = particleEngine.makeParticle(ParticleTypes.FLAME, position.x, position.y, position.z, mX, mY, mZ);
        }

        if("smoke".equals(data.getString("mode"))) {
            fx = particleEngine.makeParticle(ParticleTypes.SMOKE, position.x, position.y, position.z, mX, mY, mZ);
        }

        if("volcano".equals(data.getString("mode"))) {
            fx = particleEngine.makeParticle(ParticleTypes.SMOKE, position.x, position.y, position.z, rand.nextGaussian() * 0.2, 2.5 + rand.nextDouble(), rand.nextGaussian() * 0.2);
            fx.scale(100);
            fx.setLifetime(200 + rand.nextInt(50));
            try {
                // org.apache.logging.log4j.core.util
                ReflectionUtil.setFieldValue(Particle.class.getDeclaredField("hasPhysics"), fx, false);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if("cloud".equals(data.getString("mode"))) {
            fx = particleEngine.makeParticle(ParticleTypes.CLOUD, position.x, position.y, position.z, mX, mY, mZ);

            if(data.contains("r")) {
                float rng = rand.nextFloat() * 0.1F;
                fx.setColor(Mth.clamp(data.getFloat("r") + rng, 0, 1), Mth.clamp(data.getFloat("g") + rng, 0, 1), Mth.clamp(data.getFloat("b") + rng, 0, 1));
                fx.scale(7.5f);
                fx.setParticleSpeed(0,0,0);
            }
        }

        if("reddust".equals(data.getString("mode"))) {
            fx = particleEngine.makeParticle(DustParticleOptions.REDSTONE, position.x, position.y, position.z, mX, mY, mZ);
        }

        if("bluedust".equals(data.getString("mode"))) {
            fx = particleEngine.makeParticle(DustParticleOptions.REDSTONE, position.x, position.y, position.z, 0.01F, 0.01F, 1F);
        }

        if("greendust".equals(data.getString("mode"))) {
            fx = particleEngine.makeParticle(DustParticleOptions.REDSTONE, position.x, position.y, position.z, 0.01F, 0.5F, 0.1F);
        }

        if("fireworks".equals(data.getString("mode"))) {
            fx = particleEngine.makeParticle(ParticleTypes.FIREWORK, position.x, position.y, position.z, 0,0, 0);
        }
        /**
         * 低版本是生成一个EntityLargeExplodeFX和多个EntityExplodeFX，这实际是原版Explosion的设定
         * 在搞版本中，这一点用HugeExplosionSeedParticle代替了，这个粒子可以自发生成若干爆炸子粒子
         * 但它固定生成6个子粒子，理论上可以通过mixin改变这个数量，但我觉得没什么必要
         * */
        if("largeexplode".equals(data.getString("mode"))) {
            fx = particleEngine.makeParticle(ParticleTypes.EXPLOSION_EMITTER, position.x, position.y, position.z, 0, 0, 0);
            float r = 1.0F - rand.nextFloat() * 0.2F;
            fx.setColor(r, 0.9f * r, 0.5f * r);
            particleEngine.add(fx);
        }

        if("townaura".equals(data.getString("mode"))) {
            fx = particleEngine.makeParticle(ParticleTypes.EFFECT, position.x, position.y, position.z, mX, mY, mZ);
            float color = 0.5F + rand.nextFloat() * 0.5F;
            fx.setColor(0.8F * color, 0.9F * color, color);
        }

        if("blockdust".equals(data.getString("mode"))) {
            BlockState state = NbtUtils.readBlockState(Minecraft.getInstance().level.holderLookup(Registries.BLOCK), data.getCompound("block"));
            fx = particleEngine.makeParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), position.x, position.y, position.z, mX, mY + 0.2, mZ);
            fx.setLifetime(10 + rand.nextInt(10));
        }

        if("colordust".equals(data.getString("mode"))) {
            fx = particleEngine.makeParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.WHITE_WOOL.defaultBlockState()), position.x, position.y, position.z, mX, mY + 0.2, mZ);
            fx.setLifetime(10 + rand.nextInt(20));
            fx.setColor(data.getFloat("r"), data.getFloat("g"), data.getFloat("b"));
        }

        if(fx != null) {
            if(data.getBoolean("noclip")) {
                try {
                    ReflectionUtil.setFieldValue(Particle.class.getDeclaredField("hasPhysics"), fx, false);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if(data.getInt("overrideAge") > 0) {
                fx.setLifetime(data.getInt("overrideAge"));
            }

            particleEngine.add(fx);
        }
    }

    public static void vanilla(CompoundTag data, Vec3 position){
        ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.get(new ResourceLocation(data.getString("mode")));
        Minecraft.getInstance().level.addParticle((ParticleOptions) particleType, position.x, position.y, position.z, data.getDouble("mX"), data.getDouble("mY"), data.getDouble("mZ"));
    }

    // 把几个喷气背包粒子效果合并了，相应的nbt方式也要改
    public static void jetpack(CompoundTag data, Vec3 position){
        ClientLevel level = Minecraft.getInstance().level;
        ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
        // 高版本表示粒子参数的设定
        ParticleStatus particleStatus = Minecraft.getInstance().options.particles().get();
        if(particleStatus == ParticleStatus.MINIMAL) return;

        if (level.getEntity(data.getInt("player")) instanceof LocalPlayer player){
            String mode = data.getString("mode");
            Vec3 vec= new Vec3(0,0,0), offset= new Vec3(0,0,0);
            double iyAdjust = 0;
            switch (mode) {
                case "jetpack" -> {
                    vec = new Vec3(0, 0, -0.25f);
                    offset = new Vec3(0.125f, 0, 0);
                    iyAdjust = -1;
                }
                case "bunny" -> {
                    vec = new Vec3(0, 0, -0.6);
                    offset = new Vec3(0.275, 0, 0);
                    iyAdjust = player.getPose() == Pose.CROUCHING ? -0.35 : -0.6;
                }
                case "bj" -> {
                    vec = new Vec3(0, 0, -0.3125);
                    offset = new Vec3(0.125, 0, 0);
                    iyAdjust = -0.9375;
                }
                case "dns" -> {
                    offset = new Vec3(0.125, 0, 0);
                    iyAdjust = -0.5;
                }
            }
            // 暂时以玩家身体方向为参考，原版不是这样的
            float angle = player.yBodyRot;
            vec = vec.yRot(angle);
            offset = offset.yRot(angle);
            double ix = player.getX() + vec.x;
            double iy = player.getY() + player.getEyeY() + iyAdjust;
            double iz = player.getZ() + vec.z;
            double ox = offset.x;
            double oz = offset.z;
            double moX = 0;
            double moY = 0;
            double moZ = 0;
            switch (mode) {
                case "jetpack" -> {
                    int state = data.getInt("state");
                    if (state == 0) {
                        moY -= 0.2;
                    } else if (state == 1) {
                        Vec3 look = player.getLookAngle();
                        moX -= look.x * 0.1D;
                        moY -= look.y * 0.1D;
                        moZ -= look.z * 0.1D;
                    }
                    double mX2 = Mth.clamp(player.getDeltaMovement().x + moX * 2, -5, 5);
                    double mY2 = Mth.clamp(player.getDeltaMovement().y + moY * 2, -5, 5);
                    double mZ2 = Mth.clamp(player.getDeltaMovement().z + moZ * 2, -5, 5);
                    level.addParticle(ParticleTypes.FLAME, ix + ox, iy, iz + oz, mX2, mY2, mZ2);
                    level.addParticle(ParticleTypes.FLAME, ix - ox, iy, iz - oz, mX2, mY2, mZ2);
                }
                case "bunny" -> {
                    vec = vec.normalize().scale(0.025);
                    moX = vec.x;
                    moY = vec.y;
                    for (int i = 0; i < 2; i++) {
                        SmokeParticle smoke = (SmokeParticle) particleEngine.makeParticle(ParticleTypes.SMOKE, ix + ox * (i == 0 ? -1 : 1), iy, iz + oz * (i == 0 ? -1 : 1), moX, 0, moZ);
                        smoke.scale(0.5f);
                        particleEngine.add(smoke);
                    }
                }
                case "bj" -> {
                    level.addParticle(new DustParticleOptions(new Vec3(0.8F, 0.5F, 1.0F).toVector3f(), 1.0f), ix + ox, iy, iz + oz, player.getDeltaMovement().x, player.getDeltaMovement().y, player.getDeltaMovement().z);
                    level.addParticle(new DustParticleOptions(new Vec3(0.8F, 0.5F, 1.0F).toVector3f(), 1.0f), ix - ox, iy, iz - oz, player.getDeltaMovement().x, player.getDeltaMovement().y, player.getDeltaMovement().z);
                }
                case "dns" -> {
                    level.addParticle(new DustParticleOptions(new Vec3(0.01F, 1.0F, 1.0F).toVector3f(), 1.0f), ix + ox, iy, iz + oz, player.getDeltaMovement().x, player.getDeltaMovement().y, player.getDeltaMovement().z);
                    level.addParticle(new DustParticleOptions(new Vec3(0.01F, 1.0F, 1.0F).toVector3f(), 1.0f), ix - ox, iy, iz - oz, player.getDeltaMovement().x, player.getDeltaMovement().y, player.getDeltaMovement().z);
                }
            }

            // 允许显示更丰富的粒子就多显示一些
            if (particleStatus == ParticleStatus.ALL && mode.equals("jetpack") || mode.equals("bj")){
                Vec3 pos = new Vec3(ix, iy, iz);
                // 方块射线检测
                BlockHitResult blockInLine = player.level().isBlockInLine(new ClipBlockStateContext(pos, pos.add(new Vec3(moX, moY, moZ).normalize().scale(10)), blockState -> !blockState.isAir()));
                if (blockInLine.getType() == HitResult.Type.BLOCK){
                    Vec3 delta = pos.subtract(blockInLine.getLocation());
                    Vec3 vel = new Vec3(0.75f - delta.length() * 0.075f, 0, 0);
                    for (int i = 0; i < (10 - delta.length()); i++) {
                        vel = vel.yRot(level.random.nextFloat() * Mth.PI * 2f);
                        level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(blockInLine.getBlockPos())),
                                blockInLine.getLocation().x, blockInLine.getLocation().y + 0.1, blockInLine.getLocation().z, vel.x, 0.1, vel.z);
                    }
                }
                double mX3 = Mth.clamp(player.getDeltaMovement().x + moX * 2, -10, 10);
                double mY3 = Mth.clamp(player.getDeltaMovement().y + moY * 2, -10, 10);
                double mZ3 = Mth.clamp(player.getDeltaMovement().z + moZ * 2, -10, 10);
                level.addParticle(ParticleTypes.SMOKE, ix + ox, iy, iz + oz, mX3, mY3, mZ3);
                level.addParticle(ParticleTypes.SMOKE, ix - ox, iy, iz - oz, mX3, mY3, mZ3);
            }
        }

    }

}
