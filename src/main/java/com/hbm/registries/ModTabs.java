package com.hbm.registries;

import com.hbm.HBMLang;
import com.hbm.block.HBMBlockComponent;
import com.hbm.item.HBMItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.hbm.HBM.MODID;

public class ModTabs {
    //创造模式物品栏注册表
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> PARTS = CREATIVE_MODE_TABS.register("hbm_parts", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_PARTS.key())).icon(() -> HBMItems.INGOT_URANIUM.get().getDefaultInstance()).build());
    public static final RegistryObject<CreativeModeTab> CONTROL = CREATIVE_MODE_TABS.register("hbm_control", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_CONTROL.key())).icon(() -> HBMItems.RTG_UNIT.get().getDefaultInstance()).build());
    public static final RegistryObject<CreativeModeTab> TEMPLATE = CREATIVE_MODE_TABS.register("hbm_template", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_TEMPLATE.key())).icon(() -> HBMItems.ASSEMBLY_TEMPLATE.get().getDefaultInstance()).build());
    public static final RegistryObject<CreativeModeTab> BLOCKS = CREATIVE_MODE_TABS.register("hbm_blocks", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_BLOCKS.key())).icon(() -> HBMBlockComponent.URANIUM_ORE.get().asItem().getDefaultInstance()).build());
    public static final RegistryObject<CreativeModeTab> MACHINE = CREATIVE_MODE_TABS.register("hbm_machine", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_MACHINE.key())).icon(() -> Blocks.DIRT.asItem().getDefaultInstance()).build());
    public static final RegistryObject<CreativeModeTab> NUKE = CREATIVE_MODE_TABS.register("hbm_nuke", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_NUKE.key())).icon(() -> ModBlocks.bomb_fat_man.get().asItem().getDefaultInstance()).build());
    public static final RegistryObject<CreativeModeTab> MISSILE = CREATIVE_MODE_TABS.register("hbm_missile", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_MISSILE.key())).icon(() -> HBMItems.MISSILE_NUCLEAR.get().getDefaultInstance()).build());
    public static final RegistryObject<CreativeModeTab> WEAPON = CREATIVE_MODE_TABS.register("hbm_weapon", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_WEAPON.key())).icon(() -> HBMItems.GUN_RIFLE.get().getDefaultInstance()).build());
    public static final RegistryObject<CreativeModeTab> CONSUMABLE = CREATIVE_MODE_TABS.register("hbm_consumable", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_CONSUMABLE.key())).icon(() -> HBMItems.BOTTLE_NUKA.get().getDefaultInstance()).build());
}
