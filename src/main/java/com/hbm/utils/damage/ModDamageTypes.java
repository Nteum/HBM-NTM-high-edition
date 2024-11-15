package com.hbm.utils.damage;

import com.hbm.main.HBMxx;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> RADIATION = createKey("radiation");
    private static ResourceKey<DamageType> createKey(String name){
        return ResourceKey.create(Registries.DAMAGE_TYPE, HBMxx.hbm(name));
    }
    public static void bootstrap(BootstapContext<DamageType> pContext) {
        pContext.register(RADIATION, new DamageType("radiation", 0.0F));
    }
    public static void register(){ }
}
