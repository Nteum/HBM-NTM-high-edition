package com.hbm.blockentity;

import com.hbm.HBMxx;
import com.hbm.block.ModBlocks;
import com.hbm.block.machine.BlockDifurnace;
import com.hbm.blockentity.machine.AssemblerEntity;
import com.hbm.blockentity.machine.CrucibleEntity;
import com.hbm.blockentity.machine.DifurnaceEntity;
import com.hbm.blockentity.machine.PressEntity;
import com.hbm.blockentity.weapon.NukeBombEntity;
import com.hbm.blockentity.weapon.NukeBombFatEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityType {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, HBMxx.MODID);
    public static final RegistryObject<BlockEntityType<DifurnaceEntity>> DIFURNACE_ENTITY =
            BLOCK_ENTITY_TYPES.register("difurnace_entity",()-> BlockEntityType.Builder.of(DifurnaceEntity::new, ModBlocks.machine_difurnace.get()).build(null));
    public static final RegistryObject<BlockEntityType<PressEntity>> PRESS_ENTITY =
            BLOCK_ENTITY_TYPES.register("press_entity",()-> BlockEntityType.Builder.of(PressEntity::new, ModBlocks.machine_press.get()).build(null));
    public static final RegistryObject<BlockEntityType<NukeBombFatEntity>> NUKE_BOMB_FAT_ENTITY =
            BLOCK_ENTITY_TYPES.register("nuke_bomb_entity",()-> BlockEntityType.Builder.of(NukeBombFatEntity::new, ModBlocks.bomb_fat_man.get()).build(null));
    public static final RegistryObject<BlockEntityType<AssemblerEntity>> ASSEMBLER_ENTITY =
            BLOCK_ENTITY_TYPES.register("assembler_entity",()-> BlockEntityType.Builder.of(AssemblerEntity::new, ModBlocks.machine_assembler.get()).build(null));
    public static final RegistryObject<BlockEntityType<CrucibleEntity>> CRUCIBLE_ENTITY =
            BLOCK_ENTITY_TYPES.register("crucible_entity",()-> BlockEntityType.Builder.of(CrucibleEntity::new, ModBlocks.machine_crucible.get()).build(null));
}
