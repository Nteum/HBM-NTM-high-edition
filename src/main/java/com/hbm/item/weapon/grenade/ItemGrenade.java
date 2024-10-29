package com.hbm.item.weapon.grenade;

import com.hbm.HBMxx;
//import com.hbm.entity.logic.EntityGrenadeBouncyBase;
//import com.hbm.entity.logic.GrenadeGeneticEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/** 手榴弹 */
public class ItemGrenade extends Item {
    public int fuse = 4;
    public String name;
    public ItemGrenade(Properties pProperties, int fuse,String name) {
        super(pProperties.stacksTo(16));
        this.fuse = fuse;
        this.name = name;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
//        PrimedTnt

//        pPlayer.playSound();
//        pLevel.playSound(pPlayer,pPlayer.getOnPos(),);

        if (!pLevel.isClientSide){
//            EntityGrenadeBouncyBase grenadeEntity = null;
//            switch (this.name) {
//                case "generic" -> grenadeEntity = GrenadeGeneticEntity.create(pLevel,pPlayer);
//                default -> HBMxx.LOGGER.info("Grenade type undefined !!!");
//            }
//            if (grenadeEntity != null){
//                grenadeEntity.shootFromRotation(pPlayer,pPlayer.getXRot(),pPlayer.getYRot(),0.0F,1.5F,1.0F);
//                pLevel.addFreshEntity(grenadeEntity);
//            }
        }

        if (!pPlayer.isCreative()){
            itemStack.shrink(1);
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
