package com.hbm.Inventory.material;

import com.hbm.HBM;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.recipe.ingredient.FluidStackIngredient;
import com.hbm.datagen.tag.FluidTagsGen;
import com.hbm.registries.HBMMatters;
import com.hbm.registries.ModTags;
import com.hbm.registries.RegistryHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Consumer;

public class HBMMatter{
    String name;
    TagKey<Item> matterKey;
    Map<TagKey<Item>, TagKey<Item>> shapes;         // 物品形状
    Map<TagKey<Block>, TagKey<Block>> blockShapes;  // 方块形状
    ModTags.TagGenEntry<Item> genEntry;
    ModTags.TagGenEntry<Block> blockGen;
    public int solidColorLight = 0xFF4A00;
    public int solidColorDark = 0x802000;
    public int moltenColor = 0xFF4A00;
    // 0 - NOT_SMELTABLE；1 - SMELTABLE；2 - VAPORIZES; 3 - BREAKS; 4 - ADDITIVE;
    public byte smeltProperty = 0;
    // 融化后的流体，用于兼容匠魂系统，不是所有matter都可融化
    RegistryObject<FluidType> fluidType;
    RegistryObject<Fluid> source;
    // 材料的转化
    HBMMatter convertMat;
    public int convIn = 0;
    public int convOut = 0;
    public HBMMatter(String name){
        this.name = name;
        this.matterKey = ModTags.Items.forgeTag(name);
    }
    public HBMMatter(String name, int color){
        this(name, color, color, color);
    }
    public HBMMatter(String name, int solidColorLight, int solidColorDark, int moltenColor){
        this(name);
        color(solidColorLight, solidColorDark, moltenColor);
    }
    public HBMMatter color(int solidColorLight, int solidColorDark, int moltenColor){
        this.solidColorLight = solidColorLight;
        this.solidColorDark = solidColorDark;
        this.moltenColor = moltenColor;
        return this;
    }
    @SafeVarargs
    public final HBMMatter shapes(TagKey<Item>... shapes){
        List<TagKey<Item>> itemTags = new ArrayList<>();
        List<TagKey<Block>> blockTags = new ArrayList<>();
        for (TagKey<Item> shape : shapes) {
            this.getShapes().put(shape, ModTags.Items.subTag(shape, matterKey));
            if (isPhysicallyPresent(shape)) {
                if (this.blockShapes == null) this.blockShapes = new HashMap<>();
                TagKey<Block> blockBase = ModTags.Blocks.convertToBlockTag(shape);
                this.blockShapes.put(blockBase, ModTags.Blocks.subBlockTag(blockBase, matterKey));
                blockTags.add(blockBase);
            }else {
                itemTags.add(shape);
            }
        }
        getGenEntry().addKeyAutogen(itemTags.toArray(TagKey[]::new));
        if (blockGen == null) blockGen = ModTags.Blocks.make(matterKey);
        blockGen.addKeyAutogen(blockTags.toArray(TagKey[]::new));
        return this;
    }
    // 添加既有的tag，不需要额外类推tag的存在
    public final HBMMatter shape(TagKey<Item> shape, TagKey<Item> element){
        this.getShapes().put(shape, element);
        this.getGenEntry().addKeyIn(element);
        return this;
    }
    private boolean isPhysicallyPresent(TagKey<Item> shape) {
        return ModTags.Blocks.BLOCK_SHAPES.contains(ModTags.Blocks.convertToBlockTag(shape));
    }
    public TagKey<Item> key(){
        return matterKey;
    }
    public String name(){
        return name;
    }
    public HBMMatter gen(Consumer<ModTags.TagGenEntry<Item>> consumer){
        if (genEntry == null) genEntry = ModTags.Items.make(matterKey);
        consumer.accept(genEntry);
        return this;
    }
    public HBMMatter convert(HBMMatter target, int convIn, int convOut){
        this.convertMat = target;
        this.convIn = convIn;
        this.convOut = convOut;
        return this;
    }
    public HBMMatter toFluid(int smeltProperty) {
        // 为每种金属生成唯一的 FluidType（用于区分颜色）
        // 这里可以巧妙地把颜色作为温度参考
        String descriptionId = "fluid." + HBM.MODID + "." + name;
        this.fluidType = ModFluids.FLUID_TYPES.register(name + "_type", () -> new FluidType(FluidType.Properties.create().temperature(moltenColor).descriptionId(descriptionId)));
        this.source = ModFluids.FLUIDS.register("molten_" + name, () -> new ForgeFlowingFluid.Source(new ForgeFlowingFluid.Properties(fluidType, this.source, this.source)));
        this.smeltProperty = (byte) smeltProperty;
        if (HBM.isDataGen()){
            FluidTagsGen.register(ModTags.Fluids.forgeTag(this.source.getId().getPath()), this.source);
            FluidTagsGen.register(ModTags.Fluids.MOLTEN, this.source);
            LanguageProvider.READY_TO_ADD.put(descriptionId, RegistryHelper.generateOrderlyName(this.name));
        }
        return this;
    }

    public Map<TagKey<Item>, TagKey<Item>> getShapes(){
        if (this.shapes == null){
            this.shapes = new HashMap<>();
        }
        return this.shapes;
    }
    public ModTags.TagGenEntry<Item> getGenEntry(){
        if (this.genEntry == null) this.genEntry = ModTags.Items.make(matterKey);
        return this.genEntry;
    }
    public HBMMatter getConvertMat(){
        return this.convertMat;
    }
    public TagKey<Item> getShape(TagKey<Item> shape){
        return this.shapes.get(shape);
    }
    public Fluid fluid(){
        return this.source == null ? null : this.source.get();
    }
    public FluidType fluidType(){
        return this.fluidType == null ? null : this.fluidType.get();
    }
    public TagKey<Fluid> fluidTag(){
        return ModTags.Fluids.forgeTag(this.source.getId().getPath());
    }
    public boolean canMolten(){
        return (smeltProperty == 1 || smeltProperty == 4) && this.source != null && this.fluidType != null;
    }
    // 锭的tag
    public TagKey<Item> ingot(){
        return getShape(Tags.Items.INGOTS);
    }
    // 粒的tag
    public TagKey<Item> nugget(){
        return getShape(Tags.Items.NUGGETS);
    }
    // 板
    public TagKey<Item> plate(){
        return getShape(ModTags.Items.PLATE);
    }
}