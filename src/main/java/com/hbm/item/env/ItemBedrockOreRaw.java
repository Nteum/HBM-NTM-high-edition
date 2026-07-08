package com.hbm.item.env;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.item.interfaces.CreativeTabVariantItem;
import com.hbm.registries.HBMDimensions;
import com.hbm.utils.data.NBTHelper;
import com.hbm.utils.data.RegistryUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemBedrockOreRaw extends Item implements CreativeTabVariantItem {
    public ItemBedrockOreRaw(Properties pProperties) {
        super(pProperties);
    }

    public static void setOreAmount(Level world, ItemStack stack, int x, int z, double mult) {
        ResourceKey<Level> dimension = world.dimension();
        ItemBedrockOreCombine.CelestialBedrockOre celestialBedrockOre = ItemBedrockOreCombine.CelestialBedrockOre.get(dimension);
        if (celestialBedrockOre == null) return;
        stack.getOrCreateTag().putString(HBMKey.DIMENSION, world.dimension().location().toString());
        for (ItemBedrockOreCombine.CelestialBedrockOreType type : celestialBedrockOre.types) {
            stack.getOrCreateTag().putDouble(type.suffix, getOreLevel((ServerLevel) world, x, z, type));
        }
    }

    public static double getOreAmount(ItemStack stack, ItemBedrockOreCombine.CelestialBedrockOreType type) {
        return NBTHelper.getDouble(stack, type.suffix, 1);
    }

    public static ResourceKey<Level> getOreBody(ItemStack stack) {
        String dimsion = NBTHelper.getStr(stack, HBMKey.DIMENSION, "");
        return ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimsion));
    }

    public static double getOreLevel(ServerLevel level, int x, int z, ItemBedrockOreCombine.CelestialBedrockOreType type) {
        // 💡 1. 解决种子与维度 ID 的问题
        // 高版本通过 level.getServer().getWorldData().worldGenOptions().seed() 拿到全局种子
        // 通过 level.dimension().location().toString().hashCode() 代替原本的 dimensionId
        long worldSeed = level.getServer().getWorldData().worldGenOptions().seed();
        int dimensionHash = level.dimension().location().toString().hashCode();
        long baseSeed = worldSeed + dimensionHash;

        // 💡 2. 解决老版本 NoiseGeneratorPerlin 的构建
        // 高版本使用 ImprovedNoise 作为最底层、高效率的单八度柏林噪声实现
        // 使用 XoroshiroRandomSource 替代老版的旧 Random 算法（更高效且不会产生周期性断层）
        ImprovedNoise levelNoise = new ImprovedNoise(new XoroshiroRandomSource(baseSeed));

        // 老版本是 type.index（即矿物的整型 ID），这里可以用我们前面在定义里写好的 type.order
        long oreSeed = baseSeed - 4096L + type.index;
        ImprovedNoise oreNoise = new ImprovedNoise(new XoroshiroRandomSource(oreSeed));

        double scale = 0.01D;

        // 💡 3. 计算噪声值
        // 老版本的 func_151601_a 相当于高版本的噪声采样器。
        // 注意：高版本的 ImprovedNoise.noise 传入的 Y 轴（第二个参数）和 Z 轴（第三个参数）不能为 0，
        // 否则会退化成 2D 截面噪声。原版习惯在不写 Y 轴时，用 0.0D 代替。
        double levelValue = levelNoise.noise(x * scale, 0.0D, z * scale);
        double oreValue = oreNoise.noise(x * scale, 0.0D, z * scale);

        // 💡 4. 完美继承原版的数学公式和夹逼限制 (MathHelper.clamp_double -> Mth.clamp)
        double rawResult = Math.abs(levelValue * oreValue) * 0.05D;
        return Mth.clamp(rawResult, 0.0D, 2.0D);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, pLevel, pTooltipComponents, pIsAdvanced);
        ResourceKey<Level> body = getOreBody(stack);
        pTooltipComponents.add(HBMLang.ITEM_BECROCK_ORE_RAW.translate(Component.translatable(RegistryUtils.getDescriptionId(body))));
        ItemBedrockOreCombine.CelestialBedrockOre celestialBedrockOre = ItemBedrockOreCombine.CelestialBedrockOre.get(body);
        if (celestialBedrockOre != null){
            for (ItemBedrockOreCombine.CelestialBedrockOreType type : celestialBedrockOre.types) {
                double amount = getOreAmount(stack, type);
                pTooltipComponents.add(Component.translatable(HBMLang.descId("item", "bedrock_ore_" + type.suffix)).append(": " +  ((int) (amount * 100)) / 100D + "(").append(ItemBedrockOreScanner.translateDensity(amount).withStyle(ItemBedrockOreScanner.getColor(amount))).append(")"));
            }
        }
    }

    @Override
    public void fillCreativeTab(BuildCreativeModeTabContentsEvent event) {
        for (ResourceKey<Level> key : HBMDimensions.LEVELS) {
            ItemStack stack = this.getDefaultInstance();
            stack.getOrCreateTag().putString(HBMKey.DIMENSION, key.location().toString());
            event.getEntries().put(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
