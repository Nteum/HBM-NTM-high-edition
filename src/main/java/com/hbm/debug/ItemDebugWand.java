package com.hbm.debug;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.addational_data.AdditionalDataManager;
import com.hbm.addational_data.DataEntry;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.world.ForgeChunkManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
//            Player player = pContext.getPlayer();
//            BlockPos clickedPos = pContext.getClickedPos();
//            ItemStack itemInHand = pContext.getItemInHand();
//            itemInHand.addTagElement(HBMKey.POSITION, NbtUtils.writeBlockPos(clickedPos));
//            ChunkPos chunkPos = new ChunkPos(clickedPos);
//            ((ServerLevel) pContext.getLevel()).getChunkSource().addRegionTicket(TicketType.FORCED, chunkPos, 0, chunkPos, true);
////            ForgeChunkManager.forceChunk((ServerLevel) pContext.getLevel(), HBM.MODID, clickedPos, SectionPos.blockToSectionCoord(clickedPos.getX()), SectionPos.blockToSectionCoord(clickedPos.getY()),true,true);
//            player.sendSystemMessage(Component.translatable("msg.hbm.mark_pos", clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()));
        }
        return super.useOn(pContext);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide && !pPlayer.hasPose(Pose.CROUCHING)){
//            ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);
//            CompoundTag posElement = itemInHand.getTagElement(HBMKey.POSITION);
//            if (!itemInHand.hasTag() || posElement == null){
//                pPlayer.sendSystemMessage(Component.translatable("msg.hbm.no_pos"));
//            }else {
//                BlockPos storedPos = NbtUtils.readBlockPos(posElement);
//                // 注意，getBlockState是会加载区块的，因此这里用了一个安全加载的函数
//                BlockState markedBlock = WorldUtils.getBlockState(pLevel, storedPos).orElse(Blocks.AIR.defaultBlockState());
//                if (markedBlock.is(Blocks.AIR) || markedBlock.is(Blocks.VOID_AIR)){
//                    pPlayer.sendSystemMessage(Component.translatable(HBMLang.BLOCK_STATE_LOSE.key(), storedPos.toShortString()));
//                }else {
//                    createMissle(pLevel, pPlayer, pUsedHand, storedPos);
////                    addEffects(pLevel, pPlayer, storedPos);
//                }
//                itemInHand.removeTagKey(HBMKey.POSITION);
//            }
//            showRadData(pLevel, pPlayer);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pLevel.isClientSide()) return;
//        showRadData(pStack, pLevel, pEntity);
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
        missileTest.setOwner(pPlayer);
        pLevel.addFreshEntity(missileTest);
        pPlayer.sendSystemMessage(Component.translatable("msg.hbm.new_missile_create", storedPos.toShortString()));
    }
    public static void addEffects(Level pLevel, Player pPlayer, BlockPos storedPos){
//        AdditionalDataManager.setEntityData(pPlayer, DataEntry.RADIATION, 900F);

        if (!pLevel.hasChunk(storedPos.getX() >> 4, storedPos.getZ() >> 4)) return;
        ChunkAccess chunk = pLevel.getChunk(storedPos);
        if (chunk instanceof LevelChunk levelChunk){
            AdditionalDataManager.setChunkData(levelChunk, DataEntry.RADIATION, 1000);
            pPlayer.sendSystemMessage(Component.translatable("msg.hbm.radiation_set_at", storedPos.toShortString()));
        }
    }
    public static void showRadData(Level pLevel, Entity pEntity){
        Float entityRad = AdditionalDataManager.getEntityData(pEntity, DataEntry.RADIATION).map(o -> (float) o).orElse(0f);
        Float chunkRad = AdditionalDataManager.getChunkData(pLevel.getChunkAt(pEntity.getOnPos()), DataEntry.RADIATION).map(o -> (float) o).orElse(0f);
        pEntity.sendSystemMessage(Component.translatable("msg.hbm.entity_chunk_rad", entityRad, chunkRad));
    }
    // 打印区块相关的辐射
    public static void showChunkRadData(ItemStack pStack, Level pLevel, Entity pEntity){
        if (pStack.getTagElement(HBMKey.POSITION) != null || pLevel.getGameTime() % 40 != 0) return;
        BlockPos blockPos;
        List<Float> radList = new ArrayList<>();
        blockPos = pEntity.getOnPos();
        ChunkPos chunkPos = new ChunkPos(blockPos);
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                LevelChunk chunk = pLevel.getChunk(chunkPos.x + i, chunkPos.z + j);
                radList.add(AdditionalDataManager.getChunkData(chunk, DataEntry.RADIATION).map(o -> (float) o).orElse(0f));
            }
        }
        pEntity.sendSystemMessage(Component.translatable("msg.hbm.radiation_now", radList.toString()));
    }
}
