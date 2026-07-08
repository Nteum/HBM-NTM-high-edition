package com.hbm.world.feature;

import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.block.BlockEnums;
import com.hbm.item.ItemEnums;
import com.hbm.item.env.ItemBedrockOre;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import com.hbm.registries.PseudoFluidStack;
import com.hbm.registries.PseudoItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 基岩矿的基本信息
 *
 * */
public class BedrockOreDefinition {
    public static final Map<String, BedrockOreDefinition> DEFINITIONS = new HashMap<>();
    public static Map<ResourceKey<Level>, Map<BedrockOreDefinition, Integer>> WEIGHTED_ORES = new HashMap<>();
    // 可用的矿物定义
    public static final BedrockOreDefinition IRON = new BedrockOreDefinition("iron", new PseudoItemStack(ModItems.BEDROCK_ORE), 0xE2C0AA, 1);
    public static final BedrockOreDefinition COPPER = new BedrockOreDefinition("copper", new PseudoItemStack(ModItems.BEDROCK_ORE), 0xEC9A63, 1);
    public static final BedrockOreDefinition BORAX = new BedrockOreDefinition("borax", new PseudoItemStack(ModItems.BEDROCK_ORE), 0xE4BE74, 3, new PseudoFluidStack(ModFluids.SULFURIC_ACID.source(), 500));
    public static final BedrockOreDefinition CHLOROCALCITE = new BedrockOreDefinition("chlorocalcite", new PseudoItemStack(ModItems.BEDROCK_ORE), 0xCDE036, 3, new PseudoFluidStack(ModFluids.SULFURIC_ACID.source(), 500));
    public static final BedrockOreDefinition ASBESTOS = new BedrockOreDefinition("asbestos", new PseudoItemStack(ModItems.BEDROCK_ORE), 0xBFBFB9, 2);
    public static final BedrockOreDefinition NIOBIUM = new BedrockOreDefinition("niobium", new PseudoItemStack(ModItems.BEDROCK_ORE), 0xAF58D8, 2, new PseudoFluidStack(ModFluids.PEROXIDE.source(), 500));
    public static final BedrockOreDefinition NEODYMIUM = new BedrockOreDefinition("neodymium", new PseudoItemStack(ModItems.BEDROCK_ORE), 0x8F8F5F, 3, new PseudoFluidStack(ModFluids.PEROXIDE.source(), 500));
    public static final BedrockOreDefinition TITANIUM = new BedrockOreDefinition("titanium", new PseudoItemStack(ModItems.BEDROCK_ORE), 0xF2EFE2, 2, new PseudoFluidStack(ModFluids.SULFURIC_ACID.source(), 500));
    public static final BedrockOreDefinition TUNGSTEN = new BedrockOreDefinition("tungsten", new PseudoItemStack(ModItems.BEDROCK_ORE), 0x2C293C, 2, new PseudoFluidStack(ModFluids.PEROXIDE.source(), 500));
    public static final BedrockOreDefinition GOLD = new BedrockOreDefinition("gold", new PseudoItemStack(ModItems.BEDROCK_ORE), 0xF9D738, 1);
    public static final BedrockOreDefinition URANIUM = new BedrockOreDefinition("uranium", new PseudoItemStack(ModItems.BEDROCK_ORE), 0x868D82, 4, new PseudoFluidStack(ModFluids.SULFURIC_ACID.source(), 500));
    public static final BedrockOreDefinition THORIUM = new BedrockOreDefinition("thorium", new PseudoItemStack(ModItems.BEDROCK_ORE), 0x7D401D, 4, new PseudoFluidStack(ModFluids.SULFURIC_ACID.source(), 500));
    public static final BedrockOreDefinition FLUORITE = new BedrockOreDefinition("fluorite", new PseudoItemStack(ModItems.BEDROCK_ORE), 0xF6F3E7, 1);

    public static final BedrockOreDefinition COAL = new BedrockOreDefinition("coal", new PseudoItemStack(()->Items.COAL, 8), 0x202020, 1);
    public static final BedrockOreDefinition NITER = new BedrockOreDefinition("niter", new PseudoItemStack(ModItems.NITER, 4), 0x202020, 2, new PseudoFluidStack(ModFluids.PEROXIDE.source(), 500));
    public static final BedrockOreDefinition REDSTONE = new BedrockOreDefinition("redstone", new PseudoItemStack(()->Items.REDSTONE, 4), 0xd01010, 1);
    public static final BedrockOreDefinition EMERALD = new BedrockOreDefinition("emerald", new PseudoItemStack(()->Items.EMERALD, 4), 0x3FDD85, 1);
    public static final BedrockOreDefinition RARE = new BedrockOreDefinition("rare", new PseudoItemStack(ModItems.CHUNK_ORE.get(ItemEnums.EnumChunkType.RARE), 2), 0x8F9999, 2, new PseudoFluidStack(ModFluids.PEROXIDE.source(), 500));
    public static final BedrockOreDefinition BAUXITE = new BedrockOreDefinition("bauxite", new PseudoItemStack(ModBlocks.STONE_RESOURCE.get(BlockEnums.EnumStoneType.BAUXITE), 2), 0xEF7213, 1);
    public static final BedrockOreDefinition GLOWSTONE = new BedrockOreDefinition("glowstone", new PseudoItemStack(()->Items.GLOWSTONE, 4), 0xF9FF4D, 1);
    public static final BedrockOreDefinition PHOSPOROUS = new BedrockOreDefinition("phosporous", new PseudoItemStack(ModItems.POWDER_FIRE, 4), 0xD7341F, 1);
    public static final BedrockOreDefinition QUARTZ = new BedrockOreDefinition("quartz", new PseudoItemStack(()->Items.QUARTZ, 4), 0xF0EFDD, 1);

