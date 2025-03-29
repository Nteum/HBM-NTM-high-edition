package com.hbm.block.weapon;

import com.hbm.block.base.BaseMachineBlock;
import com.hbm.block.base.BedLikeBlock;
import com.hbm.blockentity.weapon.NukeBombEntity;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class NukeBomb extends BedLikeBlock implements IBomb {
    private boolean ready = false;
    public boolean explode = true;
    public int range = 0;
    public NukeBomb(Properties pProperties, int range) {
        super(pProperties);
        this.range = range;
    }

//    @Override
//    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
//        if (!explode)explode = true;
//        return InteractionResult.SUCCESS;
//    }

//    @Override
//    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
//
//    }

    /** 继承Ibomb，核弹引爆函数 */
    @Override
    public BombReturnCode explode(Level pLevel, BlockPos pPos) {
        if (!pLevel.isClientSide){
            pLevel.playSound((Player) null,pPos, ModSounds.WEAPON_NUCLEAR_EXPLOSION.get(), SoundSource.RECORDS,1.0F,1.0F);
            pLevel.addFreshEntity(EntityNukeExplosionMK5.statFac(pLevel,range,pPos.getCenter()));
            pLevel.addFreshEntity(new EntityNukeTorex(pLevel,pPos.getCenter().add(0,4.5,0),range));

            return BombReturnCode.DETONATED;
        }

        return BombReturnCode.UNDEFINED;
//        if(!pLevel.isClientSide) {
//            NukeBombEntity entity = (NukeBombEntity) pLevel.getBlockEntity(pPos);
//
//            if(entity.isReady()) {
////                this.onBlockDestroyedByPlayer(world, x, y, z, 1);
////                entity.clearSlots();
//                pLevel.removeBlock(pPos,true);
////                igniteTestBomb(world, x, y, z);
//                return BombReturnCode.DETONATED;
//            }
//
//            return BombReturnCode.ERROR_MISSING_COMPONENT;
//        }
//
//        return BombReturnCode.UNDEFINED;
    }
    /** 继承自blockentity */
//    @Override
//    public RenderShape getRenderShape(BlockState pState) {
//        return RenderShape.MODEL;
//    }
}
