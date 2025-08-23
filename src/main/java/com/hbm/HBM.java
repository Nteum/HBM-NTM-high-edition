package com.hbm;

import com.hbm.capabilities.network.TransmitterNetworkRegistry;
import com.hbm.config.ClientConfig;
import com.hbm.config.CommonConfig;
import com.hbm.config.ServerConfig;
import com.hbm.datagen.damageSource.DamageTypeJsonProvider;
import com.hbm.datagen.damageSource.DmgTagProvider;
import com.hbm.datagen.loot.BlockLootGen;
import com.hbm.datagen.loot.ChestLootGen;
import com.hbm.datagen.loot.FishLootGen;
import com.hbm.datagen.model.BlockStateGen;
import com.hbm.datagen.model.ItemModelGen;
import com.hbm.datagen.recipe.RecipeGen;
import com.hbm.datagen.tag.BlockTagsGen;
import com.hbm.datagen.tag.ItemTagsGen;
import com.hbm.registries.ModBlocks;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.network.ModMessages;
import com.hbm.particle.ModParticleTypes;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.datagen.*;
import com.hbm.entity.ModEntityType;
import com.hbm.gui.ModMenuType;
import com.hbm.registries.ModCreativeModeTab;
import com.hbm.registries.ModItems;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.registries.ModSounds;
import com.hbm.world.feature.ModFeatures;
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
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
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
        modEventBus.addListener(this::onServerSetup);
        modEventBus.addListener(this::onPostLoad);
        modEventBus.addListener(this::onGatherData);
        modEventBus.addListener(ModCreativeModeTab::addCreative);

        //模组内容的注册
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeModeTab.CREATIVE_MODE_TABS.register(modEventBus);
        ModBlockEntityType.REGISTER.register(modEventBus);
        ModRecipes.RECIPE_TYPE.register(modEventBus);
        ModRecipes.SERIALIZER.register(modEventBus);
        ModFluids.register(modEventBus);
        ModParticleTypes.PARTICLE_TYPES.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModEntityType.ENTITY_TYPES.register(modEventBus);
        ModMenuType.MOD_MENU_TYPES.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.CONFIG_SPEC, "hbm-common.toml");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        if (!CONFIG_PATH.toFile().exists()) CONFIG_PATH.toFile().mkdir();
        ModMessages.register(); //注册所有的消息
        TransmitterNetworkRegistry.initiate(); //注册传输网络系统
    }

    public void onServerSetup(FMLDedicatedServerSetupEvent event) {

    }

    public void onPostLoad(FMLLoadCompleteEvent event){
        ClientConfig.initConfig();
        ServerConfig.initConfig();
    }

    private void onServerStopped(ServerStoppedEvent event){
        TransmitterNetworkRegistry.reset();
    }

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
        generator.addProvider(event.includeClient(),new LanguageProvider(packOutput,HBM.MODID,"en_us"));
        generator.addProvider(event.includeClient(),new ItemModelGen(packOutput, HBM.MODID,helper));
        generator.addProvider(event.includeClient(),new BlockStateGen(packOutput, HBM.MODID,helper));
        /** 服务端数据生成，生成到data目录下 */
        BlockTagsGen blockTagsGen = new BlockTagsGen(packOutput, lookupProvider, MODID, helper);
        generator.addProvider(event.includeServer(),new ForgeAdvancementProvider(packOutput,lookupProvider,helper, List.of(new AdvacementGen())));
        generator.addProvider(event.includeServer(), new RecipeGen(packOutput,helper,MODID));
        generator.addProvider(event.includeServer(), blockTagsGen);
        generator.addProvider(event.includeServer(), new ItemTagsGen(packOutput,lookupProvider,blockTagsGen.contentsGetter(),MODID,helper));
        generator.addProvider(event.includeServer(), new DamageTypeJsonProvider(packOutput, MODID));
        generator.addProvider(event.includeServer(), new DmgTagProvider(packOutput, lookupProvider, MODID, helper));
//        generator.addProvider(event.includeServer(), new TagDmgTypeGen(packOutput,lookupProvider));
//        generator.addProvider(event.includeServer(), new RegistryDataGen(packOutput,lookupProvider));
        generator.addProvider(event.includeServer(), new WorldGen(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), (DataProvider.Factory<LootTableProvider>) output->new LootTableProvider(output, Collections.emptySet(),List.of(
                new LootTableProvider.SubProviderEntry(BlockLootGen::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(ChestLootGen::new, LootContextParamSets.CHEST),
                new LootTableProvider.SubProviderEntry(FishLootGen::new, LootContextParamSets.FISHING)
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

    public static boolean isLoad(String modID){
        return ModList.get().isLoaded(modID);
    }
    public static ResourceLocation rl(String s){return ResourceLocation.tryBuild(HBM.MODID,s);}
}
