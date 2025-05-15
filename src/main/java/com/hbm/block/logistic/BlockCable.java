package com.hbm.block.logistic;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.machine.CableEntity;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class BlockCable extends AbstractPipeBlock implements EntityBlock{


    public BlockCable(Properties pProperties) {
        super(pProperties, 0.18F);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CableEntity(pPos,pState);
    }


    @Override
    protected boolean connBlockEntityCond(LevelAccessor pLevel, BlockState state, BlockPos blockPos, BlockPos neighbourPos) {
        return state.hasBlockEntity() && pLevel.getBlockEntity(neighbourPos).getCapability(ForgeCapabilities.ENERGY).isPresent();
    }
}
