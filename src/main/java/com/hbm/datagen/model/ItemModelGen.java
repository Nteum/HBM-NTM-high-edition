package com.hbm.datagen.model;

import com.hbm.HBM;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.item.HBMComponent;
import com.hbm.item.HBMWeapon;
import com.hbm.item.HBMtools;
import com.hbm.registries.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelGen extends ItemModelProvider {
    public ItemModelGen(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        HBMtools.genModel(this);
        HBMComponent.genModel(this);
        HBMWeapon.genModel(this);
        ModFluids.bucketModel(this);
        this.basicItem(ModItems.ingot_steel.get());
        this.basicItem(ModItems.ingot_red_copper.get());
        this.basicItem(ModItems.ingot_tungsten.get());
        this.basicItem(ModItems.ingot_aluminium.get());
        this.basicItem(ModItems.ingot_lead.get());
        this.basicItem(ModItems.ingot_zirconium.get());
        this.basicItem(ModItems.ingot_magnetized_tungsten.get());
        this.basicItem(ModItems.ingot_solinium.get());

        this.basicItem(ModItems.plate_steel.get());
        this.basicItem(ModItems.plate_iron.get());

        this.basicItem(ModItems.fluorite.get());

        this.basicItem(ModItems.nugget_zirconium.get());

//        this.singleTexture("coke",new ResourceLocation("minecraft:item/generated"),"layer0",
//                new ResourceLocation(HBMxx.MODID,"item/coke.coal"));
        this.basicItem(ModItems.ingot_advanced_alloy.get());
        this.basicItem(ModItems.solid_fuel.get());
        this.basicItem(ModItems.lignite.get());
        this.basicItem(ModItems.powder_lignite.get());
        this.basicItem(ModItems.powder_coal.get());
        this.basicItem(ModItems.coke_coal.get());
        this.basicItem(ModItems.coke_lignite.get());
        this.basicItem(ModItems.coke_petroleum.get());
        this.basicItem(ModItems.briquette_wood.get());
        this.basicItem(ModItems.briquette_lignite.get());
        this.basicItem(ModItems.briquette_coal.get());

        this.basicItem(ModItems.detonator.get());
        this.basicItem(ModItems.grenade_generic.get());
        this.basicItem(ModItems.grenade_strong.get());
        this.basicItem(ModItems.grenade_fire.get());
        this.basicItem(ModItems.grenade_frag.get());
        this.basicItem(ModItems.grenade_black_hole.get());

//        this.basicItem(ModItems.bucket_irradiated_water.get());
//        this.basicItem(ModItems.bucket_irradiated_polluted.get());
//        this.basicItem(ModItems.bucket_sulfuric_acid.get());
        this.basicItem(ModItems.overlay_my_fluid.get());

        this.basicItem(ModItems.BEDROCK_ORE.get());
        /* tool */
        this.basicItem(ModItems.SCREWDRIVER.get());
    }

    public void registerOrdinaryItemModel(String key){
        this.singleTexture(key,new ResourceLocation("item/generated"),"layer0"
                ,new ResourceLocation(HBM.MODID, "item/" + key));
    }
}
