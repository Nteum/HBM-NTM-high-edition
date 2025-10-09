package com.hbm.render.model.armor;

import com.hbm.render.model.AccessableRenderable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import org.lwjgl.opengl.GL11;

public class ModelArmorRPA extends ModelArmorBase{
    public static ResourceLocation chestplateTex = new ResourceLocation("hbm:textures/models/armor/rpa_chest.png");
    AccessableRenderable.Component fan;
    AccessableRenderable.Component glow;
    public ModelArmorRPA() {
        super("Head","Body","LeftArm","RightArm","LeftLeg","RightLeg","LeftBoot","RightBoot","Fan","Glow");
        armTexExtra = new ResourceLocation("hbm:textures/models/armor/rpa_arm.png");
    }

    @Override
    public void initializeParts() {
        super.initializeParts();
        if (names.size() >= 9){
            this.fan = this.accRenderable.components.get(this.names.get(8)).setRotPoint(0, 4.875f, 0);
            this.glow = this.accRenderable.components.get(this.names.get(9));
        }
    }

    @Override
    public ModelArmorBase<?> adjustWithOrigin(HumanoidModel origin, EquipmentSlot equipmentSlot) {
        super.adjustWithOrigin(origin, equipmentSlot);
        this.fan.visible = this.glow.visible = this.cbody.visible;
        this.fan.copyPose(origin.body);
        this.glow.copyPose(origin.body);
        return this;
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        super.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        if (this.cbody.visible){
            /// START GLOW ///
            MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            try {
                VertexConsumer buffer = bufferSource.getBuffer(RenderType.eyes(chestplateTex));
                this.glow.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
            } finally {
                bufferSource.endBatch();
            }

//            float lastX = OpenGlHelper.lastBrightnessX;
//            float lastY = OpenGlHelper.lastBrightnessY;
//            GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
//            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
//            GL11.glDisable(GL11.GL_LIGHTING);
//            this.glow.render(scaleFactor);
//            GL11.glEnable(GL11.GL_LIGHTING);
//            GL11.glPopAttrib();
//            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastX, lastY);

            /// END GLOW ///

            /// START FAN ///
            this.fan.zRot = (float) (-System.currentTimeMillis() / 2D % 360);
            this.fan.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
            /// END FAN ///
        }
    }
}
