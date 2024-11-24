package com.hbm.block.weapon;

import com.hbm.block.machine.BaseMachineBlock;
import com.hbm.blockentity.weapon.NukeBombEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class NukeBomb extends BaseMachineBlock implements IBomb {
    private boolean ready = false;
    public boolean explode = true;
    public NukeBomb(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!explode)explode = true;
        return InteractionResult.SUCCESS;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {

    }

    /** 继承Ibomb，核弹引爆函数 */
    @Override
    public BombReturnCode explode(Level pLevel, BlockPos pPos) {
        if(!pLevel.isClientSide) {
            NukeBombEntity entity = (NukeBombEntity) pLevel.getBlockEntity(pPos);

            if(entity.isReady()) {
//                this.onBlockDestroyedByPlayer(world, x, y, z, 1);
//                entity.clearSlots();
                pLevel.removeBlock(pPos,true);
//                igniteTestBomb(world, x, y, z);
                return BombReturnCode.DETONATED;
            }

            return BombReturnCode.ERROR_MISSING_COMPONENT;
        }

        return BombReturnCode.UNDEFINED;
    }
    /** 继承自blockentity */
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }
}
