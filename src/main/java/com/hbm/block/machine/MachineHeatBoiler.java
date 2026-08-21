package com.hbm.block.machine;

import com.hbm.HBMLang;
import com.hbm.block.HBMBlockProperties;
import com.hbm.block.interfaces.ILookOverlay;
import com.hbm.block.interfaces.ITooltipProvider;
import com.hbm.blockentity.machine.TileEntityHeatBoiler;
import com.hbm.core.block.BlockDummyable;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.registries.HBMCaps;
import com.hbm.registries.ModItems;
import com.hbm.utils.WorldUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MachineHeatBoiler extends BlockDummyable implements ILookOverlay, ITooltipProvider {
    public static final BooleanProperty EXPLODED = HBMBlockProperties.BROKEN;
    static MultiblockData MULTIBLOCK_DATA;
    public MachineHeatBoiler(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(EXPLODED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(EXPLODED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext).setValue(EXPLODED, false);
    }

    @Override
    public MultiblockData getMultiblockData() {
        if (MULTIBLOCK_DATA == null){
            MULTIBLOCK_DATA = new MultiblockData(3, 0, 1, 1, 1, 1)
                    .addCap(new Vec3i(1,0, 0), ForgeCapabilities.FLUID_HANDLER, Direction.EAST)
                    .addCap(new Vec3i(-1,0, 0), ForgeCapabilities.FLUID_HANDLER, Direction.WEST)
                    .addCap(new Vec3i(0,0, 1), ForgeCapabilities.FLUID_HANDLER, Direction.SOUTH)
                    .addCap(new Vec3i(0,0, -1), ForgeCapabilities.FLUID_HANDLER, Direction.NORTH)
                    .addCap(new Vec3i(0,3, 0), ForgeCapabilities.FLUID_HANDLER, Direction.UP)
            ;
        }
        return MULTIBLOCK_DATA;
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileEntityHeatBoiler(pPos, pState);
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        if (pState.getValue(EXPLODED)){
            return List.of(new ItemStack(ModItems.INGOT_STEEL.get(), 4), new ItemStack(ModItems.PLATE_COPPER.get(), 8));
        }
        return super.getDrops(pState, pParams);
    }

    @Override
    public List<Component> getDesc(Level level, BlockPos pos) {
        ArrayList<Component> result = new ArrayList<>();
        BlockState blockState = level.getBlockState(pos);
        if (blockState.getValue(EXPLODED)) return result;
        BlockPos core = getCore(blockState, level, pos);
        if (WorldUtils.getTileEntity(level, core) instanceof TileEntityHeatBoiler boiler){
            result.add(Component.translatable(getDescriptionId()));
            result.add(HBMLang.SHOW_HEAT.translate(boiler.getHeats().getHeat()));
            result.add(Component.literal("-> ").withStyle(ChatFormatting.GREEN).append(HBMLang.GUI_TOOLTIP_FLUID.translate(
                    boiler.getFluids().getFluidTank(0).getFluid().getDisplayName(),
                    boiler.getFluids().getFluidInTank(0) + " / " + boiler.getFluids().getFluidTank(0).getCapacity())));
            result.add(Component.literal("<- ").withStyle(ChatFormatting.RED).append(HBMLang.GUI_TOOLTIP_FLUID.translate(
                    boiler.getFluids().getFluidTank(1).getFluid().getDisplayName(),
                    boiler.getFluids().getFluidInTank(1) + " / " + boiler.getFluids().getFluidTank(1).getCapacity())));
        }
        return result;
    }
}
