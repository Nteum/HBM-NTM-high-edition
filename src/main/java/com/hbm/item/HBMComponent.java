package com.hbm.item;

import com.hbm.datagen.LanguageProvider;
import com.hbm.item.env.ItemEggGlyphid;
import com.hbm.item.env.ItemEggGlyphidToBirth;
import com.hbm.item.misc.ItemCircuit;
import com.hbm.item.tool.BatteryItem;
import com.hbm.registries.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

//一些零部件
public class HBMComponent {
    // 记录所用矿物的注册物品，进行批量处理。
    private static final List<RegistryObject<Item>> matherialList = new ArrayList<>();
    // 线圈等物品，明明逻辑是 物品类型 + 材料 ，游戏内的名称需要颠倒过来
    private static final List<RegistryObject<Item>> partList = new ArrayList<>();
    public static RegistryObject<Item> LASER_CRYSTAL_DIGAMMA;
    //工业元件
    public static RegistryObject<Item> BATTERY_CREATIVE;
    public static RegistryObject<Item> BATTERY_GENERIC;
    public static RegistryObject<Item> BATTERY_ADVANCED;
    public static RegistryObject<Item> BATTERY_LITHIUM;

    public static RegistryObject<Item> EGG_GLYPHID;
    public static RegistryObject<Item> EGG_GLYPHID_TO_BIRTH;
    //==================元件 part======================
    public static RegistryObject<Item> CIRCUIT_BASIC;
    public static RegistryObject<Item> WIRE_FINE_ALUMINIUM;
    public static RegistryObject<Item> SHELL;
    public static RegistryObject<Item> DUCT_TAPE;
    //====================材料=============================
    public static RegistryObject<Item> STEEL_INGOT;
    public static RegistryObject<Item> STEEL_DUST;
    public static RegistryObject<Item> STEEL_PLATE;
    public static RegistryObject<Item> STEEL_SMALL_DUST;
    public static RegistryObject<Item> PLATE_TITANIUM;
    public static void register(DeferredRegister<Item> ITEMS){
        LASER_CRYSTAL_DIGAMMA = ITEMS.register("laser_crystal_digamma",()->new Item(new Item.Properties()));
        BATTERY_CREATIVE = ITEMS.register("battery_creative",()->new BatteryItem(-1, 1_000_000L, new Item.Properties().stacksTo(1)));
        BATTERY_GENERIC = ITEMS.register("battery_generic",()->new BatteryItem(false,5_000, 100, new Item.Properties()));
        BATTERY_ADVANCED = ITEMS.register("battery_advanced",()->new BatteryItem(false,60_000, 500, new Item.Properties()));
        BATTERY_LITHIUM = ITEMS.register("battery_lithium",()->new BatteryItem(false,250_000, 1000, new Item.Properties()));

        EGG_GLYPHID = ITEMS.register("egg_glyphid",()->new ItemEggGlyphid(new Item.Properties()));
        EGG_GLYPHID_TO_BIRTH = ITEMS.register("egg_glyphid_to_birth",()->new ItemEggGlyphidToBirth(new Item.Properties()));
        //==================元件 part======================
        CIRCUIT_BASIC = register(partList, "circuit_basic",()->new Item(new Item.Properties()));
        WIRE_FINE_ALUMINIUM = register(partList, "wire_aluminium",()->new Item(new Item.Properties()));
        SHELL = register(partList, "shell",()->new Item(new Item.Properties()));
        DUCT_TAPE = register(partList, "duct_tape",()->new Item(new Item.Properties()));
        //==================材料 material===================
        STEEL_SMALL_DUST = register(matherialList, "dust_small_steel", ()-> new Item(new Item.Properties()));
        STEEL_DUST = register(matherialList, "dust_steel", ()-> new Item(new Item.Properties()));
        STEEL_PLATE = register(matherialList, "plate_steel", ()-> new Item(new Item.Properties()));
        STEEL_INGOT = register(matherialList, "ingot_steel", ()-> new Item(new Item.Properties()));
        PLATE_TITANIUM = register(matherialList, "plate_titanium", ()-> new Item(new Item.Properties()));
    }
    public static void creativeTab(CreativeModeTab.Output pOutput){
        pOutput.accept(LASER_CRYSTAL_DIGAMMA.get());
        pOutput.accept(BATTERY_CREATIVE.get());
        pOutput.accept(BATTERY_GENERIC.get());
        pOutput.accept(BATTERY_ADVANCED.get());
        pOutput.accept(BATTERY_LITHIUM.get());

        pOutput.accept(EGG_GLYPHID.get());
        pOutput.accept(EGG_GLYPHID_TO_BIRTH.get());
        
        matherialList.forEach(itemRegistryObject -> pOutput.accept(itemRegistryObject.get()));
        partList.forEach(itemRegistryObject -> pOutput.accept(itemRegistryObject.get()));
    }
    public static void genModel(ItemModelProvider provider){
        provider.basicItem(LASER_CRYSTAL_DIGAMMA.get());
        provider.basicItem(BATTERY_CREATIVE.get());
        provider.basicItem(BATTERY_GENERIC.get());
        provider.basicItem(BATTERY_ADVANCED.get());
        provider.basicItem(BATTERY_LITHIUM.get());

        singleTexture(provider, EGG_GLYPHID.get(), "egg_glyphid_base");
        singleTexture(provider, EGG_GLYPHID_TO_BIRTH.get(), "egg_glyphid");

        matherialList.forEach(itemRegistryObject -> provider.basicItem(itemRegistryObject.get()));
        partList.forEach(itemRegistryObject -> provider.basicItem(itemRegistryObject.get()));
    }
    public static void languageSupport(LanguageProvider provider){
        provider.add(LASER_CRYSTAL_DIGAMMA.get(),"Layser crystal digamma");
        provider.add(BATTERY_CREATIVE.get(),"Creative battery");
        provider.add(BATTERY_GENERIC.get(),"Genetic battery");
        provider.add(BATTERY_ADVANCED.get(),"Advanced battery");
        provider.add(BATTERY_LITHIUM.get(),"Lithium battery");

        provider.add(EGG_GLYPHID.get(),"Glyphid Egg");
        provider.add(EGG_GLYPHID_TO_BIRTH.get(),"Glypid Egg will birth");

        // 所有物料的名称就是它们内部名称
        matherialList.forEach(itemRegistryObject -> provider.add(itemRegistryObject.get(), generateReversedName(itemRegistryObject.getId().getPath())));
        // 所有零件的名称是颠倒的名称
        partList.forEach(itemRegistryObject -> provider.add(itemRegistryObject.get(), generateReversedName(itemRegistryObject.getId().getPath())));
    }

