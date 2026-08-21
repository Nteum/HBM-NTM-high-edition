package com.hbm.render.util;

import com.hbm.blockentity.base.DummyableBE;
import com.hbm.blockentity.base.TileProxyCombo;
import com.hbm.core.blockentity.BEProxy;
import com.hbm.registries.HBMCaps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 显示特定方块的边界，用于多方块机器的调试
 * */
@OnlyIn(Dist.CLIENT)
public class DebugBoundsRenderer {

    // 调试开关，可以用一个快捷键或者指令来切换这个布尔值
    public static boolean SHOW_DEBUG_BOUNDS = false;

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        // 1. 只在“渲染当前世界线框”或者“AFTER_TRIPLES”阶段绘制，防止被方块遮挡或产生渲染错位
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        if (!SHOW_DEBUG_BOUNDS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        PoseStack poseStack = event.getPoseStack();
        // 拿到当前相机的实际坐标，用于将世界绝对坐标转换为渲染的相对坐标
        double camX = event.getCamera().getPosition().x;
        double camY = event.getCamera().getPosition().y;
        double camZ = event.getCamera().getPosition().z;

        // 获取原版用于画线框的 VertexConsumer (使用专用的单色线框类型)
        VertexConsumer buffer = mc.renderBuffers().crumblingBufferSource().getBuffer(RenderType.lines());

        // 2. 遍历玩家周围的 BlockEntity（这里假设只拿渲染距离内的）
        // 如果你的联动有特殊的中心管理器，可以直接去你的全局大表（如联动系统 INSTANCES）里拿位置
        BlockPos playerPos = mc.player.blockPosition();
        int radius = 16; // 调试检测半径

        poseStack.pushPose();

        // 核心步骤：将渲染矩阵的坐标原点平移到当前相机位置，这样后续直接用世界绝对物理坐标计算即可
        poseStack.translate(-camX, -camY, -camZ);

        for (BlockPos pos : BlockPos.betweenClosed(playerPos.offset(-radius, -8, -radius), playerPos.offset(radius, 8, radius))) {
            BlockEntity be = mc.level.getBlockEntity(pos);

            float[] rgba;
            AABB box = new AABB(pos), renderBoundingBox = null;
            if (be instanceof DummyableBE machine){
                rgba = new float[]{1.0f, 0.0f, 0.0f, 1.0f};
                renderBoundingBox = machine.getRenderBoundingBox();
            }else if (be instanceof BEProxy proxy){
                if (proxy.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent()) rgba = new float[]{0.0f, 1.0f, 0.0f, 1.0f};
                else if (proxy.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent() || proxy.getCapability(HBMCaps.LONG_ENERGY).isPresent()) rgba = new float[]{0.0f, 0.0f, 1.0f, 1.0f};
                else rgba = new float[]{1.0f, 0.0f, 0.0f, 0.1f};
            }else if (be instanceof TileProxyCombo proxy){
                if (proxy.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent()) rgba = new float[]{0.0f, 1.0f, 0.0f, 1.0f};
                else if (proxy.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent() || proxy.getCapability(HBMCaps.LONG_ENERGY).isPresent()) rgba = new float[]{0.0f, 0.0f, 1.0f, 1.0f};
                else rgba = new float[]{1.0f, 0.0f, 0.0f, 0.1f};
            }else continue;

            // 4. 调用原版底层的画线框算法 (红、绿、蓝、透明度)
            // 参数：PoseStack, VertexConsumer, x1, y1, z1, x2, y2, z2, R, G, B, A
            LevelRenderer.renderLineBox(
                    poseStack, buffer,
                    box.minX, box.minY, box.minZ,
                    box.maxX, box.maxY, box.maxZ,
                    rgba[0], rgba[1], rgba[2], rgba[3]
            );
            // 绘制机器碰撞箱体积
            if (renderBoundingBox != null){
                LevelRenderer.renderLineBox(
                        poseStack, buffer,
                        renderBoundingBox.minX, renderBoundingBox.minY, renderBoundingBox.minZ,
                        renderBoundingBox.maxX, renderBoundingBox.maxY, renderBoundingBox.maxZ,
                        1, 1, 1, 1f
                );
            }
        }

        poseStack.popPose();
    }
}