    // NTM Space Fork ores
//    BedrockOreDefinition nickel = new BedrockOreDefinition(EnumBedrockOre.NICKEL,												2);
//    BedrockOreDefinition cadmium = new BedrockOreDefinition(EnumBedrockOre.CAD, 3, new FluidStack(Fluids.SULFURIC_ACID, 500));
//    BedrockOreDefinition zinc = new BedrockOreDefinition(EnumBedrockOre.ZINC,												2);
//    BedrockOreDefinition lithium = new BedrockOreDefinition(new ItemStack(ModItems.powder_lithium, 4), 1, 0xFFFFFF);
//    BedrockOreDefinition ice = new BedrockOreDefinition(new ItemStack(Blocks.packed_ice, 8), 1, 0xAAFFFF);
//    BedrockOreDefinition coltan = new BedrockOreDefinition(new ItemStack(ModItems.fragment_coltan), 1, 0xA78D7A);
//    BedrockOreDefinition lanthanium = new BedrockOreDefinition(new ItemStack(ModItems.powder_lanthanium), 2, 0xA1B9B9);
//    BedrockOreDefinition schrabidium = new BedrockOreDefinition(new ItemStack(ModItems.powder_schrabidium, 1), 4, 0x00FFFF, new FluidStack(Fluids.NITRIC_ACID, 500));
//    BedrockOreDefinition cinnabar = new BedrockOreDefinition(new ItemStack(ModItems.cinnebar, 4), 1, 0xFF0000);
//    BedrockOreDefinition hematite = new BedrockOreDefinition(DictFrame.fromOne(ModBlocks.stone_resource, EnumStoneType.HEMATITE, 2), 1, 0xEF7213);
    public static void registerBedrockOre(BedrockOreDefinition def, int weight) {
        registerBedrockOre(Level.OVERWORLD, def, weight);
    }
    public static void registerBedrockOre(ResourceKey<Level> levelResourceKey, BedrockOreDefinition def, int weight) {
        WEIGHTED_ORES.computeIfAbsent(levelResourceKey, dim -> new HashMap<>()).put(def, weight);
    }

    private PseudoItemStack itemStack;
    private PseudoFluidStack fluidStack;
    public String id;
    public int tier;
    public int color;

    public BedrockOreDefinition(String id, PseudoItemStack stack, int color, int tier) {
        this(id, stack, color, tier, null);
    }

    public BedrockOreDefinition(String id, PseudoItemStack stack, int color,  int tier, PseudoFluidStack fluidStack) {
        this.itemStack = stack;
        this.id = id;
        this.tier = tier;
        this.color = color;
        this.fluidStack = fluidStack;
        DEFINITIONS.put(id, this);
    }

    public ItemStack getItemStack(){
        ItemStack stack = this.itemStack.get();
        if (stack.getItem() instanceof ItemBedrockOre) {
            ItemBedrockOre.setDefinition(stack, this.id);
        }
        return stack;
    }
    
    public FluidStack getAcid(){
        return this.fluidStack == null ? FluidStack.EMPTY : this.fluidStack.get();
    }

    public static int getTier(double density) {
        if(density > 1.5) return 4;
        if(density > 1) return 3;
        if(density > 0.75) return 2;
        return 1;
    }

    public static final PseudoFluidStack BORE_TIER_1 = null;
    public static final PseudoFluidStack BORE_TIER_2 = new PseudoFluidStack(()->Fluids.WATER, 1_000);
    public static final PseudoFluidStack BORE_TIER_3 = new PseudoFluidStack(ModFluids.SULFURIC_ACID.source(), 1_000);
    public static final PseudoFluidStack BORE_TIER_4 = new PseudoFluidStack(ModFluids.SOLVENT.source(), 2_000);

    public static FluidStack getBoreFluid(double density) {
        if(density > 1.5) return BORE_TIER_4.get();
        if(density > 1) return BORE_TIER_3.get();
        if(density > 0.75) return BORE_TIER_2.get();
        return FluidStack.EMPTY;
    }
}
