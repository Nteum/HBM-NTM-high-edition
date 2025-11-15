package com.hbm.item;

import com.hbm.HBM;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class HBMItemProperties {
    private static final Logger LOGGER = HBM.LOGGER;
    private HBMItemProperties() {}

    // 条目结构：持有 RegistryObject<Item>（更安全）、property 名、以及 client-side Supplier<Boolean>
    public static record PropertyEntry(Supplier<? extends Item> itemSupplier, String propertyName, Supplier<Boolean> condition) {}

    // 线程安全的列表（写入在注册阶段，读取在 client setup）
// 你也可以用 ConcurrentLinkedQueue，如果 build 可能在并发上下文中调用
    private static final List<PropertyEntry> ENTRIES = new ArrayList<>();

    public static void add(Supplier<? extends Item> itemSupplier, String propertyName, Supplier<Boolean> condition) {
        ENTRIES.add(new PropertyEntry(itemSupplier, propertyName, condition));
    }

    // 在 FMLClientSetupEvent 中调用：遍历并注册
    public static void registerAll() {
        ENTRIES.forEach(entry -> {
            Item item;
            try {
                item = entry.itemSupplier.get();
            } catch (Exception e) {
                // 防御性处理，避免单个失败阻塞其它注册
                LOGGER.warn("Failed to get item for property registration: {}", entry.propertyName, e);
                return;
            }
            if (item == null) {
                LOGGER.warn("Item supplier returned null for property {}", entry.propertyName);
                return;
            }

            ResourceLocation propId = new ResourceLocation(entry.propertyName);
            ItemProperties.register(item, propId, (stack, level, entity, seed) -> {
                try {
                    return entry.condition.get() ? 1.0F : 0.0F;
                } catch (Throwable t) {
                    // 防御：条件函数不应抛异常
                    return 0.0F;
                }
            });
        });
        // 可选：清空集合，避免重复注册
        ENTRIES.clear();
    }
}
