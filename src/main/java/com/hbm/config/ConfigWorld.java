package com.hbm.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 航空版特有的配置，会插入到hbm原本的配置中。
 * */
public class ConfigWorld {
    public static ForgeConfigSpec.ConfigValue<Boolean> overworldOre;
    public static ForgeConfigSpec.ConfigValue<Boolean> netherOre;
    public static ForgeConfigSpec.ConfigValue<Boolean> endOre;

    public static ForgeConfigSpec.ConfigValue<Integer> ironSpawn;

    public static ForgeConfigSpec.ConfigValue<Integer> uraniumSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> thoriumSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> titaniumSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> sulfurSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> aluminiumSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> copperSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> nickelSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> zincSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> mineralSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> fluoriteSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> niterSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> tungstenSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> leadSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> berylliumSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> ligniteSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> asbestosSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> rareSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> lithiumSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> cinnebarSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> gassshaleSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> gasbubbleSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> explosivebubbleSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> cobaltSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> oilSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockOilSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> meteoriteSpawn;

    // Space oils and ores
    public static ForgeConfigSpec.ConfigValue<Integer> dunaOilSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> eveGasSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> laytheOilSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> munBrineSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> minmusBrineSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> ikeBrineSpawn;

    public static ForgeConfigSpec.ConfigValue<Integer> bedrockOilPerDeposit;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockGasPerDepositMin;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockGasPerDepositMax;

    public static ForgeConfigSpec.ConfigValue<Integer> earthOilPerDeposit;
    public static ForgeConfigSpec.ConfigValue<Integer> earthGasPerDepositMin;
    public static ForgeConfigSpec.ConfigValue<Integer> earthGasPerDepositMax;
    public static ForgeConfigSpec.ConfigValue<Double> earthOilDrainChance;

    public static ForgeConfigSpec.ConfigValue<Integer> dunaOilPerDeposit;
    public static ForgeConfigSpec.ConfigValue<Integer> dunaGasPerDepositMin;
    public static ForgeConfigSpec.ConfigValue<Integer> dunaGasPerDepositMax;
    public static ForgeConfigSpec.ConfigValue<Double> dunaOilDrainChance;

    public static ForgeConfigSpec.ConfigValue<Integer> laytheOilPerDeposit;
    public static ForgeConfigSpec.ConfigValue<Integer> laytheGasPerDepositMin;
    public static ForgeConfigSpec.ConfigValue<Integer> laytheGasPerDepositMax;
    public static ForgeConfigSpec.ConfigValue<Double> laytheOilDrainChance;

    public static ForgeConfigSpec.ConfigValue<Integer> tektoOilSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> tektoOilPerDeposit;
    public static ForgeConfigSpec.ConfigValue<Integer> tektoGasPerDepositMin;
    public static ForgeConfigSpec.ConfigValue<Integer> tektoGasPerDepositMax;
    public static ForgeConfigSpec.ConfigValue<Double> tektoOilDrainChance;

    public static ForgeConfigSpec.ConfigValue<Integer> tektoBedrockOilSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> tektoBedrockOilPerDeposit;
    public static ForgeConfigSpec.ConfigValue<Integer> tektoBedrockGasPerDepositMin;
    public static ForgeConfigSpec.ConfigValue<Integer> tektoBedrockGasPerDepositMax;

    public static ForgeConfigSpec.ConfigValue<Integer> eveGasPerDeposit;
    public static ForgeConfigSpec.ConfigValue<Integer> evePetPerDepositMin;
    public static ForgeConfigSpec.ConfigValue<Integer> evePetPerDepositMax;
    public static ForgeConfigSpec.ConfigValue<Double> eveGasDrainChance;

    public static ForgeConfigSpec.ConfigValue<Integer> munBrinePerDeposit;
    public static ForgeConfigSpec.ConfigValue<Double> munBrineDrainChance;

    public static ForgeConfigSpec.ConfigValue<Integer> minmusBrinePerDeposit;
    public static ForgeConfigSpec.ConfigValue<Double> minmusBrineDrainChance;

