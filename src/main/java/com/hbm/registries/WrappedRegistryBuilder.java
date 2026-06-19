package com.hbm.registries;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.loot.BlockLootGen;
import com.hbm.datagen.model.BlockStateGen;
import com.hbm.datagen.model.ItemModelGen;
import com.hbm.datagen.tag.BlockTagsGen;
import com.hbm.datagen.tag.ItemTagsGen;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.*;

public abstract class WrappedRegistryBuilder<T> implements Supplier<T>{
    // 注册对象
    RegistryObject<T> registryObject;
    // 注册名
    String name;
    // 根据id生成本地语言名的方式，或者就是本地语言名本身。
    String genNameWay = HBMKey.ORDERLY_GEN;
    WrappedRegistryBuilder(String name, Supplier<? extends T> sup){
        this.name = name;
        this.registryObject = register(sup);
    }

    protected abstract RegistryObject<T> register(Supplier<? extends T> sup);
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

    public abstract RegistryObject<T> build();

    public static class WrappedItemRegistryBuilder extends WrappedRegistryBuilder<Item> {
        // 创造模式物品栏
        ResourceKey<CreativeModeTab> creativeKey;
        // 模型
        String genModelWay = HBMKey.BASIC_MODEL;
        Consumer<ItemModelGen> modelFactory;
        ResourceLocation itemProperty;
        ItemPropertyFunction itemPropertyFunction;
        ItemColor itemColor;
        // 本地化
        String[] descriptions;
        // tag
        List<TagKey<Item>> tags;

        public WrappedItemRegistryBuilder(String name, Supplier<? extends Item> sup) {
            super(name, sup);
        }

        @Override
        protected RegistryObject<Item> register(Supplier<? extends Item> sup) {
            RegistryObject<Item> existing = findExistingItem(name);
            if (existing != null) {
                return existing;
            }
            return ModItems.ITEMS.register(name, sup);
        }
        // ==================构建单个所需的函数
        public WrappedItemRegistryBuilder tab(ResourceKey<CreativeModeTab> tabKey){
            creativeKey = tabKey;
            return this;
        }
        // 直接指定模型生成函数
        public WrappedItemRegistryBuilder model(String genModelWay){
            this.genModelWay = genModelWay;
            return this;
        }

        public WrappedItemRegistryBuilder model(Consumer<ItemModelGen> modelGen){
            this.genModelWay = HBMKey.MODEL_STANDALONE;
            this.modelFactory = modelGen;
            return this;
        }

        public WrappedItemRegistryBuilder itemProperties(ResourceLocation property, ItemPropertyFunction itemPropertyFunction){
            this.itemProperty = property;
            this.itemPropertyFunction = itemPropertyFunction;
            return this;
        }

        public WrappedItemRegistryBuilder color(ItemColor itemColor){
            this.itemColor = itemColor;
            return this;
        }

        public WrappedItemRegistryBuilder loc(String genNameWay, String ... desc){
            this.genNameWay = genNameWay;
            if (desc != null && desc.length > 0) this.descriptions = desc;
            return this;
        }

        @SafeVarargs
        public final WrappedItemRegistryBuilder tags(TagKey<Item>... itemTags){
            if (itemTags.length > 0){
                this.tags = Arrays.stream(itemTags).toList();
            }
            return this;
        }

        /**
         * 新增方法：配置文件控制贴图
         * @param propertyName 客户端 property 名称，对应模型 overrides 的 predicate 名
         * @param condition 返回 true 或 false，根据配置切换贴图
         */
        public WrappedItemRegistryBuilder withConfigTexture(String propertyName, Supplier<Boolean> condition) {
            this.itemProperty = HBM.rl(propertyName);
            this.itemPropertyFunction = (stack, level, entity, seed) -> condition.get() ? 1 : 0;
            this.genModelWay = HBMKey.MODEL_DYNAMIC;
            return this;
        }


