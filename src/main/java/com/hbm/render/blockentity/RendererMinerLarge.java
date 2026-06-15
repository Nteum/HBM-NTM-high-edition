package com.hbm.render.blockentity;

import com.hbm.HBM;
import com.hbm.blockentity.machine.TileMinerLarge;
import com.hbm.main.ResourceManager;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.Models;
import com.hbm.render.model.engine.CustomPartsModel;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;

public class RendererMinerLarge implements BlockEntityRenderer<TileMinerLarge> {
    public static final ResourceLocation cobble = HBM.rl("textures/models/machines/cobblestone.png");
    public static final ResourceLocation gravel = HBM.rl("textures/models/machines/gravel.png");
    private BakedModel miner;
    public RendererMinerLarge(BlockEntityRendererProvider.Context pContext){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        miner = modelManager.getModel(Models.MINER_LARGE);
    }
    @Override
    public void render(TileMinerLarge drill, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockState blockState = drill.getBlockState();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer modelRenderer = blockRenderer.getModelRenderer();

        pPoseStack.pushPose();
        DirectionUtils.generalMachineRotate(pPoseStack, blockState);
        pPoseStack.translate(0.5f, -4f, 0.5f);
        if (miner instanceof CustomPartsModel.Baked customBakedModel){
            // 机器主体
            RenderUtils.renderModel(customBakedModel.getPart("Main"), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());


            float crusher = drill.prevCrusherRotation + (drill.crusherRotation - drill.prevCrusherRotation) * pPartialTick;
            pPoseStack.pushPose();
            pPoseStack.translate(0.0F, 2.0F, 2.8125F);
            pPoseStack.mulPose(Axis.XP.rotation(-crusher));
            pPoseStack.translate(0.0F, -2.0F, -2.8125F);
            RenderUtils.renderModel(customBakedModel.getPart("Crusher1"), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            pPoseStack.popPose();

            pPoseStack.pushPose();
            pPoseStack.translate(0.0F, 2.0F, 2.1875F);
            pPoseStack.mulPose(Axis.XP.rotation(crusher));
            pPoseStack.translate(0.0F, -2.0F, -2.1875F);
            RenderUtils.renderModel(customBakedModel.getPart("Crusher2"), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            pPoseStack.popPose();

            pPoseStack.pushPose();
            pPoseStack.mulPose(Axis.YN.rotation(drill.prevDrillRotation + (drill.drillRotation - drill.prevDrillRotation) * pPartialTick));
            float ext = drill.prevDrillExtension + (drill.drillExtension - drill.prevDrillExtension) * pPartialTick;
            pPoseStack.translate(0.0F, -ext, 0.0F);
            RenderUtils.renderModel(customBakedModel.getPart("Drillbit"), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            while(ext >= -1.5) {
                RenderUtils.renderModel(customBakedModel.getPart("Shaft"), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
                pPoseStack.translate(0.0D, 2.0D, 0.0D);
                ext -= 2;
            }
            pPoseStack.popPose();

            if(drill.chuteTimer > 0) {
                float widthX = 0.125f;
                float widthZ = 0.125f;
                double speed = 250D;
                double dropU = -System.currentTimeMillis() % speed / speed;
                double dropL = dropU + 4;
                RenderUtils.renderRectPillar(pPoseStack, pBuffer.getBuffer(RenderType.armorCutoutNoCull(cobble)), 0xffffffff, new Vec3(0, 3, 2.5), new Vec3(0, 2, 2.5), 2 * widthX, 2 * widthZ, pPackedLight, pPackedOverlay);

                boolean smoosh = drill.isEnableCrusher();
                widthX = smoosh ? 0.5f : 0.25f;
                widthZ = 0.0625f;
                double uU = smoosh ? 4 : 2;
                double uL = 0.5;
                RenderUtils.renderRectPillar(pPoseStack, pBuffer.getBuffer(RenderType.armorCutoutNoCull(gravel)), 0xffffffff, new Vec3(0, 2, 2.5), new Vec3(0, 1, 2.5), 2 * widthX, 2 * widthZ, pPackedLight, pPackedOverlay);
            }
        }else {
            RenderUtils.renderModel(miner, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
        }
        pPoseStack.popPose();
    }
}
