package com.hbm.render.model.entity;

import com.hbm.entity.mob.EntityGlyphid;
import com.hbm.main.ResourceManager;
import com.hbm.render.model.BaseObjModel;
import com.hbm.render.model.IObjModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ModelGlyphid<T extends Entity> extends EntityModel<T> implements IObjModel {
    public BaseObjModel rootModel;
    // 身体部分
    public BaseObjModel body;
    public BaseObjModel jawLeft;
    public BaseObjModel jawRight;
    public BaseObjModel jawTop;
    public BaseObjModel armRightUpper;
    public BaseObjModel armRightMid;
    public BaseObjModel armRightLower;
    public BaseObjModel armLeftUpper;
    public BaseObjModel armLeftMid;
    public BaseObjModel armLeftLower;
    public BaseObjModel legRightUpper;
    public BaseObjModel legRightLower;
    public BaseObjModel legLeftUpper;
    public BaseObjModel legLeftLower;
    public BaseObjModel armorRight;
    public BaseObjModel armorLeft;
    public BaseObjModel armorFront;
    public BaseObjModel armRightArmor;
    public BaseObjModel armLeftArmor;
    
    @Override
    public void parseJson(ResourceLocation jsonPath) {
        this.rootModel = BaseObjModel.create(jsonPath, RenderType::entityCutoutNoCull);
        this.body = rootModel.getChild("Body");
        jawLeft = rootModel.getChild("JawLeft").setRotPoint(0, 0.5f, 0.25f);
        jawRight = rootModel.getChild("JawRight").setRotPoint(0, 0.5f, 0.25f);
        jawTop = rootModel.getChild("JawTop").setRotPoint(0, 0.5f, 0.25f);
        armRightUpper = rootModel.getChild("ArmRightUpper").setRotPoint(-0.25f, 0.625f, 0.0625f);
        armRightMid = rootModel.getChild("ArmRightMid").setRotPoint(-0.25f, 0.625f, 0.0625f);
        armRightLower = rootModel.getChild("ArmRightLower").setRotPoint(-0.25f, 0.625f, 0.0625f);
        armLeftUpper = rootModel.getChild("ArmLeftUpper").setRotPoint(0.25f, 0.625f, 0.0625f);
        armLeftMid = rootModel.getChild("ArmLeftMid").setRotPoint(0.25f, 0.625f, 0.0625f);
        armLeftLower = rootModel.getChild("ArmLeftLower").setRotPoint(0.25f, 0.625f, 0.0625f);
        legRightUpper = rootModel.getChild("LegRightUpper").setRotPoint(0, 0.25f, 0);
        legRightLower = rootModel.getChild("LegRightLower").setRotPoint(-0.5625f, 0.25f, 0);
        legLeftUpper = rootModel.getChild("LegLeftUpper").setRotPoint(0, 0.25f, 0);
        legLeftLower = rootModel.getChild("LegLeftLower").setRotPoint(0.5625f, 0.25f, 0);
        armorRight = rootModel.getChild("ArmorRight");
        armorLeft = rootModel.getChild("ArmorLeft");
        armorFront = rootModel.getChild("ArmorFront");
        armRightArmor = rootModel.getChild("ArmRightArmor");
        armLeftArmor = rootModel.getChild("ArmLeftArmor");
    }
    @Override
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        Byte armor = pEntity.getEntityData().get(EntityGlyphid.DATA_ARMOR);
        float bite = 0;
        float headTilt = (float) (Math.sin(pLimbSwing * Math.PI) * 30);
        if((armor & (1 << 0)) > 0) armorFront.visible = true;
        if((armor & (1 << 1)) > 0) armorLeft.visible = true;
        if((armor & (1 << 2)) > 0) armorRight.visible = true;
        float walkCycle = pLimbSwing;
        float cy0 = (float) Math.sin(walkCycle % (Math.PI * 2));
        float cy1 = (float) Math.sin(walkCycle % (Math.PI * 2) - Math.PI * 0.5);
        float cy2 = (float) Math.sin(walkCycle % (Math.PI * 2) - Math.PI);
        float cy3 = (float) Math.sin(walkCycle % (Math.PI * 2) - Math.PI * 0.75);
        /// LEFT ARM ///
        armLeftUpper.setRot((float) (35 + cy1 * 20), 10, 0);
        armLeftMid.setRot((float) (-75 - cy1 * 20 + cy0 * 20), 0, 0);
        armLeftLower.setRot((float) (90 - cy0 * 45), 0, 0);
        if((armor & (1 << 3)) > 0) armLeftArmor.visible = true;
        /// RIGHT ARM ///
        armRightUpper.setRot((float) (35 + cy2 * 20), -10, 0);
        armRightMid.setRot((float) (-75 - cy2 * 20 + cy3 * 20), 0, 0);
        armRightLower.setRot((float) (90 - cy3 * 45), 10, 0);
        if((armor & (1 << 4)) > 0) armRightArmor.visible = true;
        /// JAW ///
        jawTop.setRot(-bite, 0, headTilt);
        jawLeft.setRot(bite, bite, headTilt);
        jawRight.setRot(bite, bite, headTilt);
        /// LEG ///
        float steppy = 15;
        float bend = 60;
        legLeftUpper.setRot((float) 0, (float) (- 15 + cy0 * 7.5), steppy + cy1 * steppy);
        legLeftLower.setRot((float) 0, (float) (- 15 + cy0 * 7.5), steppy + cy1 * steppy -bend - cy1 * steppy);
        legRightUpper.setRot((float) 0, (float) (- 45 + cy0 * 7.5), -steppy + cy1 * steppy);
        legRightLower.setRot((float) 0, (float) (- 45 + cy0 * 7.5), -steppy + cy1 * steppy + bend - cy1 * steppy);
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        this.body.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.jawTop.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.jawLeft.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.jawRight.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.armLeftUpper.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.armLeftMid.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.armLeftLower.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.armRightUpper.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.armRightMid.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.armRightLower.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);

        float steppy = 15;
        float bend = 60;
        float cy0 = (float) ((legLeftUpper.yRot + 15) / 7.5);
        float cy1 = (float) ((legLeftUpper.zRot - steppy) / steppy);
        for (int i = 0; i < 3; i++) {
            float c0 = cy0 * (i == 1 ? -1 : 1);
            float c1 = cy1 * (i == 1 ? -1 : 1);
            legLeftUpper.setRot((float) 0, (float) (i * 30 - 15 + c0 * 7.5), steppy + c1 * steppy);
            this.legLeftUpper.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
            legLeftLower.setRot((float) 0, (float) (i * 30 - 15 + c0 * 7.5), steppy + c1 * steppy - bend - c1 * steppy);
            this.legLeftLower.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
            legLeftUpper.setRot((float) 0, (float) (i * 30 - 45 + c0 * 7.5), -steppy + c1 * steppy);
            this.legRightUpper.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
            legLeftUpper.setRot((float) 0, (float) (i * 30 - 45 + c0 * 7.5), -steppy + c1 * steppy + bend - c1 * steppy);
            this.legRightLower.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }
    }

    @Override
    public BaseObjModel getRootModel() {
        return rootModel;
    }
}
