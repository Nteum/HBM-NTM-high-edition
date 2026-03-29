package com.hbm.Inventory.material;

import com.hbm.registries.ModTags;
import com.hbm.registries.ModTags.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;

import java.util.*;
/**
 * 物质形式遵循匠魂的数值，一个粒相当于16mb
 * */
public class HBMMatForm {
    public static Map<TagKey<Item>, HBMMatForm> MATTER_FORMATS = new HashMap<>();
    // 手动修改的
    public static final HBMMatForm ANY = new HBMMatForm(ModTags.Items.ANY, false, 0);
    public static final HBMMatForm ONLY_ORE = new HBMMatForm(Tags.Items.ORE_BEARING_GROUND_STONE, false, 0);
    public static final HBMMatForm ORE = new HBMMatForm(Tags.Items.ORES,false, 0);
    public static final HBMMatForm ORENETHER = new HBMMatForm(Tags.Items.ORES_IN_GROUND_NETHERRACK, false, 0);
    public static final HBMMatForm QUANTUM = new HBMMatForm(Items.QUANTUM, true, 2);
    public static final HBMMatForm NUGGET = new HBMMatForm(Tags.Items.NUGGETS, true, 16);
    public static final HBMMatForm TINY = new HBMMatForm(Items.TINY, false, 16);
    public static final HBMMatForm FRAGMENT = new HBMMatForm(Items.FRAGMENT, true, 16);
    public static final HBMMatForm DUSTTINY = new HBMMatForm(Items.SMALL_DUST, true, NUGGET.quantity);
    public static final HBMMatForm WIRE = new HBMMatForm(Items.WIRE, true, 18);
    public static final HBMMatForm BOLT = new HBMMatForm(Items.BOLT, true, 18);
    // 尚未修改的
    public static final HBMMatForm BILLET = new HBMMatForm(Items.BILLET, true, NUGGET.quantity * 6);
    public static final HBMMatForm INGOT = new HBMMatForm(Tags.Items.INGOTS, true, NUGGET.quantity * 9);
    public static final HBMMatForm GEM = new HBMMatForm(Tags.Items.GEMS, true, INGOT.quantity);
    public static final HBMMatForm CRYSTAL = new HBMMatForm(Items.CRYSTAL, true, INGOT.quantity);
    public static final HBMMatForm DUST = new HBMMatForm(Tags.Items.DUSTS, true, INGOT.quantity);
    public static final HBMMatForm DENSEWIRE = new HBMMatForm(Items.DENSEWIRE, true, INGOT.quantity);
    public static final HBMMatForm PLATE = new HBMMatForm(Items.PLATE, true, INGOT.quantity);
    public static final HBMMatForm CASTPLATE = new HBMMatForm(Items.CASTPLATE, true, INGOT.quantity * 3);
    public static final HBMMatForm WELDEDPLATE = new HBMMatForm(Items.WELDEDPLATE, true, INGOT.quantity * 6);
    public static final HBMMatForm SHELL = new HBMMatForm(Items.SHELL, true, INGOT.quantity * 4);
    public static final HBMMatForm PIPE = new HBMMatForm(Items.PIPE, true, INGOT.quantity * 3);
    public static final HBMMatForm QUART = new HBMMatForm(Items.QUART, true, 324);
    public static final HBMMatForm BLOCK = new HBMMatForm(Tags.Items.STORAGE_BLOCKS, true, INGOT.quantity * 9);
    // 枪械零件与构件
    public static final HBMMatForm LIGHTBARREL = new HBMMatForm(Items.LIGHTBARREL, true, INGOT.quantity * 3);
    public static final HBMMatForm HEAVYBARREL = new HBMMatForm(Items.HEAVYBARREL, true, INGOT.quantity * 6);
    public static final HBMMatForm LIGHTRECEIVER = new HBMMatForm(Items.LIGHTRECEIVER, true, INGOT.quantity * 4);
    public static final HBMMatForm HEAVYRECEIVER = new HBMMatForm(Items.HEAVYRECEIVER, true, INGOT.quantity * 9);
    public static final HBMMatForm MECHANISM = new HBMMatForm(Items.MECHANISM, true, INGOT.quantity * 4);
    public static final HBMMatForm STOCK = new HBMMatForm(Items.STOCK, true, INGOT.quantity * 4);
    public static final HBMMatForm GRIP = new HBMMatForm(Items.GRIP, true, INGOT.quantity * 2);

    /**
     * ====================================================================================
     * */
    TagKey<Item> format;
    Set<TagKey<Item>> content;
    boolean autoGen = true; // 是否自动生成
    public int quantity = 72;      // 等效物质的量，默认8为一个粒，72为一个锭
    public HBMMatForm(TagKey<Item> format, boolean autoGen, int quantity, TagKey<Item> ... keys){
        this.format = format;
        this.autoGen = autoGen;
        this.quantity = quantity;

        if (keys.length > 0){
            content = new HashSet<>();
            content.addAll(Arrays.stream(keys).toList());
        }

        MATTER_FORMATS.put(format, this);
    }

    public TagKey<Item> getFormat(){
        return this.format;
    }
}
