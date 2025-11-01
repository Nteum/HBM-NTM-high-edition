package com.hbm.registries;

import com.hbm.HBM;
import com.hbm.HBMLang;
import com.hbm.addational_data.Pollution;
import com.hbm.commands.RadiationCommand;
import com.hbm.commands.TestCommand;
import com.hbm.commands.WandCommand;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.server.command.EnumArgument;

/** 指令不需要注册表，注册指令实际上是一个事件，但为了维持格式的统一，还是视为注册类 */
public class ModCommands {
    public static void registerServerCommands(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext buildContext = event.getBuildContext();

//        TestCommand.register(dispatcher);
        dispatcher.register(TestCommand.register());
        RadiationCommand.register(dispatcher);
        WandCommand.register(dispatcher);

        dispatcher.register(Commands.literal("hbm").executes(context -> {
            context.getSource().sendSystemMessage(Component.literal("Welcome to HBM"));
            return 1;
        }).then(Commands.literal("pollution")
                .then(Commands.literal("check").executes(context -> {
                    Pollution.showPollution(context.getSource().getLevel(), context.getSource().getPlayer());
                    return 1;
                }))
                .then(Commands.literal("clear").executes(context -> {
                    Pollution.clearPollution(context.getSource().getLevel(), context.getSource().getPlayer());
                    return 1;
                }))
                .then(Commands.literal("set").then(Commands.argument("type", EnumArgument.enumArgument(Pollution.Type.class)).then(Commands.argument("value", FloatArgumentType.floatArg(0)).executes(context -> {
                    Pollution.Type type = context.getArgument("type", Pollution.Type.class);
                    float value = FloatArgumentType.getFloat(context, "value");
                    if (context.getSource().getPlayer() != null)
                        Pollution.setPollution(context.getSource().getLevel(), context.getSource().getPlayer().getOnPos(), type, value);
                    return 1;
                })))))
        .then(Commands.literal("radiation")
                .then(Commands.literal("check").executes(context -> {
                    var player = context.getSource().getPlayer();
                    float radiation = ChunkRadiationManager.proxy.getRadiation(context.getSource().getLevel(), player.getOnPos());
                    context.getSource().sendSystemMessage(Component.literal("Radiation: " + radiation));
                    return 1;
                }))
                .then(Commands.literal("clear").executes(context -> {
                    ChunkRadiationManager.proxy.clearSystem(context.getSource().getLevel());
                    return 1;
                }))
                .then(Commands.literal("set").then(Commands.argument("value", FloatArgumentType.floatArg(0)).executes(context -> {
                    var player = context.getSource().getPlayer();
                    ChunkRadiationManager.proxy.setRadiation(context.getSource().getLevel(), player.getOnPos(), FloatArgumentType.getFloat(context, "value"));
                    return 1;
                }))))
        .then(Commands.literal("debug").executes(context -> {
                HBM.debug = !HBM.debug;
                context.getSource().sendSuccess(() -> Component.translatable(HBMLang.COMMAND_DEBUG.key(),HBM.debug), true);
                return 0;
            }))
        );
    }
}
