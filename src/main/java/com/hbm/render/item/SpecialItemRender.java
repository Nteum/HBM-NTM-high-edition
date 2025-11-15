package com.hbm.render.item;

import com.hbm.HBM;
import com.hbm.item.HBMCombat;
import com.hbm.item.HBMWeapon;
import com.hbm.item.ItemBlockCustomModel;
import com.hbm.item.armor.ItemArmorFSB;
import com.hbm.item.armor.ItemArmorT51;
import com.hbm.item.weapon.ItemGun;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.Models;
import com.hbm.render.model.armor.ModelArmorT51;
import com.hbm.render.model.item.SimpleBakedModelWrapper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ArmorItem;
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
        pPoseStack.pushPose();

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

        pPoseStack.popPose();
    }
}
