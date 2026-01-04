package com.hbm.dim;

import com.hbm.HBM;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class HBMChunkGenerators {
    protected static final DeferredRegister<Codec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR, HBM.MODID);

    public static void register(IEventBus eventBus){
        CHUNK_GENERATORS.register(eventBus);
    }
}
