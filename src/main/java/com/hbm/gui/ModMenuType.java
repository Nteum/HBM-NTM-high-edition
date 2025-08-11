package com.hbm.gui;

import com.hbm.blockentity.base2.CapabilityBlockEntity;
import com.hbm.blockentity.machine.ElectricFurnaceEntity;
import com.hbm.gui.menu.*;
import com.hbm.HBM;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuType {
    public static final DeferredRegister<MenuType<?>> MOD_MENU_TYPES = DeferredRegister.create(Registries.MENU, HBM.MODID);
    public static final RegistryObject<MenuType<DifurnaceMenu>> DIFURNACE_MENU =
            MOD_MENU_TYPES.register("difurnace_menu",()->new MenuType<>(DifurnaceMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<PressMenu>> PRESS_MENU =
            MOD_MENU_TYPES.register("press_menu",()->new MenuType<>(PressMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<BatteryMenu>> BATTERY_MENU =
            MOD_MENU_TYPES.register("battery_menu",()->new MenuType<>(BatteryMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<AssemblerMenu>> ASSEMBLER_MENU =
            MOD_MENU_TYPES.register("assembler_menu",()->new MenuType<>(AssemblerMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<ChemplantMenu>> CHEMPLANT_MENU =
            MOD_MENU_TYPES.register("chemplant_menu",
//                    ()->new MenuType<>(ChemplantMenu::new, FeatureFlags.VANILLA_SET)
                    ()->IForgeMenuType.create(
                    (windowId, inv, data) -> {
                        // 这里是服务端→客户端的构造（通常会读 data）
                        // 但如果是客户端直接打开，可以自己调用另一个构造方法
                        BlockPos pos = data.readBlockPos();
                        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
//                        BlockEntity be = inv.player.level().getBlockEntity(pos);
//                        if (be instanceof CapabilityBlockEntity blockEntity) {
//                            return new ChemplantMenu(windowId, inv, blockEntity);
//                        }
                        return new ChemplantMenu(windowId, inv, be);
                    }
            )
            );
    public static final RegistryObject<MenuType<BarrelMenu>> BARREL_MENU =
            MOD_MENU_TYPES.register("barrel_menu",()->new MenuType<>(BarrelMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<ElectricFurnaceMenu>> ELECTRIC_FURNACE_MENU =
            MOD_MENU_TYPES.register("electric_furnace_menu",()->new MenuType<>(ElectricFurnaceMenu::new, FeatureFlags.VANILLA_SET));
}
