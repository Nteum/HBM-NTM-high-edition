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
    public static final RegistryObject<GlyphidHive> GLYPHID_HIVE = FEATURES.register("glyphid_hive", ()->new GlyphidHive(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<MeteCreator> METE_CREATOR = FEATURES.register("mete_creator", ()->new MeteCreator(MeteCreator.CreatorConfiguration.CODEC));
    public static final RegistryObject<Meteorite> METEORITE = FEATURES.register("meteorite", ()->new Meteorite(Meteorite.Configuration.CODEC));
    public static void register(IEventBus iEventBus){
        FEATURES.register(iEventBus);
    }
}
