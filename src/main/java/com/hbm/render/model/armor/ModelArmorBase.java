package com.hbm.render.model.armor;

import com.hbm.render.model.AccessableRenderable;
import com.hbm.render.model.IObjModel;
import com.hbm.render.model.ModelRendererObj;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.IRenderable;

import java.util.List;
import java.util.Map;

/**
 * HBM自定义obj盔甲的渲染方式
 * */
public class ModelArmorBase<T extends LivingEntity> extends HumanoidModel<T> implements IObjModel {
    public CompositeRenderable renderable;
    public AccessableRenderable accRenderable;

    public ModelRendererObj head;
    public ModelRendererObj body;
    public ModelRendererObj leftArm;
    public ModelRendererObj rightArm;
    public ModelRendererObj leftLeg;
    public ModelRendererObj rightLeg;
    public ModelRendererObj leftFoot;
    public ModelRendererObj rightFoot;

    public ModelArmorBase() {
        // 初始构造先用一个空的model撑一下，后面直接复制已有的model
        super(DUMMY_HUMANOID);
    }

    @Override
    public IRenderable getRenderable() {
        return renderable;
    }

    @Override
    public void setRenderable(IRenderable renderable) {
        this.renderable = (CompositeRenderable) renderable;
    }

    @Override
    public void parseJson(ResourceLocation jsonPath) {
        IObjModel.super.parseJson(jsonPath);
        this.accRenderable = new AccessableRenderable(renderable);

    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        super.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }
}
