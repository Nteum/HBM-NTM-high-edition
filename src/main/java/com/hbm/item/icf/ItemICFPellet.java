package com.hbm.item.icf;

import com.hbm.Inventory.fluid.ModFluids;

import com.hbm.registries.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Simplified ICF fuel pellet implementation. Each pellet stores two fuel
 * constituents and tracks depletion produced by laser heating in the reactor.
 */
public class ItemICFPellet extends Item {

    public static final String TAG_TYPE_ONE = "type1";
    public static final String TAG_TYPE_TWO = "type2";
    public static final String TAG_MUON = "muon";
    public static final String TAG_DEPLETION = "depletion";

    private static final long BASE_DEPLETION = 50_000_000_000L;
    private static final long BASE_FUSING_DIFFICULTY = 10_000_000L;
    private static final Map<FuelType, List<Supplier<? extends ItemLike>>> FUEL_SOURCES = new EnumMap<>(FuelType.class);
    private static final Map<FuelType, List<Supplier<? extends Fluid>>> FLUID_SOURCES = new EnumMap<>(FuelType.class);

    static {
        registerFuel(FuelType.DEUTERIUM, ModItems.CELL_DEUTERIUM);
        registerFuel(FuelType.TRITIUM, ModItems.CELL_TRITIUM);
        registerFluid(FuelType.HYDROGEN, ModFluids.HYDROGEN.source());
        registerFluid(FuelType.DEUTERIUM, ModFluids.DEUTERIUM.source());
        registerFluid(FuelType.TRITIUM, ModFluids.TRITIUM.source());
        registerFluid(FuelType.HELIUM3, ModFluids.HELIUM3.source());
        registerFluid(FuelType.HELIUM4, ModFluids.HELIUM4.source());
        registerFluid(FuelType.OXYGEN, ModFluids.OXYGEN.source());
        registerFluid(FuelType.CHLORINE, ModFluids.CHLORINE.source());
        registerFuel(FuelType.LITHIUM, ModItems.LITHIUM);
        registerFuel(FuelType.BERYLLIUM, ModItems.INGOT_BERYLLIUM);
        registerFuel(FuelType.BORON, ModItems.INGOT_BORON);
        registerFuel(FuelType.CARBON, ModItems.INGOT_GRAPHITE);
        registerFuel(FuelType.SODIUM, ModItems.POWDER_SODIUM);
        registerFuel(FuelType.CHLORINE, ModItems.CHLORINE_PINWHEEL);
        registerFuel(FuelType.CALCIUM, ModItems.INGOT_CALCIUM);
    }

    public enum FuelType {
        HYDROGEN(0x4040FF, 1.00D, 0.85D, 1.00D),
        DEUTERIUM(0x2828CB, 1.25D, 1.00D, 1.00D),
        TRITIUM(0x000092, 1.50D, 1.00D, 1.05D),
        HELIUM3(0xFFF09F, 1.75D, 1.00D, 1.25D),
        HELIUM4(0xFF9B60, 2.00D, 1.00D, 1.50D),
        LITHIUM(0xE9E9E9, 1.25D, 0.85D, 2.00D),
        BERYLLIUM(0xA79D80, 2.00D, 1.00D, 2.50D),
        BORON(0x697F89, 3.00D, 0.50D, 3.50D),
        CARBON(0x454545, 2.00D, 1.00D, 5.00D),
        OXYGEN(0xB4E2FF, 1.25D, 1.50D, 7.50D),
        SODIUM(0xDFE4E7, 3.00D, 0.75D, 8.75D),
        CHLORINE(0xDAE598, 2.50D, 1.00D, 10.0D),
        CALCIUM(0xD2C7A9, 3.00D, 1.00D, 12.5D);

        public final int color;
        public final double reactionMultiplier;
        public final double depletionSpeed;
        public final double fusingDifficulty;

        FuelType(int color, double reactionMultiplier, double depletionSpeed, double fusingDifficulty) {
            this.color = color;
            this.reactionMultiplier = reactionMultiplier;
            this.depletionSpeed = depletionSpeed;
            this.fusingDifficulty = fusingDifficulty;
        }

