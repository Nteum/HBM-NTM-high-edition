package com.hbm.item.tool;

import com.hbm.HBMLang;
import com.hbm.api.energy.fe.HBMEnergyStorage;
import com.hbm.api.energy.ItemStackEnergyHandler;
import com.hbm.block.machine.BlockBattery;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.capabilities.ItemCapabilityWrapper;
import com.hbm.item.BlockItemHBM;
import com.hbm.utils.ItemDataUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BatteryBlockItem extends BlockItemHBM {
    long capacity;
    long inout;
    boolean isEmpty;
    static int DEFAULT_DAMAGE = 1000;
    public BatteryBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties.defaultDurability(DEFAULT_DAMAGE));
        this.capacity = ((BlockBattery)pBlock).type.getMaxEnergy();
        this.inout = ((BlockBattery)pBlock).type.getOutput();
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(Component.translatable(HBMLang.ENERGY.key(),getEnergy(pStack)));
    }

    @Override
    protected void gatherCapabilities(List<ItemCapabilityWrapper.ItemCapability> capabilities, ItemStack stack, CompoundTag nbt) {
        super.gatherCapabilities(capabilities, stack, nbt);
        capabilities.add(new ItemStackEnergyHandler(capacity,inout,true));
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
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return super.onItemUseFirst(stack, context);
    }

    @Override
    public InteractionResult place(BlockPlaceContext pContext) {
        ItemStack itemInHand = pContext.getItemInHand();
        CompoundTag dataMapIfPresent = ItemDataUtils.getDataMapIfPresent(itemInHand);
        if (dataMapIfPresent != null){
            setBlockEntityData(itemInHand, ModBlockEntityType.BATTERY_ENTITY.get(), dataMapIfPresent);
        }
        return super.place(pContext);
    }
}
