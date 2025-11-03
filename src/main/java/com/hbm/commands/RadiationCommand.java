package com.hbm.commands;

import com.hbm.handler.radiation.ChunkRadiationManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class RadiationCommand {
    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher){
        pDispatcher.register(((Commands.literal("ntmrad").requires(commandSourceStack -> commandSourceStack.hasPermission(2)
                )).then(Commands.literal("clear")).executes(context -> {return setRadiate(context.getSource().getLevel(),context.getSource().getPlayer(),0);
                })).then(Commands.literal("set").then(Commands.argument("rads", IntegerArgumentType.integer(0)).executes(context -> {
                    return setRadiate(context.getSource().getLevel(),context.getSource().getPlayer(),IntegerArgumentType.getInteger(context,"rads"));
                }))));
    }

    private static int setRadiate(Level level, Player player, int eRads){
        if (eRads == 0){
            ChunkRadiationManager.proxy.clearSystem(level);
        player.displayClientMessage(Component.translatable("msg.hbm.radiation_cleared").withStyle(ChatFormatting.GREEN),true);
        }else {
            ChunkRadiationManager.proxy.setRadiation(level,player.getOnPos(),eRads);
        player.displayClientMessage(Component.translatable("msg.hbm.radiation_set").withStyle(ChatFormatting.RED),true);
        }
        return 0;
    }
}
