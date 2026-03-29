package com.hbm.block.weapon;

import com.hbm.blockentity.weapon.NukeBombCustomEntity;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NukeCustom extends NukeBomb implements IBomb{
//    public static final VoxelShape this.shape = Block.box(-30,0,0,24,16,16);
    public static final int maxNuke = 200;

    public NukeCustom(Properties pProperties, int range) {
        super(pProperties,range);
        this.shape = Block.box(-8,0,0,48,16,16);
    }

    @Override
    public BombReturnCode explode(Level pLevel, BlockPos pPos) {
        if (!pLevel.isClientSide){
            pLevel.playSound((Player) null,pPos, ModSounds.WEAPON_NUCLEAR_EXPLOSION.get(), SoundSource.RECORDS,5.0F,1.0F);
            pLevel.addFreshEntity(EntityNukeExplosionMK5.statFac(pLevel,range,pPos.getCenter()));
            pLevel.addFreshEntity(new EntityNukeTorex(pLevel,pPos.getCenter().add(0,4.5,0),range));
            pLevel.destroyBlock(pPos,false);
            triggerExplosionVisual(pLevel, pPos);

            return BombReturnCode.DETONATED;
        }

        return BombReturnCode.UNDEFINED;
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new NukeBombCustomEntity(pPos,pState);
    }
}