        @Override
        public RegistryObject<Item> build() {
//            RegistryObject<Item> existing = findExistingItem(name);
//            if (existing != null) {
//                return existing;
//            }
            ModItems.itemList.add(this);
            return registryObject;
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

        public void languageSupport(LanguageProvider provider){
            switch (genNameWay){
                case HBMKey.ORDERLY_GEN -> provider.add(get(), RegistryHelper.generateOrderlyName(getId().getPath()));
                case HBMKey.REVERSE_GEN -> provider.add(get(), RegistryHelper.generateReversedName(getId().getPath()));
                case HBMKey.ORDERLY_GEN_EXCEPT_FIRST -> provider.add(get(), RegistryHelper.generateOrderlyExceptFirstName(getId().getPath()));
                case HBMKey.GEN_STANDALONE -> {
                    return;
                }
                // 如何无法匹配上，则视为直接输入的翻译内容
                default -> provider.add(get(), genNameWay);
            }
            if (descriptions != null && descriptions.length > 0){
                for (int i = 0; i < descriptions.length; i++) {
                    provider.add(get().getDescriptionId() + ".desc" + i, descriptions[i]);
                }
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
                        provider.withExistingParent(name, "minecraft:item/template_spawn_egg");
                case HBMKey.MODEL_STANDALONE -> {
                    if (modelFactory != null) modelFactory.accept(provider);
                }
                case HBMKey.MODEL_DYNAMIC -> {}

            }
        }

        public void tagSupport(ItemTagsGen provider){
            if (this.tags == null || this.tags.isEmpty()) return;
            for (TagKey<Item> tag : this.tags) {
                if (tag != null) provider.tag(tag).add(registryObject.get());
            }
        }

        public void itemPropertiesSupport(){
            if (this.itemProperty != null && this.itemPropertyFunction != null)
                ItemProperties.register(get(), this.itemProperty, this.itemPropertyFunction);
        }

        public void itemColorSupport(RegisterColorHandlersEvent.Item event){
            if (this.itemColor != null)
                event.register(this.itemColor, get());
        }
    }

//    private static abstract class Builder<T>{
//        final String name;
//        final Supplier<? extends T> sup;
//        String genNameWay = HBMKey.ORDERLY_GEN;
//        String localizedName;
//
//        Builder(String name, Supplier<? extends T> sup){
//            this.name = name;
//            this.sup = sup;
//        }
//
//        public abstract RegistryObject<T> build();
//    }

//    public static class ItemBuilder extends Builder<Item>{
//        ResourceKey<CreativeModeTab> creativeKey;
//        String genModelWay = HBMKey.BASIC_MODEL;
//        Consumer<ItemModelGen> modelGen;
//        // 动态物品模型参数
//        String propertyName;
//        // 自动生成的描述文昌
//        String[] descriptions;
//        Supplier<Boolean> condition;
//        List<TagKey<Item>> tags;
//        public ItemBuilder(String name, Supplier<? extends Item> sup) {
//            super(name, sup);
//        }
//
//        public ItemBuilder tab(ResourceKey<CreativeModeTab> tabKey){
//            creativeKey = tabKey;
//            return this;
//        }
//        // 直接指定模型生成函数
//        public ItemBuilder model(Consumer<ItemModelGen> modelGen){
//            this.genModelWay = HBMKey.MODEL_STANDALONE;
//            this.modelGen = modelGen;
//            return this;
//        }
//
//        public ItemBuilder model(String genModelWay){
//            this.genModelWay = genModelWay;
//            return this;
//        }
//
//        /**
//         * 新增方法：配置文件控制贴图
//         * @param propertyName 客户端 property 名称，对应模型 overrides 的 predicate 名
//         * @param condition 返回 true 或 false，根据配置切换贴图
//         */
//        public ItemBuilder withConfigTexture(String propertyName, Supplier<Boolean> condition) {
//            this.propertyName = propertyName;
//            this.condition = condition;
//            this.genModelWay = HBMKey.MODEL_DYNAMIC;
//            return this;
//        }
//        public ItemBuilder loc(String genNameWay, String ... desc){
//            this.genNameWay = genNameWay;
//            if (desc != null && desc.length > 0) this.descriptions = desc;
//            return this;
//        }
//        @SafeVarargs
//        public final ItemBuilder tags(TagKey<Item>... itemTags){
//            if (itemTags.length > 0){
//                this.tags = Arrays.stream(itemTags).toList();
//            }
//            return this;
//        }
//        @Override
//        public RegistryObject<Item> build() {
//            RegistryObject<Item> existing = findExistingItem(name);
//            if (existing != null) {
//                return existing;
//            }
//            WrappedItemRegistryBuilder itemRegistry = new WrappedItemRegistryBuilder();
//            itemRegistry.registryObject = ModItems.ITEMS.register(name, sup);
//            itemRegistry.creativeKey = creativeKey;
//            itemRegistry.genModelWay = genModelWay;
//            itemRegistry.genNameWay = genNameWay;
//            itemRegistry.modelFactory = modelGen;
//            itemRegistry.tags = tags;
//            itemRegistry.descriptions = descriptions;
//            if (itemRegistry.genNameWay!= null && itemRegistry.genNameWay.equals(HBMKey.LITERALLY) && localizedName!=null)
//                itemRegistry.localizedName = localizedName;
//            ModItems.itemList.add(itemRegistry);
//            return itemRegistry.registryObject;
//        }
//
//        private static RegistryObject<Item> findExistingItem(String name) {
//            ResourceLocation id = new ResourceLocation(HBM.MODID, name);
//            for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
//                ResourceLocation entryId = entry.getId();
//                if (id.equals(entryId)) {
//                    return entry;
//                }
//            }
//            return null;
//        }
//    }