    public static ForgeConfigSpec.ConfigValue<Integer> ikeBrinePerDeposit;
    public static ForgeConfigSpec.ConfigValue<Double> ikeBrineDrainChance;


    public static ForgeConfigSpec.ConfigValue<Boolean> newBedrockOres;

    public static ForgeConfigSpec.ConfigValue<Integer> bedrockIronSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockCopperSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockBoraxSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockChlorocalciteSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockAsbestosSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockNiobiumSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockNeodymiumSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockTitaniumSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockTungstenSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockGoldSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockUraniumSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockThoriumSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockCoalSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockNiterSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockFluoriteSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockRedstoneSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockBismuthSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockCadmiumSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockRareEarthSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockBauxiteSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockEmeraldSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockGlowstoneSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockPhosphorusSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> bedrockQuartzSpawn;

    public static ForgeConfigSpec.ConfigValue<Integer> ironClusterSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> titaniumClusterSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> aluminiumClusterSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> copperClusterSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> alexandriteSpawn;

    public static ForgeConfigSpec.ConfigValue<Integer> limestoneSpawn;

    public static ForgeConfigSpec.ConfigValue<Integer> netherUraniumuSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> netherTungstenSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> netherSulfurSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> netherPhosphorusSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> netherCoalSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> netherPlutoniumSpawn;
    public static ForgeConfigSpec.ConfigValue<Integer> netherCobaltSpawn;

    public static ForgeConfigSpec.ConfigValue<Integer> endTikiteSpawn;

    public static ForgeConfigSpec.ConfigValue<Boolean> enableHematite;
    public static ForgeConfigSpec.ConfigValue<Boolean> enableMalachite;
    public static ForgeConfigSpec.ConfigValue<Boolean> enableBauxite;

    public static ForgeConfigSpec.ConfigValue<Boolean> enableSulfurCave;
    public static ForgeConfigSpec.ConfigValue<Boolean> enableAsbestosCave;

    //	public static ForgeConfigSpec.ConfigValue<Integer> radioStructure;
    public static ForgeConfigSpec.ConfigValue<Integer> antennaStructure;
    public static ForgeConfigSpec.ConfigValue<Integer> atomStructure;
    public static ForgeConfigSpec.ConfigValue<Integer> dungeonStructure;
    public static ForgeConfigSpec.ConfigValue<Integer> relayStructure;
    public static ForgeConfigSpec.ConfigValue<Integer> satelliteStructure;
    //	public static ForgeConfigSpec.ConfigValue<Integer> factoryStructure;
    public static ForgeConfigSpec.ConfigValue<Integer> dudStructure;
    public static ForgeConfigSpec.ConfigValue<Integer> spaceshipStructure;
    public static ForgeConfigSpec.ConfigValue<Integer> barrelStructure;
    public static ForgeConfigSpec.ConfigValue<Integer> geyserWater;
    public static ForgeConfigSpec.ConfigValue<Integer> geyserChlorine;
    public static ForgeConfigSpec.ConfigValue<Integer> geyserVapor;
    public static ForgeConfigSpec.ConfigValue<Integer> capsuleStructure;
    public static ForgeConfigSpec.ConfigValue<Integer> arcticStructure;
    public static ForgeConfigSpec.ConfigValue<Integer> jungleStructure;
    public static ForgeConfigSpec.ConfigValue<Integer> pyramidStructure;

    public static ForgeConfigSpec.ConfigValue<Integer> broadcaster;
    public static ForgeConfigSpec.ConfigValue<Integer> minefreq;
    public static ForgeConfigSpec.ConfigValue<Integer> radfreq;
    public static ForgeConfigSpec.ConfigValue<Integer> vaultfreq;

