package com.hbm.datagen.model;

import com.hbm.HBM;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.item.*;
import com.hbm.registries.ModItems;;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ItemModelGen extends ItemModelProvider {
    private final Set<ResourceLocation> generatedModels = new HashSet<>();

    public ItemModelGen(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ModItems.genModel(this);
        HBMWeapon.genModel(this);
        HBMCombat.genModel(this);
        ModFluids.bucketModel(this);

//        this.basicItem(ModItems.overlay_my_fluid.get());
//        this.basicItem(ModItems.BEDROCK_ORE.get());
//        /* tool */
//        this.basicItem(ModItems.SCREWDRIVER.get());

        generateMissingSimpleItemModels();

        ResourceLocation item_path;
        ResourceLocation property_stage = HBM.rl("stage");
        ResourceLocation property_broken = new ResourceLocation("broken");
        item_path = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ModItems.INGOT_U238M2.get()));
        this.getBuilder(item_path.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", new ResourceLocation(item_path.getNamespace(), "item/ingot_u238m2"))
                .override().predicate(property_stage,0).model(this.basicItem(HBM.rl("ingot_u238m2"))).end()
                .override().predicate(property_stage, 1).model(this.basicItem(HBM.rl("hs-elements"))).end()
                .override().predicate(property_stage, 2).model(this.basicItem(HBM.rl("hs-arsenic"))).end()
                .override().predicate(property_stage, 3).model(this.basicItem(HBM.rl("hs-vault"))).end();

        item_path = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ModItems.INGOT_NEPTUNIUM.get()));
        this.getBuilder(item_path.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", new ResourceLocation(item_path.getNamespace(), "item/" + item_path.getPath()))
                .override()
                .predicate(property_stage, 1.0f).model(this.basicItem(HBM.rl("ingot_nikonium")))
                .end();
    }

    public void registerOrdinaryItemModel(String key){
        this.singleTexture(key,new ResourceLocation("item/generated"),"layer0"
                ,new ResourceLocation(HBM.MODID, "item/" + key));
    }
    /**
     * 产生默认的实体模型，在游戏内自主渲染。
     * */
    public void builtinModel(Item item){
        ResourceLocation resourceLocation = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item));
        this.withExistingParent(resourceLocation.toString(), "builtin/entity");
    }

    @Override
    public ItemModelBuilder basicItem(Item item) {
        ItemModelBuilder builder = super.basicItem(item);
        markGenerated(ForgeRegistries.ITEMS.getKey(item));
        return builder;
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
        return name.contains(":") ? new ResourceLocation(name) : new ResourceLocation(HBM.MODID, name);
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
            ResourceLocation manualModel = new ResourceLocation(id.getNamespace(), "models/item/" + id.getPath() + ".json");
            if (existingFileHelper != null && existingFileHelper.exists(manualModel, PackType.CLIENT_RESOURCES)) {
                return;
            }
            this.basicItem(item);
        });
    }
}
