package com.hbm;

import com.hbm.capabilities.network.TransmitterNetworkRegistry;
import com.hbm.config.ClientConfig;
import com.hbm.config.CommonConfig;
import com.hbm.config.ServerConfig;
import com.hbm.compat.bigexplosives.BigExplosivesMod;
import com.hbm.compat.ballistix.BallistixCompat;
import com.hbm.datagen.damageSource.HBMDamageTagProvider;
import com.hbm.datagen.levelgen.HBMWorldGenProvider;
import com.hbm.datagen.tag.FluidTagsGen;
import com.hbm.dev.AssetConsistencyChecker;
import com.hbm.dev.ModelValidator;
import com.hbm.datagen.loot.BlockLootGen;
import com.hbm.datagen.loot.ChestLootGen;
import com.hbm.datagen.loot.EntityLootGen;
import com.hbm.datagen.loot.FishLootGen;
import com.hbm.datagen.model.BlockStateGen;
import com.hbm.datagen.model.ItemModelGen;
import com.hbm.datagen.recipe.RecipeGen;
import com.hbm.datagen.tag.BlockTagsGen;
import com.hbm.datagen.tag.ItemTagsGen;
import com.hbm.dim.HBMChunkGenerators;
import com.hbm.effect.ModEffects;
import com.hbm.main.ClientEventHandler;
import com.hbm.main.ServerEventHandler;
import com.hbm.registries.*;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.network.ModMessages;
import com.hbm.particle.ModParticleTypes;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.datagen.*;
import com.hbm.entity.ModEntityType;
import com.hbm.gui.ModMenuType;
import com.hbm.Inventory.recipe.CrackingRecipes;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.reactor.rbmk.RBMKManager;
import com.hbm.reactor.pile.PileNeutronTicker;
import com.hbm.render.model.Models;
import com.hbm.world.feature.ModFeatures;
import com.hbm.world.structure.ModStructureProcessors;
import com.mojang.logging.LogUtils;
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
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 模组主类，主要处理mod初始化的东西，并提供一些关于mod整体的信息，不要什么都往里面加。
 * */
@Mod(HBM.MODID)
public class HBM {
    public static final String MODID = "hbm";
    public static final Logger LOGGER = LogUtils.getLogger();
    //debug模式
    public static       boolean debug       = false;
    public static final Path    CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve(MODID + "Configs");
    public static final Path    RECIPE_PATH = FMLPaths.CONFIGDIR.get().resolve(MODID + "Recipes");