    public static class WrappedBlockRegistryBuilder extends WrappedRegistryBuilder<Block> {
        ResourceKey<CreativeModeTab> creativeKey;
        String genModelWay = HBMKey.MODEL_CUBE_ALL;
        BiConsumer<Block, BlockStateGen> modelFactory;
        BlockColor blockColor;
        String lootWay = HBMKey.DROP_SELF;
        List<TagKey<Block>> tags;
        Function<Block, BlockItem> blockItem;
        public WrappedBlockRegistryBuilder(String name, Supplier<? extends Block> sup) {
            super(name, sup);
        }

        @Override
        protected RegistryObject<Block> register(Supplier<? extends Block> sup) {
            return ModBlocks.BLOCKS.register(name, sup);
        }

        public WrappedBlockRegistryBuilder tab(ResourceKey<CreativeModeTab> tabKey){
            creativeKey = tabKey;
            if (ModBlocks.isMachineTab(tabKey)) {
                tags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.Blocks.MACHINE);
            }
            return this;
        }

        public WrappedBlockRegistryBuilder model(String genModelWay){
            this.genModelWay = genModelWay;
            return this;
        }

        public WrappedBlockRegistryBuilder model(BiConsumer<Block, BlockStateGen> modelGen){
            this.genModelWay = HBMKey.MODEL_STANDALONE;
            this.modelFactory = modelGen;
            return this;
        }

        public WrappedBlockRegistryBuilder color(BlockColor blockColor){
            this.blockColor = blockColor;
            return this;
        }

        // 仅限于简单的loot，过于复杂的loot请在LootGen中单独指定
        public WrappedBlockRegistryBuilder loot(String lootWay){
            this.lootWay = lootWay;
            return this;
        }

        public WrappedBlockRegistryBuilder loc(String genNameWay){
            this.genNameWay = genNameWay;
            return this;
        }

        public WrappedBlockRegistryBuilder item(Function<Block, BlockItem> blockItem){
            this.blockItem = blockItem;
            return this;
        }

        @SafeVarargs
        public final WrappedBlockRegistryBuilder tags(TagKey<Block>... blockTags){
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
            ModItems.ITEMS.register(name, blockItem != null ? () -> blockItem.apply(get()) : ()->new BlockItem(get(),new Item.Properties()));
            ModBlocks.blockList.add(this);
            return registryObject;
        }


