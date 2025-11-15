package com.hbm.init;

import com.hbm.compat.bigexplosives.BigExplosivesMod;
import com.hbm.render.entity.AtomicBombEntity;
import com.hbm.render.entity.AtomicBombExplosionEntity;
import com.hbm.render.entity.FiveBombEntity;
import com.hbm.render.entity.FiveHundredKgExplosionEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BigExplosivesModEntities {

    public static final DeferredRegister<EntityType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BigExplosivesMod.MODID);

    public static final RegistryObject<EntityType<FiveBombEntity>> FIVE_BOMB = register(
            "five_bomb",
            EntityType.Builder.<FiveBombEntity>of(FiveBombEntity::new, MobCategory.MISC)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setCustomClientFactory(FiveBombEntity::new)
                    .sized(1.0F, 1.5F));

    public static final RegistryObject<EntityType<FiveHundredKgExplosionEntity>> FIVE_HUNDRED_KG_EXPLOSION = register(
            "five_hundred_kg_explosion",
            EntityType.Builder.<FiveHundredKgExplosionEntity>of(FiveHundredKgExplosionEntity::new, MobCategory.MISC)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setCustomClientFactory(FiveHundredKgExplosionEntity::new)
                    .sized(5.0F, 5.0F));

    public static final RegistryObject<EntityType<AtomicBombEntity>> ATOMIC_BOMB = register(
            "atomic_bomb",
            EntityType.Builder.<AtomicBombEntity>of(AtomicBombEntity::new, MobCategory.MISC)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setCustomClientFactory(AtomicBombEntity::new)
                    .sized(1.0F, 1.5F));

    public static final RegistryObject<EntityType<AtomicBombExplosionEntity>> ATOMIC_BOMB_EXPLOSION = register(
            "atomic_bomb_explosion",
            EntityType.Builder.<AtomicBombExplosionEntity>of(AtomicBombExplosionEntity::new, MobCategory.MISC)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(64)
                    .setUpdateInterval(3)
                    .setCustomClientFactory(AtomicBombExplosionEntity::new)
                    .sized(60.0F, 60.0F));

    private BigExplosivesModEntities() {
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return REGISTRY.register(name, () -> builder.build(name));
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            FiveBombEntity.init();
            FiveHundredKgExplosionEntity.init();
            AtomicBombEntity.init();
            AtomicBombExplosionEntity.init();
        });
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(FIVE_BOMB.get(), FiveBombEntity.createAttributes().build());
        event.put(FIVE_HUNDRED_KG_EXPLOSION.get(), FiveHundredKgExplosionEntity.createAttributes().build());
        event.put(ATOMIC_BOMB.get(), AtomicBombEntity.createAttributes().build());
        event.put(ATOMIC_BOMB_EXPLOSION.get(), AtomicBombExplosionEntity.createAttributes().build());
    }
}
