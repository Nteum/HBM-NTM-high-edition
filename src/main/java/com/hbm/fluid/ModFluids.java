package com.hbm.fluid;

import com.hbm.main.HBMxx;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

//在这个类里面注册流体
public class ModFluids {
    //流体注册器
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, HBMxx.MODID);
    //辐射水
    public static RegistryObject<FlowingFluid> IRRADIATED_WATER_SOURCE_BLOCK = FLUIDS.register("irradiated_water",()->new ForgeFlowingFluid.Source(ModFluids.IRRADIATED_WATER_PROPERTIES));
    public static RegistryObject<FlowingFluid> IRRADIATED_WATER_FLOW_BLOCK = FLUIDS.register("irradiated_water_flow",()->new ForgeFlowingFluid.Flowing(ModFluids.IRRADIATED_WATER_PROPERTIES));

    public static RegistryObject<FlowingFluid> IRRADIATED_POLLUTED_SOURCE_BLOCK = FLUIDS.register("irradiated_polluted",()->new ForgeFlowingFluid.Source(ModFluids.IRRADIATED_POLLUTED_PROPERTIES));
    public static RegistryObject<FlowingFluid> IRRADIATED_POLLUTED_FLOW_BLOCK = FLUIDS.register("irradiated_polluted_flow",()->new ForgeFlowingFluid.Flowing(ModFluids.IRRADIATED_POLLUTED_PROPERTIES));
    //硫酸
    public static RegistryObject<FlowingFluid> SULFURIC_ACID_SOURCE_BLOCK = FLUIDS.register("sulfuric_acid_still",()->new ForgeFlowingFluid.Source(ModFluids.SULFURIC_ACID_PROPERTIES));
    public static RegistryObject<FlowingFluid> SULFURIC_ACID_FLOW_BLOCK = FLUIDS.register("sulfuric_acid_flow",()->new ForgeFlowingFluid.Flowing(ModFluids.SULFURIC_ACID_PROPERTIES));

    public static final ForgeFlowingFluid.Properties IRRADIATED_WATER_PROPERTIES = new ForgeFlowingFluid.Properties(ModFluidTypes.IRRADIATED_WATER,
            ModFluids.IRRADIATED_WATER_SOURCE_BLOCK, ModFluids.IRRADIATED_WATER_FLOW_BLOCK).bucket(ModItems.bucket_irradiated_water).slopeFindDistance(2).levelDecreasePerBlock(2).block(ModBlocks.irradiated_water);
    public static final ForgeFlowingFluid.Properties IRRADIATED_POLLUTED_PROPERTIES = new ForgeFlowingFluid.Properties(ModFluidTypes.IRRADIATED_POLLUTED,
            ModFluids.IRRADIATED_POLLUTED_SOURCE_BLOCK, ModFluids.IRRADIATED_POLLUTED_FLOW_BLOCK).bucket(ModItems.bucket_irradiated_polluted).slopeFindDistance(2).levelDecreasePerBlock(2).block(ModBlocks.irradiated_polluted);
    public static final ForgeFlowingFluid.Properties SULFURIC_ACID_PROPERTIES = new ForgeFlowingFluid.Properties(ModFluidTypes.SULFURIC_ACID,
            ModFluids.SULFURIC_ACID_SOURCE_BLOCK, ModFluids.SULFURIC_ACID_FLOW_BLOCK).bucket(ModItems.bucket_sulfuric_acid).slopeFindDistance(2).levelDecreasePerBlock(2).block(ModBlocks.sulfuric_acid);

}
