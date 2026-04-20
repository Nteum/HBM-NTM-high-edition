package com.hbm.item.armor;

import com.hbm.api.fluid.FluidUtils;
import com.hbm.api.fluid.ItemStackFluidHandler;
import com.hbm.capabilities.ItemCapabilityWrapper;
import com.hbm.item.HBMCombat;
import com.hbm.item.tool.BatteryItem;
import com.hbm.utils.math.BobMth;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** 可以添加燃料的盔甲 */
public class ItemArmorFSBFueled extends ItemArmorFSB{
    Fluid fluid;
    public int maxFuel = 1;
    public int fillRate;
    public int consumption;
    public int drain;
    public ItemArmorFSBFueled(ArmorMaterial pMaterial, Type pType, Properties pProperties, Fluid fuel, int maxFuel, int fillRate, int consumption, int drain) {
        this(pMaterial, pType, pProperties.durability(BatteryItem.DEFAULT_DAMAGE), fuel, maxFuel, fillRate, consumption, drain, null);
    }
    public ItemArmorFSBFueled(ArmorMaterial pMaterial, Type pType, Properties pProperties, Fluid fuel, int maxFuel, int fillRate, int consumption, int drain, Supplier<HBMCombat.Suit> suit) {
        super(pMaterial, pType, pProperties.durability(BatteryItem.DEFAULT_DAMAGE), suit);
        this.fluid = fuel;
        this.maxFuel = maxFuel;
        this.fillRate = fillRate;
        this.consumption = consumption;
        this.drain = drain;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        List<ItemCapabilityWrapper.ItemCapability> capabilities = new ArrayList<>();
        capabilities.add(new ItemStackFluidHandler(maxFuel, fillRate, 0,fluid));
        return new ItemCapabilityWrapper(stack, capabilities.toArray(ItemCapabilityWrapper.ItemCapability[]::new));
    }

    @Override
    public boolean isArmorEnabled(ItemStack stack) {
        return FluidUtils.getAmount(stack) > 0;
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);

        if (!player.isCreative() && level.getGameTime() % 10 == 0 && this.drain > 0 && hasFSBArmor(player)){
            FluidUtils.absorbOnly(stack, this.drain);
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> list, TooltipFlag pIsAdvanced) {
        FluidStack fluid1 = FluidUtils.getFluid(pStack);
        list.add(((MutableComponent)fluid1.getDisplayName()).append(": " + BobMth.getShortNumber(fluid1.getAmount()) + " / " + BobMth.getShortNumber(this.maxFuel)));

        super.appendHoverText(pStack, pLevel, list, pIsAdvanced);
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return FluidUtils.getAmount(pStack) < this.maxFuel;
    }
}
