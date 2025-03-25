package com.hbm.world.feature;

import com.hbm.HBM;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, HBM.MODID);

    public static final RegistryObject<BedrockOreFeature> BEDROCK_ORE = FEATURES.register("bedrock_ore", ()->new BedrockOreFeature(NoneFeatureConfiguration.CODEC));

    public static void register(IEventBus iEventBus){
        FEATURES.register(iEventBus);
    }
}
