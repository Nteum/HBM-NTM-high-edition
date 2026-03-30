package com.hbm.item;

import com.hbm.registries.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Compatibility bridge for legacy references to {@code HBMtools}.
 */
@Deprecated
public class HBMtools extends ModItems {

    private HBMtools() {
    }

    /**
     * Legacy typo-compatible alias.
     */
    public static final RegistryObject<Item> ORE_SCANNER = ORE_CANNER;

    public static void register(DeferredRegister<Item> items) {
        // Intentionally no-op: tools are already declared in ModItems.
    }

    public static void creativeTab(CreativeModeTab.Output output) {
        output.accept(UPGRADE_BASE.get());
        output.accept(GEIGER_COUNTER.get());
        output.accept(BUILD_WAND.get());
        output.accept(DEBUG_WAND.get());
        output.accept(DESIGNATOR.get());
        output.accept(RBMK_TOOL.get());
        output.accept(DOSIMETER.get());
        output.accept(DIGAMMA_DIAGNOSTIC.get());
        output.accept(POLLUTION_DETECTOR.get());
        output.accept(ORE_SCANNER.get());
        output.accept(HAND_DRILL.get());
        output.accept(HAND_DRILL_DESH.get());
        output.accept(SCREWDRIVER.get());
        output.accept(reacher.get());
    }

    public static void genModel(ItemModelProvider provider) {
        provider.basicItem(UPGRADE_BASE.get());
        provider.basicItem(GEIGER_COUNTER.get());
        provider.basicItem(DESIGNATOR.get());
        provider.basicItem(RBMK_TOOL.get());
        provider.basicItem(DOSIMETER.get());
        provider.basicItem(DIGAMMA_DIAGNOSTIC.get());
        provider.basicItem(POLLUTION_DETECTOR.get());
        provider.basicItem(ORE_SCANNER.get());
        provider.basicItem(BUILD_WAND.get());
        provider.basicItem(DEBUG_WAND.get());
    }
}
