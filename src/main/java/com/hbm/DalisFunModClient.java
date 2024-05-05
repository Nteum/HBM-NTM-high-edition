package com.hbm;

import com.hbm.client.gui.ElectricFurnaceScreen;
import com.hbm.client.gui.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class DalisFunModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //注册GUI
        HandledScreens.register(ModScreenHandlers.elecFurnace, ElectricFurnaceScreen::new);
    }
}
