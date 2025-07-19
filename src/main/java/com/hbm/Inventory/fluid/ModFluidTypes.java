package com.hbm.Inventory.fluid;

import com.hbm.HBM;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Vector3f;

//在这个类里面注册流体类型
public class ModFluidTypes {
//    //流体类型注册器
//    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, HBM.MODID);
//    //原版水的静止和流动贴图
//    public static final ResourceLocation WATER_STILL_TEX = new ResourceLocation("block/water_still");
//    public static final ResourceLocation WATER_FLOWING_TEX = new ResourceLocation("block/water_flow");
//    //液体的overlay，暂时没用，只用来填充参数，因为我试过，纹理加载不出来。
//    public static final ResourceLocation WATER_OVERLAY = new ResourceLocation("block/water_overlay");
//    public static final ResourceLocation SULFURIC_OVERLAY = HBM.rl("block/fluid/H2SO4_overlay");
//    public static final ResourceLocation SULFURIC_STILL_TEX = new ResourceLocation(HBM.MODID,"block/fluid/sulfuric_acid_still");
//    public static final ResourceLocation SULFURIC_FLOWING_TEX = new ResourceLocation(HBM.MODID,"block/fluid/sulfuric_acid_flowing");
//    public static final ResourceLocation IRRADIATED_WATER_STILL = new ResourceLocation(HBM.MODID,"block/fluid/irradiated_water_still");
//    public static final ResourceLocation IRRADIATED_WATER_FLOW = new ResourceLocation(HBM.MODID,"block/fluid/irradiated_water_flow");
//    public static final ResourceLocation IRRADIATED_POLLUTED_STILL = new ResourceLocation(HBM.MODID,"block/fluid/irradiated_polluted_still");
//    public static final ResourceLocation IRRADIATED_POLLUTED_FLOW = new ResourceLocation(HBM.MODID,"block/fluid/irradiated_polluted_flow");
//    //液体属性
//    private static final FluidType.Properties defaultProperties =  FluidType.Properties.create().lightLevel(4).density(15).viscosity(5).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK);
//    //辐射水
//    public static final RegistryObject<FluidType> IRRADIATED_WATER = FLUID_TYPES.register("irradiated_water",
//            ()->new BaseFluidType(IRRADIATED_WATER_STILL,IRRADIATED_WATER_FLOW,WATER_OVERLAY,0xA1E038D0,new Vector3f(224f / 255f, 56f / 255f, 208f / 255f), defaultProperties));//0x96C074
//    public static final RegistryObject<FluidType> IRRADIATED_POLLUTED = FLUID_TYPES.register("irradiated_polluted",
//            ()->new BaseFluidType(IRRADIATED_POLLUTED_STILL,IRRADIATED_POLLUTED_FLOW,WATER_OVERLAY,0xA1E038D0,new Vector3f(224f / 255f, 56f / 255f, 208f / 255f), defaultProperties));
//    //硫酸
//    public static final RegistryObject<FluidType> SULFURIC_ACID = FLUID_TYPES.register("sulfuric_acid",
//            ()->new BaseFluidType(WATER_STILL_TEX,WATER_FLOWING_TEX,WATER_OVERLAY,0xA1E038D0,new Vector3f(224f / 255f, 56f / 255f, 208f / 255f),defaultProperties));//0x96C074
}
