package com.hbm.render.model;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.hbm.HBM;
import com.hbm.render.model.entity.ObjEntityModelSingle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Mod.EventBusSubscriber(modid = HBM.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Models {
    private static final Set<ResourceLocation> models = new HashSet<>();
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
    public static final ResourceLocation MP_W_15_BALEFIRE = add(HBM.rl("item/missile/mp_w_15"));
    public static final ResourceLocation CHEMPLANT_BODY = add(HBM.rl("block/chemplant/chemplant_new_body"));
    public static final ResourceLocation CHEMPLANT_PISTON = add(HBM.rl("block/chemplant/chemplant_new_piston"));
    public static final ResourceLocation CHEMPLANT_SPINNER = add(HBM.rl("block/chemplant/chemplant_new_spinner"));

//    public static final ResourceLocation MISSILE_TEST = addEntity(HBM.modelRl("entity/missile/missile_test"));
    public static final ResourceLocation MISSILE_MICRO = addEntity(HBM.modelRl("entity/missile/missile_micro.obj"));

    public static ResourceLocation add(ResourceLocation rl){
        models.add(rl);
        return rl;
    }
    public static ResourceLocation addEntity(ResourceLocation rl){
        entityModels.put(rl, null);
        return rl;
    }
    public static void registerModels(ModelEvent.RegisterAdditional event){
        models.forEach(event::register);
    }
    public static void loadEntityModel(FMLClientSetupEvent event){
        event.enqueueWork(() -> {
//            Gson gson = new Gson();
//            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            try {
                entityModels.forEach((rl, model) -> {
//                    Optional<Resource> resource = resourceManager.getResource(MISSILE_TEST.withSuffix(".json"));
                    if (model != null
//                            && resource.isEmpty()
                    ) return;
//                    try (InputStreamReader reader = new InputStreamReader(resource.get().open())) {
//                        JsonObject jsonData = gson.fromJson(reader, JsonObject.class);
                        // 处理你的 jsonData
//                        System.out.println("Loaded JSON: " + jsonData.toString());
                        ObjModel objModel = ObjLoader.INSTANCE.loadModel(new ObjModel.ModelSettings(rl, false, true, true, true, null));
//                        ObjModel objModel = ObjLoader.INSTANCE.read(jsonData, null);
//                    ObjEntityModelSingle entityModelSingle = new ObjEntityModelSingle(objModel);
                        // 将ObjModel转换成实体Model
                        entityModels.put(rl, new ObjEntityModelSingle(objModel));
//                    }catch (Exception e){}
                });
            } catch (Exception e) {
                e.printStackTrace();
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
