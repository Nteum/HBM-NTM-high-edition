package com.hbm.item.armor;

import com.hbm.HBMLang;
import com.hbm.api.energy.IEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.api.energy.ItemStackEnergyHandler;
import com.hbm.registries.HBMCaps;
import com.hbm.capabilities.ItemCapabilityWrapper;
import com.hbm.item.tool.BatteryItem;
import com.hbm.utils.math.BobMth;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 带能量供能的盔甲
 * */
public class ItemArmorFSBPowered extends ItemArmorFSB{
    public long capacity = 1;
    public long input;
    public long consumption;
    public long tickDrain;
    public ItemArmorFSBPowered(ArmorMaterial pMaterial, Type pType, Properties pProperties, long capacity, long in, long consum, long drain) {
        super(pMaterial, pType, pProperties.durability(BatteryItem.DEFAULT_DAMAGE));
        this.capacity = capacity;
        this.input = in;
        this.consumption = consum;
        this.tickDrain = drain;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        List<ItemCapabilityWrapper.ItemCapability> capabilities = new ArrayList<>();
        capabilities.add(new ItemStackEnergyHandler(capacity, input, 0,true));
        return new ItemCapabilityWrapper(stack, capabilities.toArray(ItemCapabilityWrapper.ItemCapability[]::new));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> list, TooltipFlag pIsAdvanced) {
        list.add(Component.translatable(HBMLang.TOOLTIP_CHARGERATE.key(), BobMth.getShortNumber(getCharge(pStack)), BobMth.getShortNumber(getCapacity(pStack))));
        super.appendHoverText(pStack, pLevel, list, pIsAdvanced);
    }

    public static long getCharge(ItemStack stack){
        long result = 0;
        IEnergyHandler energyHandler = stack.getCapability(HBMCaps.LONG_ENERGY).orElse(null);
        if (energyHandler != null) result = energyHandler.getStored();
        return result;
    }
    public static long getCapacity(ItemStack stack){
        long result = 0;
        IEnergyHandler energyHandler = stack.getCapability(HBMCaps.LONG_ENERGY).orElse(null);
        if (energyHandler != null) result = energyHandler.getCapacity();
        return result;
    }

    @Override
    public boolean isArmorEnabled(ItemStack stack) {
        return getCharge(stack) > 0;
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return getCharge(pStack) < getCapacity(pStack);
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
        if (tickDrain > 0 && !level.isClientSide() && !player.isCreative() && hasFSBArmor(player)){
            TransmitUtils.dischargeOnly(stack, tickDrain);
        }
    }
}
