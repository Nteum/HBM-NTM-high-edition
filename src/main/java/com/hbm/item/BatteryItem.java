package com.hbm.item;

import com.hbm.api.energy.IItemBattery;
import com.hbm.api.energy.ItemEnergyProxy;
import com.hbm.registries.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BatteryItem extends Item implements IItemBattery {
    public final ItemEnergyProxy itemEnergyProxy;
    public BatteryItem(long capacity, long maxExtract, long maxReceive, Properties pProperties) {
        super(pProperties.defaultDurability(ItemEnergyProxy.DEFAULT_DAMAGE));
        itemEnergyProxy = new ItemEnergyProxy(capacity, maxExtract, maxReceive);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(Component.literal("energy: "+pStack.getOrCreateTag().getLong("energy")));
    }

    @Override
    public ItemEnergyProxy getEnergyProxy() {
        return itemEnergyProxy;
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return !pStack.is(ModItems.BATTERY_CREATIVE.get());
    }
//    public static boolean canCharge(ItemStack stack){
//        return stack.getItem() instanceof BatteryItem && ((BatteryItem) stack.getItem()).batteryItemData.capacity > 0 && stack.getOrCreateTag().getLong("energy") < ((BatteryItem) stack.getItem()).batteryItemData.capacity;
//    }
}
