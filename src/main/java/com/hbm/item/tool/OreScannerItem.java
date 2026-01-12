package com.hbm.item.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.Tags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Lightweight ore density scanner. Counts ore-tagged blocks in a small cube
 * around the clicked block and reports the total.
 */
public class OreScannerItem extends Item {

    private static final int HORIZONTAL_RADIUS = 3;
    private static final int VERTICAL_RADIUS = 4;

    public OreScannerItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockPos origin = context.getClickedPos();
        int oreCount = countOres(level, origin);
        if (context.getPlayer() instanceof ServerPlayer player) {
            player.displayClientMessage(
                    Component.translatable("message.hbm.ore_scanner",
                            oreCount,
                            HORIZONTAL_RADIUS * 2 + 1,
                            VERTICAL_RADIUS * 2 + 1),
                    true);
        }
        return InteractionResult.CONSUME;
    }

    private static int countOres(Level level, BlockPos origin) {
        int total = 0;
        MutableBlockPos cursor = new MutableBlockPos();
        for (int dx = -HORIZONTAL_RADIUS; dx <= HORIZONTAL_RADIUS; dx++) {
            for (int dz = -HORIZONTAL_RADIUS; dz <= HORIZONTAL_RADIUS; dz++) {
                for (int dy = -VERTICAL_RADIUS; dy <= VERTICAL_RADIUS; dy++) {
                    cursor.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    BlockState state = level.getBlockState(cursor);
                    if (state.is(Tags.Blocks.ORES)) {
                        total++;
                    }
                }
            }
        }
        return total;
    }
}