    public static ForgeConfigSpec.ConfigValue<Boolean> enableMeteorStrikes;
    public static ForgeConfigSpec.ConfigValue<Boolean> enableMeteorShowers;
    public static ForgeConfigSpec.ConfigValue<Boolean> enableMeteorTails;
    public static ForgeConfigSpec.ConfigValue<Boolean> enableSpecialMeteors;
    public static ForgeConfigSpec.ConfigValue<Integer> meteorStrikeChance;
    public static ForgeConfigSpec.ConfigValue<Integer> meteorShowerChance;
    public static ForgeConfigSpec.ConfigValue<Integer> meteorShowerDuration;

    public static ForgeConfigSpec.ConfigValue<Boolean> enableCraterBiomes;
    public static ForgeConfigSpec.ConfigValue<Integer> craterBiomeId;
    public static ForgeConfigSpec.ConfigValue<Integer> craterBiomeInnerId;
    public static ForgeConfigSpec.ConfigValue<Integer> craterBiomeOuterId;
    public static ForgeConfigSpec.ConfigValue<Double> craterBiomeRad;
    public static ForgeConfigSpec.ConfigValue<Double> craterBiomeInnerRad;
    public static ForgeConfigSpec.ConfigValue<Double> craterBiomeOuterRad;
    public static ForgeConfigSpec.ConfigValue<Double> craterBiomeWaterMult;

