package com.hbm.item.weapon;

import com.hbm.block.weapon.IBomb;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ScreenEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class ItemDetonator extends Item {

    public ItemDetonator(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("item.hbmxx.detonator.tooltip1"));
        pTooltipComponents.add(Component.translatable("item.hbmxx.detonator.tooltip2"));
        if (!pStack.hasTag() || pStack.getTag().getIntArray("pos") == null){
            pTooltipComponents.add(Component.translatable("item.hbmxx.detonator.tooltip3").withStyle(ChatFormatting.RED));
        }else {
            int[] pos = pStack.getTag().getIntArray("pos");
            pTooltipComponents.add(Component.translatable("item.hbmxx.detonator.tooltip4",pos[0],pos[1],pos[2]).withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getPlayer().hasPose(Pose.CROUCHING)){
            Player player = pContext.getPlayer();
            BlockPos clickedPos = pContext.getClickedPos();
            ItemStack itemInHand = pContext.getItemInHand();
            itemInHand.addTagElement("pos", new IntArrayTag(new int[]{clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()}));
            if(!pContext.getLevel().isClientSide) {
                player.sendSystemMessage(Component.translatable("msg.hbm.nuclear_in", clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()));
            }
        }
        return super.useOn(pContext);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pPlayer.hasPose(Pose.CROUCHING)){
            ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);
            if (!itemInHand.hasTag() || itemInHand.getTag().getIntArray("pos") == null){
                if (!pLevel.isClientSide){
                    pPlayer.sendSystemMessage(Component.translatable("msg.hbm.no_pos"));
                }
            }else {
                int[] pos = itemInHand.getTag().getIntArray("pos");
                Block block = pLevel.getBlockState(BlockPos.containing(pos[0], pos[1], pos[2])).getBlock();
                if (block instanceof IBomb){
                    if (!pLevel.isClientSide){
                        IBomb.BombReturnCode bombReturnCode = ((IBomb) block).explode(pLevel, BlockPos.containing(pos[0], pos[1], pos[2]));
                        itemInHand.removeTagKey("pos");
                        if (bombReturnCode.wasSuccessful())
                            pPlayer.sendSystemMessage(Component.translatable("msg.hbm.nuclear_exploded", pos[0], pos[1], pos[2]));
                    }
                }else {
                    if (!pLevel.isClientSide && itemInHand.hasTag()){
                        pPlayer.displayClientMessage(Component.translatable("msg.hbm.bomb_disappeared"),true);
                    }
                }
                itemInHand.removeTagKey("pos");
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

}
