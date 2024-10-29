package com.hbm;

import com.hbm.api.HBMTags;
import com.hbm.block.ModBlocks;
import com.hbm.fluid.ModFluidTypes;
import com.hbm.fluid.ModFluids;
import com.hbm.model.Models;
import com.hbm.particle.ModParticleTypes;
import com.hbm.render.ModTextureLoader;
import com.hbm.render.blockentity.AssemblerRenderer;
import com.hbm.render.blockentity.CrucibleRenderer;
import com.hbm.render.blockentity.NukeFatRender;
import com.hbm.render.blockentity.PressRenderer;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.datagen.*;
import com.hbm.entity.ModEntityType;
import com.hbm.gui.menu.ModMenuType;
import com.hbm.gui.screen.DifurnaceGui;
import com.hbm.gui.screen.PressGui;
import com.hbm.item.ModCreativeModeTab;
import com.hbm.item.ModItems;
import com.hbm.model.entity.TestEntityModel;
import com.hbm.recipe.ModRecipes;
import com.hbm.render.entity.TestEntityRenderer;
import com.hbm.render.item.SpecialItemRender;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.texture.PreloadedTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(HBMxx.MODID)
public class HBMxx {
    public static final String MODID = "hbmxx";
    public static final Logger LOGGER = LogUtils.getLogger();
//    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
//    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
//            .withTabsBefore(CreativeModeTabs.COMBAT)
//            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
//            .displayItems((parameters, output) -> {
//            output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
//            }).build());

    public HBMxx() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onGatherData);
        modEventBus.addListener(ModCreativeModeTab::addCreative);

        MinecraftForge.EVENT_BUS.register(this);

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeModeTab.CREATIVE_MODE_TABS.register(modEventBus);
        ModBlockEntityType.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModEntityType.ENTITY_TYPES.register(modEventBus);
        ModMenuType.MOD_MENU_TYPES.register(modEventBus);
        ModRecipes.SERIALIZER.register(modEventBus);
        ModFluidTypes.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModParticleTypes.PARTICLE_TYPES.register(modEventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    public static ResourceLocation hbm(String s){return ResourceLocation.tryBuild(HBMxx.MODID,s);}

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
        generator.addProvider(event.includeClient(),new EnglishLanguageProvider(packOutput,HBMxx.MODID,"en_us"));
        generator.addProvider(event.includeClient(),new ItemModelGen(packOutput,HBMxx.MODID,helper));
        generator.addProvider(event.includeClient(),new BlockStateGen(packOutput,HBMxx.MODID,helper));
        /** 服务端数据生成，生成到data目录下 */
        BlockTagsGen blockTagsGen = new BlockTagsGen(packOutput, lookupProvider, MODID, helper);
        generator.addProvider(event.includeServer(),new ForgeAdvancementProvider(packOutput,lookupProvider,helper, List.of(new AdvacementGen())));
        generator.addProvider(event.includeServer(), new RecipeGen(packOutput));
        generator.addProvider(event.includeServer(), blockTagsGen);
        generator.addProvider(event.includeServer(), new HBMTags.HBMItemTags(packOutput,lookupProvider, blockTagsGen.contentsGetter(),MODID,helper));

//        System.out.println("id: "+ ModItems.ignot_steel.getId());
//        System.out.println("id path: " + ModItems.ignot_steel.getId().getPath());
//        System.out.println("id namespace: " + ModItems.ignot_steel.getId().getNamespace());
//        System.out.println("key: "+ ModItems.ignot_steel.getKey());
//        System.out.println("key location: "+ ModItems.ignot_steel.getKey().location());
//        System.out.println("description id: "+ModItems.ignot_steel.get().getDescriptionId());
//        System.out.println("tab id: "+ModCreativeModeTab.HBM_ITEM.getId() + " | tab path: " + ModCreativeModeTab.HBM_ITEM.getId().getPath());
//        System.out.println("tab language key: "+ModCreativeModeTab.HBM_ITEM.getId().toLanguageKey());
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
//        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
//            event.accept(EXAMPLE_BLOCK_ITEM);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            /** 注册menu和gui */
            event.enqueueWork(()-> {
                MenuScreens.register(ModMenuType.DIFURNACE_MENU.get(), DifurnaceGui::new);
                MenuScreens.register(ModMenuType.PRESS_MENU.get(), PressGui::new);
                //方块实体渲染
                BlockEntityRenderers.register(ModBlockEntityType.PRESS_ENTITY.get(), PressRenderer::new);
                BlockEntityRenderers.register(ModBlockEntityType.ASSEMBLER_ENTITY.get(), AssemblerRenderer::new);
                BlockEntityRenderers.register(ModBlockEntityType.CRUCIBLE_ENTITY.get(), CrucibleRenderer::new);
                BlockEntityRenderers.register(ModBlockEntityType.NUKE_BOMB_FAT_ENTITY.get(), NukeFatRender::new);
                //实体渲染
                EntityRenderers.register(ModEntityType.TEST_ENTITY.get(), TestEntityRenderer::new);
                //设置液体的渲染（因为液体是半透明的，所以需要设置一下）
                ItemBlockRenderTypes.setRenderLayer(ModFluids.IRRADIATED_WATER_SOURCE_BLOCK.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(ModFluids.IRRADIATED_WATER_FLOW_BLOCK.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(ModFluids.IRRADIATED_POLLUTED_SOURCE_BLOCK.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(ModFluids.IRRADIATED_POLLUTED_FLOW_BLOCK.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(ModFluids.SULFURIC_ACID_SOURCE_BLOCK.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(ModFluids.SULFURIC_ACID_FLOW_BLOCK.get(), RenderType.translucent());
                //尝试加载贴图
                ResourceLocation overlay1 = new ResourceLocation(HBMxx.MODID,"fluid/irradiated_water_overlay");
                Minecraft.getInstance().textureManager.register(overlay1,new SimpleTexture(overlay1));
            });
        }

        @SubscribeEvent
        public static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event)
        {
            /* 注册entity model */
            event.registerLayerDefinition(TestEntityModel.LAYER_LOCATION,TestEntityModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void registerAdditional(ModelEvent.RegisterAdditional event){
            //注册自定义加载模型
            event.register(Models.ASSEMBLER_BODY);
            event.register(Models.ASSEMBLER_COG);
            event.register(Models.ASSEMBLER_ARM);
            event.register(Models.ASSEMBLER_SLIDER);
            event.register(Models.CRUCIBLE);
            event.register(Models.FAT_MAN);
        }

        @SubscribeEvent
        public static void registerParticleProvidersEvent(RegisterParticleProvidersEvent event){
            //注册模组专属粒子效果
//            event.registerSpriteSet();
        }

        @SubscribeEvent
        public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event){
            //注册模组客户端专属的资源加载器
        }

        @SubscribeEvent
        public static void registerTextures(RegisterTextureAtlasSpriteLoadersEvent event){
            event.register("custom_texture_loader", new ModTextureLoader());

        }

        @SubscribeEvent
        public static void registerColorHandler(RegisterColorHandlersEvent.Block event){
//            event.register((state, level, pos, tintIndex) -> {
//                return 0x00000000;
//            }, ModBlocks.irradiated_water.get());
        }
    }
}
