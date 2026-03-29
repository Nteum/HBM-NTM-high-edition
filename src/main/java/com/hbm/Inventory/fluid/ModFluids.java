package com.hbm.Inventory.fluid;

import com.hbm.HBM;
import com.hbm.Inventory.fluid.trait.FT_Corrosive;
import com.hbm.Inventory.fluid.trait.FT_Coolable;
import com.hbm.Inventory.fluid.trait.FT_Heatable;
import com.hbm.item.tool.FluidBucketItem;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.hbm.Inventory.fluid.ExtendedFluidType.ExtendedProperties;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hbm.HBM.MODID;
import static com.hbm.Inventory.fluid.ExtendedFluidType.*;

//在这个类里面注册流体
public class ModFluids {
    //流体注册器
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, HBM.MODID);
    //流体类型注册器
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, HBM.MODID);
    public static final DeferredRegister<Block> FLUID_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> FLUID_CONTAINER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // 列表
    public static final List<FluidRegistryHolder> fluidList = new ArrayList<>();

    // FluidType
    public static final ExtendedFluidType water = new ExtendedFluidType("water", 0x3333FF, prop_water, ExtendedProperties.of().pfr(0,0,0));
    public static final ExtendedFluidType lava = new ExtendedFluidType("lava", 0xFF3300, prop_lava, ExtendedProperties.of().pfr(4,0,0));
    public static final ExtendedFluidType milk = new ExtendedFluidType("milk", 0xA0A0A4, prop_water.canConvertToSource(false), ExtendedProperties.of().pfr(0,0,0));
    public static final ExtendedFluidType irradiated_water = new ExtendedFluidType("irradiated_water",HBM.rl("block/fluid/irradiated_water_still"),HBM.rl("block/fluid/irradiated_water_flow"),WATER_OVERLAY,0xA1E038D0,new Vector3f(224f / 255f, 56f / 255f, 208f / 255f), prop_water.descriptionId("Radioactive Water"), ExtendedProperties.of());
    public static final ExtendedFluidType irradiated_polluted = new ExtendedFluidType("irradiated_polluted",HBM.rl("block/fluid/irradiated_polluted_still"),HBM.rl("block/fluid/irradiated_polluted_flow"),WATER_OVERLAY,0xA1E038D0,new Vector3f(224f / 255f, 56f / 255f, 208f / 255f), prop_water.descriptionId("Nuclear-contaminated Water"), ExtendedProperties.of());
    public static final ExtendedFluidType sulfuric_acid = new ExtendedFluidType("sulfuric_acid",0xB0AA64, solution.descriptionId("Sulfuric Acid"), ExtendedProperties.of().pfr(3,0,2).traits(new FT_Corrosive(50), LIQUID));
    public static final ExtendedFluidType steam = new ExtendedFluidType("steam",0xe5e5e5, solution.temperature(373), ExtendedProperties.of().pfr(3,0,0).traits(GASEOUS, UNSIPHONABLE));
    public static final ExtendedFluidType HOTSTEAM = new ExtendedFluidType("hot_steam",0xE7D6D6, solution.temperature(573), ExtendedProperties.of().pfr(4,0,0).traits(GASEOUS, UNSIPHONABLE));
    public static final ExtendedFluidType SUPERHOTSTEAM = new ExtendedFluidType("superhot_steam",0xE7B7B7, solution.temperature(723), ExtendedProperties.of().pfr(4,0,0).traits(GASEOUS, UNSIPHONABLE));
    public static final ExtendedFluidType ULTRAHOTSTEAM = new ExtendedFluidType("ultrahot_steam",0xE39393, solution.temperature(873), ExtendedProperties.of().pfr(4,0,0).traits(GASEOUS, UNSIPHONABLE));
    public static final ExtendedFluidType carbon_dioxide = new ExtendedFluidType("carbon_dioxide", 0xb0b0b0, solution.descriptionId("Carbon Dioxide"), ExtendedProperties.of().pfr(0,0,0).traits(GASEOUS, UNSIPHONABLE));
    public static final ExtendedFluidType coolant_hot = new ExtendedFluidType("coolant_hot",0x99525E, solution, ExtendedProperties.of().pfr(1,0,0).traits(LIQUID));
    public static final ExtendedFluidType coolant = new ExtendedFluidType("coolant",0xd8fcff, solution, ExtendedProperties.of().pfr(1,0,0).traits(LIQUID));
    public static final ExtendedFluidType hydrogen = new ExtendedFluidType("hydrogen",0x4286f4, solution.descriptionId("Hydrogen"),ExtendedProperties.of().pfr(3,4,0).traits(GASEOUS));
    public static final ExtendedFluidType deuterium = new ExtendedFluidType("deuterium",0x2f6bff, solution.descriptionId("Deuterium"),ExtendedProperties.of().pfr(3,4,0).traits(GASEOUS));
    public static final ExtendedFluidType tritium = new ExtendedFluidType("tritium",0x0b2a86, solution.descriptionId("Tritium"),ExtendedProperties.of().pfr(3,4,0).traits(GASEOUS));
    public static final ExtendedFluidType helium3 = new ExtendedFluidType("helium3",0xfff0c4, solution.descriptionId("Helium-3"),ExtendedProperties.of().pfr(2,2,0).traits(GASEOUS));
    public static final ExtendedFluidType helium4 = new ExtendedFluidType("helium4",0xffb480, solution.descriptionId("Helium-4"),ExtendedProperties.of().pfr(2,2,0).traits(GASEOUS));
    public static final ExtendedFluidType oxygen = new ExtendedFluidType("oxygen",0xb4e2ff, solution.descriptionId("Oxygen"),ExtendedProperties.of().pfr(2,1,0).traits(GASEOUS));
    public static final ExtendedFluidType chlorine = new ExtendedFluidType("chlorine",0xdae598, solution.descriptionId("Chlorine"),ExtendedProperties.of().pfr(2,1,0).traits(GASEOUS));
    public static final ExtendedFluidType diesel = new ExtendedFluidType("diesel", 0xf2eed5, solution.descriptionId("Diesel"), ExtendedProperties.of().pfr(1, 2, 0));
    public static final ExtendedFluidType oil = new ExtendedFluidType("oil", 0x1d150d, solution.descriptionId("Crude Oil"), ExtendedProperties.of().pfr(1, 2, 0).traits(LIQUID, VISCOUS));
    public static final ExtendedFluidType crack_oil = new ExtendedFluidType("crack_oil", 0x2b1a10, solution.descriptionId("Cracked Oil"), ExtendedProperties.of().pfr(1, 3, 0).traits(LIQUID, VISCOUS));
    public static final ExtendedFluidType bitumen = new ExtendedFluidType("bitumen", 0x150f0b, solution.descriptionId("Bitumen"), ExtendedProperties.of().pfr(1, 3, 0).traits(LIQUID, VISCOUS));
    public static final ExtendedFluidType smear = new ExtendedFluidType("smear", 0x402213, solution.descriptionId("Heavy Residue"), ExtendedProperties.of().pfr(1, 2, 0).traits(LIQUID, VISCOUS));
    public static final ExtendedFluidType naphtha = new ExtendedFluidType("naphtha", 0xfff1b5, solution.descriptionId("Naphtha"), ExtendedProperties.of().pfr(1, 1, 0));
    public static final ExtendedFluidType petroleum = new ExtendedFluidType("petroleum", 0x2e2e2e, solution.descriptionId("Petroleum"), ExtendedProperties.of().pfr(1, 3, 0));
    public static final ExtendedFluidType aromatics = new ExtendedFluidType("aromatics", 0xb04b8e, solution.descriptionId("Aromatics"), ExtendedProperties.of().pfr(2, 2, 1));
    public static final ExtendedFluidType unsaturateds = new ExtendedFluidType("unsaturateds", 0xffb347, solution.descriptionId("Unsaturateds"), ExtendedProperties.of().pfr(2, 1, 1));
    public static final ExtendedFluidType refinery_gas = new ExtendedFluidType("refinery_gas", 0xffd966, solution.descriptionId("Refinery Gas"), ExtendedProperties.of().pfr(1, 2, 0).traits(GASEOUS));
    public static final ExtendedFluidType diesel_crack = new ExtendedFluidType("diesel_crack", 0xe5d4ab, solution.descriptionId("Cracked Diesel"), ExtendedProperties.of().pfr(1, 2, 0));
    public static final ExtendedFluidType kerosene = new ExtendedFluidType("kerosene", 0xf6f2d9, solution.descriptionId("Kerosene"), ExtendedProperties.of().pfr(1, 1, 0));
    public static final ExtendedFluidType wood_oil = new ExtendedFluidType("wood_oil", 0x2f2519, solution.descriptionId("Wood Oil"), ExtendedProperties.of().pfr(1, 1, 0).traits(LIQUID, VISCOUS));
    public static final ExtendedFluidType heating_oil = new ExtendedFluidType("heating_oil", 0x8c6b2b, solution.descriptionId("Heating Oil"), ExtendedProperties.of().pfr(1, 2, 0));
    public static final ExtendedFluidType heating_oil_vacuum = new ExtendedFluidType("heating_oil_vacuum", 0xa37932, solution.descriptionId("Vacuum Heating Oil"), ExtendedProperties.of().pfr(1, 2, 0));
    public static final ExtendedFluidType reform_gas = new ExtendedFluidType("reform_gas", 0xfceea1, solution.descriptionId("Reform Gas"), ExtendedProperties.of().pfr(1, 1, 1).traits(GASEOUS));
    public static final ExtendedFluidType reformate = new ExtendedFluidType("reformate", 0xe0d342, solution.descriptionId("Reformate"), ExtendedProperties.of().pfr(1, 1, 0));
    public static final ExtendedFluidType biogas = new ExtendedFluidType("biogas", 0x7ed691, solution.descriptionId("Biogas"), ExtendedProperties.of().pfr(1, 1, 0).traits(GASEOUS));
    public static final ExtendedFluidType spent_steam = new ExtendedFluidType("spent_steam", 0xc9c9c9, solution.descriptionId("Spent Steam"), ExtendedProperties.of().pfr(0,0,0).traits(GASEOUS, UNSIPHONABLE));
    public static final ExtendedFluidType smoke = new ExtendedFluidType("smoke", 0x808080, solution.descriptionId("Smoke"), ExtendedProperties.of().pfr(0,0,0).traits(GASEOUS, NOID, NOCON));
    public static final ExtendedFluidType smoke_leaded = new ExtendedFluidType("smoke_leaded", 0x808080, solution.descriptionId("Leaded Smoke"), ExtendedProperties.of().pfr(0,0,0).traits(GASEOUS, NOID, NOCON));
    public static final ExtendedFluidType smoke_poison = new ExtendedFluidType("smoke_poison", 0x808080, solution.descriptionId("Poison Somke"), ExtendedProperties.of().pfr(0,0,0).traits(GASEOUS, NOID, NOCON));
    static {
        FT_Heatable coolantHeatable = new FT_Heatable()
                .setEff(FT_Heatable.HeatingType.HEATEXCHANGER, 1.0D)
                .setEff(FT_Heatable.HeatingType.PWR, 1.0D)
                .setEff(FT_Heatable.HeatingType.ICF, 1.0D)
                .addStep(300, 1, coolant_hot, 1);
        coolant.hbmProperties.traits.put(FT_Heatable.class, coolantHeatable);
        coolant_hot.hbmProperties.traits.put(FT_Coolable.class,
                new FT_Coolable(coolant, 1, 1, 300).setEff(FT_Coolable.CoolingType.HEATEXCHANGER, 1.0D));
    }

    // 流体注册
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> COOLANT_HOT = register(coolant_hot);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> COOLANT = register(coolant);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> HYDROGEN = register(hydrogen);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> DEUTERIUM = register(deuterium);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> TRITIUM = register(tritium);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> HELIUM3 = register(helium3);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> HELIUM4 = register(helium4);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> OXYGEN = register(oxygen);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> CHLORINE = register(chlorine);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> IRRADIATED_WATER = register(irradiated_water);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> IRRADIATED_POLLUTED = register(irradiated_polluted);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> SULFURIC_ACID = register(sulfuric_acid);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> STEAM = register(steam);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> HOT_STEAM = register(HOTSTEAM);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> SUPERHOT_STEAM = register(SUPERHOTSTEAM);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> CARBON_DIOXIDE = register(carbon_dioxide);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> DIESEL = register(diesel);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> OIL = register(oil);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> CRACK_OIL = register(crack_oil);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> BITUMEN = register(bitumen);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> SMEAR = register(smear);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> NAPHTHA = register(naphtha);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> PETROLEUM = register(petroleum);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> AROMATICS = register(aromatics);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> UNSATURATEDS = register(unsaturateds);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> REFINERY_GAS = register(refinery_gas);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> DIESEL_CRACK = register(diesel_crack);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> KEROSENE = register(kerosene);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> WOOD_OIL = register(wood_oil);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> HEATING_OIL = register(heating_oil);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> HEATING_OIL_VACUUM = register(heating_oil_vacuum);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> REFORM_GAS = register(reform_gas);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> REFORMATE = register(reformate);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> BIOGAS = register(biogas);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> SPENT_STEAM = register(spent_steam);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> SMOKE = register(smoke);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> SMOKE_LEADED = register(smoke_leaded);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> SMOKE_POISON = register(smoke_poison);

    public static FluidRegistryHolder register(ExtendedFluidType fluidType){
        String name = fluidType.name;
        RegistryObject<FluidType> type = FLUID_TYPES.register(name, () -> fluidType);
        RegistryObject<ForgeFlowingFluid.Source> source = FLUIDS.register(name, () -> new ForgeFlowingFluid.Source(fluidType.flowProperties));
        RegistryObject<ForgeFlowingFluid.Flowing> flowing = FLUIDS.register(name+"_flow", () -> new ForgeFlowingFluid.Flowing(fluidType.flowProperties));
        fluidType.flowProperties = new ForgeFlowingFluid.Properties(type, source, flowing);
        RegistryObject<LiquidBlock> block = FLUID_BLOCKS.register(name, () -> new LiquidBlock(source, BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable()));
        RegistryObject<BucketItem> bucket = FLUID_CONTAINER.register("bucket_" + name, () -> new FluidBucketItem(source, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
        fluidType.flowProperties.block(block);
        fluidType.flowProperties.bucket(bucket);
        FluidRegistryHolder registryHolder = new FluidRegistryHolder(type, source, flowing, block, bucket);
        fluidList.add(registryHolder);
        return registryHolder;
    }

    public static void register(IEventBus modEventBus){
//        HYDROGEN = register(hydrogen);
//        IRRADIATED_WATER = register(irradiated_water);
//        IRRADIATED_POLLUTED = register(irradiated_polluted);
//        SULFURIC_ACID = register(sulfuric_acid);

        FLUIDS.register(modEventBus);
        FLUID_TYPES.register(modEventBus);
        FLUID_BLOCKS.register(modEventBus);
        FLUID_CONTAINER.register(modEventBus);
    }

    public static void bucketModel(ItemModelProvider provider){
        ModFluids.fluidList.stream().map(FluidRegistryHolder::bucket).filter(bucket -> bucket.get() instanceof FluidBucketItem).forEach(bucket -> {
            provider.withExistingParent(bucket.getId().getPath(), HBM.rl("item/fluid_bucket"));
        });
    }
    public static void localName(LanguageProvider languageProvider){
        ModFluids.fluidList.forEach(holder -> {
            ExtendedFluidType fluidType = (ExtendedFluidType) holder.type.get();
            String enName = Arrays.stream(fluidType.name.split("_")).map(s -> (s == null || s.isEmpty()) ? s : s.substring(0, 1).toUpperCase() + s.substring(1)).reduce("", (ss, s) -> ss + " " + s);
            languageProvider.add(fluidType.getDescriptionId(), enName);
            if (holder.bucket().get() instanceof BucketItem bucketItem)
                languageProvider.add(bucketItem, enName + " Bucket");
        });
    }

    public record FluidRegistryHolder<TYPE extends FluidType, SOURCE extends ForgeFlowingFluid.Source, FLOWING extends ForgeFlowingFluid.Flowing, BLOCK extends LiquidBlock, BUCKET extends BucketItem>
            (RegistryObject<TYPE> type, RegistryObject<SOURCE> source, RegistryObject<FLOWING> flowing, RegistryObject<BLOCK> block, RegistryObject<BUCKET> bucket){
    }
}
