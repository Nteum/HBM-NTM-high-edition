package com.hbm.block.generic;

import com.hbm.HBMLang;
import com.hbm.block.interfaces.ITooltipProvider;
import com.hbm.effect.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockSpeedy extends Block{
    int speed = 0;
    public BlockSpeedy(Properties pProperties, int speed) {
        super(pProperties);
        this.speed = speed;
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        super.stepOn(pLevel, pPos, pState, pEntity);
        if (!pLevel.isClientSide && pEntity instanceof Player player && speed > 0){
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1, speed));
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(HBMLang.BLOCK_SPEEDY_DESC.translate(speed));
    }
}
