package com.hbm.item.env;

import com.hbm.HBMKey;
import com.hbm.registries.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class ItemEggGlyphid extends Item {
    public static int MAX_COUNTDOWN = 2000;
    public ItemEggGlyphid(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (!pLevel.isClientSide){
            CompoundTag tag = pStack.getOrCreateTag();
            if (!tag.contains(HBMKey.COUNTDOWN)) tag.putInt(HBMKey.COUNTDOWN, MAX_COUNTDOWN);
            else {
                int countdown = tag.getInt(HBMKey.COUNTDOWN);
                countdown --;
                if (countdown <= 0){
                    ServerPlayer player = (ServerPlayer) pEntity;
                    if (player.isOnFire()) player.getInventory().removeItem(pStack);
                    else {
                        if (!player.isCreative()) {
                            FoodData foodData = player.getFoodData();
                            foodData.setFoodLevel(Math.max(0, foodData.getFoodLevel() - 2));
                            foodData.setSaturation(Math.max(0, foodData.getSaturationLevel() - 2));
                        }
                        ItemStack newitem = pLevel.getRandom().nextDouble() < 0.7 ? ModItems.EGG_GLYPHID.get().getDefaultInstance() : ModItems.EGG_GLYPHID_TO_BIRTH.get().getDefaultInstance();
                        player.addItem(newitem);
                    }
                    countdown = MAX_COUNTDOWN;
                }
                tag.putInt(HBMKey.COUNTDOWN, countdown);
            }
        }
    }
}
