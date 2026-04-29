package com.hbm.render.blockentity;

import com.hbm.blockentity.weapon.NukeBombCustomEntity;
import com.hbm.render.model.Models;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;

import static com.hbm.render.RenderUtils.renderBlockModel;

public class NukeCustomRender extends MultiPartRenderer<NukeBombCustomEntity> {
    public static BakedModel bomb_model;
    public static BakedModel boy_model;
    public static BakedModel fat_man_model;
    public static BakedModel gadget_model;
    public static BakedModel mike_model;
    public static BakedModel tsar_model;
    public static BakedModel fleija_model;
    public static BakedModel solinium_model;
    public static BakedModel prototype_model;
    public static BakedModel multi_model;

    public NukeCustomRender(BlockEntityRendererProvider.Context pContext){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        bomb_model = modelManager.getModel(Models.CUSTOM_NUKE);
        boy_model = modelManager.getModel(Models.BOY);
        fat_man_model = modelManager.getModel(Models.FAT_MAN);
        gadget_model = modelManager.getModel(Models.CUSTOM_NUKE_GADGET);
        mike_model = modelManager.getModel(Models.CUSTOM_NUKE_MIKE);
        tsar_model = modelManager.getModel(Models.CUSTOM_NUKE_TSAR);
        fleija_model = modelManager.getModel(Models.CUSTOM_NUKE_FLEIJA);
        solinium_model = modelManager.getModel(Models.CUSTOM_NUKE_SOLINIUM);
        prototype_model = modelManager.getModel(Models.CUSTOM_NUKE_PROTOTYPE);
        multi_model = modelManager.getModel(Models.CUSTOM_NUKE_MULTI);
    }
//    @Override
//    public void render(NukeBombCustomEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
//        BlockState blockState = pBlockEntity.getBlockState();
//        BlockRenderDispatcher blockDispatcher = Minecraft.getInstance().getBlockRenderer();
//        ModelBlockRenderer blockRenderer = blockDispatcher.getModelRenderer();
//        //根据方向确定旋转角度
//        Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
//        int rotation = 0;
//        switch (direction){
//            case NORTH -> rotation = 0;
//            case WEST -> rotation = 90;
//            case SOUTH -> rotation = 180;
//            case EAST -> rotation = 270;
//        }
//
//        //渲染核弹模型
//        pPoseStack.pushPose();
//        pPoseStack.mulPose(Axis.YP.rotationDegrees(rotation));
//        renderBlockModel(bomb_model,blockState,blockRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
//        pPoseStack.popPose();
//    }

    @Override
    public void renderMultiPart(NukeBombCustomEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockState blockState = pBlockEntity.getBlockState();
        BlockRenderDispatcher blockDispatcher = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer blockRenderer = blockDispatcher.getModelRenderer();
        renderBlockModel(modelFor(pBlockEntity.getProfile()),blockState,blockRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
    }

    private BakedModel modelFor(NukeBombCustomEntity.CustomNukeProfile profile) {
        return switch (profile) {
            case BOY -> boy_model;
            case FAT_MAN -> fat_man_model;
            case GADGET -> gadget_model;
            case MIKE -> mike_model;
            case TSAR -> tsar_model;
            case FLEIJA -> fleija_model;
            case SOLINIUM -> solinium_model;
            case PROTOTYPE -> prototype_model;
            case MULTI -> multi_model;
            case DEFAULT -> bomb_model;
        };
    }
}
