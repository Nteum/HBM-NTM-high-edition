// 集中管理“旧ID -> 新ID / 贴图名映射”
// 放在 client-only 包里
package com.hbm.render.model;

import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.SimpleBakedModel;

import java.util.*;
import static com.hbm.HBM.MODID;
import static com.hbm.HBM.LOGGER;
/*	1.	构建前：一行式脚本改 MTL 里的贴图路径；
	2.	注册阶段：RegisterAdditional 把所有旧/新 ID 都注册；
	3.	烘焙阶段：ModifyBakingResult 做 ID 映射 & 道具 inventory 绑定；
	4.	运行期：已实现的弱引用缓存 + 生命周期清理；
	5.	质检：ModelQC 在启动时扫一遍常见问题。
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModelPorting {
    // 旧->新模型 ID 映射（OBJ/JSON 都可用）
    private static final Map<ResourceLocation, ResourceLocation> MODEL_ID_REMAP = Map.ofEntries(
        // 例：旧 obj 名迁到新路径
        Map.entry(new ResourceLocation(MODID, "block/old_crucible"),
                  new ResourceLocation(MODID, "block/crucible/crucible")),
        Map.entry(new ResourceLocation(MODID, "item/old_fatman"),
                  new ResourceLocation(MODID, "block/bomb/fat_man"))
    );

    // 道具“烘焙后”替换为 inventory 变体（把 OBJ 的 baked 放到物品模型位）
    private static final Map<ResourceLocation, ResourceLocation> ITEM_INVENTORY_BIND = Map.ofEntries(
        Map.entry(new ResourceLocation(MODID, "item/mp_warhead_15_balefire"),
                  new ResourceLocation(MODID, "item/mp_warhead_15_balefire"))
    );

    private ModelPorting() {}

    // 注册所有将要出现的模型（OBJ/JSON 均可）
    @SubscribeEvent
    public static void onRegisterAdditional(ModelEvent.RegisterAdditional e) {
        MODEL_ID_REMAP.values().forEach(e::register);
        MODEL_ID_REMAP.keySet().forEach(e::register);
        ITEM_INVENTORY_BIND.values().forEach(e::register);
        LOGGER.debug("[ModelPorting] registered additional models: {}", MODEL_ID_REMAP);
    }

    // 烘焙结果修改：统一做 ID 替换 + 物品 inventory 绑定
    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult e) {
        var models = e.getModels();

        // 1) 旧ID → 新ID 替换（把烘焙产物搬家）
        MODEL_ID_REMAP.forEach((oldId, newId) -> {
            BakedModel baked = models.get(oldId);
            if (baked != null) {
                models.put(newId, baked);
                LOGGER.debug("[ModelPorting] remapped baked model {} -> {}", oldId, newId);
            }
        });

        // 2) 绑定物品的 inventory 变体（用 SimpleBakedModel 即可）
        ITEM_INVENTORY_BIND.forEach((itemId, bakedId) -> {
            BakedModel baked = models.get(bakedId);
            if (baked instanceof net.minecraft.client.resources.model.SimpleBakedModel sbm) {
                ModelResourceLocation inv = new ModelResourceLocation(itemId, "inventory");
                models.put(inv, baked);
                LOGGER.debug("[ModelPorting] bind inventory model {} -> {}", inv, bakedId);
            }
        });
    }
}