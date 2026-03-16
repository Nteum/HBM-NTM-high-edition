package com.hbm.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class Compat {
    public static final String MOD_GT6 = "gregtech";
    public static final String MOD_GCC = "GalacticraftCore";
    public static final String MOD_AR = "advancedrocketry";
    public static final String MOD_EF = "etfuturum";
    public static final String MOD_REC = "ReactorCraft";
    public static final String MOD_TIC = "TConstruct";
    public static final String MOD_RC = "Railcraft";
    public static final String MOD_TC = "tc";
    public static final String MOD_EIDS = "endlessids";
    public static final String MOD_ANG = "angelica";

    public static Item tryLoadItem(String domain, String name) {
        return BuiltInRegistries.ITEM.get(rl(domain, name));
    }

    public static Block tryLoadBlock(String domain, String name) {
        return BuiltInRegistries.BLOCK.get(rl(domain, name));
    }

    private static ResourceLocation rl(String domain, String name) {
        return new ResourceLocation(domain, name);
    }
}
