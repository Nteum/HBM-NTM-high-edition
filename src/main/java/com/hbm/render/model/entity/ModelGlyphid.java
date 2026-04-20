package com.hbm.render.model.entity;

import com.hbm.entity.mob.EntityGlyphid;
import com.hbm.render.model.BaseObjModel;
import com.hbm.render.model.IObjModel;
import com.hbm.utils.math.BobMth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class ModelGlyphid<T extends Entity> extends EntityModel<T> implements IObjModel {
    private static final Logger LOGGER = LogManager.getLogger("HBM-ModelGlyphid");
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
    public String jaw = "jaw";  // 用于控制整个jaw的移动
    // 实体相关参数
    public float scale;
    public float[] cy = new float[2];
    
    @Override
    public void parseJson(ResourceLocation jsonPath) {
        this.rootModel = BaseObjModel.create(jsonPath, RenderType::entityCutoutNoCull);
        // 由于腿部是单独渲染，因此需要单独调它的偏移量
        try {
            LOGGER.debug("Glyphid model '{}' adjusting leg groups {}", rootModel.getModelIdentifier(), List.of(legLeftUpper, legRightUpper));
            this.rootModel.adjXYZ(0, -1.5f * getRootModel().size, 0, legLeftUpper, legRightUpper);
        } catch (Exception ex) {
            LOGGER.error("Failed to adjust glyphid model '{}' groups {}", rootModel.getModelIdentifier(), List.of(legLeftUpper, legRightUpper), ex);
        }
        rootModel.getChild(body);
        // 异虫的颚，用一个整体组件控制颚的转动
        rootModel.addChild(jaw, new BaseObjModel(rootModel, jaw).setRotPoint(0, 0.5f, 0.25f)
                .addChild(jawLeft, rootModel.popChild(jawLeft).setRotPoint(0, 0.5f, 0.25f))
                .addChild(jawRight, rootModel.popChild(jawRight).setRotPoint(0, 0.5f, 0.25f))
                .addChild(jawTop, rootModel.popChild(jawTop).setRotPoint(0, 0.5f, 0.25f))
        );
        // 右臂
        rootModel.getChild(armRightLower).addChild(armRightArmor, rootModel.popChild(armRightArmor)).setRotPoint(-0.25f, 0.625f, 0.9375f);
        rootModel.getChild(armRightMid).addChild(armRightLower, rootModel.popChild(armRightLower)).setRotPoint(-0.25f, 0.625f, 0.4375f);
        rootModel.getChild(armRightUpper).addChild(armRightMid, rootModel.popChild(armRightMid)).setRotPoint(-0.25f, 0.625f, 0.0625f);
        // 左臂
        rootModel.getChild(armLeftLower).addChild(armLeftArmor, rootModel.popChild(armLeftArmor)).setRotPoint(0.25f, 0.625f, 0.9375f);
        rootModel.getChild(armLeftMid).addChild(armLeftLower, rootModel.popChild(armLeftLower)).setRotPoint(0.25f, 0.625f, 0.4375f);
        rootModel.getChild(armLeftUpper).addChild(armLeftMid, rootModel.popChild(armLeftMid)).setRotPoint(0.25f, 0.625f, 0.0625f);
        // 两腿
        rootModel.getChild(legRightLower).setRotPoint(-0.5625f, 0.25f, 0);
        rootModel.getChild(legRightUpper).addChild(legRightLower, rootModel.popChild(legRightLower)).visible(false).setRotPoint(0, 0.25f, 0);
        rootModel.getChild(legLeftLower).setRotPoint(0.5625f, 0.25f, 0);
        rootModel.getChild(legLeftUpper).addChild(legLeftLower, rootModel.popChild(legLeftLower)).visible(false).setRotPoint(0, 0.25f, 0);

        rootModel.getChild(armorRight);
        rootModel.getChild(armorLeft);
        rootModel.getChild(armorFront);
    }
    @Override
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        this.scale = ((EntityGlyphid) pEntity).getScale() * 16;
        this.rootModel.scale(scale);
        float cy0 = (float) Math.sin(pLimbSwing % (Math.PI * 2));
        float cy1 = (float) Math.sin(pLimbSwing % (Math.PI * 2) - Math.PI * 0.5);
        float cy2 = (float) Math.sin(pLimbSwing % (Math.PI * 2) - Math.PI);
        float cy3 = (float) Math.sin(pLimbSwing % (Math.PI * 2) - Math.PI * 0.75);
        // armor
        byte armor = pEntity.getEntityData().get(EntityGlyphid.DATA_ARMOR);
        rootModel.getChild(armorFront).visible = (armor & (1)) > 0;
        rootModel.getChild(armorLeft).visible = (armor & (1 << 1)) > 0;
        rootModel.getChild(armorRight).visible = (armor & (1 << 2)) > 0;
        // head
        float headYRot = pNetHeadYaw * ((float) Math.PI / 180F);
        float headXRot = pHeadPitch * ((float) Math.PI / 180F);
        rootModel.getChild(jaw).getChild(jawTop).setRot(-headXRot + pLimbSwingAmount, headYRot, 0);
        rootModel.getChild(jaw).getChild(jawLeft).setRot(-headXRot + pLimbSwingAmount, headYRot + pLimbSwingAmount, 0);
        rootModel.getChild(jaw).getChild(jawRight).setRot(-headXRot + pLimbSwingAmount, headYRot - pLimbSwingAmount, 0);
        // left arm
        rootModel.getChild(armLeftUpper).setRot(BobMth.degree2Radians(35 + cy1 * 20), BobMth.degree2Radians(10), 0)
                .getChild(armLeftMid).setRot(BobMth.degree2Radians(-75 - cy1 * 20 + cy0 * 20), 0, 0)
                .getChild(armLeftLower).setRot(BobMth.degree2Radians(90 - cy0 * 45), 0, 0)
                .getChild(armLeftArmor).visible((armor & (1 << 3)) > 0);
        // right arm
        rootModel.getChild(armRightUpper).setRot(BobMth.degree2Radians(35 + cy2 * 20), BobMth.degree2Radians(-10), 0)
                .getChild(armRightMid).setRot(BobMth.degree2Radians(-75 - cy2 * 20 + cy3 * 20), 0, 0)
                .getChild(armRightLower).setRot(BobMth.degree2Radians(90 - cy3 * 45), BobMth.degree2Radians(10), 0)
                .getChild(armRightArmor).visible((armor & (1 << 4)) > 0);
        // leg
        cy[0] = cy0;
        cy[1] = cy1;
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.XN.rotationDegrees(180));
        this.rootModel.visible(false, legLeftUpper, legRightUpper);
        this.rootModel.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        /**
         * 渲染异虫的六条腿，我也希望能仅仅通过通用模型解决这一问题，然而并没有成功，只能硬套bob原版的逻辑。
         * */
        this.rootModel.visible(true, legLeftUpper, legRightUpper);
        float steppy = 15;
        float bend = 60;
        for (int i = 0; i < 3; i++) {
            float c0 = cy[0] * (i == 1 ? -1 : 1);
            float c1 = cy[1] * (i == 1 ? -1 : 1);

            rootModel.getChild(legLeftUpper).setRot( 0, BobMth.degree2Radians(i * 30 - 15 + c0 * 7.5f), BobMth.degree2Radians(steppy + c1 * steppy));
            rootModel.getChild(legLeftUpper).getChild(legLeftLower).setRot( 0, 0, BobMth.degree2Radians(-bend - c1 * steppy));
            this.rootModel.getChild(legLeftUpper).renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
            rootModel.getChild(legRightUpper).setRot(0, BobMth.degree2Radians(i * 30 - 45 + c0 * 7.5f), BobMth.degree2Radians(-steppy + c1 * steppy));
            rootModel.getChild(legRightUpper).getChild(legRightLower).setRot(0, 0, BobMth.degree2Radians(bend - c1 * steppy));
            this.rootModel.getChild(legRightUpper).renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }
        this.rootModel.visible(false, legLeftUpper, legRightUpper);

        pPoseStack.popPose();
    }

    @Override
    public BaseObjModel getRootModel() {
        return rootModel;
    }

    public List<String> trackedGroups() {
        return Arrays.asList(
            body,
            jawLeft,
            jawRight,
            jawTop,
            armRightUpper,
            armRightMid,
            armRightLower,
            armLeftUpper,
            armLeftMid,
            armLeftLower,
            legRightUpper,
            legRightLower,
            legLeftUpper,
            legLeftLower,
            armorRight,
            armorLeft,
            armorFront,
            armRightArmor,
            armLeftArmor,
            jaw
        );
    }
}
