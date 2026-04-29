package com.hbm.render.model;

import com.hbm.HBM;
import com.hbm.item.HBMWeapon;
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
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = HBM.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Models {
    private static final Set<ResourceLocation> models = new HashSet<>();
    // 为避免静态强引用导致跨世界/资源重载的内存泄漏，改为并发Map + 弱引用/轻量键值
    // 物品：只缓存 资源模型RL -> 物品ID(ResourceLocation) 的映射，避免强持有 RegistryObject
    private static final ConcurrentMap<ResourceLocation, ResourceLocation> ITEM_MODEL_KEYS = new ConcurrentHashMap<>();
    // 实体：缓存 RL -> 实体模型 的弱引用，便于GC在资源重载/内存紧张时回收
    private static final ConcurrentMap<ResourceLocation, WeakReference<Model>> ENTITY_MODELS = new ConcurrentHashMap<>();
    // 为了在缓存清理或GC后能够重新构造模型，额外保留工厂引用
    private static final ConcurrentMap<ResourceLocation, Supplier<Model>> ENTITY_MODEL_FACTORIES = new ConcurrentHashMap<>();
    
    public static final ResourceLocation ASSEMBLER_BODY = add(HBM.rl("block/assembler/assembler_body"));
    public static final ResourceLocation ASSEMBLER_COG = add(HBM.rl("block/assembler/assembler_cog"));
    public static final ResourceLocation ASSEMBLER_SLIDER = add(HBM.rl("block/assembler/assembler_slider"));
    public static final ResourceLocation ASSEMBLER_ARM = add(HBM.rl("block/assembler/assembler_arm"));
    public static final ResourceLocation CRUCIBLE = add(HBM.rl("block/crucible"));
    public static final ResourceLocation FAT_MAN = add(HBM.rl("block/bomb/fat_man"));
    public static final ResourceLocation BOY = add(HBM.rl("block/bomb/boy"));
    public static final ResourceLocation CUSTOM_NUKE = add(HBM.rl("block/bomb/custom"));
    public static final ResourceLocation CUSTOM_NUKE_GADGET = add(HBM.rl("block/bomb/custom_gadget"));
    public static final ResourceLocation CUSTOM_NUKE_MIKE = add(HBM.rl("block/bomb/custom_mike"));
    public static final ResourceLocation CUSTOM_NUKE_TSAR = add(HBM.rl("block/bomb/custom_tsar"));
    public static final ResourceLocation CUSTOM_NUKE_FLEIJA = add(HBM.rl("block/bomb/custom_fleija"));
    public static final ResourceLocation CUSTOM_NUKE_SOLINIUM = add(HBM.rl("block/bomb/custom_solinium"));
    public static final ResourceLocation CUSTOM_NUKE_PROTOTYPE = add(HBM.rl("block/bomb/custom_prototype"));
    public static final ResourceLocation CUSTOM_NUKE_MULTI = add(HBM.rl("block/bomb/custom_multi"));
    public static final ResourceLocation BLACK_HOLE = add(HBM.rl("block/effect/sphere"));
    public static final ResourceLocation CHEMPLANT_BODY = add(HBM.rl("block/chemplant/chemplant_new_body"));
    public static final ResourceLocation CHEMPLANT_PISTON = add(HBM.rl("block/chemplant/chemplant_new_piston"));
    public static final ResourceLocation CHEMPLANT_SPINNER = add(HBM.rl("block/chemplant/chemplant_new_spinner"));
    public static final ResourceLocation LAUNCH_PAD = add(HBM.rl("block/launch_pad"));
    public static final ResourceLocation WOOD_BURNER = add(HBM.rl("block/machine_wood_burner"));
    public static final ResourceLocation ZIRNOX_BASE = add(HBM.rl("block/reactor/zirnox"));
    public static final ResourceLocation RESEARCH_REACTOR_BASE = add(HBM.rl("block/reactor/reactor_small_base"));
    public static final ResourceLocation RESEARCH_REACTOR_RODS = add(HBM.rl("block/reactor/reactor_small_rods"));
    public static final ResourceLocation BREEDER_REACTOR = add(HBM.rl("block/reactor/breeder"));
    public static final ResourceLocation SPACE_STATION_BASE = add(HBM.rl("block/space_station_base"));
    public static final ResourceLocation CONNECTOR = add(HBM.rl("block/connector"));
    public static final ResourceLocation PRESS_HEAD = add(HBM.rl("block/press/press_head"));
    public static final ResourceLocation FIREBOX = add(HBM.rl("block/firebox"));

    public static final ResourceLocation MP_W_15_BALEFIRE = addItem(HBM.rl("item/mp_warhead_15_balefire"), HBMWeapon.MP_WARHEAD_15_BALEFIRE);
//    public static final ResourceLocation GUN_RIFLE = addItem(HBM.rl("item/gun_marseleg"), ModItems.GUN_RIFLE.getId());

    public static final ResourceLocation MISSILE_TEST = addEntity(HBM.modelRl("entity/missile/missile_test"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_MICRO = addEntity(HBM.modelRl("entity/missile/missile_micro"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_MICRO_EMP = addEntity(HBM.modelRl("entity/missile/missile_micro_emp"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_MICRO_BHOLE = addEntity(HBM.modelRl("entity/missile/missile_micro_bhole"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_MICRO_SCHRAB = addEntity(HBM.modelRl("entity/missile/missile_micro_schrabidium"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_MICRO_TAINT = addEntity(HBM.modelRl("entity/missile/missile_micro_taint"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_V2_GENERIC = addEntity(HBM.modelRl("entity/missile/missile_v2_generic"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_V2_INCENDIARY = addEntity(HBM.modelRl("entity/missile/missile_v2_incendiary"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_V2_CLUSTER = addEntity(HBM.modelRl("entity/missile/missile_v2_cluster"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_V2_BUSTER = addEntity(HBM.modelRl("entity/missile/missile_v2_buster"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_V2_DECOY = addEntity(HBM.modelRl("entity/missile/missile_v2_decoy"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_STRONG_GENERIC = addEntity(HBM.modelRl("entity/missile/missile_strong_generic"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_STRONG_EMP = addEntity(HBM.modelRl("entity/missile/missile_strong_emp"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_STRONG_INCENDIARY = addEntity(HBM.modelRl("entity/missile/missile_strong_incendiary"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_STRONG_CLUSTER = addEntity(HBM.modelRl("entity/missile/missile_strong_cluster"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_STRONG_BUSTER = addEntity(HBM.modelRl("entity/missile/missile_strong_buster"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_HUGE_GENERIC = addEntity(HBM.modelRl("entity/missile/missile_huge_generic"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_HUGE_INFERNO = addEntity(HBM.modelRl("entity/missile/missile_huge_inferno"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_HUGE_RAIN = addEntity(HBM.modelRl("entity/missile/missile_huge_rain"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_HUGE_DRILL = addEntity(HBM.modelRl("entity/missile/missile_huge_drill"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_ATLAS_NUCLEAR = addEntity(HBM.modelRl("entity/missile/missile_atlas_nuclear"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_ATLAS_THERMO = addEntity(HBM.modelRl("entity/missile/missile_atlas_thermo"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_ATLAS_VOLCANO = addEntity(HBM.modelRl("entity/missile/missile_atlas_volcano"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_ATLAS_DOOMSDAY = addEntity(HBM.modelRl("entity/missile/missile_atlas_doomsday"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_ATLAS_DOOMSDAY_RUSTED = addEntity(HBM.modelRl("entity/missile/missile_atlas_doomsday_rusted"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_ATLAS_SHUTTLE = addEntity(HBM.modelRl("entity/missile/missile_atlas_shuttle"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_STEALTH = addEntity(HBM.modelRl("entity/missile/missile_stealth"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_ABM = addEntity(HBM.modelRl("entity/missile/missile_abm"), ObjEntityModelSingle::new);
    public static final ResourceLocation MISSILE_NUKE = MISSILE_ATLAS_NUCLEAR;
    public static final ResourceLocation T51 = addEntity(HBM.modelRl("item/armor_t51"), ModelArmorT51::new);
    public static final ResourceLocation BISMUTH = addEntity(HBM.modelRl("item/armor_bismuth"), ModelArmorBismuth::new);
    public static final ResourceLocation DESH = addEntity(HBM.modelRl("item/armor_desh"), ModelArmorDesh::new);
    public static final ResourceLocation DIESEL = addEntity(HBM.modelRl("item/armor_diesel"), ModelArmorDiesel::new);
    public static final ResourceLocation RPA = addEntity(HBM.modelRl("item/armor_rpa"), ModelArmorRPA::new);
    public static final ResourceLocation AJR = addEntity(HBM.modelRl("item/armor_ajr"), ModelArmorAJR::new);
    public static final ResourceLocation BJ = addEntity(HBM.modelRl("item/armor_bj"), ModelArmorBJ::new);
    public static final ResourceLocation GLYPHID = addEntity(HBM.modelRl("entity/glyphid"), ModelGlyphid::new);

    public static ResourceLocation add(ResourceLocation rl){
        models.add(rl);
        return rl;
    }
    public static ResourceLocation addItem(ResourceLocation rl, RegistryObject<Item> itemRegistryObject){
        models.add(rl);
        // 只记录物品的ID，避免强引用整个 RegistryObject 链
        ITEM_MODEL_KEYS.put(rl, itemRegistryObject.getId());
        return rl;
    }
    public static ResourceLocation addEntity(ResourceLocation rl, Supplier<Model> modelFactory){
        ENTITY_MODEL_FACTORIES.put(rl, modelFactory);
        Model model = buildEntityModel(rl, modelFactory);
        if (model != null) {
            ENTITY_MODELS.put(rl, new WeakReference<>(model));
        }
        return rl;
    }
    public static void registerModels(ModelEvent.RegisterAdditional event){
        models.forEach(event::register);
    }
    public static void onClientSetup(FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            try {
                reloadEntityModels();
            } catch (Exception e) {
                HBM.LOGGER.error("Failed to preload entity models", e);
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
        WeakReference<Model> ref = ENTITY_MODELS.get(rl);
        Model model = ref != null ? ref.get() : null;
        if (model == null) {
            model = rebuildEntityModel(rl);
        } else {
            prepareModel(rl, model);
        }
        return model;
    }

    // ================= 生命周期清理与事件钩子 =================
    private static void clearCaches(String reason) {
        ITEM_MODEL_KEYS.clear();
        ENTITY_MODELS.clear();
        HBM.LOGGER.debug("[Models] caches cleared due to {}", reason);
    }

    // 模型烘焙完成（资源重载）后，清空缓存，避免旧模型残留；该事件在 MOD 总线
    @net.minecraftforge.eventbus.api.SubscribeEvent
    public static void onBakingCompleted(ModelEvent.BakingCompleted e) {
        clearCaches("BakingCompleted");
        reloadEntityModels();
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
            e.registerReloadListener(new SimplePreparableReloadListener<Void>() {
                @Override
                protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
                    return null;
                }

                @Override
                protected void apply(Void data, ResourceManager resourceManager, ProfilerFiller profiler) {
                    clearCaches("ReloadListener");
                    reloadEntityModels();
                }
            });
        }
    }

    private static Model buildEntityModel(ResourceLocation rl, Supplier<Model> modelFactory) {
        Model model = modelFactory.get();
        prepareModel(rl, model);
        return model;
    }

    private static Model rebuildEntityModel(ResourceLocation rl) {
        Supplier<Model> factory = ENTITY_MODEL_FACTORIES.get(rl);
        if (factory == null) {
            return null;
        }
        Model model = buildEntityModel(rl, factory);
        if (model != null) {
            ENTITY_MODELS.put(rl, new WeakReference<>(model));
        }
        return model;
    }

    private static void reloadEntityModels() {
        ENTITY_MODELS.clear();
        ENTITY_MODEL_FACTORIES.keySet().forEach(Models::rebuildEntityModel);
    }

    private static void prepareModel(ResourceLocation rl, Model model) {
        if (!(model instanceof IObjModel objModel)) {
            return;
        }
        if (objModel.getRootModel() != null) {
            return;
        }
        Minecraft minecraft = safeGetMinecraft();
        if (minecraft == null) {
            return;
        }
        ResourceManager resourceManager = minecraft.getResourceManager();
        if (resourceManager == null) {
            return;
        }
        ResourceLocation jsonPath = rl.withSuffix(".json");
        if (resourceManager.getResource(jsonPath).isEmpty()) {
            HBM.LOGGER.debug("[Models] Resource {} not ready; postpone parsing", jsonPath);
            return;
        }
        objModel.parseJson(rl);
    }

    private static Minecraft safeGetMinecraft() {
        try {
            return Minecraft.getInstance();
        } catch (Throwable t) {
            return null;
        }
    }
}
