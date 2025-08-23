package com.hbm.config;


import com.electronwill.nightconfig.core.CommentedConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import static com.hbm.config.CommonConfig.addBoolean;
import static com.hbm.config.CommonConfig.addInt;

public class ConfigLBSM {
    public static boolean enableLBSM = false;
    public static boolean enableLBSMFullSchrab = true;
    public static boolean enableLBSMShorterDecay = true;
    public static boolean enableLBSMSimpleArmorRecipes = true;
    public static boolean enableLBSMSimpleToolRecipes = true;
    public static boolean enableLBSMSimpleAlloy = true;
    public static boolean enableLBSMSimpleChemsitry = true;
    public static boolean enableLBSMSimpleCentrifuge = true;
    public static boolean enableLBSMUnlockAnvil = true;
    public static boolean enableLBSMSimpleCrafting = true;
    public static boolean enableLBSMSimpleMedicineRecipes = true;
    public static boolean enableLBSMSafeCrates = true;
    public static boolean enableLBSMSafeMEDrives = true;
    public static boolean enableLBSMIGen = true;
    public static int schrabRate = 20;

    public static void addConfig(ForgeConfigSpec.Builder builder){
        builder.comment("""
                Will most likely break standard progression!
                However, the game gets generally easier and more enjoyable for casual players.
                Progression-braking recipes are usually not too severe, so the mode is generally server-friendly!""");
        builder.push(CommonConfig.CATEGORY_LBSM);
        
        addBoolean(builder, "enableLessBullshitMode", false, "The central toggle for LBS mode. Forced OFF when 528 is enabled!");
        addBoolean(builder, "fullSchrab", true, "When enabled, this will replace schraranium with full schrabidium ingots in the transmutator's output");
        addBoolean(builder, "shortDecay", true, "When enabled, this will highly accelerate the speed at which nuclear waste disposal drums decay their contents. 60x faster than 528 mode and 5-12x faster than on normal mode.");
        addBoolean(builder, "recipeSimpleArmor", true, "When enabled, simplifies the recipe for armor sets like starmetal or schrabidium.");
        addBoolean(builder,"recipeSimpleTool", true, "When enabled, simplifies the recipe for tool sets like starmetal or scrhabidium" );
        addBoolean(builder, "recipeSimpleAlloy", true, "When enabled, adds some blast furnace recipes to make certain things cheaper");
        addBoolean(builder, "recipeSimpleChemistry", true, "When enabled, simplifies some chemical plant recipes");
        addBoolean(builder, "recipeSimpleCentrifuge", true, "When enabled, enhances centrifuge outputs to make rare materials more common");
        addBoolean(builder, "recipeUnlockAnvil", true, "When enabled, all anvil recipes are available at tier 1");
        addBoolean(builder, "recipeSimpleCrafting", true, "When enabled, some uncraftable or more expansive items get simple crafting recipes. Scorched uranium also becomes washable");
        addBoolean(builder, "recipeSimpleMedicine", true, "When enabled, makes some medicine recipes (like ones that require bismuth) much more affordable");
        addBoolean(builder, "safeCrates", true, "When enabled, prevents crates from becoming radioactive");
        addBoolean(builder, "safeMEDrives", true, "When enabled, prevents ME Drives and Portable Cells from becoming radioactive");
        addBoolean(builder, "iGen", true, "When enabled, restores the industrial generator to pre-nerf power");
        addInt(builder, "schrabOreRate", 20, "Changes the amount of uranium ore needed on average to create one schrabidium ore using nukes. Standard mode value is 100");

        builder.pop();
    }
    /**
     * 注意这个函数要在Config528.loadConfig之后使用。
     * */
    public static void loadConfig(final String catName, final ModConfigEvent event){
        CommentedConfig configData = event.getConfig().getConfigData();
        CommentedConfig config;
        if (configData.contains(catName) && (config = configData.get(catName)) instanceof CommentedConfig){
            enableLBSM = config.get("enableLessBullshitMode");
            enableLBSMFullSchrab = config.get("fullSchrab");
            enableLBSMShorterDecay = config.get("shortDecay");
            enableLBSMSimpleArmorRecipes = config.get("recipeSimpleArmor");
            enableLBSMSimpleToolRecipes = config.get("recipeSimpleTool");
            enableLBSMSimpleAlloy = config.get("recipeSimpleAlloy");
            enableLBSMSimpleChemsitry = config.get("recipeSimpleChemistry");
            enableLBSMSimpleCentrifuge = config.get("recipeSimpleCentrifuge");
            enableLBSMUnlockAnvil = config.get("recipeUnlockAnvil");
            enableLBSMSimpleCrafting = config.get("recipeSimpleCrafting");
            enableLBSMSimpleMedicineRecipes = config.get("recipeSimpleMedicine");
            enableLBSMSafeCrates = config.get("safeCrates");
            enableLBSMSafeMEDrives = config.get("safeMEDrives");
            enableLBSMIGen = config.get("iGen");
            schrabRate = config.get("schrabOreRate");
        }
        
        // 528模式优先级更高
        if (Config528.enable528) enableLBSM = false;
    }
}
