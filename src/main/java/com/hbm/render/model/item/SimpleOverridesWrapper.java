package com.hbm.render.model.item;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class SimpleOverridesWrapper extends SimpleBakedModelWrapper {
    private final BakedItemOverrides manualOverrides;

    public SimpleOverridesWrapper(SimpleBakedModel original, BakedItemOverrides itemOverrides) {
        super(original);
        // 创建真正的 ItemOverrides 对象
        // 参数依次为：模型烘焙器, 原始模型, 模型管理, 以及我们的手动列表
        this.manualOverrides = itemOverrides;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.manualOverrides;
    }

    public static class BakedItemOverrides extends ItemOverrides {
        private BakedOverride[] overrides;
        private ResourceLocation[] properties;
        int cnt = 0;
        public BakedItemOverrides(Map<ItemOverride, BakedModel> itemOverrides){
            List<ItemOverride> overrideList = itemOverrides.keySet().stream().toList();
            properties = overrideList.stream().flatMap(ItemOverride::getPredicates).map(ItemOverride.Predicate::getProperty).distinct().toArray(ResourceLocation[]::new);
            Object2IntMap<ResourceLocation> object2intmap = new Object2IntOpenHashMap<>();

            for(int i = 0; i < this.properties.length; ++i) {
                object2intmap.put(this.properties[i], i);
            }

            List<BakedOverride> list = Lists.newArrayList();

            for(int j = overrideList.size() - 1; j >= 0; --j) {
                ItemOverride itemoverride = overrideList.get(j);
                PropertyMatcher[] aitemoverrides$propertymatcher = itemoverride.getPredicates().map((p_173477_) -> {
                    int k = object2intmap.getInt(p_173477_.getProperty());
                    return new PropertyMatcher(k, p_173477_.getValue());
                }).toArray(PropertyMatcher[]::new);
                list.add(new BakedOverride(aitemoverrides$propertymatcher, itemOverrides.get(itemoverride)));
            }

            this.overrides = list.toArray(new BakedOverride[0]);
        }

        @Override
        public com.google.common.collect.ImmutableList<BakedOverride> getOverrides() {
            return com.google.common.collect.ImmutableList.copyOf(overrides);
        }
    }
}
