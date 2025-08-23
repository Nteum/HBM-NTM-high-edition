package com.hbm.item;

import com.hbm.datagen.LanguageProvider;
import com.hbm.item.env.ItemEggGlyphid;
import com.hbm.item.env.ItemEggGlyphidToBirth;
import com.hbm.item.tool.BatteryItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

//一些零部件
public class HBMComponent {
    public static RegistryObject<Item> LASER_CRYSTAL_DIGAMMA;
    //工业元件
    public static RegistryObject<Item> BATTERY_CREATIVE;
    public static RegistryObject<Item> BATTERY_GENERIC;
    public static RegistryObject<Item> BATTERY_ADVANCED;
    public static RegistryObject<Item> BATTERY_LITHIUM;

    public static RegistryObject<Item> EGG_GLYPHID;
    public static RegistryObject<Item> EGG_GLYPHID_TO_BIRTH;
    public static void register(DeferredRegister<Item> ITEMS){
        LASER_CRYSTAL_DIGAMMA = ITEMS.register("laser_crystal_digamma",()->new Item(new Item.Properties()));
        BATTERY_CREATIVE = ITEMS.register("battery_creative",()->new BatteryItem(-1, 1_000_000L, new Item.Properties().stacksTo(1)));
        BATTERY_GENERIC = ITEMS.register("battery_generic",()->new BatteryItem(false,5_000, 100, new Item.Properties()));
        BATTERY_ADVANCED = ITEMS.register("battery_advanced",()->new BatteryItem(false,60_000, 500, new Item.Properties()));
        BATTERY_LITHIUM = ITEMS.register("battery_lithium",()->new BatteryItem(false,250_000, 1000, new Item.Properties()));

        EGG_GLYPHID = ITEMS.register("egg_glyphid",()->new ItemEggGlyphid(new Item.Properties()));
        EGG_GLYPHID_TO_BIRTH = ITEMS.register("egg_glyphid_to_birth",()->new ItemEggGlyphidToBirth(new Item.Properties()));
    }
    public static void creativeTab(CreativeModeTab.Output pOutput){
        pOutput.accept(LASER_CRYSTAL_DIGAMMA.get());
        pOutput.accept(BATTERY_CREATIVE.get());
//        ItemStack itemStack = new ItemStack(BATTERY_GENERIC.get());
//        itemStack.setDamageValue(itemStack.getMaxDamage());
//        pOutput.accept(itemStack);
        pOutput.accept(BATTERY_GENERIC.get());
        pOutput.accept(BATTERY_ADVANCED.get());
        pOutput.accept(BATTERY_LITHIUM.get());
        pOutput.accept(EGG_GLYPHID.get());
        pOutput.accept(EGG_GLYPHID_TO_BIRTH.get());
    }
    public static void genModel(ItemModelProvider provider){
        provider.basicItem(LASER_CRYSTAL_DIGAMMA.get());
        provider.basicItem(BATTERY_CREATIVE.get());
        provider.basicItem(BATTERY_GENERIC.get());
        provider.basicItem(BATTERY_ADVANCED.get());
        provider.basicItem(BATTERY_LITHIUM.get());

        singleTexture(provider, EGG_GLYPHID.get(), "egg_glyphid_base");
        singleTexture(provider, EGG_GLYPHID_TO_BIRTH.get(), "egg_glyphid");
    }
    public static void languageSupport(LanguageProvider provider){
        provider.add(LASER_CRYSTAL_DIGAMMA.get(),"Layser crystal digamma");
        provider.add(BATTERY_CREATIVE.get(),"Creative battery");
        provider.add(BATTERY_GENERIC.get(),"Genetic battery");
        provider.add(BATTERY_ADVANCED.get(),"Advanced battery");
        provider.add(BATTERY_LITHIUM.get(),"Lithium battery");

        provider.add(EGG_GLYPHID.get(),"Glyphid Egg");
        provider.add(EGG_GLYPHID_TO_BIRTH.get(),"Glypid Egg will birth");
    }

    public static void singleTexture(ItemModelProvider provider, Item item, String textureName){
        ResourceLocation itemRL = ForgeRegistries.ITEMS.getKey(item);
        provider.getBuilder(itemRL.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", new ResourceLocation(itemRL.getNamespace(), "item/" + textureName));
    }
}
