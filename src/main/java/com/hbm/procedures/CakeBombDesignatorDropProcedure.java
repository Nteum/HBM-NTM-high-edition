package net.mcreator.nuclearcraft.procedures;

import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.mcreator.nuclearcraft.init.BigExplosivesModEntities;
import net.mcreator.nuclearcraft.init.BigExplosivesModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/CakeBombDesignatorDropProcedure.class */
public class CakeBombDesignatorDropProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
        if (entity == null) {
            return;
        }
        if (!world.m_8055_(new BlockPos(entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(0.0d)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123341_(), entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(0.0d)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123342_(), entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(0.0d)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123343_())).m_60815_()) {
            double Scaling2 = 0.0d + 250.0d;
            if (world instanceof ServerLevel) {
                Entity entityToSpawn = ((EntityType) BigExplosivesModEntities.CAKE_BOMB.get()).m_262496_((ServerLevel) world, BlockPos.m_274561_(entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(Scaling2)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123341_(), entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(Scaling2)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123342_() + 400, entity.m_9236_().m_45547_(new ClipContext(entity.m_20299_(1.0f), entity.m_20299_(1.0f).m_82549_(entity.m_20252_(1.0f).m_82490_(Scaling2)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).m_82425_().m_123343_()), MobSpawnType.MOB_SUMMONED);
                if (entityToSpawn != null) {
                    entityToSpawn.m_20334_(0.0d, 0.0d, 0.0d);
                }
            }
        }
        BigExplosivesMod.queueServerWork(1, () -> {
            if (itemstack.m_220157_(1, RandomSource.m_216327_(), (ServerPlayer) null)) {
                itemstack.m_41774_(1);
                itemstack.m_41721_(0);
            }
            if (entity instanceof Player) {
                Player _player = (Player) entity;
                _player.m_36335_().m_41524_((Item) BigExplosivesModItems.FIVE_HUNDRED_KG_BOMB_DESIGNATOR.get(), 300);
            }
        });
        if (world instanceof Level) {
            Level _level = (Level) world;
            if (!_level.m_5776_()) {
                _level.m_5594_((Player) null, BlockPos.m_274561_(x, y, z), (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:designatorbeep")), SoundSource.NEUTRAL, 1.0f, 1.0f);
            } else {
                _level.m_7785_(x, y, z, (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:designatorbeep")), SoundSource.NEUTRAL, 1.0f, 1.0f, false);
            }
        }
    }
}
