package com.hbm.datagen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class FluidTagsGen extends FluidTagsProvider {
    private static Map<TagKey<Fluid>, Supplier<Fluid>> READY_TO_REGISTER = new HashMap<>();
    public FluidTagsGen(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        for (Map.Entry<TagKey<Fluid>, Supplier<Fluid>> entry : READY_TO_REGISTER.entrySet()) {
            this.tag(entry.getKey()).add(entry.getValue().get());
        }
    }

    public static void register(TagKey<Fluid> key, Supplier<Fluid> fluid){
        READY_TO_REGISTER.put(key, fluid);
    }
}
