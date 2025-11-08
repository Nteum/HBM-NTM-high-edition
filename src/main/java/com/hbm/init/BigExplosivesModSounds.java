package com.hbm.init;

import com.hbm.compat.bigexplosives.BigExplosivesMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public final class BigExplosivesModSounds {

    public static final DeferredRegister<SoundEvent> REGISTRY =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BigExplosivesMod.MODID);

    public static final RegistryObject<SoundEvent> BIG_BOMB = register("big_bomb");
    public static final RegistryObject<SoundEvent> UNDERWATER_EXPLODE = register("underwaterexplode");
    public static final RegistryObject<SoundEvent> ATOM_BOMB_CLOSE = register("atombombclose");
    public static final RegistryObject<SoundEvent> ATOM_BOMB_FAR = register("atombombfar");
    public static final RegistryObject<SoundEvent> ATOM_BOMB_EXTREMELY_FAR = register("atombombextremelyfar");
    public static final RegistryObject<SoundEvent> BOOM = register("boom");
    public static final RegistryObject<SoundEvent> SUPER_FAR_EXPLOSION = register("superfarexplosion");

    private BigExplosivesModSounds() {
    }

    private static RegistryObject<SoundEvent> register(String name) {
        return REGISTRY.register(name,
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BigExplosivesMod.MODID, name)));
    }
}
