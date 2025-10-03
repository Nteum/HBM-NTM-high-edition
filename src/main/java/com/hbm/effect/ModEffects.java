package com.hbm.effect;

import com.hbm.HBM;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 游戏效果
 * */
public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, HBM.MODID);
    public static final RegistryObject<MobEffect> RADIATION = EFFECTS.register("radiation", () -> new HBMMobEffect(MobEffectCategory.HARMFUL, 0x84C128));

    public static void register(IEventBus bus){
        EFFECTS.register(bus);
    }
}
