package com.hbm.item;

import com.hbm.HBMLang;
import com.hbm.api.energy.fe.HBMEnergyStorage;
import com.hbm.api.energy.fe.ItemStackEnergyHandler;
import com.hbm.block.machine.BlockBattery;
import com.hbm.blockentity.machine.BatteryEntity;
import com.hbm.capabilities.Capabilities;
import com.hbm.api.energy.IItemBattery;
import com.hbm.api.energy.ItemEnergyProxy;
import com.hbm.capabilities.ItemCapabilityWrapper;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BatteryBlockItem extends BlockItemHBM implements IItemBattery {
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
        pTooltip.add(Component.translatable(HBMLang.ENERGY.getTranslationKey(),getEnergy(pStack)));
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
    public ItemEnergyProxy getEnergyProxy() {
        return null;
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
    public InteractionResult useOn(UseOnContext pContext) {
        return super.useOn(pContext);
    }

    @Override
    public InteractionResult place(BlockPlaceContext pContext) {
        InteractionResult interactionResult = super.place(pContext);
        if (!pContext.getLevel().isClientSide() && interactionResult.consumesAction()){
            BlockPos clickedPos = pContext.getClickedPos();
            Level level = pContext.getLevel();
            BlockEntity blockEntity = level.getBlockEntity(clickedPos);
            ItemStack itemInHand = pContext.getItemInHand();
            if (blockEntity instanceof BatteryEntity battery && itemInHand.getItem() instanceof BatteryBlockItem){
                battery.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
                    ((HBMEnergyStorage)cap).setEnergy(getEnergy(itemInHand));
                });
            }
        }
        return interactionResult;
    }
}
