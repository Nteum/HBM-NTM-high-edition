package com.hbm.render.model.armor;

import net.minecraft.resources.ResourceLocation;

public class ModelArmorAJR extends ModelArmorBase{
    public ModelArmorAJR() {
        super("Head","Body","LeftArm","RightArm","LeftLeg","RightLeg","LeftFoot","RightFoot","RocketBox");
        this.armTexExtra = new ResourceLocation("hbm:textures/models/armor/ajr_arm.png");
    }
    public ModelArmorAJR name(String nameStr){
        this.armTexExtra = new ResourceLocation("hbm:textures/models/armor/" + nameStr + "_arm.png");
        return this;
    }
}
