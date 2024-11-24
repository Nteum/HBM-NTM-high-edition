package com.hbm.block.weapon;

import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class NukeCustom extends Block implements IBomb{
    public static final int maxNuke = 200;

    public NukeCustom(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public BombReturnCode explode(Level pLevel, BlockPos pPos) {
        if (!pLevel.isClientSide){
            pLevel.playSound((Player) null,pPos, ModSounds.WEAPON_NUCLEAR_EXPLOSION.get(), SoundSource.RECORDS,1.0F,1.0F);
            pLevel.addFreshEntity(EntityNukeExplosionMK5.statFac(pLevel,120,pPos.getCenter()));
            pLevel.addFreshEntity(new EntityNukeTorex(pLevel,pPos.getCenter().add(0,4.5,0),120));

            return BombReturnCode.DETONATED;
        }

        return BombReturnCode.UNDEFINED;
    }
}
