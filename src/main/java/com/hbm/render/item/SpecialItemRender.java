package com.hbm.render.item;

import com.hbm.item.HBMWeapon;
import com.hbm.item.ItemBlockCustomModel;
import com.hbm.item.armor.ItemArmorFSB;
import com.hbm.item.weapon.ItemGun;
import com.hbm.registries.ModBlocks;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.Models;
import com.hbm.core.client.model.TrianglePartsModel;
import com.hbm.render.model.item.SimpleBakedModelWrapper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

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
        pPoseStack.pushPose();

        if (pStack.getItem() instanceof IMultiLayerItem iMultiLayerItem) renderMultiLayers(iMultiLayerItem.getLayers(pStack), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pStack);
        if (pStack.is(HBMWeapon.MP_WARHEAD_15_BALEFIRE.get())){
            if (missileHeadModel == null) missileHeadModel = (SimpleBakedModelWrapper) Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(HBMWeapon.MP_WARHEAD_15_BALEFIRE.getId(), "inventory"));

            pPoseStack.scale(0.35f, 0.35f, 0.35f);
            pPoseStack.translate(0.5,0.7,0);
            if (pDisplayContext.firstPerson()) pPoseStack.translate(0.5,0,0.5);
            pPoseStack.mulPose(Axis.ZN.rotationDegrees(45));
            if (Minecraft.getInstance().level != null)
                pPoseStack.mulPose(Axis.YP.rotationDegrees(Minecraft.getInstance().level.getGameTime() % 360));

            RenderUtils.renderModel(missileHeadModel, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
        }
        else if (pStack.getItem() instanceof  ItemArmorFSB armorFSB){
            armorFSB.renderObjItem(pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        }else if (pStack.getItem() instanceof ItemGun itemGun){
            itemGun.renderGun(pStack, pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        }else if (pStack.getItem() instanceof ItemBlockCustomModel itemBlockCustomModel){
            itemBlockCustomModel.renderItemModel(pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        }
        // 先用空间站试试水
        else if (pStack.is(ModBlocks.SPACE_STATION_BASE.get().asItem())){
            TrianglePartsModel.renderItem(pStack, pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, 1);
        }

        pPoseStack.popPose();
    }

    private void renderMultiLayers(List<ResourceLocation> layers, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, ItemStack stack) {
        if (layers == null || layers.isEmpty()) return;
        // 这一步负责把对应的贴图包装成一个扁平的 BakedModel 并叠加到当前的 PoseStack 矩阵上
        ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
        BakedModel layerModel;
        poseStack.pushPose();
        for (int i = 0; i < layers.size(); i++) {
            ResourceLocation location = layers.get(i);
            if (location == null) continue;
            ModelResourceLocation modelIdentifier = new ModelResourceLocation(location, "inventory");
            layerModel = ir.getItemModelShaper().getModelManager().getModel(modelIdentifier);
            ir.renderModelLists(layerModel, stack, light, overlay, poseStack, buffer.getBuffer(RenderType.cutout()));
            // 每个贴图z轴有一定距离
            poseStack.translate(0, 0, 0.001);
        }
        poseStack.popPose();
    }
}
