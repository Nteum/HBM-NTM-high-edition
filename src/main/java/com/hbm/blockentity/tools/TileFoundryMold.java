package com.hbm.blockentity.tools;

import com.hbm.Inventory.material.HBMMatForm;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.item.tool.ItemMold;
import com.hbm.registries.HBMMatters;
import com.hbm.registries.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class TileFoundryMold extends TileFoundryBase{
    public TileFoundryMold(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.TILE_FOUNDRY_MOLD.get(), pPos, pBlockState);
        this.tank = new FluidTank(HBMMatForm.INGOT.quantity, fluidStack -> {
            ItemStack moldStack = this.items.getStackInSlot(0);
            if (moldStack.isEmpty()) return false;
            if (moldStack.getItem() instanceof ItemMold itemMold){
                return HBMMatters.doFluidValidToForm(fluidStack, itemMold.getForm());
            }
            return false;
        }){
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                shouldSync = true;
            }
        };
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this.tank);
    }
}
