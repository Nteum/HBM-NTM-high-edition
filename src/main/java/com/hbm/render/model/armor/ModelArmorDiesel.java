package com.hbm.render.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public class ModelArmorDiesel extends ModelArmorBase{
    public ModelArmorDiesel() {
        super("Head","Body","LeftArm","RightArm","LeftLeg","RightLeg","LeftBoot","RightBoot");
        armTexExtra = true;
    }
//
//    @Override
//    public void initializeParts() {
//        this.chead = this.accRenderable.components.get("Head");
//        this.cbody = this.accRenderable.components.get("Body");
//        this.cleftArm = this.accRenderable.components.get("LeftArm");
//        this.crightArm = this.accRenderable.components.get("RightArm");
//        this.cleftLeg = this.accRenderable.components.get("LeftLeg").setRotPoint(0, 12, 0);
//        this.crightLeg = this.accRenderable.components.get("RightLeg").setRotPoint(0, 12, 0);
//        this.cleftFoot = this.accRenderable.components.get("LeftBoot").setRotPoint(0, 12, 0);
//        this.crightFoot = this.accRenderable.components.get("RightBoot").setRotPoint(0, 12, 0);
//    }

    @Override
    public ModelArmorBase<?> adjustWithOrigin(HumanoidModel origin, EquipmentSlot equipmentSlot) {
        super.adjustWithOrigin(origin, equipmentSlot);
        this.cleftArm.adjXYZ(0, -0.5f, 0);
        this.crightArm.adjXYZ(0, -0.5f, 0);
        return this;
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        super.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        if (this.cbody.visible){
            VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.armorCutoutNoCull(new ResourceLocation("hbm:textures/models/armor/bnuuy_arm.png")));
            this.cleftArm.render(pPoseStack, buffer, pPackedLight, pPackedOverlay);
            this.crightArm.render(pPoseStack, buffer, pPackedLight, pPackedOverlay);
        }
    }
}
