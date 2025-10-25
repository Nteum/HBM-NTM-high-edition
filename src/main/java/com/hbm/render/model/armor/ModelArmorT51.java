package com.hbm.render.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ModelArmorT51 extends ModelArmorBase{
    public ModelArmorT51() {
        super("Helmet","Chest","LeftArm","RightArm","LeftLeg","RightLeg","LeftBoot","RightBoot");
        armTexExtra = new ResourceLocation("hbm:textures/models/armor/t51_arm.png");
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        super.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }
}
