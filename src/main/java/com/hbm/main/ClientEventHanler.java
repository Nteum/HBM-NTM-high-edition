package com.hbm.main;

import com.hbm.HBM;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.config.ConfigLBSM;
import com.hbm.entity.ModEntityType;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.gui.ModMenuType;
import com.hbm.gui.screen.*;
import com.hbm.gui.screen.RenderUtils;
import com.hbm.registries.ModItems;
import com.hbm.item.tool.FluidBucketItem;
import com.hbm.registries.ModKeyMapping;
import com.hbm.registries.ModItems;;
import com.hbm.render.entity.missile.MissileTaintRenderer;
import com.hbm.render.entity.mob.GlyphidRender;
import com.hbm.render.item.ItemModelReloader;
import com.hbm.render.model.Models;
import com.hbm.render.model.item.SimpleOverridesWrapper;
import com.hbm.render.model.item.SimpleOverridesWrapper.*;
import com.hbm.render.overlay.AtomicFlashOverlay;
import com.hbm.render.pipeline.GeoRenderPipeline;
import com.hbm.render.model.entity.TestEntityModel;
import com.hbm.particle.ModParticleTypes;
import com.hbm.render.blockentity.*;
import com.hbm.render.blockentity.TokamakRenderer;
import com.hbm.render.entity.TestEntityRenderer;
import com.hbm.render.entity.effect.BlackHoleRender;
import com.hbm.render.entity.EntityBlankRender;
import com.hbm.render.entity.effect.EntityTorexRender;
import com.hbm.render.item.SpecialItemRender;
import com.hbm.settings.tooltip.TooltipRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Mod.EventBusSubscriber(modid = HBM.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ClientEventHanler {
    public static SpecialItemRender specialItemRender;

    public static void registerEvents(IEventBus forgeBus, IEventBus modBus){
        // mod总线事件
        modBus.addListener(ClientEventHanler::onClientSetup);
        modBus.addListener(ClientEventHanler::registerEntityLayers);
        modBus.addListener(ClientEventHanler::registerAdditional);
        modBus.addListener(ClientEventHanler::modifyBakingResult);
        modBus.addListener(ClientEventHanler::registerParticleProvidersEvent);
        modBus.addListener(ClientEventHanler::registerClientReloadListeners);
        modBus.addListener(ClientEventHanler::onClientSetupFinished);
        modBus.addListener(ClientEventHanler::registerColorHandlerItem);
        // forge总线事件
        forgeBus.addListener(ClientEventHanler::onKeyPressed);
        forgeBus.addListener(AtomicFlashOverlay::onClientTick);
        forgeBus.addListener(AtomicFlashOverlay::onGuiRender);
        forgeBus.addListener(TooltipRegistries::onTooltip);
    }
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        // 物品贴图逻辑
        ItemProperties.register(ModItems.INGOT_U238M2.get(), HBM.rl("stage"),
                (stack, level, entity, seed) -> stack.hasTag() && stack.getTag().contains("stage", Tag.TAG_INT) ? (float) stack.getTag().getInt("stage") : 0);
        /** 注册menu和gui */
        event.enqueueWork(()-> {
            // menu和screen的对应关系
            MenuScreens.register(ModMenuType.DIFURNACE_MENU.get(), DifurnaceGui::new);
            MenuScreens.register(ModMenuType.PRESS_MENU.get(), PressGui::new);
            MenuScreens.register(ModMenuType.BATTERY_MENU.get(), BatteryGui::new);
            MenuScreens.register(ModMenuType.ASSEMBLER_MENU.get(), AssemblerGui::new);
            MenuScreens.register(ModMenuType.CHEMPLANT_MENU.get(), ChemplantGui::new);
            MenuScreens.register(ModMenuType.BARREL_MENU.get(), BarrelGui::new);
            MenuScreens.register(ModMenuType.ELECTRIC_FURNACE_MENU.get(), ElectricFurnaceGui::new);
            MenuScreens.register(ModMenuType.LAUNCH_PAD_MENU.get(), LaunchPadGui::new);
            MenuScreens.register(ModMenuType.SHREDDER_MENU.get(), ShredderGui::new);
            MenuScreens.register(ModMenuType.TOKAMAK_MENU.get(), TokamakGui::new);
            MenuScreens.register(ModMenuType.RBMK_BASE_MENU.get(), RBMKBaseScreen::new);
            MenuScreens.register(ModMenuType.RBMK_FUEL_CHANNEL_MENU.get(), RBMKFuelChannelScreen::new);
            MenuScreens.register(ModMenuType.RBMK_CONTROL_ROD_MENU.get(), RBMKControlRodScreen::new);
            MenuScreens.register(ModMenuType.RBMK_PERIPHERAL_MENU.get(), RBMKPeripheralScreen::new);
            MenuScreens.register(ModMenuType.IRON_CRATE_MENU.get(), IronCrateScreen::new);
            MenuScreens.register(ModMenuType.STEEL_CRATE_MENU.get(), SteelCrateScreen::new);
            MenuScreens.register(ModMenuType.WOOD_BURNER_MENU.get(), WoodBurnerScreen::new);
            //方块实体渲染
            BlockEntityRenderers.register(ModBlockEntityType.PRESS_ENTITY.get(), PressRenderer::new);
            BlockEntityRenderers.register(ModBlockEntityType.ASSEMBLER_ENTITY.get(), AssemblerRenderer::new);
            BlockEntityRenderers.register(ModBlockEntityType.CRUCIBLE_ENTITY.get(), CrucibleRenderer::new);
            BlockEntityRenderers.register(ModBlockEntityType.NUKE_BOMB_FAT_ENTITY.get(), NukeFatRender::new);
            BlockEntityRenderers.register(ModBlockEntityType.NUKE_BOMB_BOY_ENTITY.get(), NukeBoyRender::new);
            BlockEntityRenderers.register(ModBlockEntityType.NUKE_BOMB_CUSTOM_ENTITY.get(), NukeCustomRender::new);
            BlockEntityRenderers.register(ModBlockEntityType.CHEMPLANT_ENTITY.get(), ChemplantRenderer::new);
            BlockEntityRenderers.register(ModBlockEntityType.LAUNCHPAD_ENTITY.get(), LaunchPadRender::new);
            BlockEntityRenderers.register(ModBlockEntityType.TOKAMAK_CONTROLLER.get(), TokamakRenderer::new);
            //实体渲染
            EntityRenderers.register(ModEntityType.TEST_ENTITY.get(), TestEntityRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_GRENADE_GENETIC.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_GRENADE_STRONG.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_GRENADE_FIRE.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_GRENADE_FRAG.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_GRENADE_BLACK_HOLE.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_BLACK_HOLE.get(), BlackHoleRender::new);
            EntityRenderers.register(ModEntityType.ENTITY_NUKE_EXPLOSION_MK5.get(), EntityBlankRender::new);
            EntityRenderers.register(ModEntityType.ENTITY_NUKE_TOREX.get(), EntityTorexRender::new);
            EntityRenderers.register(ModEntityType.ENTITY_MISSILE_TEST.get(), MissileTaintRenderer::new);
            EntityRenderers.register(ModEntityType.GLYPHID.get(), GlyphidRender::new);

            RenderUtils.init();
        });
        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.INGOT_NEPTUNIUM.get(), HBM.rl("stage"),
                    (stack, level, entity, seed) -> ConfigLBSM.enableLBSM && ConfigLBSM.enableLBSMFullSchrab ? 1 : 0);
        });
    }

    @SubscribeEvent
    public static void onClientSetupFinished(FMLLoadCompleteEvent event){
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event){
        if (event.phase == TickEvent.Phase.END){
        }
    }

    /**
     * 关于按键事件：
     * - ScreenEvent.KeyPressed是在GUI里触发的，包括玩家物品栏界面/交流窗/物品gui，但不在没有gui的情况下触发
     * - InputEvent.Key是在没有GUI时出发的，有GUI时它会被覆盖，如果监听操控性按键，应当使用这个
     * */
    @SubscribeEvent
    public static void onKeyPressed(InputEvent.Key event){
        ModKeyMapping.preCheck(event);
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
        Models.registerModels(event);
//        // 显式告知游戏加载这些模型资源
//        event.register(new ResourceLocation("hbm", "item/hs-elements"));
//        event.register(new ResourceLocation("hbm", "item/hs-arsenic"));
//        event.register(new ResourceLocation("hbm", "item/hs-vault"));
    }

    @SubscribeEvent
    public static void modifyBakingResult(ModelEvent.ModifyBakingResult event){
        // 修改模型烘焙结果
        Models.modifyBakingResult(event);
//        // 添加overrides
//        // 1. 获取你的物品注册名对应的模型资源位置
//        ModelResourceLocation mainModelLoc = new ModelResourceLocation(new ResourceLocation("hbm", "ingot_u238m2"), "inventory");
//
//        // 2. 获取主模型实例
//        BakedModel mainModel = event.getModels().get(mainModelLoc);
//
//        if (mainModel != null) {
//            // 3. 定义你的谓词逻辑（对应 JSON 中的 predicate）
//            // 注意：ResourceLocation 必须和你代码中注册 ItemProperties 的一致
//            ResourceLocation stageProperty = new ResourceLocation("hbm", "stage");
//
//            // 4. 获取子模型（这些模型必须在资源文件夹里有对应的 JSON 文件）
//            Map<ItemOverride, BakedModel> itemOverrides = new HashMap<>();
//            ModelResourceLocation modelLoc1 = new ModelResourceLocation(new ResourceLocation("hbm", "hs-elements"), "inventory");
//            ModelResourceLocation modelLoc2 = new ModelResourceLocation(new ResourceLocation("hbm", "hs-arsenic"), "inventory");
//            ModelResourceLocation modelLoc3 = new ModelResourceLocation(new ResourceLocation("hbm", "hs-vault"), "inventory");
//            // 即使 JSON 里没写 overrides，只要这几个文件存在，加载器就会预加载它们
//            BakedModel stage1Model = event.getModels().get(modelLoc1);
//            BakedModel stage2Model = event.getModels().get(modelLoc2);
//            BakedModel stage3Model = event.getModels().get(modelLoc3);
//            if (stage1Model != null) {
//                itemOverrides.put(new ItemOverride(modelLoc1, List.of(new ItemOverride.Predicate(stageProperty, 1.0F))), stage1Model);
//            }
//            if (stage2Model != null) {
//                itemOverrides.put(new ItemOverride(modelLoc2, List.of(new ItemOverride.Predicate(stageProperty, 2.0F))), stage2Model);
//            }
//            if (stage3Model != null) {
//                itemOverrides.put(new ItemOverride(modelLoc3, List.of(new ItemOverride.Predicate(stageProperty, 3.0F))), stage3Model);
//            }
//            // 创建ItemOverrides
//            BakedModel customModel = new SimpleOverridesWrapper((SimpleBakedModel) mainModel, new SimpleOverridesWrapper.BakedItemOverrides(itemOverrides));
//            // 7. 将修改后的模型放回注册表
//            event.getModels().put(mainModelLoc, customModel);
//
//            System.out.println("HBM Debug: 成功手动注入了 " + itemOverrides.size() + " 个 Overrides！");
//        }
    }

    @SubscribeEvent
    public static void registerParticleProvidersEvent(RegisterParticleProvidersEvent event){
        //注册模组专属粒子效果
        ModParticleTypes.register(event);
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event){
        event.registerReloadListener(GeoRenderPipeline.INSTANCE);
        event.registerReloadListener(ItemModelReloader.INSTANCE);
    }
    public static BlockEntityWithoutLevelRenderer getLazyItemRender(){
        if (specialItemRender == null){
            specialItemRender = new SpecialItemRender();
        }
        return specialItemRender;
    }

    @SubscribeEvent
    public static void registerColorHandlerItem(RegisterColorHandlersEvent.Item event){
        /** 给物品添加颜色 */
        // 流体桶的染色
        FluidBucketItem[] fluidBucketItems = ModFluids.fluidList.stream().map(holder -> holder.bucket().get()).filter(bucket -> bucket instanceof FluidBucketItem).toArray(FluidBucketItem[]::new);
        event.register(FluidBucketItem::getColor, fluidBucketItems);
        event.register((itemstack,color)->0xEC9A63, ModItems.BEDROCK_ORE.get());
    }
}
