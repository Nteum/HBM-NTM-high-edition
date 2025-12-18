package com.hbm.compat.ballistix;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BallistixEntities {

    public static final DeferredRegister<EntityType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BallistixCompat.MODID);

    public static final RegistryObject<EntityType<BallistixPrimedExplosive>> PRIMED_EXPLOSIVE = register(
            "primed_explosive",
            EntityType.Builder.<BallistixPrimedExplosive>of(BallistixPrimedExplosive::new, MobCategory.MISC)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(64)
                    .setUpdateInterval(10)
                    .sized(0.98F, 0.98F));

    private BallistixEntities() {
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String id,
            EntityType.Builder<T> builder) {
        return REGISTRY.register(id, () -> builder.build(id));
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(BallistixPrimedExplosive::init);
    }
}
