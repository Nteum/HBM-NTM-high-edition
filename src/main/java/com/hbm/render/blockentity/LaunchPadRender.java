package com.hbm.render.blockentity;

import com.hbm.blockentity.weapon.LaunchPadTileEntity;
import com.hbm.item.weapon.ItemMissile;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.Models;
import com.hbm.render.model.entity.ObjEntityModelSingle;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;

public class LaunchPadRender implements BlockEntityRenderer<LaunchPadTileEntity> {
    public static BakedModel launchpad;
    public LaunchPadRender(BlockEntityRendererProvider.Context pContext){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        launchpad = modelManager.getModel(Models.LAUNCH_PAD);
    }
    @Override
    public void render(LaunchPadTileEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();

        BlockState blockState = pBlockEntity.getBlockState();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer modelRenderer = blockRenderer.getModelRenderer();
        pPoseStack.translate(-0.5f, 0, -0.5f);
        DirectionUtils.generalMachineRotate(pPoseStack, blockState);
        RenderUtils.renderBlockModel(launchpad,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);

        if(pBlockEntity.toRender != null && pBlockEntity.toRender.getItem() instanceof ItemMissile missile && missile.model != null) {
            if (missile.model.get() instanceof ObjEntityModelSingle objEntityModelSingle){
                pPoseStack.translate(0,pBlockEntity.getLaunchOffset(),0);
                objEntityModelSingle.renderModel(pPoseStack, pBuffer, pPackedLight, pPackedOverlay);

            }
        }

        pPoseStack.popPose();
    }
}
