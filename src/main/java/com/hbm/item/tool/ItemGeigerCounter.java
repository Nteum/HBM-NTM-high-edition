package com.hbm.item.tool;

import com.hbm.addational_data.AdditionalDataManager;
import com.hbm.addational_data.DataEntry;
import com.hbm.blockentity.tools.TileEntityGeiger;
import com.hbm.registries.ModSounds;
import com.hbm.utils.ContaminationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemGeigerCounter extends Item {
    public ItemGeigerCounter(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pEntity instanceof Player && !pLevel.isClientSide()){
            Float rad = AdditionalDataManager.getEntityData(pEntity, DataEntry.RADIATION).map(o -> (float) o).orElse(0f);
            TileEntityGeiger.show(pLevel, (BlockPos) null, (Player) pEntity, pEntity.tickCount, rad);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide()){
            pPlayer.playSound(ModSounds.ITEM_TECH_BOOP.get(), 1.0f, 1.0f);
            ContaminationUtil.printGeigerData(pPlayer);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
