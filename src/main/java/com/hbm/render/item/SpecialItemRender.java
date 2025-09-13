package com.hbm.render.item;

import com.hbm.item.HBMWeapon;
import com.hbm.render.model.Models;
import com.hbm.render.model.MissileHeadModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpecialItemRender extends BlockEntityWithoutLevelRenderer {
    private MissileHeadModel missileHeadModel;
    public SpecialItemRender(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);

    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
        super.onResourceManagerReload(pResourceManager);
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        this.missileHeadModel = new MissileHeadModel(modelManager.getModel(Models.MP_W_15_BALEFIRE));
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        super.renderByItem(pStack, pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
//        ModelManager modelManager = Minecraft.getInstance().getModelManager();
//        BlockRenderDispatcher blockDispatcher = Minecraft.getInstance().getBlockRenderer();
//        ModelBlockRenderer blockRenderer = blockDispatcher.getModelRenderer();
//        BakedModel model;
//        if (item instanceof NukeFat.NukeItem){
//            Block block = ((BlockItem) item).getBlock();
//            BlockState blockState = block.defaultBlockState();
//            model = modelManager.getModel(Models.FAT_MAN);
//
//            pPoseStack.popPose();
//            pPoseStack.mulPose(Axis.YP.rotationDegrees(45));
//            renderBlockModel(model,blockState,blockRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
//            pPoseStack.pushPose();
//        }
        if (pStack.is(HBMWeapon.MP_WARHEAD_15_BALEFIRE.get())){
            pPoseStack.pushPose();

//            VertexConsumer vertexconsumer1 = ItemRenderer.getFoilBufferDirect(pBuffer, this.missileHeadModel.renderType(MissileHeadModel.TEXTURE), false, pStack.hasFoil());
//            this.missileHeadModel.renderToBuffer(pPoseStack,vertexconsumer1,pPackedLight,pPackedOverlay,1.0F,1.0F,1.0F,1.0F);
            pPoseStack.popPose();
        }
    }
}
