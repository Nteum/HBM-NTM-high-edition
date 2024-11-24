package com.hbm.registries;

import com.hbm.main.HBMxx;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, HBMxx.MODID);
    public static RegistryObject<MobEffect> register(String name, Supplier<MobEffect> effect) {
        return EFFECTS.register(name,effect);
    }
}