package com.hbm.render.model.armor;

import com.hbm.HBM;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.AccessableRenderable;
import com.hbm.render.model.BaseObjModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public class ModelArmorBJ extends ModelArmorBase{
    public boolean showJet = false;
    public BaseObjModel jetpack;
    public static ResourceLocation jetpackTex = HBM.rl("textures/models/armor/bj_jetpack.png");
    public ModelArmorBJ(){
        super("Head","Body","LeftArm","RightArm","LeftLeg","RightLeg","LeftFoot","RightFoot","Jetpack");
        this.armTexExtra = HBM.rl("textures/models/armor/bj_arm.png");
    }

    public void setShowJet(boolean b){
        this.showJet = b;
    }

    @Override
    public void initializeParts() {
        super.initializeParts();
        if (names.size() >= 9) this.jetpack = getComponent((String) names.get(8));
    }

    @Override
    public ModelArmorBase<?> adjustWithOrigin(HumanoidModel origin, EquipmentSlot equipmentSlot) {
        super.adjustWithOrigin(origin, equipmentSlot);
        if (showJet && equipmentSlot == EquipmentSlot.CHEST){
            this.jetpack.copyPose(origin.body);
            this.jetpack.visible = true;
        }
        return this;
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        super.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        if (showJet){
            this.jetpack.bindTexture(jetpackTex);
            this.jetpack.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }
    }
}
