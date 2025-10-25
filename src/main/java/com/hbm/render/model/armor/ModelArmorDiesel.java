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
        armTexExtra = new ResourceLocation("hbm:textures/models/armor/bnuuy_arm.png");
    }
}
