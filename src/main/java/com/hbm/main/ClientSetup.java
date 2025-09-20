package com.hbm.main;

import com.hbm.HBM;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.entity.ModEntityType;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.gui.ModMenuType;
import com.hbm.gui.screen.*;
import com.hbm.gui.screen.RenderUtils;
import com.hbm.item.HBMWeapon;
import com.hbm.item.tool.FluidBucketItem;
import com.hbm.registries.ModItems;
import com.hbm.render.entity.missile.MissileTaintRenderer;
import com.hbm.render.model.Models;
import com.hbm.render.model.entity.TestEntityModel;
import com.hbm.particle.ModParticleTypes;
import com.hbm.render.blockentity.*;
import com.hbm.render.entity.TestEntityRenderer;
import com.hbm.render.entity.effect.BlackHoleRender;
import com.hbm.render.entity.EntityBlankRender;
import com.hbm.render.entity.effect.EntityTorexRender;
import com.hbm.render.item.SpecialItemRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

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
            MenuScreens.register(ModMenuType.CHEMPLANT_MENU.get(), ChemplantGui::new);
            MenuScreens.register(ModMenuType.BARREL_MENU.get(), BarrelGui::new);
            MenuScreens.register(ModMenuType.ELECTRIC_FURNACE_MENU.get(), ElectricFurnaceGui::new);
            //方块实体渲染
            BlockEntityRenderers.register(ModBlockEntityType.PRESS_ENTITY.get(), PressRenderer::new);
            BlockEntityRenderers.register(ModBlockEntityType.ASSEMBLER_ENTITY.get(), AssemblerRenderer::new);
            BlockEntityRenderers.register(ModBlockEntityType.CRUCIBLE_ENTITY.get(), CrucibleRenderer::new);
            BlockEntityRenderers.register(ModBlockEntityType.NUKE_BOMB_FAT_ENTITY.get(), NukeFatRender::new);
            BlockEntityRenderers.register(ModBlockEntityType.NUKE_BOMB_BOY_ENTITY.get(), NukeBoyRender::new);
            BlockEntityRenderers.register(ModBlockEntityType.NUKE_BOMB_CUSTOM_ENTITY.get(), NukeCustomRender::new);
            BlockEntityRenderers.register(ModBlockEntityType.CHEMPLANT_ENTITY.get(), ChemplantRenderer::new);
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

            RenderUtils.init();
            specialItemRender = new SpecialItemRender(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());
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
            return new SpecialItemRender();
        }
        return specialItemRender;
    }

    @SubscribeEvent
    public static void onClientSetupFinished(FMLLoadCompleteEvent event){
//        Models.onLoadComplete(event);
    }

    @SubscribeEvent
    public static void registerColorHandlerItem(RegisterColorHandlersEvent.Item event){
        // 流体桶的染色
        FluidBucketItem[] fluidBucketItems = ModFluids.fluidList.stream().map(holder -> holder.bucket().get()).filter(bucket -> bucket instanceof FluidBucketItem).toArray(FluidBucketItem[]::new);
        event.register(FluidBucketItem::getColor, fluidBucketItems);
    }
}
