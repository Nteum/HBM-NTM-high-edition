package com.hbm.render.model;

import com.hbm.HBM;
import com.hbm.datagen.model.BlockStateGen;
import com.hbm.item.HBMWeapon;
import com.hbm.registries.ModItems;
import com.hbm.render.model.armor.*;
import com.hbm.render.model.entity.ModelGlyphid;
import com.hbm.render.model.entity.ObjEntityModelSingle;
import com.hbm.render.model.item.SimpleBakedModelWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Mod.EventBusSubscriber(modid = HBM.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Models {
    private static final Set<ResourceLocation> models = new HashSet<>();
    // 为避免静态强引用导致跨世界/资源重载的内存泄漏，改为并发Map + 弱引用/轻量键值
    // 物品：只缓存 资源模型RL -> 物品ID(ResourceLocation) 的映射，避免强持有 RegistryObject
    private static final ConcurrentMap<ResourceLocation, ResourceLocation> ITEM_MODEL_KEYS = new ConcurrentHashMap<>();
    // 实体：缓存 RL -> 实体模型 的弱引用，便于GC在资源重载/内存紧张时回收
//    private static final ConcurrentMap<ResourceLocation, SoftReference<Model>> ENTITY_MODELS = new ConcurrentHashMap<>();
    private static final ConcurrentMap<ResourceLocation, Model> ENTITY_MODELS = new ConcurrentHashMap<>();

    public static final ResourceLocation PRESS_BODY = add(HBM.rl("block/press/press_body"));
    public static final ResourceLocation PRESS_HEAD = add(HBM.rl("block/press/press_head"));
    public static final ResourceLocation ASSEMBLER_BODY = add(HBM.rl("block/assembler/assembler_body"));
    public static final ResourceLocation ASSEMBLER_COG = add(HBM.rl("block/assembler/assembler_cog"));
    public static final ResourceLocation ASSEMBLER_SLIDER = add(HBM.rl("block/assembler/assembler_slider"));
    public static final ResourceLocation ASSEMBLER_ARM = add(HBM.rl("block/assembler/assembler_arm"));
    public static final ResourceLocation CRUCIBLE = add(HBM.rl("block/crucible/crucible"));
    public static final ResourceLocation FAT_MAN = add(HBM.rl("block/bomb/fat_man"));
    public static final ResourceLocation BOY = add(HBM.rl("block/bomb/boy"));
    public static final ResourceLocation CUSTOM_NUKE = add(HBM.rl("block/bomb/custom"));
    public static final ResourceLocation BLACK_HOLE = add(HBM.rl("block/effect/sphere"));
    public static final ResourceLocation CHEMPLANT_BODY = add(HBM.rl("block/chemplant/chemplant_new_body"));
    public static final ResourceLocation CHEMPLANT_PISTON = add(HBM.rl("block/chemplant/chemplant_new_piston"));
    public static final ResourceLocation CHEMPLANT_SPINNER = add(HBM.rl("block/chemplant/chemplant_new_spinner"));
    public static final ResourceLocation LAUNCH_PAD = add(HBM.rl("block/launch_pad"));

    public static final ResourceLocation MP_W_15_BALEFIRE = addItem(HBM.rl("item/mp_warhead_15_balefire"), HBMWeapon.MP_WARHEAD_15_BALEFIRE.getId());
    public static final ResourceLocation GUN_RIFLE = addItem(HBM.rl("item/gun_marseleg"), ModItems.GUN_RIFLE.getId());

    public static final ResourceLocation MISSILE_TEST = addEntity(HBM.modelRl("entity/missile/missile_test"), new ObjEntityModelSingle());
    public static final ResourceLocation MISSILE_NUKE = addEntity(HBM.rl("entity/missile_nuclear"), new ObjEntityModelSingle());
    public static final ResourceLocation T51 = addEntity(HBM.modelRl("item/armor_t51"), new ModelArmorT51());
    public static final ResourceLocation BISMUTH = addEntity(HBM.modelRl("item/armor_bismuth"), new ModelArmorBismuth());
    public static final ResourceLocation DESH = addEntity(HBM.modelRl("item/armor_desh"), new ModelArmorDesh());
    public static final ResourceLocation DIESEL = addEntity(HBM.modelRl("item/armor_diesel"), new ModelArmorDiesel());
    public static final ResourceLocation RPA = addEntity(HBM.modelRl("item/armor_rpa"), new ModelArmorRPA());
    public static final ResourceLocation AJR = addEntity(HBM.modelRl("item/armor_ajr"), new ModelArmorAJR());
    public static final ResourceLocation BJ = addEntity(HBM.modelRl("item/armor_bj"), new ModelArmorBJ());
    public static final ResourceLocation GLYPHID = addEntity(HBM.modelRl("entity/glyphid"), new ModelGlyphid<>());

    public static ResourceLocation add(ResourceLocation rl){
        models.add(rl);
        return rl;
    }
    public static ResourceLocation addItem(ResourceLocation rl, ResourceLocation resourceLocation){
        models.add(rl);
        // 只记录物品的ID，避免强引用整个 RegistryObject 链
        ITEM_MODEL_KEYS.put(rl, resourceLocation);
        return rl;
    }
    public static ResourceLocation addEntity(ResourceLocation rl, Model model){
//        ENTITY_MODELS.put(rl, new SoftReference<>(model));
        ENTITY_MODELS.put(rl, model);
        return rl;
    }
    public static void registerModels(ModelEvent.RegisterAdditional event){
        models.forEach(event::register);
    }
    public static void onClientSetup(FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            // 加载实体模型
            try {
                ENTITY_MODELS.forEach((rl, model) -> {
//                    Model model = ref.get();
                    if (model == null) return; // 弱引用已被回收则跳过
                    HBM.LOGGER.info("Entity obj model: " + rl.toString());
                    if (model instanceof IObjModel objEntityModel){
                        if (objEntityModel.getRootModel() == null) objEntityModel.parseJson(rl);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void modifyBakingResult(ModelEvent.ModifyBakingResult event){
        ITEM_MODEL_KEYS.forEach((rl, itemId) -> {
            BakedModel bakedModel = event.getModels().get(rl);
            if (bakedModel instanceof SimpleBakedModel) {
                event.getModels().put(new ModelResourceLocation(itemId, "inventory"), new SimpleBakedModelWrapper((SimpleBakedModel) bakedModel));
            }
        });
    }

    public static BakedModel get(ResourceLocation rl){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        return modelManager.getModel(rl);
    }
    public static Model getEntityModel(ResourceLocation rl){
//        SoftReference<Model> ref = ENTITY_MODELS.get(rl);
//        Model model = ref != null ? ref.get() : null;
        Model model = ENTITY_MODELS.get(rl);
        if (model == null) {
            // 模型被GC清理或未加载，尝试重新加载
            HBM.LOGGER.debug("[Models] Reloading entity model: {}", rl);
            try {
                model = new ObjEntityModelSingle(); // 或根据类型自行加载
//                ENTITY_MODELS.put(rl, new SoftReference<>(model));
                ENTITY_MODELS.put(rl, model);
            } catch (Exception e) {
                HBM.LOGGER.error("Failed to reload model {}", rl, e);
            }
        }
        return model;
    }

    // ================= 生命周期清理与事件钩子 =================
//    private static void clearCaches(String reason) {
//        ITEM_MODEL_KEYS.clear();
//        ENTITY_MODELS.clear();
//        HBM.LOGGER.debug("[Models] caches cleared due to {}", reason);
//    }

    // 模型烘焙完成（资源重载）后，清空缓存，避免旧模型残留；该事件在 MOD 总线
    @net.minecraftforge.eventbus.api.SubscribeEvent
    public static void onBakingCompleted(ModelEvent.BakingCompleted e) {
//        clearCaches("BakingCompleted");
    }

    // 客户端断线/切世界：FORGE 总线事件
    @Mod.EventBusSubscriber(modid = HBM.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeSideHooks {
        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void onClientDisconnect(net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut e) {
//            clearCaches("ClientDisconnect");
        }
        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void onRegisterReload(net.minecraftforge.client.event.RegisterClientReloadListenersEvent e) {
//            e.registerReloadListener((pPreparationBarrier, pResourceManager, pPreparationsProfiler, pReloadProfiler, pBackgroundExecutor, pGameExecutor) -> CompletableFuture.runAsync(() -> clearCaches("ReloadListener")));
        }
    }
}
