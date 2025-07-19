package com.hbm.Inventory.fluid;

import com.hbm.HBM;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.hbm.Inventory.fluid.ExtendedFluidType.ExtendedProperties;

import static com.hbm.Inventory.fluid.ExtendedFluidType.*;

//在这个类里面注册流体
public class ModFluids {
    //流体注册器
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, HBM.MODID);
    //流体类型注册器
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, HBM.MODID);

    //辐射水
//    public static RegistryObject<FlowingFluid> IRRADIATED_WATER_SOURCE_BLOCK = FLUIDS.register("irradiated_water",()->new ForgeFlowingFluid.Source(ModFluids.IRRADIATED_WATER_PROPERTIES));
//    public static RegistryObject<FlowingFluid> IRRADIATED_WATER_FLOW_BLOCK = FLUIDS.register("irradiated_water_flow",()->new ForgeFlowingFluid.Flowing(ModFluids.IRRADIATED_WATER_PROPERTIES));
//
//    public static RegistryObject<FlowingFluid> IRRADIATED_POLLUTED_SOURCE_BLOCK = FLUIDS.register("irradiated_polluted",()->new ForgeFlowingFluid.Source(ModFluids.IRRADIATED_POLLUTED_PROPERTIES));
//    public static RegistryObject<FlowingFluid> IRRADIATED_POLLUTED_FLOW_BLOCK = FLUIDS.register("irradiated_polluted_flow",()->new ForgeFlowingFluid.Flowing(ModFluids.IRRADIATED_POLLUTED_PROPERTIES));
//    //硫酸
//    public static RegistryObject<FlowingFluid> SULFURIC_ACID_SOURCE_BLOCK = FLUIDS.register("sulfuric_acid_still",()->new ForgeFlowingFluid.Source(ModFluids.SULFURIC_ACID_PROPERTIES));
//    public static RegistryObject<FlowingFluid> SULFURIC_ACID_FLOW_BLOCK = FLUIDS.register("sulfuric_acid_flow",()->new ForgeFlowingFluid.Flowing(ModFluids.SULFURIC_ACID_PROPERTIES));
//
//    public static final ForgeFlowingFluid.Properties IRRADIATED_WATER_PROPERTIES = new ForgeFlowingFluid.Properties(ModFluidTypes.IRRADIATED_WATER,
//            ModFluids.IRRADIATED_WATER_SOURCE_BLOCK, ModFluids.IRRADIATED_WATER_FLOW_BLOCK).bucket(ModItems.bucket_irradiated_water).slopeFindDistance(2).levelDecreasePerBlock(2).block(ModBlocks.irradiated_water);
//    public static final ForgeFlowingFluid.Properties IRRADIATED_POLLUTED_PROPERTIES = new ForgeFlowingFluid.Properties(ModFluidTypes.IRRADIATED_POLLUTED,
//            ModFluids.IRRADIATED_POLLUTED_SOURCE_BLOCK, ModFluids.IRRADIATED_POLLUTED_FLOW_BLOCK).bucket(ModItems.bucket_irradiated_polluted).slopeFindDistance(2).levelDecreasePerBlock(2).block(ModBlocks.irradiated_polluted);
//    public static final ForgeFlowingFluid.Properties SULFURIC_ACID_PROPERTIES = new ForgeFlowingFluid.Properties(ModFluidTypes.SULFURIC_ACID,
//            ModFluids.SULFURIC_ACID_SOURCE_BLOCK, ModFluids.SULFURIC_ACID_FLOW_BLOCK).bucket(ModItems.bucket_sulfuric_acid).slopeFindDistance(2).levelDecreasePerBlock(2).block(ModBlocks.sulfuric_acid);
    // 流体注册
    public static final ExtendedFluidType STEAM = new ExtendedFluidType(0xe5e5e5, prop_water.temperature(373), ExtendedProperties.of().pfr(3,0,0).traits(GASEOUS, UNSIPHONABLE));
    public static final ExtendedFluidType HOTSTEAM = new ExtendedFluidType(0xE7D6D6, prop_water.temperature(573), ExtendedProperties.of().pfr(4,0,0).traits(GASEOUS, UNSIPHONABLE));
    public static final ExtendedFluidType SUPERHOTSTEAM = new ExtendedFluidType(0xE7B7B7, prop_water.temperature(723), ExtendedProperties.of().pfr(4,0,0).traits(GASEOUS, UNSIPHONABLE));
    public static final ExtendedFluidType ULTRAHOTSTEAM = new ExtendedFluidType(0xE39393, prop_water.temperature(873), ExtendedProperties.of().pfr(4,0,0).traits(GASEOUS, UNSIPHONABLE));
    public static final ExtendedFluidType COOLANT = new ExtendedFluidType(0xd8fcff, prop_water, ExtendedProperties.of().pfr(1,0,0).traits(LIQUID));
