package com.hbm.client.gui;

import com.hbm.DalisFunMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

//ScreenHandler注册机制
//参考原版ScreenHandlerType
public class ModScreenHandlers {
    public static final ScreenHandlerType<ElectricFurnaceScreenHandler> elecFurnace = ModScreenHandlers.register("elec_furnace",ElectricFurnaceScreenHandler::new);
    public static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, id, new ScreenHandlerType<T>(factory, FeatureFlags.VANILLA_FEATURES));
    }
    // 在程序入口使用，让fabric在合适的时间加载它
    public static void registerScreenHandler(){
        DalisFunMod.LOGGER.info("ScreenHandler register");
    }
}
