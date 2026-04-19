package com.hbm.render.entity.missile;

import com.hbm.entity.weapon.missile.EntityMissileAntiBallistic;
import com.hbm.main.ResourceManager;
import com.hbm.render.model.Models;
import com.hbm.render.model.entity.ObjEntityModelSingle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MissileABMRenderer extends EntityRenderer<EntityMissileAntiBallistic> {

    public MissileABMRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntityMissileAntiBallistic entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        ObjEntityModelSingle model = (ObjEntityModelSingle) Models.getEntityModel(Models.MISSILE_ABM);
        if (model == null || model.getRootModel() == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot())));
        poseStack.mulPose(Axis.XN.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) - 90));
        model.renderModel(poseStack, buffer, packedLight, partialTick);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityMissileAntiBallistic entity) {
        return ResourceManager.missileAA_tex;
    }
}
