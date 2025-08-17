package com.hbm.commands;

import com.hbm.HBM;
import com.hbm.HBMLang;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class TestCommand {
    public TestCommand(){}

//    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher){
//        pDispatcher.register(Commands.literal("hbmtest").executes(context -> {
//            ServerPlayer player = context.getSource().getPlayer();
//            assert player != null;
//            player.displayClientMessage(Component.literal("HBM test command show").withStyle(ChatFormatting.RED),true);
//            return 0;
//        }));
//    }
    public static LiteralArgumentBuilder<CommandSourceStack> register(){
        return Commands.literal("hbmtest").executes(ctx -> {
            HBM.debug = !HBM.debug;
            ctx.getSource().sendSuccess(() -> Component.translatable(HBMLang.COMMAND_DEBUG.key(),HBM.debug), true);
            return 0;
        });
    }
}