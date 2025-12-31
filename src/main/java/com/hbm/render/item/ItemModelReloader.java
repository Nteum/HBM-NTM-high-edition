package com.hbm.render.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.hbm.registries.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class ItemModelReloader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final ItemModelReloader INSTANCE = new ItemModelReloader();
    private static final Logger LOGGER = LogManager.getLogger("HBM-ItemModelDebugger");
    private ItemModelReloader() {
        super(GSON, "models/item");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
//        reloadSpecificItemModel(ModItems.INGOT_U238M2.get());
    }

    public static void reloadSpecificItemModel(Item item) {
        Minecraft mc = Minecraft.getInstance();
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);

        if (itemId == null) return;

        // 获取模型位置
        ModelResourceLocation modelLoc = new ModelResourceLocation(itemId, "inventory");

        // 2. 重新加载模型 JSON
        ResourceLocation modelJsonLoc = new ResourceLocation(itemId.getNamespace(), "models/item/" + itemId.getPath() + ".json");

        ModelManager manager = mc.getModelManager();
        ItemRenderer itemRenderer = mc.getItemRenderer();
        manager.getModelBakery().getBakedTopLevelModels().remove(modelLoc);
        itemRenderer.getItemModelShaper().register(item, modelLoc);
        itemRenderer.getItemModelShaper().rebuildCache();
    }
}
