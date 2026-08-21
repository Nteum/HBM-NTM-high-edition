package com.hbm.item.special;

import com.hbm.core.capability.ItemStackCapabilityProvider;
import com.hbm.core.item.ItemBattery;
import com.hbm.registries.HBMCaps;
import com.hbm.registries.ModSounds;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemPotatos extends ItemBattery {
    public ItemPotatos(long capacity, long input, long output, Properties pProperties) {
        super(capacity, input, output, pProperties);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (pLevel.random.nextInt(200 + pLevel.random.nextInt(100)) > 0) return;
        if (pEntity instanceof Player && pSlotId == Player.HAND_SLOTS){
            pStack.getCapability(HBMCaps.LONG_ENERGY).ifPresent(ienergyHandler -> {
                float pitch = (float) ienergyHandler.getStored() / (float) ienergyHandler.getCapacity() * 0.5F + 0.5F;
                pLevel.playSound(null, pEntity, ModSounds.POTATOS_RANDOM.get(), SoundSource.RECORDS, 1, pitch);
            });
        }
    }
}
