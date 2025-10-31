package net.mcreator.nuclearcraft.init;

import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.mcreator.nuclearcraft.entity.AtomicBombEntity;
import net.mcreator.nuclearcraft.entity.AtomicBombExplosionEntity;
import net.mcreator.nuclearcraft.entity.BunkerBusterEntity;
import net.mcreator.nuclearcraft.entity.CakeBombEntity;
import net.mcreator.nuclearcraft.entity.FiveBombEntity;
import net.mcreator.nuclearcraft.entity.FiveHundredKgExplosionEntity;
import net.mcreator.nuclearcraft.entity.FourthOfJullyEntity;
import net.mcreator.nuclearcraft.entity.NapalmBarraageEntity;
import net.mcreator.nuclearcraft.entity.NapalmBombEntity;
import net.mcreator.nuclearcraft.entity.TenKgBombAirstrikesEntity;
import net.mcreator.nuclearcraft.entity.TwoFiddyEntity;
import net.mcreator.nuclearcraft.entity.TwoHundredFiftyKgExplosionEntity;
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
/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/init/BigExplosivesModEntities.class */
public class BigExplosivesModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BigExplosivesMod.MODID);
    public static final RegistryObject<EntityType<TwoFiddyEntity>> TWO_FIDDY = register("two_fiddy", EntityType.Builder.m_20704_(TwoFiddyEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(TwoFiddyEntity::new).m_20719_().m_20699_(0.6f, 1.8f));
    public static final RegistryObject<EntityType<TwoHundredFiftyKgExplosionEntity>> TWO_HUNDRED_FIFTY_KG_EXPLOSION = register("two_hundred_fifty_kg_explosion", EntityType.Builder.m_20704_(TwoHundredFiftyKgExplosionEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(TwoHundredFiftyKgExplosionEntity::new).m_20719_().m_20699_(1.0f, 1.0f));
    public static final RegistryObject<EntityType<TenKgBombAirstrikesEntity>> TEN_KG_BOMB_AIRSTRIKES = register("ten_kg_bomb_airstrikes", EntityType.Builder.m_20704_(TenKgBombAirstrikesEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(TenKgBombAirstrikesEntity::new).m_20699_(0.6f, 1.8f));
    public static final RegistryObject<EntityType<FiveBombEntity>> FIVE_BOMB = register("five_bomb", EntityType.Builder.m_20704_(FiveBombEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(FiveBombEntity::new).m_20699_(1.0f, 1.5f));
    public static final RegistryObject<EntityType<FiveHundredKgExplosionEntity>> FIVE_HUNDRED_KG_EXPLOSION = register("five_hundred_kg_explosion", EntityType.Builder.m_20704_(FiveHundredKgExplosionEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(FiveHundredKgExplosionEntity::new).m_20719_().m_20699_(5.0f, 5.0f));
    public static final RegistryObject<EntityType<NapalmBombEntity>> NAPALM_BOMB = register("napalm_bomb", EntityType.Builder.m_20704_(NapalmBombEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(NapalmBombEntity::new).m_20719_().m_20699_(0.6f, 1.8f));
    public static final RegistryObject<EntityType<NapalmBarraageEntity>> NAPALM_BARRAAGE = register("napalm_barraage", EntityType.Builder.m_20704_(NapalmBarraageEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(NapalmBarraageEntity::new).m_20699_(0.6f, 1.8f));
    public static final RegistryObject<EntityType<BunkerBusterEntity>> BUNKER_BUSTER = register("bunker_buster", EntityType.Builder.m_20704_(BunkerBusterEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(BunkerBusterEntity::new).m_20719_().m_20699_(0.6f, 1.8f));
    public static final RegistryObject<EntityType<AtomicBombEntity>> ATOMIC_BOMB = register("atomic_bomb", EntityType.Builder.m_20704_(AtomicBombEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(AtomicBombEntity::new).m_20699_(1.0f, 1.5f));
    public static final RegistryObject<EntityType<AtomicBombExplosionEntity>> ATOMIC_BOMB_EXPLOSION = register("atomic_bomb_explosion", EntityType.Builder.m_20704_(AtomicBombExplosionEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(AtomicBombExplosionEntity::new).m_20719_().m_20699_(60.0f, 60.0f));
    public static final RegistryObject<EntityType<CakeBombEntity>> CAKE_BOMB = register("cake_bomb", EntityType.Builder.m_20704_(CakeBombEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(CakeBombEntity::new).m_20699_(0.6f, 1.8f));
    public static final RegistryObject<EntityType<FourthOfJullyEntity>> FOURTH_OF_JULLY = register("fourth_of_jully", EntityType.Builder.m_20704_(FourthOfJullyEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(FourthOfJullyEntity::new).m_20719_().m_20699_(0.6f, 1.8f));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
        return REGISTRY.register(registryname, () -> {
            return entityTypeBuilder.m_20712_(registryname);
        });
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            TwoFiddyEntity.init();
            TwoHundredFiftyKgExplosionEntity.init();
            TenKgBombAirstrikesEntity.init();
            FiveBombEntity.init();
            FiveHundredKgExplosionEntity.init();
            NapalmBombEntity.init();
            NapalmBarraageEntity.init();
            BunkerBusterEntity.init();
            AtomicBombEntity.init();
            AtomicBombExplosionEntity.init();
            CakeBombEntity.init();
            FourthOfJullyEntity.init();
        });
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put((EntityType) TWO_FIDDY.get(), TwoFiddyEntity.createAttributes().m_22265_());
        event.put((EntityType) TWO_HUNDRED_FIFTY_KG_EXPLOSION.get(), TwoHundredFiftyKgExplosionEntity.createAttributes().m_22265_());
        event.put((EntityType) TEN_KG_BOMB_AIRSTRIKES.get(), TenKgBombAirstrikesEntity.createAttributes().m_22265_());
        event.put((EntityType) FIVE_BOMB.get(), FiveBombEntity.createAttributes().m_22265_());
        event.put((EntityType) FIVE_HUNDRED_KG_EXPLOSION.get(), FiveHundredKgExplosionEntity.createAttributes().m_22265_());
        event.put((EntityType) NAPALM_BOMB.get(), NapalmBombEntity.createAttributes().m_22265_());
        event.put((EntityType) NAPALM_BARRAAGE.get(), NapalmBarraageEntity.createAttributes().m_22265_());
        event.put((EntityType) BUNKER_BUSTER.get(), BunkerBusterEntity.createAttributes().m_22265_());
        event.put((EntityType) ATOMIC_BOMB.get(), AtomicBombEntity.createAttributes().m_22265_());
        event.put((EntityType) ATOMIC_BOMB_EXPLOSION.get(), AtomicBombExplosionEntity.createAttributes().m_22265_());
        event.put((EntityType) CAKE_BOMB.get(), CakeBombEntity.createAttributes().m_22265_());
        event.put((EntityType) FOURTH_OF_JULLY.get(), FourthOfJullyEntity.createAttributes().m_22265_());
    }
}