    public HBM() {
        //forge事件总线
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStopped);
        //模组事件总线
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::onPostLoad);
        modEventBus.addListener(this::onGatherData);
        modEventBus.addListener(ModKeyMapping::register);
        ClientEventHandler.registerEvents(MinecraftForge.EVENT_BUS, modEventBus);
        ServerEventHandler.registerEvents(MinecraftForge.EVENT_BUS, modEventBus);

        //模组内容的注册
        ModEntityType.ENTITY_TYPES.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModBlockEntityType.REGISTER.register(modEventBus);
        ModRecipes.RECIPE_TYPE.register(modEventBus);
        ModRecipes.SERIALIZER.register(modEventBus);
        ModFluids.register(modEventBus);
        ModParticleTypes.PARTICLE_TYPES.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModStructureProcessors.STRUCTURE_PROCESSORS.register(modEventBus);
        ModMenuType.MOD_MENU_TYPES.register(modEventBus);
        ModEffects.register(modEventBus);
        BigExplosivesMod.register(modEventBus);
        BallistixCompat.register(modEventBus);
        HBMChunkGenerators.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.CONFIG_SPEC, "hbm-common.toml");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        if (!CONFIG_PATH.toFile().exists()) CONFIG_PATH.toFile().mkdir();
        ModMessages.register(); //注册所有的消息
        TransmitterNetworkRegistry.initiate(); //注册传输网络系统
        RBMKManager.init();
        PileNeutronTicker.init();
        CrackingRecipes.registerDefaults();
        event.enqueueWork(HBMBiomes::setUp);    // 生物群系的注册
    }

    public void onClientSetup(FMLClientSetupEvent event){
        Models.onClientSetup(event);
    }

    public void onPostLoad(FMLLoadCompleteEvent event){
//        ClientConfig.initConfig();
//        ServerConfig.initConfig();
//        event.enqueueWork(() -> {
//            AssetConsistencyChecker.runIfRequested();
//            ModelValidator.runIfRequested();
//        });
    }

    private void onServerStopped(ServerStoppedEvent event){
        TransmitterNetworkRegistry.reset();
    }

    /**
     * 数据生成入口，只会在runData时候被调用
     * */
    private void onGatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        /** 服务端数据生成，生成到data目录下 */
        generator.addProvider(event.includeServer(),new HBMJsonProvider(packOutput, MODID, helper, false));
        generator.addProvider(event.includeServer(), new HBMDamageTagProvider(packOutput, lookupProvider, MODID, helper));
        BlockTagsGen blockTagsGen = new BlockTagsGen(packOutput, lookupProvider, MODID, helper);
        generator.addProvider(event.includeServer(),new ForgeAdvancementProvider(packOutput,lookupProvider,helper, List.of(new AdvacementGen())));
        generator.addProvider(event.includeServer(), new RecipeGen(packOutput,helper,MODID));
        generator.addProvider(event.includeServer(), blockTagsGen);
        generator.addProvider(event.includeServer(), new ItemTagsGen(packOutput,lookupProvider,blockTagsGen.contentsGetter(),MODID,helper));
        generator.addProvider(event.includeServer(), new FluidTagsGen(packOutput,lookupProvider,MODID,helper));
//        generator.addProvider(event.includeServer(), new WorldGen(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), (DataProvider.Factory<LootTableProvider>) output->new LootTableProvider(output, Collections.emptySet(),List.of(
                new LootTableProvider.SubProviderEntry(BlockLootGen::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(ChestLootGen::new, LootContextParamSets.CHEST),
                new LootTableProvider.SubProviderEntry(FishLootGen::new, LootContextParamSets.FISHING),
                new LootTableProvider.SubProviderEntry(EntityLootGen::new, LootContextParamSets.ENTITY)
        )));
        generator.addProvider(event.includeServer(), new HBMWorldGenProvider(packOutput, lookupProvider));
        /** 客户端数据生成，生成到assets目录下 */
        boolean includeClient = event.includeClient();
        LOGGER.info("GatherData flags -> includeServer: {}, includeClient: {}", event.includeServer(), includeClient);
        if (!includeClient) {
            LOGGER.warn("runData invoked without --client flag; forcing client-side datagen to keep assets in sync.");
        }
        DataGenerator.PackGenerator resourcePack = generator.getVanillaPack(true);
        resourcePack.addProvider(output -> new HBMJsonProvider(output, MODID, helper, true));
        resourcePack.addProvider(output -> new LanguageProvider(output,HBM.MODID,"en_us"));
        resourcePack.addProvider(output -> new ItemModelGen(output, HBM.MODID, helper));
        resourcePack.addProvider(output -> new BlockStateGen(output, HBM.MODID,helper));
//        resourcePack.addProvider(output -> new HBMWorldGenProvider(output,lookupProvider));
        LOGGER.info("Datagen providers registered: {}", generator.getProvidersView().keySet());
    }

    public static boolean isLoad(String modID){
        return ModList.get().isLoaded(modID);
    }
    /**
     * 判断当前是否处于数据生成模式，这个环境变量在build.gradle中加入的，而rundata中则没有加入。
     */
    public static boolean isDataGen() {
        return System.getProperty("forge.enabledGameTestNamespaces") == null;
    }

    public static ResourceLocation rl(String s){return ResourceLocation.tryBuild(HBM.MODID,s);}
    public static ResourceLocation modelRl(String s){return rl("models/" + s);}
}
