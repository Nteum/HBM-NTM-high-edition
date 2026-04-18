package com.hbm.render.entity.missile;

import com.hbm.entity.weapon.missile.EntityMissile;
import com.hbm.entity.weapon.missile.EntityMissileTier0.EntityMissileTest;
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

public class MissileTaintRenderer extends EntityRenderer<EntityMissile> {

    public MissileTaintRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntityMissile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        ObjEntityModelSingle model = resolveModel(entity);
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
    public ResourceLocation getTextureLocation(EntityMissile entity) {
        if (!(entity instanceof EntityMissileTest missile)) {
            return ResourceManager.missileMicroTest_tex;
        }
        return switch (missile.getPayload()) {
            case TEST -> ResourceManager.missileMicroTest_tex;
            case MICRO -> ResourceManager.missileMicro_tex;
            case EMP -> ResourceManager.missileMicroEMP_tex;
            case BHOLE -> ResourceManager.missileMicroBHole_tex;
            case SCHRABIDIUM -> ResourceManager.missileMicroSchrab_tex;
            case TAINT -> ResourceManager.missileMicroTaint_tex;
            case GENERIC, REJUVENATION -> ResourceManager.missileV2_HE_tex;
            case DECOY -> ResourceManager.missileV2_decoy_tex;
            case INCENDIARY -> ResourceManager.missileV2_IN_tex;
            case CLUSTER -> ResourceManager.missileV2_CL_tex;
            case BUSTER -> ResourceManager.missileV2_BU_tex;
            case STRONG -> ResourceManager.missileStrong_HE_tex;
            case EMP_STRONG -> ResourceManager.missileStrong_EMP_tex;
            case INCENDIARY_STRONG -> ResourceManager.missileStrong_IN_tex;
            case CLUSTER_STRONG -> ResourceManager.missileStrong_CL_tex;
            case BUSTER_STRONG -> ResourceManager.missileStrong_BU_tex;
            case BURST -> ResourceManager.missileHuge_HE_tex;
            case INFERNO -> ResourceManager.missileHuge_IN_tex;
            case RAIN -> ResourceManager.missileHuge_CL_tex;
            case DRILL -> ResourceManager.missileHuge_BU_tex;
            case NUCLEAR -> ResourceManager.missileNuclear_tex;
            case NUCLEAR_CLUSTER -> ResourceManager.missileThermo_tex;
            case VOLCANO -> ResourceManager.missileVolcano_tex;
            case DOOMSDAY -> ResourceManager.missileDoomsday_tex;
            case DOOMSDAY_RUSTED -> ResourceManager.missileDoomsdayRusted_tex;
            case STEALTH -> ResourceManager.missileStealth_tex;
            case SHUTTLE, SOYUZ -> ResourceManager.missileShuttle_tex;
        };
    }

    private ObjEntityModelSingle resolveModel(EntityMissile entity) {
        ResourceLocation modelId = Models.MISSILE_TEST;
        if (entity instanceof EntityMissileTest missile) {
            modelId = switch (missile.getPayload()) {
                case TEST -> Models.MISSILE_TEST;
                case MICRO -> Models.MISSILE_MICRO;
                case EMP -> Models.MISSILE_MICRO_EMP;
                case BHOLE -> Models.MISSILE_MICRO_BHOLE;
                case SCHRABIDIUM -> Models.MISSILE_MICRO_SCHRAB;
                case TAINT -> Models.MISSILE_MICRO_TAINT;
                case GENERIC, REJUVENATION -> Models.MISSILE_V2_GENERIC;
                case DECOY -> Models.MISSILE_V2_DECOY;
                case INCENDIARY -> Models.MISSILE_V2_INCENDIARY;
                case CLUSTER -> Models.MISSILE_V2_CLUSTER;
                case BUSTER -> Models.MISSILE_V2_BUSTER;
                case STRONG -> Models.MISSILE_STRONG_GENERIC;
                case EMP_STRONG -> Models.MISSILE_STRONG_EMP;
                case INCENDIARY_STRONG -> Models.MISSILE_STRONG_INCENDIARY;
                case CLUSTER_STRONG -> Models.MISSILE_STRONG_CLUSTER;
                case BUSTER_STRONG -> Models.MISSILE_STRONG_BUSTER;
                case BURST -> Models.MISSILE_HUGE_GENERIC;
                case INFERNO -> Models.MISSILE_HUGE_INFERNO;
                case RAIN -> Models.MISSILE_HUGE_RAIN;
                case DRILL -> Models.MISSILE_HUGE_DRILL;
                case NUCLEAR -> Models.MISSILE_ATLAS_NUCLEAR;
                case NUCLEAR_CLUSTER -> Models.MISSILE_ATLAS_THERMO;
                case VOLCANO -> Models.MISSILE_ATLAS_VOLCANO;
                case DOOMSDAY -> Models.MISSILE_ATLAS_DOOMSDAY;
                case DOOMSDAY_RUSTED -> Models.MISSILE_ATLAS_DOOMSDAY_RUSTED;
                case STEALTH -> Models.MISSILE_STEALTH;
                case SHUTTLE, SOYUZ -> Models.MISSILE_ATLAS_SHUTTLE;
            };
        }
        return (ObjEntityModelSingle) Models.getEntityModel(modelId);
    }
}
