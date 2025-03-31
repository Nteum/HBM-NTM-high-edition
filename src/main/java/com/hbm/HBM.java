package com.hbm;

import com.hbm.config.GeneralConfig;
import com.hbm.datagen.loot.BlockLootGen;
import com.hbm.datagen.loot.LootTableGen;
import com.hbm.datagen.model.BlockStateGen;
import com.hbm.datagen.model.ItemModelGen;
import com.hbm.datagen.recipe.RecipeGen;
import com.hbm.datagen.tag.BlockTagsGen;
import com.hbm.datagen.tag.ItemTagsGen;
import com.hbm.recipe.ModRecipeType;
import com.hbm.registries.ModBlocks;
import com.hbm.fluid.ModFluidTypes;
import com.hbm.fluid.ModFluids;
import com.hbm.network.ModMessages;
import com.hbm.particle.ModParticleTypes;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.datagen.*;
import com.hbm.entity.ModEntityType;
import com.hbm.gui.ModMenuType;
import com.hbm.registries.ModCreativeModeTab;
import com.hbm.registries.ModItems;
import com.hbm.recipe.ModRecipes;
import com.hbm.registries.ModSounds;
import com.hbm.world.feature.ModFeatures;
import com.mojang.logging.LogUtils;
import com.test.RecipeTest;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(HBM.MODID)
public class HBM {
    public static final String MODID = "hbmxx";
    public static final Logger LOGGER = LogUtils.getLogger();
//    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
//    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
//            .withTabsBefore(CreativeModeTabs.COMBAT)
//            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
//            .displayItems((parameters, output) -> {
//            output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
//            }).build());

    public HBM() {
        //模组事件总线
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onGatherData);
        modEventBus.addListener(ModCreativeModeTab::addCreative);
        //forge事件总线
        MinecraftForge.EVENT_BUS.register(this);
        //模组内容的注册
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeModeTab.CREATIVE_MODE_TABS.register(modEventBus);
        ModBlockEntityType.REGISTER.register(modEventBus);
        ModRecipeType.RECIPE_TYPE.register(modEventBus);
        ModRecipes.SERIALIZER.register(modEventBus);
        ModFluidTypes.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModParticleTypes.PARTICLE_TYPES.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModEntityType.ENTITY_TYPES.register(modEventBus);
        ModMenuType.MOD_MENU_TYPES.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GeneralConfig.CONFIG_SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        ModMessages.register(); //注册所有的消息
    }

    public static ResourceLocation rl(String s){return ResourceLocation.tryBuild(HBM.MODID,s);}

    /**
     * 数据生成入口，只会在runData时候被调用
     * */
    private void onGatherData(GatherDataEvent event){
//        SerializableRecipe.initialize();
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        /** 客户端数据生成，生成到assets目录下 */
//        generator.addProvider(event.includeClient(),new EnglishLanguageProvider(packOutput,HBMxx.MODID,"en_us"));
        generator.addProvider(event.includeClient(),new ItemModelGen(packOutput, HBM.MODID,helper));
        generator.addProvider(event.includeClient(),new BlockStateGen(packOutput, HBM.MODID,helper));
        /** 服务端数据生成，生成到data目录下 */
        BlockTagsGen blockTagsGen = new BlockTagsGen(packOutput, lookupProvider, MODID, helper);
        generator.addProvider(event.includeServer(),new ForgeAdvancementProvider(packOutput,lookupProvider,helper, List.of(new AdvacementGen())));
        generator.addProvider(event.includeServer(), new RecipeGen(packOutput,helper,MODID));
        generator.addProvider(event.includeServer(), blockTagsGen);
        generator.addProvider(event.includeServer(), new ItemTagsGen(packOutput,lookupProvider,blockTagsGen.contentsGetter(),MODID,helper));
        generator.addProvider(event.includeServer(), new TagDmgTypeGen(packOutput,lookupProvider));
//        generator.addProvider(event.includeServer(), new RegistryDataGen(packOutput,lookupProvider));
        generator.addProvider(event.includeServer(), new WorldGen(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), (DataProvider.Factory<LootTableGen>) output->new LootTableGen(output, Collections.emptySet(),List.of(
                new LootTableProvider.SubProviderEntry(BlockLootGen::new, LootContextParamSets.BLOCK)
        )));

//        System.out.println("id: "+ ModItems.ignot_steel.getId());
//        System.out.println("id path: " + ModItems.ignot_steel.getId().getPath());
//        System.out.println("id namespace: " + ModItems.ignot_steel.getId().getNamespace());
//        System.out.println("key: "+ ModItems.ignot_steel.getKey());
//        System.out.println("key location: "+ ModItems.ignot_steel.getKey().location());
//        System.out.println("description id: "+ModItems.ignot_steel.get().getDescriptionId());
//        System.out.println("tab id: "+ModCreativeModeTab.HBM_ITEM.getId() + " | tab path: " + ModCreativeModeTab.HBM_ITEM.getId().getPath());
//        System.out.println("tab language key: "+ModCreativeModeTab.HBM_ITEM.getId().toLanguageKey());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        RecipeTest.countableIngredientLoad(event);
    }
}
