package com.hbm.render.model.armor;

import com.hbm.render.model.AccessableRenderable;
import com.hbm.render.model.IObjModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.IRenderable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * HBM自定义obj盔甲的渲染方式
 * */
public class ModelArmorBase<T extends LivingEntity> extends HumanoidArmorModel<T> implements IObjModel {
    public CompositeRenderable renderable;
    public AccessableRenderable accRenderable;
    // 身体部分
    public List<String> names;
    public AccessableRenderable.Component chead;
    public AccessableRenderable.Component cbody;
    public AccessableRenderable.Component cleftArm;
    public AccessableRenderable.Component crightArm;
    public AccessableRenderable.Component cleftLeg;
    public AccessableRenderable.Component crightLeg;
    public AccessableRenderable.Component cleftFoot;
    public AccessableRenderable.Component crightFoot;
    // flags
    protected boolean armTexExtra = false;

    public ModelArmorBase(String ... strings) {
        // 初始构造先用一个空的model撑一下，后面直接复制已有的model
        super(DUMMY_HUMANOID);
        names = new ArrayList<>();
        names.addAll(Arrays.stream(strings).toList());
    }

    @Override
    public IRenderable getRenderable() {
        return renderable;
    }

    @Override
    public void setRenderable(IRenderable renderable) {
        this.renderable = (CompositeRenderable) renderable;
    }
    
    public AccessableRenderable.Component getComponent(String name){
        return this.accRenderable.components.get(name);
    }

    @Override
    public void parseJson(ResourceLocation jsonPath) {
        IObjModel.super.parseJson(jsonPath);
        this.accRenderable = new AccessableRenderable(renderable);
        initializeParts();
    }
    public void initializeParts(){
        this.chead = this.accRenderable.components.get(names.get(0));
        this.cbody = this.accRenderable.components.get(names.get(1));
        this.cleftArm = this.accRenderable.components.get(names.get(2)).setRotPoint(0, 1.9f, 2.5f);
        this.crightArm = this.accRenderable.components.get(names.get(3)).setRotPoint(0, 1.9f, 2.5f);
        this.cleftLeg = this.accRenderable.components.get(names.get(4)).setRotPoint(0, 12, 0);
        this.crightLeg = this.accRenderable.components.get(names.get(5)).setRotPoint(0, 12, 0);
        if (names.size() >= 7) this.cleftFoot = this.accRenderable.components.get(names.get(6)).setRotPoint(0, 12, 0);
        if (names.size() >= 8) this.crightFoot = this.accRenderable.components.get(names.get(7)).setRotPoint(0, 12, 0);
    }

    public ModelArmorBase<?> adjustWithOrigin(HumanoidModel<?> origin, EquipmentSlot equipmentSlot){
        this.chead.copyPose(origin.hat);
        this.cbody.copyPose(origin.body);
        // 对双臂位置进行微调
        this.cleftArm.copyPose(origin.leftArm).adjXYZ(-5.2f, -2.1f, 0);
        this.crightArm.copyPose(origin.rightArm).adjXYZ(5.2f, -2f, 0);
        // 对双腿位置进行微调
        this.cleftLeg.copyPose(origin.leftLeg).resetX().resetY();
        this.crightLeg.copyPose(origin.rightLeg).resetX().resetY();
        this.cleftFoot.copyPose(origin.leftLeg).resetX().resetY();
        this.crightFoot.copyPose(origin.rightLeg).resetX().resetY();
        // 修改HumanoidArmorLayer中对可见性不可理喻的设计
        this.setObjVisible(false);
        switch (equipmentSlot) {
            case HEAD:
                this.chead.visible = true;
                break;
            case CHEST:
                this.cbody.visible = true;
                this.crightArm.visible = true;
                this.cleftArm.visible = true;
                break;
            case LEGS:
                this.crightLeg.visible = true;
                this.cleftLeg.visible = true;
                break;
            case FEET:
                this.cleftFoot.visible = true;
                this.crightFoot.visible = true;
        }
        return this;
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
//        super.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.cleftFoot.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.crightFoot.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.cleftLeg.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.crightLeg.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.cbody.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.chead.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        if (!this.armTexExtra){
            this.cleftArm.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
            this.crightArm.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        }
    }
    
    public void setObjVisible(boolean visible){
        this.accRenderable.components.forEach((s, component) -> component.visible = visible);
    }
}