        public static FuelType fromIndex(int index) {
            FuelType[] values = values();
            if (index < 0 || index >= values.length) {
                return DEUTERIUM;
            }
            return values[index];
        }
    }

    public ItemICFPellet(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static FuelType getFuel(ItemStack stack, boolean primary) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return primary ? FuelType.DEUTERIUM : FuelType.TRITIUM;
        }
        int idx = tag.getByte(primary ? TAG_TYPE_ONE : TAG_TYPE_TWO);
        return FuelType.fromIndex(idx);
    }

    public static void setFuelTypes(ItemStack stack, FuelType primary, FuelType secondary, boolean muonCatalysed) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putByte(TAG_TYPE_ONE, (byte) primary.ordinal());
        tag.putByte(TAG_TYPE_TWO, (byte) secondary.ordinal());
        tag.putBoolean(TAG_MUON, muonCatalysed);
        tag.putLong(TAG_DEPLETION, 0L);
    }

    public static ItemStack createStack(FuelType primary, FuelType secondary, boolean muon) {
        ItemStack stack = new ItemStack(ModItems.icf_pellet.get());
        setFuelTypes(stack, primary, secondary, muon);
        return stack;
    }

    public static long getMaxDepletion(ItemStack stack) {
        FuelType primary = getFuel(stack, true);
        FuelType secondary = getFuel(stack, false);
        double value = BASE_DEPLETION;
        value /= primary.depletionSpeed;
        value /= secondary.depletionSpeed;
        return (long) value;
    }

    public static long getDepletion(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return 0L;
        }
        return tag.getLong(TAG_DEPLETION);
    }

    public static void setDepletion(ItemStack stack, long value) {
        stack.getOrCreateTag().putLong(TAG_DEPLETION, Math.max(0L, value));
    }

    public static boolean isMuonCatalysed(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(TAG_MUON);
    }

    public static long getFusingDifficulty(ItemStack stack) {
        FuelType primary = getFuel(stack, true);
        FuelType secondary = getFuel(stack, false);
        double difficulty = BASE_FUSING_DIFFICULTY;
        difficulty *= primary.fusingDifficulty;
        difficulty *= secondary.fusingDifficulty;
        if (isMuonCatalysed(stack)) {
            difficulty /= 4.0D;
        }
        return (long) difficulty;
    }

    /**
     * Simulate a fusion reaction for the pellet. Consumes the supplied "laser"
     * energy (in TU) and returns how much heat the pellet generated.
     */
    public static long react(ItemStack stack, long laserInput) {
        if (laserInput <= 0) {
            return 0;
        }
        FuelType primary = getFuel(stack, true);
        FuelType secondary = getFuel(stack, false);
        long produced = (long) (laserInput * primary.reactionMultiplier * secondary.reactionMultiplier);
        long depletion = getDepletion(stack) + laserInput;
        setDepletion(stack, depletion);
        return produced;
    }

    public static boolean isSpent(ItemStack stack) {
        return getDepletion(stack) >= getMaxDepletion(stack);
    }

    public static int getFuelColor(ItemStack stack) {
        FuelType primary = getFuel(stack, true);
        FuelType secondary = getFuel(stack, false);
        int r = (((primary.color & 0xFF0000) >> 16) + ((secondary.color & 0xFF0000) >> 16)) / 2;
        int g = (((primary.color & 0x00FF00) >> 8) + ((secondary.color & 0x00FF00) >> 8)) / 2;
        int b = ((primary.color & 0x0000FF) + (secondary.color & 0x0000FF)) / 2;
        return (r << 16) | (g << 8) | b;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return getFuelColor(stack);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        long max = Math.max(1L, getMaxDepletion(stack));
        long depletion = Math.max(0L, Math.min(max, getDepletion(stack)));
        double ratio = 1.0D - ((double) depletion / (double) max);
        return Math.max(1, (int) Math.round(13 * ratio));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull java.util.List<Component> tooltip, @NotNull TooltipFlag flag) {
        FuelType primary = getFuel(stack, true);
        FuelType secondary = getFuel(stack, false);
        long max = getMaxDepletion(stack);
        long depletion = getDepletion(stack);
        double pct = max <= 0 ? 0.0D : (double) depletion * 100.0D / (double) max;
        tooltip.add(Component.literal(ChatFormatting.GREEN + "Depletion: " +
                ChatFormatting.RESET + String.format(Locale.ROOT, "%.1f%%", pct)));
        tooltip.add(Component.literal(ChatFormatting.YELLOW + "Fuel: " + ChatFormatting.RESET +
                primary.name().toLowerCase(Locale.ROOT) + " / " + secondary.name().toLowerCase(Locale.ROOT)));
        tooltip.add(Component.literal(ChatFormatting.AQUA + "Difficulty: " + ChatFormatting.RESET +
                String.format(Locale.ROOT, "%,d TU", getFusingDifficulty(stack))));
        tooltip.add(Component.literal(ChatFormatting.GOLD + "Reactivity: " + ChatFormatting.RESET +
                String.format(Locale.ROOT, "x%.2f", primary.reactionMultiplier * secondary.reactionMultiplier)));
        if (isMuonCatalysed(stack)) {
            tooltip.add(Component.literal(ChatFormatting.DARK_AQUA + "Muon catalysed"));
        }
    }

    public static boolean isSupportedFuel(FuelType type) {
        boolean hasItems = FUEL_SOURCES.containsKey(type) && !FUEL_SOURCES.get(type).isEmpty();
        boolean hasFluids = FLUID_SOURCES.containsKey(type) && !FLUID_SOURCES.get(type).isEmpty();
        return hasItems || hasFluids;
    }

    @Nullable
    public static FuelType fuelFromStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        for (Map.Entry<FuelType, List<Supplier<? extends ItemLike>>> entry : FUEL_SOURCES.entrySet()) {
            for (Supplier<? extends ItemLike> supplier : entry.getValue()) {
                ItemLike like = supplier.get();
                if (stack.is(like.asItem())) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    @Nullable
    public static FuelType fuelFromFluid(FluidStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        for (Map.Entry<FuelType, List<Supplier<? extends Fluid>>> entry : FLUID_SOURCES.entrySet()) {
            for (Supplier<? extends Fluid> supplier : entry.getValue()) {
                Fluid fluid = supplier.get();
                if (stack.getFluid().isSame(fluid)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public static List<ItemStack> getFuelItems(FuelType type) {
        List<Supplier<? extends ItemLike>> suppliers = FUEL_SOURCES.get(type);
        if (suppliers == null || suppliers.isEmpty()) {
            return List.of();
        }
        List<ItemStack> stacks = new ArrayList<>();
        for (Supplier<? extends ItemLike> supplier : suppliers) {
            ItemLike like = supplier.get();
            stacks.add(new ItemStack(like));
        }
        return stacks;
    }

    public static List<ItemStack> getFluidDisplays(FuelType type) {
        List<Supplier<? extends Fluid>> suppliers = FLUID_SOURCES.get(type);
        if (suppliers == null || suppliers.isEmpty()) {
            return List.of();
        }
        List<ItemStack> stacks = new ArrayList<>();
        for (Supplier<? extends Fluid> supplier : suppliers) {
            Fluid fluid = supplier.get();
            if (fluid.getBucket() != null) {
                stacks.add(new ItemStack(fluid.getBucket()));
            }
        }
        return stacks;
    }

    private static void registerFuel(FuelType type, Supplier<? extends ItemLike>... sources) {
        List<Supplier<? extends ItemLike>> list = FUEL_SOURCES.computeIfAbsent(type, key -> new ArrayList<>());
        list.addAll(List.of(sources));
    }

    private static void registerFluid(FuelType type, Supplier<? extends Fluid>... fluids) {
        List<Supplier<? extends Fluid>> list = FLUID_SOURCES.computeIfAbsent(type, key -> new ArrayList<>());
        list.addAll(List.of(fluids));
    }
}
