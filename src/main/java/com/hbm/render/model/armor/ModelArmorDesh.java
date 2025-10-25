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
}
