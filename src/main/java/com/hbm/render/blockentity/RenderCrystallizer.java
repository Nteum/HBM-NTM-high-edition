package com.hbm.render.blockentity;

import com.hbm.Inventory.fluid.ExtendedFluidType;
import com.hbm.blockentity.machine.TileCrystallizer;
import com.hbm.gui.screen.GuiCentrifuge;
import com.hbm.main.ResourceManager;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.engine.CustomPartsModel;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class RenderCrystallizer implements BlockEntityRenderer<TileCrystallizer> {
    BakedModel model;
    public RenderCrystallizer(BlockEntityRendererProvider.Context pContext){}
    @Override
    public void render(TileCrystallizer crystallizer, float pPartialTick, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockState blockState = crystallizer.getBlockState();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        poseStack.pushPose();
        DirectionUtils.generalMachineRotate(poseStack, blockState);

        if (model == null) model = blockRenderer.getBlockModel(blockState);
        if (model instanceof CustomPartsModel.Baked customPartsModel){
            RenderUtils.renderModel(customPartsModel.getPart("Body"), poseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());

            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(crystallizer.prevAngle + (crystallizer.angle - crystallizer.prevAngle) * pPartialTick));
            RenderUtils.renderModel(customPartsModel.getPart("Spinner"), poseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            poseStack.popPose();

            if (crystallizer.prevAngle != crystallizer.angle){
                RenderSystem.enableBlend();
                RenderSystem.disableDepthTest();
                FluidStack fluidInTank = crystallizer.getFluids().getFluidInTank(0);
                RenderUtils.renderModel(customPartsModel.getPart("Fluid"), poseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.entityTranslucent(((ExtendedFluidType)(fluidInTank.getFluid().getFluidType())).stillTexture));
                RenderSystem.enableDepthTest();
                RenderSystem.disableBlend();
            }
        }else RenderUtils.renderModel(model, poseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
        poseStack.popPose();
    }
}
