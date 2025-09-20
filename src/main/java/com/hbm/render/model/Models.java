package com.hbm.render.model;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.hbm.HBM;
import com.hbm.item.HBMWeapon;
import com.hbm.render.model.entity.ObjEntityModelSingle;
import com.hbm.render.model.item.SimpleBakedModelWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.RegistryObject;

import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Mod.EventBusSubscriber(modid = HBM.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Models {
    private static final Set<ResourceLocation> models = new HashSet<>();
    private static final Map<ResourceLocation, RegistryObject<Item>> itemModels = new HashMap<>();
    private static final Map<ResourceLocation, Model> entityModels = new HashMap<>();
    
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

    public static final ResourceLocation MP_W_15_BALEFIRE = addItem(HBM.rl("item/mp_warhead_15_balefire"), HBMWeapon.MP_WARHEAD_15_BALEFIRE);

    public static final ResourceLocation MISSILE_TEST = addEntity(HBM.modelRl("entity/missile/missile_test"), new ObjEntityModelSingle());

    public static ResourceLocation add(ResourceLocation rl){
        models.add(rl);
        return rl;
    }
    public static ResourceLocation addItem(ResourceLocation rl, RegistryObject<Item> itemRegistryObject){
        models.add(rl);
        itemModels.put(rl, itemRegistryObject);
        return rl;
    }
    public static ResourceLocation addEntity(ResourceLocation rl, Model model){
        entityModels.put(rl, model);
        return rl;
    }
    public static void registerModels(ModelEvent.RegisterAdditional event){
        models.forEach(event::register);
    }
    public static void onClientSetup(FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            // 加载实体模型
            try {
                entityModels.forEach((rl, model) -> {
                    if (model instanceof ObjEntityModelSingle objEntityModel){
                        if (objEntityModel.renderable != null) return;
                        objEntityModel.parseJson(rl);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void modifyBakingResult(ModelEvent.ModifyBakingResult event){
        itemModels.forEach((rl, item) -> {
            BakedModel bakedModel = event.getModels().get(rl);
            if (bakedModel instanceof SimpleBakedModel) {
                event.getModels().put(new ModelResourceLocation(item.getId(), "inventory"), new SimpleBakedModelWrapper((SimpleBakedModel) bakedModel));
            }
        });
    }

    public static BakedModel get(ResourceLocation rl){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        return modelManager.getModel(rl);
    }
    public static Model getEntityModel(ResourceLocation rl){
        return entityModels.get(rl);
    }
}
