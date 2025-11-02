package net.mcreator.nuclearcraft.init;

import com.mojang.datafixers.types.Type;
import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.mcreator.nuclearcraft.block.entity.AdvancedWorkBenchBlockEntity;
import net.mcreator.nuclearcraft.block.entity.BasicWorkBenchBlockEntity;
import net.mcreator.nuclearcraft.block.entity.BasicWorkBenchGeckolibTileEntity;
import net.mcreator.nuclearcraft.block.entity.GeckoAdvancedWorkbenchTileEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/init/BigExplosivesModBlockEntities.class */
public class BigExplosivesModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BigExplosivesMod.MODID);
    public static final RegistryObject<BlockEntityType<?>> BASIC_WORK_BENCH = register("basic_work_bench", BigExplosivesModBlocks.BASIC_WORK_BENCH, BasicWorkBenchBlockEntity::new);
    public static final RegistryObject<BlockEntityType<?>> ADVANCED_WORK_BENCH = register("advanced_work_bench", BigExplosivesModBlocks.ADVANCED_WORK_BENCH, AdvancedWorkBenchBlockEntity::new);
    public static final RegistryObject<BlockEntityType<BasicWorkBenchGeckolibTileEntity>> BASIC_WORK_BENCH_GECKOLIB = REGISTRY.register("basic_work_bench_geckolib", () -> {
        return BlockEntityType.Builder.m_155273_(BasicWorkBenchGeckolibTileEntity::new, new Block[]{(Block) BigExplosivesModBlocks.BASIC_WORK_BENCH_GECKOLIB.get()}).m_58966_((Type) null);
    });
    public static final RegistryObject<BlockEntityType<GeckoAdvancedWorkbenchTileEntity>> GECKO_ADVANCED_WORKBENCH = REGISTRY.register("gecko_advanced_workbench", () -> {
        return BlockEntityType.Builder.m_155273_(GeckoAdvancedWorkbenchTileEntity::new, new Block[]{(Block) BigExplosivesModBlocks.GECKO_ADVANCED_WORKBENCH.get()}).m_58966_((Type) null);
    });

    private static RegistryObject<BlockEntityType<?>> register(String registryname, RegistryObject<Block> block, BlockEntityType.BlockEntitySupplier<?> supplier) {
        return REGISTRY.register(registryname, () -> {
            return BlockEntityType.Builder.m_155273_(supplier, new Block[]{(Block) block.get()}).m_58966_((Type) null);
        });
    }
}
