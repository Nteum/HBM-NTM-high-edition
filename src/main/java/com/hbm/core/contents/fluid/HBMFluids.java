package com.hbm.core.contents.fluid;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.core.contents.addational_data.Pollution;
import com.hbm.item.tool.FluidBucketItem;
import com.hbm.registries.ModTabs;
import com.hbm.registries.WrappedRegistryBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Consumer;

import com.hbm.core.contents.fluid.TraitData.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hbm.HBM.MODID;
import static com.hbm.core.contents.fluid.FluidTraits.*;

/**
 * HBM 流体注册表 —— 从 1.7.10 Fluids.java 迁移。
 *
 * <h3>分组说明</h3>
 * 流体按照 Trait 模式分为若干组，每组用一个 helper 方法批量注册。
 * 这样新增流体只需在对应组里加一行，不用重复写 trait 配置。
 *
 * <h3>命名规范</h3>
 * 流体名保留旧版的 snake_case 小写风格（兼容 NBT 存档），
 * 流体 key = {@code nt_vehicle:fluid_xxx}。
 */
@SuppressWarnings("unused")
public class HBMFluids {
    //流体注册器
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, HBM.MODID);
    //流体类型注册器
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, HBM.MODID);

    // ==== 基础 ====
    public static FluidHolder NONE, WATER, AIR, AIRBLAST;
    // ==== 蒸汽链 ====
    public static FluidHolder STEAM, HOTSTEAM, SUPERHOTSTEAM, ULTRAHOTSTEAM, SPENTSTEAM;
    // ==== 冷却系 ====
    public static FluidHolder COOLANT, COOLANT_HOT;
    // ==== 熔岩 ====
    public static FluidHolder LAVA, MAGMA;
    // ==== 燃料气体 ====
    public static FluidHolder DEUTERIUM, TRITIUM;
    // ==== 石油链 ====
    public static FluidHolder OIL, HOTOIL, HEAVYOIL, BITUMEN, SMEAR, HEATINGOIL, RECLAIMED, PETROIL, LUBRICANT, NAPHTHA, DIESEL, LIGHTOIL, KEROSENE, GAS, PETROLEUM, LPG, BIOGAS, BIOFUEL, NITAN;
    public static FluidHolder CRACKOIL, COALOIL, HOTCRACKOIL, NAPHTHA_CRACK, LIGHTOIL_CRACK, DIESEL_CRACK;
    public static FluidHolder AROMATICS, UNSATURATEDS;
    public static FluidHolder OIL_DS, HOTOIL_DS, CRACKOIL_DS, HOTCRACKOIL_DS, NAPHTHA_DS, LIGHTOIL_DS;
    public static FluidHolder GASOLINE, GASOLINE_LEADED, COALGAS, COALGAS_LEADED, PETROIL_LEADED;
    public static FluidHolder HEAVYOIL_VACUUM, LIGHTOIL_VACUUM, HEATINGOIL_VACUUM;
    public static FluidHolder REFORMATE, DIESEL_REFORM, DIESEL_CRACK_REFORM, KEROSENE_REFORM, REFORMGAS;
    public static FluidHolder XYLENE;
    public static FluidHolder WOODOIL, COALCREOSOTE;
    public static FluidHolder OIL_COKER, NAPHTHA_COKER, GAS_COKER, FLUE;
    // ==== 一般气体 ====
    public static FluidHolder HYDROGEN, OXYGEN, NITROGEN, CARBONDIOXIDE;
    public static FluidHolder HELIUM3, HELIUM4, NEON, ARGON, KRYPTON, XENON;
    public static FluidHolder SYNGAS, OXYHYDROGEN, SOURGAS;
    public static FluidHolder CHLOROMETHANE, CHLOROETHANE, CHLORINE, FLUORINE, BROMINE;
    public static FluidHolder AMMONIA, HYDRAZINE;
    public static FluidHolder METHANOL, ETHANOL;
    public static FluidHolder PHOSGENE, MUSTARDGAS, DICYANOACETYLENE, DHC;
    public static FluidHolder LITHYDRO, LITHCARBONATE;
    // ==== 酸与腐蚀性 ====
    public static FluidHolder SULFURIC_ACID, HCL, NITRIC_ACID, PEROXIDE, SOLVENT, MINSOL, FRACKSOL;
    public static FluidHolder RADIOSOLVENT, CCL, KMnO4, LYE, VITRIOL;
    public static FluidHolder CHLOROCALCITE_SOLUTION, CHLOROCALCITE_MIX, CHLOROCALCITE_CLEANED;
    public static FluidHolder POTASSIUM_CHLORIDE, CALCIUM_CHLORIDE, CALCIUM_SOLUTION;
    public static FluidHolder BAUXITE_SOLUTION, SODIUM_ALUMINATE, ALUMINA, AQUEOUS_NICKEL;
    public static FluidHolder AQUEOUS_COPPER, COPPERSULFATE;
    // ==== 核工业 ====
    public static FluidHolder UF6, PUF6, SAS3, SCHRABIDIC, AMAT, ASCHRAB;
    public static FluidHolder WATZ, GAS_WATZ, CRYOGEL;
    public static FluidHolder URANIUM_BROMIDE, THORIUM_BROMIDE;
    public static FluidHolder GASEOUS_URANIUM_BROMIDE, GASEOUS_PLUTONIUM_BROMIDE, GASEOUS_SCHRABIDIUM_BROMIDE, GASEOUS_THORIUM_BROMIDE;
    public static FluidHolder PERFLUOROMETHYL, PERFLUOROMETHYL_COLD, PERFLUOROMETHYL_HOT;
    public static FluidHolder WASTEFLUID, WASTEGAS, REDMUD, HTCO4, HGAS;
    // ==== 金属熔液 ====
    public static FluidHolder MERCURY, LEAD, LEAD_HOT, SODIUM, SODIUM_HOT;
    public static FluidHolder THORIUM_SALT, THORIUM_SALT_HOT, THORIUM_SALT_DEPLETED;
    // ==== 等离子与特种 ====
    public static FluidHolder PLASMA_DT, PLASMA_HD, PLASMA_HT, PLASMA_DH3, PLASMA_XM, PLASMA_BF;
    public static FluidHolder BALEFIRE, PAIN, DEATH, SALIENT;
    public static FluidHolder NMASS, NMASSTETRANOL, STELLAR_FLUX, FULLERENE;
    public static FluidHolder IONGEL, ELBOWGREASE;
    public static FluidHolder COLLOID, BRINE, CONGLOMERA, SEEDSLURRY, SLOP;
    // ==== 蒸汽/水 ====
    public static FluidHolder HEAVYWATER, HEAVYWATER_HOT, SUPERHEATED_HYDROGEN;
    // ==== 外星大气 ====
    public static FluidHolder EARTHAIR, EVEAIR, DUNAAIR, TEKTOAIR, JOOLGAS, SARNUSGAS, UGAS, NGAS;
    // ==== 杂项 ====
    public static FluidHolder BLOOD, BLOOD_HOT, BLOODGAS, SCUTTERBLOOD;
    public static FluidHolder MUG, MUG_HOT, COFFEE, TEA, HONEY, MILK, SMILK, EMILK, CMILK, CREAM;
    public static FluidHolder EGG, CHOLESTEROL, ESTRADIOL;
    public static FluidHolder FISHOIL, SUNFLOWEROIL;
    public static FluidHolder XPJUICE, ENDERJUICE;
    public static FluidHolder NITROGLYCERIN;
    public static FluidHolder SMOKE, SMOKE_LEADED, SMOKE_POISON;
    public static FluidHolder PHEROMONE, PHEROMONE_M;
    public static FluidHolder CONCRETE, VINYL, TCRUDE, CBENZ, HALOLIGHT, POLYTHYLENE;
    public static FluidHolder IRRADIATED_WATER, IRRADIATED_POLLUTED;

    // 省略较少使用的流体定义；所有 ~180 个应在此

    // ================================================================
    // 初始化
    // ================================================================

    private static final List<FluidHolder> ALL = new ArrayList<>();

    public static void init() {
        // ==== 基础 ====
        NONE = add(of("none", 0x888888, 0, 0, 0, 0, b -> {}));
        WATER = add(of("water", 0x3333FF, 0, 0, 0, 20, b -> b.addTags(LIQUID, UNSIPHONABLE)));
        AIR    = add(simpleGas("air", 0xE7EAEB, 0, 0));
        AIRBLAST = add(of("airblast", 0xFFDADA, 0, 3, 0, 1200, b -> b.addTag(GASEOUS).gaseous()));

        // 蒸汽链
        STEAM         = add(hotGas("steam", 0xE5E5E5, 3, 100));
        HOTSTEAM      = add(hotGas("hotsteam", 0xE7D6D6, 4, 300));
        SUPERHOTSTEAM = add(hotGas("superhotsteam", 0xE7B7B7, 4, 450));
        ULTRAHOTSTEAM = add(hotGas("ultrahotsteam", 0xE39393, 4, 600));
        SPENTSTEAM    = add(hotGas("spentsteam", 0xC8C8C8, 3, 70));

        // 冷却剂
        COOLANT     = add(of("coolant", 0xD8FCFF, 1, 0, 0, 25, b -> b.addTag(LIQUID)));
        COOLANT_HOT = add(of("coolant_hot", 0xFFD8D8, 1, 0, 0, 300, b ->
                b.addTag(LIQUID)
                 .addData(new TraitData.Coolable("coolant", 1000, 900, 5000, List.of(
                         eff(TraitData.HeatingType.HEAT_EXCHANGER, 1.0)))
                 )));

        // 熔岩
        LAVA = add(of("lava", 0xFF3300, 4, 0, 0, 1200, b -> b.addTags(LIQUID, VISCOUS).lightLevel(15).viscosity(8000)));
        MAGMA = add(of("magma", 0xFF3300, 3, 3, 3, 1300, b -> b.addTags(LIQUID, VISCOUS).lightLevel(12)));

        // ==== 燃料气体 ====
        DEUTERIUM = add(combustibleGas("deuterium", 0x0000FF, 3, TraitData.FuelGrade.HIGH, 5_000, 10_000));
        TRITIUM   = add(combustibleGas("tritium", 0x000099, 3, FuelGrade.HIGH, 5_000, 10_000, new TraitData.VentRadiation(0.001F)));

        // ==== 石油链 ====
        OIL        = add(crudeOil("oil", 0x020202, 2, 1));
        HOTOIL     = add(hotOil("hotoil", 0x300900, 2, 3, 350));
        HEAVYOIL   = add(fuelOil("heavyoil", 0x141312, 2, 1, FuelGrade.LOW, 50_000, 25_000));
        BITUMEN    = add(of("bitumen", 0x1F2426, 2, 0, 0, 20, b -> b.addTags(LIQUID, VISCOUS).addData(polluteOil())));
        SMEAR      = add(fuelOil("smear", 0x190F01, 2, 1, FuelGrade.LOW, 50_000, 0));

        HEATINGOIL = add(fuelOil("heatingoil", 0x211806, 2, 2, FuelGrade.LOW, 150_000, 100_000));
        RECLAIMED  = add(fuelOil("reclaimed", 0x332B22, 2, 2, FuelGrade.LOW, 100_000, 200_000, polluteFuel()));
        PETROIL    = add(fuelOil("petroil", 0x44413D, 1, 3, FuelGrade.MEDIUM, 125_000, 300_000, polluteFuel()));
        LUBRICANT  = add(of("lubricant", 0x606060, 2, 1, 0, 20, b -> b.addTags(LIQUID, VISCOUS).addData(polluteOil())));

        NAPHTHA    = add(fuelOil("naphtha", 0x595744, 2, 1, FuelGrade.MEDIUM, 125_000, 200_000, polluteFuel()));
        DIESEL     = add(fuelOil("diesel", 0xF2EED5, 1, 2, FuelGrade.HIGH, 200_000, 500_000, polluteFuel()));
        LIGHTOIL   = add(fuelOil("lightoil", 0x8C7451, 1, 2, FuelGrade.MEDIUM, 200_000, 500_000, polluteFuel()));
        KEROSENE   = add(fuelOil("kerosene", 0xFFA5D2, 1, 2, FuelGrade.AERO, 300_000, 1_250_000, polluteFuel(), new RocketFuel(308, 981_000)));

        GAS        = add(fuelGas("gas", 0xFFFFED, 1, 4));
        PETROLEUM  = add(fuelGas("petroleum", 0x7CB7C9, 1, 4));
        LPG        = add(of("lpg", 0x4747EA, 1, 3, 1, 20, b ->
                b.addTags(LIQUID).addData(polluteLiquidGas())
                 .addData(new TraitData.Flammable(200_000))
                 .addData(new Combustible(400_000, FuelGrade.HIGH))));
        BIOGAS     = add(fuelGas("biogas", 0xBFD37C, 1, 4));
        BIOFUEL    = add(fuelOil("biofuel", 0xEEF274, 1, 2, FuelGrade.HIGH, 150_000, 400_000, polluteFuel()));
        NITAN      = add(fuelOil("nitan", 0x8018AD, 2, 4, FuelGrade.HIGH, 2_000_000, 5_000_000, polluteFuel()));

        // 裂化产物
        CRACKOIL      = add(fuelOil("crackoil", 0x4A3A2A, 2, 1, FuelGrade.MEDIUM, 80_000, 150_000, polluteOil()));
        COALOIL       = add(crudeOil("coaloil", 0x020202, 2, 1));
        HOTCRACKOIL   = add(hotOil("hotcrackoil", 0x300900, 2, 3, 350));
        NAPHTHA_CRACK = add(fuelOil("naphtha_crack", 0x6B5E3A, 2, 1, FuelGrade.MEDIUM, 100_000, 180_000, polluteOil()));
        DIESEL_CRACK  = add(fuelOil("diesel_crack", 0xD4C9A8, 1, 2, FuelGrade.HIGH, 180_000, 450_000, polluteOil()));
        LIGHTOIL_CRACK= add(fuelOil("lightoil_crack", 0x7A6345, 1, 2, FuelGrade.HIGH, 180_000, 450_000, polluteOil()));

        // 芳烃/不饱和烃
        AROMATICS     = add(of("aromatics", 0x68A09A, 1, 4, 1, 20, b -> b.addTags(LIQUID, VISCOUS).addData(new Flammable(25_000)).addData(polluteGas())));
        UNSATURATEDS  = add(of("unsaturates", 0x628FAE, 1, 4, 1, 20, b -> b.addTag(GASEOUS).gaseous().addData(new Flammable(1_000_000)).addData(polluteGas())));

        // 脱硫产物
        OIL_DS        = add(crudeOil("oil_ds", 0x121212, 2, 1));
        HOTOIL_DS     = add(hotOil("hotoil_ds", 0x3F180F, 2, 3, 350));
        CRACKOIL_DS   = add(crudeOil("crackoil_ds", 0x2A1C11, 2, 1));
        HOTCRACKOIL_DS = add(hotOil("hotcrackoil_ds", 0x3A1A28, 2, 3, 350));
        NAPHTHA_DS    = add(fuelOil("naphtha_ds", 0x63614E, 2, 1, FuelGrade.MEDIUM, 125_000, 200_000, polluteFuel()));
        LIGHTOIL_DS   = add(fuelOil("lightoil_ds", 0x63543E, 1, 2, FuelGrade.MEDIUM, 200_000, 500_000, polluteFuel()));

        // 车用燃料
        GASOLINE        = add(fuelOil("gasoline", 0xD4C878, 1, 3, FuelGrade.HIGH, 250_000, 600_000, polluteOil()));
        GASOLINE_LEADED = add(fuelOilLead("gasoline_leaded", 0xD4B878, 1, 3, FuelGrade.HIGH, 250_000, 600_000, polluteFuelLeaded()));
        COALGAS         = add(fuelOil("coalgas", 0x445772, 1, 2, FuelGrade.MEDIUM, 75_000, 150_000, polluteFuel()));
        COALGAS_LEADED  = add(fuelOilLead("coalgas_leaded", 0x445772, 1, 2, FuelGrade.MEDIUM, 75_000, 250_000, polluteFuelLeaded()));
        PETROIL_LEADED  = add(fuelOilLead("petroil_leaded", 0x54413D, 1, 3, FuelGrade.MEDIUM, 125_000, 300_000, polluteFuelLeaded()));

        // 真空蒸馏
        HEAVYOIL_VACUUM  = add(of("heavyoil_vacuum", 0x131214, 2, 1, 0, 20, b -> b.addTags(LIQUID, VISCOUS).addData(polluteOil())));
        LIGHTOIL_VACUUM  = add(of("lightoil_vacuum", 0x8C8851, 1, 2, 0, 20, b -> b.addTags(LIQUID).addData(polluteFuel())));
        HEATINGOIL_VACUUM= add(of("heatingoil_vacuum", 0x211D06, 2, 2, 0, 20, b -> b.addTags(LIQUID, VISCOUS).addData(polluteOil())));

        // 重整产物
        REFORMATE         = add(of("reformate", 0x835472, 2, 2, 0, 20, b -> b.addTags(LIQUID, VISCOUS).addData(polluteFuel())));
        DIESEL_REFORM     = add(of("diesel_reform", 0xCDC3C6, 1, 2, 0, 20, b -> b.addTags(LIQUID).addData(polluteFuel())));
        DIESEL_CRACK_REFORM = add(of("diesel_crack_reform", 0xCDC3CC, 1, 2, 0, 20, b -> b.addTags(LIQUID).addData(polluteFuel())));
        KEROSENE_REFORM   = add(of("kerosene_reform", 0xFFA5F3, 1, 2, 0, 20, b -> b.addTags(LIQUID).addData(polluteFuel()).addData(new RocketFuel(321, 1_564_000))));
        REFORMGAS         = add(fuelGas("reformgas", 0x6362AE, 1, 4));
        XYLENE            = add(of("xylene", 0x5C4E76, 2, 3, 0, 20, b -> b.addTags(LIQUID, VISCOUS).addData(polluteFuel())));

        // 生物油
        WOODOIL     = add(of("woodoil", 0x847D54, 2, 2, 0, 20, b -> b.addTags(LIQUID, VISCOUS).addData(polluteOil())));
        COALCREOSOTE = add(of("coalcreosote", 0x51694F, 3, 2, 0, 20, b -> b.addTags(LIQUID, VISCOUS).addData(polluteOil())));

        // 焦化
        OIL_COKER    = add(of("oil_coker", 0x001802, 2, 1, 0, 20, b -> b.addTags(LIQUID, VISCOUS).addData(polluteOil())));
        NAPHTHA_COKER = add(of("naphtha_coker", 0x495944, 2, 1, 0, 20, b -> b.addTags(LIQUID, VISCOUS).addData(polluteOil())));
        GAS_COKER    = add(fuelGas("gas_coker", 0xDEF4CA, 1, 4));
        FLUE         = add(of("flue", 0x131313, 1, 4, 1, 25, b -> b.addTag(GASEOUS).gaseous().addData(new Flammable(25_000)).addData(new Polluting(Map.of(), Map.of(PollutionType.SOOT, SOOT_GAS)))));
        
        // ==== 一般气体 ====
        HYDROGEN       = add(simpleGas("hydrogen", 0x7EBFE0, 1, 4));
        OXYGEN         = add(simpleGas("oxygen", 0xFFFFFF, 0, 0));
        NITROGEN       = add(simpleGas("nitrogen", 0xC0C0C0, 0, 0));
        CARBONDIOXIDE  = add(simpleGas("carbon_dioxide", 0xA0A0A0, 1, 0));
        HELIUM3        = add(simpleGas("helium3", 0xD0D0D0, 0, 0));
        HELIUM4        = add(simpleGas("helium4", 0xE0E0E0, 0, 0));
        NEON           = add(simpleGas("neon", 0xFF8080, 0, 0));
        ARGON          = add(simpleGas("argon", 0x8080FF, 0, 0));
        KRYPTON        = add(simpleGas("krypton", 0xC0FFC0, 0, 0));
        XENON          = add(simpleGas("xenon", 0xFFC0FF, 0, 0));

        // 化工气体
        SYNGAS         = add(fuelGas("syngas", 0xC0C090, 1, 4));
        OXYHYDROGEN    = add(combustibleGas("oxyhydrogen", 0xFFE0E0, 0, FuelGrade.HIGH, 25_000, 0));
        SOURGAS        = add(of("sourgas", 0xC9BE0D, 4, 0, 0, 10, b -> b.addTag(GASEOUS).gaseous().addData(new Corrosion(10))));

        // 卤代烃气体
        CHLOROMETHANE = add(of("chloromethane", 0xD3CF9E, 2, 4, 0, 25, b -> b.addTag(GASEOUS).gaseous().addData(new Corrosion(15)).addData(new Flammable(50_000))));
        CHLOROETHANE  = add(simpleGas("chloroethane", 0xBBA9A0, 2, 0));
        CHLORINE       = add(of("chlorine", 0xB0FFB0, 4, 0, 2, 25, b -> b.addTag(GASEOUS).gaseous().addData(new Corrosion(30))));
        FLUORINE       = add(of("fluorine", 0xF0FFF0, 5, 0, 3, 25, b -> b.addTag(GASEOUS).gaseous().addData(new Corrosion(60))));
        BROMINE        = add(of("bromine", 0x8B0000, 4, 0, 2, 25, b -> b.addTags(LIQUID, GASEOUS_AT_ROOM_TEMP).addData(new Corrosion(40))));

        // 氨/肼
        AMMONIA   = add(of("ammonia", 0x00A0F7, 2, 0, 1, 25, b -> b.addTag(GASEOUS).gaseous()));
        HYDRAZINE = add(of("hydrazine", 0x31517D, 2, 3, 2, 25, b -> b.addTags(LIQUID).addData(new Flammable(500_000)).addData(new Combustible(1_250_000, FuelGrade.HIGH)).addData(new Corrosion(30)).addData(new RocketFuel(210, 600_000))));

        // 醇类
        METHANOL = add(of("methanol", 0x88739F, 3, 4, 0, 25, b -> b.addTags(LIQUID, GASEOUS_AT_ROOM_TEMP).gaseous().addData(new Flammable(400_000)).addData(new Combustible(600_000, FuelGrade.HIGH))));
        ETHANOL  = add(fuelOil("ethanol", 0xE0FFFF, 2, 3, FuelGrade.HIGH, 75_000, 200_000, polluteFuel()));

        // 有毒气体
        PHOSGENE         = add(of("phosgene", 0xCFC4A4, 4, 0, 1, 25, b -> b.addTag(GASEOUS).gaseous().addData(new Polluting(Map.of(PollutionType.SOOT, POISON_EXTREME), Map.of()))));
        MUSTARDGAS       = add(of("mustardgas", 0xBAB572, 4, 1, 1, 25, b -> b.addTag(GASEOUS).gaseous().addData(new Polluting(Map.of(PollutionType.SOOT, POISON_EXTREME), Map.of()))));
        DICYANOACETYLENE = add(of("dicyanoacetylene", 0x675A9F, 1, 2, 1, 25, b -> b.addTag(GASEOUS).gaseous().addData(new Flammable(4_000_000))));
        DHC              = add(simpleGas("dhc", 0xD2AFFF, 0, 0));
        LITHYDRO         = add(simpleGas("lithydro", 0xD1CEBE, 0, 0));
        LITHCARBONATE    = add(simpleGas("lithcarbonate", 0xD1CEBE, 0, 0));

        // ==== 酸与腐蚀性 ====
        SULFURIC_ACID = add(corrosiveLiquid("sulfuric_acid", 0xC0C060, 5, 0, 50));
        HCL           = add(corrosiveLiquid("hcl", 0xC0FFC0, 5, 0, 40));
        NITRIC_ACID   = add(corrosiveLiquid("nitric_acid", 0xFFC0C0, 5, 0, 45));
        PEROXIDE         = add(of("peroxide", 0xFFF7AA, 3, 0, 3, 25, b -> b.addTags(LIQUID).addData(new Corrosion(40))));
        SOLVENT          = add(of("solvent", 0xE4E3EF, 2, 3, 0, 25, b -> b.addTags(LIQUID).addData(new Corrosion(30))));
        MINSOL           = add(of("minsol", 0xFADF6A, 3, 0, 3, 25, b -> b.addTags(LIQUID).addData(new Corrosion(10))));
        FRACKSOL         = add(of("fracksol", 0x798A6B, 1, 3, 3, 25, b -> b.addTags(LIQUID, VISCOUS).addData(new Corrosion(15))));
        RADIOSOLVENT     = add(of("radiosolvent", 0xA4D7DD, 3, 3, 0, 25, b -> b.addTags(LIQUID).addData(new Corrosion(50))));
        CCL              = add(of("ccl", 0x0C3B2F, 0, 0, 0, 25, b -> b.addTags(LIQUID).addData(new Corrosion(10))));
        KMnO4            = add(of("kmno4", 0x560046, 4, 0, 0, 25, b -> b.addTags(LIQUID).addData(new Corrosion(15))));
        LYE              = add(corrosiveLiquid("lye", 0xFFECCC, 3, 0, 40));
        VITRIOL          = add(of("vitriol", 0x6E5222, 2, 0, 1, 25, b -> b.addTags(LIQUID, VISCOUS)));

        // 氯碱化工
        CHLOROCALCITE_SOLUTION = add(of("chlorocalcite_solution", 0x808080, 0, 0, 0, 25, b -> b.addTags(LIQUID, NO_CONTAINER).addData(new Corrosion(60))));
        CHLOROCALCITE_MIX      = add(of("chlorocalcite_mix", 0x808080, 0, 0, 0, 25, b -> b.addTags(LIQUID, NO_CONTAINER).addData(new Corrosion(60))));
        CHLOROCALCITE_CLEANED  = add(of("chlorocalcite_cleaned", 0x808080, 0, 0, 0, 25, b -> b.addTags(LIQUID, NO_CONTAINER).addData(new Corrosion(60))));
        POTASSIUM_CHLORIDE     = add(of("potassium_chloride", 0x808080, 0, 0, 0, 25, b -> b.addTags(LIQUID, NO_CONTAINER).addData(new Corrosion(60))));
        CALCIUM_CHLORIDE       = add(of("calcium_chloride", 0x808080, 0, 0, 0, 25, b -> b.addTags(LIQUID, NO_CONTAINER).addData(new Corrosion(60))));
        CALCIUM_SOLUTION       = add(of("calcium_solution", 0x808080, 0, 0, 0, 25, b -> b.addTags(LIQUID, NO_CONTAINER).addData(new Corrosion(60))));

        // 铝土
        BAUXITE_SOLUTION = add(of("bauxite_solution", 0xE2560F, 3, 0, 3, 25, b -> b.addTags(LIQUID, VISCOUS).addData(new Corrosion(40))));
        SODIUM_ALUMINATE = add(of("sodium_aluminate", 0xFFD191, 3, 0, 1, 25, b -> b.addTags(LIQUID).addData(new Corrosion(30))));
        ALUMINA          = add(simpleLiquid("alumina", 0xDDFFFF, 0, 0));
        AQUEOUS_NICKEL   = add(simpleLiquid("aqueous_nickel", 0xDACEBA, 0, 0));
        AQUEOUS_COPPER   = add(of("aqueous_copper", 0x4CC2A2, 0, 0, 0, 25, b -> b.addTags(LIQUID, VISCOUS)));
        COPPERSULFATE    = add(of("coppersulfate", 0x55E5CF, 0, 0, 0, 25, b -> b.addTags(LIQUID, VISCOUS)));

        // WATZ
        WATZ     = add(of("watz", 0x86653E, 4, 0, 3, 25, b -> b.addTags(LIQUID, VISCOUS).addData(new Corrosion(60)).addData(new VentRadiation(0.1F)).addData(new Polluting(Map.of(PollutionType.SOOT, POISON_EXTREME), Map.of()))));
        GAS_WATZ = add(of("gas_watz", 0x86653E, 4, 0, 3, 2500, b -> b.addTags(GASEOUS, NO_CONTAINER).gaseous().addData(new Polluting(Map.of(PollutionType.SOOT, POISON_EXTREME), Map.of())).addData(new RocketFuel(1200, 700_000))));
        CRYOGEL  = add(of("cryogel", 0x32FFFF, 2, 0, 0, -170, b -> b.addTags(LIQUID, VISCOUS)));

        // 溴化物
        URANIUM_BROMIDE  = add(of("uranium_bromide", 0xD1CEBE, 0, 0, 0, 200, b -> b.addTags(LIQUID, VISCOUS).addData(new Corrosion(65)).addData(new VentRadiation(0.1F))));
        THORIUM_BROMIDE  = add(of("thorium_bromide", 0x7A5542, 0, 0, 0, 200, b -> b.addTags(LIQUID, VISCOUS).addData(new Corrosion(65)).addData(new VentRadiation(0.1F))));
        GASEOUS_URANIUM_BROMIDE    = add(gaseousBromide("gaseous_uranium_bromide", 0xD1CEBE, 2500, 1500));
        GASEOUS_PLUTONIUM_BROMIDE  = add(gaseousBromide("gaseous_plutonium_bromide", 0x4C4C4C, 2600, 2000));
        GASEOUS_SCHRABIDIUM_BROMIDE = add(gaseousBromide("gaseous_schrabidium_bromide", 0x006B6B, 3000, 3000));
        GASEOUS_THORIUM_BROMIDE    = add(gaseousBromide("gaseous_thorium_bromide", 0x7A5542, 2300, 1300));

        // 全氟甲基
        PERFLUOROMETHYL      = add(of("perfluoromethyl", 0xBDC8DC, 1, 0, 1, 15, b -> b.addTags(LIQUID)));
        PERFLUOROMETHYL_COLD = add(of("perfluoromethyl_cold", 0x99DADE, 1, 0, 1, -150, b -> b.addTags(LIQUID)));
        PERFLUOROMETHYL_HOT  = add(of("perfluoromethyl_hot", 0xB899DE, 1, 0, 1, 250, b -> b.addTags(LIQUID)));

        // 废物
        WASTEFLUID = add(of("wastefluid", 0x544400, 2, 0, 1, 25, b -> b.addTags(LIQUID, VISCOUS, NO_CONTAINER).addData(new VentRadiation(0.5F))));
        WASTEGAS   = add(of("wastegas", 0xB8B8B8, 2, 0, 1, 25, b -> b.addTags(GASEOUS, NO_CONTAINER).gaseous().addData(new VentRadiation(0.5F)).addData(new RocketFuel(900, 700_000))));
        REDMUD     = add(of("redmud", 0xD85638, 3, 0, 4, 25, b -> b.addTags(LIQUID, VISCOUS, REQUIRES_LEAD_CONTAINER).addData(new Corrosion(60)).addData(new Flammable(1_000)).addData(new Polluting(Map.of(PollutionType.SOOT, POISON_EXTREME), Map.of()))));
        HTCO4      = add(of("htco4", 0x675454, 1, 3, 0, 25, b -> b.addTags(LIQUID).addData(new Corrosion(10)).addData(new VentRadiation(0.5F))));
        HGAS       = add(of("hgas", 0x999368, 0, 0, 0, 25, b -> b.addTag(GASEOUS).gaseous().addData(new Corrosion(120))));
        
        // ==== 核工业 ====
        UF6        = add(of("uf6", 0xD1CEBE, 4, 0, 2, 25, b -> b.addTag(GASEOUS).gaseous().addData(new VentRadiation(0.2F)).addData(new Corrosion(15))));
        PUF6       = add(of("puf6", 0x4C4C4C, 4, 0, 4, 25, b -> b.addTag(GASEOUS).gaseous().addData(new VentRadiation(0.1F)).addData(new Corrosion(15))));
        SAS3       = add(of("sas3", 0x4FFFFC, 5, 0, 4, 25, b -> b.addTags(LIQUID).addData(new VentRadiation(1.0F)).addData(new Corrosion(30))));
        SCHRABIDIC = add(of("schrabidic", 0x006B6B, 5, 0, 5, 25, b -> b.addTags(LIQUID).addData(new VentRadiation(1.0F)).addData(new Corrosion(75))));
        AMAT       = add(of("amat", 0x010101, 5, 0, 5, 25, b -> b.addTags(GASEOUS, ANTIMATTER).gaseous()));
        ASCHRAB    = add(of("aschrab", 0x020202, 5, 0, 5, 25, b -> b.addTags(LIQUID, ANTIMATTER)));

        // ==== 金属熔液 ====
        MERCURY   = add(of("mercury", 0xC0C0C0, 4, 0, 0, 25, b -> b.addTags(LIQUID, VISCOUS)));
        LEAD      = add(hotMetal("lead", 0x606080, 328));
        LEAD_HOT  = add(hotMetal("lead_hot", 0xFF4040, 500));
        SODIUM    = add(hotMetal("sodium", 0xC0C0FF, 98));
        SODIUM_HOT= add(hotMetal("sodium_hot", 0xFFA0A0, 500));

        THORIUM_SALT          = add(hotCorrosive("thorium_salt", 0x7A5542, 2, 800, 65));
        THORIUM_SALT_HOT      = add(hotCorrosive("thorium_salt_hot", 0x3E3627, 2, 1600, 65));
        THORIUM_SALT_DEPLETED = add(hotCorrosive("thorium_salt_depleted", 0x302D1C, 2, 800, 65));

        // ==== 等离子 ====
        PLASMA_DT  = add(plasma("plasma_dt", 0x00FFFF));
        PLASMA_HD  = add(plasma("plasma_hd", 0xFFFF00));
        PLASMA_HT  = add(plasma("plasma_ht", 0xFF00FF));
        PLASMA_DH3 = add(plasma("plasma_dh3", 0x00FF00));
        PLASMA_XM  = add(plasma("plasma_xm", 0xFF8800));
        PLASMA_BF  = add(plasma("plasma_bf", 0x8000FF));

        BALEFIRE = add(of("balefire", 0x28E02E, 4, 4, 3, 1500, b -> b.addTags(LIQUID, VISCOUS).lightLevel(12).addData(new Corrosion(50)).addData(new Flammable(1_000_000)).addData(new Combustible(2_500_000, FuelGrade.HIGH)).addData(polluteFuel())));
        PAIN     = add(of("pain", 0x938541, 2, 0, 1, 300, b -> b.addTags(LIQUID, VISCOUS).addData(new Corrosion(30))));
        DEATH    = add(of("death", 0x717A88, 2, 0, 1, 300, b -> b.addTags(LIQUID, VISCOUS, REQUIRES_LEAD_CONTAINER).addData(new Corrosion(80))));
        SALIENT  = add(of("salient", 0x457F2D, 0, 0, 0, 25, b -> b.addTags(LIQUID, VISCOUS, DELICIOUS)));

        NMASS         = add(of("nmass", 0x53A9F4, 1, 2, 0, 25, b -> b.addTags(LIQUID).addData(new Corrosion(10)).addData(new VentRadiation(0.04F))));
        NMASSTETRANOL = add(of("nmastetranol", 0xF1DB0F, 1, 3, 0, 25, b -> b.addTags(LIQUID).addData(new Flammable(1_000_000)).addData(new Corrosion(70)).addData(new VentRadiation(0.01F))));
        STELLAR_FLUX  = add(of("stellar_flux", 0xE300FF, 0, 4, 4, 25, b -> b.addTags(GASEOUS, ANTIMATTER).gaseous()));
        FULLERENE     = add(of("fullerene", 0xFF7FED, 3, 3, 3, 25, b -> b.addTags(LIQUID).addData(new Corrosion(65)).addData(new Polluting(Map.of(PollutionType.SOOT, POISON_MINOR), Map.of()))));

        IONGEL      = add(of("iongel", 0xB8FFFF, 1, 0, 4, 25, b -> b.addTags(LIQUID, VISCOUS)));
        ELBOWGREASE = add(of("elbowgrease", 0xCBC433, 1, 3, 0, 25, b -> b.addTags(LIQUID).addData(new Flammable(600_000))));

        COLLOID     = add(of("colloid", 0x787878, 0, 0, 0, 25, b -> b.addTags(LIQUID, VISCOUS)));
        BRINE       = add(of("brine", 0xD1A73E, 3, 3, 3, 25, b -> b.addTags(LIQUID, VISCOUS)));
        CONGLOMERA  = add(of("conglomera", 0x364D47, 0, 0, 2, 25, b -> b.addTags(LIQUID, VISCOUS)));
        SEEDSLURRY  = add(of("seedslurry", 0x7CC35E, 0, 0, 0, 25, b -> b.addTags(LIQUID, VISCOUS)));
        SLOP        = add(of("slop", 0x929D45, 0, 0, 0, 25, b -> b.addTags(LIQUID, VISCOUS)));

        // ======== 蒸汽/水 ========
        HEAVYWATER          = add(simpleLiquid("heavywater", 0x00A0B0, 1, 0));
        HEAVYWATER_HOT      = add(of("heavywater_hot", 0x4D007B, 1, 0, 0, 600, b -> b.addTags(LIQUID, VISCOUS)));
        SUPERHEATED_HYDROGEN = add(of("superheated_hydrogen", 0xE39393, 0, 0, 0, 2200, b -> b.addTags(GASEOUS, NO_CONTAINER).gaseous().addData(new RocketFuel(900, 700_000))));
        
        // ==== 外星大气 ====
        EARTHAIR = add(simpleGas("earthair", 0xC0D0FF, 0, 0));
        EVEAIR   = add(simpleGas("eveair", 0x35204A, 2, 0));
        DUNAAIR  = add(simpleGas("dunaair", 0xC05040, 1, 0));
        TEKTOAIR = add(simpleGas("tektoair", 0x40C860, 3, 0));
        JOOLGAS  = add(simpleGas("joolgas", 0x75AD50, 3, 0));
        SARNUSGAS  = add(simpleGas("sarnusgas", 0xE47D5C, 0, 0));
        UGAS       = add(simpleGas("ugas", 0x718C9A, 0, 0));
        NGAS       = add(simpleGas("ngas", 0xA37BA3, 0, 0));

        // ==== 杂项 ====
        BLOOD          = add(of("blood", 0xB22424, 0, 0, 0, 37, b -> b.addTags(LIQUID, VISCOUS, DELICIOUS)));
        BLOOD_HOT      = add(of("blood_hot", 0xF22419, 3, 0, 0, 666, b -> b.addTags(LIQUID, VISCOUS)));
        BLOODGAS       = add(of("bloodgas", 0x591000, 3, 1, 1, 25, b -> b.addTags(LIQUID).addData(new Flammable(1_000_000)).addData(new Combustible(2_500_000, FuelGrade.AERO))));
        SCUTTERBLOOD   = add(of("scutterblood", 0x6C166C, 0, 0, 0, 25, b -> b.addTags(LIQUID, VISCOUS, DELICIOUS)));

        MUG     = add(of("mug", 0x4B2D28, 0, 0, 0, 25, b -> b.addTags(LIQUID, DELICIOUS)));
        MUG_HOT = add(of("mug_hot", 0x6B2A20, 0, 0, 0, 500, b -> b.addTags(LIQUID, DELICIOUS)));
        COFFEE  = add(of("coffee", 0x57493D, 0, 0, 0, 60, b -> b.addTags(LIQUID, DELICIOUS)));
        TEA     = add(of("tea", 0x76523C, 0, 0, 0, 60, b -> b.addTags(LIQUID, DELICIOUS)));
        HONEY   = add(of("honey", 0xD99A02, 0, 0, 0, 25, b -> b.addTags(LIQUID, DELICIOUS)));
        MILK    = add(of("milk", 0xCFCFCF, 0, 0, 0, 5, b -> b.addTags(LIQUID, DELICIOUS)));
        SMILK   = add(of("smilk", 0xF5DEE4, 0, 0, 0, 5, b -> b.addTags(LIQUID, DELICIOUS)));
        EMILK   = add(of("emilk", 0xCFCFCF, 0, 0, 0, 5, b -> b.addTags(LIQUID, DELICIOUS)));
        CMILK   = add(of("cmilk", 0xCFCFCF, 0, 0, 0, 5, b -> b.addTags(LIQUID, DELICIOUS)));
        CREAM   = add(of("cream", 0xCFCFCF, 0, 0, 0, 5, b -> b.addTags(LIQUID, DELICIOUS)));

        EGG         = add(simpleLiquid("egg", 0xD2C273, 0, 0));
        CHOLESTEROL = add(simpleLiquid("cholesterol", 0xD6D2BD, 0, 0));
        ESTRADIOL   = add(simpleLiquid("estradiol", 0xCDD5D8, 0, 0));

        FISHOIL      = add(of("fishoil", 0x4B4A45, 0, 1, 0, 25, b -> b.addTags(LIQUID).addData(polluteFuel())));
        SUNFLOWEROIL = add(of("sunfloweroil", 0xCBAD45, 0, 1, 0, 25, b -> b.addTags(LIQUID).addData(polluteFuel())));

        XPJUICE   = add(of("xpjuice", 0xBBFF09, 0, 0, 0, 25, b -> b.addTags(LIQUID, VISCOUS)));
        ENDERJUICE = add(simpleLiquid("enderjuice", 0x127766, 0, 0));

        NITROGLYCERIN = add(simpleLiquid("nitroglycerin", 0x92ACA6, 0, 4));

        SMOKE        = add(of("smoke", 0x808080, 0, 0, 0, 25, b -> b.addTags(GASEOUS, NO_CONTAINER).gaseous()));
        SMOKE_LEADED = add(of("smoke_leaded", 0x808080, 0, 0, 0, 25, b -> b.addTags(GASEOUS, NO_CONTAINER).gaseous()));
        SMOKE_POISON = add(of("smoke_poison", 0x808080, 0, 0, 0, 25, b -> b.addTags(GASEOUS, NO_CONTAINER).gaseous()));

        PHEROMONE   = add(of("pheromone", 0x5FA6E8, 0, 0, 0, 25, b -> b.addTags(LIQUID).addData(new Pheromone(1))));
        PHEROMONE_M = add(of("pheromone_m", 0x48C9B0, 0, 0, 0, 25, b -> b.addTags(LIQUID).addData(new Pheromone(2))));

        IRRADIATED_WATER = add(of("irradiated_water", 0xA1E038D0, 0, 0, 0, 20, b -> b.addData(new VentRadiation(0.1f)).stillTexture(HBM.rl("block/fluid/irradiated_water_still")).flowingTexture(HBM.rl("block/fluid/irradiated_water_flow"))));
        IRRADIATED_POLLUTED = add(of("irradiated_polluted", 0xA1E038D0, 0, 0, 0, 20, b -> b.addData(new VentRadiation(0.5f)).stillTexture(HBM.rl("block/fluid/irradiated_polluted_still")).flowingTexture(HBM.rl("block/fluid/irradiated_polluted_flow"))));

        // 建材/化工原料
        CONCRETE    = add(simpleLiquid("concrete", 0xA2A2A2, 0, 0));
        VINYL       = add(simpleLiquid("vinyl", 0xA2A2A2, 0, 0));
        TCRUDE      = add(simpleLiquid("tcrude", 0x051914, 0, 0));
        CBENZ       = add(simpleLiquid("cbenz", 0x91C6BB, 0, 0));
        HALOLIGHT   = add(simpleLiquid("halolight", 0xB6F9CF, 0, 0));
        POLYTHYLENE = add(of("polyethylene", 0x35302E, 1, 2, 0, 25, b -> b.addTags(LIQUID).addData(new Flammable(50_000))));
    }

    // ================================================================
    // Helper 方法 —— 每种 Trait 组合一个
    // ================================================================

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    /** 基础流体 */
    private static HbmFluidType of(String name, int color, int tox, int react, int symbol,
                                   int temp, Consumer<FluidTypeBuilder> extra) {
        var b = HbmFluidType.builder()
                .name(name).color(color).toxicity(tox).reactivity(react).temperature(temp)
                .tint(color);
        extra.accept(b);
        var t = b.build();
//        ALL.add(t);
        return t;
    }
    /** 简单标签液体 */
    private static HbmFluidType simpleLiquid(String name, int color, int tox, int react) {
        return of(name, color, tox, react, 0, 25, b -> b.addTag(LIQUID));
    }

    /** 惰性气体（常温气态，不着色） */
    private static HbmFluidType simpleGas(String name, int color, int tox, int react, IFluidTrait ... traits) {
        return of(name, color, tox, react, 0, 25, b ->
                b.addTag(GASEOUS).gaseous().addData(traits));
    }

    /** 燃料气体（可燃气体） */
    private static HbmFluidType fuelGas(String name, int color, int tox, int react, IFluidTrait ... traits) {
        return of(name, color, tox, react, 1, 25, b ->
                b.addTag(GASEOUS).gaseous()
                 .addData(new Flammable(25_000))
                 .addData(polluteGas()).addData(traits));
    }

    /** 可燃气体（有热值和燃烧等级） */
    private static HbmFluidType combustibleGas(String name, int color, int tox,
                                                FuelGrade grade, long flameHeat, long combEnergy, IFluidTrait ... traits) {
        return of(name, color, tox, 4, 0, 25, b -> {
            b.addTag(GASEOUS).gaseous();
            if (flameHeat > 0) b.addData(new Flammable(flameHeat));
            if (combEnergy > 0) b.addData(new Combustible(combEnergy, grade));
            b.addData(polluteGas()).addData(traits);
        });
    }

    /** 热气体（蒸汽类） */
    private static HbmFluidType hotGas(String name, int color, int tox, int temp) {
        return of(name, color, tox, 0, 0, temp, b ->
                b.addTags(GASEOUS, UNSIPHONABLE));
    }

    /** 原油（粘稠可燃液体） */
    private static HbmFluidType crudeOil(String name, int color, int tox, int react) {
        return of(name, color, tox, react, 0, 25, b ->
                b.addTags(LIQUID, VISCOUS)
                 .addData(new Flammable(10_000))
                 .addData(polluteOil()));
    }

    /** 热原油 */
    private static HbmFluidType hotOil(String name, int color, int tox, int react, int temp) {
        return of(name, color, tox, react, 0, temp, b ->
                b.addTags(LIQUID, VISCOUS).addData(polluteOil()));
    }

    /** 燃料油 */
    private static HbmFluidType fuelOil(String name, int color, int tox, int react,
                                         FuelGrade grade, long flameHeat, long combEnergy, IFluidTrait ... traits) {
        return of(name, color, tox, react, 0, 25, b -> {
            b.addTags(LIQUID, VISCOUS);
            if (flameHeat > 0) b.addData(new Flammable(flameHeat));
            if (combEnergy > 0) b.addData(new Combustible(combEnergy, grade));
            b.addData(polluteOil()).addData(traits);
        });
    }

    private static HbmFluidType fuelOilLead(String name, int color, int tox, int react,
                                        FuelGrade grade, long flameHeat, long combEnergy, IFluidTrait ... traits) {
        return of(name, color, tox, react, 0, 25, b -> {
            b.addTags(LIQUID, VISCOUS, LEADED_FUEL);
            if (flameHeat > 0) b.addData(new Flammable(flameHeat));
            if (combEnergy > 0) b.addData(new Combustible(combEnergy, grade));
            b.addData(polluteOil()).addData(traits);
        });
    }

    /** 腐蚀性液体 */
    private static HbmFluidType corrosiveLiquid(String name, int color, int tox, int react, int rating) {
        return of(name, color, tox, react, 2, 25, b ->
                b.addTags(LIQUID).addData(new Corrosion(rating)));
    }

    /** 热腐蚀性液体 */
    private static HbmFluidType hotCorrosive(String name, int color, int tox, int temp, int rating) {
        return of(name, color, tox, 0, 3, temp, b -> b.addTags(LIQUID, VISCOUS).addData(new Corrosion(rating)));
    }

    /** 热金属熔液 */
    private static HbmFluidType hotMetal(String name, int color, int temp) {
        return of(name, color, 2, 0, 0, temp, b ->
                b.addTags(LIQUID, VISCOUS).lightLevel(temp >= 500 ? 10 : 3));
    }

    /** 等离子体 */
    private static HbmFluidType plasma(String name, int color) {
        return of(name, color, 4, 0, 4, 10000, b ->
                b.addTag(PLASMA).lightLevel(15).gaseous());
    }

    /** 气态溴化物 */
    private static HbmFluidType gaseousBromide(String name, int color, int temp, int rocketIsp) {
        return of(name, color, 0, 0, 0, temp, b ->
                b.addTags(GASEOUS, NO_CONTAINER).gaseous().addData(new RocketFuel(rocketIsp, 700_000)));
    }

    // ================================================================
    // 污染预设
    // ================================================================

    private static final float SOOT_CRUDE  = Pollution.POISON_PER_SECOND * 0.1F;
    private static final float SOOT_FUEL   = Pollution.POISON_PER_SECOND * 0.025F;
    private static final float SOOT_GAS    = Pollution.POISON_PER_SECOND * 0.005F;
    private static final float HEAVY_METAL = Pollution.POISON_PER_SECOND * 0.025F;
    private static final float POISON_OIL  = Pollution.POISON_PER_SECOND * 0.0025F;
    public static final float POISON_EXTREME = Pollution.POISON_PER_SECOND * 0.025F;
    public static final float POISON_MINOR = Pollution.POISON_PER_SECOND * 0.001F;

    private static Polluting polluteOil()    { return new Polluting(Map.of(PollutionType.SOOT, SOOT_CRUDE), Map.of(PollutionType.SOOT, SOOT_CRUDE * 4)); }
    private static Polluting polluteFuel()   { return new Polluting(Map.of(PollutionType.SOOT, SOOT_FUEL), Map.of(PollutionType.SOOT, SOOT_FUEL * 4)); }
    private static Polluting polluteGas()    { return new Polluting(Map.of(PollutionType.SOOT, SOOT_GAS), Map.of(PollutionType.SOOT, SOOT_GAS * 4)); }
    private static Polluting polluteLiquidGas() { return new Polluting(Map.of(PollutionType.SOOT, SOOT_GAS * 2), Map.of(PollutionType.SOOT, SOOT_GAS * 8)); }
    private static Polluting polluteFuelLeaded() { return new Polluting(
            Map.of(PollutionType.SOOT, SOOT_FUEL),
            Map.of(PollutionType.SOOT, SOOT_FUEL * 4, PollutionType.HEAVY_METAL, HEAVY_METAL)); }

    private static HeatingType.Efficiency eff(HeatingType t, double f)  { return new HeatingType.Efficiency(t, f); }

    /** 获取所有已注册流体 */
    public static List<FluidHolder> all() { return Collections.unmodifiableList(ALL); }

    /** 按名称查询 */
    public static HbmFluidType byName(String name) {
        for (var t : ALL) if (t.toString().contains(name)) return t.type.get();
        return WATER.type.get(); // fallback
    }

    // ==== 注册相关内容 ====
    private static FluidHolder add(HbmFluidType type){
        FluidHolder fluidHolder = new FluidHolder(type);
        ALL.add(fluidHolder);
        return fluidHolder;
    }

    public static void register(IEventBus modEventBus){
        init();
        for (FluidHolder fluidHolder : ALL) {
            fluidHolder.registerFluid();
        }
        FLUIDS.register(modEventBus);
        FLUID_TYPES.register(modEventBus);
    }

