package com.hbm.main;

import com.hbm.HBM;
import com.hbm.block.interfaces.ILookOverlay;
import com.hbm.block.interfaces.ITooltipProvider;
import com.hbm.blockentity.HBMTiles;
import com.hbm.blockentity.logistic.PipeEntityBEPipeBase;
import com.hbm.config.ConfigLBSM;
import com.hbm.space.dim.orbit.SpaceSpecialEffects;
import com.hbm.entity.ModEntityType;
import com.hbm.gui.HBMMenus;
import com.hbm.gui.screen.*;
import com.hbm.item.icf.ItemICFPellet;
import com.hbm.item.pwr.ItemPWRFuel;
import com.hbm.item.research.ItemBreedingRod;
import com.hbm.item.weapon.ItemMissile;
import com.hbm.item.zirnox.ItemZirnoxRod;
import com.hbm.particle.ModParticleTypes;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModKeyMapping;
import com.hbm.registries.ModTabs;
import com.hbm.render.blockentity.*;
import com.hbm.render.entity.EntityBlankRender;
import com.hbm.render.entity.TestEntityRenderer;
import com.hbm.render.entity.effect.BlackHoleRender;
import com.hbm.render.entity.effect.EntityTorexRender;
import com.hbm.render.entity.effect.RenderMeteor;
//import com.hbm.render.entity.missile.MissileABMRenderer;
import com.hbm.render.entity.missile.MissileTaintRenderer;
import com.hbm.render.entity.mob.GlyphidRender;
import com.hbm.render.item.SpecialItemRender;
import com.hbm.render.model.Models;
import com.hbm.core.client.model.CustomPartsModel;
import com.hbm.core.client.model.TrianglePartsModel;
import com.hbm.render.model.entity.TestEntityModel;
import com.hbm.render.overlay.AtomicFlashOverlay;
import com.hbm.render.overlay.DebugTagOverlay;
import com.hbm.render.pipeline.GeoRenderPipeline;
import com.hbm.render.util.DebugBoundsRenderer;
import com.hbm.settings.tooltip.TooltipRegistries;
import com.hbm.utils.WorldUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;

