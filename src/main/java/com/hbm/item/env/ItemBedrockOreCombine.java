package com.hbm.item.env;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.material.HBMMatter;
import com.hbm.datagen.model.ItemModelGen;
import com.hbm.main.ClientEventHandler;
import com.hbm.registries.HBMDimensions;
import com.hbm.registries.HBMMatters;
import com.hbm.registries.ModItems;
import com.hbm.render.item.IMultiLayerItem;
import com.hbm.utils.data.NBTHelper;
import com.hbm.utils.math.BitUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

import static com.hbm.item.env.ItemBedrockOreCombine.ProcessingTrait.*;

// 复合型基岩矿
public class ItemBedrockOreCombine extends ItemBedrockOre {
    public ItemBedrockOreCombine(Properties pProperties) {
        super(pProperties);
    }

    record BedrockOreOutput(HBMMatter mat, int amount) { }
    public static BedrockOreOutput o(HBMMatter mat, int amount) {
        return new BedrockOreOutput(mat, amount);
    }

    // BEDROCK_ORE_COMPLEX 获取物品属性的功能
    public static float getGradeProperty(ItemStack stack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed){
        BedrockOreGrade grade = getGrade(stack);
        return grade != null ? grade.ordinal() : 0;
    }
    public static float getTypeProperty(ItemStack stack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed){
        CelestialBedrockOreType type = getType(stack);
        return type != null ? Math.abs(type.suffix.hashCode()) : 0;
    }
    // BEDROCK_ORE_COMPLEX生成模型的过程
    public static void genModel(ItemModelGen provider){
        ResourceLocation path = ItemModelGen.getPath(ModItems.BEDROCK_ORE_COMPLEX.get());
        ItemModelBuilder builder = provider.getBuilder(path.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"));
        List<ResourceLocation> layers = new ArrayList<>();
        for (BedrockOreGrade grade : BedrockOreGrade.values()) {
            HashSet<Integer> integers = new HashSet<>(CelestialBedrockOre.hashes);
            for (CelestialBedrockOreType oreType : CelestialBedrockOre.oreTypes) {
                if (!integers.contains(oreType.suffix.hashCode())) continue;
                layers.clear();
                layers.add(HBM.rl("bedrock_ore_" + grade.prefix + "_" + oreType.suffix));
                for (ProcessingTrait trait : grade.traits) {
                    layers.add(HBM.rl("bedrock_ore_overlay." + trait.name().toLowerCase()));
                }
                builder.override().predicate(ItemModelGen.property_stage, grade.ordinal()).predicate(ItemModelGen.property_type, Math.abs(oreType.suffix.hashCode()))
                        .model(provider.multiLayerItem(HBM.rl("bedrock_ore_" + grade.name().toLowerCase() + "_" + oreType.suffix), true, layers.toArray(ResourceLocation[]::new))).end();
                integers.remove(oreType.suffix.hashCode());
            }
        }
    }
    // BEDROCK_ORE_COMPLEX 对应颜色
    public static int getColor(ItemStack stack){
        BedrockOreGrade grade = getGrade(stack);
        CelestialBedrockOreType type = getType(stack);
        return grade == null ? 0xFFFFFF : type == null ? grade.tint : BitUtil.blendColor(grade.tint, type.light);
    }

    @Override
    public void fillCreativeTab(BuildCreativeModeTabContentsEvent event) {
        for (BedrockOreGrade grade : BedrockOreGrade.values()) {
            for (CelestialBedrockOreType oreType : CelestialBedrockOre.oreTypes) {
                event.getEntries().put(make(grade, oreType), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        MutableComponent name = Component.empty();
        CelestialBedrockOreType type = getType(stack);
        BedrockOreGrade grade = getGrade(stack);
        if (grade != null) name.append(HBMLang.valueOf(grade.prefix).translate()).append(" ");
        if (type != null) name.append(HBMLang.valueOf(type.suffix).translate()).append(" ");
        return name.append(super.getName(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, pLevel, pTooltipComponents, pIsAdvanced);
        CelestialBedrockOreType type = getType(stack);
        BedrockOreGrade grade = getGrade(stack);
        if (grade == null || type == null) return;
        for (ProcessingTrait trait : grade.traits) {
            pTooltipComponents.add(HBMLang.valueOf(trait.name().toLowerCase()).translate());
        }
        if (grade == BedrockOreGrade.BASE){
            pTooltipComponents.add(HBMLang.ITEM_ORE_BEDROCK_DESC1.translate().withStyle(ChatFormatting.GRAY));
            pTooltipComponents.add(HBMLang.ITEM_ORE_BEDROCK_DESC2.translate(type.primary.mat).withStyle(ChatFormatting.GRAY));
            pTooltipComponents.add(HBMLang.ITEM_ORE_BEDROCK_DESC3.translate(type.byproductAcid.mat.name()).withStyle(ChatFormatting.GRAY));
            pTooltipComponents.add(HBMLang.ITEM_ORE_BEDROCK_DESC4.translate(type.byproductSolvent.mat.name()).withStyle(ChatFormatting.GRAY));
            pTooltipComponents.add(HBMLang.ITEM_ORE_BEDROCK_DESC5.translate(type.byproductRad.mat.name()).withStyle(ChatFormatting.GRAY));
        }
    }

//    @Override
//    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {
//        int meta = stack.getItemDamage();
//        BedrockOreGrade grade = this.getGrade(meta);
//        CelestialBedrockOreType type = this.getType(meta);
//
//        for(ProcessingTrait trait : grade.traits) {
//            list.add(I18nUtil.resolveKey(this.getUnlocalizedNameInefficiently(stack) + ".trait." + trait.name().toLowerCase(Locale.US)));
//        }
//
//        if(grade == BedrockOreGrade.BASE) {
//            list.add(EnumChatFormatting.DARK_GRAY + "Processing outputs");
//            list.add(EnumChatFormatting.DARK_GRAY + "Main: " + EnumChatFormatting.GRAY + I18nUtil.resolveKey(type.primary.mat.getUnlocalizedName()));
//            list.add(EnumChatFormatting.DARK_GRAY + "Sulfuric: " + EnumChatFormatting.GRAY + I18nUtil.resolveKey(type.byproductAcid.mat.getUnlocalizedName()));
//            list.add(EnumChatFormatting.DARK_GRAY + "Solvent: " + EnumChatFormatting.GRAY + I18nUtil.resolveKey(type.byproductSolvent.mat.getUnlocalizedName()));
//            list.add(EnumChatFormatting.DARK_GRAY + "HPS: " + EnumChatFormatting.GRAY + I18nUtil.resolveKey(type.byproductRad.mat.getUnlocalizedName()));
//        }
//    }

    public enum ProcessingTrait {
        ROASTED, ARC, WASHED, CENTRIFUGED, SULFURIC, SOLVENT, RAD
    }

    public static class CelestialBedrockOreType {
        ResourceKey<Level> dim;
        public int index; int light; int dark;
        public String suffix;
        BedrockOreOutput primary;
        BedrockOreOutput byproductAcid;
        BedrockOreOutput byproductSolvent;
        BedrockOreOutput byproductRad;
    }

    public static class CelestialBedrockOre {
        // TODO: rebalance cryolite, Laythe gets regular aluminium for now
        static {
            //					primary						sulfuric					solvent						dcm
            register(
//                    HBMDimensions.KERBIN,
                    Level.OVERWORLD,    // 航天版里写的是KERBIN，根据代码分析和游戏测试，感觉这个应该指的是主世界
                    T("light", o(HBMMatters.IRON, 18), o(HBMMatters.COPPER, 9), o(HBMMatters.CRYOLITE, 6), o(HBMMatters.SODIUM, 3)),
                    T("heavy", o(HBMMatters.TUNGSTEN, 18), o(HBMMatters.TUNGSTEN, 9), o(HBMMatters.ZINC, 6), o(HBMMatters.ZINC, 3)),
                    T("nonmetal", o(HBMMatters.COAL, 18), o(HBMMatters.LIGNITE, 9), o(HBMMatters.SULFUR, 6), o(HBMMatters.KNO, 3)),
                    T("crystal", o(HBMMatters.REDSTONE, 18), o(HBMMatters.ASBESTOS, 9), o(HBMMatters.DIAMOND, 6), o(HBMMatters.EMERALD, 3))
            );

            register(
                    HBMDimensions.MOON_KEY,
                    T("light", o(HBMMatters.LITHIUM, 18), o(HBMMatters.IRON, 9), o(HBMMatters.SODIUM, 6), o(HBMMatters.CHLOROCALCITE, 3)),
                    T("heavy", o(HBMMatters.LEAD, 18), o(HBMMatters.ZINC, 9), o(HBMMatters.GOLD, 6), o(HBMMatters.BISMUTH, 3)),
                    T("rare", o(HBMMatters.COBALT, 18), o(HBMMatters.RAREEARTH, 9), o(HBMMatters.NEODYMIUM, 6), o(HBMMatters.STRONTIUM, 3)),
                    T("nonmetal", o(HBMMatters.SULFUR, 18), o(HBMMatters.FLUORITE, 9), o(HBMMatters.KNO, 6), o(HBMMatters.SILICON, 3)),
                    T("crystal", o(HBMMatters.QUARTZ, 18), o(HBMMatters.SODALITE, 9), o(HBMMatters.EMERALD, 6), o(HBMMatters.CINNABAR, 3))
            );

            register(
                    HBMDimensions.MINMUS,
                    T("light", o(HBMMatters.COPPER, 18), o(HBMMatters.TITANIUM, 9), o(HBMMatters.CHLOROCALCITE, 6), o(HBMMatters.COPPER, 3)),
                    T("heavy", o(HBMMatters.LEAD, 18), o(HBMMatters.GOLD, 9), o(HBMMatters.TUNGSTEN, 6), o(HBMMatters.BISMUTH, 3)),
                    T("rare", o(HBMMatters.ZIRCONIUM, 18), o(HBMMatters.BORON, 9), o(HBMMatters.COBALT, 6), o(HBMMatters.STRONTIUM, 3)),
                    T("nonmetal", o(HBMMatters.SULFUR, 18), o(HBMMatters.KNO, 9), o(HBMMatters.FLUORITE, 6), o(HBMMatters.SILICON, 3)),
                    T("crystal", o(HBMMatters.EMERALD, 18), o(HBMMatters.SODALITE, 9), o(HBMMatters.DIAMOND, 6), o(HBMMatters.EMERALD, 3))
            );

            register(
                    HBMDimensions.DUNA,
                    T("light", o(HBMMatters.IRON, 18), o(HBMMatters.NICKEL, 9), o(HBMMatters.TITANIUM, 6), o(HBMMatters.CHLOROCALCITE, 3)),
                    T("heavy", o(HBMMatters.BERYLLIUM, 18), o(HBMMatters.TUNGSTEN, 9), o(HBMMatters.ZINC, 6), o(HBMMatters.BISMUTH, 3)),
                    T("rare", o(HBMMatters.RAREEARTH, 18), o(HBMMatters.BORON, 9), o(HBMMatters.ZIRCONIUM, 6), o(HBMMatters.STRONTIUM, 3)),
                    T("actinide", o(HBMMatters.THORIUM, 18), o(HBMMatters.RADIUM, 9), o(HBMMatters.POLONIUM, 6), o(HBMMatters.U233, 3)),
                    T("nonmetal", o(HBMMatters.FLUORITE, 18), o(HBMMatters.SULFUR, 9), o(HBMMatters.SILICON, 6), o(HBMMatters.PHOSPHORUS, 3)),
                    T("crystal", o(HBMMatters.REDSTONE, 18), o(HBMMatters.CINNABAR, 9), o(HBMMatters.DIAMOND, 6), o(HBMMatters.MOLYSITE, 3))
            );

            register(
                    HBMDimensions.MOHO,
                    T("light", o(HBMMatters.TITANIUM, 18), o(HBMMatters.CHLOROCALCITE, 9), o(HBMMatters.NICKEL, 6), o(HBMMatters.LITHIUM, 3)),
                    T("heavy", o(HBMMatters.GOLD, 18), o(HBMMatters.ZINC, 9), o(HBMMatters.LEAD, 6), o(HBMMatters.BISMUTH, 3)),
                    T("rare", o(HBMMatters.NEODYMIUM, 18), o(HBMMatters.ZIRCONIUM, 9), o(HBMMatters.BROMINE, 6), o(HBMMatters.STRONTIUM, 3)),
                    T("actinide", o(HBMMatters.AUSTRALIUM, 18), o(HBMMatters.AUSTRALIUM, 9), o(HBMMatters.TASMANITE, 6), o(HBMMatters.AYERITE, 3)),
                    T("nonmetal", o(HBMMatters.GLOWSTONE, 18), o(HBMMatters.PHOSPHORUS, 9), o(HBMMatters.SULFUR, 6), o(HBMMatters.PHOSPHORUS_W, 3)),
                    T("crystal", o(HBMMatters.CINNABAR, 18), o(HBMMatters.REDSTONE, 9), o(HBMMatters.QUARTZ, 6), o(HBMMatters.MOLYSITE, 3))
            );

            register(
                    HBMDimensions.DRES,
                    T("light", o(HBMMatters.NICKEL, 18), o(HBMMatters.TITANIUM, 9), o(HBMMatters.CADMIUM, 6), o(HBMMatters.GALLIUM, 3)),
                    T("heavy", o(HBMMatters.ZINC, 18), o(HBMMatters.GOLD, 9), o(HBMMatters.BISMUTH, 6), o(HBMMatters.ARSENIC, 3)),
                    T("rare", o(HBMMatters.TANTALIUM, 18), o(HBMMatters.LANTHANIUM, 9), o(HBMMatters.NIOBIUM, 6), o(HBMMatters.STRONTIUM, 3)),
                    T("actinide", o(HBMMatters.URANIUM, 18), o(HBMMatters.RADIUM, 9), o(HBMMatters.TECHNETIUM, 6), o(HBMMatters.U238, 3)),
                    T("nonmetal", o(HBMMatters.SILICON, 18), o(HBMMatters.SILICON, 9), o(HBMMatters.FLUORITE, 6), o(HBMMatters.FLUORITE, 3)),
                    T("crystal", o(HBMMatters.DIAMOND, 18), o(HBMMatters.BORAX, 9), o(HBMMatters.MOLYSITE, 6), o(HBMMatters.MOLYSITE, 3))
            );

            register(
                    HBMDimensions.EVE,
                    T("light", o(HBMMatters.SODIUM, 18), o(HBMMatters.CHLOROCALCITE, 9), o(HBMMatters.IRON, 6), o(HBMMatters.CO60, 3)),
                    T("heavy", o(HBMMatters.TUNGSTEN, 18), o(HBMMatters.LEAD, 9), o(HBMMatters.ARSENIC, 6), o(HBMMatters.PB209, 3)),
                    T("rare", o(HBMMatters.NIOBIUM, 18), o(HBMMatters.STRONTIUM, 9), o(HBMMatters.IODINE, 6), o(HBMMatters.AU198, 3)),
                    T("actinide", o(HBMMatters.PLUTONIUM, 18), o(HBMMatters.POLONIUM, 9), o(HBMMatters.NEPTUNIUM, 6), o(HBMMatters.PU239, 3)),
                    T("schrabidic", o(HBMMatters.SCHRABIDIUM, 18), o(HBMMatters.SOLINIUM, 9), o(HBMMatters.GHIORSIUM, 6), o(HBMMatters.SCHRABIDIUM, 3)),
                    T("crystal", o(HBMMatters.SODALITE, 18), o(HBMMatters.MOLYSITE, 9), o(HBMMatters.DIAMOND, 6), o(HBMMatters.BORAX, 3))
            );

            register(
                    HBMDimensions.IKE,
                    T("light", o(HBMMatters.COPPER, 18), o(HBMMatters.BAUXITE, 9), o(HBMMatters.NICKEL, 6), o(HBMMatters.SODIUM, 3)),
                    T("heavy", o(HBMMatters.LEAD, 18), o(HBMMatters.ZINC, 9), o(HBMMatters.GOLD, 6), o(HBMMatters.ARSENIC, 3)),
                    T("rare", o(HBMMatters.BORON, 18), o(HBMMatters.NEODYMIUM, 9), o(HBMMatters.STRONTIUM, 6), o(HBMMatters.LANTHANIUM, 3)),
                    T("hazard", o(HBMMatters.URANIUM, 18), o(HBMMatters.U238, 9), o(HBMMatters.PLUTONIUM, 6), o(HBMMatters.TECHNETIUM, 3))
            );

            register(
                    HBMDimensions.LAYTHE,
                    T("light", o(HBMMatters.ALUMINIUM, 18), o(HBMMatters.TITANIUM, 9), o(HBMMatters.GALLIUM, 6), o(HBMMatters.HAFNIUM, 3)),
                    T("heavy", o(HBMMatters.BERYLLIUM, 18), o(HBMMatters.TUNGSTEN, 9), o(HBMMatters.LEAD, 6), o(HBMMatters.ARSENIC, 3)),
                    T("rare", o(HBMMatters.RAREEARTH, 18), o(HBMMatters.NEODYMIUM, 9), o(HBMMatters.STRONTIUM, 6), o(HBMMatters.NIOBIUM, 3)),
                    T("actinide", o(HBMMatters.URANIUM, 18), o(HBMMatters.THORIUM, 9), o(HBMMatters.POLONIUM, 6), o(HBMMatters.U235, 3)),
                    T("nonmetal", o(HBMMatters.CHLOROCALCITE, 18), o(HBMMatters.COAL, 9), o(HBMMatters.FLUORITE, 6), o(HBMMatters.SILICON, 3)),
                    T("crystal", o(HBMMatters.ASBESTOS, 18), o(HBMMatters.SODALITE, 9), o(HBMMatters.DIAMOND, 6), o(HBMMatters.SODALITE, 3))
            );
            register(
                    HBMDimensions.TEKTO,
                    T("light", o(HBMMatters.TITANIUM, 18), o(HBMMatters.COPPER, 9), o(HBMMatters.NICKEL, 6), o(HBMMatters.LITHIUM, 3)),
                    T("heavy", o(HBMMatters.BERYLLIUM, 18), o(HBMMatters.LEAD, 9), o(HBMMatters.ZINC, 6), o(HBMMatters.ZINC, 3)),
                    T("rare", o(HBMMatters.BORON, 18), o(HBMMatters.RAREEARTH, 9), o(HBMMatters.TANTALIUM, 6), o(HBMMatters.BISMUTH, 3)),
                    T("actinide", o(HBMMatters.URANIUM, 18), o(HBMMatters.NEPTUNIUM, 9), o(HBMMatters.RADIUM, 6), o(HBMMatters.TECHNETIUM, 3)),
                    T("crystal", o(HBMMatters.EMERALD, 18), o(HBMMatters.SILICON, 9), o(HBMMatters.MOLYSITE, 6), o(HBMMatters.BORAX, 3)),
                    T("plastic", o(HBMMatters.POLYMER, 18), o(HBMMatters.RUBBER, 9), o(HBMMatters.SEMTEX, 6), o(HBMMatters.PVC, 3))
            );
			/*
			register(
				HBMDimensions.THATMO,
				T("light",		o(MAT_CRYOLITE, 24),		o(MAT_TITANIUM, 12),			o(MAT_GALLIUM, 6),		o(MAT_HAFNIUM, 3)),
				T("heavy",		o(MAT_BERYLLIUM, 24),	o(MAT_TUNGSTEN, 12),			o(MAT_LEAD, 6),			o(MAT_ARSENIC, 3)),
				T("rare",		o(MAT_RAREEARTH, 24),	o(MAT_NEODYMIUM, 12),		o(MAT_STRONTIUM, 6),		o(MAT_NIOBIUM, 3)),
				T("actinide",	o(MAT_URANIUM, 24),		o(MAT_THORIUM, 12),			o(MAT_POLONIUM, 6),		o(MAT_U235, 3)),
				T("nonmetal",	o(MAT_CHLOROCALCITE, 24),o(MAT_COAL, 12),				o(MAT_FLUORITE, 6),		o(MAT_SILICON, 3)),
				T("crystal",		o(MAT_ASBESTOS, 24),		o(MAT_SODALITE, 12),			o(MAT_DIAMOND, 6),		o(MAT_SODALITE, 3))
				);
				*/
        }

        public CelestialBedrockOreType[] types;

        private CelestialBedrockOre(CelestialBedrockOreType... types) {
            this.types = types;
        }

        private static HashMap<ResourceKey<Level>, CelestialBedrockOre> oreMap;
        public static List<CelestialBedrockOreType> oreTypes;
        private static Set<Integer> hashes;

        private static void register(ResourceKey<Level> body, CelestialBedrockOreType... types) {
            if(oreMap == null) oreMap = new HashMap<>();
//            if(oreTypes == null) oreTypes = new ArrayList<>();
            oreMap.put(body, new CelestialBedrockOre(types));
            for(CelestialBedrockOreType type : types) {
                type.dim = body;
//                oreTypes.add(type);
            }
        }

        public static CelestialBedrockOre get(ResourceKey<Level> dim) {
            return oreMap.get(dim);
        }

//        public static List<CelestialBedrockOreType> getAllTypes() {
//            return oreTypes;
//        }
//
//        public static int getTotalTypeCount() {
//            return index + 1;
//        }
//
//        private static int index;

        private static CelestialBedrockOreType T(String suffix, BedrockOreOutput primary, BedrockOreOutput byproductAcid, BedrockOreOutput byproductSolvent, BedrockOreOutput byproductRad) {
            if (hashes == null) hashes = new HashSet<>();
            if (!hashes.contains(suffix.hashCode())) hashes.add(suffix.hashCode());
            CelestialBedrockOreType type = new CelestialBedrockOreType();
            if (oreTypes == null) oreTypes = new ArrayList<>();
            oreTypes.add(type);
            type.index = oreTypes.size() - 1;

            type.suffix = suffix;
            type.primary = primary;
            type.byproductAcid = byproductAcid;
            type.byproductSolvent = byproductSolvent;
            type.byproductRad = byproductRad;

            // Colours are autogenerated because otherwise I'd have to manually specify like 80 different colours
            type.light = getAverageColor(false, primary, byproductAcid, byproductSolvent, byproductRad);
            type.dark = getAverageColor(true, primary, byproductAcid, byproductSolvent, byproductRad);

            return type;
        }

        private static int getAverageColor(boolean dark, BedrockOreOutput... outputs) {
            int r = 0, g = 0, b = 0;

            for(BedrockOreOutput output : outputs) {
                int color = dark ? output.mat.solidColorDark : output.mat.solidColorLight;
                r += color >> 16 & 255;
                g += color >> 8 & 255;
                b += color & 255;
            }

            r /= outputs.length;
            g /= outputs.length;
            b /= outputs.length;

            return r << 16 | g << 8 | b;
        }
    }

    public static final int none = 0xFFFFFF;
    public static final int roasted = 0xCFCFCF;
    public static final int arc = 0xC3A2A2;
    public static final int washed = 0xDBE2CB;

    public enum BedrockOreGrade {
        BASE(none, "base"),												//from the slopper
        BASE_ROASTED(roasted, "base", ROASTED),							//optional combination oven step, yields vitriol
        BASE_WASHED(washed, "base", WASHED),							//primitive-ass acidizer with water
        PRIMARY(none, "primary", CENTRIFUGED),							//centrifuging for more primary
        PRIMARY_ROASTED(roasted, "primary", ROASTED),					//optional comboven
        PRIMARY_SULFURIC(0xFFFFD3, "primary", SULFURIC),				//sulfuric acid
        PRIMARY_NOSULFURIC(0xD3D4FF, "primary", CENTRIFUGED, SULFURIC),	//from centrifuging, sulfuric byproduct removed
        PRIMARY_SOLVENT(0xD3F0FF, "primary", SOLVENT),					//solvent
        PRIMARY_NOSOLVENT(0xFFDED3, "primary", CENTRIFUGED, SOLVENT),	//solvent byproduct removed
        PRIMARY_RAD(0xECFFD3, "primary", RAD),							//radsolvent
        PRIMARY_NORAD(0xEBD3FF, "primary", CENTRIFUGED, RAD),			//radsolvent byproduct removed
        PRIMARY_FIRST(0xFFD3D4, "primary", CENTRIFUGED),				//higher first material yield
        PRIMARY_SECOND(0xD3FFEB, "primary", CENTRIFUGED),				//higher second material yield
        CRUMBS(none, "crumbs", CENTRIFUGED),							//endpoint for primary, recycling

        SULFURIC_BYPRODUCT(none, "sulfuric", CENTRIFUGED, SULFURIC),	//from centrifuging
        SULFURIC_ROASTED(roasted, "sulfuric", ROASTED, SULFURIC),		//comboven again
        SULFURIC_ARC(arc, "sulfuric", ARC, SULFURIC),					//alternate step
        SULFURIC_WASHED(washed, "sulfuric", WASHED, SULFURIC),			//sulfuric endpoint

        SOLVENT_BYPRODUCT(none, "solvent", CENTRIFUGED, SOLVENT),		//from centrifuging
        SOLVENT_ROASTED(roasted, "solvent", ROASTED, SOLVENT),			//comboven again
        SOLVENT_ARC(arc, "solvent", ARC, SOLVENT),						//alternate step
        SOLVENT_WASHED(washed, "solvent", WASHED, SOLVENT),				//solvent endpoint

        RAD_BYPRODUCT(none, "rad", CENTRIFUGED, RAD),					//from centrifuging
        RAD_ROASTED(roasted, "rad", ROASTED, RAD),						//comboven again
        RAD_ARC(arc, "rad", ARC, RAD),									//alternate step
        RAD_WASHED(washed, "rad", WASHED, RAD);							//rad endpoint

        public int tint;
        public String prefix;
        public ProcessingTrait[] traits;

        BedrockOreGrade(int tint, String prefix, ProcessingTrait... traits) {
            this.tint = tint;
            this.prefix = prefix;
            this.traits = traits;
        }
    }

    public static ItemStack make(BedrockOreGrade grade, CelestialBedrockOreType type) {
        return make(grade, type, 1);
    }

    public static ItemStack make(BedrockOreGrade grade, CelestialBedrockOreType type, int amount) {
        ItemStack stack = new ItemStack(ModItems.BEDROCK_ORE_COMPLEX.get(), amount);
        stack.getOrCreateTag().putInt(HBMKey.GRADE, grade.ordinal());
        stack.getOrCreateTag().putInt(HBMKey.TYPE, type.index);
        return stack;
    }

    public static BedrockOreGrade getGrade(ItemStack stack) {
        return NBTHelper.getEnum(stack, HBMKey.GRADE, null, BedrockOreGrade.class);
    }

    public static CelestialBedrockOreType getType(ItemStack stack) {
        return NBTHelper.getList(stack, HBMKey.TYPE, null, CelestialBedrockOre.oreTypes);
    }
}
