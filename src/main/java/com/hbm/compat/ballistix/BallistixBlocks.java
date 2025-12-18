package com.hbm.compat.ballistix;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BallistixBlocks {

    public static final DeferredRegister<Block> REGISTRY =
            DeferredRegister.create(ForgeRegistries.BLOCKS, BallistixCompat.MODID);

    public static final Map<BallistixExplosiveType, RegistryObject<BallistixExplosiveBlock>> EXPLOSIVES =
            new EnumMap<>(BallistixExplosiveType.class);

    static {
        for (BallistixExplosiveType type : BallistixExplosiveType.portedTypes()) {
            EXPLOSIVES.put(type, REGISTRY.register(type.id(), () -> new BallistixExplosiveBlock(type)));
        }
    }

    private BallistixBlocks() {
    }
}
