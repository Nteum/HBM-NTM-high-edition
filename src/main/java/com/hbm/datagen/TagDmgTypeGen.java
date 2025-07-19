package com.hbm.datagen;

import com.hbm.utils.damage.ModDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;

import java.util.concurrent.CompletableFuture;
/** damagetype对应的标签类
 * 目前的问题在于这里面加不上模组添加的damagetype
 *
 * */
public class TagDmgTypeGen extends TagsProvider<DamageType> {
    public TagDmgTypeGen(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider) {
        super(pOutput, Registries.DAMAGE_TYPE, pLookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(DamageTypeTags.BYPASSES_ARMOR).add(ModDamageTypes.RADIATION);
    }
}
