package com.hbm.render.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ModelArmorDesh extends ModelArmorBase{
    public ModelArmorDesh() {
        super("Head","Body","LeftArm","RightArm","LeftLeg","RightLeg","LeftBoot","RightBoot");
        armTexExtra = true;
    }

//    @Override
//    public void initializeParts() {
//        this.chead = this.accRenderable.components.get("Head");
//        this.cbody = this.accRenderable.components.get("Body");
//        this.cleftArm = this.accRenderable.components.get("LeftArm").setRotPoint(0, 2f, 1.5f);
//        this.crightArm = this.accRenderable.components.get("RightArm").setRotPoint(0, 2f, 1.5f);
//        this.cleftLeg = this.accRenderable.components.get("LeftLeg").setRotPoint(0, 12, 0);
//        this.crightLeg = this.accRenderable.components.get("RightLeg").setRotPoint(0, 12, 0);
//        this.cleftFoot = this.accRenderable.components.get("LeftBoot").setRotPoint(0, 12, 0);
//        this.crightFoot = this.accRenderable.components.get("RightBoot").setRotPoint(0, 12, 0);
//    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        super.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        if (this.cbody.visible){
            VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.armorCutoutNoCull(new ResourceLocation("hbm:textures/models/armor/steamsuit_arm.png")));
            this.cleftArm.render(pPoseStack, buffer, pPackedLight, pPackedOverlay);
            this.crightArm.render(pPoseStack, buffer, pPackedLight, pPackedOverlay);
        }
    }
}
