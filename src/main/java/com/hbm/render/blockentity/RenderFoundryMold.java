package com.hbm.render.blockentity;

import com.hbm.HBM;
import com.hbm.Inventory.material.HBMMatter;
import com.hbm.blockentity.tools.TileFoundryMold;
import com.hbm.registries.HBMMatters;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderFoundryMold implements BlockEntityRenderer<TileFoundryMold> {
    public static final ResourceLocation FLUID_TEXTURE = HBM.rl("textures/block/fluid/lava_gray.png");
    public RenderFoundryMold(BlockEntityRendererProvider.Context pContext){
    }
    @Override
    public void render(TileFoundryMold be, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStackHandler items = be.getItems();
        FluidTank tank = be.getTank();
        ItemStack moldStack, resultStack;
        pPoseStack.pushPose();
        if (!tank.isEmpty()){
            int capacity = tank.getCapacity();
            int fluidAmount = tank.getFluidAmount();
            float flowHeight = 0.375f * fluidAmount / capacity + 0.125f;
            HBMMatter matter = HBMMatters.getMatterFromFluid(tank.getFluid());
            int color = matter == null ? 0xffffff : matter.moltenColor;
            float red = FastColor.ARGB32.red(color) / 255.0f;
            float green = FastColor.ARGB32.green(color) / 255.0f;
            float blue = FastColor.ARGB32.blue(color) / 255.0f;
            float alpha = 1.0f;
            pPoseStack.translate(0.5, flowHeight, 0.5);
            VertexConsumer consumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(FLUID_TEXTURE));
            Matrix4f pose = pPoseStack.last().pose();
            Matrix3f normal = pPoseStack.last().normal();
            int light = pPackedLight; // 也可自行计算方块光照
            int overlay = pPackedOverlay; // 通常用 OverlayTexture.NO_OVERLAY

            float width = 0.9f;
            float depth = 0.9f;
            // 因为已经平移到中心，顶点坐标范围是 -width/2 到 width/2
            float halfWidth = width / 2.0f;
            float halfDepth = depth / 2.0f;

            // 顶点 0 (左下)
            consumer.vertex(pose, -halfWidth, 0, -halfDepth).color(red, green, blue, alpha).uv(0, 1).overlayCoords(overlay).uv2(light).normal(normal, 0, 1, 0).endVertex();
            // 顶点 1 (右下)
            consumer.vertex(pose,  halfWidth, 0, -halfDepth).color(red, green, blue, alpha).uv(1, 1).overlayCoords(overlay).uv2(light).normal(normal, 0, 1, 0).endVertex();
            // 顶点 2 (右上)
            consumer.vertex(pose,  halfWidth, 0,  halfDepth).color(red, green, blue, alpha).uv(1, 0).overlayCoords(overlay).uv2(light).normal(normal, 0, 1, 0).endVertex();
            // 顶点 3 (左上)
            consumer.vertex(pose, -halfWidth, 0,  halfDepth).color(red, green, blue, alpha).uv(0, 0).overlayCoords(overlay).uv2(light).normal(normal, 0, 1, 0).endVertex();
        }else {
            pPoseStack.pushPose();
            pPoseStack.translate(0.5, 0.125, 0.5);
            if (!(moldStack = items.getStackInSlot(0)).isEmpty()){
                pPoseStack.pushPose();
                pPoseStack.mulPose(Axis.XN.rotation(Mth.HALF_PI));
                pPoseStack.scale(0.9f, 0.9f, 0.9f);
                itemRenderer.render(moldStack, ItemDisplayContext.GUI,true,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,itemRenderer.getModel(moldStack, be.getLevel(), null, 0));
                pPoseStack.popPose();
            }
            if (!(resultStack = items.getStackInSlot(1)).isEmpty()){
                pPoseStack.pushPose();
                pPoseStack.translate(0, 0.125, 0);
                pPoseStack.mulPose(Axis.XN.rotation(Mth.HALF_PI));
                pPoseStack.scale(0.75f, 0.75f, 0.75f);
                itemRenderer.render(resultStack, ItemDisplayContext.GUI,true,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,itemRenderer.getModel(resultStack, be.getLevel(), null, 0));
                pPoseStack.popPose();
            }
            pPoseStack.popPose();
        }
        pPoseStack.popPose();
    }
}