        public void languageSupport(LanguageProvider provider){
            switch (genNameWay){
                case HBMKey.ORDERLY_GEN -> provider.add(get(), RegistryHelper.generateOrderlyName(getId().getPath()));
                case HBMKey.REVERSE_GEN -> provider.add(get(), RegistryHelper.generateReversedName(getId().getPath()));
                case HBMKey.ORDERLY_GEN_EXCEPT_FIRST -> provider.add(get(), RegistryHelper.generateOrderlyExceptFirstName(getId().getPath()));
                case HBMKey.GEN_STANDALONE -> {}
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
        public void blockColorSupport(RegisterColorHandlersEvent.Block event){
            if (this.blockColor != null)
                event.register(this.blockColor, get());
        }
    }

//    public static class BlockBuilder extends Builder<Block>{
//        // 这个默认值是要保留的
//        String genModelWay = HBMKey.MODEL_CUBE_ALL;
//        String lootWay = HBMKey.DROP_SELF;
//        ResourceKey<CreativeModeTab> creativeKey;
//        BiConsumer<Block, BlockStateGen> modelGen;
//        Function<Block, BlockItem> blockItem;
//        List<TagKey<Block>> tags;
//        public BlockBuilder(String name, Supplier<? extends Block> sup) {
//            super(name, sup);
//        }
//
//        public BlockBuilder tab(ResourceKey<CreativeModeTab> tabKey){
//            creativeKey = tabKey;
//            if (ModBlocks.isMachineTab(tabKey)) {
//                tags(BlockTags.MINEABLE_WITH_PICKAXE, ModTags.Blocks.MACHINE);
//            }
//            return this;
//        }
//
//        public BlockBuilder model(BiConsumer<Block, BlockStateGen> modelGen){
//            this.genModelWay = HBMKey.MODEL_STANDALONE;
//            this.modelGen = modelGen;
//            return this;
//        }
//
//        public BlockBuilder model(String genModelWay){
//            this.genModelWay = genModelWay;
//            return this;
//        }
//        // 仅限于简单的loot，过于复杂的loot请在LootGen中单独指定
//        public BlockBuilder loot(String lootWay){
//            this.lootWay = lootWay;
//            return this;
//        }
//
//        public BlockBuilder loc(String genNameWay){
//            this.genNameWay = genNameWay;
//            return this;
//        }
//
//        public BlockBuilder item(Function<Block, BlockItem> blockItem){
//            this.blockItem = blockItem;
//            return this;
//        }
//
//        @SafeVarargs
//        public final BlockBuilder tags(TagKey<Block>... blockTags){
//            if (blockTags.length > 0){
//                if (this.tags == null) this.tags = new ArrayList<>();
//                for (TagKey<Block> blockTag : blockTags) {
//                    if (!this.tags.contains(blockTag)) {
//                        this.tags.add(blockTag);
//                    }
//                }
//            }
//            return this;
//        }
//
//        @Override
//        public RegistryObject<Block> build() {
//            WrappedBlockRegistryBuilder blockRegistry = new WrappedBlockRegistryBuilder();
//            blockRegistry.registryObject = ModBlocks.BLOCKS.register(name, sup);
//            ModItems.ITEMS.register(name, blockItem != null ? () -> blockItem.apply(blockRegistry.get()) : ()->new BlockItem(blockRegistry.get(),new Item.Properties()));
//            blockRegistry.creativeKey = creativeKey;
//            blockRegistry.genModelWay = genModelWay;
//            blockRegistry.genNameWay = genNameWay;
//            blockRegistry.modelFactory = modelGen;
//            blockRegistry.lootWay = lootWay;
//            blockRegistry.tags = tags;
//            if (blockRegistry.genNameWay!= null && blockRegistry.genNameWay.equals(HBMKey.LITERALLY) && localizedName!=null)
//                blockRegistry.localizedName = localizedName;
//            ModBlocks.blockList.add(blockRegistry);
//            return blockRegistry.registryObject;
//        }
//    }

    public static class RegisterObjectCollection<T, R>{
        Map<R, RegistryObject<T>> registryObjectMap;
//        public EnumRegisterObjectCollection(Class<R> theEnum, Function<R, RegistryObject<T>> func){
//            this.registryObjectMap = new HashMap<>();
//            for (R enumConstant : theEnum.getEnumConstants()) {
//                registryObjectMap.put(enumConstant, func.apply(enumConstant));
//            }
//        }
        public RegisterObjectCollection(Class<R> theEnum, Function<R, RegistryObject<T>> func){
            this(List.of(theEnum.getEnumConstants()), func);
        }

        public RegisterObjectCollection(Collection<R> collection, Function<R, RegistryObject<T>> func){
            this(collection, func, o -> true);
        }

        public RegisterObjectCollection(Collection<R> collection, Function<R, RegistryObject<T>> func, Predicate<R> filter){
            this.registryObjectMap = new HashMap<>();
            for (R key : collection) {
                if (filter.test(key))
                    registryObjectMap.put(key, func.apply(key));
            }
        }

        public RegistryObject<T> get(R key){
            return registryObjectMap.get(key);
        }
    }
}
