package com.hbm.item.weapon;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ItemDesignator extends Item {
    public ItemDesignator(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();
        if (!level.isClientSide()){
            ItemStack itemInHand = pContext.getItemInHand();
            if (player.hasPose(Pose.CROUCHING) && itemInHand.getItem() instanceof ItemDesignator){
                BlockPos clickedPos = pContext.getClickedPos();
                itemInHand.getOrCreateTag().putIntArray("pos",new int[]{clickedPos.getX(),clickedPos.getY(),clickedPos.getZ()});
            }
        }
        return super.useOn(pContext);
    }

}
