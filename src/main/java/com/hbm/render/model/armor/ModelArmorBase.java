package com.hbm.render.model.armor;

import com.hbm.render.model.AccessableRenderable;
import com.hbm.render.model.IObjModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.IRenderable;

/**
 * HBM自定义obj盔甲的渲染方式
 * */
public class ModelArmorBase<T extends LivingEntity> extends HumanoidArmorModel<T> implements IObjModel {
    public CompositeRenderable renderable;
    public AccessableRenderable accRenderable;
    // 身体部分
    public AccessableRenderable.Component chead;
    public AccessableRenderable.Component cbody;
    public AccessableRenderable.Component cleftArm;
    public AccessableRenderable.Component crightArm;
    public AccessableRenderable.Component cleftLeg;
    public AccessableRenderable.Component crightLeg;
    public AccessableRenderable.Component cleftFoot;
    public AccessableRenderable.Component crightFoot;

    public ModelArmorBase() {
        // 初始构造先用一个空的model撑一下，后面直接复制已有的model
        super(DUMMY_HUMANOID);
    }

    @Override
    public IRenderable getRenderable() {
        return renderable;
    }

    @Override
    public void setRenderable(IRenderable renderable) {
        this.renderable = (CompositeRenderable) renderable;
    }

    @Override
    public void parseJson(ResourceLocation jsonPath) {
        IObjModel.super.parseJson(jsonPath);
        this.accRenderable = new AccessableRenderable(renderable);
        initializeParts();
    }
    public void initializeParts(){
        this.chead = this.accRenderable.components.get("Helmet");
        this.cbody = this.accRenderable.components.get("Chest");
        this.cleftArm = this.accRenderable.components.get("LeftArm");
        this.crightArm = this.accRenderable.components.get("RightArm");
        this.cleftLeg = this.accRenderable.components.get("LeftLeg").setRotPoint(0, 12, 0);
        this.crightLeg = this.accRenderable.components.get("RightLeg").setRotPoint(0, 12, 0);
        this.cleftFoot = this.accRenderable.components.get("LeftBoot").setRotPoint(0, 12, 0);
        this.crightFoot = this.accRenderable.components.get("RightBoot").setRotPoint(0, 12, 0);
    }

    public ModelArmorBase<?> adjustWithOrigin(HumanoidModel<?> origin){
        this.chead.copyPose(origin.hat);
        this.cbody.copyPose(origin.body);
        // 对双臂位置进行微调
        this.cleftArm.copyPose(origin.leftArm);
        this.crightArm.copyPose(origin.rightArm);
        this.cleftArm.x = 0;
        this.crightArm.x = 0;
        // 对双腿位置进行微调
        this.cleftLeg.copyPose(origin.leftLeg).resetX().resetY();
        this.crightLeg.copyPose(origin.rightLeg).resetX().resetY();
        this.cleftFoot.copyPose(origin.leftLeg).resetX().resetY();
        this.crightFoot.copyPose(origin.rightLeg).resetX().resetY();
        return this;
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
//        super.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.chead.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.cbody.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.cleftArm.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.crightArm.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.cleftLeg.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.crightLeg.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.cleftFoot.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.crightFoot.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
    }
}
