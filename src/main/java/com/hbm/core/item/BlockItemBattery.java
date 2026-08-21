package com.hbm.core.item;

import com.hbm.HBMLang;
import com.hbm.api.energy.fe.HBMEnergyStorage;
import com.hbm.core.capability.ItemStackCapabilityProvider;
import com.hbm.core.capability.energy.ItemStackEnergyHandler;
import com.hbm.block.machine.BlockBattery;
import com.hbm.blockentity.HBMTiles;
import com.hbm.registries.HBMCaps;
import com.hbm.utils.ItemDataUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockItemBattery extends BlockItemCapacities {
    long capacity;
    long inout;
    boolean isEmpty = false;
    static int DEFAULT_DAMAGE = 1000;
    public BlockItemBattery(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties.defaultDurability(DEFAULT_DAMAGE));
        this.capacity = ((BlockBattery)pBlock).type.getMaxEnergy();
        this.inout = ((BlockBattery)pBlock).type.getOutput();
    }

    @Override
    protected void addCapabilities(ItemStack stack, @Nullable CompoundTag nbt, ItemStackCapabilityProvider provider) {
        provider.addCapability(HBMCaps.LONG_ENERGY, new ItemStackEnergyHandler(stack, capacity, inout, isEmpty));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(Component.translatable(HBMLang.ENERGY.key(),getEnergy(pStack)));
    }

    public static long getEnergy(ItemStack pStack){
        long result = 0;
        IEnergyStorage iEnergyStorage = pStack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
        if (iEnergyStorage instanceof HBMEnergyStorage energyStorage)
            result = energyStorage.getEnergyStored();
        return result;
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return true;
    }

    @Override
    public InteractionResult place(BlockPlaceContext pContext) {
        ItemStack itemInHand = pContext.getItemInHand();
        CompoundTag dataMapIfPresent = ItemDataUtils.getDataMapIfPresent(itemInHand);
        if (dataMapIfPresent != null){
            setBlockEntityData(itemInHand, HBMTiles.BATTERY_ENTITY.get(), dataMapIfPresent);
        }
        return super.place(pContext);
    }
}
