package com.hbm.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import static com.hbm.config.CommonConfig.addBoolean;
import static com.hbm.config.CommonConfig.addInt;

public class Config528 {
    public static boolean enable528 = false;
    public static boolean enable528ReasimBoilers = true;
    public static boolean enable528ColtanDeposit = true;
    public static boolean enable528ColtanSpawn = false;
    public static boolean enable528BedrockDeposit = true;
    public static boolean enable528BedrockSpawn = false;
    public static boolean enable528BosniaSimulator = true;
    public static boolean enable528BedrockReplacement = true;
    public static boolean enable528NetherBurn = true;
    public static int coltanRate = 2;
    public static int bedrockRate = 50;

    public static void addConfig(ForgeConfigSpec.Builder builder){
        builder.comment("""
                CAUTION
                528 Mode: Please proceed with caution!
                528-Modus: Lassen Sie Vorsicht walten!
                способ-528: действовать с осторожностью!""");
        builder.push(CommonConfig.CATEGORY_528);

        addBoolean(builder, "enable528", false, "The central toggle for 528 mode.");
        addBoolean(builder, "forceReasimBoilers", true, "Keeps the RBMK dial for ReaSim boilers on, preventing use of non-ReaSim boiler columns and forcing the use of steam in-/outlets");
        addBoolean(builder, "enableColtanDepsoit", true, "Enables the coltan deposit. A large amount of coltan will spawn around a single random location in the world.");
        addBoolean(builder, "enableColtanSpawning", false, "Enables coltan ore as a random spawn in the world. Unlike the deposit option, coltan will not just spawn in one central location.");
        addBoolean(builder, "enableBedrockDepsoit", true, "Enables bedrock coltan ores in the coltan deposit. These ores can be drilled to extract infinite coltan, albeit slowly.");
        addBoolean(builder, "enableBedrockSpawning", false, "Enables the bedrock coltan ores as a rare spawn. These will be rarely found anywhere in the world.");
        addBoolean(builder, "enableBosniaSimulator", true, "Enables anti tank mines spawning all over the world.");
        addBoolean(builder, "enable528BedrockReplacement", true, "Replaces certain bedrock ores with ones that require additional processing.");
        addBoolean(builder, "enable528NetherBurn", true, "Whether players burn in the nether");
        addInt(builder, "oreColtanFrequency", 2, "Determines how many coltan ore veins are to be expected in a chunk. These values do not affect the frequency in deposits, and only apply if random coltan spanwing is enabled.");
        addInt(builder, "bedrockColtanFrequency", 50, "Determines how often (1 in X) bedrock coltan ores spawn. Applies for both the bedrock ores in the coltan deposit (if applicable) and the random bedrock ores (if applicable)");
        
        builder.pop();
    }

    public static void loadConfig(final ModConfigEvent event){
        
    }
}
