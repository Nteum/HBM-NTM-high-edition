package com.hbm.utils.Debug;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.block.weapon.IBomb;
import com.hbm.item.HBMtools;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDebugWand extends Item {

    public ItemDebugWand(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable(HBMLang.CACHED_DATA.key()));
        if (pStack.hasTag() && pStack.getTagElement(HBMKey.POSITION) != null){
            pTooltipComponents.add(Component.translatable(HBMLang.POS_DATA.key(), NbtUtils.readBlockPos(pStack.getTagElement(HBMKey.POSITION)).toShortString()));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (!pContext.getLevel().isClientSide && pContext.getPlayer().hasPose(Pose.CROUCHING)){
            Player player = pContext.getPlayer();
            BlockPos clickedPos = pContext.getClickedPos();
            ItemStack itemInHand = pContext.getItemInHand();
            itemInHand.addTagElement(HBMKey.POSITION, NbtUtils.writeBlockPos(clickedPos));
            player.sendSystemMessage(Component.literal("Mark pos on [" + clickedPos.getX() + "," + clickedPos.getY() + "," + clickedPos.getZ() + "]"));
        }
        return super.useOn(pContext);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide && !pPlayer.hasPose(Pose.CROUCHING)){
            ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);
            CompoundTag posElement = itemInHand.getTagElement(HBMKey.POSITION);
            if (!itemInHand.hasTag() || posElement == null){
                pPlayer.sendSystemMessage(Component.literal("No pos has been set."));
            }else {
                BlockPos storedPos = NbtUtils.readBlockPos(posElement);
                // 注意，getBlockState是会加载区块的，因此这里用了一个安全加载的函数
                BlockState markedBlock = WorldUtils.getBlockState(pLevel, storedPos).orElse(Blocks.AIR.defaultBlockState());
                if (markedBlock.is(Blocks.AIR) || markedBlock.is(Blocks.VOID_AIR)){
                    pPlayer.sendSystemMessage(Component.translatable(HBMLang.BLOCK_STATE_LOSE.key(), storedPos.toShortString()));
                    itemInHand.removeTagKey(HBMKey.POSITION);
                }else {
                    pPlayer.sendSystemMessage(Component.translatable(HBMLang.BLOCK_STATE_INFO.key(), storedPos.toShortString(), markedBlock.getBlock().getDescriptionId()));
                }
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
//        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
//        if (!pLevel.isClientSide && pLevel.getGameTime()%5==0 && pStack.is(HBMtools.DEBUG_WAND.get()) && pStack.hasTag()){
//            CompoundTag posTag = pStack.getTagElement(HBMKey.POSITION);
//            if (posTag != null){
//                BlockPos blockPos = NbtUtils.readBlockPos(posTag);
//                boolean doChunkLoad = pLevel.hasChunk(SectionPos.blockToSectionCoord(blockPos.getX()), SectionPos.blockToSectionCoord(blockPos.getY()));
//                pEntity.sendSystemMessage(Component.translatable(HBMLang.CHUNK_DATA.key(), new ChunkPos(blockPos).toString()).append(" is " + (doChunkLoad ? "load" : "unload")));
//            }
//        }
    }
}
