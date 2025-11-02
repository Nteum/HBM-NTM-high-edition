package com.hbm.debug;

import com.hbm.registries.HBMDamage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

//自杀手枪，测试伤害类型
public class GunSuicide extends Item {
    public GunSuicide(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide()){
            pPlayer.hurt(HBMDamage.get(HBMDamage.DIGAMMA, pLevel.registryAccess(), null, pPlayer), 100);
            pPlayer.sendSystemMessage(Component.literal("pong!"));
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
