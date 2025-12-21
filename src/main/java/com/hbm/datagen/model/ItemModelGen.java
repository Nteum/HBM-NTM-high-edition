package com.hbm.datagen.model;

import com.hbm.HBM;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.item.*;
import com.hbm.registries.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
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
        HBMtools.genModel(this);
        HBMComponent.genModel(this);
        HBMWeapon.genModel(this);
        HBMCombat.genModel(this);
        ModFluids.bucketModel(this);
//        this.basicItem(ModItems.ingot_red_copper.get());
//        this.basicItem(ModItems.ingot_tungsten.get());
//        this.basicItem(ModItems.ingot_aluminium.get());
//        this.basicItem(ModItems.ingot_lead.get());
//        this.basicItem(ModItems.ingot_zirconium.get());
//        this.basicItem(ModItems.ingot_magnetized_tungsten.get());
//        this.basicItem(ModItems.ingot_solinium.get());
//        this.basicItem(ModItems.plate_iron.get());
//        this.basicItem(ModItems.fluorite.get());
//        this.basicItem(ModItems.nugget_zirconium.get());
//
//        this.basicItem(ModItems.ingot_advanced_alloy.get());
//        this.basicItem(ModItems.solid_fuel.get());
//        this.basicItem(ModItems.lignite.get());
//        this.basicItem(ModItems.powder_lignite.get());
//        this.basicItem(ModItems.powder_coal.get());
//        this.basicItem(ModItems.coke_coal.get());
//        this.basicItem(ModItems.coke_lignite.get());
//        this.basicItem(ModItems.coke_petroleum.get());
//        this.basicItem(ModItems.briquette_wood.get());
//        this.basicItem(ModItems.briquette_lignite.get());
//        this.basicItem(ModItems.briquette_coal.get());
//
////        this.basicItem(ModItems.detonator.get());
//        this.basicItem(ModItems.grenade_generic.get());
//        this.basicItem(ModItems.grenade_strong.get());
//        this.basicItem(ModItems.grenade_fire.get());
//        this.basicItem(ModItems.grenade_frag.get());
//        this.basicItem(ModItems.grenade_black_hole.get());
        this.basicItem(HBMItems.overlay_my_fluid.get());
        this.basicItem(HBMItems.BEDROCK_ORE.get());
        /* tool */
        this.basicItem(HBMItems.SCREWDRIVER.get());

        generateMissingSimpleItemModels();

        ResourceLocation u238m2 = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(HBMItems.INGOT_U238M2.get()));
        this.getBuilder(u238m2.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", new ResourceLocation(u238m2.getNamespace(), "item/ingot_u238m2"))
                .override()
                .predicate(HBM.rl("stage"), 1).model(this.basicItem(HBM.rl("item/hs-elements")))
                .predicate(HBM.rl("stage"), 2).model(this.basicItem(HBM.rl("item/hs-arsenic")))
                .predicate(HBM.rl("stage"), 3).model(this.basicItem(HBM.rl("item/hs-vault")))
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
//    // 产生可变化的实体
//    public void dynamicModel(Item item){
//        ResourceLocation rl = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item));
//        this.getBuilder(rl.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
//                .texture("layer0", new ResourceLocation(rl.getNamespace(), "item/" + rl.getPath()))
//                .override().model(this.basicItem(HBM.rl("item/ingot_nikonium"))).end();
//    }

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
