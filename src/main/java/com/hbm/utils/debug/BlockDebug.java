package com.hbm.utils.debug;

import com.hbm.item.HBMtools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class BlockDebug extends Block {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("debug_active");
    public BlockDebug(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(ACTIVE);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pPlayer.getItemInHand(pHand).is(HBMtools.DEBUG_WAND.get())){
            if (pState.getValue(ACTIVE)){
                pState.setValue(ACTIVE, false);
                pPlayer.sendSystemMessage(Component.literal("Debug block switch to inactive."));
            }else {
                pState.setValue(ACTIVE, true);
                pPlayer.sendSystemMessage(Component.literal("Debug block switch to active."));
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.tick(pState, pLevel, pPos, pRandom);
        if (pLevel.getGameTime() % 20 == 0){
            pLevel.players().get(0).sendSystemMessage(Component.literal("Debug block alive, now is " + pLevel.getGameTime()));
        }
    }
}
