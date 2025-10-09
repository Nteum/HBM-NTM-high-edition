package com.hbm.render.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ModelArmorDesh extends ModelArmorBase{
    public ModelArmorDesh() {
        super("Head","Body","LeftArm","RightArm","LeftLeg","RightLeg","LeftBoot","RightBoot");
        armTexExtra = new ResourceLocation("hbm:textures/models/armor/steamsuit_arm.png");
    }

//    @Override
//    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
//        super.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
//        if (this.cbody.visible){
//            VertexConsumer buffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.armorCutoutNoCull(new ResourceLocation("hbm:textures/models/armor/steamsuit_arm.png")));
//            this.cleftArm.render(pPoseStack, buffer, pPackedLight, pPackedOverlay);
//            this.crightArm.render(pPoseStack, buffer, pPackedLight, pPackedOverlay);
//        }
//    }
}
