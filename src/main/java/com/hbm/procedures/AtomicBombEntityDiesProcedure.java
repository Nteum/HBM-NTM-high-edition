package net.mcreator.nuclearcraft.procedures;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.mcreator.nuclearcraft.init.BigExplosivesModEntities;
import net.mcreator.nuclearcraft.init.BigExplosivesModItems;
import net.mcreator.nuclearcraft.init.BigExplosivesModMobEffects;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/AtomicBombEntityDiesProcedure.class */
public class AtomicBombEntityDiesProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) throws IOException {
        if (entity == null) {
            return;
        }
        new File("");
        new JsonObject();
        File bigexplosives = new File(FMLPaths.GAMEDIR.get().toString() + "/config/", File.separator + "bigexplosivesconfig.json");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(bigexplosives));
            StringBuilder jsonstringbuilder = new StringBuilder();
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                } else {
                    jsonstringbuilder.append(line);
                }
            }
            bufferedReader.close();
            if (!entity.m_9236_().m_5776_() && entity.m_20194_() != null) {
                entity.m_20194_().m_129892_().m_230957_(new CommandSourceStack(CommandSource.f_80164_, entity.m_20182_(), entity.m_20155_(), entity.m_9236_() instanceof ServerLevel ? (ServerLevel) entity.m_9236_() : null, 4, entity.m_7755_().getString(), entity.m_5446_(), entity.m_9236_().m_7654_(), entity), "teleport @s ~ ~ ~ ~ ~90");
            }
            for (int index0 = 0; index0 < ((int) (200.0d * 20.0d)); index0++) {
                if (!entity.m_9236_().m_5776_() && entity.m_20194_() != null) {
                    entity.m_20194_().m_129892_().m_230957_(new CommandSourceStack(CommandSource.f_80164_, entity.m_20182_(), entity.m_20155_(), entity.m_9236_() instanceof ServerLevel ? (ServerLevel) entity.m_9236_() : null, 4, entity.m_7755_().getString(), entity.m_5446_(), entity.m_9236_().m_7654_(), entity), "fill ^ ^ ^0 ^ ^ ^" + 4626322717216342016 + " air");
                }
                if (!entity.m_9236_().m_5776_() && entity.m_20194_() != null) {
                    entity.m_20194_().m_129892_().m_230957_(new CommandSourceStack(CommandSource.f_80164_, entity.m_20182_(), entity.m_20155_(), entity.m_9236_() instanceof ServerLevel ? (ServerLevel) entity.m_9236_() : null, 4, entity.m_7755_().getString(), entity.m_5446_(), entity.m_9236_().m_7654_(), entity), "fill ^ ^ ^0 ^ ^ ^" + (20.0d * (-1.0d)) + " air");
                }
                if (!entity.m_9236_().m_5776_() && entity.m_20194_() != null) {
                    entity.m_20194_().m_129892_().m_230957_(new CommandSourceStack(CommandSource.f_80164_, entity.m_20182_(), entity.m_20155_(), entity.m_9236_() instanceof ServerLevel ? (ServerLevel) entity.m_9236_() : null, 4, entity.m_7755_().getString(), entity.m_5446_(), entity.m_9236_().m_7654_(), entity), "teleport @s ~ ~ ~ ~4 ~-0.1");
                }
            }
            BigExplosivesMod.queueServerWork(1, () -> {
                Vec3 _center = new Vec3(x, y, z);
                List<Entity> _entfound = world.m_6443_(Entity.class, new AABB(_center, _center).m_82400_(25.0d), e -> {
                    return true;
                }).stream().sorted(Comparator.comparingDouble(_entcnd -> {
                    return _entcnd.m_20238_(_center);
                })).toList();
                for (Entity entity2 : _entfound) {
                    entity2.m_6469_(new DamageSource(world.m_9598_().m_175515_(Registries.f_268580_).m_246971_(ResourceKey.m_135785_(Registries.f_268580_, new ResourceLocation("big_explosives:atomized")))), 120.0f);
                    if (entity2 instanceof LivingEntity) {
                        LivingEntity _entity = (LivingEntity) entity2;
                        if (!_entity.m_9236_().m_5776_()) {
                            _entity.m_7292_(new MobEffectInstance((MobEffect) BigExplosivesModMobEffects.WHITE_OUT_EFFECT.get(), 220, 0, false, false));
                        }
                    }
                    if (entity2 instanceof LivingEntity) {
                        LivingEntity _entity2 = (LivingEntity) entity2;
                        if (!_entity2.m_9236_().m_5776_()) {
                            _entity2.m_7292_(new MobEffectInstance((MobEffect) BigExplosivesModMobEffects.SCREEN_SHAKE_EFFECT.get(), 380, 0, false, false));
                        }
                    }
                    if (entity2 instanceof LivingEntity) {
                        LivingEntity _entity3 = (LivingEntity) entity2;
                        if (!_entity3.m_9236_().m_5776_()) {
                            _entity3.m_7292_(new MobEffectInstance((MobEffect) BigExplosivesModMobEffects.RADIATION_POISONING.get(), 8000, 0, false, true));
                        }
                    }
                }
            });
            BigExplosivesMod.queueServerWork(1, () -> {
                Vec3 _center = new Vec3(x, y, z);
                List<Entity> _entfound = world.m_6443_(Entity.class, new AABB(_center, _center).m_82400_(35.0d), e -> {
                    return true;
                }).stream().sorted(Comparator.comparingDouble(_entcnd -> {
                    return _entcnd.m_20238_(_center);
                })).toList();
                for (Entity entity2 : _entfound) {
                    entity2.m_6469_(new DamageSource(world.m_9598_().m_175515_(Registries.f_268580_).m_246971_(ResourceKey.m_135785_(Registries.f_268580_, new ResourceLocation("big_explosives:atomized")))), 60.0f);
                    if (entity2 instanceof LivingEntity) {
                        LivingEntity _entity = (LivingEntity) entity2;
                        if (!_entity.m_9236_().m_5776_()) {
                            _entity.m_7292_(new MobEffectInstance((MobEffect) BigExplosivesModMobEffects.SCREEN_SHAKE_EFFECT.get(), 380, 0, false, false));
                        }
                    }
                    if (entity2 instanceof LivingEntity) {
                        LivingEntity _entity2 = (LivingEntity) entity2;
                        if (!_entity2.m_9236_().m_5776_()) {
                            _entity2.m_7292_(new MobEffectInstance((MobEffect) BigExplosivesModMobEffects.WHITE_OUT_EFFECT.get(), 150, 0, false, false));
                        }
                    }
                    if (entity2 instanceof LivingEntity) {
                        LivingEntity _entity3 = (LivingEntity) entity2;
                        if (!_entity3.m_9236_().m_5776_()) {
                            _entity3.m_7292_(new MobEffectInstance((MobEffect) BigExplosivesModMobEffects.RADIATION_POISONING.get(), 6000, 0, false, true));
                        }
                    }
                }
                BigExplosivesMod.queueServerWork(1, () -> {
                    Vec3 _center2 = new Vec3(x, y, z);
                    List<Entity> _entfound2 = world.m_6443_(Entity.class, new AABB(_center2, _center2).m_82400_(45.0d), e2 -> {
                        return true;
                    }).stream().sorted(Comparator.comparingDouble(_entcnd2 -> {
                        return _entcnd2.m_20238_(_center2);
                    })).toList();
                    for (Entity entity3 : _entfound2) {
                        entity3.m_6469_(new DamageSource(world.m_9598_().m_175515_(Registries.f_268580_).m_246971_(ResourceKey.m_135785_(Registries.f_268580_, new ResourceLocation("big_explosives:atomized")))), 30.0f);
                        if (entity3 instanceof LivingEntity) {
                            LivingEntity _entity4 = (LivingEntity) entity3;
                            if (!_entity4.m_9236_().m_5776_()) {
                                _entity4.m_7292_(new MobEffectInstance((MobEffect) BigExplosivesModMobEffects.SCREEN_SHAKE_EFFECT.get(), 380, 0, false, false));
                            }
                        }
                        if (entity3 instanceof LivingEntity) {
                            LivingEntity _entity5 = (LivingEntity) entity3;
                            if (!_entity5.m_9236_().m_5776_()) {
                                _entity5.m_7292_(new MobEffectInstance((MobEffect) BigExplosivesModMobEffects.WHITE_OUT_EFFECT.get(), 150, 0, false, false));
                            }
                        }
                        if (entity3 instanceof LivingEntity) {
                            LivingEntity _entity6 = (LivingEntity) entity3;
                            if (!_entity6.m_9236_().m_5776_()) {
                                _entity6.m_7292_(new MobEffectInstance((MobEffect) BigExplosivesModMobEffects.RADIATION_POISONING.get(), 4000, 0, false, true));
                            }
                        }
                    }
                });
                BigExplosivesMod.queueServerWork(1, () -> {
                    Vec3 _center2 = new Vec3(x, y, z);
                    List<Entity> _entfound2 = world.m_6443_(Entity.class, new AABB(_center2, _center2).m_82400_(90.0d), e2 -> {
                        return true;
                    }).stream().sorted(Comparator.comparingDouble(_entcnd2 -> {
                        return _entcnd2.m_20238_(_center2);
                    })).toList();
                    for (Entity entity3 : _entfound2) {
                        entity3.m_6469_(new DamageSource(world.m_9598_().m_175515_(Registries.f_268580_).m_246971_(ResourceKey.m_135785_(Registries.f_268580_, new ResourceLocation("big_explosives:atomized")))), 15.0f);
                        if (entity3 instanceof LivingEntity) {
                            LivingEntity _entity4 = (LivingEntity) entity3;
                            if (!_entity4.m_9236_().m_5776_()) {
                                _entity4.m_7292_(new MobEffectInstance((MobEffect) BigExplosivesModMobEffects.SCREEN_SHAKE_EFFECT.get(), 380, 0, false, false));
                            }
                        }
                        if (entity3 instanceof LivingEntity) {
                            LivingEntity _entity5 = (LivingEntity) entity3;
                            if (!_entity5.m_9236_().m_5776_()) {
                                _entity5.m_7292_(new MobEffectInstance((MobEffect) BigExplosivesModMobEffects.WHITE_OUT_EFFECT.get(), 100, 0, false, false));
                            }
                        }
                        if (entity3 instanceof LivingEntity) {
                            LivingEntity _entity6 = (LivingEntity) entity3;
                            if (!_entity6.m_9236_().m_5776_()) {
                                _entity6.m_7292_(new MobEffectInstance((MobEffect) BigExplosivesModMobEffects.RADIATION_POISONING.get(), 2000, 0, false, true));
                            }
                        }
                    }
                });
            });
            BigExplosivesMod.queueServerWork(1, () -> {
                for (int index1 = 0; index1 < 300; index1++) {
                    BigExplosivesMod.queueServerWork(1, () -> {
                        int iM_6527_;
                        if (world instanceof ServerLevel) {
                            ServerLevel projectileLevel = (ServerLevel) world;
                            ?? r0 = new Object() { // from class: net.mcreator.nuclearcraft.procedures.AtomicBombEntityDiesProcedure.1
                                public Projectile getFireball(Level level, Entity shooter, double ax, double ay, double az) {
                                    LargeFireball largeFireball = new LargeFireball(EntityType.f_20463_, level);
                                    largeFireball.m_5602_(shooter);
                                    ((AbstractHurtingProjectile) largeFireball).f_36813_ = ax;
                                    ((AbstractHurtingProjectile) largeFireball).f_36814_ = ay;
                                    ((AbstractHurtingProjectile) largeFireball).f_36815_ = az;
                                    return largeFireball;
                                }
                            };
                            if (entity instanceof ServerPlayer) {
                                ServerPlayer _player = (ServerPlayer) entity;
                                if (!_player.m_9236_().m_5776_()) {
                                    iM_6527_ = (!_player.m_8963_().equals(_player.m_9236_().m_46472_()) || _player.m_8961_() == null) ? _player.m_9236_().m_6106_().m_6527_() : _player.m_8961_().m_123342_();
                                } else {
                                    iM_6527_ = 0;
                                }
                            }
                            Projectile _entityToSpawn = r0.getFireball(projectileLevel, entity, 0.0d, iM_6527_ - 0.1d, 0.0d);
                            _entityToSpawn.m_6034_(x + Mth.m_216263_(RandomSource.m_216327_(), -18.0d, 18.0d), y, z + Mth.m_216263_(RandomSource.m_216327_(), -18.0d, 18.0d));
                            _entityToSpawn.m_6686_(0.0d, 0.0d, 0.0d, 1.0f, 0.0f);
                            projectileLevel.m_7967_(_entityToSpawn);
                        }
                    });
                }
            });
            BigExplosivesMod.queueServerWork(3, () -> {
                if (world instanceof ServerLevel) {
                    ServerLevel _level = (ServerLevel) world;
                    Entity entityToSpawn = ((EntityType) BigExplosivesModEntities.ATOMIC_BOMB_EXPLOSION.get()).m_262496_(_level, BlockPos.m_274561_(x, y - 3.0d, z), MobSpawnType.MOB_SUMMONED);
                    if (entityToSpawn != null) {
                        entityToSpawn.m_20334_(0.0d, 0.0d, 0.0d);
                    }
                }
            });
            BigExplosivesMod.queueServerWork(3, () -> {
                if (world instanceof Level) {
                    Level _level = (Level) world;
                    if (!_level.m_5776_()) {
                        _level.m_5594_((Player) null, BlockPos.m_274561_(x, y, z), (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:boom")), SoundSource.NEUTRAL, 100.0f, 1.0f);
                    } else {
                        _level.m_7785_(x, y, z, (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:boom")), SoundSource.NEUTRAL, 100.0f, 1.0f, false);
                    }
                }
            });
            BigExplosivesMod.queueServerWork(3, () -> {
                if (world instanceof Level) {
                    Level _level = (Level) world;
                    if (!_level.m_5776_()) {
                        _level.m_5594_((Player) null, BlockPos.m_274561_(x, y, z), (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:atombombclose")), SoundSource.NEUTRAL, 200.0f, 1.0f);
                    } else {
                        _level.m_7785_(x, y, z, (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:atombombclose")), SoundSource.NEUTRAL, 200.0f, 1.0f, false);
                    }
                }
            });
            BigExplosivesMod.queueServerWork(3, () -> {
                if (world instanceof Level) {
                    Level _level = (Level) world;
                    if (!_level.m_5776_()) {
                        _level.m_5594_((Player) null, BlockPos.m_274561_(x, y, z), (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:atombombfar")), SoundSource.NEUTRAL, 400.0f, 1.0f);
                    } else {
                        _level.m_7785_(x, y, z, (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:atombombfar")), SoundSource.NEUTRAL, 400.0f, 1.0f, false);
                    }
                }
            });
            BigExplosivesMod.queueServerWork(3, () -> {
                if (world instanceof Level) {
                    Level _level = (Level) world;
                    if (!_level.m_5776_()) {
                        _level.m_5594_((Player) null, BlockPos.m_274561_(x, y, z), (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:atombombextremelyfar")), SoundSource.NEUTRAL, 600.0f, 1.0f);
                    } else {
                        _level.m_7785_(x, y, z, (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:atombombextremelyfar")), SoundSource.NEUTRAL, 600.0f, 1.0f, false);
                    }
                }
            });
            BigExplosivesMod.queueServerWork(3, () -> {
                if (world instanceof Level) {
                    Level _level = (Level) world;
                    if (!_level.m_5776_()) {
                        _level.m_5594_((Player) null, BlockPos.m_274561_(x, y, z), (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:superfarexplosion")), SoundSource.NEUTRAL, 1000.0f, 1.0f);
                    } else {
                        _level.m_7785_(x, y, z, (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:superfarexplosion")), SoundSource.NEUTRAL, 1000.0f, 1.0f, false);
                    }
                }
            });
            if (entity instanceof Player) {
                Player _player = (Player) entity;
                _player.m_36335_().m_41524_((Item) BigExplosivesModItems.TEST.get(), 600);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
