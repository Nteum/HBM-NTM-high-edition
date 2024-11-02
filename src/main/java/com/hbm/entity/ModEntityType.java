package com.hbm.entity;

import com.hbm.entity.weapon.grenade.EntityGrenadeFire;
import com.hbm.entity.weapon.grenade.EntityGrenadeFrag;
import com.hbm.main.HBMxx;
//import com.hbm.entity.logic.GrenadeGeneticEntity;
//import com.hbm.entity.logic.NukeExplodeEntity;
import com.hbm.entity.weapon.grenade.EntityGrenadeGenetic;
import com.hbm.entity.weapon.grenade.EntityGrenadeStrong;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** 模型的实体种类 */
public class ModEntityType {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, HBMxx.MODID);
//    public static final RegistryObject<EntityType<NukeExplodeEntity>> NUKE_EXPLODE_ENTITY =
//            ENTITY_TYPES.register("nuke_explode_entity",()->EntityType.Builder.<NukeExplodeEntity>of(NukeExplodeEntity::new, null)
//                    .build(new ResourceLocation(HBMxx.MODID,"nuke_explode_entity").toString()));
    public static final RegistryObject<EntityType<EntityGrenadeGenetic>> ENTITY_GRENADE_GENETIC
        = register("grenade_genetic",EntityType.Builder.<EntityGrenadeGenetic>of(EntityGrenadeGenetic::new, MobCategory.MISC));
    public static final RegistryObject<EntityType<EntityGrenadeStrong>> ENTITY_GRENADE_STRONG
            = register("grenade_strong",EntityType.Builder.<EntityGrenadeStrong>of(EntityGrenadeStrong::new, MobCategory.MISC));
    public static final RegistryObject<EntityType<EntityGrenadeFire>> ENTITY_GRENADE_FIRE
            = register("grenade_fire",EntityType.Builder.<EntityGrenadeFire>of(EntityGrenadeFire::new, MobCategory.MISC));
    public static final RegistryObject<EntityType<EntityGrenadeFrag>> ENTITY_GRENADE_FRAG
            = register("grenade_frag",EntityType.Builder.<EntityGrenadeFrag>of(EntityGrenadeFrag::new, MobCategory.MISC));
    public static final RegistryObject<EntityType<TestEntity>> TEST_ENTITY = register("test_entity",EntityType.Builder.<TestEntity>of(TestEntity::new, MobCategory.MISC));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String pKey, EntityType.Builder<T> pBuilder) {
        return ENTITY_TYPES.register(pKey,()->pBuilder.build(new ResourceLocation(HBMxx.MODID,pKey).toString()));
    }
}
