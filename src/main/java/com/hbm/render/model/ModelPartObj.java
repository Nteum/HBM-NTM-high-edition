package com.hbm.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import org.joml.Quaternionf;

import java.util.*;

/**
 * 模仿ModelPart做的控制obj文件的功能
 * */
public class ModelPartObj {
    public float x;
    public float y;
    public float z;
    public float xRot;
    public float yRot;
    public float zRot;
    public float xScale = 1.0F;
    public float yScale = 1.0F;
    public float zScale = 1.0F;
    public boolean visible = true;
    public boolean skipDraw;
    private PartPose initialPose = PartPose.ZERO;
    public List<AccessableRenderable.Component> components;
    public Map<String, ModelPartObj> children;
    public ModelPartObj(AccessableRenderable.Component core, AccessableRenderable.Component ... componentList){
        components = new ArrayList<>();
        children = new HashMap<>();
        components.add(core);
        for (AccessableRenderable.Component component : componentList) {
            children.put(component.name, new ModelPartObj(component, component.children.toArray(new AccessableRenderable.Component[0])));
        }
    }
    public PartPose storePose() {
        return PartPose.offsetAndRotation(this.x, this.y, this.z, this.xRot, this.yRot, this.zRot);
    }

    public PartPose getInitialPose() {
        return this.initialPose;
    }

    public void setInitialPose(PartPose pInitialPose) {
        this.initialPose = pInitialPose;
    }

    public void resetPose() {
        this.loadPose(this.initialPose);
    }

    public void loadPose(PartPose pPartPose) {
        this.x = pPartPose.x;
        this.y = pPartPose.y;
        this.z = pPartPose.z;
        this.xRot = pPartPose.xRot;
        this.yRot = pPartPose.yRot;
        this.zRot = pPartPose.zRot;
        this.xScale = 1.0F;
        this.yScale = 1.0F;
        this.zScale = 1.0F;
    }

    public void copyFrom(ModelPart pModelPart) {
        this.xScale = pModelPart.xScale;
        this.yScale = pModelPart.yScale;
        this.zScale = pModelPart.zScale;
        this.xRot = pModelPart.xRot;
        this.yRot = pModelPart.yRot;
        this.zRot = pModelPart.zRot;
        this.x = pModelPart.x;
        this.y = pModelPart.y;
        this.z = pModelPart.z;
    }

    public boolean hasChild(String pName) {
        return this.children.containsKey(pName);
    }

    public ModelPartObj getChild(String pName) {
        ModelPartObj modelpart = this.children.get(pName);
        if (modelpart == null) {
            throw new NoSuchElementException("Can't find part " + pName);
        } else {
            return modelpart;
        }
    }

    public void setPos(float pX, float pY, float pZ) {
        this.x = pX;
        this.y = pY;
        this.z = pZ;
    }

    public void setRotation(float pXRot, float pYRot, float pZRot) {
        this.xRot = pXRot;
        this.yRot = pYRot;
        this.zRot = pZRot;
    }

    public void translateAndRotate(PoseStack pPoseStack) {
        pPoseStack.translate(this.x / 16.0F, this.y / 16.0F, this.z / 16.0F);
        if (this.xRot != 0.0F || this.yRot != 0.0F || this.zRot != 0.0F) {
            pPoseStack.mulPose((new Quaternionf()).rotationZYX(this.zRot, this.yRot, this.xRot));
        }

        if (this.xScale != 1.0F || this.yScale != 1.0F || this.zScale != 1.0F) {
            pPoseStack.scale(this.xScale, this.yScale, this.zScale);
        }
    }

    public void render(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay) {
        this.render(pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void render(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        if (this.visible) {
//            if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
//                pPoseStack.pushPose();
//                this.translateAndRotate(pPoseStack);
//                if (!this.skipDraw) {
//                    this.compile(pPoseStack.last(), pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
//                }
//
//                for(ModelPart modelpart : this.children.values()) {
//                    modelpart.render(pPoseStack, pVertexConsumer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
//                }
//
//                pPoseStack.popPose();
//            }
        }
    }
}
