package com.hbm.block.machine;

import com.hbm.HBMLang;
import com.hbm.block.base.BaseMachineBlock;
import com.hbm.block.base.BlockMachineBase;
import com.hbm.block.interfaces.ILookOverlay;
import com.hbm.blockentity.machine.BarrelEntity;
import com.hbm.utils.InventoryUtils;
import com.hbm.utils.WorldUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockFluidBarrel extends BlockMachineBase implements ILookOverlay {
    public static final VoxelShape SHAPE = Block.box(2,0.0D,2,14,16,14);
    public BarrelProperties barrelProperties;
    public BlockFluidBarrel(Properties pProperties, BarrelProperties barrelProperties) {
        super(pProperties);
        this.barrelProperties = barrelProperties;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BarrelEntity(pPos,pState);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(Component.translatable(HBMLang.FLUID_CAPACITY.key(),this.barrelProperties.capacity).withStyle(ChatFormatting.AQUA));
    }

    @Override
    public List<Component> getDesc(Level level, BlockPos pos) {
        BarrelEntity tileEntity = WorldUtils.getTileEntity(BarrelEntity.class, level, pos);
        if (tileEntity == null) return List.of();
        FluidTank tank = tileEntity.getFluidTanks().get(0);
        return List.of(
                Component.translatable(this.getDescriptionId()),
                tank.isEmpty() ? HBMLang.EMPTY.translate() : Component.translatable(HBMLang.GUI_TOOLTIP_FLUID.key(), tank.getFluidInTank(0).getFluid().getFluidType().getDescriptionId(), this.barrelProperties.capacity).withStyle(ChatFormatting.AQUA)
        );
    }

    public static class BarrelProperties{
        public int capacity;
        public boolean hotResistance = false;
        public boolean corrosiveResistance = false;
        public boolean highCorroResist = false;
        public boolean antimatter = false;
        public boolean leaky = false;
        public boolean isCreative = false;
        public static BarrelProperties of(){return new BarrelProperties();}
        public BarrelProperties capacity(int cap){
            this.capacity = cap;
            return this;
        }
        public BarrelProperties hotResist(){
            this.hotResistance = true;
            return this;
        }
        public BarrelProperties corrosiveResistance(){
            this.corrosiveResistance = true;
            return this;
        }
        public BarrelProperties highCorroResist(){
            this.highCorroResist = true;
            this.corrosiveResistance = true;
            return this;
        }
        public BarrelProperties antimatter(){
            this.antimatter = true;
            return this;
        }
        public BarrelProperties leaky(){
            this.leaky = true;
            return this;
        }
    }
}
