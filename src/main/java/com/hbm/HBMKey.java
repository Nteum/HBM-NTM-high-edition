package com.hbm;

import com.google.common.base.Strings;
import net.minecraftforge.registries.ForgeRegistries;

//mod使用的key，仅用于mod内使用，不用于翻译
public class HBMKey {
    public static final String MODID = "hbm";
    // block state
    public static final String IS_CORE = "is_core";
    public static final String VARIANT = "variant";
    // 注册表
    public static final String BASIC_MODEL = "basic_model";
    public static final String SPAWN_EGG_MODEL = "spawn_egg_model";
    public static final String MODEL_ENTITY = "entity_model";
    public static final String MODEL_DYNAMIC = "model_dynamic";
    public static final String MODEL_CUBE_ALL = "model_cube_all";
    public static final String MODEL_CUBE_TOP = "cube_top";
    public static final String MODEL_CUBE_BOTTOM_TOP = "model_cube_bottom_top";
    public static final String MODEL_FRONT_SIDE = "model_front_side";
    public static final String MODEL_FRONT_SIDE_TOP = "model_front_side_top";
    public static final String MODEL_PILLAR = "model_pillar";
    public static final String MODEL_EXISTING_FILE = "model_existing_file";
    public static final String MODEL_STANDALONE = "model_standalone";
    public static final String MODEL_DIFURNACE = "model_difurnace";
    public static final String MODEL_HORIZONTAL_WITH_FILE = "model_horizontal_with_file";
    public static final String MODEL_LEAVES = "model_leaves";
    public static final String MODEL_EXISTING = "model_existing";

    public static final String LITERALLY = "literally";
    public static final String ORDERLY_GEN = "orderly_gen";
    public static final String REVERSE_GEN = "reverse_gen";
    public static final String ORDERLY_GEN_EXCEPT_FIRST = "orderly_gen_except_first";
    public static final String GEN_STANDALONE = "gen_standalone";
    public static final String DROP_SELF = "drop_self";
    public static final String DROP_NONE = "drop_none";
    public static final String DROP_STANDALONE = "drop_standalone";
    public static final String SHREDDER = "shredder";
    // key relate to capability
    public static final String DATA = "hbmdata";
    public static final String CAPS = "capability";
    public static final String ENERGY = "energy";
    public static final String FLUIDS = "fluids";
    public static final String STORED = "stored";
    public static final String MODE = "mode";
    // machine process
    public static final String MUFFLED = "muffled";
    public static final String RUNNING = "running";
    public static final String RESULT_ITEM = "resultItem";
    public static final String IS_FORMED = "isFormed";
    public static final String CORE_POS = "corePos";
    public static final String RECIPE_NOW = "recipe_now";
    public static final String PROGRESS = "progress";
    public static final String DURATION = "duration";
    public static final String TICK_POWER = "tick_power";
    public static final String POLLUTION_TYPE = "pollution_type";
    public static final String TYPE = "type";
    public static final String POLLUTION = "pollution";
    public static final String RADIATION = "radiation";
    public static final String FLUX = "flux";
    public static final String HEAT = "heat";
    public static final String PRESSURE = "pressure";
    public static final String DELAY = "delay";
    //cable
    public static final String FORBID_DIR = "forbidDir";
    public static final String CONN_LIMIT = "conn_limit";
    //Server to Client specific sync NBT tags
    public static final String CURRENT_ACCEPTORS = "acceptors";
    public static final String CURRENT_CONNECTIONS = "connections";
    public static final String NETWORK = "network";
    public static final String VALVE = "valve";
    //generic
    public static final String ITEM = "item";
    public static final String TAG = "tag";
    public static final String NUM = "nums";
    public static final String COUNT = "count";
    public static final String VOLUME = "volume";
    public static final String SIDE = "side";
    public static final String REDSTONE = "redstone";
    public static final String CONNECTION = "connection";
    public static final String POSITION = "position";
    public static final String POSITIONS = "positions";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String DIMENSION = "dimension";
    public static final String TOOLTIP = "tooltip";
    public static final String COUNTDOWN = "countdown";
    public static final String WATER_TIMER = "waterTimer";
    public static final String JOINED = "joined";
    public static final String UPGRADE = "upgrade";
    public static final String FILTER = "filter";
    public static final String ORDER = "order";
    // machine name
    public static final String BLAST = "blast";
    public static final String ASSEMBLER = "assembler";
    public static final String CHEMPLANT = "chemplant";
    // tag
    public static final String NUGGETS = "nuggets";
    public static final String INGOTS = "ingots";
    public static final String DUSTS = "dusts";
    public static final String SMALL_DUSTS = "small_dusts";
    public static final String GEMS = "gems";
    public static final String CRYSTALS = "crystals";
    public static final String PLATES = "plates";
    public static final String CAST_PLATES = "cast_plates";
    public static final String BILLETS = "billets";
    public static final String STORAGE_BLOCKS = "storage_blocks";
    public static final String ORES = "ores";
    public static final String WIRE = "wire";

    public static final String IRON = "iron";
    public static final String TIN = "tin";
    public static final String STEEL = "steel";
    public static final String URANIUM = "uranium";
    public static final String TITANIUM = "titanium";
    public static final String ALUMINIUM = "aluminium";
    public static final String LEAD = "lead";
    public static final String QUARTZ = "quartz";
    public static final String LAPIS = "lapis_lazuli";
    public static final String DIAMOND = "diamond";
    public static final String EMERALD = "emerald";
    public static final String BIOMASS = "biomass";
    public static final String COKE = "coke";
    public static final String COAL = "coal";
    public static final String LIGNITE = "lignite";
    public static final String SAWDUST = "sawdust";
    public static final String COPPER = "copper";
    public static final String RED_COPPER = "red_copper";
    public static final String GOLD = "gold";
    public static final String TUNGSTEN = "tungsten";
    public static final String ADVANCED_ALLOY = "advanced_alloy";
    public static final String CARBON = "carbon";
    public static final String SCHRABIDIUM = "schrabidium";
    public static final String ZINC = "zinc";
    public static final String MAGNETIZED_TUNGSTEN = "magnetized_tungsten";

    // GUI
    public static final String BTN = "btn";
    public static final String CLICK = "click";

    public static String link(String ... strings){
        return link('/', strings);
    }
    public static String link(char separator, String ... strings){
        if (strings.length == 0) return "";
        if (strings.length == 1) return strings[0];
        StringBuilder sb = new StringBuilder(strings[0]);
        for (int i = 1; i < strings.length; i++) {
            sb.append(separator).append(strings[i]);
        }
        return sb.toString();
    }
}
