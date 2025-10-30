package com.hbm.registries;

import com.hbm.HBMKey;
import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.loot.BlockLootGen;
import com.hbm.datagen.model.BlockStateGen;
import com.hbm.datagen.model.ItemModelGen;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class WrapperRegistry<T> {
    RegistryObject<T> registryObject;
    String localizedName;
    String genNameWay = HBMKey.ORDERLY_GEN;
    public T get(){
        return registryObject.get();
    }
    public ResourceLocation getId()
    {
        return registryObject.getId();
    }
    @Nullable
    public ResourceKey<T> getKey()
    {
        return registryObject.getKey();
    }

    public static class WrappedItemRegistry extends WrapperRegistry<Item>{
        ResourceKey<CreativeModeTab> creativeKey;
        String genModelWay = HBMKey.BASIC_MODEL;
        public void languageSupport(LanguageProvider provider){
            switch (genNameWay){
                case HBMKey.LITERALLY -> provider.add(get(), localizedName);
                case HBMKey.ORDERLY_GEN -> provider.add(get(), RegistryHelper.generateOrderlyName(getId().getPath()));
                case HBMKey.REVERSE_GEN -> provider.add(get(), RegistryHelper.generateReversedName(getId().getPath()));
                case HBMKey.ORDERLY_GEN_EXCEPT_FIRST -> provider.add(get(), RegistryHelper.generateOrderlyExceptFirstName(getId().getPath()));
                default -> provider.add(get(), getId().toLanguageKey());
            }
        }

        public void creativeTabSupport(BuildCreativeModeTabContentsEvent event){
            if (event.getTabKey() == this.creativeKey){
                event.getEntries().put(new ItemStack(get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }

        public void modelSupport(ItemModelGen provider){
            if (genModelWay.equals(HBMKey.BASIC_MODEL)){
                provider.basicItem(get());
            }else if (genModelWay.equals(HBMKey.SPAWN_EGG_MODEL)){
                provider.withExistingParent(localizedName, "minecraft:item/template_spawn_egg");
            }
        }
    }

    public static class WrappedBlockRegistry extends WrapperRegistry<Block> {
        ResourceKey<CreativeModeTab> creativeKey;
        String genModelWay = HBMKey.BASIC_MODEL;
        String lootWay = HBMKey.DROP_SELF;
        public void languageSupport(LanguageProvider provider){
            switch (genNameWay){
                case HBMKey.LITERALLY -> provider.add(get(), localizedName);
                case HBMKey.ORDERLY_GEN -> provider.add(get(), RegistryHelper.generateOrderlyName(getId().getPath()));
                case HBMKey.REVERSE_GEN -> provider.add(get(), RegistryHelper.generateReversedName(getId().getPath()));
                case HBMKey.ORDERLY_GEN_EXCEPT_FIRST -> provider.add(get(), RegistryHelper.generateOrderlyExceptFirstName(getId().getPath()));
                default -> provider.add(get(), getId().toLanguageKey());
            }
        }

        public void creativeTabSupport(BuildCreativeModeTabContentsEvent event){
            if (event.getTabKey() == this.creativeKey){
                event.getEntries().put(new ItemStack(get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }

        public void modelSupport(BlockStateGen provider){
            if (genModelWay.equals(HBMKey.CUBE_ALL_MODEL)){
                provider.cubeAll(get());
            }
        }

        public void lootSupport(BlockLootGen provider){
            switch (lootWay){
                case HBMKey.DROP_SELF -> provider.dropSelf(registryObject.get());
                case HBMKey.DROP_NONE -> provider.add(registryObject.get(), BlockLootSubProvider.noDrop());
            }
        }
    }
}
