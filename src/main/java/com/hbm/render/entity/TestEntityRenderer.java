package com.hbm.render.entity;

import com.hbm.HBM;
import com.hbm.entity.TestEntity;
import com.hbm.render.model.entity.TestEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class TestEntityRenderer extends EntityRenderer {
    // 存储我们的模型。
    private EntityModel<TestEntity> testEntityModel;

    public TestEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        //这里使用pContext.bakeLayer(FlyingSwordModel.LAYER_LOCATION)来准备ModelPart，这里的获得ModelPart是等会我们需要去注册的，通过LAYER_LOCATION注册我们的ModelPart
        testEntityModel = new TestEntityModel(pContext.bakeLayer(TestEntityModel.LAYER_LOCATION));
    }
    //这个方法返回一个ResourceLocation对象，指明了飞行剑实体的纹理文件位置。
    @Override
    public ResourceLocation getTextureLocation(Entity pEntity) {
        return new ResourceLocation(HBM.MODID, "textures/entity/test_entity_texture.png");
    }
    //重写了render方法，这个方法定义了实体在游戏中的渲染逻辑。
    @Override
    public void render(Entity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        //你的渲染应该在posh和pop之间，避免污染其他的渲染。
        pPoseStack.pushPose();
        // 绕y轴旋转45°
        pPoseStack.mulPose(Axis.YN.rotationDegrees(45));
        // 向下移动1格
        pPoseStack.translate(0,-1,0);
        // 构建顶点
        VertexConsumer buffer = pBuffer.getBuffer(this.testEntityModel.renderType(this.getTextureLocation(pEntity)));
        // 调用模型的render方法进行渲染，这里的OverlayTexture下有很多类型，自己选用。
        this.testEntityModel.renderToBuffer(pPoseStack,buffer,pPackedLight, OverlayTexture.NO_OVERLAY,1f,1f,1f,1f);
        pPoseStack.popPose();
    }
}
