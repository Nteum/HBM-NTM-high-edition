package com.hbm.datagen;

import com.hbm.DalisFunMod;
import com.hbm.block.ModBlocks;
import com.hbm.block.decorate.ConcreteColored;
import com.hbm.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.Optional;

public class ModModelGenerator extends FabricModelProvider {
    public ModModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        //registerSimpleCubeAll：注册各各面相同的物体
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ore_uranium);
        //registerSingleton：注册无状态多面不同的物体
        //TextureMap：用于添加方块不同面和贴图路径的对应关系
        //Model：渲染的方式，在Models中有原版所有的贴图方式
        TextureMap battery_schrabidium_texturemap = new TextureMap()
                .put(TextureKey.FRONT,new Identifier(DalisFunMod.MOD_ID,ModelIds.getBlockModelId(ModBlocks.battery_schrabidium).getPath()+"_front"))
                .put(TextureKey.SIDE,new Identifier(DalisFunMod.MOD_ID,ModelIds.getBlockModelId(ModBlocks.battery_schrabidium).getPath()+"_side"))
                .put(TextureKey.TOP,new Identifier(DalisFunMod.MOD_ID,ModelIds.getBlockModelId(ModBlocks.battery_schrabidium).getPath()+"_top"));
        blockStateModelGenerator.registerSingleton(ModBlocks.battery_schrabidium,battery_schrabidium_texturemap,Models.ORIENTABLE);
        //混凝土
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.concrete);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.brick_concrete);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.brick_concrete_mossy);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.brick_concrete_cracked);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.brick_concrete_broken);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.brick_concrete_marked);
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(ModBlocks.concrete_colored)
                .coordinate(BlockStateVariantMap.create(ConcreteColored.concrete_color)
                        .register((Integer) 0,BlockStateVariant.create().put(VariantSettings.MODEL,Models.CUBE_ALL.upload(new Identifier(DalisFunMod.MOD_ID,"block/concrete_white"),
                                new TextureMap().put(TextureKey.ALL,new Identifier(DalisFunMod.MOD_ID,"block/concrete_white")), blockStateModelGenerator.modelCollector)))
                        .register((Integer) 1,BlockStateVariant.create().put(VariantSettings.MODEL,Models.CUBE_ALL.upload(new Identifier(DalisFunMod.MOD_ID,"block/concrete_orange"),
                                new TextureMap().put(TextureKey.ALL,new Identifier(DalisFunMod.MOD_ID,"block/concrete_orange")), blockStateModelGenerator.modelCollector)))
                        .register((Integer) 2,BlockStateVariant.create().put(VariantSettings.MODEL,Models.CUBE_ALL.upload(new Identifier(DalisFunMod.MOD_ID,"block/concrete_magenta"),
                                new TextureMap().put(TextureKey.ALL,new Identifier(DalisFunMod.MOD_ID,"block/concrete_magenta")), blockStateModelGenerator.modelCollector)))
                        .register((Integer) 3,BlockStateVariant.create().put(VariantSettings.MODEL,Models.CUBE_ALL.upload(new Identifier(DalisFunMod.MOD_ID,"block/concrete_light_blue"),
                                new TextureMap().put(TextureKey.ALL,new Identifier(DalisFunMod.MOD_ID,"block/concrete_light_blue")), blockStateModelGenerator.modelCollector)))
                        .register((Integer) 4,BlockStateVariant.create().put(VariantSettings.MODEL,Models.CUBE_ALL.upload(new Identifier(DalisFunMod.MOD_ID,"block/concrete_yellow"),
                                new TextureMap().put(TextureKey.ALL,new Identifier(DalisFunMod.MOD_ID,"block/concrete_yellow")), blockStateModelGenerator.modelCollector)))
                        .register((Integer) 5,BlockStateVariant.create().put(VariantSettings.MODEL,Models.CUBE_ALL.upload(new Identifier(DalisFunMod.MOD_ID,"block/concrete_lime"),
                                new TextureMap().put(TextureKey.ALL,new Identifier(DalisFunMod.MOD_ID,"block/concrete_lime")), blockStateModelGenerator.modelCollector)))
                        .register((Integer) 6,BlockStateVariant.create().put(VariantSettings.MODEL,Models.CUBE_ALL.upload(new Identifier(DalisFunMod.MOD_ID,"block/concrete_pink"),
                                new TextureMap().put(TextureKey.ALL,new Identifier(DalisFunMod.MOD_ID,"block/concrete_pink")), blockStateModelGenerator.modelCollector)))
                        .register((Integer) 7,BlockStateVariant.create().put(VariantSettings.MODEL,Models.CUBE_ALL.upload(new Identifier(DalisFunMod.MOD_ID,"block/concrete_gray"),
                                new TextureMap().put(TextureKey.ALL,new Identifier(DalisFunMod.MOD_ID,"block/concrete_gray")), blockStateModelGenerator.modelCollector)))
                        .register((Integer) 8,BlockStateVariant.create().put(VariantSettings.MODEL,Models.CUBE_ALL.upload(new Identifier(DalisFunMod.MOD_ID,"block/concrete_colored"),//默认
                                new TextureMap().put(TextureKey.ALL,new Identifier(DalisFunMod.MOD_ID,"block/concrete")), blockStateModelGenerator.modelCollector)))
                        .register((Integer) 9,BlockStateVariant.create().put(VariantSettings.MODEL,Models.CUBE_ALL.upload(new Identifier(DalisFunMod.MOD_ID,"block/concrete_cyan"),
                                new TextureMap().put(TextureKey.ALL,new Identifier(DalisFunMod.MOD_ID,"block/concrete_cyan")), blockStateModelGenerator.modelCollector)))
                        .register((Integer) 10,BlockStateVariant.create().put(VariantSettings.MODEL,Models.CUBE_ALL.upload(new Identifier(DalisFunMod.MOD_ID,"block/concrete_purple"),
                                new TextureMap().put(TextureKey.ALL,new Identifier(DalisFunMod.MOD_ID,"block/concrete_purple")), blockStateModelGenerator.modelCollector)))
                        .register((Integer) 11,BlockStateVariant.create().put(VariantSettings.MODEL,Models.CUBE_ALL.upload(new Identifier(DalisFunMod.MOD_ID,"block/concrete_blue"),
                                new TextureMap().put(TextureKey.ALL,new Identifier(DalisFunMod.MOD_ID,"block/concrete_blue")), blockStateModelGenerator.modelCollector)))
                        .register((Integer) 12,BlockStateVariant.create().put(VariantSettings.MODEL,Models.CUBE_ALL.upload(new Identifier(DalisFunMod.MOD_ID,"block/concrete_brown"),
                                new TextureMap().put(TextureKey.ALL,new Identifier(DalisFunMod.MOD_ID,"block/concrete_brown")), blockStateModelGenerator.modelCollector)))
                        .register((Integer) 13,BlockStateVariant.create().put(VariantSettings.MODEL,Models.CUBE_ALL.upload(new Identifier(DalisFunMod.MOD_ID,"block/concrete_green"),
                                new TextureMap().put(TextureKey.ALL,new Identifier(DalisFunMod.MOD_ID,"block/concrete_green")), blockStateModelGenerator.modelCollector)))
                        .register((Integer) 14,BlockStateVariant.create().put(VariantSettings.MODEL,Models.CUBE_ALL.upload(new Identifier(DalisFunMod.MOD_ID,"block/concrete_red"),
                                new TextureMap().put(TextureKey.ALL,new Identifier(DalisFunMod.MOD_ID,"block/concrete_red")), blockStateModelGenerator.modelCollector)))
                        .register((Integer) 15,BlockStateVariant.create().put(VariantSettings.MODEL,Models.CUBE_ALL.upload(new Identifier(DalisFunMod.MOD_ID,"block/concrete_black"),
                                new TextureMap().put(TextureKey.ALL,new Identifier(DalisFunMod.MOD_ID,"block/concrete_black")), blockStateModelGenerator.modelCollector)))
//                        .register((Integer) 16,BlockStateVariant.create().put(VariantSettings.MODEL,Models.CUBE_ALL.upload(new Identifier(DalisFunMod.MOD_ID,"block/concrete_colored"),
//                                new TextureMap().put(TextureKey.ALL,new Identifier(DalisFunMod.MOD_ID,"block/concrete")), blockStateModelGenerator.modelCollector)))
                ));
