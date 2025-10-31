package net.mcreator.nuclearcraft.init;

import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.mcreator.nuclearcraft.block.display.BasicWorkBenchGeckolibDisplayItem;
import net.mcreator.nuclearcraft.block.display.GeckoAdvancedWorkbenchDisplayItem;
import net.mcreator.nuclearcraft.item.AtomBombItem;
import net.mcreator.nuclearcraft.item.AtomicCoreItem;
import net.mcreator.nuclearcraft.item.BunkerBusterBombItem;
import net.mcreator.nuclearcraft.item.BunkerBusterDesignatorItem;
import net.mcreator.nuclearcraft.item.CakeBombDesignatorItem;
import net.mcreator.nuclearcraft.item.CoordStorerItem;
import net.mcreator.nuclearcraft.item.EmptyAirstrikeDesignatorItem;
import net.mcreator.nuclearcraft.item.ExplosiveCoreItem;
import net.mcreator.nuclearcraft.item.FireCoreItem;
import net.mcreator.nuclearcraft.item.FiveHundredKgBombDesignatorItem;
import net.mcreator.nuclearcraft.item.FiveHundredKilogramBombItem;
import net.mcreator.nuclearcraft.item.FourthOfJulyBombItem;
import net.mcreator.nuclearcraft.item.FourthOfJulyDesignatorItem;
import net.mcreator.nuclearcraft.item.GoldenBeetrootItem;
import net.mcreator.nuclearcraft.item.GoldenBeetrootSoupItem;
import net.mcreator.nuclearcraft.item.IronPlateItem;
import net.mcreator.nuclearcraft.item.NapalmBarageDesignatorrItem;
import net.mcreator.nuclearcraft.item.NapalmBombbItem;
import net.mcreator.nuclearcraft.item.NapalmButtonItem;
import net.mcreator.nuclearcraft.item.TenKgBombBarrageDesignatorItem;
import net.mcreator.nuclearcraft.item.TenKgBombItem;
import net.mcreator.nuclearcraft.item.TenKgNapalmBombItem;
import net.mcreator.nuclearcraft.item.TestItem;
import net.mcreator.nuclearcraft.item.TwoFiddyButtonItem;
import net.mcreator.nuclearcraft.item.TwoHundredAndFiftyKgBombItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/init/BigExplosivesModItems.class */
public class BigExplosivesModItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, BigExplosivesMod.MODID);
    public static final RegistryObject<Item> TWO_FIDDY_BUTTON = REGISTRY.register("two_fiddy_button", () -> {
        return new TwoFiddyButtonItem();
    });
    public static final RegistryObject<Item> TWO_HUNDRED_AND_FIFTY_KG_BOMB = REGISTRY.register("two_hundred_and_fifty_kg_bomb", () -> {
        return new TwoHundredAndFiftyKgBombItem();
    });
    public static final RegistryObject<Item> EMPTY_AIRSTRIKE_DESIGNATOR = REGISTRY.register("empty_airstrike_designator", () -> {
        return new EmptyAirstrikeDesignatorItem();
    });
    public static final RegistryObject<Item> FIVE_HUNDRED_KILOGRAM_BOMB = REGISTRY.register("five_hundred_kilogram_bomb", () -> {
        return new FiveHundredKilogramBombItem();
    });
    public static final RegistryObject<Item> TEN_KG_BOMB = REGISTRY.register("ten_kg_bomb", () -> {
        return new TenKgBombItem();
    });
    public static final RegistryObject<Item> NAPALM_BUTTON = REGISTRY.register("napalm_button", () -> {
        return new NapalmButtonItem();
    });
    public static final RegistryObject<Item> NAPALM_BOMBB = REGISTRY.register("napalm_bombb", () -> {
        return new NapalmBombbItem();
    });
    public static final RegistryObject<Item> TEN_KG_NAPALM_BOMB = REGISTRY.register("ten_kg_napalm_bomb", () -> {
        return new TenKgNapalmBombItem();
    });
    public static final RegistryObject<Item> BUNKER_BUSTER_BOMB = REGISTRY.register("bunker_buster_bomb", () -> {
        return new BunkerBusterBombItem();
    });
    public static final RegistryObject<Item> BUNKER_BUSTER_DESIGNATOR = REGISTRY.register("bunker_buster_designator", () -> {
        return new BunkerBusterDesignatorItem();
    });
    public static final RegistryObject<Item> ATOM_BOMB = REGISTRY.register("atom_bomb", () -> {
        return new AtomBombItem();
    });
    public static final RegistryObject<Item> TEST = REGISTRY.register("test", () -> {
        return new TestItem();
    });
    public static final RegistryObject<Item> FIVE_HUNDRED_KG_BOMB_DESIGNATOR = REGISTRY.register("five_hundred_kg_bomb_designator", () -> {
        return new FiveHundredKgBombDesignatorItem();
    });
    public static final RegistryObject<Item> TEN_KG_BOMB_BARRAGE_DESIGNATOR = REGISTRY.register("ten_kg_bomb_barrage_designator", () -> {
        return new TenKgBombBarrageDesignatorItem();
    });
    public static final RegistryObject<Item> NAPALM_BARAGE_DESIGNATORR = REGISTRY.register("napalm_barage_designatorr", () -> {
        return new NapalmBarageDesignatorrItem();
    });
    public static final RegistryObject<Item> BASIC_WORK_BENCH = block(BigExplosivesModBlocks.BASIC_WORK_BENCH);
    public static final RegistryObject<Item> ADVANCED_WORK_BENCH = block(BigExplosivesModBlocks.ADVANCED_WORK_BENCH);
    public static final RegistryObject<Item> IRON_PLATE = REGISTRY.register("iron_plate", () -> {
        return new IronPlateItem();
    });
    public static final RegistryObject<Item> ATOMIC_CORE = REGISTRY.register("atomic_core", () -> {
        return new AtomicCoreItem();
    });
    public static final RegistryObject<Item> FIRE_CORE = REGISTRY.register("fire_core", () -> {
        return new FireCoreItem();
    });
    public static final RegistryObject<Item> BASIC_WORK_BENCH_GECKOLIB = REGISTRY.register(BigExplosivesModBlocks.BASIC_WORK_BENCH_GECKOLIB.getId().m_135815_(), () -> {
        return new BasicWorkBenchGeckolibDisplayItem((Block) BigExplosivesModBlocks.BASIC_WORK_BENCH_GECKOLIB.get(), new Item.Properties());
    });
    public static final RegistryObject<Item> GECKO_ADVANCED_WORKBENCH = REGISTRY.register(BigExplosivesModBlocks.GECKO_ADVANCED_WORKBENCH.getId().m_135815_(), () -> {
        return new GeckoAdvancedWorkbenchDisplayItem((Block) BigExplosivesModBlocks.GECKO_ADVANCED_WORKBENCH.get(), new Item.Properties());
    });
    public static final RegistryObject<Item> EXPLOSIVE_CORE = REGISTRY.register("explosive_core", () -> {
        return new ExplosiveCoreItem();
    });
    public static final RegistryObject<Item> GOLDEN_BEETROOT = REGISTRY.register("golden_beetroot", () -> {
        return new GoldenBeetrootItem();
    });
    public static final RegistryObject<Item> GOLDEN_BEETROOT_SOUP = REGISTRY.register("golden_beetroot_soup", () -> {
        return new GoldenBeetrootSoupItem();
    });
    public static final RegistryObject<Item> CAKE_BOMB_DESIGNATOR = REGISTRY.register("cake_bomb_designator", () -> {
        return new CakeBombDesignatorItem();
    });
    public static final RegistryObject<Item> COORD_STORER = REGISTRY.register("coord_storer", () -> {
        return new CoordStorerItem();
    });
    public static final RegistryObject<Item> FOURTH_OF_JULY_DESIGNATOR = REGISTRY.register("fourth_of_july_designator", () -> {
        return new FourthOfJulyDesignatorItem();
    });
    public static final RegistryObject<Item> FOURTH_OF_JULY_BOMB = REGISTRY.register("fourth_of_july_bomb", () -> {
        return new FourthOfJulyBombItem();
    });

    private static RegistryObject<Item> block(RegistryObject<Block> block) {
        return REGISTRY.register(block.getId().m_135815_(), () -> {
            return new BlockItem((Block) block.get(), new Item.Properties());
        });
    }
}
