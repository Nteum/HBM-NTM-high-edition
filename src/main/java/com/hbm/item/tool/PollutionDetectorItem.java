package com.hbm.item.tool;

import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionType;
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
        float soot = PollutionHandler.getPollution(level, pos, PollutionType.SOOT);
        float poison = PollutionHandler.getPollution(level, pos, PollutionType.POISON);
        float heavy = PollutionHandler.getPollution(level, pos, PollutionType.HEAVYMETAL);
        player.displayClientMessage(
                Component.translatable("message.hbm.pollution_detector",
                        format(soot), format(poison), format(heavy)),
                true);
    }

    private static String format(float value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }
}