//@Mod.EventBusSubscriber(modid = HBM.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    public static SpecialItemRender specialItemRender;

    public static void registerEvents(IEventBus forgeBus, IEventBus modBus){
        // mod总线事件
        modBus.addListener(ClientEventHandler::onClientSetup);
        modBus.addListener(ClientEventHandler::onRegisterBlockColorHandlerEvent);
        modBus.addListener(ClientEventHandler::onRegisterItemColorHandlerEvent);
        modBus.addListener(ClientEventHandler::registerEntityLayers);
        modBus.addListener(ClientEventHandler::registerAdditional);
        modBus.addListener(ClientEventHandler::modifyBakingResult);
        modBus.addListener(ClientEventHandler::registerParticleProvidersEvent);
        modBus.addListener(ClientEventHandler::registerClientReloadListeners);
        modBus.addListener(ClientEventHandler::onClientSetupFinished);
        modBus.addListener(ClientEventHandler::registerDimensionsSpecialEffects);
        modBus.addListener(ClientEventHandler::registerGeometryLoaders);
        modBus.addListener(ModTabs::addCreative);
        modBus.addListener(ClientEventHandler::registerGuiOverlayEvent);
        // forge总线事件
        forgeBus.addListener(ClientEventHandler::onKeyPressed);
        forgeBus.addListener(ClientEventHandler::onMouseScroll);
        forgeBus.addListener(AtomicFlashOverlay::onClientTick);
        forgeBus.addListener(AtomicFlashOverlay::onGuiRender);
        forgeBus.addListener(DebugTagOverlay::onGuiRender);
        forgeBus.addListener(TooltipRegistries::onTooltip);
        forgeBus.addListener(ClientEventHandler::onRenderGUIOverlay);
        forgeBus.addListener(DebugBoundsRenderer::onRenderLevelStage);
    }
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        /** 注册menu和gui */
        event.enqueueWork(()-> {
            // menu和screen的对应关系
//            MenuScreens.register(HBMMenus.DIFURNACE_MENU.get(), DifurnaceGui::new);
            MenuScreens.register(HBMMenus.PRESS_MENU.get(), PressGui::new);
            MenuScreens.register(HBMMenus.BATTERY_MENU.get(), BatteryGui::new);
            MenuScreens.register(HBMMenus.ASSEMBLER_MENU.get(), AssemblerGui::new);
            MenuScreens.register(HBMMenus.CHEMPLANT_MENU.get(), ChemplantGui::new);
            MenuScreens.register(HBMMenus.BARREL_MENU.get(), BarrelGui::new);
            MenuScreens.register(HBMMenus.GAS_TURBINE_MENU.get(), GasTurbineScreen::new);
//             MenuScreens.register(HBMMenus.ELECTRIC_FURNACE_MENU.get(), ElectricFurnaceGui::new);
            MenuScreens.register(HBMMenus.LAUNCH_PAD_MENU.get(), LaunchPadGui::new);
            MenuScreens.register(HBMMenus.SHREDDER_MENU.get(), ShredderGui::new);
            MenuScreens.register(HBMMenus.TOKAMAK_MENU.get(), TokamakGui::new);
            MenuScreens.register(HBMMenus.PWR_MENU.get(), PWRScreen::new);
            MenuScreens.register(HBMMenus.ZIRNOX_MENU.get(), ZirnoxScreen::new);
            MenuScreens.register(HBMMenus.RBMK_BASE_MENU.get(), RBMKBaseScreen::new);
            MenuScreens.register(HBMMenus.RBMK_FUEL_CHANNEL_MENU.get(), RBMKFuelChannelScreen::new);
            MenuScreens.register(HBMMenus.RBMK_CONTROL_ROD_MENU.get(), RBMKControlRodScreen::new);
            MenuScreens.register(HBMMenus.RBMK_PERIPHERAL_MENU.get(), RBMKPeripheralScreen::new);
            MenuScreens.register(HBMMenus.RBMK_AUTOLOADER_MENU.get(), RBMKAutoloaderScreen::new);
            MenuScreens.register(HBMMenus.RBMK_KEYPAD_CONFIG_MENU.get(), RBMKKeypadConfigScreen::new);
            MenuScreens.register(HBMMenus.RBMK_GAUGE_CONFIG_MENU.get(), RBMKGaugeConfigScreen::new);
            MenuScreens.register(HBMMenus.RBMK_RADIO_CONTROLLER_MENU.get(), RBMKRadioControllerScreen::new);
            MenuScreens.register(HBMMenus.IRON_CRATE_MENU.get(), IronCrateScreen::new);
            MenuScreens.register(HBMMenus.STEEL_CRATE_MENU.get(), SteelCrateScreen::new);
            MenuScreens.register(HBMMenus.WOOD_BURNER_MENU.get(), WoodBurnerScreen::new);
            MenuScreens.register(HBMMenus.ICF_MENU.get(), ICFScreen::new);
            MenuScreens.register(HBMMenus.ICF_PRESS_MENU.get(), ICFPressScreen::new);
            MenuScreens.register(HBMMenus.RESEARCH_REACTOR_MENU.get(), ResearchReactorScreen::new);
            MenuScreens.register(HBMMenus.BREEDER_REACTOR_MENU.get(), BreederReactorScreen::new);
//            MenuScreens.register(HBMMenus.MENU_FIREBOX.get(), GuiFirebox::new);
            MenuScreens.register(HBMMenus.MENU_CRUCIBLE.get(), GuiCrucible::new);
            MenuScreens.register(HBMMenus.MENU_CONVEYOR_INSERTER.get(), GuiConveyorInserter::new);
            MenuScreens.register(HBMMenus.MENU_CONVEYOR_EXTRACTOR.get(), GuiConveyorExtractor::new);
            MenuScreens.register(HBMMenus.MENU_CONVEYOR_ROUTER.get(), GuiConveyorRouter::new);
            MenuScreens.register(HBMMenus.MENU_MINER_LARGE.get(), GuiMinerLarge::new);
            ModBlocks.guiSupport();
            //方块实体渲染
//            BlockEntityRenderers.register(HBMTiles.PRESS_ENTITY.get(), PressRenderer::new);
            BlockEntityRenderers.register(HBMTiles.ASSEMBLER_ENTITY.get(), AssemblerRenderer::new);
//            BlockEntityRenderers.register(HBMTiles.NUKE_BOMB_FAT_ENTITY.get(), NukeFatRender::new);
//            BlockEntityRenderers.register(HBMTiles.NUKE_BOMB_BOY_ENTITY.get(), NukeBoyRender::new);
//            BlockEntityRenderers.register(HBMTiles.NUKE_BOMB_CUSTOM_ENTITY.get(), NukeCustomRender::new);
            BlockEntityRenderers.register(HBMTiles.CHEMPLANT_ENTITY.get(), ChemplantRenderer::new);
            BlockEntityRenderers.register(HBMTiles.LAUNCHPAD_ENTITY.get(), LaunchPadRender::new);
            BlockEntityRenderers.register(HBMTiles.TOKAMAK_CONTROLLER.get(), TokamakRenderer::new);
            BlockEntityRenderers.register(HBMTiles.ZIRNOX_REACTOR_ENTITY.get(), ZirnoxRenderer::new);
            BlockEntityRenderers.register(HBMTiles.RESEARCH_REACTOR_ENTITY.get(), ResearchReactorRenderer::new);
            BlockEntityRenderers.register(HBMTiles.BREEDER_REACTOR_ENTITY.get(), ctx -> new BreederReactorRenderer());
            BlockEntityRenderers.register(HBMTiles.TILE_SPACE_STATION.get(), SpaceStationRender::new);
            BlockEntityRenderers.register(HBMTiles.TILE_CONNECTOR.get(), ConnectorRender::new);
//            BlockEntityRenderers.register(HBMTiles.TILE_FIREBOX.get(), RendererFirebox::new);
            BlockEntityRenderers.register(HBMTiles.CRUCIBLE_ENTITY.get(), CrucibleRenderer::new);
            BlockEntityRenderers.register(HBMTiles.TILE_FOUNDRY_MOLD.get(), RenderFoundryMold::new);
            BlockEntityRenderers.register(HBMTiles.TILE_CONVEYOR.get(), RendererConveyor::new);
            BlockEntityRenderers.register(HBMTiles.RBMK_PERIPHERAL_ENTITY.get(), RBMKPeripheralRenderer::new);
            BlockEntityRenderers.register(HBMTiles.RBMK_DISPLAY_ENTITY.get(), RBMKDisplayRenderer::new);
            BlockEntityRenderers.register(HBMTiles.RBMK_GRAPH_ENTITY.get(), RBMKGraphRenderer::new);
            BlockEntityRenderers.register(HBMTiles.RBMK_NUMITRON_ENTITY.get(), RBMKNumitronRenderer::new);
            BlockEntityRenderers.register(HBMTiles.RBMK_KEYPAD_ENTITY.get(), RBMKKeypadRenderer::new);
            BlockEntityRenderers.register(HBMTiles.RBMK_GAUGE_ENTITY.get(), RBMKGaugeRenderer::new);
            BlockEntityRenderers.register(HBMTiles.TILE_MINER_LARGE.get(), RendererMinerLarge::new);
            ModBlocks.rendererSupport();
            //实体渲染
            EntityRenderers.register(ModEntityType.TEST_ENTITY.get(), TestEntityRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_GRENADE_GENETIC.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_GRENADE_STRONG.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_GRENADE_FIRE.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_GRENADE_FRAG.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_GRENADE_BLACK_HOLE.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_GRENADE_LEGACY.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_GUN_BULLET.get(), EntityBlankRender::new);
            EntityRenderers.register(ModEntityType.ENTITY_BLACK_HOLE.get(), BlackHoleRender::new);
            EntityRenderers.register(ModEntityType.ENTITY_NUKE_EXPLOSION_MK5.get(), EntityBlankRender::new);
            EntityRenderers.register(ModEntityType.ENTITY_NUKE_TOREX.get(), EntityTorexRender::new);
            EntityRenderers.register(ModEntityType.ENTITY_MISSILE_TEST.get(), MissileTaintRenderer::new);
//            EntityRenderers.register(ModEntityType.ENTITY_MISSILE_ANTI_BALLISTIC.get(), MissileABMRenderer::new);
            EntityRenderers.register(ModEntityType.GLYPHID.get(), GlyphidRender::new);
            EntityRenderers.register(ModEntityType.ENTITY_METEOR.get(), RenderMeteor::new);

            RenderUtils.init();
            // 物品属性，用于贴图变化
            ItemProperties.register(ModItems.INGOT_U238M2.get(), HBM.rl("stage"),
                    (stack, level, entity, seed) -> stack.hasTag() && stack.getTag().contains("stage", Tag.TAG_INT)
                            ? (float) stack.getTag().getInt("stage") : 0);
            ItemProperties.register(ModItems.INGOT_NEPTUNIUM.get(), HBM.rl("stage"),
                    (stack, level, entity, seed) -> ConfigLBSM.enableLBSM && ConfigLBSM.enableLBSMFullSchrab ? 1 : 0);
            ItemProperties.register(ModItems.pwr_fuel.get(), HBM.rl("pwr_type"),
                    (stack, level, entity, seed) -> ItemPWRFuel.getFuelTypeIndex(stack));
            ItemProperties.register(ModItems.rod_zirnox.get(), HBM.rl("zirnox_type"),
                    (stack, level, entity, seed) -> ItemZirnoxRod.getRodTypeIndex(stack));
            ItemProperties.register(ModItems.rod_breeder_single.get(), HBM.rl("breeder_type"),
                    (stack, level, entity, seed) -> ItemBreedingRod.getType(stack).ordinal());
            ItemProperties.register(ModItems.rod_breeder_dual.get(), HBM.rl("breeder_type"),
                    (stack, level, entity, seed) -> ItemBreedingRod.getType(stack).ordinal());
            ItemProperties.register(ModItems.rod_breeder_quad.get(), HBM.rl("breeder_type"),
                    (stack, level, entity, seed) -> ItemBreedingRod.getType(stack).ordinal());
            ModItems.itemPropertiesSupport();
        });
    }

    @SubscribeEvent
    public static void onRegisterBlockColorHandlerEvent(RegisterColorHandlersEvent.Block event){
        // 方块的颜色
        event.register((state, level, pos, tintIndex) -> switch (tintIndex){
            case 0 -> 0xff0000;
            case 1 -> 0xff8000;
            case 2 -> 0xffff00;
            case 3 -> 0x00ff00;
            case 4 -> 0x0080ff;
            case 5 -> 0x8000ff;
            default -> -1;
        }, ModBlocks.CONVEYOR_ROUTER.get());
//        event.register((state, level, pos, tintIndex) -> {
//            if (tintIndex == 1 && level != null && pos != null) {
//                PipeEntityBEPipeBase be = WorldUtils.getTileEntity(PipeEntityBEPipeBase.class, level, pos);
//                if (be != null) {
//                    return be.getFluidColor();
//                }
//            }
//            return -1;
//        }, ModBlocks.FLUID_PIPE.get());
        ModBlocks.blockColorSupport(event);
    }
    @SubscribeEvent
    public static void onRegisterItemColorHandlerEvent(RegisterColorHandlersEvent.Item event){
        /** 给物品添加颜色 */
        // 流体桶的染色
//        FluidBucketItem[] fluidBucketItems = ModFluids.fluidList.stream().map(holder -> holder.bucket().get()).filter(bucket -> bucket instanceof FluidBucketItem).toArray(FluidBucketItem[]::new);
//        event.register(FluidBucketItem::getColor, fluidBucketItems);
        event.register((stack, tintIndex) -> tintIndex == 0 ? ItemICFPellet.getFuelColor(stack) : 0xFFFFFF, ModItems.icf_pellet.get());
        ModItems.itemColorSupport(event);
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
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.isPaused() || minecraft.player == null || minecraft.screen != null) {
            return;
        }
        long window = minecraft.getWindow().getWindow();
        boolean commandDown = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SUPER)
                || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SUPER);
        if ((!Screen.hasControlDown() && !commandDown) || event.getScrollDelta() == 0.0D) {
            return;
        }

        ItemStack missileStack = minecraft.player.getMainHandItem();
        if (!(missileStack.getItem() instanceof ItemMissile)) {
            missileStack = minecraft.player.getOffhandItem();
            if (!(missileStack.getItem() instanceof ItemMissile)) {
                return;
            }
        }

        int dir = event.getScrollDelta() > 0.0D ? 1 : -1;
        ItemMissile.LaunchMode mode = ItemMissile.cycleLaunchMode(missileStack, dir);
        minecraft.player.displayClientMessage(Component.literal("导弹模式: " + mode.display()), true);
        event.setCanceled(true);
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
    }

    @SubscribeEvent
    public static void modifyBakingResult(ModelEvent.ModifyBakingResult event){
        // 修改模型烘焙结果
        Models.modifyBakingResult(event);
    }

    @SubscribeEvent
    public static void registerParticleProvidersEvent(RegisterParticleProvidersEvent event){
        //注册模组专属粒子效果
        ModParticleTypes.register(event);
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event){
        event.registerReloadListener(GeoRenderPipeline.INSTANCE);
//        event.registerReloadListener(ItemModelReloader.INSTANCE);
    }
    public static BlockEntityWithoutLevelRenderer getLazyItemRender(){
        if (specialItemRender == null){
            specialItemRender = new SpecialItemRender();
        }
        return specialItemRender;
    }
    // 注册各维度天空渲染
    @SubscribeEvent
    public static void registerDimensionsSpecialEffects(RegisterDimensionSpecialEffectsEvent event){
        event.register(HBM.rl("space_effects"), new SpaceSpecialEffects());
    }
    // 注册单独加入的模型加载器
    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event){
        CustomPartsModel.Loader.register(event);
        TrianglePartsModel.Loader.register(event);
//        event.register(CustomPartsModel.Loader.LOADER_NAME, CustomPartsModel.Loader.INSTANCE);
        event.register("door", CustomPartsModel.Loader.INSTANCE);
        event.register("advanced_assembly_machine_loader", CustomPartsModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public static void onRenderGUIOverlay(RenderGuiOverlayEvent.Post event){
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        LocalPlayer player = mc.player;
        if (level == null || player == null || mc.screen != null) return;

        if (event.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type()){
            HitResult hitResult = mc.hitResult;
            if (hitResult != null) {
                if (hitResult.getType() == HitResult.Type.BLOCK){
                    ItemStack itemInHand;
                    BlockState aimBlockState;
                    // 3. 计算屏幕中心位置
                    int screenWidth = mc.getWindow().getGuiScaledWidth();
                    int screenHeight = mc.getWindow().getGuiScaledHeight();
                    // 准星正中心是 (screenWidth / 2, screenHeight / 2)
                    // 我们向右下方偏移 12 像素

                    BlockPos blockPos = ((net.minecraft.world.phys.BlockHitResult) hitResult).getBlockPos();
                    if (!(itemInHand = player.getMainHandItem()).isEmpty() && itemInHand.getItem() instanceof ILookOverlay lookOverlay){
                        lookOverlay.printHook(level, blockPos);
                    }else if ((aimBlockState = level.getBlockState(blockPos)).getBlock() instanceof ILookOverlay lookOverlay){
                        GuiGraphics graphics = event.getGuiGraphics();
                        List<Component> desc = lookOverlay.getDesc(level, blockPos);
                        if (desc == null || desc.isEmpty()) {
                            return;
                        }
                        int fontHeight = mc.font.lineHeight;
                        int lineSpace = 2;  // 暂时把间距设为固定值
                        int renderX = screenWidth / 2 + 12;
                        int renderY = screenHeight / 2 - desc.size() / 2 * (lineSpace + fontHeight);
                        for (Component component : desc) {
                            if (component == null) {
                                continue;
                            }
                            graphics.drawString(mc.font, component.getVisualOrderText(), renderX, renderY, 0xFFFFFF);
                            renderY += fontHeight + lineSpace;
                        }
                    }
                } else if (hitResult.getType() == HitResult.Type.ENTITY){

                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemTooltipEvent(ItemTooltipEvent event){
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        if (item.getDescriptionId().contains(HBM.MODID) && item instanceof ITooltipProvider tooltipProvider) {
            tooltipProvider.addInformation(event.getItemStack(), event.getEntity(), event.getToolTip(), event.getFlags());
        }
    }

    @SubscribeEvent
    public static void registerGuiOverlayEvent(RegisterGuiOverlaysEvent event){
        ModItems.hudSupport(event);
    }
}
