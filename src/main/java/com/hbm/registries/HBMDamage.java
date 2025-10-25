package com.hbm.registries;

import com.hbm.HBM;
import com.hbm.datagen.HBMJsonProvider;
import com.hbm.datagen.LanguageProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.data.loading.DatagenModLoader;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HBMDamage {
    // 数据生成
    public static Map<String, String> localizedDescribe = new HashMap<>();
    public static List<DamageType> damageTypeData = new ArrayList<>();

    public static ResourceKey<DamageType> NUKE = register("nuclear_blast");
    public static ResourceKey<DamageType> MUD_POISON = register("mud_poison");
    public static ResourceKey<DamageType> ACID = register("acid");
    public static ResourceKey<DamageType> EUTHANIZED_SELF = register("euthanized_self");
    public static ResourceKey<DamageType> EUTHANIZED_SELF_ALTER = register("euthanized_self_alter");
    public static ResourceKey<DamageType> TAU_BLAST = register("tau_blast");
    public static ResourceKey<DamageType> RADIATION = register("radiation");
    public static ResourceKey<DamageType> DIGAMMA = register("digamma");
    public static ResourceKey<DamageType> SUICIDE = register("suicide");
    public static ResourceKey<DamageType> TELEPORTER = register("teleporter");
    public static ResourceKey<DamageType> CHEATER = register("cheater");
    public static ResourceKey<DamageType> RUBBLE = register("rubble");
    public static ResourceKey<DamageType> SHRAPNEL = register("shrapnel");
    public static ResourceKey<DamageType> BLACKHOLE = register("blackhole");
    public static ResourceKey<DamageType> TURBOFAN = register("turbofan");
    public static ResourceKey<DamageType> METEORITE = register("meteorite");
    public static ResourceKey<DamageType> BOXCAR = register("boxcar");
    public static ResourceKey<DamageType> BOAT = register("boat");
    public static ResourceKey<DamageType> BUILDING = register("building");
    public static ResourceKey<DamageType> TAINT = register("taint");
    public static ResourceKey<DamageType> AMS = register("ams");
    public static ResourceKey<DamageType> AMS_CORE = register("ams_core");
    public static ResourceKey<DamageType> BROADCAST = register("broadcast");
    public static ResourceKey<DamageType> BANG = register("bang");
    public static ResourceKey<DamageType> PC = register("pc");
    public static ResourceKey<DamageType> CLOUD = register("cloud");
    public static ResourceKey<DamageType> LEAD = register("lead");
    public static ResourceKey<DamageType> ENERVATION = register("enervation");
    public static ResourceKey<DamageType> ELECTRICITY = register("electricity");
    public static ResourceKey<DamageType> EXHAUST = register("exhaust");
    public static ResourceKey<DamageType> SPIKES = register("spikes");
    public static ResourceKey<DamageType> LUNAR = register("lunar");
    public static ResourceKey<DamageType> MONOXIDE = register("monoxide");
    public static ResourceKey<DamageType> ASBESTOS = register("asbestos");
    public static ResourceKey<DamageType> BLACKLUNG = register("blacklung");
    public static ResourceKey<DamageType> MKU = register("mku");
    public static ResourceKey<DamageType> VACUUM = register("vacuum");
    public static ResourceKey<DamageType> OVERDOSE = register("overdose");
    public static ResourceKey<DamageType> MICROWAVE = register("microwave");
    public static ResourceKey<DamageType> NITAN = register("nitan");
    // 1710版将以下部分单列了一个DamageClass,意义不明,因此暂时只作为普通damagetype使用.
    public static ResourceKey<DamageType> LASER = register("laser");
    public static ResourceKey<DamageType> ELECTRIC = register("electric");
    public static ResourceKey<DamageType> SUBAUTOMIC = register("subautomic");


    public static ResourceKey<DamageType> register(String id){
        ResourceKey<DamageType> key = ResourceKey.create(Registries.DAMAGE_TYPE, HBM.rl(id));
        String englishName = RegistryHelper.generateOrderlyName(id);
        localizedDescribe.put("death.attack." + id, "%s is killed by " + englishName);
        localizedDescribe.put("death.attack." + id + ".entity", "%1$s is killed by " + englishName + ", from %2$s");
        localizedDescribe.put("death.attack." + id + ".item", "%1$s is killed by " + englishName + ", from %2$s with %3$s");
//        damageTypeData.add(new DamageType(id, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0f, DamageEffects.HURT));
        return key;
    }

    public static void clearLocalData(){
        if (!DatagenModLoader.isRunningDataGen()) {
            localizedDescribe = null;
//            damageTypeData = null;
        }
    }

    public static void languageSupport(LanguageProvider provider){
        localizedDescribe.forEach(provider::add);
    }

    public static void damageTypeJson(HBMJsonProvider provider){
//        damageTypeData.forEach(provider::damageType);
    }

    // ref: superb warfire
    public static DamageSource get(ResourceKey<DamageType> key, RegistryAccess registryAccess, @Nullable Entity directEntity, @Nullable Entity attacker) {
        return new DamageMessages(registryAccess.registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(key), directEntity, attacker);
    }
    private static class DamageMessages extends DamageSource {

        public DamageMessages(Holder.Reference<DamageType> typeReference) {
            super(typeReference);
        }

        public DamageMessages(Holder.Reference<DamageType> typeReference, Entity entity) {
            super(typeReference, entity);
        }

        public DamageMessages(Holder.Reference<DamageType> typeReference, Entity directEntity, Entity attacker) {
            super(typeReference, directEntity, attacker);
        }

        @Override
        public Component getLocalizedDeathMessage(LivingEntity pLivingEntity) {
            Entity entity = this.getEntity() == null ? this.getDirectEntity() : this.getEntity();
            if (entity == null) {
                return Component.translatable("death.attack." + this.getMsgId(), pLivingEntity.getDisplayName());
            } else if (entity instanceof LivingEntity living && living.getMainHandItem().hasCustomHoverName()) {
                return Component.translatable("death.attack." + this.getMsgId() + ".item", pLivingEntity.getDisplayName(), entity.getDisplayName(), living.getMainHandItem().getDisplayName());
            } else {
                return Component.translatable("death.attack." + this.getMsgId() + ".entity", pLivingEntity.getDisplayName(), entity.getDisplayName());
            }
        }
    }
}
