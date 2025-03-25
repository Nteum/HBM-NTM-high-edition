package com.hbm.item;

import com.hbm.block.machine.BlockBattery;
import com.hbm.blockentity.machine.BatteryEntity;
import com.hbm.capabilities.Capabilities;
import com.hbm.api.energy.IItemBattery;
import com.hbm.api.energy.ItemEnergyProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BatteryBlockItem extends BlockItem implements IItemBattery {
    public final ItemEnergyProxy itemEnergyProxy;
    public BatteryBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties.defaultDurability(ItemEnergyProxy.DEFAULT_DAMAGE));
        if (pBlock instanceof BlockBattery battery){
            long capacity = battery.type.getMaxEnergy();
//            long capacity = 10000;
            itemEnergyProxy = new ItemEnergyProxy(capacity,capacity,capacity);
        }else {
            itemEnergyProxy = new ItemEnergyProxy();
        }
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
            if (blockEntity instanceof BatteryEntity battery && itemInHand.getItem() instanceof BatteryBlockItem batteryBlockItem){
                battery.getCapability(Capabilities.ENERGY).ifPresent(cap -> {
                    cap.setEnergy(itemInHand.getOrCreateTag().getLong("energy"));
                });
            }
        }
        return interactionResult;
    }
}
