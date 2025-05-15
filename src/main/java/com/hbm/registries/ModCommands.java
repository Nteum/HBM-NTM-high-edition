package com.hbm.registries;

import com.hbm.commands.RadiationCommand;
import com.hbm.commands.TestCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
/** 指令不需要注册表，注册指令实际上是一个事件，但为了维持格式的统一，还是视为注册类 */
@Mod.EventBusSubscriber()
public class ModCommands {
    @SubscribeEvent
    public static void registerServerCommands(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext buildContext = event.getBuildContext();

//        TestCommand.register(dispatcher);
        dispatcher.register(TestCommand.register());
        RadiationCommand.register(dispatcher);
    }
}