    public static void addConfig(final ForgeConfigSpec.Builder builder) {
        builder.push(CommonConfig.CATEGORY_ORES);

        overworldOre = builder.comment("General switch for whether overworld ores should be generated. Does not include special structures like oil.").define("2.D00_overworldOres", true);
        netherOre = builder.comment("General switch for whether nether ores should be generated.").define("2.D01_netherOres", true);
        endOre = builder.comment("General switch for whether end ores should be generated. Does not include special structures like trixite crystals.").define("2.D02_endOres", true);

        uraniumSpawn = builder.comment("Amount of uranium ore veins per chunk").define("2.00_uraniumSpawnrate", 7);
        titaniumSpawn = builder.comment("Amount of titanium ore veins per chunk").define("2.01_titaniumSpawnrate", 8);
        sulfurSpawn = builder.comment("Amount of sulfur ore veins per chunk").define("2.02_sulfurSpawnrate", 5);
        aluminiumSpawn = builder.comment("Amount of aluminium ore veins per chunk").define("2.03_aluminiumSpawnrate", 7);
        copperSpawn = builder.comment("Amount of copper ore veins per chunk").define("2.04_copperSpawnrate", 12);
        fluoriteSpawn = builder.comment("Amount of fluorite ore veins per chunk").define("2.05_fluoriteSpawnrate", 6);
        niterSpawn = builder.comment("Amount of niter ore veins per chunk").define("2.06_niterSpawnrate", 6);
        tungstenSpawn = builder.comment("Amount of tungsten ore veins per chunk").define("2.07_tungstenSpawnrate", 10);
        leadSpawn = builder.comment("Amount of lead ore veins per chunk").define("2.08_leadSpawnrate", 6);
        berylliumSpawn = builder.comment("Amount of beryllium ore veins per chunk").define("2.09_berylliumSpawnrate", 6);
        thoriumSpawn = builder.comment("Amount of thorium ore veins per chunk").define("2.10_thoriumSpawnrate", 7);
        ligniteSpawn = builder.comment("Amount of lignite ore veins per chunk").define("2.11_ligniteSpawnrate", 2);
        asbestosSpawn = builder.comment("Amount of asbestos ore veins per chunk").define("2.12_asbestosSpawnRate", 2);
        lithiumSpawn = builder.comment("Amount of schist lithium ore veins per chunk").define("2.13_lithiumSpawnRate", 6);
        rareSpawn = builder.comment("Amount of rare earth ore veins per chunk").define("2.14_rareEarthSpawnRate", 6);
        gassshaleSpawn = builder.comment("Amount of oil shale veins per chunk").define("2.16_gasShaleSpawnRate", 5);
        gasbubbleSpawn = builder.comment("Spawns a gas bubble every nTH chunk").define("2.17_gasBubbleSpawnRate", 12);
        cinnebarSpawn = builder.comment("Amount of cinnebar ore veins per chunk").define("2.18_cinnebarSpawnRate", 1);
        cobaltSpawn = builder.comment("Amount of cobalt ore veins per chunk").define("2.18_cobaltSpawnRate", 2);
        explosivebubbleSpawn = builder.comment("Spawns an explosive gas bubble every nTH chunk").define("2.19_explosiveBubbleSpawnRate", 0);
        alexandriteSpawn = builder.comment("Spawns an alexandrite vein every nTH chunk").define("2.20_alexandriteSpawnRate", 100);
        oilSpawn = builder.comment("Spawns an oil bubble every nTH chunk").define("2.21_oilSpawnRate", 100);
        bedrockOilSpawn = builder.comment("Spawns a bedrock oil node every nTH chunk").define("2.22_bedrockOilSpawnRate", 200);
        meteoriteSpawn = builder.comment("Spawns a fallen meteorite every nTH chunk").define("2.23_meteoriteSpawnRate", 200);
        nickelSpawn = builder.comment("Amount of nickel ore veins per chunk").define("2.24_nickelSpawnrate", 12);
        zincSpawn = builder.comment("Amount of zinc ore veins per chunk").define("2.25_zincSpawnrate", 8);
        mineralSpawn = builder.comment("Amount of mineral ore veins per chunk").define("2.26_mineralSpawnrate", 4);
        dunaOilSpawn = builder.comment("Spawns an oil bubble every nTH chunk (on Duna)").define("2.27S_oilSpawnRate", 100);
        laytheOilSpawn = builder.comment("Spawns a DS oil bubble every nTH chunk (on Laythe)").define("2.28S_oilSpawnRate", 100);
        eveGasSpawn = builder.comment("Spawns a natural gas bubble every nTH chunk (on Eve)").define("2.29S_gasSpawnRate", 100);
        munBrineSpawn = builder.comment("Spawns a brine bubble every nTH chunk (on Mun)").define("2.30S_brineSpawnRate", 100);
        minmusBrineSpawn = builder.comment("Spawns a brine bubble every nTH chunk (on Minmus)").define("2.31S_brineSpawnRate", 100);
        ikeBrineSpawn = builder.comment("Spawns a brine bubble every nTH chunk (on Ike)").define("2.32S_brineSpawnRate", 100);

        bedrockOilPerDeposit = builder.comment("Oil extracted per bedrock oil block suck").define("2.O00_bedrockOilPerDeposit", 100);
        bedrockGasPerDepositMin = builder.comment("Minimum natural gas extracted per bedrock oil block suck").define("2.O01_bedrockGasPerDepositMin", 10);
        bedrockGasPerDepositMax = builder.comment("Maximum natural gas extracted per bedrock oil block suck").define("2.O02_bedrockGasPerDepositMax", 50);

        earthOilPerDeposit = builder.comment("Oil extracted per Earth oil block suck").define("2.O03_earthOilPerDeposit", 500);
        earthGasPerDepositMin = builder.comment("Minimum natural gas extracted per Earth oil block suck").define("2.O04_earthGasPerDepositMin", 100);
        earthGasPerDepositMax = builder.comment("Maximum natural gas extracted per Earth oil block suck").define("2.O05_earthGasPerDepositMax", 500);
        earthOilDrainChance = builder.comment("Chance for an Earth oil block to become empty on suck").define("2.O06_earthOilDrainChance", 0.5);

        dunaOilPerDeposit = builder.comment("Oil extracted per Duna oil block suck").define("2.O07_dunaOilPerDeposit", 200);
        dunaGasPerDepositMin = builder.comment("Minimum natural gas extracted per Duna oil block suck").define("2.O08_dunaGasPerDepositMin", 100);
        dunaGasPerDepositMax = builder.comment("Maximum natural gas extracted per Duna oil block suck").define("2.O09_dunaGasPerDepositMax", 500);
        dunaOilDrainChance = builder.comment("Chance for a Duna oil block to become empty on suck").define("2.O10_dunaOilDrainChance", 0.1);

        laytheOilPerDeposit = builder.comment("Desulfurized Oil extracted per Laythe oil block suck").define("2.O11_laytheOilPerDeposit", 500);
        laytheGasPerDepositMin = builder.comment("Minimum natural gas extracted per Laythe oil block suck").define("2.O12_laytheGasPerDepositMin", 100);
        laytheGasPerDepositMax = builder.comment("Maximum natural gas extracted per Laythe oil block suck").define("2.O13_laytheGasPerDepositMax", 500);
        laytheOilDrainChance = builder.comment("Chance for a Laythe oil block to become empty on suck").define("2.O14_laytheOilDrainChance", 0.5);

        eveGasPerDeposit = builder.comment("Natural Gas extracted per Eve gas block suck").define("2.O15_eveGasPerDeposit", 500);
        evePetPerDepositMin = builder.comment("Minimum petroleum gas extracted per Eve oil block suck").define("2.O16_evePetPerDepositMin", 20);
        evePetPerDepositMax = builder.comment("Maximum petroleum gas extracted per Eve oil block suck").define("2.O17_evePetPerDepositMax", 100);
        eveGasDrainChance = builder.comment("Chance for an Eve gas block to become empty on suck").define("2.O18_eveGasDrainChance", 0.05);

        munBrinePerDeposit = builder.comment("Brine extracted per Mun brine block suck").define("2.O19_munBrinePerDeposit", 300);
        munBrineDrainChance = builder.comment("Chance for an Mun brine block to become empty on suck").define("2.O20_munBrineDrainChance", 0.05);

        minmusBrinePerDeposit = builder.comment("Brine extracted per Minmus brine block suck").define("2.O21_minmusBrinePerDeposit", 300);
        minmusBrineDrainChance = builder.comment("Chance for an Minmus brine block to become empty on suck").define("2.O22_minmusBrineDrainChance", 0.05);

        ikeBrinePerDeposit = builder.comment("Brine extracted per Ike brine block suck").define("2.O23_ikeBrinePerDeposit", 300);
        ikeBrineDrainChance = builder.comment("Chance for an Ike brine block to become empty on suck").define("2.O24_ikeBrineDrainChance", 0.05);

        tektoOilSpawn = builder.comment("Spawns a Tekto oil bubble every nTH chunk (on Tekto)").define("2.33S_tektoOilSpawnRate", 100);
        tektoOilPerDeposit = builder.comment("Oil extracted per Tekto oil block suck").define("2.O25_tektoOilPerDeposit", 500);
        tektoGasPerDepositMin = builder.comment("Minimum natural gas extracted per Tekto oil block suck").define("2.O26_tektoGasPerDepositMin", 100);
        tektoGasPerDepositMax = builder.comment("Maximum natural gas extracted per Tekto oil block suck").define("2.O27_tektoGasPerDepositMax", 500);
        tektoOilDrainChance = builder.comment("Chance for a Tekto oil block to become empty on suck").define("2.O28_tektoOilDrainChance", 0.05);


        tektoBedrockOilSpawn = builder.comment("Spawns a Tekto bedrock oil bubble every nTH chunk (on Tekto)").define("2.34_tektoBedrockOilSpawnRate", 200);
        tektoBedrockOilPerDeposit = builder.comment("Oil extracted per bedrock oil block suck").define("2.O35_tektoBedrockOilPerDeposit", 100);
        tektoBedrockGasPerDepositMin = builder.comment("Minimum natural gas extracted per bedrock oil block suck").define("2.O36_tektoBedrockGasPerDepositMin", 10);
        tektoBedrockGasPerDepositMax = builder.comment("Maximum natural gas extracted per bedrock oil block suck").define("2.O37_tektoBedrockGasPerDepositMax", 50);


        newBedrockOres = builder.comment("Enables the newer genreric bedrock ores").define("2.NB_newBedrockOres", true);
        bedrockIronSpawn = builder.comment("Spawn weight for iron bedrock ore").define("2.B00_bedrockIronWeight", 100);
        bedrockCopperSpawn = builder.comment("Spawn weight for copper bedrock ore").define("2.B01_bedrockCopperWeight", 200);
        bedrockBoraxSpawn = builder.comment("Spawn weight for borax bedrock ore").define("2.B02_bedrockBoraxWeight", 50);
        bedrockAsbestosSpawn = builder.comment("Spawn weight for asbestos bedrock ore").define("2.B03_bedrockAsbestosWeight", 50);
        bedrockNiobiumSpawn = builder.comment("Spawn weight for niobium bedrock ore").define("2.B04_bedrockNiobiumWeight", 50);
        bedrockTitaniumSpawn = builder.comment("Spawn weight for titanium bedrock ore").define("2.B05_bedrockTitaniumWeight", 100);
        bedrockTungstenSpawn = builder.comment("Spawn weight for tungsten bedrock ore").define("2.B06_bedrockTungstenWeight", 100);
        bedrockGoldSpawn = builder.comment("Spawn weight for gold bedrock ore").define("2.B07_bedrockGoldWeight", 50);
        bedrockUraniumSpawn = builder.comment("Spawn weight for uranium bedrock ore").define("2.B08_bedrockUraniumWeight", 35);
        bedrockThoriumSpawn = builder.comment("Spawn weight for thorium bedrock ore").define("2.B09_bedrockThoriumWeight", 50);
        bedrockCoalSpawn = builder.comment("Spawn weight for coal bedrock ore").define("2.B10_bedrockCoalWeight", 200);
        bedrockNiterSpawn = builder.comment("Spawn weight for niter bedrock ore").define("2.B11_bedrockNiterWeight", 50);
        bedrockFluoriteSpawn = builder.comment("Spawn weight for fluorite bedrock ore").define("2.B12_bedrockFluoriteWeight", 50);
        bedrockRedstoneSpawn = builder.comment("Spawn weight for redstone bedrock ore").define("2.B13_bedrockRedstoneWeight", 50);
        bedrockBismuthSpawn = builder.comment("Spawns a bedrock bismuth deposit every nTH chunk").define("2.B08_bedrockBismuthSpawn", 400);
        bedrockCadmiumSpawn = builder.comment("Spawns a bedrock cadmium deposit every nTH chunk").define("2.B09_bedrockCadmiumSpawn", 400);
        // JESUS CHRIST....
        // bedrockChlorocalciteSpawn = builder.comment("Spawn weight for chlorocalcite bedrock ore").define("2.B14_bedrockbChlorocalciteWeight", 35);
        bedrockChlorocalciteSpawn = builder.comment("Spawn weight for chlorocalcite bedrock ore").define("2.B14_bedrockChlorocalciteWeight", 35);
        bedrockNeodymiumSpawn = builder.comment("Spawn weight for neodymium bedrock ore").define("2.B15_bedrockNeodymiumWeight", 50);
        bedrockRareEarthSpawn = builder.comment("Spawn weight for rare earth bedrock ore").define("2.B16_bedrockRareEarthWeight", 50);
        bedrockBauxiteSpawn = builder.comment("Spawn weight for bauxite bedrock ore").define("2.B17_bedrockBauxiteWeight", 100);
        bedrockEmeraldSpawn = builder.comment("Spawn weight for emerald bedrock ore").define("2.B18_bedrockEmeraldWeight", 50);

        bedrockGlowstoneSpawn = builder.comment("Spawn weight for glowstone bedrock ore").define("2.BN00_bedrockGlowstoneWeight", 100);
        bedrockPhosphorusSpawn = builder.comment("Spawn weight for phosphorus bedrock ore").define("2.BN01_bedrockPhosphorusWeight", 50);
        bedrockQuartzSpawn = builder.comment("Spawn weight for quartz bedrock ore").define("2.BN01_bedrockQuartzWeight", 100);

        ironClusterSpawn = builder.comment("Amount of iron cluster veins per chunk").define("2.C00_ironClusterSpawn", 4);
        titaniumClusterSpawn = builder.comment("Amount of titanium cluster veins per chunk").define("2.C01_titaniumClusterSpawn", 2);
        aluminiumClusterSpawn = builder.comment("Amount of aluminium cluster veins per chunk").define("2.C02_aluminiumClusterSpawn", 3);
        copperClusterSpawn = builder.comment("Amount of copper cluster veins per chunk").define("2.C03_copperClusterSpawn", 4);

        limestoneSpawn = builder.comment("Amount of limestone block veins per chunk").define("2.L02_limestoneSpawn", 1);

        netherUraniumuSpawn = builder.comment("Amount of nether uranium per chunk").define("2.N00_uraniumSpawnrate", 8);
        netherTungstenSpawn = builder.comment("Amount of nether tungsten per chunk").define("2.N01_tungstenSpawnrate", 10);
        netherSulfurSpawn = builder.comment("Amount of nether sulfur per chunk").define("2.N02_sulfurSpawnrate", 26);
        netherPhosphorusSpawn = builder.comment("Amount of nether phosphorus per chunk").define("2.N03_phosphorusSpawnrate", 24);
        netherCoalSpawn = builder.comment("Amount of nether coal per chunk").define("2.N04_coalSpawnrate", 8);
        netherPlutoniumSpawn = builder.comment("Amount of nether plutonium per chunk, if enabled").define("2.N05_plutoniumSpawnrate", 8);
        netherCobaltSpawn = builder.comment("Amount of nether cobalt per chunk").define("2.N06_cobaltSpawnrate", 2);

        endTikiteSpawn = builder.comment("Amount of end trixite per chunk").define("2.E00_tikiteSpawnrate", 8);

        enableHematite = builder.comment("Toggles hematite deposits").define("2.L00_enableHematite", true);
        enableMalachite = builder.comment("Toggles malachite deposits").define("2.L01_enableMalachite", true);
        enableBauxite = builder.comment("Toggles bauxite deposits").define("2.L02_enableBauxite", true);

        enableSulfurCave = builder.comment("Toggles sulfur caves").define("2.C00_enableSulfurCave", true);
        enableAsbestosCave = builder.comment("Toggles asbestos caves").define("2.C01_enableAsbestosCave", true);
        builder.pop();

        builder.push(CommonConfig.CATEGORY_DUNGEONS);
//		radioStructure = builder.comment("Spawn radio station on every nTH chunk").define("4.00_radioSpawn", 500);
        antennaStructure = builder.comment("Spawn antenna on every nTH chunk").defineInRange("4.01_antennaSpawn", 250, 0, Integer.MAX_VALUE);
        atomStructure = builder.comment("Spawn power plant on every nTH chunk").defineInRange("4.02_atomSpawn", 500, 0, Integer.MAX_VALUE);
        dungeonStructure = builder.comment("Spawn library dungeon on every nTH chunk").defineInRange("4.04_dungeonSpawn", 64, 0, Integer.MAX_VALUE);
        relayStructure = builder.comment("Spawn relay on every nTH chunk").defineInRange("4.05_relaySpawn", 500, 0, Integer.MAX_VALUE);
        satelliteStructure = builder.comment("Spawn satellite dish on every nTH chunk").defineInRange("4.06_satelliteSpawn", 500, 0, Integer.MAX_VALUE);
//		factoryStructure = builder.comment("Spawn factory on every nTH chunk").define("4.09_factorySpawn", 1000);
        dudStructure = builder.comment("Spawn dud on every nTH chunk").defineInRange("4.10_dudSpawn", 500, 0, Integer.MAX_VALUE);
        spaceshipStructure = builder.comment("Spawn spaceship on every nTH chunk").defineInRange("4.11_spaceshipSpawn", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
        barrelStructure = builder.comment("Spawn waste tank on every nTH chunk").defineInRange("4.12_barrelSpawn", 5000, 0, Integer.MAX_VALUE);
        broadcaster = builder.comment("Spawn corrupt broadcaster on every nTH chunk").defineInRange("4.13_broadcasterSpawn", 5000, 0, Integer.MAX_VALUE);
        minefreq = builder.comment("Spawn AP landmine on every nTH chunk").defineInRange("4.14_landmineSpawn", 64, 0, Integer.MAX_VALUE);
        radfreq = builder.comment("Spawn radiation hotspot on every nTH chunk").defineInRange("4.15_radHotspotSpawn", 5000, 0, Integer.MAX_VALUE);
        vaultfreq = builder.comment("Spawn locked safe on every nTH chunk").defineInRange("4.16_vaultSpawn", 2500, 0, Integer.MAX_VALUE);
        geyserWater = builder.comment("Spawn water geyser on every nTH chunk").defineInRange("4.17_geyserWaterSpawn", 3000, 0, Integer.MAX_VALUE);
        geyserChlorine = builder.comment("Spawn poison geyser on every nTH chunk").defineInRange("4.18_geyserChlorineSpawn", 3000, 0, Integer.MAX_VALUE);
        geyserVapor = builder.comment("Spawn vapor geyser on every nTH chunk").defineInRange("4.19_geyserVaporSpawn", 500, 0, Integer.MAX_VALUE);
        capsuleStructure = builder.comment("Spawn landing capsule on every nTH chunk").defineInRange("4.21_capsuleSpawn", 100, 0, Integer.MAX_VALUE);
        arcticStructure = builder.comment("Spawn arctic code vault on every nTH chunk").defineInRange("4.22_arcticVaultSpawn", 500, 0, Integer.MAX_VALUE);
        jungleStructure = builder.comment("Spawn jungle dungeon on every nTH chunk").defineInRange("4.23_jungleDungeonSpawn", 2000, 0, Integer.MAX_VALUE);
        pyramidStructure = builder.comment("Spawn pyramid on every nTH chunk").define("4.24_pyramidSpawn", 4000);
        builder.pop();

        builder.push(CommonConfig.CATEGORY_METEORS);
        enableMeteorStrikes = builder.comment("Toggles the spawning of meteors").define("5.00_enableMeteorStrikes", true);
        enableMeteorShowers = builder.comment("Toggles meteor showers, which start with a 1% chance for every spawned meteor").define("5.01_enableMeteorShowers", true);
        enableMeteorTails = builder.comment("Toggles the particle effect created by falling meteors").define("5.02_enableMeteorTails", true);
        enableSpecialMeteors = builder.comment("Toggles rare, special meteor types with different impact effects").define("5.03_enableSpecialMeteors", true);
        meteorStrikeChance = builder.comment("The probability of a meteor spawning (an average of once every nTH ticks)").defineInRange("5.03_meteorStrikeChance", 20 * 60 * 60 * 5, 1, Integer.MAX_VALUE);
        meteorShowerChance = builder.comment("The probability of a meteor spawning during meteor shower (an average of once every nTH ticks)").defineInRange("5.04_meteorShowerChance", 20 * 60 * 15, 1, Integer.MAX_VALUE);
        meteorShowerDuration = builder.comment("Max duration of meteor shower in ticks").define("5.05_meteorShowerDuration", 20 * 60 * 30);

        // Move defaults into unused ranges if EndlessIDs is installed
        builder.pop();

        builder.push(CommonConfig.CATEGORY_BIOMES);
        enableCraterBiomes = builder.comment("Enables the biome change caused by nuclear explosions").define("17.B_toggle", true);
        craterBiomeRad = builder.comment("RAD/s for the crater biome").define("17.R00_craterBiomeRad", 5.0D);
        craterBiomeInnerRad = builder.comment("RAD/s for the inner crater biome").define("17.R01_craterBiomeInnerRad", 25.0D);
        craterBiomeOuterRad = builder.comment("RAD/s for the outer crater biome").define("17.R02_craterBiomeOuterRad", 0.5D);
        craterBiomeWaterMult = builder.comment("Multiplier for RAD/s in crater biomes when in water").define("17.R03_craterBiomeWaterMult", 5.0D);
    }
}
