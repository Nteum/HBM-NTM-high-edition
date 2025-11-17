package com.hbm.compat.ballistix;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Simple renderer that mirrors the vanilla TNT renderer but swaps the block
 * state to the corresponding Ballistix explosive.
 */
public class BallistixPrimedExplosiveRenderer extends EntityRenderer<BallistixPrimedExplosive> {

    private final BlockRenderDispatcher blockRenderer;

    public BallistixPrimedExplosiveRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(BallistixPrimedExplosive entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.5D, 0.0D);

        int fuse = entity.getFuse();
        if (fuse < 10) {
            float f = 1.0F - ((float) fuse + partialTicks) / 10.0F;
            f = Mth.clamp(f, 0.0F, 1.0F);
            f = f * f;
            f = f * f;
            float scale = 1.0F + f * 0.3F;
            poseStack.scale(scale, scale, scale);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.translate(-0.5D, -0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));

        BlockState state = entity.getRenderState();
        blockRenderer.renderSingleBlock(state, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BallistixPrimedExplosive entity) {
        // Vanilla TNT renderer draws from the block atlas; same here.
        return net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS;
    }
}
