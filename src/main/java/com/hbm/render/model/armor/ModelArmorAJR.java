package com.hbm.render.model.armor;

import com.hbm.render.model.AccessableRenderable;
import net.minecraft.resources.ResourceLocation;

public class ModelArmorAJR extends ModelArmorBase{
    public AccessableRenderable.Component rocket_box;
    public ModelArmorAJR() {
        super("Head","Body","LeftArm","RightArm","LeftLeg","RightLeg","LeftBoot","RightBoot"
//                ,"RocketBox"
        );
        this.armTexExtra = new ResourceLocation("hbm:textures/models/armor/ajr_arm.png");
    }

    @Override
    public void initializeParts() {
        super.initializeParts();
        if (names.size() >= 9) this.rocket_box = this.accRenderable.components.get(names.get(9));
    }
}
