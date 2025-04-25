package com.hbm.main;

import com.hbm.HBM;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.entity.ModEntityType;
import com.hbm.fluid.ModFluids;
import com.hbm.gui.ModMenuType;
import com.hbm.gui.screen.AssemblerGui;
import com.hbm.gui.screen.BatteryGui;
import com.hbm.gui.screen.DifurnaceGui;
import com.hbm.gui.screen.PressGui;
import com.hbm.model.Models;
import com.hbm.model.entity.TestEntityModel;
import com.hbm.particle.ModParticleTypes;
import com.hbm.particle.type.HBMSmokeParticle;
import com.hbm.render.ModTextureLoader;
import com.hbm.render.blockentity.*;
import com.hbm.render.entity.TestEntityRenderer;
import com.hbm.render.entity.effect.BlackHoleRender;
import com.hbm.render.entity.EntityBlankRender;
import com.hbm.render.entity.effect.EntityTorexRender;
import com.hbm.render.item.SpecialItemRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = HBM.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    public static SpecialItemRender specialItemRender;
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        /** 注册menu和gui */
        event.enqueueWork(()-> {
            MenuScreens.register(ModMenuType.DIFURNACE_MENU.get(), DifurnaceGui::new);
            MenuScreens.register(ModMenuType.PRESS_MENU.get(), PressGui::new);
            MenuScreens.register(ModMenuType.BATTERY_MENU.get(), BatteryGui::new);
            MenuScreens.register(ModMenuType.ASSEMBLER_MENU.get(), AssemblerGui::new);
            //方块实体渲染
            BlockEntityRenderers.register(ModBlockEntityType.PRESS_ENTITY.get(), PressRenderer::new);
            BlockEntityRenderers.register(ModBlockEntityType.ASSEMBLER_ENTITY.get(), AssemblerRenderer::new);
            BlockEntityRenderers.register(ModBlockEntityType.CRUCIBLE_ENTITY.get(), CrucibleRenderer::new);
            BlockEntityRenderers.register(ModBlockEntityType.NUKE_BOMB_FAT_ENTITY.get(), NukeFatRender::new);
            BlockEntityRenderers.register(ModBlockEntityType.NUKE_BOMB_BOY_ENTITY.get(), NukeBoyRender::new);
            BlockEntityRenderers.register(ModBlockEntityType.NUKE_BOMB_CUSTOM_ENTITY.get(), NukeCustomRender::new);
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
            //设置液体的渲染（因为液体是半透明的，所以需要设置一下）
            ItemBlockRenderTypes.setRenderLayer(ModFluids.IRRADIATED_WATER_SOURCE_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.IRRADIATED_WATER_FLOW_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.IRRADIATED_POLLUTED_SOURCE_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.IRRADIATED_POLLUTED_FLOW_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SULFURIC_ACID_SOURCE_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SULFURIC_ACID_FLOW_BLOCK.get(), RenderType.translucent());
            //尝试加载贴图
            ResourceLocation overlay1 = new ResourceLocation(HBM.MODID,"fluid/irradiated_water_overlay");
//            Minecraft.getInstance().textureManager.register(overlay1,new SimpleTexture(overlay1));
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
        event.register(Models.BLACK_HOLE);
    }

    @SubscribeEvent
    public static void modifyBakingResult(ModelEvent.ModifyBakingResult event){
    }

    @SubscribeEvent
    public static void registerParticleProvidersEvent(RegisterParticleProvidersEvent event){
        //注册模组专属粒子效果
        event.registerSpriteSet(ModParticleTypes.HBM_SMOKE.get(), HBMSmokeParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event){
        BlockEntityRenderDispatcher blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
        EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
        specialItemRender = new SpecialItemRender(blockEntityRenderDispatcher,entityModels);
        //注册模组客户端专属的资源加载器
        event.registerReloadListener(specialItemRender);
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

//    @SubscribeEvent
//    public static void renderGui(RenderGuiEvent event){
//
//    }
}
