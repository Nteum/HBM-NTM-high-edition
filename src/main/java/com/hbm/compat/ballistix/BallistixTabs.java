package com.hbm.compat.ballistix;

import java.util.Arrays;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class BallistixTabs {

    public static final DeferredRegister<CreativeModeTab> REGISTRY =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BallistixCompat.MODID);

    public static final RegistryObject<CreativeModeTab> EXPLOSIVES = REGISTRY.register("explosives", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("item_group.ballistix.explosives"))
                    .icon(() -> new ItemStack(BallistixItems.EXPLOSIVE_ITEMS
                            .get(BallistixExplosiveType.OBSIDIAN).get()))
                    .displayItems((params, output) -> Arrays.stream(BallistixExplosiveType.values())
                            .forEach(type -> output.accept(BallistixItems.EXPLOSIVE_ITEMS.get(type).get())))
                    .build());

    private BallistixTabs() {
    }
}
