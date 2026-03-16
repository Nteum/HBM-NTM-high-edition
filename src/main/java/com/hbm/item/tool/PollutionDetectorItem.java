package com.hbm.item.tool;

import com.hbm.addational_data.Pollution;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Locale;

/**
 * Simple HUD-style pollution meter. When held in hand it periodically samples
 * pollution types around the user and prints the values to the action bar.
 */
public class PollutionDetectorItem extends Item {

    private static final int UPDATE_INTERVAL_TICKS = 40;

    public PollutionDetectorItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player)) {
            return;
        }
        boolean active = selected || player.getOffhandItem() == stack;
        if (!active) {
            return;
        }
        if (level.getGameTime() % UPDATE_INTERVAL_TICKS != 0) {
            return;
        }
        BlockPos pos = player.blockPosition();
        float soot = Pollution.getPollution(level, pos, Pollution.Type.SOOT);
        float poison = Pollution.getPollution(level, pos, Pollution.Type.POISON);
        float heavy = Pollution.getPollution(level, pos, Pollution.Type.HEAVYMETAL);
        player.displayClientMessage(
                Component.translatable("message.hbm.pollution_detector",
                        format(soot), format(poison), format(heavy)),
                true);
    }

    private static String format(float value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }
}
