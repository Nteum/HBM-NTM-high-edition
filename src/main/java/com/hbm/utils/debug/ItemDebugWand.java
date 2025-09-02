package com.hbm.utils.debug;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.entity.weapon.missile.EntityMissileTier0.*;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.world.ForgeChunkManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

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
            ChunkPos chunkPos = new ChunkPos(clickedPos);
            ((ServerLevel) pContext.getLevel()).getChunkSource().addRegionTicket(TicketType.FORCED, chunkPos, 0, chunkPos, true);
//            ForgeChunkManager.forceChunk((ServerLevel) pContext.getLevel(), HBM.MODID, clickedPos, SectionPos.blockToSectionCoord(clickedPos.getX()), SectionPos.blockToSectionCoord(clickedPos.getY()),true,true);
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
                }else {
                    createMissle(pLevel, pPlayer, pUsedHand, storedPos);
                }
                itemInHand.removeTagKey(HBMKey.POSITION);
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

    /**
     * 这些函数均在use函数里调用，用于调试各种不同的效果
     * */
    public static void forceChunk(Level pLevel, Player pPlayer, BlockPos storedPos, BlockState markedBlock){
        ForgeChunkManager.forceChunk((ServerLevel) pLevel, HBM.MODID, storedPos, SectionPos.blockToSectionCoord(storedPos.getX()), SectionPos.blockToSectionCoord(storedPos.getY()),false,false);
        pPlayer.sendSystemMessage(Component.translatable(HBMLang.BLOCK_STATE_INFO.key(), storedPos.toShortString(), markedBlock.getBlock().getDescriptionId()));
    }
    public static void createMissle(Level pLevel, Player pPlayer, InteractionHand pUsedHand, BlockPos storedPos){
        EntityMissileTest missileTest = new EntityMissileTest(pLevel, (float) pPlayer.getX(), (float) (pPlayer.getY()+2), (float) pPlayer.getZ(), storedPos);
        pLevel.addFreshEntity(missileTest);
        pPlayer.sendSystemMessage(Component.literal("New missile create, aim at: " + storedPos.toShortString()));

    }
}
