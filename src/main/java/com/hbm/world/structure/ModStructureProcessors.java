package com.hbm.world.structure;

import com.hbm.HBM;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModStructureProcessors {

    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSORS =
            DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, HBM.MODID);

    public static final RegistryObject<StructureProcessorType<StructureFoundationProcessor>> FOUNDATION_PROCESSOR =
            STRUCTURE_PROCESSORS.register("foundation_processor", () -> () -> StructureFoundationProcessor.CODEC);

    private ModStructureProcessors() {
    }
}
