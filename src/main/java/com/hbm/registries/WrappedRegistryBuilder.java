package com.hbm.registries;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.datagen.json.HBMJsonProvider;
import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.loot.BlockLootGen;
import com.hbm.datagen.model.BlockStateGen;
import com.hbm.datagen.model.ItemModelGen;
import com.hbm.datagen.tag.BlockTagsGen;
import com.hbm.datagen.tag.ItemTagsGen;
import com.hbm.gui.ModMenuType;
import com.hbm.gui.screen.ICFScreen;
import com.hbm.item.interfaces.CreativeTabVariantItem;
import com.hbm.render.blockentity.NukeBoyRender;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.network.IContainerFactory;
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

    public record ItemPropertyInfo(ResourceLocation itemProperty, boolean withBaseTexture, ItemPropertyFunction itemPropertyFunction, Map<Float, ResourceLocation> propertyTextures){}

    public static class WrappedItemRegistryBuilder extends WrappedRegistryBuilder<Item> {
        // 创造模式物品栏
        ResourceKey<CreativeModeTab> creativeKey;
        // 模型
        String genModelWay = HBMKey.BASIC_MODEL;
        Consumer<ItemModelGen> modelFactory;
        ItemColor itemColor;
        ResourceLocation[] layerTextures;
//        ResourceLocation itemProperty;
//        ItemPropertyFunction itemPropertyFunction;
//        boolean withBaseTexture;
//        Map<Float, ResourceLocation> propertyTextures;
        List<ItemPropertyInfo> itemPropertyInfos;
        // 本地化
        String[] descriptions;
        // tag
        List<TagKey<Item>> tags;
        // hud
        Consumer<RegisterGuiOverlaysEvent> hudRegister;

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

        public WrappedItemRegistryBuilder model(String genModelWay, ResourceLocation... alterTexture){
            this.genModelWay = genModelWay;
            this.layerTextures = alterTexture;
            return this;
        }

        public WrappedItemRegistryBuilder model(Consumer<ItemModelGen> modelGen){
            this.genModelWay = HBMKey.MODEL_STANDALONE;
            this.modelFactory = modelGen;
            return this;
        }

        public WrappedItemRegistryBuilder itemProperties(ResourceLocation property, ItemPropertyFunction itemPropertyFunction, ResourceLocation texture){
            return itemProperties(property, true, itemPropertyFunction, Map.of(1f, texture));
        }
        public WrappedItemRegistryBuilder itemProperties(ResourceLocation property, boolean withBaseTexture, ItemPropertyFunction itemPropertyFunction, Map<Float, ResourceLocation> propertyTextures){
            this.genModelWay = HBMKey.MODEL_ITEM_PROPERTY;
            if (itemPropertyInfos == null) itemPropertyInfos = new ArrayList<>();
            itemPropertyInfos.add(new ItemPropertyInfo(property, withBaseTexture, itemPropertyFunction, propertyTextures));
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

        public WrappedItemRegistryBuilder hud(Consumer<RegisterGuiOverlaysEvent> hudRegister){
            this.hudRegister = hudRegister;
            return this;
        }

        /**
         * 新增方法：配置文件控制贴图
         * @param propertyName 客户端 property 名称，对应模型 overrides 的 predicate 名
         * @param condition 返回 true 或 false，根据配置切换贴图
         */
        public WrappedItemRegistryBuilder withConfigTexture(String propertyName, Supplier<Boolean> condition) {
            this.genModelWay = HBMKey.MODEL_DYNAMIC;
            return itemProperties(HBM.rl(propertyName), true, (stack, level, entity, seed) -> condition.get() ? 1 : 0, null);
//            this.itemProperty = HBM.rl(propertyName);
//            this.itemPropertyFunction = (stack, level, entity, seed) -> condition.get() ? 1 : 0
//            return this;
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
                if (item instanceof CreativeTabVariantItem variantItem) {
                    variantItem.fillCreativeTab(event);
                } else {
                    event.getEntries().put(new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }
        }

        public void modelSupport(ItemModelGen provider){
            switch (genModelWay) {
                // 基础贴图
                case HBMKey.BASIC_MODEL -> provider.basicItem(get());
                case HBMKey.SPAWN_EGG_MODEL -> provider.withExistingParent(name, "minecraft:item/template_spawn_egg");
                // 单层贴图，但不依据物品key生成
                case HBMKey.MODEL_ITEM_SINGLE -> {
                    if (layerTextures.length > 0) provider.singleTexture(get(), layerTextures[0]);
                }
                // 依据物品属性设置贴图
                case HBMKey.MODEL_ITEM_PROPERTY -> {
                    if (this.itemPropertyInfos != null && !this.itemPropertyInfos.isEmpty()){
                        if (itemPropertyInfos.size() == 1) {
                            ItemPropertyInfo info = itemPropertyInfos.get(0);
                            provider.basicItemWithProperty(get(), info.withBaseTexture, info.itemProperty, info.propertyTextures);
                        }else if (itemPropertyInfos.size() == 2){
                            provider.basicItemWith2Properties(get(), itemPropertyInfos);
                        }
                    }
                }
                // 多层贴图
                case HBMKey.MODEL_ITEM_OVERLAY -> {
                    if (layerTextures != null && layerTextures.length > 0) provider.multiLayerItem(get(), false, layerTextures);
                }case HBMKey.MODEL_ITEM_MULTI_LAYER -> {
                    if (layerTextures != null && layerTextures.length > 0) provider.multiLayerItem(get(), true, layerTextures);
                }
                // 单独设置模型
                case HBMKey.MODEL_STANDALONE -> {
                    if (modelFactory != null) modelFactory.accept(provider);
                }case HBMKey.MODEL_DYNAMIC -> {}
            }
        }

        public void tagSupport(ItemTagsGen provider){
            if (this.tags == null || this.tags.isEmpty()) return;
            for (TagKey<Item> tag : this.tags) {
                if (tag != null) provider.tag(tag).add(registryObject.get());
            }
        }

        public void itemPropertiesSupport(){
            if (this.itemPropertyInfos != null && !this.itemPropertyInfos.isEmpty())
                for (ItemPropertyInfo itemPropertyInfo : this.itemPropertyInfos) {
                    ItemProperties.register(get(), itemPropertyInfo.itemProperty, itemPropertyInfo.itemPropertyFunction);
                }
        }

        public void itemColorSupport(RegisterColorHandlersEvent.Item event){
            if (this.itemColor != null)
                event.register(this.itemColor, get());
        }

        public void hudSupport(RegisterGuiOverlaysEvent event){
            if (this.hudRegister != null) this.hudRegister.accept(event);
        }

        public void customJsonSupport(HBMJsonProvider provider){

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

    public static class tileData<T extends BlockEntity, M extends AbstractContainerMenu, G extends AbstractContainerScreen<M>, R extends BlockEntityRenderer<T>>{
        public BlockEntityType.BlockEntitySupplier<T> tileFactory;
        public IContainerFactory<M> menuFactory;
        public MenuScreens.ScreenConstructor<M, G> guiFactory;
        public BlockEntityRendererProvider<T> rendererFactory;
    }
    public static class WrappedBlockRegistryBuilder extends WrappedRegistryBuilder<Block> {
        ResourceKey<CreativeModeTab> creativeKey;
        String genModelWay = HBMKey.MODEL_CUBE_ALL;
        BiConsumer<Block, BlockStateGen> modelFactory;
        BlockColor blockColor;
        String lootWay = HBMKey.DROP_SELF;
        List<TagKey<Block>> tags;
        Function<Block, BlockItem> blockItem;
        //
        tileData tileData;

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

        public WrappedBlockRegistryBuilder tile(BlockEntityType.BlockEntitySupplier<? extends BlockEntity> tileFactory){
            if (this.tileData == null) this.tileData = new tileData();
            this.tileData.tileFactory = tileFactory;
            return this;
        }

        public WrappedBlockRegistryBuilder menu(IContainerFactory menuFactory){
            if (this.tileData == null) this.tileData = new tileData();
            this.tileData.menuFactory = menuFactory;
            return this;
        }

        public WrappedBlockRegistryBuilder gui(MenuScreens.ScreenConstructor guiFactory){
            if (this.tileData == null) this.tileData = new tileData();
            this.tileData.guiFactory = guiFactory;
            return this;
        }

        public WrappedBlockRegistryBuilder renderer(BlockEntityRendererProvider rendererFactory){
            if (this.tileData == null) this.tileData = new tileData();
            this.tileData.rendererFactory = rendererFactory;
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

        public void customJsonSupport(HBMJsonProvider provider){
        }

        public void tileSupport(){
            if (this.tileData != null && this.tileData.tileFactory != null){
                ModBlockEntityType.register("tile_" + name, this.tileData.tileFactory);
            }
        }

        public void menuSupport(){
            if (this.tileData != null && this.tileData.tileFactory != null && this.tileData.menuFactory != null){
                ModMenuType.register("menu_" + name, this.tileData.menuFactory);
            }
        }

        public void guiSupport(){
            if (this.tileData != null && this.tileData.menuFactory != null && this.tileData.guiFactory != null){
                MenuScreens.register(ModMenuType.typesMaps.get("menu_" + name).get(), this.tileData.guiFactory);
            }
        }

        public void rendererSupport(){
            if (this.tileData != null && this.tileData.tileFactory != null && this.tileData.rendererFactory != null)
                BlockEntityRenderers.register(ModBlockEntityType.tileTypes.get("tile_" + name).get(), this.tileData.rendererFactory);
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
        public final Map<R, RegistryObject<T>> registryObjectMap;
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
