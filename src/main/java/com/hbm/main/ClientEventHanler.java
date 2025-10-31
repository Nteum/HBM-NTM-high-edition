package com.hbm.main;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.entity.ModEntityType;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.gui.ModMenuType;
import com.hbm.gui.screen.*;
import com.hbm.gui.screen.RenderUtils;
import com.hbm.item.tool.FluidBucketItem;
import com.hbm.registries.ModKeyMapping;
import com.hbm.registries.ModItems;
import com.hbm.render.entity.missile.MissileTaintRenderer;
import com.hbm.render.entity.mob.GlyphidRender;
import com.hbm.render.model.Models;
import com.hbm.render.model.entity.TestEntityModel;
import com.hbm.particle.ModParticleTypes;
import com.hbm.render.blockentity.*;
import com.hbm.render.entity.TestEntityRenderer;
import com.hbm.render.entity.EntityBlankRender;
import com.hbm.render.entity.effect.BlackHoleRender;
import com.hbm.render.entity.effect.CoreExplosionRenderer;
import com.hbm.render.entity.effect.EntityTorexRender;
import com.hbm.render.item.SpecialItemRender;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

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
    }
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
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
            //方块实体渲染
            BlockEntityRenderers.register(ModBlockEntityType.PRESS_ENTITY.get(), PressRenderer::new);
            BlockEntityRenderers.register(ModBlockEntityType.ASSEMBLER_ENTITY.get(), AssemblerRenderer::new);
            BlockEntityRenderers.register(ModBlockEntityType.CRUCIBLE_ENTITY.get(), CrucibleRenderer::new);
            BlockEntityRenderers.register(ModBlockEntityType.NUKE_BOMB_FAT_ENTITY.get(), NukeFatRender::new);
            BlockEntityRenderers.register(ModBlockEntityType.NUKE_BOMB_BOY_ENTITY.get(), NukeBoyRender::new);
            BlockEntityRenderers.register(ModBlockEntityType.NUKE_BOMB_CUSTOM_ENTITY.get(), NukeCustomRender::new);
            BlockEntityRenderers.register(ModBlockEntityType.CHEMPLANT_ENTITY.get(), ChemplantRenderer::new);
            BlockEntityRenderers.register(ModBlockEntityType.LAUNCHPAD_ENTITY.get(), LaunchPadRender::new);
            //实体渲染
            EntityRenderers.register(ModEntityType.TEST_ENTITY.get(), TestEntityRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_GRENADE_GENETIC.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_GRENADE_STRONG.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_GRENADE_FIRE.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_GRENADE_FRAG.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_GRENADE_BLACK_HOLE.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_BLACK_HOLE.get(), BlackHoleRender::new);
            EntityRenderers.register(ModEntityType.ENTITY_CORE_EXPLOSION.get(), CoreExplosionRenderer::new);
            EntityRenderers.register(ModEntityType.ENTITY_NUKE_EXPLOSION_MK5.get(), EntityBlankRender::new);
            EntityRenderers.register(ModEntityType.ENTITY_NUKE_TOREX.get(), EntityTorexRender::new);
            EntityRenderers.register(ModEntityType.ENTITY_MISSILE_TEST.get(), MissileTaintRenderer::new);
            EntityRenderers.register(ModEntityType.GLYPHID.get(), GlyphidRender::new);

            RenderUtils.init();
//            specialItemRender = new SpecialItemRender(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());
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
//        BlockEntityRenderDispatcher blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
//        EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
//        specialItemRender = new SpecialItemRender(blockEntityRenderDispatcher,entityModels);
//        //注册模组客户端专属的资源加载器
//        event.registerReloadListener(specialItemRender);
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
