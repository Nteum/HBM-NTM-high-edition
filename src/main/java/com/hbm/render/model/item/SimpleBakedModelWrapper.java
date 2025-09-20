package com.hbm.render.model.item;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
/**
 * SimpleBakedModel 的包装类，用于物品渲染
 * 其实说白了就是为了把它设成自定义模型 Custom Model
 * */
public class SimpleBakedModelWrapper implements BakedModel {
    SimpleBakedModel model;
    public SimpleBakedModelWrapper(SimpleBakedModel model){
        this.model = model;
    }
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, RandomSource pRandom) {
        return model.getQuads(pState, pDirection, pRandom);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return model.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return model.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return model.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return model.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return model.getOverrides();
    }
}
