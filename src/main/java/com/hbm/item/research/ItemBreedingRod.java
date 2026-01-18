package com.hbm.item.research;

import com.hbm.registries.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemBreedingRod extends Item {

    private static final String TAG_TYPE = "hbmRodType";
    private final RodForm form;

    public ItemBreedingRod(Properties properties, RodForm form) {
        super(properties.stacksTo(1));
        this.form = form;
    }

    public RodForm getForm() {
        return form;
    }

    public static ItemStack createStack(RodForm form, RodType type) {
        Item item = switch (form) {
            case SINGLE -> ModItems.rod_breeder_single.get();
            case DUAL -> ModItems.rod_breeder_dual.get();
            case QUAD -> ModItems.rod_breeder_quad.get();
        };
        ItemStack stack = new ItemStack(item);
        setType(stack, type);
        return stack;
    }

    public static void setType(ItemStack stack, RodType type) {
        stack.getOrCreateTag().putByte(TAG_TYPE, (byte) type.ordinal());
    }

    public static RodType getType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return RodType.LITHIUM;
        }
        return RodType.byOrdinal(tag.getByte(TAG_TYPE));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(ChatFormatting.YELLOW + "[Breeder Rod]"));
        tooltip.add(Component.literal(ChatFormatting.AQUA + " Type: " + getType(stack).displayName()));
        tooltip.add(Component.literal(ChatFormatting.GRAY + " Form: " + form.displayName()));
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return false;
    }

    public enum RodForm {
        SINGLE("Single"),
        DUAL("Dual"),
        QUAD("Quad");

        private final String name;

        RodForm(String name) {
            this.name = name;
        }

        public String displayName() {
            return name;
        }
    }

    public enum RodType {
        LITHIUM,
        TRITIUM,
        CO,
        CO60,
        TH232,
        THF,
        U235,
        NP237,
        U238,
        PU238,
        PU239,
        RGP,
        WASTE,
        LEAD,
        URANIUM,
        RA226,
        AC227;

        private static final RodType[] VALUES = values();

        public static RodType byOrdinal(int idx) {
            if (idx < 0 || idx >= VALUES.length) {
                return LITHIUM;
            }
            return VALUES[idx];
        }

        public String displayName() {
            String base = name().toLowerCase();
            return base.substring(0, 1).toUpperCase() + base.substring(1);
        }
    }
}
