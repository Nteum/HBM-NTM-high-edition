package com.hbm.block.weapon;

import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class NukeCustom extends Block implements IBomb{
    public static final int maxNuke = 200;

    public NukeCustom(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public BombReturnCode explode(Level pLevel, BlockPos pPos) {
        if (!pLevel.isClientSide){
            pLevel.addFreshEntity(EntityNukeExplosionMK5.statFac(pLevel,120,pPos.getCenter()));
            pLevel.addFreshEntity(new EntityNukeTorex(pLevel,pPos.getCenter().add(0,4.5,0),120));

//            ServerLevel serverLevel = (ServerLevel) pLevel;
//            for (ServerPlayer player : serverLevel.players()) {
//                if (player.distanceToSqr(pPos.getCenter()) < 4096.0F){
//
//                }
//            }
            return BombReturnCode.DETONATED;
        }
        return BombReturnCode.UNDEFINED;
    }
}
