package com.hbm.entity.blockentity;

import com.hbm.DalisFunMod;
import com.hbm.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<MachineElectricFurnaceEntity> electric_furnace_entity = registerEntity(MachineElectricFurnaceEntity::new,"electric_furnace_entity", ModBlocks.machine_electric_furnace);

    public static <T extends BlockEntity>BlockEntityType<T> registerEntity(FabricBlockEntityTypeBuilder.Factory<? extends T> factory, String path, Block block){
        return (BlockEntityType<T>)Registry.register(Registries.BLOCK_ENTITY_TYPE,new Identifier(DalisFunMod.MOD_ID,path),
                FabricBlockEntityTypeBuilder.create(factory,block).build());
    }
    public static void register(){
        DalisFunMod.LOGGER.info("Mod block entity registry");
    }
}
