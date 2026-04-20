package com.hbm.registries;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.loot.BlockLootGen;
import com.hbm.datagen.model.BlockStateGen;
import com.hbm.datagen.model.ItemModelGen;
import com.hbm.datagen.tag.BlockTagsGen;
import com.hbm.datagen.tag.ItemTagsGen;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class WrapperRegistry<T> implements Supplier<T>{
    RegistryObject<T> registryObject;
    String localizedName;
    // 根据id生成本地语言名的方式或者就是本地语言名本身。
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
        Consumer<ItemModelGen> modelFactory;
        List<TagKey<Item>> tags;
        public void languageSupport(LanguageProvider provider){
            switch (genNameWay){
                case HBMKey.LITERALLY -> provider.add(get(), localizedName);
                case HBMKey.ORDERLY_GEN -> provider.add(get(), RegistryHelper.generateOrderlyName(getId().getPath()));
                case HBMKey.REVERSE_GEN -> provider.add(get(), RegistryHelper.generateReversedName(getId().getPath()));
                case HBMKey.ORDERLY_GEN_EXCEPT_FIRST -> provider.add(get(), RegistryHelper.generateOrderlyExceptFirstName(getId().getPath()));
                case HBMKey.GEN_STANDALONE -> {
                    return;
                }
                // 如何无法匹配上，则视为直接输入的翻译内容
                default -> provider.add(get(), genNameWay);
            }
        }

        public void creativeTabSupport(BuildCreativeModeTabContentsEvent event){
            if (event.getTabKey() == this.creativeKey){
                Item item = get();
                if (item instanceof com.hbm.item.CreativeTabVariantItem variantItem) {
                    variantItem.fillCreativeTab(event);
                } else {
                    event.getEntries().put(new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }
        }

        public void modelSupport(ItemModelGen provider){
            switch (genModelWay) {
                case HBMKey.BASIC_MODEL -> provider.basicItem(get());
                case HBMKey.SPAWN_EGG_MODEL ->
                        provider.withExistingParent(localizedName, "minecraft:item/template_spawn_egg");
                case HBMKey.MODEL_STANDALONE -> {
                    if (modelFactory != null) modelFactory.accept(provider);
                }
                case HBMKey.MODEL_DYNAMIC -> {

                }
            }
        }

        public void tagSupport(ItemTagsGen provider){
            if (this.tags == null || this.tags.isEmpty()) return;
            for (TagKey<Item> tag : this.tags) {
                provider.tag(tag).add(registryObject.get());
            }
        }
    }

    private static abstract class Builder<T>{
        final String name;
        final Supplier<? extends T> sup;
        String genNameWay = HBMKey.ORDERLY_GEN;
        String localizedName;

        Builder(String name, Supplier<? extends T> sup){
            this.name = name;
            this.sup = sup;
        }

        public abstract RegistryObject<T> build();
    }

    public static class ItemBuilder extends Builder<Item>{
        ResourceKey<CreativeModeTab> creativeKey;
        String genModelWay = HBMKey.BASIC_MODEL;
        Consumer<ItemModelGen> modelGen;
        // 动态物品模型参数
        String propertyName;
        Supplier<Boolean> condition;
        List<TagKey<Item>> tags;
        public ItemBuilder(String name, Supplier<? extends Item> sup) {
            super(name, sup);
        }

        public ItemBuilder tab(ResourceKey<CreativeModeTab> tabKey){
            creativeKey = tabKey;
            return this;
        }
        // 直接指定模型生成函数
        public ItemBuilder model(Consumer<ItemModelGen> modelGen){
            this.genModelWay = HBMKey.MODEL_STANDALONE;
            this.modelGen = modelGen;
            return this;
        }

        public ItemBuilder model(String genModelWay){
            this.genModelWay = genModelWay;
            return this;
        }

        /**
         * 新增方法：配置文件控制贴图
         * @param propertyName 客户端 property 名称，对应模型 overrides 的 predicate 名
         * @param condition 返回 true 或 false，根据配置切换贴图
         */
        public ItemBuilder withConfigTexture(String propertyName, Supplier<Boolean> condition) {
            this.propertyName = propertyName;
            this.condition = condition;
            this.genModelWay = HBMKey.MODEL_DYNAMIC;
            return this;
        }
        public ItemBuilder loc(String genNameWay){
            this.genNameWay = genNameWay;
            return this;
        }
        @SafeVarargs
        public final ItemBuilder tags(TagKey<Item>... itemTags){
            if (itemTags.length > 0){
                this.tags = Arrays.stream(itemTags).toList();
            }
            return this;
        }
        @Override
        public RegistryObject<Item> build() {
            RegistryObject<Item> existing = findExistingItem(name);
            if (existing != null) {
                return existing;
            }
            WrappedItemRegistry itemRegistry = new WrappedItemRegistry();
            itemRegistry.registryObject = ModItems.ITEMS.register(name, sup);
            itemRegistry.creativeKey = creativeKey;
            itemRegistry.genModelWay = genModelWay;
            itemRegistry.genNameWay = genNameWay;
            itemRegistry.modelFactory = modelGen;
            itemRegistry.tags = tags;
            if (itemRegistry.genNameWay!= null && itemRegistry.genNameWay.equals(HBMKey.LITERALLY) && localizedName!=null)
                itemRegistry.localizedName = localizedName;
            ModItems.itemList.add(itemRegistry);
            return itemRegistry.registryObject;
        }

        private static RegistryObject<Item> findExistingItem(String name) {
            ResourceLocation id = new ResourceLocation(HBM.MODID, name);
            for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
                ResourceLocation entryId = entry.getId();
                if (id.equals(entryId)) {
                    return entry;
                }
            }
            return null;
        }
    }

    public static class WrappedBlockRegistry extends WrapperRegistry<Block> {
        ResourceKey<CreativeModeTab> creativeKey;
        String genModelWay = HBMKey.MODEL_CUBE_ALL;
        String lootWay = HBMKey.DROP_SELF;
        BiConsumer<Block, BlockStateGen> modelFactory;
        List<TagKey<Block>> tags;
        public void languageSupport(LanguageProvider provider){
            switch (genNameWay){
                case HBMKey.LITERALLY -> provider.add(get(), localizedName);
                case HBMKey.ORDERLY_GEN -> provider.add(get(), RegistryHelper.generateOrderlyName(getId().getPath()));
                case HBMKey.REVERSE_GEN -> provider.add(get(), RegistryHelper.generateReversedName(getId().getPath()));
                case HBMKey.ORDERLY_GEN_EXCEPT_FIRST -> provider.add(get(), RegistryHelper.generateOrderlyExceptFirstName(getId().getPath()));
                case HBMKey.GEN_STANDALONE -> {
                    return;
                }
                default -> provider.add(get(), genNameWay);
            }
        }

        public void creativeTabSupport(BuildCreativeModeTabContentsEvent event){
            if (event.getTabKey() == this.creativeKey){
                event.getEntries().put(new ItemStack(get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }

        public void modelSupport(BlockStateGen provider){
            switch (genModelWay) {
                case HBMKey.MODEL_CUBE_ALL -> provider.simpleBlockWithItem(get());
                case HBMKey.MODEL_CUBE_TOP -> provider.simpleBlockWithItem(get(), provider.genBuiltInModelFile(get(), "cube_top"));
                case HBMKey.MODEL_PILLAR -> {
                    provider.logBlock((RotatedPillarBlock) get());
                    provider.simpleBlockItem(get(), new ModelFile.UncheckedModelFile(provider.key(get()).withPrefix("block/")));
                }
                case HBMKey.MODEL_EXISTING -> provider.simpleBlockWithItem(get(), provider.genBuiltInModelFile(get(), "existing"));
                case HBMKey.MODEL_CUBE_BOTTOM_TOP -> provider.simpleBlockWithItem(get(), provider.genBuiltInModelFile(get(), "cube_bottom_top"));
                case HBMKey.MODEL_LEAVES -> provider.simpleBlockWithItem(get(), provider.genBuiltInModelFile(get(), "leaves"));
//                case HBMKey.MODEL_FRONT_SIDE -> provider.frontSideBlockWithItem(get());
//                case HBMKey.MODEL_FRONT_SIDE_TOP -> provider.frontSideTopBlockWithItem(get());
//                case HBMKey.MODEL_DIFURNACE -> provider.difuranceBlockWithItem(get());
//                case HBMKey.MODEL_HORIZONTAL_WITH_FILE -> provider.addObjHorizonalModel(get());
                default -> {
                    if (modelFactory instanceof BiConsumer<Block, BlockStateGen>) modelFactory.accept(get(), provider);
                }
            }
        }

        public void lootSupport(BlockLootGen provider){
            switch (lootWay){
                case HBMKey.DROP_SELF -> provider.dropSelf(registryObject.get());
                case HBMKey.DROP_NONE -> provider.add(registryObject.get(), BlockLootSubProvider.noDrop());
            }
        }
        public void tagSupport(BlockTagsGen blockTagsGen){
            if (this.tags == null) return;
            for (TagKey<Block> tag : this.tags) {
                blockTagsGen.tag(tag).add(get());
            }
        }
    }

    public static class BlockBuilder extends Builder<Block>{
        // 这个默认值是要保留的
        String genModelWay = HBMKey.MODEL_CUBE_ALL;
        String lootWay = HBMKey.DROP_SELF;
        ResourceKey<CreativeModeTab> creativeKey;
        BiConsumer<Block, BlockStateGen> modelGen;
        Function<Block, BlockItem> blockItem;
        List<TagKey<Block>> tags;
        public BlockBuilder(String name, Supplier<? extends Block> sup) {
            super(name, sup);
        }

        public BlockBuilder tab(ResourceKey<CreativeModeTab> tabKey){
            creativeKey = tabKey;
            if (ModBlocks.isMachineTab(tabKey)) {
                tags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.Blocks.MACHINE);
            }
            return this;
        }

        public BlockBuilder model(BiConsumer<Block, BlockStateGen> modelGen){
            this.genModelWay = HBMKey.MODEL_STANDALONE;
            this.modelGen = modelGen;
            return this;
        }

        public BlockBuilder model(String genModelWay){
            this.genModelWay = genModelWay;
            return this;
        }
        // 仅限于简单的loot，过于复杂的loot请在LootGen中单独指定
        public BlockBuilder loot(String lootWay){
            this.lootWay = lootWay;
            return this;
        }

        public BlockBuilder loc(String genNameWay){
            this.genNameWay = genNameWay;
            return this;
        }

        public BlockBuilder item(Function<Block, BlockItem> blockItem){
            this.blockItem = blockItem;
            return this;
        }

        @SafeVarargs
        public final BlockBuilder tags(TagKey<Block>... blockTags){
            if (blockTags.length > 0){
                if (this.tags == null) this.tags = new ArrayList<>();
                for (TagKey<Block> blockTag : blockTags) {
                    if (!this.tags.contains(blockTag)) {
                        this.tags.add(blockTag);
                    }
                }
            }
            return this;
        }

        @Override
        public RegistryObject<Block> build() {
            WrappedBlockRegistry blockRegistry = new WrappedBlockRegistry();
            blockRegistry.registryObject = ModBlocks.BLOCKS.register(name, sup);
            ModItems.ITEMS.register(name, blockItem != null ? () -> blockItem.apply(blockRegistry.get()) : ()->new BlockItem(blockRegistry.get(),new Item.Properties()));
            blockRegistry.creativeKey = creativeKey;
            blockRegistry.genModelWay = genModelWay;
            blockRegistry.genNameWay = genNameWay;
            blockRegistry.modelFactory = modelGen;
            blockRegistry.lootWay = lootWay;
            blockRegistry.tags = tags;
            if (blockRegistry.genNameWay!= null && blockRegistry.genNameWay.equals(HBMKey.LITERALLY) && localizedName!=null)
                blockRegistry.localizedName = localizedName;
            ModBlocks.blockList.add(blockRegistry);
            return blockRegistry.registryObject;
        }
    }
}
