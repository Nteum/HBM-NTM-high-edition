package com.hbm.Inventory.fluid;

import com.hbm.HBM;
import com.hbm.Inventory.fluid.trait.FT_Corrosive;
import com.hbm.item.tool.FluidBucketItem;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
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
    public static final ExtendedFluidType COOLANT = new ExtendedFluidType("coolant",0xd8fcff, solution, ExtendedProperties.of().pfr(1,0,0).traits(LIQUID));
//    public static final ExtendedFluidType DEUTERIUM = new ExtendedFluidType(0x0000FF, solution, ExtendedProperties.of().pfr(3,4,0).burn(10).traits(GASEOUS));
//    public static final ExtendedFluidType TRITIUM = new ExtendedFluidType(0x000099, solution, ExtendedProperties.of().pfr(3,4,0).burn(5).traits(GASEOUS).rad(0.001F));
//    public static final ExtendedFluidType OIL = new ExtendedFluidType(0x020202, solution, ExtendedProperties.of().pfr(2,1,0).burn(10).traits(LIQUID,VISCOUS));
//    public static final ExtendedFluidType COOLANT = new ExtendedFluidType(0xd8fcff, solution, ExtendedProperties.of().pfr(1).traits(LIQUID));
    public static final ExtendedFluidType hydrogen = new ExtendedFluidType("hydrogen",0x4286f4, solution.descriptionId("Hydrogen"),ExtendedProperties.of().pfr(3,4,0));
    public static final ExtendedFluidType diesel = new ExtendedFluidType("diesel", 0xf2eed5, solution.descriptionId("Diesel"), ExtendedProperties.of().pfr(1, 2, 0));
    public static final ExtendedFluidType wood_oil = new ExtendedFluidType("woodoil", 0xf2eed5, solution.descriptionId("Wood Oil"), ExtendedProperties.of().pfr(1, 2, 0));

    // 流体注册
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> HYDROGEN = register(hydrogen);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> IRRADIATED_WATER = register(irradiated_water);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> IRRADIATED_POLLUTED = register(irradiated_polluted);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> SULFURIC_ACID = register(sulfuric_acid);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> STEAM = register(steam);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> DIESEL = register(diesel);
    public static FluidRegistryHolder<ExtendedFluidType, ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing,LiquidBlock, BucketItem> WOOD_OIL = register(wood_oil);

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
