package com.hbm.datagen.model;

import com.hbm.HBM;
import com.hbm.block.env.BedRockOre;
import com.hbm.registries.ModBlocks;
import com.hbm.render.model.Models;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class BlockStateGen extends BlockStateProvider {
    private List<ICategoryStateProvider> categoryStateProviders = new ArrayList<>();
    // 用泥土方块作为占位模型
    private ModelFile BLANK_MODEL = models().getExistingFile(new ResourceLocation("block/dirt"));
    public BlockStateGen(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
        categoryStateProviders.add(new DecorateBlockStateProvider(this));
        categoryStateProviders.add(new ObjMachineBlockStateProvider(this));
    }

    @Override
    protected void registerStatesAndModels() {
        for (ICategoryStateProvider categoryStateProvider : categoryStateProviders) {
            categoryStateProvider.registerStatesAndModels();
        }
        ModBlocks.genModel(this);
        //简单方块和物品
        horizontalBlockWithItem(ModBlocks.machine_battery.get(),this.models().orientable("machine_battery", new ResourceLocation(HBM.MODID, "block/battery_side"), new ResourceLocation(HBM.MODID, "block/battery_front"), new ResourceLocation(HBM.MODID, "block/battery_top")));
        horizontalBlockWithItem(ModBlocks.machine_lithium_battery.get(),this.models().orientable("machine_lithium_battery", new ResourceLocation(HBM.MODID, "block/battery_lithium_side"), new ResourceLocation(HBM.MODID, "block/battery_lithium_front"), new ResourceLocation(HBM.MODID, "block/battery_lithium_top")));
        horizontalBlockWithItem(ModBlocks.machine_schrabidium_battery.get(),this.models().orientable("machine_schrabidium_battery", new ResourceLocation(HBM.MODID, "block/battery_schrabidium_side"), new ResourceLocation(HBM.MODID, "block/battery_schrabidium_front"), new ResourceLocation(HBM.MODID, "block/battery_schrabidium_top")));
        horizontalBlockWithItem(ModBlocks.machine_dineutronium_battery.get(),this.models().orientable("machine_dineutronium_battery", new ResourceLocation(HBM.MODID, "block/battery_dineutronium_side"), new ResourceLocation(HBM.MODID, "block/battery_dineutronium_front"), new ResourceLocation(HBM.MODID, "block/battery_dineutronium_top")));

        ModelFile.ExistingModelFile conveyorModel = this.models().getExistingFile(new ResourceLocation(HBM.MODID, "block/conveyor"));
        horizontalBlock(ModBlocks.conveyor.get(),conveyorModel);
        simpleBlockItem(ModBlocks.conveyor.get(),conveyorModel);
        //多状态的方块和物品
        //obj机器
//        addObjHorizonalModel(ModBlocks.anvil_iron.get(),"block/anvil/anvil_iron");
//        addObjHorizonalModel(ModBlocks.anvil_bismuth.get(),"block/anvil/anvil_bismuth");
//        addObjHorizonalModel(ModBlocks.anvil_desh.get(),"block/anvil/anvil_desh");
//        addObjHorizonalModel(ModBlocks.machine_cracking_tower.get(),"block/cracking_tower/machine_cracking_tower");

//        var press_body_model = this.models().getExistingFile(new ResourceLocation(HBM.MODID, "block/press/press_body"));
//        var press_head_model = this.models().getExistingFile(new ResourceLocation(HBM.MODID, "block/press/press_head"));
//        this.simpleBlockWithItem(ModBlocks.machine_press.get(),press_body_model);
//        this.simpleBlock(ModBlocks.part_press_head.get(),press_head_model);

//        var assembler_body_model = this.models().getExistingFile(new ResourceLocation(HBM.MODID, "block/assembler/assembler_body"));
//        var assembler_arm_model = this.models().getExistingFile(new ResourceLocation(HBM.MODID, "block/assembler/assembler_arm"));
//        var assembler_cog_model = this.models().getExistingFile(new ResourceLocation(HBM.MODID, "block/assembler/assembler_cog"));
//        var assembler_slider_model = this.models().getExistingFile(new ResourceLocation(HBM.MODID, "block/assembler/assembler_slider"));
//        this.horizontalBlock(ModBlocks.machine_assembler.get(),assembler_body_model);
//        this.simpleBlockItem(ModBlocks.machine_assembler.get(),assembler_body_model);
        //坩埚模型
        var crucible_model = this.models().getExistingFile(Models.CRUCIBLE);
        this.horizontalBlock(ModBlocks.machine_crucible.get(),crucible_model);
        this.simpleBlockItem(ModBlocks.machine_crucible.get(),crucible_model);

//        ModelFile.ExistingModelFile bomb_model_fatman = this.models().getExistingFile(new ResourceLocation(HBM.MODID, "block/bomb/fat_man"));
//        this.simpleBlockItem(ModBlocks.bomb_fat_man.get(),bomb_model_fatman);
        ModelFile.ExistingModelFile bomb_model_boy = this.models().getExistingFile(HBM.rl("block/bomb/boy"));
        this.simpleBlockItem(ModBlocks.bomb_boy.get(),bomb_model_boy);
        ModelFile.ExistingModelFile bomb_model_custom = this.models().getExistingFile(HBM.rl("block/bomb/custom"));
        this.simpleBlockItem(ModBlocks.bomb_custom.get(),bomb_model_custom);

        //线缆
        cableBlockWithItem();
        //电池

        // 默认贴图
        this.horizontalBlock(ModBlocks.PRESS.get(), blockState -> BLANK_MODEL);
        this.builtInBlockItem(ModBlocks.PRESS.get());
    }

    public void horizontalBlockWithItem(Block block){
        horizontalBlockWithItem(block, key(block).getPath());
    }
    public void horizontalBlockWithItem(Block block, String path){
        ModelFile.ExistingModelFile model = models().getExistingFile(HBM.rl(path));
        horizontalBlockWithItem(block, model);
    }
    public void horizontalBlockWithItem(Block block, ModelFile model){
        horizontalBlock(block,model);
        simpleBlockItem(block,model);
    }

    public void addObjHorizonalModel(Block block,String name){
        ModelFile.ExistingModelFile existingFile = this.models().getExistingFile(new ResourceLocation(HBM.MODID, name));
        this.horizontalBlock(block,existingFile);
        this.simpleBlockItem(block,existingFile);
    }
    /** 添加有两个状态，并带有水平方向的方块（HBM的方块机器大部分属于此列） */
    private void addBooleanStateWithFace(Block block, BooleanProperty booleanProperty, ModelFile model1, ModelFile model2){
        this.getVariantBuilder(block)
                .forAllStates(state -> {
                    Boolean value = state.getValue(booleanProperty);
                    return ConfiguredModel.builder()
                            .modelFile(value == Boolean.FALSE ? model1 : model2)
                            .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                            .build();
                });
        this.simpleBlockItem(block,model1);
    }

    public  <E extends Enum<E> & StringRepresentable> void addEnumStateBlock(Block block, EnumProperty<E> enumProperty, Function<Enum<E>, ModelFile> enumModelFileFunction){
        this.getVariantBuilder(block)
                .forAllStates(state -> {
                    E value = state.getValue(enumProperty);
                    return ConfiguredModel.builder()
                            .modelFile(enumModelFileFunction.apply(value))
                            .build();
                });
    }

    public void addIntStateCubeAllBlock(Block block, IntegerProperty integerProperty){
        ResourceLocation blockTexture = blockTexture(block);
        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        VariantBlockStateBuilder.PartialBlockstate partialState = variantBuilder.partialState();
        for (Integer value : integerProperty.getPossibleValues()) {
            partialState.with(integerProperty, value).addModels(ConfiguredModel.builder().modelFile(models().cubeAll(path(block) + "_" + value, blockTexture.withSuffix("_" + value))).buildLast());
        }
    }

    public ModelFile enumModelFileFunction_BedRockOreType(BedRockOre.BedRockOreType value) {
        return switch (value){
            case IRON -> models().getExistingFile(HBM.rl("block/env/bedrock_ore_iron"));
            case COPPER -> models().getExistingFile(HBM.rl("block/env/bedrock_ore_copper"));
            default -> null;
        };
    }

    private void cableBlockWithItem(){
        ModelFile.ExistingModelFile inventory = this.models().getExistingFile(HBM.rl("block/pipes/cable_neo"));
        ModelFile.ExistingModelFile core = this.models().getExistingFile(HBM.rl("block/pipes/cable_core"));
        ModelFile.ExistingModelFile side = this.models().getExistingFile(HBM.rl("block/pipes/cable_side"));
        MultiPartBlockStateBuilder builder = this.getMultipartBuilder(ModBlocks.RED_CABLE.get()).part().modelFile(core).addModel().end();
        sixWayMultipart(builder,side);
        simpleBlockItem(ModBlocks.RED_CABLE.get(), inventory);
    }
    public void sixWayMultipart(MultiPartBlockStateBuilder builder, ModelFile side) {
        PipeBlock.PROPERTY_BY_DIRECTION.entrySet().forEach(e -> {
            Direction dir = e.getKey();
            if (dir.getAxis().isHorizontal()) {
                builder.part().modelFile(side).rotationY((((int) dir.toYRot()) + 180) % 360).uvLock(true).addModel()
                        .condition(e.getValue(), true);
            }else {
                builder.part().modelFile(side).rotationX(dir == Direction.UP ? -90 : 90).uvLock(true).addModel()
                        .condition(e.getValue(), true);
            }
        });
    }

    /// REMAKE ///
    public String path(Block block){
        return key(block).getPath();
    }
    public ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    public void simpleBlockWithItem(Block block){
        simpleBlockWithItem(block, models().cubeAll(path(block), key(block).withPrefix("block/")));
    }
    public void builtInBlockItem(Block block){
        itemModels().getBuilder(key(block).getPath()).parent(new ModelFile.UncheckedModelFile("builtin/entity"));
    }
    // 只有前面和侧面两个贴图的方块
    public void frontSideBlockWithItem(Block block){
        ResourceLocation key = key(block);
        ResourceLocation texLoc = key.withPrefix("block/");
        horizontalBlockWithItem(block, models().orientable(path(block), texLoc.withSuffix("_side"), texLoc.withSuffix("_front"), texLoc.withSuffix("_side")));
    }
    public void frontSideTopBlockWithItem(Block block){
        ResourceLocation key = key(block);
        ResourceLocation texLoc = key.withPrefix("block/");
        horizontalBlockWithItem(block, models().orientable(path(block), texLoc.withSuffix("_side"), texLoc.withSuffix("_front"), texLoc.withSuffix("_top")));
    }
    /**
     * 根据已有的json模型文件找模型，
     * */
    public void existingFileBlockWithItem(Block block){
        if (block.defaultBlockState().hasProperty(BlockStateProperties.HORIZONTAL_FACING)){
            horizontalBlockWithItem(block, models().getExistingFile(key(block).withPrefix("block/")));
        }else if (block.defaultBlockState().getProperties().isEmpty()){
            simpleBlockWithItem(block, models().getExistingFile(key(block).withPrefix("block/")));
        }
    }
    /** 添加有水平方向且含布尔值的方块（HBM的方块机器大部分属于此列） */
    private void addBooleanStateWithFace(Block block, BooleanProperty booleanProperty){
        ResourceLocation key = key(block);
        ResourceLocation texLoc = key.withPrefix("block/");
        BlockModelBuilder defaultState;
        BlockModelBuilder alterState;
        if (!this.models().existingFileHelper.exists(texLoc.withSuffix("_bottom"), PackType.CLIENT_RESOURCES)) {
            defaultState = this.models().orientable(key.getPath(),texLoc.withSuffix("_side"), texLoc.withSuffix("_front"), texLoc.withSuffix("_top"));
            alterState = this.models().orientable(key.getPath(),texLoc.withSuffix("_side"), texLoc.withSuffix("_front_alter"), texLoc.withSuffix("_top"));
        }else {
            defaultState = this.models().orientableWithBottom(key.getPath(),texLoc.withSuffix("_side"), texLoc.withSuffix("_front"),texLoc.withSuffix("_bottom"), texLoc.withSuffix("_top"));
            alterState = models().orientableWithBottom(key.getPath(),texLoc.withSuffix("_side"), texLoc.withSuffix("_front"), texLoc.withSuffix("_bottom"),
                    texLoc.withSuffix(this.models().existingFileHelper.exists(texLoc.withSuffix("_top_alter"), PackType.CLIENT_RESOURCES) ?  "_top_alter" : "_top"));
        }
        addBooleanStateWithFace(block, booleanProperty, defaultState, alterState);
    }
    // 参考合合金炉的模型，分亮 / 灭两种状态，模型有前面、侧面、上下面
    public void difuranceBlockWithItem(Block block){
        addBooleanStateWithFace(block, BlockStateProperties.LIT);
    }
}
