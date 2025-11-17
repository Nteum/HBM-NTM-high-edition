package com.hbm.init;

import com.hbm.compat.bigexplosives.BigExplosivesMod;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


public final class BigExplosivesModTabs {

    public static final DeferredRegister<CreativeModeTab> REGISTRY =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BigExplosivesMod.MODID);

    public static final RegistryObject<CreativeModeTab> EXPLOSIVES = REGISTRY.register("explosives", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("item_group.big_explosives.explosives"))
                    .icon(() -> new ItemStack(BigExplosivesModItems.ATOM_BOMB.get()))
                    .displayItems((params, output) -> {
                        output.accept(BigExplosivesModItems.ATOM_BOMB.get());
                        output.accept(BigExplosivesModItems.FIVE_HUNDRED_KILOGRAM_BOMB.get());
                        // Also surface HBM’s placeable bomb blocks here (moved from weapons/tools)
                        output.accept(ModBlocks.bomb_boy.get());
                        output.accept(ModBlocks.bomb_fat_man.get());
                        output.accept(ModBlocks.bomb_custom.get());
                    })
                    .build());

    private BigExplosivesModTabs() {
    }
}
