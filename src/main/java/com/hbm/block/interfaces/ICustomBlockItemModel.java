package com.hbm.block.interfaces;

import net.minecraft.client.resources.model.BakedModel;

/**
 * 需要特殊建模的物品模型
 * */
public interface ICustomBlockItemModel {
    BakedModel[] getModels();
}
