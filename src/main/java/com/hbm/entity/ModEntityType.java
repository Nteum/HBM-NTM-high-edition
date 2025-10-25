package com.hbm.entity;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.entity.effect.EntityBlackHole;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
//import com.hbm.entity.mob.EntityGlyphid;
import com.hbm.entity.mob.EntityGlyphid;
import com.hbm.entity.weapon.grenade.*;
//import com.hbm.entity.logic.GrenadeGeneticEntity;
//import com.hbm.entity.logic.NukeExplodeEntity;
import com.hbm.entity.weapon.missile.EntityMissileAntiBallistic;
import com.hbm.entity.weapon.missile.EntityMissileTier0.*;
import com.hbm.registries.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** 模型的实体种类 */
public class ModEntityType {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, HBM.MODID);
    /**
     * 手榴弹实体
     * */
    public static final RegistryObject<EntityType<EntityGrenadeGenetic>> ENTITY_GRENADE_GENETIC
        = register("grenade_genetic",EntityType.Builder.<EntityGrenadeGenetic>of(EntityGrenadeGenetic::new, MobCategory.MISC));
    public static final RegistryObject<EntityType<EntityGrenadeStrong>> ENTITY_GRENADE_STRONG
            = register("grenade_strong",EntityType.Builder.<EntityGrenadeStrong>of(EntityGrenadeStrong::new, MobCategory.MISC));
    public static final RegistryObject<EntityType<EntityGrenadeFire>> ENTITY_GRENADE_FIRE
            = register("grenade_fire",EntityType.Builder.<EntityGrenadeFire>of(EntityGrenadeFire::new, MobCategory.MISC));
    public static final RegistryObject<EntityType<EntityGrenadeFrag>> ENTITY_GRENADE_FRAG
            = register("grenade_frag",EntityType.Builder.<EntityGrenadeFrag>of(EntityGrenadeFrag::new, MobCategory.MISC));
    public static final RegistryObject<EntityType<EntityGrenadeBlackHole>> ENTITY_GRENADE_BLACK_HOLE
            = register("grenade_black_hole",EntityType.Builder.<EntityGrenadeBlackHole>of(EntityGrenadeBlackHole::new, MobCategory.MISC));
    /** 导弹 */
    public static final RegistryObject<EntityType<EntityMissileTest>> ENTITY_MISSILE_TEST
            = register("missile_test",EntityType.Builder.<EntityMissileTest>of(EntityMissileTest::new, MobCategory.MISC));
    public static final RegistryObject<EntityType<EntityMissileAntiBallistic>> ENTITY_MISSILE_ANTI_BALLISTIC
            = register("entity_missile_anti_ballistic",EntityType.Builder.<EntityMissileAntiBallistic>of(EntityMissileAntiBallistic::new, MobCategory.MISC));

    /**
     * 用于效果的实体
     * */
    public static final RegistryObject<EntityType<EntityBlackHole>> ENTITY_BLACK_HOLE
            = register("black_hole",EntityType.Builder.<EntityBlackHole>of(EntityBlackHole::new, MobCategory.MISC));
    public static final RegistryObject<EntityType<EntityNukeTorex>> ENTITY_NUKE_TOREX
            = register("torex",EntityType.Builder.<EntityNukeTorex>of(EntityNukeTorex::new, MobCategory.MISC));
    /**
     * 炸弹实体
     * */
    public static final RegistryObject<EntityType<EntityNukeExplosionMK5>> ENTITY_NUKE_EXPLOSION_MK5
            = register("entity_nuke_explosion_mk5",EntityType.Builder.<EntityNukeExplosionMK5>of(EntityNukeExplosionMK5::new, MobCategory.MISC));

    public static final RegistryObject<EntityType<TestEntity>> TEST_ENTITY = register("test_entity",EntityType.Builder.<TestEntity>of(TestEntity::new, MobCategory.MISC));

    /**
     * 生物实体
     * */
    public static final RegistryObject<EntityType<EntityGlyphid>> GLYPHID
            = register("glyphid",EntityType.Builder.<EntityGlyphid>of(EntityGlyphid::new, MobCategory.MONSTER)
            .sized(1.75F, 1F));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String pKey, EntityType.Builder<T> pBuilder) {
        return ENTITY_TYPES.register(pKey,()->pBuilder.build(new ResourceLocation(HBM.MODID,pKey).toString()));
    }
//    // 我需要在实体注册类里面注册相应的刷怪蛋，否则数据生成时似乎无法找到对应的实体类型
//    private static <T extends Mob> RegistryObject<EntityType<T>> registerGlyphid(String pKey, EntityType.Builder<T> pBuilder) {
//        RegistryObject<EntityType<T>> registerEntity = ENTITY_TYPES.register(pKey, () -> pBuilder.build(new ResourceLocation(HBM.MODID, pKey).toString()));
//        ModItems.GLYPHID_SPAWN_EGG = ModItems.add("glyphid_spawn_egg", ()->new SpawnEggItem(registerEntity.get(), 0, 0xffffffff, new Item.Properties()), CreativeModeTabs.SPAWN_EGGS, HBMKey.SPAWN_EGG_MODEL, HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
//        return registerEntity;
//    }
}
