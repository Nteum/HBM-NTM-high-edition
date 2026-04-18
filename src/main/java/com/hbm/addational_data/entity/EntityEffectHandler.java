package com.hbm.addational_data.entity;

import com.hbm.addational_data.AdditionalDataManager;
import com.hbm.addational_data.DataEntry;
import com.hbm.addational_data.chunk.RadiationManager;
import com.hbm.addational_data.entity.living.ContaminationEffectLists;
import com.hbm.addational_data.entity.living.ContaminationEffectLists.ContaminationEffect;
import com.hbm.config.RadiationConfig;
import com.hbm.effect.ModEffects;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toclient.S2CParticlePacket;
import com.hbm.registries.ModSounds;
import com.hbm.utils.ContaminationUtil;
import com.hbm.utils.ContaminationUtil.ContaminationType;
import com.hbm.utils.ContaminationUtil.HazardType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.PacketDistributor.TargetPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class EntityEffectHandler {
    private static final float RADIATION_DECAY_FLOOR = 0.00001F;
    private static final float RADIATION_DECAY_RATIO = 0.001F;

    public static void onUpdate(LivingEntity entity) {
        if (!entity.isAlive() || entity.level().isClientSide || (entity instanceof Player player && player.isCreative()))
            return;
        Optional<IEntityAdditionalData> optional = AdditionalDataManager.getAdditionalData(entity);
        if (optional.isEmpty()) return;
        IEntityAdditionalData entityData = optional.get();

        Level level = entity.level();
        handleContamination(entity);
        handleEnvRadiation(entity, entityData);
        decayRadiation(entity, entityData);
        handleRadiationEffect(entity, entityData);
    }
    /**
     * 物品、方块等给玩家造成的辐射值
     * */
    private static void handleContamination(LivingEntity entity) {
        ContaminationEffectLists effectLists = AdditionalDataManager.getEntityData(entity, DataEntry.CONTAMINATION_EFFECTS).map(o -> (ContaminationEffectLists) o).orElse(new ContaminationEffectLists());
        List<ContaminationEffect> rem = new ArrayList();

        for(ContaminationEffect con : effectLists.getEffects()) {
            ContaminationUtil.contaminate(entity, HazardType.RADIATION, con.ignoreArmor ? ContaminationType.RAD_BYPASS : ContaminationType.CREATIVE, con.getRad());
            con.time--;
            if(con.time <= 0) rem.add(con);
        }

        effectLists.getEffects().removeAll(rem);
    }
    /**
     * 更新环境辐射造成的玩家辐射值
     */
    public static void handleEnvRadiation(LivingEntity entity, IEntityAdditionalData entityData) {
        if (ContaminationUtil.isRadImmune(entity)) return;

        Level level = entity.level();
        float rad = RadiationManager.getRadiation(level, entity.blockPosition());
        if (level.dimension().equals(Level.NETHER) && rad <= RadiationConfig.hellRad)
            rad = (float) RadiationConfig.hellRad;
        AdditionalDataManager.setEntityData(entity, DataEntry.RADIATION_BUF, rad);
        // 环境对玩家的污染
        if (rad > 0) ContaminationUtil.contaminate(entity, HazardType.RADIATION, ContaminationType.CREATIVE, rad / 20f);

        if (entity instanceof Player player && player.isCreative()) return;

        Random rand = new Random(entity.getId());

        int r600 = rand.nextInt(600);
        int r1200 = rand.nextInt(1200);

        float entityRad = AdditionalDataManager.getEntityData(entity, DataEntry.RADIATION).map(o -> (float) o).orElse(0f);

        // 呕吐效果
        if (entityRad > 600) {
            if ((level.getGameTime() + r600) % 600 < 20 && canVomit(entity)){
                CompoundTag tag = new CompoundTag();
                tag.putString("type", "vomit");
                tag.putString("mode", "blood");
                tag.putInt("count", 25);
                tag.putInt("entity", entity.getId());
                ModMessages.sendToAllAround(new S2CParticlePacket(tag, 0, 0, 0), new TargetPoint(entity.getX(), entity.getY(), entity.getZ(), 25, entity.level().dimension()));
                if ((level.getGameTime() + r600) % 600 == 1){
                    entity.playSound(ModSounds.PLAYER_VOMIT.get(), 1f, 1f);
                    entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, 19));
                }
            }
        } else if (entityRad > 200 && (level.getGameTime() + r1200) % 1200 < 20 && canVomit(entity)){
            CompoundTag tag = new CompoundTag();
            tag.putString("type", "vomit");
            tag.putString("mode", "normal");
            tag.putInt("count", 15);
            tag.putInt("entity", entity.getId());
            ModMessages.sendToAllAround(new S2CParticlePacket(tag, 0, 0, 0), new TargetPoint(entity.getX(), entity.getY(), entity.getZ(), 25, entity.level().dimension()));
            if ((level.getGameTime() + r1200) % 1200 == 1){
                entity.playSound(ModSounds.PLAYER_VOMIT.get(), 1f, 1f);
                entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, 19));
            }
        }

        if (entityRad > 900 && (level.getGameTime() + rand.nextInt(10)) % 10 == 0){
            CompoundTag tag = new CompoundTag();
            tag.putString("type", "sweat");
            tag.putInt("count", 1);
            tag.putInt("block", BuiltInRegistries.BLOCK.getId(Blocks.REDSTONE_BLOCK));
            tag.putInt("entity", entity.getId());
            ModMessages.sendToAllAround(new S2CParticlePacket(tag, 0, 0, 0), new TargetPoint(entity.getX(), entity.getY(), entity.getZ(), 25, entity.level().dimension()));
        } else if (entity instanceof ServerPlayer player && entityRad > 600){
            // 玩家看到的辐射烟雾，应该仅限于玩家主观视角
            CompoundTag tag = new CompoundTag();
            tag.putString("type", "radiation");
            tag.putInt("count", entityRad > 900 ? 4 : entityRad > 800 ? 2 : 1);
            ModMessages.sendToPlayer(new S2CParticlePacket(tag, 0, 0, 0), player);
        }
    }

    private static void decayRadiation(LivingEntity entity, IEntityAdditionalData entityData) {
        if (!entityData.contains(DataEntry.RADIATION)) {
            return;
        }
        float rad = entityData.getData(DataEntry.RADIATION, Float.class).orElse(0F);
        if (rad <= 0F) {
            entityData.setData(DataEntry.RADIATION, 0F);
            return;
        }
        float decay = Math.max(rad * RADIATION_DECAY_RATIO, RADIATION_DECAY_FLOOR);
        float next = Math.max(0F, rad - decay);
        if (next <= RADIATION_DECAY_FLOOR) {
            entityData.setData(DataEntry.RADIATION, 0F);
        } else if (next != rad) {
            entityData.setData(DataEntry.RADIATION, next);
        }
    }

    /**
     * 辐射值在生物上产生的效果
     * */
    public static void handleRadiationEffect(LivingEntity entity, IEntityAdditionalData entityData){
        // 只考虑辐射值本身存在的情况
        if (entityData.contains(DataEntry.RADIATION)){
            float rad = (float) entityData.getData(DataEntry.RADIATION).get();
            int amplifier = entity.hasEffect(ModEffects.RADIATION.get()) ? entity.getEffect(ModEffects.RADIATION.get()).getAmplifier() : -1;
            if (rad >= 1000){
                entity.kill();
            }else if(rad >= 800) {
                if (amplifier < 4) entity.addEffect(new MobEffectInstance(ModEffects.RADIATION.get(), 3600, 4));
            } else if(rad >= 600) {
                if (amplifier < 3) entity.addEffect(new MobEffectInstance(ModEffects.RADIATION.get(), 3600, 3));
            } else if(rad >= 400) {
                if (amplifier < 2) entity.addEffect(new MobEffectInstance(ModEffects.RADIATION.get(), 3600, 2));
            } else if(rad >= 200) {
                if (amplifier < 1) entity.addEffect(new MobEffectInstance(ModEffects.RADIATION.get(), 3600, 1));
            }
        }
    }
    /** 判断实体是否可以呕吐 */
    private static boolean canVomit(LivingEntity e) {
        return e.isUnderWater() || e.isInLava();
    }
}
