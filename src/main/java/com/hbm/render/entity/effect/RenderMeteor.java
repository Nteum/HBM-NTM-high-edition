package com.hbm.render.entity.effect;

import com.hbm.HBM;
import com.hbm.entity.effect.EntityMeteor;
import com.hbm.registries.ModBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RenderMeteor extends EntityRenderer<EntityMeteor> {
    public RenderMeteor(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(EntityMeteor pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        pPoseStack.pushPose();
        pPoseStack.translate(-1, 1, -1);
        pPoseStack.scale(2, 2, 2);
        float roll = (pEntity.tickCount + pPartialTick) * 10;
        Quaternionf quaternionf = new Quaternionf().rotationAxis((float) Math.toRadians(roll), 1.0f, 1.0f, 1.0f);
        pPoseStack.mulPose(quaternionf);
        blockRenderer.renderSingleBlock(ModBlocks.BLOCK_METEOR_MOLTEN.get().defaultBlockState(), pPoseStack, pBuffer, pPackedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid());
        pPoseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityMeteor pEntity) {
        return HBM.rl("textures/block/block_meteor_molten.png");
    }
}
