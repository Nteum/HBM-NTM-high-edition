package com.hbm.entity;

import com.hbm.HBMxx;
//import com.hbm.entity.logic.GrenadeGeneticEntity;
//import com.hbm.entity.logic.NukeExplodeEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** 模型的实体种类 */
public class ModEntityType {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, HBMxx.MODID);
//    public static final RegistryObject<EntityType<NukeExplodeEntity>> NUKE_EXPLODE_ENTITY =
//            ENTITY_TYPES.register("nuke_explode_entity",()->EntityType.Builder.<NukeExplodeEntity>of(NukeExplodeEntity::new, null)
//                    .build(new ResourceLocation(HBMxx.MODID,"nuke_explode_entity").toString()));
//    public static final RegistryObject<EntityType<GrenadeGeneticEntity>> GRENADE_GENETIC_ENTITY =
//            ENTITY_TYPES.register("grenade_genetic_entity",()->EntityType.Builder.<GrenadeGeneticEntity>of(GrenadeGeneticEntity::new, null)
//                    .build(new ResourceLocation(HBMxx.MODID,"grenade_genetic_entity").toString()));
    public static final RegistryObject<EntityType<TestEntity>> TEST_ENTITY =
            ENTITY_TYPES.register("test_entity",()->EntityType.Builder.<TestEntity>of(TestEntity::new, null)
                    .build(new ResourceLocation(HBMxx.MODID,"test_entity").toString()));

}
