package com.hbm.render.item;

import com.hbm.item.HBMWeapon;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.Models;
import com.hbm.render.model.item.SimpleBakedModelWrapper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpecialItemRender extends BlockEntityWithoutLevelRenderer {
    private SimpleBakedModelWrapper missileHeadModel;
    // 空加载函数，主要用于懒加载。
    public SpecialItemRender(){
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }
    public SpecialItemRender(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
        super.onResourceManagerReload(pResourceManager);
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        missileHeadModel = (SimpleBakedModelWrapper) Models.get(Models.MP_W_15_BALEFIRE);
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        super.renderByItem(pStack, pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        if (pStack.is(HBMWeapon.MP_WARHEAD_15_BALEFIRE.get())){
            pPoseStack.pushPose();
            pPoseStack.scale(0.25f, 0.25f, 0.25f);
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(45));
            RenderUtils.renderModel(missileHeadModel, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            pPoseStack.popPose();
        }
    }
}
