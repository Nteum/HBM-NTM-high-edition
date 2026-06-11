package com.hbm.item.misc;

import com.hbm.HBMLang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDrillbit extends Item {
    EnumDrillType type;
    public ItemDrillbit(EnumDrillType type, Properties pProperties) {
        super(pProperties);
        this.type = type;
    }

    public EnumDrillType getType(){
        return this.type;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(HBMLang.SPEED.translate(((int) (type.speed * 100))).append("%").withStyle(ChatFormatting.YELLOW));
        pTooltipComponents.add(HBMLang.TIER.translate(type.tier).append("%").withStyle(ChatFormatting.YELLOW));
        if(type.fortune > 0) pTooltipComponents.add(Component.translatable("enchantment.minecraft.fortune").append(" " + type.fortune).withStyle(ChatFormatting.LIGHT_PURPLE));
        if(type.vein) pTooltipComponents.add(HBMLang.VEIN_MINER.translate().withStyle(ChatFormatting.GREEN));
        if(type.silk) pTooltipComponents.add(Component.translatable("enchantment.minecraft.silk_touch").withStyle(ChatFormatting.GREEN));
    }

    public enum EnumDrillType {
        STEEL			(1.0D, 1, 0, false, false),
        STEEL_DIAMOND	(1.0D, 1, 2, false, true),
        HSS				(1.2D, 2, 0, true, false),
        HSS_DIAMOND		(1.2D, 2, 3, true, true),
        DESH			(1.5D, 3, 1, true, true),
        DESH_DIAMOND	(1.5D, 3, 4, true, true),
        TCALLOY			(2.0D, 4, 1, true, true),
        TCALLOY_DIAMOND	(2.0D, 4, 4, true, true),
        FERRO			(2.5D, 5, 1, true, true),
        FERRO_DIAMOND	(2.5D, 5, 4, true, true);

        public final double speed;
        public final int tier;
        public final int fortune;
        public final boolean vein;
        public final boolean silk;

        private EnumDrillType(double speed, int tier, int fortune, boolean vein, boolean silk) {
            this.speed = speed;
            this.tier = tier;
            this.fortune = fortune;
            this.vein = vein;
            this.silk = silk;
        }
    }
}
