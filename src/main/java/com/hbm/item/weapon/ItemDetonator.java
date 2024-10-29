package com.hbm.item.weapon;

import java.util.List;

import com.hbm.block.weapon.IBomb;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.Level;


public class ItemDetonator extends Item {

    public ItemDetonator(Properties pProperties) {
        super(pProperties);
    }

//    @Override
//    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean bool) {
//        list.add("Shift right-click to set position,");
//        list.add("right-click to detonate!");
//        if(itemstack.getTagCompound() == null) {
//            list.add(EnumChatFormatting.RED + "No position set!");
//        } else {
//            list.add(EnumChatFormatting.YELLOW + "Linked to " + itemstack.stackTagCompound.getInteger("x") + ", " + itemstack.stackTagCompound.getInteger("y") + ", " + itemstack.stackTagCompound.getInteger("z"));
//        }
//    }


    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getPlayer().getPose().compareTo(Pose.CROUCHING)==0){
            Player player = pContext.getPlayer();
            player.sendSystemMessage(Component.literal("player sneaking !!!"));
            BlockPos clickedPos = pContext.getClickedPos();
            ItemStack itemInHand = pContext.getItemInHand();
            itemInHand.addTagElement("pos", new IntArrayTag(new int[]{clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()}));
            if(!pContext.getLevel().isClientSide) {
                player.sendSystemMessage(Component.literal("set pos!"));
            }
        }
        return super.useOn(pContext);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);
        if (itemInHand.getTag() == null){
            pPlayer.sendSystemMessage(Component.literal("please set pos"));
        }else {
            if (itemInHand.getTag().getIntArray("pos") == null){
                return InteractionResultHolder.fail(itemInHand);
            }
            int[] pos = itemInHand.getTag().getIntArray("pos");
            if (!pLevel.isClientSide){
                Block block = pLevel.getBlockState(BlockPos.containing(pos[0],pos[1],pos[2])).getBlock();
                if (block instanceof IBomb){
                    IBomb.BombReturnCode bombReturnCode = ((IBomb) block).explode(pLevel, BlockPos.containing(pos[0], pos[1], pos[2]));
                    itemInHand.removeTagKey("pos");
                }else {
                    pPlayer.displayClientMessage(Component.literal("bomb disappeared why?"),true);
                }
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

}

