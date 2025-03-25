package com.hbm.api.resource;

import java.util.Arrays;
import java.util.List;

public enum OreType {
    URANIUM("uranium"),
    TITANIUM("titanium"),
    THORIUM("thorium"),
    NITER("niter"),
    TUNGSTNE("tungsten"),
    ALUMINIUM("aluminium"),
    FLUORITE("fluorite"),
    LEAD("lead"),
    BERYLLIUM("beryllium"),
    SA326("sa326"),
    ASBESTOS("asbestos"),
    OIL("oil"),
    RARE_EARTH("rare_earth"),
    LITHIUM("lithium"),
    COBALT("cobalt"),
    COLTAN("coltan"),
    SMOLDER("smolder"),
    GAS("gas"),
    PLUTONIUM("plutonium"),
    TIKITE("tikite");

    public static final List<OreType> NUGGETS = Arrays.asList();
    public static final List<OreType> SHARDS = Arrays.asList();
    public static final List<OreType> DUSTS = Arrays.asList();
    public static final List<OreType> INGOTS = Arrays.asList();

    public final String key;
    OreType(String key) {
        this.key = key;
    }
}
