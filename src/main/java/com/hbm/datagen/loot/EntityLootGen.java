package com.hbm.datagen.loot;

import com.hbm.entity.ModEntityType;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModItems;;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Stream;

/**
 * 杀死实体的凋落物表，只处理本模组注册的实体，避免外部模组缺失资源导致数据生成中断。
 */
public class EntityLootGen extends EntityLootSubProvider {
    public EntityLootGen() {
        super(FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    public void generate() {
        this.add(ModEntityType.GLYPHID.get(), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(2.0f))
                .add(LootItem.lootTableItem(ModItems.GLYPHID_MEAT.get())
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                    .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))
                .apply(SmeltItemFunction.smelted()
                    .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE))))
        );
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return ModEntityType.ENTITY_TYPES.getEntries().stream().map(RegistryObject::get);
    }

}
