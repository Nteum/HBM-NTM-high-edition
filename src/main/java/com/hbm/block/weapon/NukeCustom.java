package com.hbm.block.weapon;

import com.hbm.blockentity.weapon.NukeBombCustomEntity;
import com.hbm.blockentity.weapon.NukeBombCustomEntity.CustomNukeProfile;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

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
            BlockPos corePos = pPos;
            BlockState state = pLevel.getBlockState(pPos);
            if (state.is(this)) {
                corePos = getCore(state, pLevel, pPos);
            }
            CustomNukeProfile profile = CustomNukeProfile.DEFAULT;
            if (pLevel.getBlockEntity(corePos) instanceof NukeBombCustomEntity customEntity) {
                profile = customEntity.getProfile();
            }
            int actualRange = profile.getRange();
            pLevel.playSound((Player) null,corePos, ModSounds.WEAPON_NUCLEAR_EXPLOSION.get(), SoundSource.RECORDS,5.0F,1.0F);
            pLevel.addFreshEntity(EntityNukeExplosionMK5.statFac(pLevel,actualRange,corePos.getCenter()));
            pLevel.addFreshEntity(new EntityNukeTorex(pLevel,corePos.getCenter().add(0,4.5,0),actualRange));
            pLevel.destroyBlock(corePos,false);

            return BombReturnCode.DETONATED;
        }

        return BombReturnCode.UNDEFINED;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pPlayer.isShiftKeyDown()) {
            return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        }

        ItemStack held = pPlayer.getItemInHand(pHand);
        CustomNukeProfile profile = CustomNukeProfile.fromKit(held);
        if (!held.isEmpty() && profile == null) {
            return InteractionResult.PASS;
        }
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockPos corePos = getCore(pState, pLevel, pPos);
        if (!(pLevel.getBlockEntity(corePos) instanceof NukeBombCustomEntity customEntity)) {
            return InteractionResult.CONSUME;
        }

        if (held.isEmpty()) {
            pPlayer.displayClientMessage(Component.literal("Custom nuke: ").append(customEntity.getProfile().getDisplayName()), true);
            return InteractionResult.CONSUME;
        }

        boolean changed = customEntity.setProfile(profile);
        if (changed && !pPlayer.getAbilities().instabuild) {
            held.shrink(1);
        }
        pPlayer.displayClientMessage(Component.literal("Custom nuke: ").append(profile.getDisplayName()), true);
        return InteractionResult.CONSUME;
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new NukeBombCustomEntity(pPos,pState);
    }
}
