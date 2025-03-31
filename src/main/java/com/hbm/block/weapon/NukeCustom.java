package com.hbm.block.weapon;

import com.hbm.block.base.BedLikeBlock;
import com.hbm.blockentity.weapon.NukeBombCustomEntity;
import com.hbm.blockentity.weapon.NukeBombFatEntity;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class NukeCustom extends NukeBomb implements IBomb{
    public static final int maxNuke = 200;

    public NukeCustom(Properties pProperties, int range) {
        super(pProperties,range);
    }

    @Override
    public BombReturnCode explode(Level pLevel, BlockPos pPos) {
        if (!pLevel.isClientSide){
            pLevel.playSound((Player) null,pPos, ModSounds.WEAPON_NUCLEAR_EXPLOSION.get(), SoundSource.RECORDS,5.0F,1.0F);
            pLevel.addFreshEntity(EntityNukeExplosionMK5.statFac(pLevel,range,pPos.getCenter()));
            pLevel.addFreshEntity(new EntityNukeTorex(pLevel,pPos.getCenter().add(0,4.5,0),range));

            return BombReturnCode.DETONATED;
        }

        return BombReturnCode.UNDEFINED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new NukeBombCustomEntity(pPos,pState);
    }
    @Override
    protected List<Vec3i> getOffsets() {
        return square(new int[]{0, 0, 1 ,0 ,0 ,2});
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }
}
