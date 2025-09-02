package com.hbm.render.entity.missile;

import com.hbm.entity.weapon.missile.EntityMissile;
import com.hbm.entity.weapon.missile.EntityMissileTier0.*;
import com.hbm.main.ResourceManager;
import com.hbm.render.model.Models;
import com.hbm.render.model.entity.ObjEntityModelSingle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MissileTaintRenderer extends EntityRenderer<EntityMissile> {
    public static ObjEntityModelSingle rocket_model;
    public MissileTaintRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        rocket_model = (ObjEntityModelSingle) Models.getEntityModel(Models.MISSILE_MICRO);
    }

    @Override
    public void render(EntityMissile pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
//        pPoseStack.translate(pEntity.getX(), pEntity.getY(), pEntity.getZ());
//        pPoseStack.mulPose(Axis.YP.rotation(Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot()) - Mth.HALF_PI));
//        pPoseStack.mulPose(Axis.ZP.rotation(Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot())));
//        pPoseStack.mulPose(Axis.YN.rotation(Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot()) - Mth.HALF_PI));
//
//        if (pEntity instanceof EntityMissile) switch (pEntity.getEntityData().get(EntityMissile.DATA_MISSILE_1)){
//            case 2: pPoseStack.mulPose(Axis.YP.rotationDegrees(90)); break;
//            case 4: pPoseStack.mulPose(Axis.YP.rotationDegrees(180)); break;
//            case 3: pPoseStack.mulPose(Axis.YP.rotationDegrees(270)); break;
//            case 5: pPoseStack.mulPose(Axis.YP.rotationDegrees(0)); break;
//        }

        pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot())));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot()) - 90));

        RenderSystem.enableCull();
        // 因为entity没有packetoverlay，因此这里设为0
        rocket_model.renderModel(pPoseStack, pBuffer, pPackedLight, pPartialTick);

        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityMissile pEntity) {
//        if(pEntity instanceof EntityMissileTaint)
//            return ResourceManager.missileMicroTaint_tex;
//        if(pEntity instanceof EntityMissileBHole)
//            return ResourceManager.missileMicroBHole_tex;
//        if(pEntity instanceof EntityMissileSchrabidium)
//            return ResourceManager.missileMicroSchrab_tex;
//        if(pEntity instanceof EntityMissileEMP)
//            return ResourceManager.missileMicroEMP_tex;
        if(pEntity instanceof EntityMissileTest)
            return ResourceManager.missileMicroTest_tex;

        return ResourceManager.missileMicroTaint_tex;
    }
}
