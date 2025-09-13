package com.hbm.render.entity.missile;

import com.hbm.entity.weapon.missile.EntityMissile;
import com.hbm.entity.weapon.missile.EntityMissileTier0.EntityMissileTest;
import com.hbm.main.ResourceManager;
import com.hbm.render.model.Models;
import com.hbm.render.model.entity.ObjEntityModelSingle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
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

        pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot())));
        pPoseStack.mulPose(Axis.XN.rotationDegrees(Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot()) - 90));

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
