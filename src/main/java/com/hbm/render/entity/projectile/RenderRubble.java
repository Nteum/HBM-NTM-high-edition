package com.hbm.render.entity.projectile;

import com.hbm.entity.projectile.EntityRubble;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Quaternionf;

public class RenderRubble extends EntityRenderer<EntityRubble> {
    public RenderRubble(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(EntityRubble pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BlockState blockState = pEntity.getBlock();
        pPoseStack.pushPose();
        Quaternionf quaternionf = new Quaternionf().rotationAxis((float) Math.toRadians((pEntity.tickCount + pPartialTick) * 10), 1.0f, 0, 0);
        pPoseStack.mulPose(quaternionf);
        blockRenderer.renderSingleBlock(blockState, pPoseStack, pBuffer, pPackedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid());
        pPoseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityRubble pEntity) {
        return null;
    }
}