//    public static final ExtendedFluidType DEUTERIUM = new ExtendedFluidType(0x0000FF, prop_water, ExtendedProperties.of().pfr(3,4,0).burn(10).traits(GASEOUS));
//    public static final ExtendedFluidType TRITIUM = new ExtendedFluidType(0x000099, prop_water, ExtendedProperties.of().pfr(3,4,0).burn(5).traits(GASEOUS).rad(0.001F));
//    public static final ExtendedFluidType OIL = new ExtendedFluidType(0x020202, prop_water, ExtendedProperties.of().pfr(2,1,0).burn(10).traits(LIQUID,VISCOUS));
//    public static final ExtendedFluidType COOLANT = new ExtendedFluidType(0xd8fcff, prop_water, ExtendedProperties.of().pfr(1).traits(LIQUID));
//    public static final ExtendedFluidType COOLANT = new ExtendedFluidType(0xd8fcff, prop_water, ExtendedProperties.of().pfr(1).traits(LIQUID));
//    public static final ExtendedFluidType COOLANT = new ExtendedFluidType(0xd8fcff, prop_water, ExtendedProperties.of().pfr(1).traits(LIQUID));
//    public static final ExtendedFluidType COOLANT = new ExtendedFluidType(0xd8fcff, prop_water, ExtendedProperties.of().pfr(1).traits(LIQUID));
//    public static final ExtendedFluidType COOLANT = new ExtendedFluidType(0xd8fcff, prop_water, ExtendedProperties.of().pfr(1).traits(LIQUID));
//    public static final ExtendedFluidType COOLANT = new ExtendedFluidType(0xd8fcff, prop_water, ExtendedProperties.of().pfr(1).traits(LIQUID));
    public static final ExtendedFluidType hydrogen = new ExtendedFluidType(0x4286f4, prop_water,ExtendedProperties.of().pfr(3,4,0));

    public static final FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> HYDROGEN = register("hydrogen",hydrogen);

    public static FluidRegistryHolder register(String name, ExtendedFluidType fluidType){
        RegistryObject<FluidType> type = FLUID_TYPES.register(name, () -> fluidType);
//        ForgeFlowingFluid.Source temp_src = new ForgeFlowingFluid.Source(fluidType.flowProperties);
//        ForgeFlowingFluid.Flowing temp_flow = new ForgeFlowingFluid.Flowing(fluidType.flowProperties);
//        new PropertiesHolder()
//        fluidType.flowProperties = new ForgeFlowingFluid.Properties(() -> fluidType, () -> temp_src, () -> temp_flow);
        RegistryObject<ForgeFlowingFluid.Source> source = FLUIDS.register(name, () -> new ForgeFlowingFluid.Source(fluidType.flowProperties));
        RegistryObject<ForgeFlowingFluid.Flowing> flowing = FLUIDS.register(name+"_flow", () -> new ForgeFlowingFluid.Flowing(fluidType.flowProperties));
        fluidType.flowProperties = new ForgeFlowingFluid.Properties(type, source, flowing);
        RegistryObject<LiquidBlock> block = ModBlocks.BLOCKS.register(name, () -> new LiquidBlock(source, BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable()));
        RegistryObject<BucketItem> bucket = ModItems.ITEMS.register("bucket_" + name, () -> new BucketItem(source, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
        return new FluidRegistryHolder(type, source, flowing, block, bucket);
    }

    public record FluidRegistryHolder<TYPE extends FluidType, SOURCE extends ForgeFlowingFluid.Source, FLOWING extends ForgeFlowingFluid.Flowing, BLOCK extends LiquidBlock, BUCKET extends BucketItem>
            (RegistryObject<TYPE> type, RegistryObject<SOURCE> source, RegistryObject<FLOWING> flowing, RegistryObject<BLOCK> block, RegistryObject<TYPE> bucket){}
}
