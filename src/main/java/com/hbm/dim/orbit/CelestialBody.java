package com.hbm.dim.orbit;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.Function;

/**
 * 代表太空中看到的天体，会随着距离玩家的远近展现不同大小，并且在玩家靠近时将其传送到对应维度
 * */
public class CelestialBody {
    public static float DEFAULT_DIST = 500;
    public Vec3 location;
    public float size;
    ResourceLocation texture;
    ResourceKey<Level> levelKey;
    public CelestialBody(Vec3 location, float size, ResourceKey<Level> levelKey, ResourceLocation texture){
        this.location = location;
        this.size = size;
        this.levelKey = levelKey;
        this.texture = texture;
    }
    public void render(Tesselator tesselator, BufferBuilder bufferbuilder, ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix){
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false); // 关闭深度写入，确保天空在最底层

        RenderSystem.setShaderTexture(0, texture);
        poseStack.pushPose();

        Vec3 playerLoc = camera.getPosition();
        Vec3 distance = location.subtract(playerLoc);
        float s = (float) Mth.clamp(size * DEFAULT_DIST / distance.length(), 2, 500);
        float elevation = (float) Math.toDegrees(-Math.atan2(distance.y(), Math.sqrt(distance.x() * distance.x() + distance.z() * distance.z()))) + 90f;
        float azimuth = (float) Math.toDegrees(Math.atan2(distance.x(), distance.z()));
        // 先绕Y轴转再绕X轴转
        poseStack.mulPose(Axis.YP.rotationDegrees(azimuth));
        poseStack.mulPose(Axis.XP.rotationDegrees(elevation));

        Matrix4f matrix4f = poseStack.last().pose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f, -s, 100.0F, -s).uv(0.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f, s, 100.0F, -s).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f, s, 100.0F, s).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix4f, -s, 100.0F, s).uv(0.0F, 1.0F).endVertex();

        poseStack.popPose();
        tesselator.end();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
    public void runServer(ServerLevel level){
        // 检测范围内的实体并进行传送
        if (level.random.nextInt(10) < 7){
            float teleportSize = size * 1.5f;
            List<Entity> list = level.getEntitiesOfClass(Entity.class, new AABB(location.subtract(teleportSize, teleportSize, teleportSize),location.add(teleportSize, teleportSize, teleportSize) ), TheEndGatewayBlockEntity::canEntityTeleport);
            if (!list.isEmpty()){
                Entity teleportedEntity = list.get(level.random.nextInt(list.size()));
                teleportToDimension(teleportedEntity.getRootVehicle(), levelKey, null);
            }
        }
    }
    /**
     * 将玩家传送到指定维度
     */
    public static void teleportToDimension(Entity entity, ResourceKey<Level> targetDim, @Nullable BlockPos pos) {
        if (targetDim == null) return;
        ServerLevel destLevel = entity.getServer().getLevel(targetDim);
        if (destLevel != null) {
            BlockPos newPos = pos == null ? destLevel.getSharedSpawnPos() : pos;
            entity.changeDimension(destLevel, new ITeleporter() {
                @Override
                public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                    // 克隆实体到目标世界
                    entity = repositionEntity.apply(false);
                    // 核心：设置在太空维度中的物理坐标
                    entity.teleportTo(newPos.getX() + 0.5, newPos.getY(), newPos.getZ() + 0.5);
                    return entity;
                }
            });
        }
    }
}
