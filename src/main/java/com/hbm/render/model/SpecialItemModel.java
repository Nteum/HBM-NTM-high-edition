package com.hbm.render.model;

import com.hbm.render.item.SpecialItemRender;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraftforge.client.RenderTypeGroup;

import java.util.List;
import java.util.Map;

public class SpecialItemModel extends SimpleBakedModel {
//    public SpecialItemRender(SimpleBakedModel model){
//        Class<? extends SimpleBakedModel> clazz = model.getClass();
//        List<BakedQuad> unculledFaces = (List<BakedQuad>)clazz.getDeclaredField("unculledFaces").get(List.class);
//        Map<Direction, List<BakedQuad>> culledFaces = (Map<Direction, List<BakedQuad>>)clazz.getDeclaredField("culledFaces").get(Map.class);
//        List<BakedQuad> unculledFaces = (List<BakedQuad>)clazz.getDeclaredField("unculledFaces").get(List.class);
//        List<BakedQuad> unculledFaces = (List<BakedQuad>)clazz.getDeclaredField("unculledFaces").get(List.class);
//        List<BakedQuad> unculledFaces = (List<BakedQuad>)clazz.getDeclaredField("unculledFaces").get(List.class);
//
//    }
    public SpecialItemModel(List<BakedQuad> pUnculledFaces, Map<Direction, List<BakedQuad>> pCulledFaces, boolean pHasAmbientOcclusion, boolean pUsesBlockLight, boolean pIsGui3d, TextureAtlasSprite pParticleIcon, ItemTransforms pTransforms, ItemOverrides pOverrides, RenderTypeGroup renderTypes) {
        super(pUnculledFaces, pCulledFaces, pHasAmbientOcclusion, pUsesBlockLight, pIsGui3d, pParticleIcon, pTransforms, pOverrides, renderTypes);
    }
//    public SpecialItemModel(SimpleBakedModel simpleBakedModel){
//    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }
}
