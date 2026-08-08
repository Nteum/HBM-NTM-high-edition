package com.hbm.core;

import net.minecraftforge.fml.common.Mod;

import static com.hbm.HBM.MODID;

public class CommonEvents {
    /** 双端forge总线事件 */
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CommonForgeEvents{

    }

    /** 双端mod总线事件 */
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonModEvents{

    }
}
