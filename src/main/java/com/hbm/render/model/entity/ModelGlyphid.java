package com.hbm.render.model.entity;

import com.hbm.entity.mob.EntityGlyphid;
import com.hbm.main.ResourceManager;
import com.hbm.render.model.BaseObjModel;
import com.hbm.render.model.IObjModel;
import com.hbm.utils.BobMth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ModelGlyphid<T extends Entity> extends EntityModel<T> implements IObjModel {
    public BaseObjModel rootModel;
    // 身体部分
    public String body = "Body";
    public String jawLeft = "JawLeft";
    public String jawRight = "JawRight";
    public String jawTop = "JawTop";
    public String armRightUpper = "ArmRightUpper";
    public String armRightMid = "ArmRightMid";
    public String armRightLower = "ArmRightLower";
    public String armLeftUpper = "ArmLeftUpper";
    public String armLeftMid = "ArmLeftMid";
    public String armLeftLower = "ArmLeftLower";
    public String legRightUpper = "LegRightUpper";
    public String legRightLower = "LegRightLower";
    public String legLeftUpper = "LegLeftUpper";
    public String legLeftLower = "LegLeftLower";
    public String armorRight = "ArmorRight";
    public String armorLeft = "ArmorLeft";
    public String armorFront = "ArmorFront";
    public String armRightArmor = "ArmRightArmor";
    public String armLeftArmor = "ArmLeftArmor";
    // 实体相关参数
    public float scale;
    public float[] cy = new float[2];
    
    @Override
    public void parseJson(ResourceLocation jsonPath) {
        this.rootModel = BaseObjModel.create(jsonPath, RenderType::entityCutoutNoCull);
        // 由于腿部是单独渲染，因此需要单独调它的偏移量
        this.rootModel.adjXYZ(0, -1.5f * getRootModel().size, 0, legLeftLower, legLeftUpper, legRightLower, legRightUpper);
//        this.rootModel.children.forEach((name, child) -> child.scale(16, 16, 16));
        rootModel.getChild(body);
        rootModel.getChild(jawLeft).setRotPoint(0, 0.5f, 0.25f);
        rootModel.getChild(jawRight).setRotPoint(0, 0.5f, 0.25f);
        rootModel.getChild(jawTop).setRotPoint(0, 0.5f, 0.25f);
        rootModel.getChild(armRightUpper).setRotPoint(-0.25f, 0.625f, 0.0625f);
        rootModel.getChild(armRightMid).setRotPoint(-0.25f, 0.625f, 0.4375f);
        rootModel.getChild(armRightLower).setRotPoint(-0.25f, 0.625f, 0.9375f);
        rootModel.getChild(armLeftUpper).setRotPoint(0.25f, 0.625f, 0.0625f);
        rootModel.getChild(armLeftMid).setRotPoint(0.25f, 0.625f, 0.4375f);
        rootModel.getChild(armLeftLower).setRotPoint(0.25f, 0.625f, 0.9375f);
        rootModel.getChild(legRightUpper).visible(false);
//                .adjXYZ(-3, 4, 0)
//                .setRotPoint(0, 0.25f, 0);
        rootModel.getChild(legRightLower).visible(false);
//                .adjXYZ(-3, 12, 0)
//                .setRotPoint(-0.5625f, 0.25f, 0);
        rootModel.getChild(legLeftUpper).visible(false);
//                .adjXYZ(3, 4, 0)
//                .setRotPoint(0, 0.25f, 0);
        rootModel.getChild(legLeftLower).visible(false);
//                .adjXYZ(3, 12, 0)
//                .setRotPoint(0.5625f, 0.25f, 0);
        rootModel.getChild(armorRight);
        rootModel.getChild(armorLeft);
        rootModel.getChild(armorFront);
        rootModel.getChild(armRightArmor).setRotPoint(-0.25f, 0.625f, 0.9375f);
        rootModel.getChild(armLeftArmor).setRotPoint(0.25f, 0.625f, 0.9375f);
    }
    @Override
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        this.scale = ((EntityGlyphid) pEntity).getScale() * 16;
        this.rootModel.scale(scale);
        float cy0 = (float) Math.sin(pLimbSwing % (Math.PI * 2));
        float cy1 = (float) Math.sin(pLimbSwing % (Math.PI * 2) - Math.PI * 0.5);
        float cy2 = (float) Math.sin(pLimbSwing % (Math.PI * 2) - Math.PI);
        float cy3 = (float) Math.sin(pLimbSwing % (Math.PI * 2) - Math.PI * 0.75);
//        // armor
//        byte armor = pEntity.getEntityData().get(EntityGlyphid.DATA_ARMOR);
//        rootModel.getChild(armorFront).visible = (armor & (1)) > 0;
//        rootModel.getChild(armorLeft).visible = (armor & (1 << 1)) > 0;
//        rootModel.getChild(armorRight).visible = (armor & (1 << 2)) > 0;
//        rootModel.getChild(armLeftArmor).visible = (armor & (1 << 3)) > 0;
//        rootModel.getChild(armRightArmor).visible = (armor & (1 << 4)) > 0;
//        // head
//        float headYRot = pNetHeadYaw * ((float) Math.PI / 180F);
//        float headXRot = pHeadPitch * ((float) Math.PI / 180F);
//        rootModel.getChild(jawTop).setRot(-headXRot + pLimbSwingAmount, headYRot, 0);
//        rootModel.getChild(jawLeft).setRot(-headXRot + pLimbSwingAmount, headYRot + pLimbSwingAmount, 0);
//        rootModel.getChild(jawRight).setRot(-headXRot + pLimbSwingAmount, headYRot - pLimbSwingAmount, 0);
//        // left arm
//        rootModel.getChild(armLeftUpper).setRot(35 + cy1 * 20, 10, 0);
//        rootModel.getChild(armLeftMid).setRot(-75 - cy1 * 20 + cy0 * 20, 0, 0);
//        rootModel.getChild(armLeftLower).setRot(90 - cy0 * 45, 0, 0);
//        // right arm
//        rootModel.getChild(armRightUpper).setRot(35 + cy2 * 20, -10, 0);
//        rootModel.getChild(armRightMid).setRot(-75 - cy2 * 20 + cy3 * 20, 0, 0);
//        rootModel.getChild(armRightLower).setRot(90 - cy3 * 45, 10, 0);
        // leg
        cy[0] = cy0;
        cy[1] = cy1;
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.XN.rotationDegrees(180));
        this.rootModel.visible(false, legLeftLower, legLeftUpper, legRightLower, legRightUpper);
        this.rootModel.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        /**
         * 渲染异虫的六条腿，我也希望能仅仅通过通用模型解决这一问题，然而并没有成功，只能硬套bob原版的逻辑。
         * */
        this.rootModel.visible(true, legLeftLower, legLeftUpper, legRightLower, legRightUpper);
        pPoseStack.translate(0, -1.5, 0);
        float steppy = 15;
        float bend = 60;
        for (int i = 0; i < 3; i++) {
            float c0 = cy[0] * (i == 1 ? -1 : 1);
            float c1 = cy[1] * (i == 1 ? -1 : 1);
            pPoseStack.pushPose();
            pPoseStack.translate(0, 0.25f, 0);
            pPoseStack.mulPose(Axis.YP.rotationDegrees(i * 30 - 15 + c0 * 7.5f));
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(steppy + c1 * steppy));
            pPoseStack.translate(0, -0.25f, 0);