    public static void singleTexture(ItemModelProvider provider, Item item, String textureName){
        ResourceLocation itemRL = ForgeRegistries.ITEMS.getKey(item);
        provider.getBuilder(itemRL.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", new ResourceLocation(itemRL.getNamespace(), "item/" + textureName));
    }
    protected static String generateOrderlyName(String itemId){
        return Arrays.stream(itemId.split("_")).map(s -> s.substring(0,1).toUpperCase() + s.substring(1)).reduce("",(r,id) -> r + " " + id);
    }
    protected static String generateReversedName(String itemId){
        List<String> strings = Arrays.stream(itemId.split("_")).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1)).toList();
        return strings.subList(0, strings.size() - 1).stream().reduce(strings.get(strings.size() - 1), (s, s1) -> s + " " + s1);
    }
    protected static RegistryObject<Item> register(List<RegistryObject<Item>> list, final String name, final Supplier<? extends Item> sup){
        RegistryObject<Item> registryObject = ModItems.ITEMS.register(name, sup);
        list.add(registryObject);
        return registryObject;
    }

    //===============enum==========================
    public enum EnumCircuitType {
        VACUUM_TUBE,
        CAPACITOR,
        CAPACITOR_TANTALIUM,
        PCB,
        SILICON,
        CHIP,
        CHIP_BISMOID,
        ANALOG,
        BASIC,
        ADVANCED,
        CAPACITOR_BOARD,
        BISMOID,
        CONTROLLER_CHASSIS,
        CONTROLLER,
        CONTROLLER_ADVANCED,
        QUANTUM,
        CHIP_QUANTUM,
        CONTROLLER_QUANTUM,
        ATOMIC_CLOCK,
    }
}
