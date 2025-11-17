package com.hbm.compat.ballistix;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BallistixItems {

    public static final DeferredRegister<Item> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ITEMS, BallistixCompat.MODID);

    public static final Map<BallistixExplosiveType, RegistryObject<Item>> EXPLOSIVE_ITEMS =
            new EnumMap<>(BallistixExplosiveType.class);

    static {
        for (BallistixExplosiveType type : BallistixExplosiveType.portedTypes()) {
            EXPLOSIVE_ITEMS.put(type, REGISTRY.register(type.id(), () ->
                    new BlockItem(BallistixBlocks.EXPLOSIVES.get(type).get(),
                            new Item.Properties())));
        }
    }

    private BallistixItems() {
    }
}