//            rootModel.getChild(legLeftUpper).setRot( 0, BobMth.degree2Radians(i * 30 - 15 + c0 * 7.5f), BobMth.degree2Radians(steppy + c1 * steppy));
//            this.rootModel.getChild(legLeftUpper).renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
            this.rootModel.getChild(legLeftUpper).renderStatic(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
            pPoseStack.translate(0.5625, 0.25, 0);
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(-bend - c1 * steppy));
            pPoseStack.translate(-0.5625, -0.25, 0);
//            rootModel.getChild(legLeftLower).setRot( 0, BobMth.degree2Radians(i * 30 - 15 + c0 * 7.5f), BobMth.degree2Radians(-bend - c1 * steppy));
//            this.rootModel.getChild(legLeftLower).renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
            this.rootModel.getChild(legLeftLower).renderStatic(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
            pPoseStack.popPose();

            pPoseStack.pushPose();
            pPoseStack.translate(0, 0.25f, 0);
            pPoseStack.mulPose(Axis.YP.rotationDegrees(i * 30 - 45 + c0 * 7.5f));
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(-steppy + c1 * steppy));
            pPoseStack.translate(0, -0.25f, 0);
//            rootModel.getChild(legRightUpper).setRot(0, BobMth.degree2Radians(i * 30 - 45 + c0 * 7.5f), BobMth.degree2Radians(-steppy + c1 * steppy));
//            this.rootModel.getChild(legRightUpper).renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
            this.rootModel.getChild(legRightUpper).renderStatic(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
            pPoseStack.translate(-0.5625, 0.25, 0);
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(bend - c1 * steppy));
            pPoseStack.translate(0.5625, -0.25, 0);
//            rootModel.getChild(legRightLower).setRot(0, BobMth.degree2Radians(i * 30 - 45 + c0 * 7.5f), BobMth.degree2Radians(bend - c1 * steppy));
//            this.rootModel.getChild(legRightLower).renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
            this.rootModel.getChild(legRightLower).renderStatic(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
            pPoseStack.popPose();
        }
        this.rootModel.visible(false, legLeftLower, legLeftUpper, legRightLower, legRightUpper);

        pPoseStack.popPose();
    }

    @Override
    public BaseObjModel getRootModel() {
        return rootModel;
    }
}
