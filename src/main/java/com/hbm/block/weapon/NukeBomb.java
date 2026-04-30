package com.hbm.block.weapon;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.weapon.EntityNukeBomb;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class NukeBomb extends BlockDummyable implements IBomb {
    public boolean explode = true;
    public int range = 0;
    public NukeBomb(Properties pProperties, int range) {
        super(pProperties);
        this.range = range;
        this.shapeRotates = true;
    }

    /** 继承Ibomb，核弹引爆函数 */
    @Override
    public BombReturnCode explode(Level pLevel, BlockPos pPos) {
        if (!pLevel.isClientSide){
            pLevel.playSound((Player) null,pPos, ModSounds.WEAPON_NUCLEAR_EXPLOSION.get(), SoundSource.RECORDS,1.0F,1.0F);
            pLevel.addFreshEntity(EntityNukeExplosionMK5.statFac(pLevel,range,pPos.getCenter()));
            pLevel.addFreshEntity(new EntityNukeTorex(pLevel,pPos.getCenter().add(0,4.5,0),range));
            pLevel.destroyBlock(pPos,false);
            return BombReturnCode.DETONATED;
        }
        return BombReturnCode.UNDEFINED;
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (!pLevel.isClientSide){
            // 先判断core状态的行为，再判断不是core状态的行为，否则程序会陷入死循环。
            if (pState.getValue(IS_CORE)){
                if (pLevel.getBlockEntity(pPos) instanceof EntityNukeBomb bomb && pLevel.hasNeighborSignal(pPos)){
                    explode(pLevel, pPos);
                }
            }else {
                super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
            }
        }
    }
}
