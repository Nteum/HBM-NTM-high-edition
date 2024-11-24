package com.hbm.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class TestCommand {
    public TestCommand(){}

    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher){
        pDispatcher.register(Commands.literal("hbmtest").executes(context -> {
            ServerPlayer player = context.getSource().getPlayer();
            assert player != null;
            player.displayClientMessage(Component.literal("HBM test command show").withStyle(ChatFormatting.RED),true);
            return 0;
        }));
    }
}