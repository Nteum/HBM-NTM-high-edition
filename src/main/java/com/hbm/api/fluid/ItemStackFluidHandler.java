package com.hbm.api.fluid;

import com.hbm.HBMKey;
import com.hbm.api.IContentsListener;
import com.hbm.api.Mode;
import com.hbm.capabilities.ItemCapabilityWrapper;
import com.hbm.capabilities.resolver.BasicCapabilityResolver;
import com.hbm.capabilities.resolver.ICapabilityResolver;
import com.hbm.utils.ItemDataUtils;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ItemStackFluidHandler extends ItemCapabilityWrapper.ItemCapability implements IContentsListener {
    protected BasicFluidTank fluidTank = null;
    public ItemStackFluidHandler(int capacity, int input, int output){
        this(capacity, input, output, null);
    }

    public ItemStackFluidHandler(int capacity, int input, int output, @Nullable Fluid fluid){
        this(new BasicFluidTank(capacity, input, output));
        if (fluid != null)
            this.fluidTank.setFluid(new FluidStack(fluid, capacity));
    }
    public ItemStackFluidHandler(BasicFluidTank fluidTank){
        this.fluidTank = fluidTank;
        this.fluidTank.setListener(this);   // 调用本类的onContentsChanged
    }

    @Override
    protected void gatherCapabilityResolvers(Consumer<ICapabilityResolver> consumer) {
        consumer.accept(new BasicCapabilityResolver(()->new SingleFluidHandler(this.fluidTank, Mode.BOTH), ForgeCapabilities.FLUID_HANDLER));
    }

    @Override
    protected void load() {
        super.load();
        ItemDataUtils.readContainers(getStack(), HBMKey.FLUIDS, this.fluidTank);
        saveDamageValue();
    }

    @Override
    public void onContentsChanged() {
        ItemDataUtils.writeContainers(getStack(), HBMKey.FLUIDS, this.fluidTank);
        saveDamageValue();
    }
    private void saveDamageValue(){
        if (getStack().isDamageableItem())
            getStack().setDamageValue((int) ((1-this.fluidTank.getPercent())*getStack().getMaxDamage()));
    }
}