//    public static void registerFluid(IEventBus modEventBus){
//        for (FluidHolder fluidHolder : ALL) {
//            fluidHolder.registerFluid();
//        }
//        FLUIDS.register(modEventBus);
//    }

    public static void registerBlock(DeferredRegister<Block> BLOCKS){
        for (FluidHolder fluidHolder : ALL) {
            fluidHolder.registerBlock(BLOCKS);
        }
    }

    public static void registerItem(DeferredRegister<Item> ITEMS){
        for (FluidHolder fluidHolder : ALL) {
            fluidHolder.registerContainer(ITEMS);
        }
    }

    public static class FluidHolder
    {
        RegistryObject<? extends HbmFluidType> type;
        RegistryObject<? extends ForgeFlowingFluid.Source> source;
        RegistryObject<? extends ForgeFlowingFluid.Flowing> flowing;
        RegistryObject<? extends LiquidBlock> block;
        RegistryObject<? extends Item> bucket;
        private HbmFluidType fluidType;

        public RegistryObject<? extends HbmFluidType> type() { return this.type; }
        public RegistryObject<? extends ForgeFlowingFluid.Source> source() { return this.source; }
        public RegistryObject<? extends ForgeFlowingFluid.Flowing> flowing() { return this.flowing; }
        public RegistryObject<? extends LiquidBlock> block() { return this.block; }
        public RegistryObject<? extends Item> bucket() { return this.bucket; }

        public FluidHolder(HbmFluidType type){
            this.fluidType = type;
            this.type = HBMFluids.FLUID_TYPES.register(type.getName(), () -> type);
        }

        public void registerFluid(){
            String name = fluidType.getName();
            this.source = FLUIDS.register(name, () -> new ForgeFlowingFluid.Source(fluidType.flowProperties));
            this.flowing = FLUIDS.register(name+"_flow", () -> new ForgeFlowingFluid.Flowing(fluidType.flowProperties));
            fluidType.flowProperties = new ForgeFlowingFluid.Properties(type, source, flowing);
        }

        public void registerBlock(DeferredRegister<Block> BLOCKS){
            // 加_fluid避免出现和其他方块重名
            this.block = BLOCKS.register(fluidType.getName() + "_fluid", () -> new LiquidBlock(source, BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable()));
            fluidType.flowProperties.block(block);
        }

        public void registerContainer(DeferredRegister<Item> ITEMS) {
            this.bucket = new WrappedRegistryBuilder.WrappedItemRegistryBuilder("bucket_" + fluidType.getName(), () -> new FluidBucketItem(source, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)))
                    .loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).tab(ModTabs.PARTS.getKey()).model(HBMKey.MODEL_ITEM_MULTI_LAYER, HBM.rl("bucket_body"), HBM.rl("bucket_content"))
                    .color((ItemStack stack, int tintIndex) -> tintIndex == 1 ? fluidType.getHbmColor() : -1)
                    .build();
            fluidType.flowProperties.bucket(bucket);
        }

    }
}
