package com.hbm.item.machine;

import com.hbm.HBMLang;
import com.hbm.core.item.ItemBattery;
import com.hbm.registries.RegistryHelper;
import com.hbm.utils.math.BobMth;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemBatterySC extends ItemBattery {
    EnumBatterySC type;

    public ItemBatterySC(EnumBatterySC type, Properties pProperties) {
        super(type.power, type.power, pProperties);
        this.type = type;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        if (pStack.getItem() instanceof ItemBatterySC batterySC){
            pTooltip.add(HBMLang.DISCHARGE_RATE.translate(BobMth.getShortNumber(type.power)));
//            pTooltip.add(Component.translatable(pStack.getDescriptionId() + ".desc"));
        }
    }

    public enum EnumBatterySC {

        EMPTY(	    0),
        WASTE(	  150),
        RA226(	  200),
        TC99(	  500),
        CO60(	  750),
        PU238(	1_000),
        PO210(	1_250),
        AU198(	1_500),
        PB209(	2_000),
        AM241(	2_500);

        public long power;

        private EnumBatterySC(long power) {
            this.power = power;
        }
    }
}
