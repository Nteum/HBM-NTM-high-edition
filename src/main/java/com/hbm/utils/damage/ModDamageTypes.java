package com.hbm.utils.damage;

import com.hbm.HBM;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> RADIATION = createKey("radiation");
    private static ResourceKey<DamageType> createKey(String name){
        return ResourceKey.create(Registries.DAMAGE_TYPE, HBM.rl(name));
    }
    public static void bootstrap(BootstapContext<DamageType> pContext) {
        pContext.register(RADIATION, new DamageType("radiation", 0.0F));
    }
    public static void register(){ }
}
