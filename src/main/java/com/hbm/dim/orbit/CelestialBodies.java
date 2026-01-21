package com.hbm.dim.orbit;

import com.hbm.HBM;
import com.hbm.registries.HBMDimensions;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 内含可用的天体
public class CelestialBodies {
    protected static Set<CelestialBody> BODIES = Set.of();

    protected static void init(){
        BODIES = new HashSet<>();
        BODIES.add(new CelestialBody(new Vec3(0, 64, 0), 30, null, new ResourceLocation("minecraft", "textures/environment/sun.png")));
        BODIES.add(new CelestialBody(new Vec3(2000, 64, 0), 15, Level.OVERWORLD, HBM.rl("textures/env/space/earth.png")));
        BODIES.add(new CelestialBody(new Vec3(3000, 64, 0), 10, HBMDimensions.MOON_KEY, HBM.rl("textures/env/space/moon.png")));
    }
    public static void runServer(Level level){
        if (BODIES.isEmpty()) init();
        if (level instanceof ServerLevel serverLevel){
            for (CelestialBody body : BODIES) {
                body.runServer(serverLevel);
            }
        }
    }
    public static void render(Tesselator tesselator, BufferBuilder bufferbuilder, ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix){
        List<CelestialBody> sortedPlanets = new ArrayList<>(BODIES);
        // 2. 根据距离平方进行降序排列 (距离大的排在前面，先渲染)
        sortedPlanets.sort((b, a) -> Double.compare(a.location.distanceTo(camera.getPosition()), b.location.distanceTo(camera.getPosition())));
        for (CelestialBody body : sortedPlanets) {
            body.render(tesselator, bufferbuilder, level, ticks, partialTick, poseStack, camera, projectionMatrix);
        }
    }
}
