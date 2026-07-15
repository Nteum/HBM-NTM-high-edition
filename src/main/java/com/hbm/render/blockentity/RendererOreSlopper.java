package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.TileOreSloppper;
import com.hbm.registries.ModItems;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.engine.CustomPartsModel;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class RendererOreSlopper implements BlockEntityRenderer<TileOreSloppper> {
    @Override
    public void render(TileOreSloppper slopper, float interp, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockState blockState = slopper.getBlockState();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        poseStack.pushPose();
        DirectionUtils.generalMachineRotate(poseStack, blockState);
        poseStack.translate(0.5, 0, 0.5);
        BakedModel blockModel = blockRenderer.getBlockModel(slopper.getBlockState());
        if (blockModel instanceof CustomPartsModel.Baked model){
            RenderUtils.renderModel(model.getPart("Base"), poseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            poseStack.pushPose();
            double slide = slopper.prevSlider + (slopper.slider - slopper.prevSlider) * interp;
            poseStack.translate(0, 0, slide * -3);
            RenderUtils.renderModel(model.getPart("Slider"), poseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            poseStack.pushPose();
            double extend = (slopper.prevBucket + (slopper.bucket - slopper.prevBucket) * interp) * 1.5;
            poseStack.translate(0, -Mth.clamp(extend - 0.25, 0, 1.25), 0);
            RenderUtils.renderModel(model.getPart("Hydraulics"), poseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            poseStack.translate(0, -Mth.clamp(extend, 0, 1.25), 0);
            RenderUtils.renderModel(model.getPart("Bucket"), poseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            if (slopper.animation == TileOreSloppper.SlopperAnimation.LIFTING){
                poseStack.translate(0.0625D, 4.3125D, 2D);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.mulPose(Axis.XN.rotationDegrees(90));
                ItemStack stack = new ItemStack(ModItems.BEDROCK_ORE_COMPLEX.get());
                ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, pPackedLight, pPackedOverlay, poseStack, pBuffer, slopper.getLevel(), 42);
            }
            poseStack.popPose();
            poseStack.popPose();
            //
            double blades = slopper.prevBlades + (slopper.blades - slopper.prevBlades) * interp;
            poseStack.pushPose();
            poseStack.translate(0.375, 2.75, 0);
            poseStack.mulPose(Axis.ZP.rotation((float) blades));
            poseStack.translate(-0.375, -2.75, 0);
            RenderUtils.renderModel(model.getPart("BladesLeft"), poseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(-0.375, 2.75, 0);
            poseStack.mulPose(Axis.ZP.rotation((float) -blades));
            poseStack.translate(0.375, -2.75, 0);
            RenderUtils.renderModel(model.getPart("BladesRight"), poseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            poseStack.popPose();

            double fan = slopper.prevFan + (slopper.fan - slopper.prevFan) * interp;

            poseStack.pushPose();
            poseStack.translate(0, 1.875, -1);
            poseStack.mulPose(Axis.XP.rotation((float) -fan));
            poseStack.translate(0, -1.875, 1);
            RenderUtils.renderModel(model.getPart("Fan"), poseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            poseStack.popPose();

            poseStack.popPose();
        }
        poseStack.popPose();
        //--------------------------
    }
}
