package com.hbm.render.model.armor;

import com.hbm.render.model.AccessableRenderable;
import com.hbm.render.model.BaseObjModel;
import com.hbm.render.model.IObjModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.IRenderable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * HBM自定义obj盔甲的渲染方式
 * */
public class ModelArmorBase<T extends LivingEntity> extends HumanoidArmorModel<T> implements IObjModel
{
    private static final Logger LOGGER = LogManager.getLogger("HBM-ModelArmor");
    static ModelPart EMPTY = new ModelPart(List.of(), Map.of());
    static ModelPart DUMMY_HUMANOID = new ModelPart(List.of(), Map.of("head",EMPTY, "hat",EMPTY, "body", EMPTY, "right_arm",EMPTY, "left_arm",EMPTY, "right_leg", EMPTY, "left_leg", EMPTY));
    public BaseObjModel rootModel;
    // 身体部分
    public List<String> names;
    public BaseObjModel chead;
    public BaseObjModel cbody;
    public BaseObjModel cleftArm;
    public BaseObjModel crightArm;
    public BaseObjModel cleftLeg;
    public BaseObjModel crightLeg;
    public BaseObjModel cleftFoot;
    public BaseObjModel crightFoot;

    protected ResourceLocation armTexExtra = null;

    public ModelArmorBase(String ... strings) {
        // 初始构造先用一个空的model撑一下，后面直接复制已有的model
        super(DUMMY_HUMANOID);
        names = new ArrayList<>();
        names.addAll(Arrays.stream(strings).toList());
    }

    @Override
    public BaseObjModel getRootModel() {
        return rootModel;
    }

    public BaseObjModel getComponent(String name){
        if (this.rootModel == null) {
            LOGGER.error("ModelArmorBase attempted to access component '{}' before model initialization.", name);
            BaseObjModel placeholder = new BaseObjModel(RenderType::armorCutoutNoCull, name);
            placeholder.setModelIdentifier("armor::placeholder::" + name);
            return placeholder;
        }
        return this.rootModel.getChild(name);
    }

    public void parseJson(ResourceLocation jsonPath) {
        this.rootModel = BaseObjModel.create(jsonPath, RenderType::armorCutoutNoCull);
        initializeParts();
    }
    public void initializeParts(){
        this.chead = getComponent(names.get(0));
        this.cbody = getComponent(names.get(1));
        this.cleftArm = getComponent(names.get(2)).setRotPoint(0, 2.2f, 2.5f);
        this.crightArm = getComponent(names.get(3)).setRotPoint(0, 2.0f, 2.0f);
        this.cleftLeg = getComponent(names.get(4)).setRotPoint(0, 12, 0);
        this.crightLeg = getComponent(names.get(5)).setRotPoint(0, 12, 0);
        if (names.size() >= 7) this.cleftFoot = getComponent(names.get(6)).setRotPoint(0, 12, 0);
        if (names.size() >= 8) this.crightFoot = getComponent(names.get(7)).setRotPoint(0, 12, 0);
    }

    public ModelArmorBase<?> adjustWithOrigin(HumanoidModel<?> origin, EquipmentSlot equipmentSlot){
        this.chead.copyPose(origin.hat);
        this.cbody.copyPose(origin.body);
        // 对双臂位置进行微调
        this.cleftArm.copyPose(origin.leftArm).adjXYZ(-5.2f, -2.2f, 0);
        this.crightArm.copyPose(origin.rightArm).adjXYZ(5.2f, -1.8f, 0);
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
        if (this.armTexExtra != null){
            this.cleftArm.bindTexture(this.armTexExtra);
            this.crightArm.bindTexture(this.armTexExtra);
        }
//        this.rootModel.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
//        super.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.cleftFoot.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.crightFoot.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.cleftLeg.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.crightLeg.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.cbody.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.chead.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.cleftArm.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        this.crightArm.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
//        if (this.cbody.visible) {
//            if (this.armTexExtra != null){
//                // 通过局部BufferSource渲染
//                MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
//                try {
//                    VertexConsumer buffer = bufferSource.getBuffer(RenderType.armorCutoutNoCull(this.armTexExtra));
//                    this.cleftArm.render(pPoseStack, buffer, pPackedLight, pPackedOverlay);
//                    this.crightArm.render(pPoseStack, buffer, pPackedLight, pPackedOverlay);
//                } finally {
//                    bufferSource.endBatch();
//                }
//            } else {
//                this.cleftArm.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
//                this.crightArm.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
//            }
//        }
    }
    
    public void setObjVisible(boolean visible){
//        this.accRenderable.components.forEach((s, component) -> component.visible = visible);
        this.rootModel.children.forEach((s, component) -> component.visible = visible);
    }
}
