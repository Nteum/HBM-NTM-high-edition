package com.hbm.entity;

import com.hbm.HBM;
import com.hbm.entity.effect.EntityBlackHole;
import com.hbm.entity.effect.EntityMeteor;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.entity.mob.EntityGlyphid;
import com.hbm.entity.mob.EntityGlyphidScout;
import com.hbm.entity.projectile.EntityRubble;
import com.hbm.entity.weapon.projectile.EntityGunBullet;
import com.hbm.entity.weapon.grenade.*;
import com.hbm.entity.weapon.missile.EntityMissileAntiBallistic;
import com.hbm.entity.weapon.missile.EntityMissileTier0.EntityMissileTest;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

//import com.hbm.entity.mob.EntityGlyphid;
//import com.hbm.entity.logic.GrenadeGeneticEntity;
//import com.hbm.entity.logic.NukeExplodeEntity;
;

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
    public static final RegistryObject<EntityType<EntityGrenadeLegacy>> ENTITY_GRENADE_LEGACY
            = register("grenade_legacy", EntityType.Builder.<EntityGrenadeLegacy>of(EntityGrenadeLegacy::new, MobCategory.MISC));
    public static final RegistryObject<EntityType<EntityGunBullet>> ENTITY_GUN_BULLET
            = register("gun_bullet", EntityType.Builder.<EntityGunBullet>of(EntityGunBullet::new, MobCategory.MISC).sized(0.125F, 0.125F).clientTrackingRange(4).updateInterval(1));
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
            = register("torex",EntityType.Builder.<EntityNukeTorex>of(EntityNukeTorex::new, MobCategory.MISC)
            .noSave().fireImmune().sized(20F, 40F).clientTrackingRange(64).updateInterval(Integer.MAX_VALUE).setShouldReceiveVelocityUpdates(false));
    public static final RegistryObject<EntityType<EntityMeteor>> ENTITY_METEOR
            = register("meteor",EntityType.Builder.<EntityMeteor>of(EntityMeteor::new, MobCategory.MISC).sized(4f, 4f).fireImmune());
    /**
     * 炸弹实体
     * */
    public static final RegistryObject<EntityType<EntityNukeExplosionMK5>> ENTITY_NUKE_EXPLOSION_MK5
            = register("entity_nuke_explosion_mk5",EntityType.Builder.<EntityNukeExplosionMK5>of(EntityNukeExplosionMK5::new, MobCategory.MISC));

    public static final RegistryObject<EntityType<TestEntity>> TEST_ENTITY = register("test_entity",EntityType.Builder.<TestEntity>of(TestEntity::new, MobCategory.MISC));
    public static final RegistryObject<EntityType<EntityRubble>> ENTITY_RUBBLE = register("entity_rubble",EntityType.Builder.<EntityRubble>of(EntityRubble::new, MobCategory.MISC));

    /**
     * 生物实体
     * */
    public static final RegistryObject<EntityType<EntityGlyphid>> GLYPHID
            = register("glyphid",EntityType.Builder.<EntityGlyphid>of(EntityGlyphid::new, MobCategory.MONSTER)
            .sized(1.75F, 1F));
    public static final RegistryObject<EntityType<EntityGlyphidScout>> GLYPHID_SCOUT
            = register("glyphid_scout",EntityType.Builder.<EntityGlyphidScout>of(EntityGlyphidScout::new, MobCategory.MONSTER)
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
