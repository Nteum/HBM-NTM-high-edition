package net.mcreator.nuclearcraft.init;

import net.mcreator.nuclearcraft.client.gui.AdvancedWorkBechGuiScreen;
import net.mcreator.nuclearcraft.client.gui.BasicWorkBenchGuiScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/init/BigExplosivesModScreens.class */
public class BigExplosivesModScreens {
    @SubscribeEvent
    public static void clientLoad(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.m_96206_((MenuType) BigExplosivesModMenus.BASIC_WORK_BENCH_GUI.get(), BasicWorkBenchGuiScreen::new);
            MenuScreens.m_96206_((MenuType) BigExplosivesModMenus.ADVANCED_WORK_BECH_GUI.get(), AdvancedWorkBechGuiScreen::new);
        });
    }
}
