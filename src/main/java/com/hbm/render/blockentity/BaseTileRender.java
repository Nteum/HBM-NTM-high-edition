package com.hbm.render.blockentity;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BaseTileRender<T extends BlockEntity> implements BlockEntityRenderer<T> {
    protected BakedModel model;
    public BaseTileRender(BlockEntityRendererProvider.Context pContext){}
}
