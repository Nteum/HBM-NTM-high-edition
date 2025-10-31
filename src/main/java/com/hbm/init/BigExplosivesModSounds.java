package net.mcreator.nuclearcraft.init;

import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/init/BigExplosivesModSounds.class */
public class BigExplosivesModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BigExplosivesMod.MODID);
    public static final RegistryObject<SoundEvent> DESIGNATORBEEP = REGISTRY.register("designatorbeep", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "designatorbeep"));
    });
    public static final RegistryObject<SoundEvent> BIG_BOMB = REGISTRY.register("big_bomb", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "big_bomb"));
    });
    public static final RegistryObject<SoundEvent> UNDERWATEREXPLODE = REGISTRY.register("underwaterexplode", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "underwaterexplode"));
    });
    public static final RegistryObject<SoundEvent> MEDIUM_BOMB = REGISTRY.register("medium_bomb", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "medium_bomb"));
    });
    public static final RegistryObject<SoundEvent> SMALL_BOMB = REGISTRY.register("small_bomb", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "small_bomb"));
    });
    public static final RegistryObject<SoundEvent> SMALLER_BOMB = REGISTRY.register("smaller_bomb", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "smaller_bomb"));
    });
    public static final RegistryObject<SoundEvent> BUNKERBUSTERDRILLSOUND = REGISTRY.register("bunkerbusterdrillsound", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "bunkerbusterdrillsound"));
    });
    public static final RegistryObject<SoundEvent> ATOMBOMBCLOSE = REGISTRY.register("atombombclose", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "atombombclose"));
    });
    public static final RegistryObject<SoundEvent> ATOMBOMBFAR = REGISTRY.register("atombombfar", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "atombombfar"));
    });
    public static final RegistryObject<SoundEvent> ATOMBOMBEXTREMELYFAR = REGISTRY.register("atombombextremelyfar", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "atombombextremelyfar"));
    });
    public static final RegistryObject<SoundEvent> EARRINGING = REGISTRY.register("earringing", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "earringing"));
    });
    public static final RegistryObject<SoundEvent> EAR_RINGINGSHORT = REGISTRY.register("ear-ringingshort", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "ear-ringingshort"));
    });
    public static final RegistryObject<SoundEvent> EARSHOT = REGISTRY.register("earshot", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "earshot"));
    });
    public static final RegistryObject<SoundEvent> BOOM = REGISTRY.register("boom", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "boom"));
    });
    public static final RegistryObject<SoundEvent> SUPERFAREXPLOSION = REGISTRY.register("superfarexplosion", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "superfarexplosion"));
    });
    public static final RegistryObject<SoundEvent> EATLIBERTY = REGISTRY.register("eatliberty", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "eatliberty"));
    });
    public static final RegistryObject<SoundEvent> HERECOMESTHECALVARY = REGISTRY.register("herecomesthecalvary", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "herecomesthecalvary"));
    });
    public static final RegistryObject<SoundEvent> COMMININHOY = REGISTRY.register("commininhoy", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "commininhoy"));
    });
    public static final RegistryObject<SoundEvent> DELIVERINGPAYLOAD = REGISTRY.register("deliveringpayload", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "deliveringpayload"));
    });
    public static final RegistryObject<SoundEvent> DEMOCRACYSONITSWAY = REGISTRY.register("democracysonitsway", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "democracysonitsway"));
    });
    public static final RegistryObject<SoundEvent> PARTYHORN = REGISTRY.register("partyhorn", () -> {
        return SoundEvent.m_262824_(new ResourceLocation(BigExplosivesMod.MODID, "partyhorn"));
    });
}