//        blockStateModelGenerator.registerSingleton(ModBlocks.concrete,new TextureMap().put(TextureKey.ALL,TextureMap.getSubId(ModBlocks.concrete,"")));
        //注册具有状态的物品，示例是一个换皮的红石灯
        //createSubModel：可以用来生成代表一个状态的model
        //先生成各个子状态的model，然后再一并注册到blockStateCollector里面
        Identifier lamp_tritium_blue_on = blockStateModelGenerator.createSubModel(ModBlocks.lamp_tritium_blue, "_on",Models.CUBE_ALL,TextureMap::all);
        Identifier lamp_tritium_blue_off = blockStateModelGenerator.createSubModel(ModBlocks.lamp_tritium_blue, "_off",Models.CUBE_ALL,TextureMap::all);
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(ModBlocks.lamp_tritium_blue)
                .coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT,lamp_tritium_blue_on,lamp_tritium_blue_off)));
        //电炉子：一个有点燃熄灭两种状态并且有方向的方块
        //方法就是首先找到一个原版有的方块，然后挨个替换它的面，然后注册上去
        //其实完全可以更简单的，但是偏偏图片后缀明明就是一个on和一个off，这和mc的方式并不兼容，最后只能用笨办法一个一个改
        //BlockStateVariantMap：方块的一个状态变量的不同取值和它的模型的对应关系
        //VariantsBlockStateSupplier 通过 coordinate 这个函数添加新的状态变量
        Identifier machine_electric_furnace_on = TexturedModel.ORIENTABLE_WITH_BOTTOM.get(Blocks.SMOKER).textures(textureMap -> {
            textureMap.put(TextureKey.FRONT, TextureMap.getSubId(ModBlocks.machine_electric_furnace, "_front_on"))
                    .put(TextureKey.SIDE, TextureMap.getSubId(ModBlocks.machine_electric_furnace, "_side"))
                    .put(TextureKey.BOTTOM, TextureMap.getSubId(ModBlocks.machine_electric_furnace, "_bottom"))
                    .put(TextureKey.TOP, TextureMap.getSubId(ModBlocks.machine_electric_furnace, "_top"));
        }).upload(ModBlocks.machine_electric_furnace, "_on", blockStateModelGenerator.modelCollector);
        Identifier machine_electric_furnace_off = TexturedModel.ORIENTABLE_WITH_BOTTOM.get(Blocks.SMOKER).textures(textureMap -> {
            textureMap.put(TextureKey.FRONT, TextureMap.getSubId(ModBlocks.machine_electric_furnace, "_front_off"))
                    .put(TextureKey.SIDE, TextureMap.getSubId(ModBlocks.machine_electric_furnace, "_side"))
                    .put(TextureKey.BOTTOM, TextureMap.getSubId(ModBlocks.machine_electric_furnace, "_bottom"))
                    .put(TextureKey.TOP, TextureMap.getSubId(ModBlocks.machine_electric_furnace, "_top"));
        }).upload(ModBlocks.machine_electric_furnace, "_off", blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(ModBlocks.machine_electric_furnace)
                .coordinate(BlockStateModelGenerator.createBooleanModelMap(Properties.LIT,machine_electric_furnace_on,machine_electric_furnace_off))
                .coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        Item tempItem = null;
        //通过反射添加物品的简单模型（仅限于简单模型，复杂模型还需另想办法）
        Class<ModItems> classModItems = ModItems.class;
        Field[] fields = classModItems.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == Item.class) {
                try {
                    tempItem = (Item) field.get(null);
                    if (tempItem instanceof ArmorItem) {
                        itemModelGenerator.registerArmor((ArmorItem)tempItem);
                        continue;
                    }
                    itemModelGenerator.register(tempItem, Models.GENERATED);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        itemModelGenerator.register(ModBlocks.machine_electric_furnace.asItem(),registerItemBlockModelWithState(ModBlocks.machine_electric_furnace,"_off"));
        itemModelGenerator.register(ModBlocks.lamp_tritium_blue.asItem(),registerItemBlockModelWithState(ModBlocks.lamp_tritium_blue,"_off"));
        itemModelGenerator.register(ModBlocks.concrete_colored.asItem(),registerItemBlockModel(ModBlocks.concrete_colored));
    }
    private static Model registerItemBlockModel(Block parent, TextureKey... requiredTextureKeys) {
        String name = ModelIds.getBlockModelId(parent).getPath();   // 获取方块的路径ID
        return new Model(Optional.of(new Identifier(DalisFunMod.MOD_ID, name)), Optional.empty(), requiredTextureKeys);
    }
    private static Model registerItemBlockModelWithState(Block parent, String suffix, TextureKey... requiredTextureKeys) {
        String name = ModelIds.getBlockModelId(parent).getPath();   // 获取方块的路径ID
        return new Model(Optional.of(new Identifier(DalisFunMod.MOD_ID, name + suffix)), Optional.empty(), requiredTextureKeys);
    }
}
