package com.hbm.block.machine;


import com.hbm.HBM;
import com.hbm.block.base.BaseMachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public abstract class BlockLitSingleBlockMachine extends BaseMachineBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public BlockLitSingleBlockMachine(Properties pProperties) {
        super(pProperties);
        //设置状态的初始值
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(LIT,Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(LIT);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
//            HBM.LOGGER.info("electric furnace clicked");
//            Boolean value = pState.getValue(LIT);
//            pState.setValue(LIT,!value);
//            pLevel.setBlockAndUpdate(pPos,pState);
            super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
            return InteractionResult.CONSUME;
        }
    }
}
