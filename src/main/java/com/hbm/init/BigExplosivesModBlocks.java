package net.mcreator.nuclearcraft.init;

import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.mcreator.nuclearcraft.block.AdvancedWorkBenchBlock;
import net.mcreator.nuclearcraft.block.BasicWorkBenchBlock;
import net.mcreator.nuclearcraft.block.BasicWorkBenchGeckolibBlock;
import net.mcreator.nuclearcraft.block.GeckoAdvancedWorkbenchBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/init/BigExplosivesModBlocks.class */
public class BigExplosivesModBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, BigExplosivesMod.MODID);
    public static final RegistryObject<Block> BASIC_WORK_BENCH = REGISTRY.register("basic_work_bench", () -> {
        return new BasicWorkBenchBlock();
    });
    public static final RegistryObject<Block> ADVANCED_WORK_BENCH = REGISTRY.register("advanced_work_bench", () -> {
        return new AdvancedWorkBenchBlock();
    });
    public static final RegistryObject<Block> BASIC_WORK_BENCH_GECKOLIB = REGISTRY.register("basic_work_bench_geckolib", () -> {
        return new BasicWorkBenchGeckolibBlock();
    });
    public static final RegistryObject<Block> GECKO_ADVANCED_WORKBENCH = REGISTRY.register("gecko_advanced_workbench", () -> {
        return new GeckoAdvancedWorkbenchBlock();
    });
}
