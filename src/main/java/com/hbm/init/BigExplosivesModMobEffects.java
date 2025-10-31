package net.mcreator.nuclearcraft.init;

import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.mcreator.nuclearcraft.potion.RadiationImmunityMobEffect;
import net.mcreator.nuclearcraft.potion.RadiationPoisoningMobEffect;
import net.mcreator.nuclearcraft.potion.ScreenShakeEffectMobEffect;
import net.mcreator.nuclearcraft.potion.WhiteOutEffectMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/init/BigExplosivesModMobEffects.class */
public class BigExplosivesModMobEffects {
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, BigExplosivesMod.MODID);
    public static final RegistryObject<MobEffect> WHITE_OUT_EFFECT = REGISTRY.register("white_out_effect", () -> {
        return new WhiteOutEffectMobEffect();
    });
    public static final RegistryObject<MobEffect> SCREEN_SHAKE_EFFECT = REGISTRY.register("screen_shake_effect", () -> {
        return new ScreenShakeEffectMobEffect();
    });
    public static final RegistryObject<MobEffect> RADIATION_POISONING = REGISTRY.register("radiation_poisoning", () -> {
        return new RadiationPoisoningMobEffect();
    });
    public static final RegistryObject<MobEffect> RADIATION_IMMUNITY = REGISTRY.register("radiation_immunity", () -> {
        return new RadiationImmunityMobEffect();
    });
}
