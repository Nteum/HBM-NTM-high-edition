package com.hbm.commands;

import com.hbm.item.tool.ItemBuildWand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class WandCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hbmwand")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("undo").executes(context -> undo(context.getSource()))));
    }

    private static int undo(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.translatable("msg.hbm.wand.undo.empty"));
            return 0;
        }
        ItemBuildWand.UndoResult result = ItemBuildWand.undoLast(player);
        return switch (result.status()) {
            case OK -> {
                source.sendSuccess(() -> Component.translatable("msg.hbm.wand.undo.success", result.count()), true);
                yield result.count();
            }
            case MISSING_LEVEL -> {
                source.sendFailure(Component.translatable("msg.hbm.wand.undo.dimension_missing", result.dimension()));
                yield 0;
            }
            case EMPTY -> {
                source.sendFailure(Component.translatable("msg.hbm.wand.undo.empty"));
                yield 0;
            }
        };
    }
}
