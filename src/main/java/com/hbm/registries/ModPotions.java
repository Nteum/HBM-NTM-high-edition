package com.hbm.registries;

import com.hbm.HBM;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, HBM.MODID);
    private static RegistryObject<Potion> register(String pKey, Potion pPotion) {
        return POTIONS.register(pKey, ()->pPotion);
    }
}
