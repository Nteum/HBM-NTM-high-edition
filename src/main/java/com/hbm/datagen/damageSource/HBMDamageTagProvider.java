package com.hbm.datagen.damageSource;

import com.hbm.registries.HBMDamage;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class HBMDamageTagProvider extends DamageTypeTagsProvider {
    public HBMDamageTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        super.addTags(pProvider);
        this.tag(DamageTypeTags.IS_EXPLOSION).add(HBMDamage.NUKE);
        this.tag(DamageTypeTags.BYPASSES_ARMOR).add(HBMDamage.MUD_POISON, HBMDamage.ACID, HBMDamage.EUTHANIZED_SELF, HBMDamage.EUTHANIZED_SELF_ALTER, HBMDamage.TAU_BLAST, HBMDamage.RADIATION, HBMDamage.DIGAMMA, HBMDamage.CHEATER,
                HBMDamage.BLACKHOLE, HBMDamage.TURBOFAN, HBMDamage.METEORITE, HBMDamage.BOXCAR, HBMDamage.BOAT, HBMDamage.BUILDING, HBMDamage.TAINT, HBMDamage.TAINT, HBMDamage.AMS, HBMDamage.AMS_CORE,
                HBMDamage.BROADCAST, HBMDamage.BANG, HBMDamage.PC, HBMDamage.CLOUD, HBMDamage.LEAD, HBMDamage.ENERVATION, HBMDamage.ELECTRICITY, HBMDamage.EXHAUST, HBMDamage.SPIKES, HBMDamage.LUNAR,
                HBMDamage.MONOXIDE, HBMDamage.ASBESTOS, HBMDamage.BLACKLUNG, HBMDamage.MKU, HBMDamage.VACUUM, HBMDamage.OVERDOSE, HBMDamage.MICROWAVE, HBMDamage.NITAN);
        // BYPASSES_EFFECTS用于对应低版本的setDamageIsAbsolute,我不知道是不是,只是低版本属于setDamageIsAbsolute的STARVE加入了这个tag
        this.tag(DamageTypeTags.BYPASSES_EFFECTS).add(HBMDamage.DIGAMMA, HBMDamage.TELEPORTER, HBMDamage.CHEATER, HBMDamage.BLACKHOLE, HBMDamage.TURBOFAN, HBMDamage.METEORITE, HBMDamage.BOXCAR,
                HBMDamage.BOAT, HBMDamage.BUILDING, HBMDamage.TAINT, HBMDamage.TAINT, HBMDamage.AMS, HBMDamage.AMS_CORE, HBMDamage.BROADCAST, HBMDamage.BANG,  HBMDamage.PC, HBMDamage.CLOUD,
                HBMDamage.LEAD, HBMDamage.ENERVATION, HBMDamage.ELECTRICITY, HBMDamage.EXHAUST, HBMDamage.LUNAR, HBMDamage.MONOXIDE, HBMDamage.ASBESTOS, HBMDamage.BLACKLUNG, HBMDamage.MKU,
                HBMDamage.VACUUM, HBMDamage.OVERDOSE, HBMDamage.MICROWAVE, HBMDamage.NITAN);
        this.tag(DamageTypeTags.IS_PROJECTILE).add(HBMDamage.SUICIDE, HBMDamage.RUBBLE, HBMDamage.SHRAPNEL);
        // 我不清楚低版本setDamageAllowedInCreativeMode对应高版本什么,参考高版本FELL_OUT_OF_WORLD的设置加这两个参数
        this.tag(DamageTypeTags.BYPASSES_INVULNERABILITY).add(HBMDamage.CHEATER, HBMDamage.NITAN);
        this.tag(DamageTypeTags.BYPASSES_RESISTANCE).add(HBMDamage.CHEATER, HBMDamage.NITAN);
    }
}
