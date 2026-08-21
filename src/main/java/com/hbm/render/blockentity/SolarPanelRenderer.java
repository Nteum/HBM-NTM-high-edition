package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.SolarPanelEntity;
import com.hbm.render.model.Models;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static com.hbm.render.RenderUtils.renderModel;

/**
 * 太阳能板 TESR。渲染 OBJ 模型（solar_panel 分件），按朝向旋转。
 */
public class SolarPanelRenderer implements BlockEntityRenderer<SolarPanelEntity> {
    private final BakedModel model;

    public SolarPanelRenderer(Context pContext){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        this.model = modelManager.getModel(Models.SOLAR_PANEL);
    }

    @Override
    public void render(SolarPanelEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        BlockState state = entity.getBlockState();
        Direction facing = state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? state.getValue(BlockStateProperties.HORIZONTAL_FACING) : Direction.SOUTH;

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        DirectionUtils.generalMachineRotate(poseStack, state);
        renderModel(model, poseStack, buffer, light, overlay, RenderType.cutout());
        poseStack.popPose();
    }
}
