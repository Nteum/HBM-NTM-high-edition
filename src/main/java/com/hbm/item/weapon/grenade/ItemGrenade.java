package com.hbm.item.weapon.grenade;

import com.hbm.HBMxx;
//import com.hbm.entity.logic.EntityGrenadeBouncyBase;
//import com.hbm.entity.logic.GrenadeGeneticEntity;
import com.hbm.entity.grenade.ThrownGrenade;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

    /** 右键手榴弹的效果
     * （参考的ItemSnowball）
     * */
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        pLevel.playSound((Player)null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!pLevel.isClientSide) {
            ThrownGrenade grenade = new ThrownGrenade(pLevel, pPlayer);
            grenade.setItem(itemStack);
            grenade.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 1.0F);
            pLevel.addFreshEntity(grenade);
        }

        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        if (!pPlayer.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
    }
}
