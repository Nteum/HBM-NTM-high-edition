package com.hbm.render.block;

import com.hbm.HBM;
import com.hbm.block.interfaces.ICustomLookTooltip;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = HBM.MODID, value = Dist.CLIENT)
public class BlockLookTooltipRenderer {

    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // RayTrace 检测方块
        HitResult hit = mc.hitResult;
        if (hit instanceof BlockHitResult bhr) {
            BlockPos pos = bhr.getBlockPos();
            BlockEntity blockEntity = mc.level.getBlockEntity(pos);
            Component tooltip;
            if (blockEntity != null && blockEntity instanceof ICustomLookTooltip be && (tooltip = be.getLookTooltip()) != null){
                // 获取渲染位置（方块上方）
                double x = pos.getX() + 0.5;
                double y = pos.getY() + 1.5;
                double z = pos.getZ() + 0.5;

                Vec3 cameraPos = event.getCamera().getPosition();
                PoseStack ps = event.getPoseStack();
                ps.pushPose();
                ps.translate(x - cameraPos.x, y - cameraPos.y, z - cameraPos.z);
                // 始终朝向相机
                ps.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
                // 文本缩放（和原版名牌一致，负号用于修正左右镜像）
                ps.scale(-0.025F, -0.025F, 0.025F);

                MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
                Matrix4f matrix = ps.last().pose();

                Font font = mc.font;
                float width = font.width(tooltip);
                // 画字（不带背景，按需要可改 SEE_THROUGH 让其无视深度）
                font.drawInBatch(tooltip, -width /2, 0, 0xFFFFFF, false, matrix, buffers, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
                // 别忘了 flush
                buffers.endBatch();

                ps.popPose();
            }
        }
    }
}

