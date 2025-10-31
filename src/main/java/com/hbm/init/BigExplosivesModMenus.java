package net.mcreator.nuclearcraft.init;

import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.mcreator.nuclearcraft.world.inventory.AdvancedWorkBechGuiMenu;
import net.mcreator.nuclearcraft.world.inventory.BasicWorkBenchGuiMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/init/BigExplosivesModMenus.class */
public class BigExplosivesModMenus {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, BigExplosivesMod.MODID);
    public static final RegistryObject<MenuType<BasicWorkBenchGuiMenu>> BASIC_WORK_BENCH_GUI = REGISTRY.register("basic_work_bench_gui", () -> {
        return IForgeMenuType.create(BasicWorkBenchGuiMenu::new);
    });
    public static final RegistryObject<MenuType<AdvancedWorkBechGuiMenu>> ADVANCED_WORK_BECH_GUI = REGISTRY.register("advanced_work_bech_gui", () -> {
        return IForgeMenuType.create(AdvancedWorkBechGuiMenu::new);
    });
}
