package com.hbm.datagen.model;

import com.hbm.HBM;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.item.*;
import com.hbm.registries.ModItems;;
import com.hbm.registries.WrappedRegistryBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ItemModelGen extends ItemModelProvider {
    private final Set<ResourceLocation> generatedModels = new HashSet<>();
    public static ResourceLocation property_stage = HBM.rl("stage");
    public static ResourceLocation property_type = HBM.rl("type");

    public ItemModelGen(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ModItems.genModel(this);
        HBMWeapon.genModel(this);
        HBMCombat.genModel(this);
//        HBMFluids.bucketModel(this);
        generateMissingSimpleItemModels();

        ResourceLocation item_path;
        item_path = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ModItems.INGOT_U238M2.get()));
        this.getBuilder(item_path.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item_path.getNamespace(), "item/ingot_u238m2"))
                .override().predicate(property_stage,0).model(this.basicItem(HBM.rl("ingot_u238m2"))).end()
                .override().predicate(property_stage, 1).model(this.basicItem(HBM.rl("hs-elements"))).end()
                .override().predicate(property_stage, 2).model(this.basicItem(HBM.rl("hs-arsenic"))).end()
                .override().predicate(property_stage, 3).model(this.basicItem(HBM.rl("hs-vault"))).end();

        item_path = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ModItems.INGOT_NEPTUNIUM.get()));
        this.getBuilder(item_path.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item_path.getNamespace(), "item/" + item_path.getPath()))
                .override()
                .predicate(property_stage, 1.0f).model(this.basicItem(HBM.rl("ingot_nikonium")))
                .end();
    }

    public void registerOrdinaryItemModel(String key){
        this.singleTexture(key,ResourceLocation.parse("item/generated"),"layer0"
                ,HBM.rl( "item/" + key));
    }

    @Override
    public ItemModelBuilder basicItem(Item item) {
        ItemModelBuilder builder = super.basicItem(item);
        markGenerated(ForgeRegistries.ITEMS.getKey(item));
        return builder;
    }

    public ItemModelBuilder basicItem(String name, ResourceLocation location) {
        return getBuilder(name).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "item/" + location.getPath()));
    }

    @Override
    public ItemModelBuilder withExistingParent(String name, ResourceLocation parent) {
        ItemModelBuilder builder = super.withExistingParent(name, parent);
        markGenerated(resolveName(name));
        return builder;
    }

    @Override
    public ItemModelBuilder getBuilder(String name) {
        ItemModelBuilder builder = super.getBuilder(name);
        markGenerated(resolveName(name));
        return builder;
    }

    private void markGenerated(ResourceLocation id){
        if (id != null && HBM.MODID.equals(id.getNamespace())){
            generatedModels.add(id);
        }
    }

    private ResourceLocation resolveName(String name){
        return name.contains(":") ? ResourceLocation.parse(name) : HBM.rl( name);
    }

    private void generateMissingSimpleItemModels(){
        Path manualModelDir = Paths.get("src", "main", "resources", "assets", HBM.MODID, "models", "item");
        ForgeRegistries.ITEMS.getValues().forEach(item -> {
            if (item == null) {
                return;
            }
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            if (id == null || !HBM.MODID.equals(id.getNamespace())) {
                return;
            }
            if (generatedModels.contains(id)) {
                return;
            }
            if (Files.exists(manualModelDir.resolve(id.getPath() + ".json"))) {
                return;
            }
            ResourceLocation manualModel = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "models/item/" + id.getPath() + ".json");
            if (existingFileHelper != null && existingFileHelper.exists(manualModel, PackType.CLIENT_RESOURCES)) {
                return;
            }
            this.basicItem(item);
        });
    }

    public static ResourceLocation getPath(Item item){
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item));
    }
    public ItemModelGen basicItemWithProperty(Item item, ResourceLocation property, ResourceLocation alterTexture){
        ResourceLocation path = getPath(item);
        this.getBuilder(path.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(path.getNamespace(), "item/" + path.getPath()))
                .override()
                .predicate(property, 1.0f).model(this.basicItem(alterTexture))
                .end();
        return this;
    }

    public ItemModelGen basicItemWithProperty(Item item, boolean withBaseTexture, ResourceLocation property, Map<Float, ResourceLocation> alterTextures){
        ResourceLocation path = getPath(item);
        ItemModelBuilder builder = this.getBuilder(path.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"));
        if (withBaseTexture) builder.texture("layer0", decorateRL(path));
        for (Map.Entry<Float, ResourceLocation> entry : alterTextures.entrySet()) {
            builder.override().predicate(property, entry.getKey()).model(this.basicItem(decorateRL(entry.getValue()))).end();
        }
        return this;
    }
    // 这里涉及复杂的嵌套，还是
    public ItemModelGen basicItemWith2Properties(Item item, List<WrappedRegistryBuilder.ItemPropertyInfo> infos){
        if (infos == null || infos.size() != 2) return this;
        ResourceLocation path = getPath(item);
        ItemModelBuilder builder = this.getBuilder(path.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"));
        WrappedRegistryBuilder.ItemPropertyInfo info1 = infos.get(0);
        WrappedRegistryBuilder.ItemPropertyInfo info2 = infos.get(1);
        for (Map.Entry<Float, ResourceLocation> entry1 : info1.propertyTextures().entrySet()) {
            for (Map.Entry<Float, ResourceLocation> entry2 : info2.propertyTextures().entrySet()) {
                builder.override().predicate(info1.itemProperty(), entry1.getKey()).predicate(info2.itemProperty(), entry2.getKey())
                        .model(this.multiLayerItem(item, info1.withBaseTexture(), entry1.getValue(), entry2.getValue()))
                        .end();
            }
        }

        return this;
    }
    public ModelFile multiLayerItem(Item item, boolean specifiedBaseTexture, ResourceLocation ... layers){
        return multiLayerItem(getPath(item), specifiedBaseTexture, layers);
//        if (layers.length == 0) return this.basicItem(item);
//        else {
//            ResourceLocation path = getPath(item);
//            ResourceLocation baseTexture = new ResourceLocation(path.getNamespace(), "item/" +( specifiedBaseTexture ? layers[0].getPath() : path.getPath()));
//            ItemModelBuilder builder = this.getBuilder(path.toString()).parent(getExistingFile(ResourceLocation.tryParse("item/generated")))
//                    .texture("layer0", baseTexture);
//            for (int i = specifiedBaseTexture ? 1 : 0; i < layers.length; i++) {
//                builder.texture("layer" + (specifiedBaseTexture ? i : i + 1), new ResourceLocation(path.getNamespace(), "item/" + layers[i].getPath()));
//            }
//            return builder;
//        }
    }

    public ModelFile multiLayerItem(ResourceLocation path, boolean specifiedBaseTexture, ResourceLocation ... layers){
        if (layers.length == 0) return this.basicItem(path);
        else {
            ResourceLocation baseTexture = ResourceLocation.fromNamespaceAndPath(path.getNamespace(), "item/" +( specifiedBaseTexture ? layers[0].getPath() : path.getPath()));
            ItemModelBuilder builder = this.getBuilder(path.toString()).parent(getExistingFile(ResourceLocation.tryParse("item/generated")))
                    .texture("layer0", baseTexture);
            for (int i = specifiedBaseTexture ? 1 : 0; i < layers.length; i++) {
                builder.texture("layer" + (specifiedBaseTexture ? i : i + 1), ResourceLocation.fromNamespaceAndPath(path.getNamespace(), "item/" + layers[i].getPath()));
            }
            return builder;
        }
    }

    public ItemModelGen singleTexture(Item item, ResourceLocation texture){
        getBuilder(getPath(item).toString()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", texture);
        return this;
    }

    private ResourceLocation decorateRL(ResourceLocation resourceLocation){
        return resourceLocation.withPrefix(ModelProvider.ITEM_FOLDER + "/");
    }
}
