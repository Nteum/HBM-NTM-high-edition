package com.hbm.registries;

import com.hbm.main.HBMxx;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, HBMxx.MODID);
    private static RegistryObject<Potion> register(String pKey, Potion pPotion) {
        return POTIONS.register(pKey, ()->pPotion);
    }
}
